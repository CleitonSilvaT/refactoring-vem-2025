package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class UnaryArithAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST operand;

    public UnaryArithAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        tratarNegativo(); // TRECHO REFATORADO
        operand = (ExpressionAST) getFirstChild();
        operand.parseComplete();
    }

    // TRECHO REFATORADO
    private void tratarNegativo() {
        if (getType() == MINUS) {
            context.negative = !context.negative;
        }
    }

    protected Type computeType() {
        return new OperadorUnarioHandler(getType(), operand).obterTipo(); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() {
        return operand.getExceptionTypes();
    }

    protected Object computeValue() {
        return new OperadorUnarioHandler(getType(), operand).calcularValor(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return operand.getVarList();
    }

    public ExpressionAST getOperand() {
        return operand;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * Analisando o código fornecido, identifiquei duas oportunidades principais:
 * - **Extração de Métodos**:
 *     - O método `computeType()` realiza uma verificação relativamente complexa que pode ser extraída.
 *     - O método `computeValue()` possui uma lógica de decisão que pode ser separada em novos métodos menores (checagem de operadores e processamento de-bitwise).
 *
 * - **Extração de Classe**:
 *     - A lógica condicional específica ao operator (plus, minus, inc e dec) pode ser encapsulada em uma nova classe auxiliar `OperadorUnarioHandler` específica para tratar desses operadores e simplificar a classe principal.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das Refatorações:
 * A aplicação das refatorações seguiu profundamente as diretrizes propostas por Martin Fowler e Marco Tulio.
 * - **Princípio de Responsabilidade Única (Extração de Classe)**:
 *     - A criação da classe `OperadorUnarioHandler` encapsula responsabilidades específicas relacionadas ao tratamento lógico dos operadores unários, tornando o código mais organizado e modularizado.
 *
 * - **Métodos Pequenos e Descritivos (Extração de Métodos)**:
 *     - Extraindo pequenos métodos como `tratarNegativo()`, `operadorModificaVariavel()`, e `calculaBitwiseNot()` simplifica a complexidade dos códigos e proporciona clareza semântica. Essa técnica contribui para um entendimento mais rápido, reduzindo o esforço cognitivo obrigatório.
 *
 * - **Redução de Duplicação de Código**:
 *     - Ao extrair os métodos para tratamento de operadores (`operadorModificaVariavel`, `calculaBitwiseNot`) numa classe separada, evita-se que tais verificações sejam repetidas na classe principal, promovendo clareza.
 *
 * Essas modificações tornam a classe original mais fácil de entender, facilitando futuras manutenções e testes unitários.
 * ### 4) Resumo das alterações:
 *
 * | Tipo de Refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Método | 5 |
 * | Extração de Classe | 1 |
 * | ------------------------- | ------------ |
 * | **Total Refatorações:** | **6** |
 */