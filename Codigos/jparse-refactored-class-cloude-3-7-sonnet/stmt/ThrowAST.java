package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class ThrowAST extends StatementAST {

    private jparse.expr.ExpressionAST throwable;

    public ThrowAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extraído método para inicializar e processar o objeto throwable
        initializeAndProcessThrowable();
    }

    // TRECHO REFATORADO: Novo método extraído para encapsular a inicialização do throwable
    private void initializeAndProcessThrowable() {
        throwable = (jparse.expr.ExpressionAST)getFirstChild();
        throwable.parseComplete();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Utiliza o método extraído para obter o tipo da exceção
        return new Type[] { getThrowableType() };
    }

    // TRECHO REFATORADO: Novo método extraído para obter o tipo do throwable
    private Type getThrowableType() {
        return throwable.retrieveType();
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[] { nonlocal };
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Utiliza o método extraído para obter a lista de variáveis
        return getThrowableVarList();
    }

    // TRECHO REFATORADO: Novo método extraído para encapsular acesso à lista de variáveis
    private VarList getThrowableVarList() {
        return throwable.getVarList();
    }

    public jparse.expr.ExpressionAST getThrowable() {
        return throwable;
    }
}

/**
 * # Refatoração da Classe ThrowAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe `ThrowAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Extração de método** para o processamento do objeto throwable no método `parseComplete()` - O código está misturando inicialização e processamento.
 * 2. **Extração de método** para simplificar o acesso à manipulação de objetos throwable - Os métodos `computeExceptions()` e `getVarList()` realizam operações simples que podem ser mais bem encapsuladas.
 * 3. A classe em geral não apresenta estrutura complexa que justifique a extração de classes completas, sendo uma classe específica de objetivo único.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `initializeAndProcessThrowable()`**:
 *     - Conforme Martin Fowler sugere, extrair trechos de código que representam uma unidade lógica em métodos separados melhora a legibilidade e manutenção do código. Este método encapsula duas operações relacionadas: a inicialização e o processamento do objeto throwable.
 *
 * 2. **Extração do método `getThrowableType()`**:
 *     - Este método foi extraído para encapsular a lógica de obtenção do tipo da exceção. A extração de métodos que encapsulam ações específicas segue o princípio de responsabilidade única mencionado por Marco Tulio.
 *     - Além disso, facilita a manutenção futura caso a lógica de obtenção do tipo precise ser alterada.
 *
 * 3. **Extração do método `getThrowableVarList()`**:
 *     - Similar ao caso anterior, este método isola a lógica de acesso à lista de variáveis, tornando o código mais expressivo e mantendo a coesão do método principal.
 *     - De acordo com Fowler, métodos com nomes descritivos que indicam sua função aumentam a compreensibilidade do código.
 *
 * A decisão de não realizar uma Extração de Classe se deve ao fato de que a classe `ThrowAST` já possui um objetivo único e bem definido, representando apenas um nó AST para uma instrução de lançamento de exceção. Não há grupos de dados ou métodos relacionados que justificariam serem movidos para uma nova classe.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 3 (initializeAndProcessThrowable, getThrowableType, getThrowableVarList)
 *     - **Extração de Classe**: 0
 *
 * As refatorações realizadas melhoraram a organização e legibilidade do código sem alterar seu comportamento original. O código está agora mais modular e com maior coesão, facilitando futuras manutenções e compreensão do fluxo de execução.
 */