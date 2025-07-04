package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class SwitchAST extends StatementAST implements JavaTokenTypes {

    private jparse.expr.ExpressionAST expr;
    private CaseGroupAST[] groupList;
    
    // TRECHO REFATORADO - Adicionada classe extraída para processamento de grupos de casos
    private CaseGroupProcessor caseGroupProcessor;

    public SwitchAST(final Token token) {
        super(token);
        // TRECHO REFATORADO - Inicialização do processador de grupos de casos
        this.caseGroupProcessor = new CaseGroupProcessor(this);
    }

    public void parseComplete() {
        super.parseComplete();
        expr = (jparse.expr.ExpressionAST)getFirstChild().getNextSibling();
        expr.parseComplete();

        // TRECHO REFATORADO - Extraído método para construir o array de grupos de casos
        construirArrayDeGruposDeCasos();

        // TRECHO REFATORADO - Extraído método para configurar os ponteiros "next"
        configurarPonteirosNext();
    }

    // TRECHO REFATORADO - Método extraído para construir o array de grupos de casos
    private void construirArrayDeGruposDeCasos() {
        final ArrayList list = new ArrayList();
        for (AST a = expr.getNextSibling().getNextSibling().getNextSibling();
             a.getType() == CASE_GROUP; a = a.getNextSibling()) {
            list.add(a);
        }
        groupList = new CaseGroupAST[list.size()];
        list.toArray(groupList);
    }

    // TRECHO REFATORADO - Método extraído para configurar os ponteiros "next"
    private void configurarPonteirosNext() {
        final StatementAST orig = context.nextStmt;
        context.pushBreak(this);
        
        // Configura os ponteiros "next" para todos os grupos exceto o último
        for (int i = 0; i < groupList.length - 1; i++) {
            context.nextStmt = groupList[i + 1];
            groupList[i].parseComplete();
        }
        
        // Configura o ponteiro "next" para o último grupo
        context.nextStmt = orig;
        if (groupList.length > 0) {
            groupList[groupList.length - 1].parseComplete();
        }
        context.popBreak();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO - Delegado para o processador de grupos de casos
        return caseGroupProcessor.computeExceptions(expr);
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO - Delegado para o processador de grupos de casos
        return caseGroupProcessor.computeControl();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO - Delegado para o processador de grupos de casos
        return caseGroupProcessor.getVarList(expr);
    }

    public jparse.expr.ExpressionAST getExpression() {
        return expr;
    }

    public CaseGroupAST[] getCaseGroups() {
        return groupList;
    }
    
    // TRECHO REFATORADO - Classe extraída para processamento de grupos de casos
    private class CaseGroupProcessor {
        private final SwitchAST switchAst;
        
        public CaseGroupProcessor(SwitchAST switchAst) {
            this.switchAst = switchAst;
        }
        
        public Type[] computeExceptions(jparse.expr.ExpressionAST expr) {
            Type[] retVal = expr.getExceptionTypes();
            for (int i = 0; i < groupList.length; i++) {
                Type.mergeTypeLists(retVal, groupList[i].getExceptionTypes());
            }
            return retVal;
        }
        
        public StatementAST[] computeControl() {
            final HashSet goPoints = coletarPontosDeControle();
            removerPontosInternosDeSwitchAst(goPoints);
            return converterParaArray(goPoints);
        }
        
        // TRECHO REFATORADO - Método extraído para coletar pontos de controle
        private HashSet coletarPontosDeControle() {
            final HashSet goPoints = new HashSet();
            for (int i = 0; i < groupList.length; i++) {
                final StatementAST[] next = groupList[i].nextControlPoints();
                for (int j = 0; j < next.length; j++) {
                    goPoints.add(next[j]);
                }
            }
            return goPoints;
        }
        
        // TRECHO REFATORADO - Método extraído para remover pontos internos
        private void removerPontosInternosDeSwitchAst(HashSet goPoints) {
            for (int i = 0; i < groupList.length; i++) {
                goPoints.remove(groupList[i]);
            }
        }
        
        // TRECHO REFATORADO - Método extraído para converter HashSet para array
        private StatementAST[] converterParaArray(HashSet goPoints) {
            final StatementAST[] points = new StatementAST[goPoints.size()];
            return (StatementAST[])goPoints.toArray(points);
        }
        
        public VarList getVarList(jparse.expr.ExpressionAST expr) {
            VarList list = expr.getVarList();
            for (int i = 0; i < groupList.length; i++) {
                list = new VarList(list, groupList[i].getVarList());
            }
            return list;
        }
    }
}

/**
 * # Análise e Refatoração da Classe SwitchAST
 * Vou analisar a classe SwitchAST e realizar a refatoração conforme solicitado, focando exclusivamente em Extração de Classe e Extração de Métodos.
 * ## 1) Oportunidades de refatoração encontradas
 * 1. O método `parseComplete()` é muito longo e faz múltiplas tarefas, incluindo:
 *     - Inicialização da expressão
 *     - Construção do array de grupos de casos
 *     - Configuração de ponteiros "next" para os grupos de casos
 *
 * 2. O método `computeControl()` contém várias operações distintas que podem ser extraídas em métodos separados:
 *     - Coleta dos pontos de controle dos grupos de casos
 *     - Filtragem dos pontos internos do switch
 *     - Conversão para array
 *
 * 3. A manipulação de `groupList` aparece em vários lugares do código com padrões similares (iterações sobre os grupos), o que sugere oportunidade para extração de métodos utilitários.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe - CaseGroupProcessor**:
 *     - Apliquei o padrão de refatoração "Extração de Classe" para isolar a responsabilidade de processar os grupos de casos.
 *     - Segundo Martin Fowler, devemos considerar extrair uma classe quando há uma parte do código que parece ter responsabilidades coesas diferentes da classe principal.
 *     - A manipulação dos grupos de casos constitui uma preocupação separada que pode ser encapsulada em uma classe própria, melhorando a coesão da classe SwitchAST.
 *
 * 2. **Extração de Métodos em parseComplete()**:
 *     - Os métodos `construirArrayDeGruposDeCasos()` e `configurarPonteirosNext()` foram extraídos para aumentar a legibilidade.
 *     - De acordo com Marco Tulio, métodos menores e bem nomeados aumentam a "mantenabilidade" do código.
 *     - A extração de métodos segue o princípio de responsabilidade única, onde cada método faz apenas uma coisa.
 *
 * 3. **Extração de Métodos em computeControl()**:
 *     - Extraí os métodos `coletarPontosDeControle()`, `removerPontosInternosDeSwitchAst()` e `converterParaArray()` para melhorar a legibilidade.
 *     - Martin Fowler sugere que a extração de métodos deve transformar um comentário em um nome de método, o que foi aplicado aqui.
 *
 * 4. **Delegação de funcionalidades para o processador**:
 *     - Os métodos `computeExceptions()`, `computeControl()` e `getVarList()` agora delegam para o processador de grupos de casos.
 *     - Essa abordagem segue o princípio "Tell, Don't Ask" mencionado por Martin Fowler, onde enviamos objetos para executar operações em vez de consultar seu estado.
 *
 * ## 4) Resumo das alterações
 * **Quantidade total de refatorações realizadas: 8**
 * **Divisão por tipo:**
 * - Extração de Método: 7
 *     1. construirArrayDeGruposDeCasos()
 *     2. configurarPonteirosNext()
 *     3. coletarPontosDeControle()
 *     4. removerPontosInternosDeSwitchAst()
 *     5. converterParaArray()
 *     6. computeExceptions() (na classe processadora)
 *     7. getVarList() (na classe processadora)
 *
 * - Extração de Classe: 1
 *     1. CaseGroupProcessor - Classe para processamento de grupos de casos
 *
 * As refatorações realizadas melhoram significativamente a organização do código, tornando-o mais legível e manutenível. O método `parseComplete()` original era muito longo e fazia várias tarefas. Agora, cada método tem uma responsabilidade clara e bem definida. A extração da classe `CaseGroupProcessor` ajuda a organizar melhor as responsabilidades e reduz o acoplamento entre as diferentes partes do código.
 */