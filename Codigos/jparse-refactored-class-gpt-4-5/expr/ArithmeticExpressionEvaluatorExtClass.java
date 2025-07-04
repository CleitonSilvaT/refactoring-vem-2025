package jparse.expr;

// TRECHO REFATORADO - nova classe extraída ArithmeticAST
public class ArithmeticEvaluator implements JavaTokenTypes {

    public static Object evaluate(final int operator, final Type type, final Object leftObj, final Object rightObj) {

        if (operator == CONCATENATION)
            return leftObj.toString() + rightObj.toString();

        Number leftNum = (Number) leftObj;
        Number rightNum = (Number) rightObj;

        switch (operator) {
            case PLUS:
                return adiciona(type, leftNum, rightNum);
            case MINUS:
                return subtrai(type, leftNum, rightNum);
            case STAR:
                return multiplica(type, leftNum, rightNum);
            case DIV:
                return divide(type, leftNum, rightNum);
            case MOD:
                return modulo(type, leftNum, rightNum);
            default:
                return ExpressionAST.nonconstant;
        }
    }

    private static Number adiciona(Type type, Number leftNum, Number rightNum) {
        if (type == Type.doubleType)
            return leftNum.doubleValue() + rightNum.doubleValue();
        if (type == Type.floatType)
            return leftNum.floatValue() + rightNum.floatValue();
        if (type == Type.longType)
            return leftNum.longValue() + rightNum.longValue();
        return leftNum.intValue() + rightNum.intValue();
    }

    private static Number subtrai(Type type, Number leftNum, Number rightNum) {
        if (type == Type.doubleType)
            return leftNum.doubleValue() - rightNum.doubleValue();
        if (type == Type.floatType)
            return leftNum.floatValue() - rightNum.floatValue();
        if (type == Type.longType)
            return leftNum.longValue() - rightNum.longValue();
        return leftNum.intValue() - rightNum.intValue();
    }

    private static Number multiplica(Type type, Number leftNum, Number rightNum) {
        if (type == Type.doubleType)
            return leftNum.doubleValue() * rightNum.doubleValue();
        if (type == Type.floatType)
            return leftNum.floatValue() * rightNum.floatValue();
        if (type == Type.longType)
            return leftNum.longValue() * rightNum.longValue();
        return leftNum.intValue() * rightNum.intValue();
    }

    private static Number divide(Type type, Number leftNum, Number rightNum) {
        if (type == Type.doubleType)
            return leftNum.doubleValue() / rightNum.doubleValue();
        if (type == Type.floatType)
            return leftNum.floatValue() / rightNum.floatValue();
        if (type == Type.longType)
            return leftNum.longValue() / rightNum.longValue();
        return leftNum.intValue() / rightNum.intValue();
    }

    private static Number modulo(Type type, Number leftNum, Number rightNum) {
        if (type == Type.longType)
            return leftNum.longValue() % rightNum.longValue();
        return leftNum.intValue() % rightNum.intValue();
    }
}