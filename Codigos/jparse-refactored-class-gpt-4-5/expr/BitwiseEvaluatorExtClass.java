// class extracted from BitwiseAST

package jparse.expr;

// TRECHO REFATORADO: nova classe responsável pela avaliação bitwise de valores constantes
public class BitwiseEvaluator implements JavaTokenTypes {

    public static Object evaluate(int tokenType, Type expressionType, Object leftObj, Object rightObj) {
        if (leftObj == ExpressionAST.nonconstant || rightObj == ExpressionAST.nonconstant) {
            return ExpressionAST.nonconstant;
        }

        if (expressionType == Type.booleanType) {
            return evaluateBoolean(tokenType, leftObj, rightObj);
        } else if (expressionType == Type.longType) {
            return evaluateLong(tokenType, leftObj, rightObj);
        } else {
            return evaluateInt(tokenType, leftObj, rightObj);
        }
    }

    // TRECHO REFATORADO: método extraído para avaliar booleanos
    private static Boolean evaluateBoolean(int tokenType, Object leftObj, Object rightObj) {
        boolean leftBool = ((Boolean) leftObj);
        boolean rightBool = ((Boolean) rightObj);
        switch (tokenType) {
            case BOR:
                return leftBool | rightBool;
            case BXOR:
                return leftBool ^ rightBool;
            case BAND:
                return leftBool & rightBool;
            default:
                return (Boolean) ExpressionAST.nonconstant;
        }
    }

    // TRECHO REFATORADO: método extraído para avaliar inteiros longos
    private static Long evaluateLong(int tokenType, Object leftObj, Object rightObj) {
        long leftLong = ((Number) leftObj).longValue();
        long rightLong = ((Number) rightObj).longValue();
        switch (tokenType) {
            case BOR:
                return leftLong | rightLong;
            case BXOR:
                return leftLong ^ rightLong;
            case BAND:
                return leftLong & rightLong;
            default:
                return (Long) ExpressionAST.nonconstant;
        }
    }

    // TRECHO REFATORADO: método extraído para avaliar inteiros normais
    private static Integer evaluateInt(int tokenType, Object leftObj, Object rightObj) {
        int leftInt = ((Number) leftObj).intValue();
        int rightInt = ((Number) rightObj).intValue();
        switch (tokenType) {
            case BOR:
                return leftInt | rightInt;
            case BXOR:
                return leftInt ^ rightInt;
            case BAND:
                return leftInt & rightInt;
            default:
                return (Integer) ExpressionAST.nonconstant;
        }
    }
}