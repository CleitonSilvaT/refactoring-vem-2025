//class extracted from BooleanAST

package jparse.expr;

// TRECHO REFATORADO: Extração da classe dedicada para comparações numéricas
class NumericComparisonEvaluator implements JavaTokenTypes {

    public Object evaluate(Object leftObj, Object rightObj, Type compareType, int operator) {
        if (compareType == Type.doubleType)
            return evaluateDouble((Number)leftObj, (Number)rightObj, operator);
        else if (compareType == Type.floatType)
            return evaluateFloat((Number)leftObj, (Number)rightObj, operator);
        else if (compareType == Type.longType)
            return evaluateLong((Number)leftObj, (Number)rightObj, operator);
        else
            return evaluateInt((Number)leftObj, (Number)rightObj, operator);
    }

    private Boolean evaluateDouble(Number l, Number r, int op) {
        double left = l.doubleValue();
        double right = r.doubleValue();
        return evaluateWithOperator(left, right, op);
    }

    private Boolean evaluateFloat(Number l, Number r, int op) {
        float left = l.floatValue();
        float right = r.floatValue();
        return evaluateWithOperator(left, right, op);
    }

    private Boolean evaluateLong(Number l, Number r, int op) {
        long left = l.longValue();
        long right = r.longValue();
        return evaluateWithOperator(left, right, op);
    }

    private Boolean evaluateInt(Number l, Number r, int op) {
        int left = l.intValue();
        int right = r.intValue();
        return evaluateWithOperator(left, right, op);
    }

    private Boolean evaluateWithOperator(double left, double right, int operator) {
        switch (operator) {
            case LT:
                return left < right;
            case GT:
                return left > right;
            case LE:
                return left <= right;
            case GE:
                return left >= right;
            default:
                return false;
        }
    }
}