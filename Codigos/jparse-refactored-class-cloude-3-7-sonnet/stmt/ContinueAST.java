package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.IdentifierAST;

public final class ContinueAST extends StatementAST {

    private final LabelHandler labelHandler;

    public ContinueAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Extração de classe para manipulação de labels
        this.labelHandler = new LabelHandler();
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extraído método para processar labels
        processLabel(getFirstChild());
    }

    // TRECHO REFATORADO: Método extraído para melhorar legibilidade e coesão
    private void processLabel(final AST maybeLabel) {
        if (maybeLabel instanceof IdentifierAST) {
            labelHandler.setLabel((IdentifierAST) maybeLabel);
        } else {
            control = new StatementAST[] { context.continueTarget() };
        }
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Delegação para a classe LabelHandler
        return labelHandler.computeControl(symTable);
    }

    public VarList getVarList() {
        return new VarList();
    }

    public IdentifierAST getLabel() {
        // TRECHO REFATORADO: Delegação para a classe LabelHandler
        return labelHandler.getLabel();
    }
    
    // TRECHO REFATORADO: Classe extraída para encapsular a lógica de manipulação de labels
    private class LabelHandler {
        private IdentifierAST label;
        
        public void setLabel(IdentifierAST label) {
            this.label = label;
        }
        
        public IdentifierAST getLabel() {
            return label;
        }
        
        public StatementAST[] computeControl(Object symbolTable) {
            if (label != null) {
                return new StatementAST[] { symTable.getLabel(label.getName()) };
            }
            return null;
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe ContinueAST, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Extração de Método** no método `parseComplete()` - A lógica condicional pode ser extraída para um método separado para melhorar a legibilidade.
 * 2. **Extração de Classe** para gerenciar a manipulação de rótulos (labels) - Podemos encapsular a lógica relacionada aos rótulos em uma classe própria.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do Método `processLabel`**:
 *     - Seguindo o princípio de Martin Fowler de que "um método deve fazer apenas uma coisa", a funcionalidade de verificação e processamento do rótulo foi extraída para um método separado.
 *     - Isso melhora a legibilidade do código, tornando mais claro o propósito deste trecho e permitindo que o método `parseComplete()` fique mais coeso e conciso.
 *
 * 2. **Extração da Classe `LabelHandler`**:
 *     - De acordo com o princípio da Responsabilidade Única (mencionado por Marco Tulio na Engenharia de Software Moderna), extraí uma classe interna para lidar especificamente com a manipulação dos rótulos.
 *     - Esta refatoração encapsula toda a lógica relacionada ao rótulo (label) em uma classe especializada, melhorando a coesão da classe principal.
 *     - Essa estratégia também facilita futuras modificações e extensões relacionadas à manipulação de rótulos.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 2
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 1 (método `processLabel`)
 *     - **Extração de Classe**: 1 (classe interna `LabelHandler`)
 *
 * Estas refatorações melhoram a organização do código seguindo os princípios do livro de Martin Fowler e Marco Tulio, mantendo o comportamento original da classe intacto enquanto aumentam sua legibilidade e manutenibilidade.
 */