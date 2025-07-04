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

        // TRECHO REFATORADO - Extraído método para melhorar legibilidade e manutenção
        AST nextNode = skipCaseDefaultLabels(getFirstChild());
        
        // TRECHO REFATORADO - Extraído método para construir a lista de statements
        buildStatementList(nextNode);
        
        // TRECHO REFATORADO - Extraído método para configurar os ponteiros "next"
        configureNextPointers();
    }
    
    // TRECHO REFATORADO - Novo método extraído para melhorar legibilidade
    private AST skipCaseDefaultLabels(AST node) {
        AST currentNode = node;
        while (currentNode != null) {
            final int nodeType = currentNode.getType();
            if (nodeType == LITERAL_case) {
                currentNode = currentNode.getNextSibling();
                ((JavaAST)currentNode).parseComplete();  // Parse the numbers
                currentNode = currentNode.getNextSibling().getNextSibling();
            } else if (nodeType == LITERAL_default) {
                currentNode = currentNode.getNextSibling().getNextSibling();
            } else {
                break;
            }
        }
        return currentNode;
    }
    
    // TRECHO REFATORADO - Novo método extraído para construir a lista de statements
    private void buildStatementList(AST startNode) {
        final ArrayList list = new ArrayList();
        AST currentNode = startNode;
        while (currentNode != null) {
            list.add(currentNode);
            currentNode = currentNode.getNextSibling();
        }
        stmtList = new StatementAST[list.size()];
        list.toArray(stmtList);
    }
    
    // TRECHO REFATORADO - Novo método extraído para configurar os ponteiros "next"
    private void configureNextPointers() {
        if (stmtList.length > 0) {
            final StatementAST originalNextStmt = context.nextStmt;
            
            for (int i = 0; i < stmtList.length - 1; i++) {
                context.nextStmt = stmtList[i + 1];
                stmtList[i].parseComplete();
            }
            
            context.nextStmt = originalNextStmt;
            stmtList[stmtList.length - 1].parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO - Código simplificado usando método extraído
        return mergeExceptions(stmtList);
    }
    
    // TRECHO REFATORADO - Extraído método para reutilização
    private Type[] mergeExceptions(StatementAST[] statements) {
        Type[] exceptions = noTypes;
        for (int i = 0; i < statements.length; i++) {
            exceptions = Type.mergeTypeLists(exceptions, statements[i].getExceptionTypes());
        }
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        if (stmtList.length == 0) {
            return new StatementAST[] { next };
        }

        // TRECHO REFATORADO - Extraído método para identificar pontos de controle
        HashSet goPoints = identifyControlPoints();
        
        // TRECHO REFATORADO - Extraído método para filtrar pontos internos
        removeInternalStatements(goPoints);

        final StatementAST[] points = new StatementAST[goPoints.size()];
        return (StatementAST[])goPoints.toArray(points);
    }
    
    // TRECHO REFATORADO - Novo método extraído para identificar pontos de controle
    private HashSet identifyControlPoints() {
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
    
    // TRECHO REFATORADO - Novo método extraído para remover declarações internas
    private void removeInternalStatements(HashSet goPoints) {
        for (int i = 0; i < stmtList.length; i++) {
            goPoints.remove(stmtList[i]);
        }
    }

    public VarList getVarList() {
        // TRECHO REFATORADO - Extraído método para criar VarList a partir de statements
        return createVarListFromStatements(stmtList);
    }
    
    // TRECHO REFATORADO - Novo método extraído para criar VarList
    private VarList createVarListFromStatements(StatementAST[] statements) {
        final VarList[] lists = new VarList[statements.length];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = statements[i].getVarList();
        }
        return new VarList(lists);
    }

    public StatementAST[] getList() {
        return stmtList;
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas
 * Na análise da classe `CaseGroupAST.java`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Método `parseComplete()`**: Este método é muito extenso e realiza múltiplas responsabilidades:
 *     - Percorre os nós AST para ignorar rótulos
 *     - Constrói uma lista de declarações
 *     - Configura os ponteiros "next" e completa as declarações
 *
 * 2. **Método `computeControl()`**: Este método tem comentários indicando que é similar a outro em `CompoundStatement`, sugerindo duplicação de código. Também tem alta complexidade com múltiplas responsabilidades.
 * 3. **Ausência de abstração para a manipulação dos nós AST**: Várias partes do código lidam diretamente com a navegação e transformação de nós AST sem abstrações adequadas.
 *
 * # 2) Classe refatorada com os devidos comentários
 *
 * # 3) Justificativa das refatorações
 * 1. **Extração do método `skipCaseDefaultLabels()`**:
 *     - Aplicando o princípio de Responsabilidade Única (SRP) de SOLID, separei a lógica de pular os rótulos case/default.
 *     - Conforme Fowler, essa extração melhora a legibilidade, isolando um comportamento específico com nome descritivo.
 *     - O nome do método agora comunica claramente sua intenção.
 *
 * 2. **Extração do método `buildStatementList()`**:
 *     - Isola a responsabilidade de construir a lista de declarações.
 *     - Seguindo o princípio de Fowler de "refatorar para entendimento", este código agora tem um propósito claro e bem definido.
 *
 * 3. **Extração do método `configureNextPointers()`**:
 *     - Separa a lógica de configuração dos ponteiros "next", melhorando a compreensão do fluxo de controle.
 *     - Facilita a manutenção futura ao agrupar comportamentos relacionados.
 *
 * 4. **Extração do método `mergeExceptions()`**:
 *     - Remove duplicação potencial com outras classes que possam precisar mesclar exceções.
 *     - Simplifica o método `computeExceptions()` tornando-o mais legível.
 *
 * 5. **Extração dos métodos `identifyControlPoints()` e `removeInternalStatements()`**:
 *     - Decompõe o método `computeControl()` em partes menores e mais compreensíveis.
 *     - Conforme Marco Tulio em "Engenharia de Software Moderna", essa decomposição facilita o entendimento e a manutenção.
 *     - Reduz a complexidade cognitiva necessária para entender o código.
 *
 * 6. **Extração do método `createVarListFromStatements()`**:
 *     - Isola a lógica de criação de VarLists a partir de statements.
 *     - Facilita a reutilização caso essa funcionalidade seja necessária em outros lugares.
 *
 * # 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 7
 * - **Divisão por tipo**:
 *     - Extração de Método: 7
 *     - Extração de Classe: 0
 *
 * As refatorações de extração de método aplicadas seguiram o princípio de Martin Fowler sobre decomposição de métodos longos e complexos em unidades menores com propósitos claros. Cada método extraído tem um nome descritivo que comunica sua função específica.
 * Não foi necessário aplicar Extração de Classe, pois a classe original já possuía uma responsabilidade bem definida como um nó AST para grupos de case. As refatorações de método foram suficientes para melhorar a clareza e manutenibilidade do código mantendo sua coesão.
 * A abordagem adotada preserva completamente o comportamento original do código enquanto melhora sua estrutura interna, conforme recomendado por Fowler e Marco Tulio.
 */