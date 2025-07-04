//class extracted from WhileAST

package jparse.stmt;

import jparse.Type;
import jparse.VarList;
import jparse.expr.ExpressionAST;

public class WhileHelper {  // TRECHO REFATORADO - Extração de classe

    public Type[] mesclarExcecoes(ExpressionAST condicao, StatementAST corpo) {  // TRECHO REFATORADO
        return Type.mergeTypeLists(condicao.getExceptionTypes(), corpo.getExceptionTypes());
    }

    public VarList unirVariaveis(ExpressionAST condicao, StatementAST corpo) { // TRECHO REFATORADO
        return new VarList(condicao.getVarList(), corpo.getVarList());
    }
}