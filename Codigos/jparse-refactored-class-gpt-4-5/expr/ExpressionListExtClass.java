//class extracted from ListAST

package jparse.expr;

import antlr.collections.AST;
import java.util.ArrayList;

// TRECHO REFATORADO: Extração de Classe para gerenciar lista de Expressions
public class ExpressionList {

    private final ExpressionAST[] list;

    public ExpressionList(final AST firstExpr) {
        final ArrayList<ExpressionAST> exprList = new ArrayList<>();
        for (AST a = firstExpr; a != null; a = a.getNextSibling()) {
            exprList.add((ExpressionAST)a);
            a = a.getNextSibling(); // Pula a vírgula
            if (a == null)
                break;
        }
        list = exprList.toArray(new ExpressionAST[0]);
    }

    public ExpressionAST[] getExpressions() {
        return list;
    }

    public void parseComplete() {
        for (ExpressionAST expr : list) {
            expr.parseComplete();
        }
    }

    public Type[] retrieveTypes() {
        Type[] types = new Type[list.length];
        for (int i = 0; i < list.length; i++) {
            types[i] = list[i].retrieveType();
        }
        return types;
    }

    public Type[] computeExceptions() {
        Type[] exceptions = ExpressionAST.noTypes;
        for (ExpressionAST expr : list) {
            exceptions = Type.mergeTypeLists(exceptions, expr.getExceptionTypes());
        }
        return exceptions;
    }

    public VarList aggregateVarLists() {
        VarList[] varLists = new VarList[list.length];
        for (int i = 0; i < list.length; i++) {
            varLists[i] = list[i].getVarList();
        }
        return new VarList(varLists);
    }

    public Object retrieveLastValue() {
        return (list.length > 0) ? list[list.length - 1].getValue() : ExpressionAST.nonconstant;
    }
}