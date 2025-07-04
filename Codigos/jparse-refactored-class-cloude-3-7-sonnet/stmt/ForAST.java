package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.HasExceptions;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;
import jparse.expr.ListAST;
import jparse.stmt.DeclarationAST;

public final class ForAST extends StatementAST implements JavaTokenTypes {

    private HasExceptions init;
    private jparse.expr.ExpressionAST cond;
    private jparse.expr.ExpressionAST incr;
    private StatementAST stmt;
    
    // TRECHO REFATORADO: Classe extraída para gerenciar os componentes do loop for
    private class ForLoopComponents {
        private final HasExceptions initializer;
        private final jparse.expr.ExpressionAST condition;
        private final jparse.expr.ExpressionAST increment;
        private final StatementAST body;
        
        public ForLoopComponents(HasExceptions init, jparse.expr.ExpressionAST cond, 
                                jparse.expr.ExpressionAST incr, StatementAST stmt) {
            this.initializer = init;
            this.condition = cond;
            this.increment = incr;
            this.body = stmt;
        }
        
        public void completeAllParsing() {
            if (initializer != null) {
                if (initializer instanceof ListAST) {
                    ((ListAST)initializer).parseComplete();
                } else {
                    ((DeclarationAST)initializer).parseComplete();
                }
            }
            
            if (condition != null) {
                condition.parseComplete();
            }
            
            if (increment != null) {
                increment.parseComplete();
            }
            
            body.parseComplete();
        }
        
        public Type[] getAllExceptionTypes() {
            Type[] exceptionTypes = mergeInitAndCondExceptions();
            exceptionTypes = Type.mergeTypeLists(exceptionTypes, increment.getExceptionTypes());
            return Type.mergeTypeLists(exceptionTypes, body.getExceptionTypes());
        }
        
        private Type[] mergeInitAndCondExceptions() {
            if (condition == null) {
                return initializer.getExceptionTypes();
            } else {
                return Type.mergeTypeLists(initializer.getExceptionTypes(),
                                          condition.getExceptionTypes());
            }
        }
        
        public VarList buildCompleteVarList() {
            final VarList initList = getInitializerVarList();
            return combineVarLists(initList);
        }
        
        private VarList getInitializerVarList() {
            return (initializer instanceof ListAST)
                ? ((ListAST)initializer).getVarList()
                : ((DeclarationAST)initializer).getVarList();
        }
        
        private VarList combineVarLists(VarList initList) {
            return new VarList(new VarList(initList, condition.getVarList()),
                              new VarList(increment.getVarList(), body.getVarList()));
        }
    }

    public ForAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        
        // TRECHO REFATORADO: Método extraído para extrair os nós da árvore AST
        extractNodesFromAST();
        
        // TRECHO REFATORADO: Método extraído para processar o contexto e completar o parsing
        processContextAndCompleteChildParsing();
    }
    
    // TRECHO REFATORADO: Método extraído para extrair os nós da árvore AST
    private void extractNodesFromAST() {
        final AST initNode = getFirstChild().getNextSibling();
        init = (HasExceptions)initNode.getFirstChild();
        
        final AST condNode = initNode.getNextSibling();
        final AST theCond = condNode.getFirstChild();
        cond = (theCond.getType() == SEMI) ? null : (jparse.expr.ExpressionAST)theCond;
        
        final AST incrNode = condNode.getNextSibling();
        incr = (jparse.expr.ExpressionAST)incrNode.getFirstChild();
        
        stmt = (StatementAST)incrNode.getNextSibling().getNextSibling();
    }
    
    // TRECHO REFATORADO: Método extraído para processar o contexto e completar o parsing
    private void processContextAndCompleteChildParsing() {
        context.pushContinue(this);
        
        // TRECHO REFATORADO: Uso da classe extraída ForLoopComponents para organizar o processo de parsing
        ForLoopComponents components = new ForLoopComponents(init, cond, incr, stmt);
        components.completeAllParsing();
        
        context.popContinue();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Uso da classe extraída ForLoopComponents para calcular as exceções
        ForLoopComponents components = new ForLoopComponents(init, cond, incr, stmt);
        return components.getAllExceptionTypes();
    }

    protected StatementAST[] computeControl() {
        return stmt.nextControlPoints();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Uso da classe extraída ForLoopComponents para construir a lista de variáveis
        ForLoopComponents components = new ForLoopComponents(init, cond, incr, stmt);
        return components.buildCompleteVarList();
    }

    public HasExceptions getInit() {
        return init;
    }

    public jparse.expr.ExpressionAST getCondition() {
        return cond;
    }

    public jparse.expr.ExpressionAST getIncrement() {
        return incr;
    }

    public StatementAST getBody() {
        return stmt;
    }
}

/**
 * # Análise de Refatoração - Classe ForAST
 * Agora vou realizar a refatoração da classe ForAST conforme solicitado, focando em Extração de Classe e Extração de Métodos para melhorar a organização e manutenibilidade do código.
 * ## 1) Oportunidades de refatoração encontradas
 * 1. O método `parseComplete()` é muito longo e faz muitas coisas diferentes: extração de nós da árvore AST, configuração de contexto e chamadas para parseComplete em outros nós. Pode ser dividido em métodos menores e mais coesos.
 * 2. O método `computeExceptions()` possui várias operações de mesclagem de listas de tipos de exceções que poderiam ser extraídas para um método separado.
 * 3. O método `getVarList()` realiza várias operações para construir uma lista de variáveis, com lógica condicional embutida, que poderia ser melhorada com a extração de métodos.
 * 4. A lógica para inicializar e processar componentes do loop for (inicializador, condição, incremento) está espalhada, podendo ser extraída para uma classe auxiliar.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe - ForLoopComponents**:
 *     - Motivação: A classe ForAST estava gerenciando múltiplos componentes relacionados ao loop for (init, cond, incr, stmt) e suas operações associadas, tornando-a menos coesa.
 *     - Segundo Martin Fowler, quando uma classe tem dados e comportamentos que formam um subgrupo lógico, podemos aplicar a refatoração "Extract Class" para melhorar a coesão.
 *     - A nova classe ForLoopComponents encapsula todas as operações relacionadas aos quatro componentes de um loop for, tornando o código mais organizado e seguindo o princípio da responsabilidade única (SRP).
 *
 * 2. **Extração de Método - extractNodesFromAST()**:
 *     - Motivação: O método parseComplete() era longo e realizava múltiplas operações distintas. A extração desse método melhora a legibilidade, isolando a lógica de extração de nós da árvore AST.
 *     - Como sugere Marco Tulio em "Engenharia de Software Moderna", métodos devem ser pequenos e ter uma única responsabilidade, o que melhora a compreensão e manutenção do código.
 *
 * 3. **Extração de Método - processContextAndCompleteChildParsing()**:
 *     - Motivação: Isola a lógica de configuração do contexto e processamento do parsing dos componentes filhos.
 *     - Conforme Martin Fowler, dividir métodos longos em partes menores e bem nomeadas melhora a legibilidade e facilita a manutenção.
 *
 * 4. **Métodos Auxiliares em ForLoopComponents**:
 *     - `completeAllParsing()`: Centraliza a lógica de processamento dos elementos do loop
 *     - `getAllExceptionTypes()`, `mergeInitAndCondExceptions()`: Melhoram a organização do processo de obtenção e mesclagem de tipos de exceções
 *     - `buildCompleteVarList()`, `getInitializerVarList()`, `combineVarLists()`: Tornam mais clara a construção da lista de variáveis
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 9
 *     - **Extração de Classe**: 1 (ForLoopComponents)
 *     - **Extração de Método**: 8
 *         - extractNodesFromAST()
 *         - processContextAndCompleteChildParsing()
 *         - completeAllParsing()
 *         - getAllExceptionTypes()
 *         - mergeInitAndCondExceptions()
 *         - buildCompleteVarList()
 *         - getInitializerVarList()
 *         - combineVarLists()
 *
 * As refatorações realizadas aumentaram significativamente a coesão e a organização do código, tornando-o mais fácil de entender e manter. Cada método agora tem uma responsabilidade bem definida, e a nova classe ForLoopComponents encapsula adequadamente a lógica associada aos componentes do loop for, seguindo as boas práticas recomendadas por Martin Fowler e Marco Tulio.
 */