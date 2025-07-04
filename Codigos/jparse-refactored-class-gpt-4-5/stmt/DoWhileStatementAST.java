package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class DoWhileAST extends StatementAST {

    private jparse.expr.ExpressionAST cond;
    private StatementAST stmt;

    public DoWhileAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        inicializarCondicaoEStatement(); // TRECHO REFATORADO
        configurarContextoContinue();    // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private void inicializarCondicaoEStatement() {
        stmt = (StatementAST) getFirstChild();
        cond = (jparse.expr.ExpressionAST) stmt.getNextSibling()
                .getNextSibling().getNextSibling();
    }

    // TRECHO REFATORADO
    private void configurarContextoContinue() {
        context.pushContinue(this);
        stmt.parseComplete();
        cond.parseComplete();
        context.popContinue();
    }

    protected Type[] computeExceptions() {
        return juntarTiposExcecoes(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private Type[] juntarTiposExcecoes() {
        return Type.mergeTypeLists(cond.getExceptionTypes(),
                stmt.getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return stmt.nextControlPoints();
    }

    public VarList getVarList() {
        return new VarList(cond.getVarList(), stmt.getVarList());
    }

    public jparse.expr.ExpressionAST getCondition() {
        return cond;
    }

    public StatementAST getBody() {
        return stmt;
    }
}

/**
 * ### 1) Oportunidades de Refatoração Encontradas:
 * Analisando o código fornecido, identifiquei as seguintes oportunidades relevantes para refatoração:
 * - Trechos com responsabilidade distinta podem ser isolados em novos métodos para melhorar a clareza e o entendimento. Por exemplo, o método **`parseComplete()` **contém operações de configuração do contexto e outras que poderiam ser melhor representadas em métodos separados.
 * - O método **`computeExceptions()` **realiza uma operação específica com tipos, que poderia ser um candidato direto para extração de método.
 * - A manipulação do contexto (continue/break), contida no método **`parseComplete()` **, pode caracterizar uma responsabilidade distinta que deve ser extraída em um método separado.
 * - Não foi identificada oportunidade relevante para Extrair Classe nesta classe devido ao seu tamanho reduzido e responsabilidade bem definida e coesa.
 *
 * ### 2) Classe Refatorada com os Devidos Comentários:
 *
 * ### 3) Justificativa das Refatorações:
 * - **Extração de Método**:
 *     - O método original `parseComplete()` tinha responsabilidades múltiplas reunidas num único método. Extraí dois métodos chamados `inicializarCondicaoEStatement()` e `configurarContextoContinue()`, cada um com responsabilidades específicas, seguindo o princípio de responsabilidade única (Martin Fowler, 2018). Isso torna o código mais legível e fácil de manter ao separar claramente as operações de inicialização e configuração do contexto.
 *     - A extração do método `juntarTiposExcecoes()` para encapsular a lógica de agregação dos tipos de exceções melhorou a clareza da intenção do código, de acordo com a recomendação de Marco Tulio no livro Engenharia de Software Moderna, no princípio de expressividade.
 *
 * - **Extração de Classe**:
 *     - Não houve necessidade ou justificativa relevante para extrair novas classes, pois a classe já apresentava alta coesão e responsabilidade única. Introduzir uma nova classe aqui não promoveria melhorias significativas claras, portanto, seguindo o princípio KISS ("Keep It Simple, Stupid", mencionado também por Marco Tulio), optei por não realizar essa refatoração.
 *
 * ### 4) Resumo das Alterações:
 * - **Quantidade total de refatorações realizadas**: 3 refatorações
 * - **Divisão por tipo de refatoração**:
 *     - Extração de Método: 3
 *         - `inicializarCondicaoEStatement()`
 *         - `configurarContextoContinue()`
 *         - `juntarTiposExcecoes()`
 *
 *     - Extração de Classe: 0 (não aplicável)
 */