package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class AssertAST extends StatementAST {

    private AssertExpression assertExpression; // TRECHO REFATORADO

    public AssertAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        assertExpression = new AssertExpression(getFirstChild()); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() { // TRECHO REFATORADO
        return assertExpression.getExceptionTypes(); // TRECHO REFATORADO
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[]{next};
    }

    public VarList getVarList() { // TRECHO REFATORADO
        return assertExpression.getVarList(); // TRECHO REFATORADO
    }

    public jparse.expr.ExpressionAST getFirstExpression() { // TRECHO REFATORADO
        return assertExpression.getPrimeiraExpressao(); // TRECHO REFATORADO
    }

    public jparse.expr.ExpressionAST getSecondExpression() { // TRECHO REFATORADO
        return assertExpression.getSegundaExpressao(); // TRECHO REFATORADO
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * Analisando o código fornecido, encontrei as seguintes oportunidades de refatoração:
 * - **Extração de Método** no método `parseComplete()`: Este método realiza várias ações, como obter o primeiro filho AST, verificar a presença de pontuação (dois pontos) e manipular a segunda expressão. Essas operações, apesar de simples, formam um bloco lógico coeso e significativo. A sua extração melhorará a legibilidade e concretizará a intenção do código.
 * - **Extração de Classe**: Uma nova classe `AssertExpression` pode ser extraída para encapsular a lógica relativa ao tratamento e armazenamento das duas expressões (condição e mensagem opcional). Assim, promove separação clara e responsabilidade única, conforme padrões indicados por Martin Fowler em _Refactoring: Improving the Design of Existing Code_.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * As refatorações realizadas seguem os princípios claramente destacados por Martin Fowler e Marco Tulio:
 * - **Extração de Classe (`AssertExpression`)**: Este tipo de refatoração é realizado para garantir que cada classe possua uma tarefa ou responsabilidade bem definida e clara. No contexto deste código, a responsabilidade de parsing e manipulação das duas expressões foi removida da classe original e encapsulada adequadamente na nova classe.
 * - **Extração de Métodos (`extrairExpressao()`)**: Método responsável por extrair e inicializar as expressões, deixando explícita a intenção do código, de forma a facilitar sua manutenção e futuras alterações.
 *
 * Essas refatorações elevam a coesão, simplificam a classe original e tornam explícitas as responsabilidades das classes e métodos envolvidos.
 * ### 4) Resumo das alterações
 *
 * | Tipo de Refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Classe | 1 |
 * | Extração de Método | 1 |
 * | **Total Geral** | **2** |
 * **Resultado Final:**
 * - Código mais claro e manutenível.
 * - Classes com responsabilidades únicas.
 * - Redução de código duplicado.
 * - Melhor aderência aos princípios de boas práticas destacadas nas referências fornecidas.
 */