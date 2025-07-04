package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class NumLiteralAST extends ExpressionAST {

    private Type type;
    private Object value;

    public NumLiteralAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído método para obter a string do token
        final String tokenString = getTokenString();
        
        // TRECHO REFATORADO: Extraído método para determinar o tipo do literal (int ou long)
        final boolean intLiteral = determinarTipoLiteral(tokenString);
        type = intLiteral ? Type.intType : Type.longType;
        
        // TRECHO REFATORADO: Extraído método para verificar se é decimal ou hexadecimal
        final boolean decimal = isDecimal(tokenString);
        
        // TRECHO REFATORADO: Extraído método para obter a string numérica
        final String numString = extrairStringNumero(tokenString, decimal, intLiteral);
        
        // TRECHO REFATORADO: Extraído método para converter a string em valor numérico
        converterParaValorNumerico(numString, decimal, intLiteral);
    }

    // TRECHO REFATORADO: Método extraído para obter a string do token
    private String getTokenString() {
        return (context.negative) ? "-" + getText() : getText();
    }

    // TRECHO REFATORADO: Método extraído para determinar o tipo do literal
    private boolean determinarTipoLiteral(String tokenString) {
        final int length = tokenString.length();
        final char lastChar = tokenString.charAt(length - 1);
        return lastChar != 'l' && lastChar != 'L';
    }

    // TRECHO REFATORADO: Método extraído para verificar se é decimal ou hexadecimal
    private boolean isDecimal(String tokenString) {
        return tokenString.length() <= 2 ||
               tokenString.charAt(0) != '0' ||
               Character.toUpperCase(tokenString.charAt(1)) != 'X';
    }

    // TRECHO REFATORADO: Método extraído para obter a string numérica
    private String extrairStringNumero(String tokenString, boolean decimal, boolean intLiteral) {
        final int length = tokenString.length();
        return tokenString.substring(decimal ? 0 : 2,
                          intLiteral ? length : length - 1);
    }

    // TRECHO REFATORADO: Método extraído para converter a string em valor numérico
    private void converterParaValorNumerico(String numString, boolean decimal, boolean intLiteral) {
        try {
            value = intLiteral
                ? (Object)Integer.valueOf(numString, decimal ? 10 : 16)
                : (Object)Long.valueOf(numString, decimal ? 10 : 16);
        } catch (NumberFormatException numEx) {
            // TRECHO REFATORADO: Extraído método para processar valores hexadecimais grandes
            value = processarHexadecimalGrande(numString, intLiteral, numEx);
        }
    }

    // TRECHO REFATORADO: Método extraído para processar valores hexadecimais grandes
    private Object processarHexadecimalGrande(String numString, boolean intLiteral, NumberFormatException numEx) {
        long theVal = 0L;
        for (int i = 0; i < numString.length(); i++) {
            final char c = numString.charAt(i);
            theVal <<= 4;
            if (c >= '0' && c <= '9')
                theVal += c - '0';
            else if (c >= 'A' && c <= 'F')
                theVal += c - 'A' + 10;
            else if (c >= 'a' && c <= 'f')
                theVal += c - 'a' + 10;
            else
                throw numEx;
        }
        return intLiteral
            ? (Object)new Integer((int)theVal)
            : (Object)new Long(theVal);
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
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `NumLiteralAST.java`, identifiquei as seguintes oportunidades de refatoração:
 * 1. O método `parseComplete()` é muito longo e realiza várias responsabilidades distintas:
 *     - Determinar o tipo do literal numérico (int ou long)
 *     - Verificar se o número é decimal ou hexadecimal
 *     - Extrair a string do número
 *     - Converter a string para um valor numérico
 *     - Lidar com tratamento de exceções para casos especiais
 *
 * 2. O tratamento de exceções contém um algoritmo complexo para analisar valor hexadecimal dígito por dígito, que pode ser extraído para um método separado.
 * 3. A classe mistura responsabilidades de parsing e determinação de tipo, o que poderia ser melhor organizado.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método - getTokenString()**: Foi extraído um método para obter a string do token, tornando o código mais legível e encapsulando a lógica de verificação de números negativos.
 * 2. **Extração de Método - determinarTipoLiteral()**: Extraí um método que verifica se o literal é do tipo int ou long, tornando a intenção do código mais clara e facilitando futuras modificações.
 * 3. **Extração de Método - isDecimal()**: Este método encapsula a lógica complexa para determinar se um número é decimal ou hexadecimal, tornando o código principal mais limpo e a lógica mais explícita.
 * 4. **Extração de Método - extrairStringNumero()**: Extraí a lógica de substring para um método próprio, tornando o código mais legível e mais fácil de entender.
 * 5. **Extração de Método - converterParaValorNumerico()**: Este método encapsula toda a lógica de conversão da string para valor numérico, incluindo o tratamento de exceção.
 * 6. **Extração de Método - processarHexadecimalGrande()**: Extraí o algoritmo complexo de tratamento de hexadecimais grandes para um método separado, tornando o código principal mais limpo e facilitando a manutenção do algoritmo específico.
 *
 * Estas refatorações seguem as diretrizes do livro de Martin Fowler sobre Extração de Método, que enfatiza a importância de métodos curtos, com nomes descritivos, que realizam uma única tarefa. Conforme Fowler destaca, "quando você se sente na necessidade de comentar algo, primeiro tente extrair um método que diga o mesmo que você queria colocar no comentário".
 * A divisão das responsabilidades também está de acordo com os princípios do Engenharia de Software Moderna, que defende métodos coesos e de fácil manutenção. As refatorações realizadas aumentam a coesão da classe, facilitam o entendimento e tornam o código mais modular.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 6
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 6
 *     - **Extração de Classe**: 0
 *
 * A refatoração focou na extração de métodos para tornar o código mais legível e manutenível. Não foi necessário extrair classes adicionais, pois a classe já possui uma responsabilidade bem definida: representar e processar um literal numérico em uma árvore de sintaxe abstrata (AST).
 */