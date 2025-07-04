package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class IndexAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST base;
    private ExpressionAST index;

    public IndexAST(final Token token) {
        super(token);
        setType(INDEX_OP);
    }

    public void parseComplete() {
        configurarBase(); // TRECHO REFATORADO
        configurarIndex(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO: Extração do método configurarBase()
    private void configurarBase() {
        base = (ExpressionAST) getFirstChild();
        base.parseComplete();
    }

    // TRECHO REFATORADO: Extração do método configurarIndex()
    private void configurarIndex() {
        index = (ExpressionAST) base.getNextSibling();
        index.parseComplete();
    }

    protected Type computeType() {
        return base.retrieveType().getComponentType();
    }

    protected Type[] computeExceptions() {
        return obterExceptionsCombinadas(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO: Extração do método obterExceptionsCombinadas()
    private Type[] obterExceptionsCombinadas() {
        return Type.mergeTypeLists(base.getExceptionTypes(), index.getExceptionTypes());
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        return new VarList(base.getVarList(), index.getVarList());
    }

    public ExpressionAST getBase() {
        return base;
    }

    public ExpressionAST getIndex() {
        return index;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - **Método `parseComplete()`**:
 *     - É possível extrair métodos menores para melhorar a legibilidade. Uma responsabilidade evidente é configurar separadamente o `base` e o `index`.
 *
 * - **Método `computeExceptions()`**:
 *     - Este método realiza uma operação específica (fundir listas de exceções) que pode ser extraída para destacar sua lógica.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * Baseando-se em Martin Fowler (2018) e Marco Tulio (Engenharia de Software Moderna), temos que:
 * - **Extração de Método (`configurarBase` e `configurarIndex`)**:
 *     - Utilizada para simplificar trechos de código complexos, aumentando a legibilidade e deixando explícitos os passos da lógica realizada. Isso também facilita futuras alterações e entendimento do código, alinhando-se com o princípio da responsabilidade única ao atribuir cada método a realizar apenas uma tarefa.
 *
 * - **Extração de Método (`obterExceptionsCombinadas`)**:
 *     - Aplicada especificamente para encapsular uma lógica bastante específica que combinava exceções do `base` e `index`. Isso diminui a complexidade percebida no método original (`computeExceptions()`), facilitando a manutenção e possíveis adaptações futuras.
 *
 * ### 4) Resumo das alterações realizadas:
 *
 * | Tipo de refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Método | 3 |
 * | Extração de Classe | 0 |
 * | **Total** | **3** |
 * - **Extração de Método**:
 *     - `configurarBase()`
 *     - `configurarIndex()`
 *     - `obterExceptionsCombinadas()`
 */