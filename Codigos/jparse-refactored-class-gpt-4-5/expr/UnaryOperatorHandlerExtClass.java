// class extracted from UnaryArithAST

package jparse.expr;

import jparse.JavaTokenTypes;
import jparse.Type;

public class OperadorUnarioHandler implements JavaTokenTypes {

    private final int operador;
    private final ExpressionAST operand;

    public OperadorUnarioHandler(int operador, ExpressionAST operand) {
        this.operador = operador;
        this.operand = operand;
    }

    // TRECHO REFATORADO
    public Type obterTipo() {
        final Type opType = operand.retrieveType();
        if (naoEhIncOuDec() && ehByteShortOuChar(opType)) {
            return Type.intType;
        }
        return opType;
    }

    // TRECHO REFATORADO
    private boolean naoEhIncOuDec() {
        return operador != INC && operador != DEC;
    }

    // TRECHO REFATORADO
    private boolean ehByteShortOuChar(Type tipo) {
        return tipo == Type.byteType || tipo == Type.shortType || tipo == Type.charType;
    }

    // TRECHO REFATORADO
    public Object calcularValor() {
        if (operadorModificaVariavel()) {
            return ExpressionAST.nonconstant;
        }

        final Object valorOperando = operand.getValue();
        if (valorOperando == ExpressionAST.nonconstant) {
            return ExpressionAST.nonconstant;
        }

        if (operador == PLUS || operador == MINUS) {
            return valorOperando;
        }

        return calculaBitwiseNot(valorOperando);
    }

    // TRECHO REFATORADO
    private boolean operadorModificaVariavel() {
        return operador == INC || operador == DEC || operador == POST_INC || operador == POST_DEC;
    }

    // TRECHO REFATORADO
    private Object calculaBitwiseNot(Object valorOperando) {
        final Number num = (Number) valorOperando;
        if (operand.retrieveType() == Type.intType) {
            return new Integer(~num.intValue());
        }
        return new Long(~num.longValue());
    }
}