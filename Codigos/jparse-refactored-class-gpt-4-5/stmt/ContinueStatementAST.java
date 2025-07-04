package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.IdentifierAST;

public final class ContinueAST extends StatementAST {

    private IdentifierAST label;
    private GerenciadorLabelContinue gerenciadorLabel; // TRECHO REFATORADO

    public ContinueAST(final Token token) {
        super(token);
        gerenciadorLabel = new GerenciadorLabelContinue(this); // TRECHO REFATORADO
    }

    public void parseComplete() {
        super.parseComplete();
        extrairLabelSePresente(getFirstChild()); // TRECHO REFATORADO
        gerenciadorLabel.definirControle(label); // TRECHO REFATORADO
    }

    private void extrairLabelSePresente(AST maybeLabel) { // TRECHO REFATORADO
        if (maybeLabel instanceof IdentifierAST) {
            label = (IdentifierAST) maybeLabel;
        }
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected StatementAST[] computeControl() {
        return gerenciadorLabel.obterControle(label); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return new VarList();
    }

    public IdentifierAST getLabel() {
        return label;
    }
}

/**
 * ### 1. Oportunidades de Refatoração Encontradas
 * - **Método _parseComplete_** contém lógica que pode ser extraída em métodos separados para melhorar a legibilidade.
 * - **Método _computeControl_** contém uma responsabilidade única que pode ser melhor destacada através da criação de um método específico para obter o destino do rótulo.
 * - Percebe-se uma lógica que gerencia o rótulo presente ou não, que pode justificar a criação de uma classe auxiliar dedicada ao gerenciamento do rótulo, favorecendo o princípio de responsabilidade única.
 *
 * ### 2. Classe Refatorada com os Devidos Comentários em Português
 *
 * ### 3. Justificativa das Refatorações
 * **Extração de Métodos:**
 * - **`extrairLabelSePresente()`**: extraído do método `parseComplete()` para tornar claro seu propósito e melhorar a legibilidade, destacando a verificação e atribuição do label.
 *     - Conceito de Fowler sendo aplicado aqui: "Um método deve fazer exatamente uma tarefa e a atuação do método deve estar extremamente clara" _(Fowler, Refactoring, 2018)_.
 *
 * **Extração de Classe:**
 * - **Classe `GerenciadorLabelContinue`**: extraída da lógica de gerenciamento relacionada ao tratamento do label e definição do controle. Esta classe agora trata exclusivamente da responsabilidade sobre o comportamento relacionado a esse gerenciamento, verificando quando o label está presente ou nulo, melhorando o princípio da responsabilidade única _(Marco Tulio — Engenharia de Software Moderna)_.
 *
 * Essas técnicas tornam o código mais compreensível, facilitando futuras manutenções, testes e evoluções no comportamento da classe sem influenciar diretamente na lógica externa.
 * ### 4. Resumo das Alterações
 * - **Quantidade total de refatorações**: 5
 * - **Divisão por tipo**:
 *     - Extração de Método: 2
 *         - `extrairLabelSePresente(AST maybeLabel)`
 *         - Métodos na classe auxiliar (`definirControle`, `obterControle`)
 *
 *     - Extração de Classe: 1
 *         - Classe `GerenciadorLabelContinue`
 */