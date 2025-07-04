package jparse;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class CompiledType extends Type {

    private final Class theClass;
    private final CompiledConstructor[] constrs;
    private final CompiledMethod[] meths;
    private Type[] inner;

    CompiledType(final Class cls) {
        this.theClass = cls;
        this.constrs = ReflectionUtil.extractConstructors(cls); // TRECHO REFATORADO
        this.meths = ReflectionUtil.extractMethods(cls); // TRECHO REFATORADO
    }

    CompiledType(final CompiledType original, final int dims) {
        this.theClass = ReflectionUtil.createArrayClass(original.theClass, dims); // TRECHO REFATORADO
        this.constrs = new CompiledConstructor[0];
        this.meths = new CompiledMethod[0];
        this.inner = new Type[0];
    }

    public boolean isAssignableFrom(final Type type) {
        return ReflectionUtil.isAssignableFrom(this, type); // TRECHO REFATORADO
    }

    public boolean isInterface() {
        return theClass.isInterface();
    }

    public boolean isArray() {
        return theClass.isArray();
    }

    public boolean isPrimitive() {
        return theClass.isPrimitive();
    }

    public boolean isInner() {
        return theClass.getDeclaringClass() != null;
    }

    public String getName() {
        return demangle(theClass.getName());
    }

    public Type getSuperclass() throws ClassNotFoundException {
        return ReflectionUtil.getSuperclass(theClass); // TRECHO REFATORADO
    }

    public String getPackage() {
        return ReflectionUtil.extractPackageName(getName()); // TRECHO REFATORADO
    }

    public Type[] getInterfaces() {
        return ReflectionUtil.getInterfaceTypes(theClass); // TRECHO REFATORADO
    }

    public Type getComponentType() {
        return ReflectionUtil.getComponentType(theClass); // TRECHO REFATORADO
    }

    public int getModifiers() {
        return theClass.getModifiers();
    }

    public Type getDeclaringClass() {
        return ReflectionUtil.getDeclaringClassType(theClass); // TRECHO REFATORADO
    }

    public Type[] getClasses() {
        return ReflectionUtil.getDeclaredAndInheritedClasses(this); // TRECHO REFATORADO
    }

    public Method[] getMethods() {
        return ReflectionUtil.collectMethods(this); // TRECHO REFATORADO
    }

    public Method getMethod(final String methName, final Type[] paramTypes, final Type caller) {
        return ReflectionUtil.searchBestMethodMatch(this, methName, paramTypes, caller); // TRECHO REFATORADO
    }

    public Constructor getConstructor(final Type[] params, final Type caller) {
        return ReflectionUtil.searchBestConstructorMatch(this, params, caller); // TRECHO REFATORADO
    }

    public Type getInner(final String name) {
        return ReflectionUtil.findInnerByName(this, name); // TRECHO REFATORADO
    }

    public Type getArrayType() {
        return ReflectionUtil.newArrayType(theClass); // TRECHO REFATORADO
    }

    public Type varType(final String varName) {
        return ReflectionUtil.searchVariableType(theClass, varName); // TRECHO REFATORADO
    }

    public Method[] getMeths(final String name, final Type[] params, final Type caller) {
        return ReflectionUtil.getMatchingMethods(this, name, params, caller); // TRECHO REFATORADO
    }

    public String toString() {
        return theClass.toString();
    }

    public void dump() {
        ReflectionUtil.dumpCompiledType(this, constrs); // TRECHO REFATORADO
    }
}

/**
 * ## 1) Oportunidades de Refatoração Encontradas
 * Identifiquei oportunidades claras para melhorar alguns dos métodos extensos e complexos da classe original, utilizando extração de métodos e classe auxiliar, tais como:
 * - **Construtores:** Extração em métodos separados para encapsular lógica relacionada ao reflection.
 * - **isAssignableFrom:** método extenso, que necessita separação clara dos tipos primitivos e não primitivos.
 * - **getMethods()** e **getMeths():** Métodos muitos complexos e que possuem lógicas distintas que podem ser claramente divididas, simplificando a leitura.
 *
 * ## 2) Classe Refatorada com Comentários Indicativos
 *
 * ## 3) Justificativa das Refatorações
 * Todas as alterações buscam diretamente melhorar clareza, encapsulamento e reutilização, eliminando complexidade e duplicação, em linha com as diretrizes e princípios definidos por Fowler (2018) e Marco Tulio (Engenharia de Software Moderna, 2020).
 * - **Extração de Classe:** Responsabilidade Única, Clarificação da intenção.
 * - **Extração de Métodos:** Reduz complexidade visual, melhora legibilidade e facilidade para alteração futura.
 *
 * ## 4) Resumo das Alterações
 * - Quantidade total de refatorações realizadas: **17 refatorações**
 *     - Extração de Classe: **1** (ReflectionUtil)
 *     - Extração de Métodos: **16**
 */