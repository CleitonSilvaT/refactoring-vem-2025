//class extracted from InitializerAST

package jparse.expr;

/**
 * Classe auxiliar para gerenciar operações do lado direito (rhs) da expressão.
 */
public class InitializerRHSHelper {
    private final ExpressionAST rhs;

    public InitializerRHSHelper(ExpressionAST rhs) {
        this.rhs = rhs;
    }

    // TRECHO REFATORADO
    public Type obterTipo() {
        return rhs.retrieveType();
    }

    // TRECHO REFATORADO
    public Type[] obterExcecoes() {
        return rhs.getExceptionTypes();
    }

    // TRECHO REFATORADO
    public Object obterValor() {
        return rhs.getValue();
    }

    // TRECHO REFATORADO
    public VarList obterVarList() {
        return rhs.getVarList();
    }

    // TRECHO REFATORADO
    public void completarAnalise() {
        rhs.parseComplete();
    }
}