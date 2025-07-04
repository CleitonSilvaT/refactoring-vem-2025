//class extracted from ForAST

package jparse.stmt;

import jparse.HasExceptions;
import jparse.expr.ExpressionAST;

public class ForClauses {
    private HasExceptions init;
    private ExpressionAST cond;
    private ExpressionAST incr;
    private StatementAST stmt;

    // Construtor
    public ForClauses(HasExceptions init, ExpressionAST cond, ExpressionAST incr, StatementAST stmt) {
        this.init = init;
        this.cond = cond;
        this.incr = incr;
        this.stmt = stmt;
    }

    public HasExceptions getInit() {
        return init;
    }

    public ExpressionAST getCond() {
        return cond;
    }

    public ExpressionAST getIncr() {
        return incr;
    }

    public StatementAST getStmt() {
        return stmt;
    }
}