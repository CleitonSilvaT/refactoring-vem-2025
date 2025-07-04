//class extracted from CharLiteralAST

package jparse.expr;

// TRECHO REFATORADO: Classe extraída responsável por armazenar e gerir o valor do caractere literal
public class CharLiteralValue {
    private final char valorLiteral;

    public CharLiteralValue(String texto) {
        this.valorLiteral = texto.charAt(0);
    }

    public Character getValor() {
        return valorLiteral;
    }
}