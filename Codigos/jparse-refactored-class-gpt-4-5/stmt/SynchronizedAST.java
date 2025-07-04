package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class SynchronizedAST extends StatementAST {

    private jparse.expr.ExpressionAST lock;
    private CompoundAST body;

    public SynchronizedAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        definirNoLock(); // TRECHO REFATORADO
        definirNoBody(); // TRECHO REFATORADO
        completarParseDosNos(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - novo método para definir o nó lock
    private void definirNoLock() {
        lock = (jparse.expr.ExpressionAST) getFirstChild().getNextSibling();
    }

    // TRECHO REFATORADO - novo método para definir o nó body
    private void definirNoBody() {
        body = (CompoundAST) lock.getNextSibling().getNextSibling();
    }

    // TRECHO REFATORADO - novo método para completar o parse dos nós
    private void completarParseDosNos() {
        lock.parseComplete();
        body.parseComplete();
    }

    protected Type[] computeExceptions() {
        return combinarTiposDeExcecao(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - nova extração para a combinação de exceções
    private Type[] combinarTiposDeExcecao() {
        return Type.mergeTypeLists(lock.getExceptionTypes(), body.getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return obterPontosControleDoCorpo(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - extração do método de controle
    private StatementAST[] obterPontosControleDoCorpo() {
        return body.nextControlPoints();
    }

    public VarList getVarList() {
        return criarVarList(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - método específico para criar VarList
    private VarList criarVarList() {
        return new VarList(lock.getVarList(), body.getVarList());
    }

    public jparse.expr.ExpressionAST getLock() {
        return lock;
    }

    public StatementAST getBody() {
        return body;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - O método `parseComplete()` realiza múltiplas operações claramente distintas (navegação entre nós, extração e parse dos elementos filhos).
 * - Cada operação no método possui uma responsabilidade específica e pode ser encapsulada em métodos separados (Extração de Método).
 * - A criação do objeto `VarList` e a obtenção dos pontos de controle também podem ser extraídas em métodos específicos para aumentar legibilidade e encapsulamento.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * - **Extração de Método** (`definirNoLock`, `definirNoBody` e `completarParseDosNos`): O método `parseComplete()` originalmente realizava múltiplas operações que dificultavam a legibilidade e manutenção. Segundo Fowler (2018), métodos menores e com responsabilidades bem definidas facilitam a compreensão e simplificam futuras modificações.
 * - **Extração de Método** (`combinarTiposDeExcecao`, `obterPontosControleDoCorpo` e `criarVarList`): Esses métodos encapsulam operações específicas, simplificam seu entendimento à primeira vista e fazem o código principal mais declarativo e intuitivo.
 * - A **Extração de Classe** não foi empregada nesta situação pois a classe existente já possui uma responsabilidade coesa e bem definida.
 *
 * ### 4) Resumo das alterações:
 * - **Refatorações totais realizadas**: 6 alterações.
 *     - Extração de Método: 6 (não houve necessidade justificada para Extração de Classe).
 *     - Extração de Classe: 0.
 */