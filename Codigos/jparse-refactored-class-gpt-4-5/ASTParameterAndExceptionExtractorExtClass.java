// class extracted from ConstrAST

package jparse;

import antlr.collections.AST;
import java.util.ArrayList;
import jparse.expr.IdentifierAST;

public class ASTExtractor {

    // TRECHO REFATORADO
    public static ParameterAST[] extrairParametros(JavaAST parameters) {
        final ArrayList<ParameterAST> pTypes = new ArrayList<>();
        for (AST p = parameters.getFirstChild(); p != null; p = p.getNextSibling()) {
            pTypes.add((ParameterAST) p);
            p = p.getNextSibling();
            if (p == null) break;
        }
        return pTypes.toArray(new ParameterAST[0]);
    }

    // TRECHO REFATORADO
    public static IdentifierAST[] extrairExcecoes(JavaAST exceptions) {
        if (exceptions == null) return new IdentifierAST[0];

        final ArrayList<IdentifierAST> eTypes = new ArrayList<>();
        for (AST e = exceptions.getFirstChild(); e != null; e = e.getNextSibling()) {
            eTypes.add((IdentifierAST) e);
            e = e.getNextSibling();
            if (e == null) break;
        }
        return eTypes.toArray(new IdentifierAST[0]);
    }
}