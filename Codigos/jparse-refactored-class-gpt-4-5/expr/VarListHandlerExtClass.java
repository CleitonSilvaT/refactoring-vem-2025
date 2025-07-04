// TRECHO REFATORADO - class extracted from AssignAST - Extração de classe para encapsular a manipulação de variáveis
package jparse.expr;

import jparse.VarList;

public class VarListHandler {
    private final ExpressionAST lhs;
    private final ExpressionAST rhs;

    public VarListHandler(ExpressionAST lhs, ExpressionAST rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public VarList getVarListCombinado() {
        final VarList leftList = lhs.getVarList();
        final VarList rightList = rhs.getVarList();
        return new VarList(leftList, rightList, true);
    }
}