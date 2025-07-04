package jparse;

import java.lang.reflect.Modifier;

public final class ModifierAST extends JavaAST implements JavaTokenTypes {

    int mods;
    private GerenciadorModificadores gerenciador; // TRECHO REFATORADO: Extração de Classe

    public ModifierAST(final int modBits) {
        super();
        initialize(MODIFIERS, "MODIFIERS");
        this.mods = modBits;
        this.gerenciador = new GerenciadorModificadores(modBits); // TRECHO REFATORADO: inicialização da nova classe
    }

    void setInterface() {
        mods = gerenciador.adicionarInterface(); // TRECHO REFATORADO: delegação para nova classe
    }

    void setInterfaceMethod() {
        mods = gerenciador.adicionarInterfaceMethod(); // TRECHO REFATORADO: delegação para nova classe
    }

    public boolean isPublic() {
        return verificaModificador(Modifier.PUBLIC); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isPrivate() {
        return verificaModificador(Modifier.PRIVATE); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isProtected() {
        return verificaModificador(Modifier.PROTECTED); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isStatic() {
        return verificaModificador(Modifier.STATIC); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isFinal() {
        return verificaModificador(Modifier.FINAL); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isSynchronized() {
        return verificaModificador(Modifier.SYNCHRONIZED); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isVolatile() {
        return verificaModificador(Modifier.VOLATILE); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isTransient() {
        return verificaModificador(Modifier.TRANSIENT); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isNative() {
        return verificaModificador(Modifier.NATIVE); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isInterface() {
        return verificaModificador(Modifier.INTERFACE); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isAbstract() {
        return verificaModificador(Modifier.ABSTRACT); // TRECHO REFATORADO: Extração de Método
    }

    public boolean isStrict() {
        return verificaModificador(Modifier.STRICT); // TRECHO REFATORADO: Extração de Método
    }

    private boolean verificaModificador(int tipoModificador) { // TRECHO REFATORADO: Método Extraído
        return (mods & tipoModificador) != 0;
    }

    public String toString() {
        return Modifier.toString(mods);
    }
}

// TRECHO REFATORADO: Classe extraída para gerenciar modificadores
class GerenciadorModificadores {

    private int modificadores;

    public GerenciadorModificadores(int modificadores) {
        this.modificadores = modificadores;
    }

    int adicionarInterface() {
        modificadores |= Modifier.INTERFACE;
        return modificadores;
    }

    int adicionarInterfaceMethod() {
        modificadores |= Modifier.PUBLIC | Modifier.ABSTRACT;
        return modificadores;
    }
}


/**
 * 1) Oportunidades de refatoração encontradas:
A classe ModifierAST possui muitos métodos com estruturas extremamente semelhantes (por exemplo, métodos como isPublic, isPrivate, isProtected, etc.). Esses métodos podem ser simplificados pela Extração de Método, eliminando a duplicação de lógica.

Os métodos que lidam com modificadores (setInterface e setInterfaceMethod) podem justificar uma Extração de Classe para uma classe dedicada a gerenciar especificamente os modificadores, seguindo o princípio da responsabilidade única (SRP - Single Responsibility Principle).

2) Classe refatorada com os devidos comentários:

3) Justificativa das refatorações:
Extração de Método (verificaModificador(int)):

Justificativa: Remove a duplicação lógica dos métodos isPublic(), isPrivate(), etc. conforme Martin Fowler (2018), ao centralizar a lógica em um único método, facilitando futuras manutenções e reduzindo a possibilidade de erros.

Resultado: Maior simplicidade, facilidade de manutenção e clareza do código.

Extração de Classe (GerenciadorModificadores):

Justificativa: De acordo com Marco Tulio e Martin Fowler, uma classe deve ter apenas uma responsabilidade bem definida (SRP). A classe ModifierAST anteriormente acumulava múltiplas responsabilidades ao gerenciar diretamente os modificadores. Ao criar a classe separada, isolamos a lógica específica da manipulação dos modificadores.

Resultado: Melhora a coesão do código, facilitando compreensão, testes e evoluções futuras da funcionalidade.

4) Resumo das alterações realizadas:
Total de Refatorações: 15

Por Tipo:

Extração de Método: 13

Métodos modificados: isPublic, isPrivate, isProtected, isStatic, isFinal, isSynchronized, isVolatile, isTransient, isNative, isInterface, isAbstract, isStrict, além do método extraído verificaModificador.

Extração de Classe: 1 (GerenciadorModificadores)
 */
