//class extracted from JavaAST

package jparse;

import antlr.CommonHiddenStreamToken;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class TokenPrinter {

    private final JavaAST javaAST;

    public TokenPrinter(JavaAST javaAST) {
        this.javaAST = javaAST;
    }

    public void printNodeWithHiddenTokens(final OutputStreamWriter output) {
        try {
            output.write(javaAST.getText());
            printHiddenAfter(output);
        } catch (IOException ignored) {
            // Tratamento mínimo, pois exceção não é esperada
        }
    }

    public void printHiddenAfter(final OutputStreamWriter output) {
        try {
            for (CommonHiddenStreamToken t = javaAST.getHiddenAfter(); t != null; t = t.getHiddenAfter()) {
                output.write(t.getText());
            }
        } catch (IOException ignored) {
            // Tratamento mínimo, pois exceção não é esperada
        }
    }

    public void printHiddenBefore(final OutputStreamWriter output) {
        CommonHiddenStreamToken tok = javaAST.getHiddenBefore();
        CommonHiddenStreamToken last = null;
        while (tok != null) {
            last = tok;
            tok = tok.getHiddenBefore();
        }
        try {
            for (CommonHiddenStreamToken t = last; t != null; t = t.getHiddenAfter()) {
                output.write(t.getText());
            }
        } catch (IOException ignored) {
            // Tratamento mínimo, pois exceção não é esperada
        }
    }
}
