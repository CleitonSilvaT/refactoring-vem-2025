package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class BooleanAST extends ExpressionAST implements JavaTokenTypes {

    // TRECHO REFATORADO: Criação da classe interna para encapsular os operandos da expressão
    private BooleanOperands operands;

    public BooleanAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Inicialização da classe interna
        operands = new BooleanOperands();
    }

    public void parseComplete() {/* implementation omitted for shortness */}

    protected Type computeType() {
        return Type.booleanType;
    }

    protected Type[] computeExceptions() {/* implementation omitted for shortness */}

    protected Object computeValue() {/* implementation omitted for shortness */}

    public VarList getVarList() {/* implementation omitted for shortness */}

    // TRECHO REFATORADO: Delegação para a classe interna
    public ExpressionAST getLeft() {
        return operands.getLeft();
    }

    // TRECHO REFATORADO: Delegação para a classe interna
    public ExpressionAST getRight() {
        return operands.getRight();
    }
    
    // TRECHO REFATORADO: Adicionados métodos setters para completar a API
    public void setLeft(ExpressionAST leftExpression) {
        operands.setLeft(leftExpression);
    }
    
    // TRECHO REFATORADO: Adicionados métodos setters para completar a API
    public void setRight(ExpressionAST rightExpression) {
        operands.setRight(rightExpression);
    }
    
    // TRECHO REFATORADO: Classe interna para encapsular os operandos da expressão booleana
    private static class BooleanOperands {
        private ExpressionAST left;
        private ExpressionAST right;
        
        public ExpressionAST getLeft() {
            return left;
        }
        
        public void setLeft(ExpressionAST leftExpression) {
            this.left = leftExpression;
        }
        
        public ExpressionAST getRight() {
            return right;
        }
        
        public void setRight(ExpressionAST rightExpression) {
            this.right = rightExpression;
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe `BooleanAST.java`, encontrei as seguintes oportunidades de refatoração:
 * 1. **Ausência de métodos para definir os valores dos campos `left` e `right`**: A classe possui getters para esses campos, mas não tem setters correspondentes, indicando uma possível incompletude na API.
 * 2. **Operações de expressão booleana**: A classe representa uma expressão booleana, mas não possui métodos específicos para operações booleanas, o que poderia ser extraído para melhorar a coesão.
 * 3. **Classe com responsabilidade dupla**: A classe tem responsabilidade tanto pelo armazenamento da expressão (lado esquerdo e direito) quanto pela lógica de computação de tipo, exceções e valor.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe (BooleanOperands)**:
 *     - Seguindo o princípio de Responsabilidade Única de Martin Fowler, extraí uma classe interna `BooleanOperands` para encapsular os operandos da expressão booleana.
 *     - Esta refatoração melhora a coesão ao separar a responsabilidade de armazenamento dos operandos da lógica de computação.
 *     - Facilita futuras extensões relacionadas aos operandos sem modificar a classe principal.
 *
 * 2. **Adição de métodos setters**:
 *     - Completei a API adicionando métodos `setLeft` e `setRight` que estavam ausentes.
 *     - Isso segue o princípio de design consistente e permite uma melhor interação com a classe.
 *     - Os setters delegam para a classe interna, mantendo o encapsulamento.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 2
 *     - **Extração de Classe**: 1 (criação da classe interna `BooleanOperands`)
 *     - **Extração de Método**: 0 (foram adicionados métodos novos, mas não houve extração de lógica existente)
 *
 * A refatoração principal foi a Extração de Classe para separar responsabilidades, seguindo o princípio de Responsabilidade Única destacado tanto por Martin Fowler quanto por Marco Tulio em seus livros. Essa separação torna o código mais organizado, facilitando sua manutenção e evolução futura.
 */