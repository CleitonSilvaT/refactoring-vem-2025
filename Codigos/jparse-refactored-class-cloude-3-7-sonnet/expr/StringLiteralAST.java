package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class StringLiteralAST extends ExpressionAST {

    // TRECHO REFATORADO: Extração da inicialização para um método separado
    public StringLiteralAST(final Token token) {
        super(token);
        inicializarLiteralString();
    }
    
    // TRECHO REFATORADO: Método extraído para encapsular a lógica de inicialização do literal de string
    private void inicializarLiteralString() {
        type = Type.stringType;    // JLS 15.8.1
        value = getText();
    }

    // TRECHO REFATORADO: Método computeType simplificado
    protected Type computeType() {
        return type;
    }

    // TRECHO REFATORADO: Método computeExceptions simplificado
    protected Type[] computeExceptions() {
        return noTypes;
    }

    // TRECHO REFATORADO: Método computeValue simplificado
    protected Object computeValue() {
        return value;
    }

    public VarList getVarList() {
        return new VarList();
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * A classe `StringLiteralAST` é relativamente simples e coesa, representando um nó AST para literais de string. Contudo, algumas oportunidades potenciais de refatoração podem ser identificadas:
 * - O construtor realiza várias operações: chamada ao construtor da superclasse, inicialização do tipo e do valor
 * - Os métodos computeType, computeExceptions e computeValue são muito simples e podem ser convertidos para implementações mais diretas
 * - A classe poderia se beneficiar de uma separação das responsabilidades relacionadas à inicialização de literais de string
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método (inicializarLiteralString):**
 *     - Segundo Martin Fowler, a Extração de Método é recomendada quando temos um fragmento de código que pode ser agrupado e nomeado adequadamente para aumentar a legibilidade.
 *     - O método `inicializarLiteralString()` encapsula a lógica específica de inicialização de um literal de string, tornando o construtor mais simples e focado em sua responsabilidade principal.
 *     - Isso facilita a manutenção e compreensão do código, permitindo também a reutilização dessa lógica caso seja necessário em futuros métodos.
 *
 * 2. **Simplificação dos métodos compute:**
 *     - Embora estes métodos já fossem bastante simples, houve uma pequena melhoria na formatação para manter consistência com o restante do código.
 *     - Esta é uma refatoração menor, mas contribui para a legibilidade geral do código.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas:** 1
 * - **Divisão por tipo:**
 *     - **Extração de Método:** 1 (`inicializarLiteralString`)
 *     - **Extração de Classe:** 0
 *
 * A classe `StringLiteralAST` é bastante direta e coesa, limitando as oportunidades para refatorações significativas. A principal refatoração foi a extração do método de inicialização para melhorar a legibilidade e manutenibilidade do código. Não foi identificada a necessidade de Extração de Classe, pois a classe já possui uma única responsabilidade bem definida: representar um literal de string na árvore de sintaxe abstrata.
 *
 */