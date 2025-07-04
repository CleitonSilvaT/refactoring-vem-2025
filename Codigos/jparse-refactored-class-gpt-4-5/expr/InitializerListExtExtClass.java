//class extracted from ArrayInitAST

package jparse.expr;

import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;
import java.util.ArrayList;

public class InitializerList implements JavaTokenTypes { // TRECHO REFATORADO: classe extraída

    private final ExpressionAST[] initializers;

    public InitializerList(AST firstChild) { // TRECHO REFATORADO: lógica extraída do método original parseComplete()
        ArrayList<ExpressionAST> list = new ArrayList<>();
        for (AST a = firstChild; a != null && a.getType() != RCURLY;
             a = a.getFirstChild()) {
            list.add((ExpressionAST) a);
            a = a.getNextSibling();
            if (a == null || a.getType() != COMMA)
                break;
        }
        initializers = list.toArray(new ExpressionAST[0]);
    }

    public void completeInitializers() { // TRECHO REFATORADO: método extraído para simplificação do método parseComplete()
        for (ExpressionAST initializer : initializers) {
            initializer.parseComplete();
        }
    }

    public ExpressionAST[] getInitializers() {
        return initializers;
    }

    public Type[] mergeExceptionTypes() { // TRECHO REFATORADO: método extraído do método computeExceptions original
        Type[] e = ExpressionAST.noTypes;
        for (ExpressionAST initializer : initializers) {
            e = Type.mergeTypeLists(e, initializer.getExceptionTypes());
        }
        return e;
    }

    public VarList buildCombinedVarList() { // TRECHO REFATORADO: método extraído do método original getVarList()
        final VarList[] lists = new VarList[initializers.length];
        for (int i = 0; i < initializers.length; i++) {
            lists[i] = initializers[i].getVarList();
        }
        return new VarList(lists);
    }
}