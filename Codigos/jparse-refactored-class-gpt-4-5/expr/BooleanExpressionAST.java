package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class BooleanAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST left;
    private ExpressionAST right;

    public BooleanAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        left = (ExpressionAST)getFirstChild();
        right = (ExpressionAST)left.getNextSibling();
        left.parseComplete();
        if (right != null)
            right.parseComplete();
    }

    protected Type computeType() {
        return Type.booleanType;
    }

    protected Type[] computeExceptions() {
        return (right == null)
            ? left.getExceptionTypes()
            : Type.mergeTypeLists(left.getExceptionTypes(), right.getExceptionTypes());
    }

    // TRECHO REFATORADO: Simplificação usando métodos extraídos
    protected Object computeValue() {
        final Object leftObj = left.getValue();
        if (leftObj == nonconstant)
            return nonconstant;

        final int operator = getType();

        if (isShortCircuitPossible(operator, leftObj))
            return leftObj;

        if (operator == LNOT)
            return computeLogicalNot(leftObj); // TRECHO REFATORADO: Extração de método lógico NOT

        if (right == null)
            return leftObj;

        final Object rightObj = right.getValue();
        if (rightObj == nonconstant)
            return nonconstant;

        return evaluateOperator(leftObj, rightObj, operator); // TRECHO REFATORADO: Extração avaliador de expressões
    }

    // TRECHO REFATORADO: Extração de método
    private boolean isShortCircuitPossible(int operator, Object leftObj) {
        return (operator == LOR && (Boolean)leftObj) ||
               (operator == LAND && !(Boolean)leftObj);
    }

    // TRECHO REFATORADO: Extração de método para operação lógica NOT
    private Object computeLogicalNot(Object obj) {
        return !(Boolean)obj;
    }

    // TRECHO REFATORADO: Extração de método para avaliar operadores
    private Object evaluateOperator(Object leftObj, Object rightObj, int operator) {
        switch (operator) {
            case LOR:
                return (Boolean)leftObj || (Boolean)rightObj;
            case LAND:
                return (Boolean)leftObj && (Boolean)rightObj;
            case NOT_EQUAL:
                return !leftObj.equals(rightObj);
            case EQUAL:
                return leftObj.equals(rightObj);
            case LITERAL_instanceof:
                return right.retrieveType() == Type.forClass(leftObj.getClass());
            default:
                break;
        }
        return evaluateComparison(leftObj, rightObj, operator);
    }

    // TRECHO REFATORADO: Extração para classe auxiliar dedicada às comparações numéricas
    private Object evaluateComparison(Object leftObj, Object rightObj, int operator) {
        Type compareType = Type.arithType(left.retrieveType(), right.retrieveType());
        return new NumericComparisonEvaluator().evaluate(leftObj, rightObj, compareType, operator);
    }

    public VarList getVarList() {
        return (right == null)
            ? left.getVarList()
            : new VarList(left.getVarList(), right.getVarList());
    }

    public ExpressionAST getLeft() {
        return left;
    }

    public ExpressionAST getRight() {
        return right;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * - O método `computeValue()` é extenso e lida com várias operações lógicas e comparações. Tal complexidade sugere que partes desse código podem ser extraídas para métodos distintos, isolando responsabilidades específicas.
 * - A avaliação das expressões booleanas e comparações numéricas (double, float, long, int) demonstram duplicidade estrutural. Estas partes são candidatas naturais à extração em métodos isolados ou classes, para garantir melhor manutenção e reutilização.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * - **Extração de Métodos:**
 * A extração de métodos ocorre conforme recomendado por Martin Fowler: métodos menores e autoexplicativos facilitam o entendimento do fluxo lógico e tornam o código mais intuitivo. Por exemplo, ações específicas como `isShortCircuitPossible`, `computeLogicalNot` e `evaluateOperator` isolam claramente responsabilidades definidas.
 * - **Extração de Classe:**
 * A classe auxiliar `NumericComparisonEvaluator` encapsula e organiza logically agrupadas operações numéricas, removendo duplicações e isolando completamente a lógica de comparações numéricas da classe original. Segundo Marco Tulio (Engenharia de Software Moderna), dividir responsabilidades claras e independentes traz benefícios na manutenção.
 *
 * ## 4) Resumo das alterações:
 * **Quantidade total de refatorações realizadas:** 5 alterações totais
 * - **Extração de Método:** 4 métodos extraídos:
 *     - `isShortCircuitPossible()`
 *     - `computeLogicalNot()`
 *     - `evaluateOperator()`
 *     - `evaluateComparison()` (indiretamente extraído para chamar a nova classe)
 *
 * - **Extração de Classe:** 1 classe extraída:
 *     - `NumericComparisonEvaluator`
 *
 * Essas refatorações melhoram substancialmente a legibilidade, organização e maintainabilidade dessa classe, alinhando-se perfeitamente com as recomendações dos livros citados.
 */