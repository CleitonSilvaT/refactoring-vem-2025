package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.IdentifierAST;

public final class BreakAST extends StatementAST {

    private IdentifierAST label;

    public BreakAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        final AST maybeLabel = getFirstChild();
        // TRECHO REFATORADO: Extraído método processLabel para lidar com a lógica de processamento do label
        processLabel(maybeLabel);
    }

    // TRECHO REFATORADO: Novo método extraído para encapsular a lógica de processamento do label
    private void processLabel(final AST maybeLabel) {
        if (maybeLabel instanceof IdentifierAST) {
            label = (IdentifierAST)maybeLabel;
        } else {
            // TRECHO REFATORADO: Extraído método configureDefaultBreakTarget para lidar com break sem label
            configureDefaultBreakTarget();
        }
    }
    
    // TRECHO REFATORADO: Novo método extraído para encapsular a lógica de configuração do alvo padrão do break
    private void configureDefaultBreakTarget() {
        control = new StatementAST[] { context.breakTarget().next };
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Extraído método getControlForLabel para encapsular a lógica de obtenção de controle baseado no label
        return getControlForLabel();
    }

    // TRECHO REFATORADO: Novo método extraído para encapsular a lógica de obtenção de controle baseado no label
    private StatementAST[] getControlForLabel() {
        return new StatementAST[] { symTable.getLabel(label.getName()) };
    }

    public VarList getVarList() {
        return new VarList();
    }

    public IdentifierAST getLabel() {
        return label;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código, identifiquei as seguintes oportunidades de refatoração:
 * 1. Na função `parseComplete()`, existe um bloco de código que poderia ser extraído para um método separado, melhorando a legibilidade e o encapsulamento da lógica de processamento do label.
 * 2. O método `computeControl()` poderia ser simplificado por meio de extração de método para encapsular a lógica de obtenção de controle baseado no label.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `processLabel`**:
 *     - Foi extraído da função `parseComplete()` para melhorar a legibilidade e o encapsulamento da lógica de processamento do label.
 *     - De acordo com Martin Fowler, a Extração de Método é importante quando temos um fragmento de código que pode ser agrupado, melhorando a clareza do código ao dar um nome descritivo à operação sendo realizada.
 *
 * 2. **Extração do método `configureDefaultBreakTarget`**:
 *     - Foi extraído para isolar a lógica específica de configuração do alvo de break quando não há label.
 *     - Esta extração segue o princípio de responsabilidade única, permitindo que cada método tenha uma função clara e bem definida.
 *
 * 3. **Extração do método `getControlForLabel`**:
 *     - Foi extraído de `computeControl()` para encapsular a lógica específica de obtenção do controle baseado no label.
 *     - Esta refatoração melhora a legibilidade ao substituir um comentário por um nome de método autodocumentado, seguindo as práticas recomendadas por Fowler.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 3 (processLabel, configureDefaultBreakTarget, getControlForLabel)
 *     - **Extração de Classe**: 0 (não foi necessário extrair classes neste caso)
 *
 * As refatorações aplicadas melhoraram a organização do código, tornando-o mais legível e modular, sem alterar seu comportamento. Os métodos extraídos seguem o princípio de responsabilidade única e possuem nomes descritivos que ajudam a entender sua função, conforme recomendado por Martin Fowler em "Refactoring: Improving the Design of Existing Code".
 */