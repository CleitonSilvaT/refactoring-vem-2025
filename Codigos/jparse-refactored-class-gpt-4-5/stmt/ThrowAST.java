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
        definirThrowable(); // TRECHO REFATORADO - método extraído
    }

    // TRECHO REFATORADO - novo método criado para melhorar legibilidade e separação de responsabilidades
    private void definirThrowable() {
        throwable = extrairPrimeiroFilho();
        throwable.parseComplete();
    }

    // TRECHO REFATORADO - novo método criado para extrair e definir primeiro filho
    private jparse.expr.ExpressionAST extrairPrimeiroFilho() {
        return (jparse.expr.ExpressionAST) getFirstChild();
    }

    protected Type[] computeExceptions() {
        return criarTiposExcecao(); // TRECHO REFATORADO - método extraído para simplificar criação e retorno
    }

    // TRECHO REFATORADO - novo método criado para simplificação da criação de exceptions
    private Type[] criarTiposExcecao() {
        return new Type[]{throwable.retrieveType()};
    }

    protected StatementAST[] computeControl() {
        return criarControleStatement(); // TRECHO REFATORADO - método extraído para simplificar criação e retorno
    }

    // TRECHO REFATORADO - novo método criado para simplificação da criação de controle
    private StatementAST[] criarControleStatement() {
        return new StatementAST[]{nonlocal};
    }

    public VarList getVarList() {
        return throwable.getVarList();
    }

    public jparse.expr.ExpressionAST getThrowable() {
        return throwable;
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas:
 * Após remover comentários iniciais, identifiquei algumas oportunidades de melhoria:
 * - Método `parseComplete()` contém múltiplas responsabilidades, tanto ao gerenciar a extração de um elemento filho quanto finalizar o processo de parsing. Essa situação é ideal para **Extração de Método**, dividindo melhor as responsabilidades do método e aumentando sua legibilidade.
 * - Os métodos `computeExceptions()` e `computeControl()` são simplificados, mas ainda assim, têm criação diretamente embutida em seu fluxo, exigindo tanto construção quanto retorno de objeto no mesmo ponto. Isso também pode ser melhorado utilizando métodos auxiliares (**Extração de Método**) para atribuir responsabilidades mais distintas e explícitas ao código.
 *
 * # 2) Classe Refatorada com os devidos comentários:
 *
 * # 3) Justificativa das refatorações:
 * - **Extração de método (`definirThrowable` e `extrairPrimeiroFilho`) no método `parseComplete()`**:
 *     - Segundo Martin Fowler, métodos devem ser pequenos e focados em uma única atividade. Antes, o método possuía mais de uma responsabilidade (extração do filho e parsing). Com essa alteração, a responsabilidade está claramente definida para cada método, facilitando não apenas a manutenção, mas também leitura e entendimento.
 *
 * - **Extração dos métodos `criarTiposExcecao()` e `criarControleStatement()`**:
 *     - Criação e retorno direto de objetos dificultam a flexibilização e clareza do código. Ao extrair estes fluxos em métodos próprios, melhoramos e explicitamos a intenção do código, dando espaço para expansões ou alterações futuras com facilidade.
 *     - De acordo com Marco Tulio, enfatiza-se o princípio da responsabilidade única, onde cada método claramente desempenha uma só tarefa, tornando o código consideravelmente mais limpo e fácil de manter.
 *
 * # 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 4
 * - **Divisão por tipo**:
 *     - Extração de métodos: 4
 *     - Extração de classes: 0 (não houve necessidade identificada para extração de classe nessa situação específica)
 */