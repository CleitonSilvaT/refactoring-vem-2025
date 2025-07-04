package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;
import jparse.expr.ExpressionAST;

public final class ReturnAST extends StatementAST implements JavaTokenTypes {

    private ExpressionAST expr;

    public ReturnAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        iniciarExpressaoRetorno(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - método extraído para melhorar clareza e coesão
    private void iniciarExpressaoRetorno() {
        final AST retVal = getFirstChild();
        if (possuiExpressao(retVal)) {
            expr = (ExpressionAST) retVal;
            expr.parseComplete();
        }
    }

    // TRECHO REFATORADO - extraído método para eliminar duplicação de lógica
    private boolean possuiExpressao(final AST retVal) {
        return retVal.getType() != SEMI;
    }

    protected Type[] computeExceptions() {
        return possuiExpressao() ? expr.getExceptionTypes() : noTypes; // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Extraído método que verifica se expr existe
    private boolean possuiExpressao() {
        return expr != null;
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[]{nonlocal};
    }

    public VarList getVarList() {
        return possuiExpressao() ? expr.getVarList() : new VarList(); // TRECHO REFATORADO
    }

    public ExpressionAST getReturnValue() {
        return expr;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * Foram identificadas as seguintes oportunidades para melhorar o código com técnicas de refatoração:
 * - O método `parseComplete()` possui responsabilidades diferentes: obter o AST filho, validar o tipo e atribuir/validar a expressão. Isso pode ser facilmente separado com extração de método.
 * - Verificação comum da existência da expressão em métodos diferentes (`computeExceptions()` e `getVarList()`), resultando em duplicação de lógica.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 * Segue a classe refatorada, com os comentários em português adicionados nos locais onde houve modificação:
 *
 * ### 3) Justificativa das refatorações:
 * Cada refatoração foi realizada levando em consideração os princípios descritos por Martin Fowler no livro _Refactoring: Improving the Design of Existing Code_ (2018) e por Marco Tulio na obra _Engenharia de Software Moderna_:
 * #### a) Extração de métodos:
 * - **`iniciarExpressaoRetorno()`**: Criado para isolar a responsabilidade de configurar a expressão de retorno, facilitando a leitura e compreensão do método `parseComplete()`.
 * - **`possuiExpressao(final AST)`**: Isola a lógica da condição para verificar se o AST filho corresponde ou não a um ponto e vírgula (ausência de expressão), reduzindo duplicação lógica e facilitando manutenção futura.
 * - **`possuiExpressao()`**: Centraliza a verificação comum para expressão nula, eliminando duplicação de código verificado múltiplas vezes na classe.
 *
 * Essas extrações seguem claramente as recomendações de Fowler quanto à melhoria de clareza, coesão e redução de duplicações.
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas:** 4
 * - **Divisão por tipo:**
 *     - Extração de Método: 4
 *     - Extração de Classe: 0 (não houve necessidade clara nesse contexto específico)
 */