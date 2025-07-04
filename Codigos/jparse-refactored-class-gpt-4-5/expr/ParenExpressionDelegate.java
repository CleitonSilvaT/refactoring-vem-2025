// class extracted from ParenthesizedAST

package jparse.expr;

import jparse.Type;
import jparse.VarList;

public class ParenExpressionDelegate {

    private final ExpressionAST expression;

    public ParenExpressionDelegate(ExpressionAST expression) {
        this.expression = expression;
    }

    public void parseComplete() {
        expression.parseComplete();
    }

    public Type computeType() {
        return expression.retrieveType(); // conforme JLS 15.8.5
    }

    public Type[] computeExceptions() {
        return expression.getExceptionTypes();
    }

    public Object computeValue() {
        return expression.getValue();
    }

    public VarList getVarList() {
        return expression.getVarList();
    }

    public ExpressionAST getExpression() {
        return expression;
    }
}