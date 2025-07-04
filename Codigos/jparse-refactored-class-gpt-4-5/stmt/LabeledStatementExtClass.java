//class extracted from LabelAST

package jparse.stmt;

import jparse.Type;
import jparse.VarList;

// TRECHO REFATORADO - Classe extraída por encapsular operações comuns delegadas.
public class LabeledStatement {
    
    private final StatementAST statement;

    public LabeledStatement(StatementAST statement) {
        this.statement = statement;
    }

    public Type[] computeExceptions() {
        return statement.getExceptionTypes();
    }

    public StatementAST[] computeControl() {
        return statement.nextControlPoints();
    }

    public VarList getVarList() {
        return statement.getVarList();
    }

    public void parseComplete() {
        statement.parseComplete();
    }
    
    public StatementAST getStatement() {
        return statement;
    }
}