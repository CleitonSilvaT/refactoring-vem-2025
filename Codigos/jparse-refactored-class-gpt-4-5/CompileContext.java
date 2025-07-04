package jparse;

import jparse.stmt.StatementAST;
import java.util.LinkedList;

public final class CompileContext {

    public boolean isField = true;
    public StatementAST nextStmt;
    public ModifierAST mods;
    public jparse.expr.TypeAST type;
    public boolean negative;

    // TRECHO REFATORADO: Extraiu classe para gerenciar targets
    private final TargetStack targetStack = new TargetStack();

    public void pushBreak(final JavaAST target) {
        // TRECHO REFATORADO: Delegação para método específico
        targetStack.pushBreak(target);
    }

    public void popBreak() {
        // TRECHO REFATORADO: Delegação para método específico
        targetStack.popBreak();
    }

    public StatementAST breakTarget() {
        // TRECHO REFATORADO: Delegação para método específico
        return targetStack.getBreakTarget();
    }

    public void pushContinue(final JavaAST target) {
        // TRECHO REFATORADO: Delegação para método específico
        targetStack.pushContinue(target);
    }

    public void popContinue() {
        // TRECHO REFATORADO: Delegação para método específico
        targetStack.popContinue();
    }

    public StatementAST continueTarget() {
        // TRECHO REFATORADO: Delegação para método específico
        return targetStack.getContinueTarget();
    }
}


/**
 * ## 1) Oportunidades de refatoração encontradas
 * Considerando os conceitos apresentados por Fowler (2018) que recomendam manter classes com responsabilidades e abstrações claramente definidas e os princípios apresentados por Marco Tulio sobre remoção de duplicações e aumento da legibilidade, os seguintes problemas foram identificados:
 * - **Duplicação em gerenciamento dos alvos de statements:** A lógica das pilhas para `break` e `continue` aparece duplicada e poderia ser extraída em uma classe específica de gerenciamento de pilha.
 * - **Baixa coesão da classe CompileContext:** A classe está acumulando responsabilidades demais, especialmente a gestão dos statements (`break` e `continue`), que poderiam estar melhor organizadas em uma classe especializada.
 * - **Falta de métodos bem definidos para encapsular o comportamento relacionado às pilhas:** Métodos isolados poderiam ser agrupados e melhor organizados em métodos mais coesos.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * - **Extração de Classe (`TargetStack`):**
 *     - Motivação: A classe original tinha baixa coesão, realizando tarefas diversas (gerenciamento de contexto de compilação e manipulação de pilhas internas). Extrair essas responsabilidades aumenta a coesão e melhora a legibilidade.
 *     - Benefício: Melhor encapsulamento e clareza na organização interna, seguindo as recomedações de Fowler (Single Responsibility Principle).
 *
 * - **Extração de Métodos (delegação dos métodos de pilha):**
 *     - Motivação: Evitar duplicação explícita do comportamento das pilhas dentro da classe `CompileContext`, e delegar essas operações diretamente à classe recém-criada.
 *     - Benefício: Facilita manutenção futura e torna o código mais declarativo, explicando claramente qual classe processa a lógica interna.
 *
 * ## 4) Resumo das alterações realizadas
 * - **Quantidade total de refatorações realizadas:** 2
 *     - **Extração de Classe:** 1 (Classe `TargetStack`)
 *     - **Extração de Método (delegação de operações para a nova classe):** 1 (múltiplos métodos delegados considerados juntos como uma extração de responsabilidade única)
 *
 */