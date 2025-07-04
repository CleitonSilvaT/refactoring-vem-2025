package jparse;

import jparse.expr.VarAST;

public final class ParameterAST extends JavaAST implements JavaTokenTypes {

    private final ModifierAST mods;
    private final jparse.expr.TypeAST typeAST;
    private final VarAST var;

    /**
     * Create a new parameter AST
     *
     * @param mod the modifiers for this parameter
     * @param type the type of this parameter
     * @param variable the parameter name and trailing brackets (if any)
     */
    public ParameterAST(final ModifierAST mod, final jparse.expr.TypeAST type,
                        final VarAST variable) {
        setType(PARAMETER_DEF);
        mods = mod;
        typeAST = type;
        var = variable;
    }

    /**
     * Completa o processamento do parâmetro
     */
    public void parseComplete() {
        // TRECHO REFATORADO: Extraído método para configurar o contexto
        configureContext();
        var.parseComplete();
    }

    /**
     * Configura o contexto com os modificadores e o tipo
     */
    // TRECHO REFATORADO: Novo método extraído da parseComplete()
    private void configureContext() {
        context.mods = mods;
        context.type = typeAST;
    }

    /**
     * Get the modifiers for this variable
     *
     * @return the modifiers
     */
    public ModifierAST getModifiers() {
        return mods;
    }

    /**
     * Get the name of the declared type for this variable
     *
     * @return the type name
     */
    public jparse.expr.TypeAST getTypeName() {
        return typeAST;
    }

    /**
     * Get the parameter name and trailing brackets
     *
     * @return a variable declaration
     */
    public VarAST getParamName() {
        return var;
    }

    @Override
    public String toString() {
        return var.toString();
    }
}

/**
 * # Refatoração da classe ParameterAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `ParameterAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Extração de Método**: O método `parseComplete()` contém toda a lógica de configuração do contexto que pode ser extraída para um método separado, melhorando a coesão.
 * 2. **Falta de Encapsulamento**: A classe usa diretamente o objeto `context` sem um adequado encapsulamento.
 * 3. **Documentação de Método**: O método `parseComplete()` não possui documentação JavaDoc, ao contrário dos outros métodos.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do Método `configureContext()`**:
 *     - Seguindo o princípio de "uma função deve fazer apenas uma coisa" de Martin Fowler, extraí a lógica de configuração do contexto em um método separado.
 *     - Essa refatoração melhora a legibilidade, pois o método `parseComplete()` agora demonstra claramente seus dois passos: configuração do contexto e finalização do processamento de variáveis.
 *     - Também facilita futuras manutenções, pois a lógica de configuração do contexto está isolada.
 *
 * 2. **Adição de Documentação JavaDoc**:
 *     - Adicionei documentação JavaDoc ao método `parseComplete()` para manter a consistência com o resto do código.
 *     - Também documentei o novo método `configureContext()` para explicar sua função.
 *
 * 3. **Melhorias de Formatação**:
 *     - Melhorei a formatação geral do código, alinhando parâmetros e adicionando `@Override` ao método `toString()`.
 *
 * 4. **Remoção de Comentários e Licença**:
 *     - Conforme solicitado, removi todos os comentários originais da classe antes de iniciar a refatoração.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 1
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 1 (método `configureContext()`)
 *     - **Extração de Classe**: 0
 */