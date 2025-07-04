//class extracted from BooleanLiteralAST

package jparse.expr;

// TRECHO REFATORADO: Nova classe extraída
public class BooleanLiteral {
    private final boolean valor;

    private BooleanLiteral(boolean valor) {
        this.valor = valor;
    }

    // TRECHO REFATORADO: novo método extraído
    public static Boolean fromTokenText(String texto) {
        return Boolean.valueOf(texto);
    }

    public boolean getValor() {
        return valor;
    }
}