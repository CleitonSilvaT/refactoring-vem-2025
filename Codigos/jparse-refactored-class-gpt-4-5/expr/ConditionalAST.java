package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class ConditionalAST extends ExpressionAST {
    private ConditionalExpression expression; // TRECHO REFATORADO

    public ConditionalAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        expression = buildConditionalExpression(); // TRECHO REFATORADO
        expression.getCondition().parseComplete();
        expression.getThenExpr().parseComplete();
        expression.getElseExpr().parseComplete();
    }

    // TRECHO REFATORADO
    private ConditionalExpression buildConditionalExpression() {
        ExpressionAST condition = (ExpressionAST) getFirstChild();
        ExpressionAST thenExpr = (ExpressionAST) condition.getNextSibling();
        ExpressionAST elseExpr = (ExpressionAST) thenExpr.getNextSibling().getNextSibling();

        return new ConditionalExpression(condition, thenExpr, elseExpr);
    }

    protected Type computeType() {
        return expression.getCommonType(); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() {
        return expression.mergeExceptions(); // TRECHO REFATORADO
    }

    protected Object computeValue() {
        return expression.evaluateValue(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return expression.getMergedVarList(); // TRECHO REFATORADO
    }

    public ExpressionAST getCondition() {
        return expression.getCondition();
    }

    public ExpressionAST getThen() {
        return expression.getThenExpr();
    }

    public ExpressionAST getElse() {
        return expression.getElseExpr();
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - **Extração de Métodos**:
 *     - O método `computeType()` possui lógica para determinar a relação entre os tipos (`Type`), com múltiplas verificações. É mais claro extrair essa lógica para um método separado que trate exclusivamente a comparação entre os tipos.
 *     - O método `parseComplete()` está realizando múltiplas responsabilidades de configuração das partes da expressão (`condition`, `then`, `else`). A separação das responsabilidades ajuda a entender claramente o código.
 *     - O método `computeExceptions()` tem repetição na operação de merge das listas de exceções. Essa operação pode ser externalizada em um método específico.
 *
 * - **Extração de Classe**:
 *     - A classe possui claramente uma estrutura dividida nas partes (condition, then, else). Isso sugere a criação de uma classe que represente especificamente uma expressão condicional contendo esses elementos, para reduzir complexidade e aumentar coesão.
 *
 *     ### 3) Justificativa das refatorações:
 * Com base nos princípios de Martin Fowler (2018) e Marco Tulio (Engenharia de Software Moderna):
 * - **Extração de Classe (Extract Class)**: A classe estava sobrecarregada com diferentes responsabilidades (montagem da expressão condicional e sua avaliação). Essa refatoração aumenta a coesão do código e organiza melhor as responsabilidades, agrupando a lógica específica da expressão condicional numa classe dedicada.
 * - **Extração de Métodos (Extract Method)**: Diversas operações complexas (determinação do tipo comum, merges de exceções, e cálculo da expressão condicional) foram extraídas como métodos separados. Isso simplifica a leitura, reduz duplicação e melhora o entendimento.
 *
 * Essas extrações claramente deixam o código mais coeso, menos acoplado, mais legível e de fácil manutenção. Identificar cada responsabilidade do código fica mais simples, já que cada operação específica agora está bem destacada, seguindo uma única responsabilidade.
 * ### 4) Resumo das alterações:
 * - **Total**: 5 refatorações realizadas
 *     - Extração de Classe: 1 refatoração
 *     - Extração de Métodos: 4 refatorações (buildConditionalExpression, getCommonType, mergeExceptions, evaluateValue)
 */