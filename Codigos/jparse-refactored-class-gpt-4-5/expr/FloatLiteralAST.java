package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class FloatLiteralAST extends ExpressionAST {

    public FloatLiteralAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        final String tokenString = obterTextoToken(); // TRECHO REFATORADO
        final boolean floatLiteral = eLiteralFloat(tokenString); // TRECHO REFATORADO
        type = determinarTipo(floatLiteral); // TRECHO REFATORADO
        value = converterValor(tokenString, floatLiteral); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private String obterTextoToken() {
        return (context.negative) ? "-" + getText() : getText();
    }

    // TRECHO REFATORADO
    private boolean eLiteralFloat(String tokenString) {
        char ultimoCaracter = tokenString.charAt(tokenString.length() - 1);
        return ultimoCaracter == 'f' || ultimoCaracter == 'F';
    }

    // TRECHO REFATORADO
    private Type determinarTipo(boolean floatLiteral) {
        return floatLiteral ? Type.floatType : Type.doubleType;
    }

    // TRECHO REFATORADO
    private Object converterValor(String tokenString, boolean isFloat) {
        if (isFloat) {
            return Float.valueOf(tokenString.substring(0, tokenString.length() - 1));
        } else {
            return Double.valueOf(tokenString);
        }
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
 * - O método `parseComplete()` mistura lógicas diferentes como analisar o valor textual, identificar o tipo e converter para um valor numérico.
 * - As lógicas de conversão de texto em número, bem como definição do tipo `float` ou `double` podem ser isoladas.
 * - Possibilidade de criar métodos extraídos especializados nestas operações para melhorar a legibilidade e manutenibilidade.
 * - É possível extrair uma classe auxiliar especializada na análise e interpretação do literal float/double.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * De acordo com Martin Fowler (2018) e também apoiado por Marco Tulio (princípio da clareza e manutenção fácil), esta refatoração se baseou principalmente no método de **Extração de Métodos**. Neste método, operações complexas ou trechos que representam conceitos específicos dentro da função primária são extraídos para métodos menores, com objetivos claramente definidos.
 * - `obterTextoToken()` encapsula a lógica que obtém o texto corrigido com sinal negativo.
 * - `eLiteralFloat(String)` encapsula a lógica de determinar se o texto representa um literal float.
 * - `determinarTipo(boolean)` simplifica a lógica de escolha do tipo.
 * - `converterValor(String, boolean)` separa claramente a lógica de conversão dos tipos float/double.
 *
 * Isso melhora muito a legibilidade, facilita entendimento rápido da função `parseComplete()` ao olhar suas chamadas e simplifica futuras manutenções.
 * Neste caso, optei por não extrair uma classe adicional visto que o conjunto das operações é pequeno e muito coeso, além de ter baixo potencial de reutilização atual em outros contextos. Caso futuramente haja expansão dessas operações, pode-se considerar a criação de outra classe especialista na conversão e análise de literais numéricos.
 * ### 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas:** 4
 * - **Divisão por tipo:**
 *     - 4 Extrações de Métodos
 *     - 0 Extrações de Classe (após análise, decidido que neste momento não há justificativa suficiente para Extração de Classe no contexto atual do código).
 */