//class extracted from NullLiteralAST

package jparse.expr;
import jparse.Type;
import jparse.VarList;

// TRECHO REFATORADO
// Classe auxiliar criada para prover valores constantes
public final class LiteralValueProvider {

    public Type getDefaultNullType(Type type) {
        return type;
    }

    public Type[] getDefaultExceptions(Type[] defaultExceptions) {
        return defaultExceptions;
    }

    public Object getDefaultNullValue() {
        return null;
    }

    public VarList getEmptyVarList() {
        return new VarList();
    }
}