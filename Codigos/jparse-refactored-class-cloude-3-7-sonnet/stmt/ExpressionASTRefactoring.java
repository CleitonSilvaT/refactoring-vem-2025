package jparse.stmt;

import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ExpressionAST extends StatementAST implements JavaTokenTypes {

    // TRECHO REFATORADO: Encapsulamento do componente de expressão em classe auxiliar
    private final ExpressionHandler expressionHandler;

    public ExpressionAST(final jparse.expr.ExpressionAST expr) {
        super();
        initialize(EXPRESSION_STAT, "EXPRESSION_STAT");
        // TRECHO REFATORADO: Criação da classe auxiliar ExpressionHandler
        expressionHandler = new ExpressionHandler(expr);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Delegação para método especializado
        completeParsing();
    }

    // TRECHO REFATORADO: Extração de método para completar o parsing
    private void completeParsing() {
        expressionHandler.getExpression().parseComplete();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Delegação para classe auxiliar
        return expressionHandler.getExceptionTypes();
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Delegação para classe auxiliar de controle de fluxo
        return FlowControlHandler.createControlFlow(next);
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Delegação para classe auxiliar
        return expressionHandler.getVarList();
    }

    public jparse.expr.ExpressionAST getExpression() {
        // TRECHO REFATORADO: Delegação para classe auxiliar
        return expressionHandler.getExpression();
    }
    
    // TRECHO REFATORADO: Classe extraída para gerenciar a expressão
    private static class ExpressionHandler {
        private final jparse.expr.ExpressionAST expression;
        
        public ExpressionHandler(jparse.expr.ExpressionAST expression) {
            this.expression = expression;
        }
        
        public jparse.expr.ExpressionAST getExpression() {
            return expression;
        }
        
        public Type[] getExceptionTypes() {
            return expression.getExceptionTypes();
        }
        
        public VarList getVarList() {
            return expression.getVarList();
        }
    }
    
    // TRECHO REFATORADO: Classe auxiliar para gerenciar o controle de fluxo
    private static class FlowControlHandler {
        public static StatementAST[] createControlFlow(StatementAST next) {
            return new StatementAST[] { next };
        }
    }
}

/**
 * # Refatoração da classe ExpressionAST
 * ## 1) Oportunidades de refatoração encontradas
 * Analisando a classe ExpressionAST, identifiquei as seguintes oportunidades de refatoração:
 * - O código tem poucos métodos, mas algumas responsabilidades podem ser melhor encapsuladas para melhorar a coesão.
 * - O método `parseComplete()` contém duas operações sequenciais que poderiam ser encapsuladas em um método específico.
 * - Os métodos `computeExceptions()` e `computeControl()` são fortemente relacionados ao controle de fluxo e exceções, o que poderia ser extraído para uma classe auxiliar.
 * - A classe gerencia elementos relacionados à expressão, o que pode ser isolado em uma classe específica para melhorar a coesão e a separação de responsabilidades.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da Classe ExpressionHandler**:
 *     - Esta refatoração segue o princípio de Responsabilidade Única (SRP) de Martin Fowler, concentrando todas as operações relacionadas à expressão em uma única classe.
 *     - A classe principal agora delega as responsabilidades específicas da expressão para esta classe auxiliar, tornando o código mais modular e coeso.
 *     - Isso facilita a manutenção, pois alterações relacionadas ao gerenciamento de expressões ficam encapsuladas.
 *
 * 2. **Extração da Classe FlowControlHandler**:
 *     - Foi extraída para encapsular a lógica de criação de fluxos de controle, isolando esta responsabilidade.
 *     - Conforme Fowler sugere, esta refatoração melhora a coesão ao separar responsabilidades distintas em classes diferentes.
 *
 * 3. **Extração do Método completeParsing()**:
 *     - Este método foi extraído para melhorar a legibilidade e encapsular a lógica de finalização do parsing.
 *     - De acordo com Marco Tulio, a Extração de Método é recomendada para dar nomes significativos a blocos de código, tornando o código mais autoexplicativo.
 *     - Facilita futuras manutenções, pois isola comportamentos específicos em métodos com nomes descritivos.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 1 (completeParsing)
 *     - **Extração de Classe**: 2 (ExpressionHandler e FlowControlHandler)
 *     - **Outras modificações relacionadas**: 2 (Delegações para as classes extraídas)
 *
 * Estas refatorações aumentam significativamente a coesão e reduzem o acoplamento da classe ExpressionAST, seguindo os princípios de design de software recomendados por Martin Fowler e Marco Tulio.
 */