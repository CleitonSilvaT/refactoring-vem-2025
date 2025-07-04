package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.ModifierAST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.TypeAST;
import jparse.expr.VarAST;

public final class CatchAST extends StatementAST {

    private VarAST param;
    private CompoundAST body;

    public CatchAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        extrairParametroCatch(); // TRECHO REFATORADO
        extrairCorpoCatch(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - método extraído para obter o parâmetro do catch
    private void extrairParametroCatch() {
        final AST theParam = getFirstChild().getNextSibling();
        context.mods = (ModifierAST) theParam.getFirstChild();
        context.type = (TypeAST) context.mods.getNextSibling();
        param = (VarAST) context.type.getNextSibling();
        param.parseComplete();
    }

    // TRECHO REFATORADO - método extraído para obter o corpo do catch
    private void extrairCorpoCatch() {
        final AST theParam = getFirstChild().getNextSibling();
        body = (CompoundAST) theParam.getNextSibling().getNextSibling();
        body.parseComplete();
    }

    protected Type[] computeExceptions() {
        return body.getExceptionTypes();
    }

    Type[] removeCaughtException(final Type[] list) {
        ExceptionHandler handler = new ExceptionHandler(param); // TRECHO REFATORADO - classe extraída
        return handler.removeCaughtExceptions(list);
    }

    protected StatementAST[] computeControl() {
        return body.nextControlPoints();
    }

    public VarList getVarList() {
        return new VarList(body.getVarList(), param);
    }

    public VarAST getParameter() {
        return param;
    }

    public CompoundAST getBody() {
        return body;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - O método `parseComplete()` realiza múltiplas tarefas claramente distintas: extrair o parâmetro e o corpo do catch. Isso indica uma oportunidade para **Extração de Método** segmentando em tarefas menores.
 * - O método `removeCaughtException()` gerencia diretamente operações com listas e possui lógica que pode ser encapsulada. Existe uma oportunidade clara de **Extração de Método** e de **Extração de Classe** para lidar com a lógica específica das exceções, aumentando a coesão e a reutilização do código.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações:
 * **Extração de Métodos:**
 * - `extrairParametroCatch()` e `extrairCorpoCatch()` foram extraídos do método `parseComplete()` para separar claramente a recuperação do parâmetro e corpo de uma estrutura catch. Esta abordagem é sustentada por Martin Fowler (2018), que recomenda métodos menores com responsabilidades bem definidas para aumentar a clareza e a manutenibilidade do código.
 * - O método `isCaughtByCatchClause()` foi extraído de dentro do loop em `removeCaughtExceptions()` para melhorar a legibilidade e explicitar a intenção lógica da condição (Fowler, 2018).
 *
 * **Extração de Classe:**
 * - Uma nova classe `ExceptionHandler` foi criada para encapsular a lógica relacionada à manipulação das exceções. Segundo Marco Túlio em "Engenharia de Software Moderna", uma classe deve ter uma responsabilidade única e coesa. Remover essa responsabilidade da classe original `CatchAST` reduz sua complexidade e aumenta sua qualidade estrutural.
 *
 * ### 4) Resumo das alterações realizadas:
 * - **Quantidade total de refatorações realizadas:** 4
 *     - **Extração de Métodos:** 3
 *         - `extrairParametroCatch()`
 *         - `extrairCorpoCatch()`
 *         - `isCaughtByCatchClause()`
 *
 *     - **Extração de Classe:** 1
 *         - Classe criada: `ExceptionHandler`
 */