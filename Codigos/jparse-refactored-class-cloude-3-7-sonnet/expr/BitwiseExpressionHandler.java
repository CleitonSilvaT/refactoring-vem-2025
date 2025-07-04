package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class BitwiseAST extends ExpressionAST implements JavaTokenTypes {

    // TRECHO REFATORADO: Adicionado objeto da classe BitwiseOperands para gerenciar operandos
    private BitwiseOperands operands;

    public BitwiseAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Inicialização do objeto operands
        this.operands = new BitwiseOperands();
    }

    // TRECHO REFATORADO: Método para configurar os operandos através do objeto operands
    public void setOperands(ExpressionAST left, ExpressionAST right) {
        operands.setLeft(left);
        operands.setRight(right);
    }

    public void parseComplete() {/* implementation omitted for shortness */}

    protected Type computeType() {/* implementation omitted for shortness */}

    protected Type[] computeExceptions() {/* implementation omitted for shortness */}

    protected Object computeValue() {/* implementation omitted for shortness */}

    public VarList getVarList() {/* implementation omitted for shortness */}

    public ExpressionAST getLeft() {
        // TRECHO REFATORADO: Delegando para o objeto operands
        return operands.getLeft();
    }

    public ExpressionAST getRight() {
        // TRECHO REFATORADO: Delegando para o objeto operands
        return operands.getRight();
    }
}

// TRECHO REFATORADO: Nova classe extraída para gerenciar os operandos da expressão
class BitwiseOperands {
    private ExpressionAST left;
    private ExpressionAST right;

    public BitwiseOperands() {
        // Construtor padrão
    }

    public ExpressionAST getLeft() {
        return left;
    }

    public void setLeft(ExpressionAST left) {
        this.left = left;
    }

    public ExpressionAST getRight() {
        return right;
    }

    public void setRight(ExpressionAST right) {
        this.right = right;
    }
}

/**
 * # Refatoração de BitwiseAST.java
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe BitwiseAST, identifiquei oportunidades de refatoração:
 * - A classe atual possui variáveis de instância (`left` e `right`) sem métodos adequados para configurá-las
 * - Os getters `getLeft()` e `getRight()` não seguem boas práticas de encapsulamento, já que não há setters correspondentes
 * - A classe mantém lógica de operações bitwise junto com gerenciamento dos operandos, violando o Princípio da Responsabilidade Única
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * As refatorações foram realizadas seguindo os princípios de Martin Fowler e os conceitos de Engenharia de Software Moderna:
 * 1. **Extração de Classe (BitwiseOperands)**:
 *     - Aplicada para separar responsabilidades, seguindo o princípio SRP (Single Responsibility Principle)
 *     - A classe BitwiseAST agora foca nas operações bitwise, enquanto a classe BitwiseOperands gerencia exclusivamente os operandos
 *     - Segundo Fowler, essa refatoração é indicada quando uma classe possui atributos que podem ser agrupados em uma responsabilidade coesa
 *
 * 2. **Adição do método setOperands**:
 *     - Fornece um meio adequado para configurar os operandos, melhorando o encapsulamento
 *     - Esse método facilita a manutenção, tornando explícita a forma de configurar os operandos
 *
 * 3. **Delegação dos métodos getLeft e getRight para BitwiseOperands**:
 *     - Mantém a interface pública da classe original, respeitando o princípio de não alterar o comportamento do código
 *     - Permite que a classe BitwiseAST foque em sua responsabilidade principal, delegando o gerenciamento de operandos
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 2
 * - **Divisão por tipo**:
 *     - **Extração de Classe**: 1 (BitwiseOperands)
 *     - **Extração de Método**: 1 (setOperands)
 *
 * Esta refatoração aumenta a manutenibilidade do código através da separação de responsabilidades, melhora o encapsulamento e torna a estrutura do código mais clara e coesa, sem alterar seu comportamento original.
 */