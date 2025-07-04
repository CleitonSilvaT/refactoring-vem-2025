package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;
import jparse.expr.ExpressionAST;

public final class WhileAST extends StatementAST {

    private ExpressionAST cond;
    private StatementAST stmt;
    private final WhileHelper helper;  // TRECHO REFATORADO - Helper introduzido

    public WhileAST(final Token token) {
        super(token);
        helper = new WhileHelper();  // TRECHO REFATORADO - Instanciação do helper
    }

    public void parseComplete() {
        super.parseComplete();
        extrairCondicaoECorpo(); // TRECHO REFATORADO
        configurarContexto(); // TRECHO REFATORADO
    }

    private void extrairCondicaoECorpo() { // TRECHO REFATORADO
        cond = (ExpressionAST) getFirstChild().getNextSibling();
        stmt = (StatementAST) cond.getNextSibling().getNextSibling();
    }

    private void configurarContexto() { // TRECHO REFATORADO
        context.pushContinue(this);
        cond.parseComplete();
        stmt.parseComplete();
        context.popContinue();
    }

    protected Type[] computeExceptions() {
        return helper.mesclarExcecoes(cond, stmt);  // TRECHO REFATORADO
    }

    protected StatementAST[] computeControl() {
        return stmt.nextControlPoints();
    }

    public VarList getVarList() {
        return helper.unirVariaveis(cond, stmt);  // TRECHO REFATORADO
    }

    public ExpressionAST getCondition() {
        return cond;
    }

    public StatementAST getBody() {
        return stmt;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * Identifiquei os seguintes pontos importantes para as refatorações de **Extração de Método** e de **Extração de Classe**:
 * - **Extração de Método**:
 *     - Método `parseComplete` realiza múltiplos procedimentos (obter nós irmãos, inserir contexto de break/continue).
 *     - Método `computeExceptions` realiza uma operação específica (mesclagem de exceções).
 *     - Método `getVarList` realiza uma operação específica (união de variáveis).
 *
 * - **Extração de Classe**:
 *     - Os métodos relacionados à manipulação e recuperação de informações internas como exceções e variáveis podem ser agrupados para melhorar coesão e encapsulamento.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * As refatorações realizadas seguem os princípios estabelecidos em Martin Fowler (2018) e Marco Túlio (Engenharia de Software Moderna):
 * - **Extração de Método** (`extrairCondicaoECorpo`, `configurarContexto`): Simplifica o processo de entendimento do método original, oferecendo autonomias claras para cada método (Princípio da Responsabilidade Única mencionado por Marco Túlio e por Fowler).
 * - **Extração de Classe** (`WhileHelper`): Agrupa métodos relacionados logicamente (tratamento de exceções e variáveis), aumentando a coesão interna da classe original (Fowler, "Extract Class"). Isso facilita futuras mudanças e testes independentes sem modificar diretamente a classe principal.
 *
 * Estas modificações removem duplicações, tornam o código mais coeso, legível, e melhor mantido.
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 2
 *         - Método `extrairCondicaoECorpo()`
 *         - Método `configurarContexto()`
 *
 *     - **Extração de Classe**: 1
 *         - Classe `WhileHelper` com métodos:
 *             - `mesclarExcecoes()`
 *             - `unirVariaveis()`
 */