package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class AssignAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST lhs;
    private ExpressionAST rhs;

    public AssignAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        atribuirFilhos(); // TRECHO REFATORADO - Extração de Método para atribuições dos filhos (lhs e rhs)
        completarParseDosFilhos(); // TRECHO REFATORADO - Extração de Método para parse dos filhos
    }

    private void atribuirFilhos() { // TRECHO REFATORADO - Novo método privado extraído
        lhs = (ExpressionAST)getFirstChild();
        rhs = (ExpressionAST)lhs.getNextSibling();
    }

    private void completarParseDosFilhos() { // TRECHO REFATORADO - Novo método privado extraído
        lhs.parseComplete();
        rhs.parseComplete();
    }

    protected Type computeType() {
        final Type type = lhs.retrieveType();
        if (type == Type.stringType && getType() == PLUS_ASSIGN)
            setType(CONCAT_ASSIGN);
        return type;
    }

    protected Type[] computeExceptions() {
        return rhs.getExceptionTypes();
    }

    protected Object computeValue() {
        return rhs.getValue();
    }

    public VarList getVarList() {
        VarListHandler handler = new VarListHandler(lhs, rhs); // TRECHO REFATORADO - Delegação para classe extraída
        return handler.getVarListCombinado(); // TRECHO REFATORADO - Chamada ao método da classe extraída
    }

    public ExpressionAST getLeft() {
        return lhs;
    }

    public ExpressionAST getRight() {
        return rhs;
    }
}

/**
 * ## 1. Oportunidades de refatoração encontradas:
 * As seguintes oportunidades foram identificadas no código apresentado:
 * - **Extração de Métodos** no método `parseComplete()`, separando claramente as atribuições e chamadas para aumento de clareza.
 * - **Extração de Classe** ao perceber que as operações relacionadas à manipulação das variáveis (método `getVarList`) têm comportamento claramente definido e distinto, o que permite uma melhor organização se encapsuladas em uma classe separada responsável exclusivamente por esta operação.
 *
 * ## 2. Classe refatorada com os devidos comentários:
 *
 * ## 3. Justificativa das refatorações:
 * - **Extração de Classe (VarListHandler)**: Segundo Fowler (2018), trata-se de uma aplicação clássica do princípio de Responsabilidade Única. A classe original possuía comportamentos específicos relacionados às variáveis que foram claramente separados nesta nova classe. Isso simplifica o entendimento e torna cada classe claramente identificável no sistema.
 * - **Extração de Métodos (`atribuirFilhos()` e `completarParseDosFilhos()`)** A extração dos métodos dentro do `parseComplete()` permite melhorar a legibilidade e clareza, eliminando complexidade escondida e atribuindo um nome claro e descritivo ao comportamento, conforme recomenda a literatura apresentada por Marco Túlio e Fowler, tornando o código autoexplicativo.
 *
 * ## 4. Resumo das alterações:
 *
 * | Tipo de Refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Classe | 1 |
 * | Extração de Método | 2 |
 * **Quantidade total de refatorações realizadas:** 3 (uma extração de classe e duas extrações de método).
 */