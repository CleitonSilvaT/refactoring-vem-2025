package jparse.stmt;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class EmptyAST extends StatementAST implements JavaTokenTypes {

    public EmptyAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Extraído método para configurar o tipo do nó
        configureNodeType();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade do construtor
    private void configureNodeType() {
        setType(EMPTY_STAT);
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Extraído método para obter tipos de exceções vazios
        return getEmptyExceptionTypes();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade
    private Type[] getEmptyExceptionTypes() {
        return noTypes;
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Extraído método para criar o array de controle
        return createControlFlow();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade
    private StatementAST[] createControlFlow() {
        return new StatementAST[] { next };
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Utilização da classe utilitária para criar VarList vazia
        return VarListFactory.createEmptyVarList();
    }
}

// TRECHO REFATORADO: Classe extraída para encapsular a criação de objetos VarList
class VarListFactory {
    /**
     * Cria uma lista de variáveis vazia.
     *
     * @return Uma nova instância de VarList vazia
     */
    public static VarList createEmptyVarList() {
        return new VarList();
    }
}

/**
 *
 * # Refatoração da Classe EmptyAST
 * Conforme solicitado, vou analisar e refatorar a classe EmptyAST seguindo os princípios de Extração de Métodos e Extração de Classes.
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código da classe EmptyAST, identifiquei as seguintes oportunidades de refatoração:
 * 1. O construtor realiza duas operações: chama o construtor da superclasse e configura o tipo do nó. Esta segunda operação pode ser extraída para um método auxiliar.
 * 2. Os três métodos `computeExceptions()`, `computeControl()` e `getVarList()` são relativamente simples, mas poderiam beneficiar-se de extração de métodos para melhorar a legibilidade.
 * 3. A criação de objetos VarList vazios é uma operação que poderia ser extraída para uma classe utilitária.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `configureNodeType()`**: Seguindo o princípio de "Responsabilidade Única" destacado por Fowler, extraí a configuração do tipo do nó para um método separado. Isso torna o construtor mais limpo e focado apenas em inicializar o objeto.
 * 2. **Extração dos métodos `getEmptyExceptionTypes()` e `createControlFlow()`**: Conforme sugerido por Fowler, mesmo operações simples podem ser extraídas para métodos separados quando isso aumenta a legibilidade. Esses métodos tornam mais claro o que cada parte do código está fazendo.
 * 3. **Extração da classe `VarListFactory`**: Aplicando o princípio de "Extração de Classe" descrito por Fowler e também mencionado por Marco Tulio em Engenharia de Software Moderna, criei uma classe utilitária para encapsular a criação de instâncias vazias de VarList. Isso facilita a reutilização dessa funcionalidade em outras partes do código e segue o padrão Factory.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5 refatorações
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 4 (configureNodeType, getEmptyExceptionTypes, createControlFlow, createEmptyVarList)
 *     - **Extração de Classe**: 1 (VarListFactory)
 *
 * As refatorações realizadas melhoram a legibilidade e a manutenibilidade do código, separando responsabilidades e tornando mais claras as operações realizadas em cada trecho. Conforme recomendado por Martin Fowler, mesmo refatorações simples podem ter um impacto significativo na qualidade do código quando aplicadas consistentemente.
 */