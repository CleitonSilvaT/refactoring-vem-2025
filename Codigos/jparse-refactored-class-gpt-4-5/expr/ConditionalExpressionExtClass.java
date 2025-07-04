//class extracted from ConditionalAST

package jparse.expr;

import jparse.Type;
import jparse.VarList;

public class ConditionalExpression {
    private final ExpressionAST condition;
    private final ExpressionAST thenExpr;
    private final ExpressionAST elseExpr;

    public ConditionalExpression(ExpressionAST condition, ExpressionAST thenExpr, ExpressionAST elseExpr) {
        this.condition = condition;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    // TRECHO REFATORADO
    public Type getCommonType() {
        final Type thenType = thenExpr.retrieveType();
        final Type elseType = elseExpr.retrieveType();

        if (thenType == null) return elseType;
        if (elseType == null) return thenType;
        if (thenType == elseType) return thenType;
        if (thenType.isAssignableFrom(elseType)) return thenType;
        if (elseType.isAssignableFrom(thenType)) return elseType;

        System.err.println("Não foi possível comparar " + thenType.getName() + " com " + elseType.getName());
        return thenType;
    }

    // TRECHO REFATORADO
    public Type[] mergeExceptions() {
        Type[] e1 = condition.getExceptionTypes();
        Type[] e2 = thenExpr.getExceptionTypes();
        Type[] e3 = elseExpr.getExceptionTypes();

        return Type.mergeTypeLists(Type.mergeTypeLists(e1, e2), e3);
    }

    // TRECHO REFATORADO
    public Object evaluateValue() {
        final Boolean cond = (Boolean) condition.getValue();
        if (cond == ExpressionAST.nonconstant) {
            return ExpressionAST.nonconstant;
        }
        return cond.booleanValue() ? thenExpr.getValue() : elseExpr.getValue();
    }

    // TRECHO REFATORADO
    public VarList getMergedVarList() {
        return new VarList(condition.getVarList(), thenExpr.getVarList(), elseExpr.getVarList());
    }

    public ExpressionAST getCondition() {
        return condition;
    }

    public ExpressionAST getThenExpr() {
        return thenExpr;
    }

    public ExpressionAST getElseExpr() {
        return elseExpr;
    }
}