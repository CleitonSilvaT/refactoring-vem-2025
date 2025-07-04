package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class NumLiteralAST extends ExpressionAST {

    public NumLiteralAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        final String tokenString = (context.negative) 
            ? "-" + getText() : getText();

        type = determineLiteralType(tokenString); // TRECHO REFATORADO
        
        final boolean decimal = isDecimalLiteral(tokenString); // TRECHO REFATORADO
        final String numString = extractNumber(tokenString, decimal); // TRECHO REFATORADO
        
        try {
            tipoLiteral(tokenString, numString, decimal); // TRECHO REFATORADO
        } catch (NumberFormatException numEx) {
            value = HexadecimalParser.converter(numString, type); // TRECHO REFATORADO
        }
    }

    // TRECHO REFATORADO: Extração de método que determina o tipo literal
    private Type determineLiteralType(final String tokenString) {
        final char lastChar = tokenString.charAt(tokenString.length() - 1);
        return (lastChar != 'l' && lastChar != 'L') ? Type.intType : Type.longType;
    }

    // TRECHO REFATORADO: Extração de método que determina se é decimal ou hexadecimal
    private boolean isDecimalLiteral(final String tokenString) {
        return tokenString.length() <= 2 ||
               tokenString.charAt(0) != '0' ||
               Character.toUpperCase(tokenString.charAt(1)) != 'X';
    }

    // TRECHO REFATORADO: Extração de método especializando a extração literal
    private String extractNumber(final String tokenString, final boolean decimal) {
        final int length = tokenString.length();
        final boolean intLiteral = (type == Type.intType);
        return tokenString.substring(decimal ? 0 : 2, intLiteral ? length : length - 1);
    }
    
    // TRECHO REFATORADO: Extração da conversão literal para método separado
    private void tipoLiteral(String tokenString, String numString, boolean decimal) {
        final boolean intLiteral = (type == Type.intType);
        value = intLiteral 
            ? Integer.valueOf(numString, decimal ? 10 : 16) 
            : Long.valueOf(numString, decimal ? 10 : 16);
    }

    protected Type computeType() {
        return type;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        return value;
    }

    public VarList getVarList() {
        return new VarList();
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - **Excesso de Lógica em `parseComplete()`**: O método `parseComplete()` contém lógica extensa e não claramente organizada, tratando de forma conjunta operações distintas (identificação do tipo literal, conversões numéricas, tratamento de exceções, etc). Esse método realiza diversas operações e responsabilidades diferentes, violando o princípio SRP (Single Responsibility Principle).
 * - **Tratamento Número Hexadecimal**: O trecho no bloco catch responsável pela conversão manual de um número hexadecimal poderia ter sua lógica encapsulada em outro método privado.
 * - **Potencial criação de classe especializada para conversão de números (Extração de classe)**: Há uma lógica significativa relacionada à conversão numérica do literal, justificando a extração para uma classe especializada, aumentando a coesão do código.
 *
 * ### 2) Classe refatorada com os devidos comentários (// TRECHO REFATORADO):
 *
 * ### 3) Justificativa das refatorações:
 * As refatorações acima têm como base o conceito de responsabilidade única de Martin Fowler (2018), melhorando exatamente os pontos do código onde havia mistura de preocupações. Além disso, a criação da classe `HexadecimalParser` é fortemente baseada no conceito de classe coesa apresentado por Marco Tulio, que recomenda encapsular lógica específica de operação (nesse caso, conversão hexadecimal) em classes especializadas, reduzindo a complexidade do código e aumentando a manutenibilidade e reuso.
 * - **Extração de métodos** (`determineLiteralType()`, `isDecimalLiteral()`, `extractNumber()`, `tipoLiteral()`, `charToDigit()`), foram utilizadas para tornar o código mais organizado, claro e autodescritivo, simplificando os trechos mais complexos incrementando a legibilidade e facilitando o entendimento e manutenção do código.
 * - **Extração de classe** (`HexadecimalParser`) realizada à parte da lógica complexa relacionada a conversão hexadecimal, removendo uma preocupação secundária da classe original e permitindo que ela se foque em sua responsabilidade primária (representação de nó numérico em AST). Isso reduz a complexidade da classe original e permite futuros reaproveitamentos da nova classe.
 *
 * ### 4) Resumo das alterações:
 * - **Total de refatorações**: 6
 * - **Extrações de método**: 5
 *     - `determineLiteralType()`
 *     - `isDecimalLiteral()`
 *     - `extractNumber()`
 *     - `tipoLiteral()`
 *     - `charToDigit()`
 *
 * - **Extração de classe**: 1
 *     - Extração da classe especializada em conversões numéricas: `HexadecimalParser`
 */