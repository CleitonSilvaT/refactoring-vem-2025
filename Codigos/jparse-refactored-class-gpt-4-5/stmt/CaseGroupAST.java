package jparse.stmt;

import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaAST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class CaseGroupAST extends StatementAST implements JavaTokenTypes {

    private StatementAST[] stmtList;

    public CaseGroupAST() {
        super();
        setType(CASE_GROUP);
    }

    public void parseComplete() {
        super.parseComplete();
        AST a = getFirstChild();
        a = pularLabels(a); // TRECHO REFATORADO
        preencherStmtList(a); // TRECHO REFATORADO
        configurarProximosPonteiros(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO: Extrai lógica para pular labels iniciais ("case" e "default")
    private AST pularLabels(AST a) {
        while (a != null) {
            final int t = a.getType();
            if (t == LITERAL_case) {
                a = a.getNextSibling();
                ((JavaAST)a).parseComplete();
                a = a.getNextSibling().getNextSibling();
            } else if (t == LITERAL_default) {
                a = a.getNextSibling().getNextSibling();
            } else {
                break;
            }
        }
        return a;
    }

    // TRECHO REFATORADO: Extrai lógica de preenchimento da lista de statements
    private void preencherStmtList(AST a) {
        final ArrayList list = new ArrayList();
        while (a != null) {
            list.add(a);
            a = a.getNextSibling();
        }
        stmtList = new StatementAST[list.size()];
        list.toArray(stmtList);
    }

    // TRECHO REFATORADO: Extrai configuração dos ponteiros 'next' durante parse
    private void configurarProximosPonteiros() {
        if (stmtList.length > 0) {
            final StatementAST orig = context.nextStmt;
            for (int i = 0; i < stmtList.length - 1; i++) {
                context.nextStmt = stmtList[i + 1];
                stmtList[i].parseComplete();
            }
            context.nextStmt = orig;
            stmtList[stmtList.length - 1].parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        Type[] exceptions = noTypes;
        for (int i = 0; i < stmtList.length; i++)
            exceptions = Type.mergeTypeLists(exceptions, stmtList[i].getExceptionTypes());
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        if (stmtList.length == 0)
            return new StatementAST[] { next };

        HashSet goPoints = coletarPontosDeControle(); // TRECHO REFATORADO
        removerStatementsInternos(goPoints); // TRECHO REFATORADO

        StatementAST[] points = new StatementAST[goPoints.size()];
        return (StatementAST[])goPoints.toArray(points);
    }

    // TRECHO REFATORADO: Extrai coleta dos pontos iniciais de controle
    private HashSet coletarPontosDeControle() {
        final HashSet goPoints = new HashSet();
        goPoints.add(stmtList[0]);
        for (int i = 0; i < stmtList.length; i++) {
            if (goPoints.contains(stmtList[i])) {
                final StatementAST[] sNext = stmtList[i].nextControlPoints();
                for (int j = 0; j < sNext.length; j++) {
                    goPoints.add(sNext[j]);
                }
            }
        }
        return goPoints;
    }

    // TRECHO REFATORADO: Extrai lógica para remover statements internos dos pontos de controle coletados
    private void removerStatementsInternos(final HashSet goPoints) {
        for (int i = 0; i < stmtList.length; i++) {
            goPoints.remove(stmtList[i]);
        }
    }

    public VarList getVarList() {
        final VarList[] lists = new VarList[stmtList.length];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = stmtList[i].getVarList();
        }
        return new VarList(lists);
    }

    public StatementAST[] getList() {
        return stmtList;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * - **Extração de Método**: O método `parseComplete()` contém múltiplas responsabilidades claramente identificáveis, como navegar até as instruções depois de labels (`case` e `default`) e preencher a lista de statements, definindo em seguida os próximos ponteiros. Esses trechos podem ser extraídos em métodos privados separados para melhorar clareza e coesão.
 * - **Extração de Método**: A lógica de `computeControl()` para construção dos pontos de controle contém duas etapas claramente distintas (coletar pontos de acesso e depois remover statements internos). Cada etapa pode ser extraída separadamente.
 * - **Extração de Classe**: Não foram encontradas justificativas consistentes para extração de classes. Não há objetos ou estruturas distintas que justifiquem uma nova classe por si só.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * - **Método `pularLabels(AST a)`**: Removemos a complexidade condicional inicial do método principal. Conforme Fowler (2018) e Marco Tulio defendem, fragmentar métodos para isolar responsabilidades melhora a clareza e a facilidade de manutenção.
 * - **Método `preencherStmtList(AST a)`**: Claramente a tarefa de preencher uma lista de statements é logicamente separável. Segundo as mesmas referências, métodos devem ser pequenos, focados e claramente nomeados conforme sua responsabilidade específica.
 * - **Método `configurarProximosPonteiros()`**: Essa lógica específica sobre configuração de ponteiros quebra a sequência lógica do método principal ao misturar tarefas de inicialização e parsing propriamente dito. A separação melhora a clareza, o entendimento e produtividade, como indicado por Marco Túlio.
 * - **Métodos `coletarPontosDeControle()` e `removerStatementsInternos(HashSet goPoints)`**: Adoção da clara divisão das etapas para o cálculo dos statements de controle. Tal separação permite compreensão clara e futura expansão/manutenção dessa lógica, respeitando recomendações sobre coesão por Fowler.
 *
 * ## 4) Resumo das alterações:
 * - Total das refatorações realizadas: **5**
 *     - Extração de Método: **5**
 *         - `pularLabels(AST a)`
 *         - `preencherStmtList(AST a)`
 *         - `configurarProximosPonteiros()`
 *         - `coletarPontosDeControle()`
 *         - `removerStatementsInternos(HashSet goPoints)`
 *
 *     - Extração de Classe: **0** (não houve oportunidade clara para criação de nova classe isolada que possuísse uma responsabilidade coesa e desvinculada do contexto atual)
 */