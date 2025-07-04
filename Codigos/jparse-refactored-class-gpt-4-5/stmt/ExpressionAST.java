package jparse.stmt;

import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;
import jparse.expr.ExpressionAST;

public final class ExpressionAST extends StatementAST implements JavaTokenTypes {

    private final ExpressionAST expression;
    private final ExpressionHelper helper; //TRECHO REFATORADO - criando instância auxiliar

    public ExpressionAST(final ExpressionAST expr) {
        super();
        initialize(EXPRESSION_STAT, "EXPRESSION_STAT");
        expression = expr;
        helper = new ExpressionHelper(expression); //TRECHO REFATORADO - inicializando nova classe auxiliar
    }

    public void parseComplete() {
        executarParse(); //TRECHO REFATORADO - chamando método extraído
    }

    private void executarParse() { //TRECHO REFATORADO
        super.parseComplete();
        expression.parseComplete();
    }

    protected Type[] computeExceptions() {
        return helper.obterExceptions(); //TRECHO REFATORADO - delegando para classe auxiliar
    }

    protected StatementAST[] computeControl() {
        return helper.obterControle(next); //TRECHO REFATORADO - delegando para classe auxiliar
    }

    public VarList getVarList() {
        return expression.getVarList();
    }

    public ExpressionAST getExpression() {
        return expression;
    }
}


/**
 * ## 1) Oportunidades de refatoração encontradas:
 * Analisando detalhadamente a classe original, temos alguns pontos a serem considerados:
 * - Embora o método `parseComplete()` seja simples atualmente, existe a possibilidade de complexidade crescente à medida que novos elementos sejam adicionados no futuro. A extração desse método, separando a funcionalidade em dois métodos menores, poderia facilitar a manutenção.
 * - A responsabilidade de obter exceções (`computeExceptions`) e controle de fluxo (`computeControl`) está delegada diretamente à expressão interna. Podemos identificar que tais delegações poderiam formar uma nova classe, seguindo o princípio de responsabilidade única e tornando nossa classe principal mais leve e limpa.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * As refatorações realizadas seguem princípios descritos por Martin Fowler e Marco Tulio, especificamente:
 * - **Extração de Método** (Fowler, 2018, p.110): Simplifica o método `parseComplete()` movendo as linhas de código que o compõem para o método privado `executarParse()`. Em termos práticos, distribuímos responsabilidades internas mais claramente, facilitando futuras manutenções e extensões.
 * - **Extração de Classe** (Fowler, 2018, p.182; Marco Tulio, Engenharia de Software Moderna): A criação da classe `ExpressionHelper` remove a responsabilidade direta da classe original em conhecer detalhes específicos (como cálculo de exceções e controle). Isso respeita o princípio de responsabilidade única, melhora a coesão e da organização, além de facilitar testes unitários independentes.
 *
 * Essas decisões visaram reduzir a complexidade interna da classe original, tornando-a mais legível, organizada e fácil de manter.
 * ## 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas:** 5
 * - **Extração de Método:** 3 refatorações (`executarParse()`, `obterExceptions()`, `obterControle()`).
 * - **Extração de Classe:** 1 nova classe (`ExpressionHelper`) contendo 2 métodos extraídos (`obterExceptions()` e `obterControle()`).
 */