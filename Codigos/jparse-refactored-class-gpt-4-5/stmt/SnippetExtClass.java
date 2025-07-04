//class extracted from ExpressionAST

package jparse.stmt;

import jparse.Type;
import jparse.expr.ExpressionAST;

public class ExpressionHelper { //TRECHO REFATORADO - Extração de Classe
    private final ExpressionAST expression;

    public ExpressionHelper(ExpressionAST expression) {
        this.expression = expression;
    }

    public Type[] obterExceptions() { //TRECHO REFATORADO - método extraído
        return expression.getExceptionTypes();
    }

    public StatementAST[] obterControle(StatementAST next) { //TRECHO REFATORADO - método extraído
        return new StatementAST[]{ next };
    }
}