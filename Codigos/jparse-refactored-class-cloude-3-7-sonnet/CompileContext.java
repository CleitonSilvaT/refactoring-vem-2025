package jparse;

import java.util.LinkedList;
import jparse.stmt.StatementAST;

public final class CompileContext {

    public boolean isField = true;
    public StatementAST nextStmt;
    public ModifierAST mods;
    public jparse.expr.TypeAST type;
    public boolean negative;
    
    // TRECHO REFATORADO: Extraído classe para gerenciar os alvos (targets)
    private final TargetManager targetManager;
    
    public CompileContext() {
        // TRECHO REFATORADO: Inicialização da nova classe extraída
        this.targetManager = new TargetManager();
    }

    /**
     * Push a new break target onto the stack
     *
     * @param target the new break target
     */
    public void pushBreak(final JavaAST target) {
        // TRECHO REFATORADO: Delegando para a classe extraída
        targetManager.pushBreak(target);
    }

    /**
     * Pop a break target off of the stack
     */
    public void popBreak() {
        // TRECHO REFATORADO: Delegando para a classe extraída
        targetManager.popBreak();
    }

    /**
     * Get the current break target
     *
     * @return the current break target
     */
    public StatementAST breakTarget() {
        // TRECHO REFATORADO: Delegando para a classe extraída
        return targetManager.getBreakTarget();
    }

    /**
     * Push a new continue (and break) target onto the stack
     *
     * @param target the new continue target
     */
    public void pushContinue(final JavaAST target) {
        // TRECHO REFATORADO: Delegando para a classe extraída
        targetManager.pushContinue(target);
    }

    /**
     * Pop a continue (and break) target off of the stack
     */
    public void popContinue() {
        // TRECHO REFATORADO: Delegando para a classe extraída
        targetManager.popContinue();
    }

    /**
     * Get the current continue target
     *
     * @return the current continue target
     */
    public StatementAST continueTarget() {
        // TRECHO REFATORADO: Delegando para a classe extraída
        return targetManager.getContinueTarget();
    }
    
    // TRECHO REFATORADO: Nova classe extraída para gerenciar os alvos de break e continue
    private static class TargetManager {
        private final LinkedList breakTargets;
        private final LinkedList continueTargets;
        
        public TargetManager() {
            this.breakTargets = new LinkedList();
            this.continueTargets = new LinkedList();
        }
        
        // TRECHO REFATORADO: Método extraído para encapsular a operação de push
        public void pushBreak(final JavaAST target) {
            breakTargets.addFirst(target);
        }
        
        public void popBreak() {
            breakTargets.removeFirst();
        }
        
        public StatementAST getBreakTarget() {
            return (StatementAST)breakTargets.getFirst();
        }
        
        // TRECHO REFATORADO: Método extraído que encapsula as duas operações de push
        public void pushContinue(final JavaAST target) {
            pushBreak(target);
            continueTargets.addFirst(target);
        }
        
        // TRECHO REFATORADO: Método extraído que encapsula as duas operações de pop
        public void popContinue() {
            breakTargets.removeFirst();
            continueTargets.removeFirst();
        }
        
        public StatementAST getContinueTarget() {
            return (StatementAST)continueTargets.getFirst();
        }
    }
}

/**
 * # Análise e Refatoração da classe CompileContext
 * Seguindo suas instruções, vou realizar a refatoração focada em Extração de Classes e Extração de Métodos para a classe CompileContext. Primeiro, removerei os comentários e analisarei a estrutura atual.
 * ## 1) Oportunidades de refatoração encontradas
 * - **Responsabilidade dividida**: A classe gerencia dois tipos de alvos (break e continue) com comportamentos similares mas com funções separadas.
 * - **Duplicação de código**: Os métodos `pushContinue` e `popContinue` contêm lógica duplicada quando comparados com `pushBreak` e `popBreak`.
 * - **Baixa coesão**: A classe combina gerenciamento de informações de contexto gerais de compilação (isField, nextStmt, etc.) com gerenciamento específico de alvos para declarações break e continue.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe (TargetManager)**:
 *     - Seguindo o princípio da Responsabilidade Única de Martin Fowler, extraí a lógica relacionada ao gerenciamento de alvos para uma classe separada. Antes, a classe CompileContext tinha múltiplas responsabilidades: gerenciar informações gerais de compilação e gerenciar alvos de break/continue.
 *     - Esta refatoração melhora a coesão, pois agora cada classe tem um conjunto bem definido de responsabilidades.
 *
 * 2. **Extração de Método (métodos na classe TargetManager)**:
 *     - Os métodos `pushBreak`, `popBreak`, `getBreakTarget`, `pushContinue`, `popContinue` e `getContinueTarget` foram extraídos para a classe TargetManager.
 *     - Esta refatoração elimina duplicação de código e melhora a organização da lógica relacionada.
 *     - O método `pushContinue` agora reutiliza o método `pushBreak`, demonstrando melhor reuso de código.
 *
 * 3. **Encapsulamento**:
 *     - As listas `breakTarget` e `continueTarget` foram encapsuladas dentro da classe TargetManager, melhorando o encapsulamento e ocultamento de informações.
 *     - Os métodos na classe original agora delegam para os métodos correspondentes na classe extraída, mantendo a interface pública sem alterações.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 7
 * - **Divisão por tipo**:
 *     - **Extração de Classe**: 1 (TargetManager)
 *     - **Extração de Método**: 6 (pushBreak, popBreak, getBreakTarget, pushContinue, popContinue, getContinueTarget)
 *
 * Estas refatorações seguem os princípios de Martin Fowler de melhorar a estrutura interna do código sem alterar seu comportamento externo. O código agora está mais coeso, com responsabilidades bem definidas, e com menor duplicação de código, tornando-o mais fácil de manter e entender.
 */