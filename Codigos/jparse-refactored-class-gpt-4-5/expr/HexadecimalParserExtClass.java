// class extracted from NumLiteralAST
// TRECHO REFATORADO: Classe extraída que encapsula lógica de conversão hexadecimal
package jparse.expr;

import jparse.Type;

public class HexadecimalParser {

    public static Object converter(String numString, Type type) throws NumberFormatException {
        long theVal = 0L;
        for (int i = 0; i < numString.length(); i++) {
            char c = numString.charAt(i);
            theVal <<= 4;
            theVal += charToDigit(c); // TRECHO REFATORADO
        }
        return (type == Type.intType)
            ? Integer.valueOf((int) theVal)
            : Long.valueOf(theVal);
    }

    // TRECHO REFATORADO: método que separa claramente lógica conversão de caractere específico
    private static int charToDigit(char c) throws NumberFormatException {
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        if (c >= 'a' && c <= 'f')
            return c - 'a' + 10;

        throw new NumberFormatException("Caracter hexadecimal inválido: " + c);
    }
}