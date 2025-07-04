package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class InitializerAST extends ExpressionAST {

    private ExpressionAST rhs;
    private InitializerRHSHelper rhsHelper; // TRECHO REFATORADO - nova classe auxiliar

    public InitializerAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        inicializarRHS(); // TRECHO REFATORADO - extração do método
        rhsHelper.completarAnalise(); // TRECHO REFATORADO - delegação para classe auxiliar
    }

    // TRECHO REFATORADO - método extraído para encapsular lógica de inicialização
    private void inicializarRHS() {
        rhs = (ExpressionAST)getFirstChild();
        rhsHelper = new InitializerRHSHelper(rhs);
    }

    protected Type computeType() {
        return rhsHelper.obterTipo(); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() {
        return rhsHelper.obterExcecoes(); // TRECHO REFATORADO
    }

    protected Object computeValue() {
        return rhsHelper.obterValor(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return rhsHelper.obterVarList(); // TRECHO REFATORADO
    }

    public ExpressionAST getRight() {
        return rhs;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - O método `parseComplete()` executa operações com pouca coesão, indicando oportunidade para Extrair Método desta operação.
 * - Os métodos `computeType()`, `computeExceptions()` e `computeValue()` delegam diretamente para a mesma instância (`rhs`). Para reduzir o acoplamento interno, as operações com a instância poderiam ser extraídas para uma classe auxiliar (Extração de Classe), tornando o código mais limpo e coeso.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * - **Extração de Classe** (`InitializerRHSHelper`): Aplicando o princípio estabelecido por Martin Fowler (2018), esta refatoração reduz e encapsula operações secundárias em uma classe mais especializada, seguindo o princípio da responsabilidade única (SRP), deixando a classe principal mais clara, focada e coesa.
 * - **Extração de Métodos** (método `inicializarRHS()`): Conforme mencionado por Marco Tulio, retirar operações específicas para métodos específicos aumenta significativamente a legibilidade e manutenção do código, tornando claras as intenções do desenvolvedor, promovendo melhor entendimento e menos riscos em futuras modificações.
 *
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas:** 6
 *     - **Extração de Método:** 1
 *     - **Extração de Classe:** 1 (que engloba 5 métodos delegados)
 */