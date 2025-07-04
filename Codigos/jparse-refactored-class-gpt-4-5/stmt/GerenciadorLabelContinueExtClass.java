//class extracted from ContinueAST

package jparse.stmt;

import jparse.expr.IdentifierAST;

public class GerenciadorLabelContinue { // TRECHO REFATORADO

    private final ContinueAST contexto;

    public GerenciadorLabelContinue(ContinueAST contexto) { // TRECHO REFATORADO
        this.contexto = contexto;
    }

    public void definirControle(IdentifierAST label) { // TRECHO REFATORADO
        if (label == null) {
            contexto.control = new StatementAST[]{contexto.context.continueTarget()};
        }
    }

    public StatementAST[] obterControle(IdentifierAST label) { // TRECHO REFATORADO
        if (label != null) {
            return new StatementAST[]{contexto.symTable.getLabel(label.getName())};
        }
        return contexto.control;
    }
}