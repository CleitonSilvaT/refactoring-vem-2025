//class extracted from CatchAST

package jparse.stmt;

import jparse.Type;
import jparse.expr.VarAST;
import java.util.ArrayList;

// TRECHO REFATORADO - classe extraída para gerenciar tratamento das exceções no catch
public class ExceptionHandler {

    private final VarAST param;

    public ExceptionHandler(final VarAST param) {
        this.param = param;
    }

    public Type[] removeCaughtExceptions(final Type[] exceptions) {
        final Type theCatch = param.retrieveType();
        final ArrayList<Type> newList = new ArrayList<>();
        for (Type exception : exceptions) {
            if (!isCaughtByCatchClause(theCatch, exception)) {
                newList.add(exception);
            }
        }
        return newList.toArray(new Type[0]);
    }

    // TRECHO REFATORADO - método extraído para melhorar clareza da condição lógica
    private boolean isCaughtByCatchClause(Type catchType, Type exceptionType) {
        return catchType.isAssignableFrom(exceptionType);
    }
}