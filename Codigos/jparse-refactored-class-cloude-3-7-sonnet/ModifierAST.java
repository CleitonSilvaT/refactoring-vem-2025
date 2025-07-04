package jparse;

import java.lang.reflect.Modifier;

public final class ModifierAST extends JavaAST implements JavaTokenTypes {

    int mods;

    // TRECHO REFATORADO: Extraído construtor para métodos mais claros
    public ModifierAST(final int modBits) {
        super();
        inicializarAST();
        definirModificadores(modBits);
    }

    // TRECHO REFATORADO: Método extraído para inicialização do AST
    private void inicializarAST() {
        initialize(MODIFIERS, "MODIFIERS");
    }

    // TRECHO REFATORADO: Método extraído para definição de modificadores
    private void definirModificadores(final int modBits) {
        mods = modBits;
    }

    // TRECHO REFATORADO: Extraída operação de modificação para uma classe auxiliar
    void setInterface() {
        ModificadorHelper.aplicarModificadorInterface(this);
    }

    // TRECHO REFATORADO: Extraída operação de modificação para uma classe auxiliar
    void setInterfaceMethod() {
        ModificadorHelper.aplicarModificadorMetodoInterface(this);
    }

    // TRECHO REFATORADO: Método extraído para verificar qualquer modificador
    private boolean isModifier(int modifier) {
        return (mods & modifier) != 0;
    }

    public boolean isPublic() {
        return Modifier.isPublic(mods);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(mods);
    }

    public boolean isProtected() {
        return Modifier.isProtected(mods);
    }

    public boolean isStatic() {
        return Modifier.isStatic(mods);
    }

    public boolean isFinal() {
        return Modifier.isFinal(mods);
    }

    public boolean isSynchronized() {
        return Modifier.isSynchronized(mods);
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(mods);
    }

    public boolean isTransient() {
        return Modifier.isTransient(mods);
    }

    public boolean isNative() {
        return Modifier.isNative(mods);
    }

    public boolean isInterface() {
        return Modifier.isInterface(mods);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(mods);
    }

    public boolean isStrict() {
        return Modifier.isStrict(mods);
    }

    public String toString() {
        return Modifier.toString(mods);
    }
}

// TRECHO REFATORADO: Nova classe extraída para manipular as operações específicas de modificadores
class ModificadorHelper {
    
    public static void aplicarModificadorInterface(ModifierAST modifierAST) {
        modifierAST.mods |= Modifier.INTERFACE;
    }
    
    public static void aplicarModificadorMetodoInterface(ModifierAST modifierAST) {
        modifierAST.mods |= Modifier.PUBLIC | Modifier.ABSTRACT;
    }
}


/**
 * # Refatoração da Classe ModifierAST
 * Vou analisar e refatorar a classe ModifierAST seguindo os princípios de Extração de Classe e Extração de Métodos conforme solicitado.
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Métodos de verificação de modificadores**: Há uma grande repetição de padrão nos métodos que verificam modificadores específicos (`isPublic()`, `isPrivate()`, etc.). Todos seguem o mesmo formato, delegando para a classe `Modifier`.
 * 2. **Configuração de modificadores**: As operações para definir tipos especiais de modificadores (`setInterface()`, `setInterfaceMethod()`) podem ser extraídas para melhor organização.
 * 3. **Inicialização de objeto**: O código no construtor pode ser extraído para um método separado para melhorar a legibilidade.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Métodos para Inicialização**:
 *     - Extraí o código de inicialização do construtor em métodos separados `inicializarAST()` e `definirModificadores()`. Isso melhora a legibilidade do construtor e torna cada função mais coesa, seguindo o princípio da Responsabilidade Única.
 *
 * 2. **Extração de Classe para Operações de Modificadores**:
 *     - Criei a classe `ModificadorHelper` para encapsular operações relacionadas à manipulação dos tipos de modificadores. Esta extração é justificada porque:
 *         - Essas operações têm uma responsabilidade específica (gerenciar tipos especiais de modificadores)
 *         - Podem ser reutilizadas em outras partes do sistema
 *         - Aumenta a coesão da classe ModifierAST
 *
 * Isso segue o que Martin Fowler sugere em seu livro sobre extração de classe quando um grupo de métodos opera num conjunto específico de dados.
 * 3. **Método auxiliar para verificação de modificadores**:
 *     - Adicionei o método `isModifier()` que poderia ser usado para substituir as chamadas repetitivas ao `Modifier`. Embora não tenha alterado os métodos existentes, este método pode ser usado para futuras extensões.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Por tipo**:
 *     - **Extração de Método**: 3 (inicializarAST, definirModificadores, isModifier)
 *     - **Extração de Classe**: 1 (ModificadorHelper com 2 métodos)
 */