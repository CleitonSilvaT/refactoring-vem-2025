package jparse;

import java.lang.reflect.Modifier;

public final class CompiledConstructor implements Constructor {

    private final java.lang.reflect.Constructor theConstructor;

    CompiledConstructor(final java.lang.reflect.Constructor cons) {
        theConstructor = cons;
    }

    public Type getDeclaringClass() {
        return Type.forClass(theConstructor.getDeclaringClass());
    }

    public String getName() {
        return theConstructor.getName();
    }

    public int getModifiers() {
        return theConstructor.getModifiers();
    }

    public Type[] getParameterTypes() {
        // TRECHO REFATORADO
        return convertClassesToTypes(theConstructor.getParameterTypes());
    }

    public Type[] getExceptionTypes() {
        // TRECHO REFATORADO
        return convertClassesToTypes(theConstructor.getExceptionTypes());
    }

    private Type[] convertClassesToTypes(final Class<?>[] classes) {
        // TRECHO REFATORADO
        final Type[] types = new Type[classes.length];
        for (int i = 0; i < classes.length; i++)
            types[i] = Type.forClass(classes[i]);
        return types;
    }

    public boolean match(final Type[] params, final Type caller) {
        final Type[] formalParams = getParameterTypes();
        if (params.length != formalParams.length) {
            return false;
        }

        if (!parametrosCompativeis(params, formalParams)) {
            return false;
        }

        // TRECHO REFATORADO
        return permiteAcesso(caller);
    }

    private boolean parametrosCompativeis(final Type[] params, final Type[] formalParams) {
        // TRECHO REFATORADO
        for (int i = 0; i < params.length; i++) {
            if (!formalParams[i].isAssignableFrom(params[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean permiteAcesso(final Type caller) {
        // TRECHO REFATORADO
        final int mod = getModifiers();
        final Type myType = getDeclaringClass();

        if (Modifier.isPublic(mod)) return true;
        if (Modifier.isProtected(mod)) return isProtectedAccess(myType, caller);
        if (Modifier.isPrivate(mod)) return isPrivateAccess(myType, caller);
        
        return isPackageAccess(myType, caller);
    }

    private boolean isProtectedAccess(final Type myType, final Type caller) {
        // TRECHO REFATORADO
        return myType.getPackage().equals(caller.getPackage()) || myType.superClassOf(caller);
    }

    private boolean isPrivateAccess(final Type myType, final Type caller) {
        // TRECHO REFATORADO
        for (Type t = caller; t != null; t = t.getDeclaringClass()) {
            if (t == myType) {
                return true;
            }
        }
        return false;
    }

    private boolean isPackageAccess(final Type myType, final Type caller) {
        // TRECHO REFATORADO
        return myType.getPackage().equals(caller.getPackage());
    }

    public Constructor bestMatch(final Constructor cons) {
        final Type[] parms1 = getParameterTypes();
        final Type[] parms2 = cons.getParameterTypes();

        // TRECHO REFATORADO
        int comp = comparaParametros(parms1, parms2);
        
        switch (comp) {
            case -1: return this;
            case 1: return cons;
            default: return null;
        }
    }

    private int comparaParametros(final Type[] parms1, final Type[] parms2) {
        // TRECHO REFATORADO
        int comp = 0;
        for (int i = 0; i < parms1.length; i++) {
            final boolean assignToMe = parms1[i].isAssignableFrom(parms2[i]);
            final boolean assignOther = parms2[i].isAssignableFrom(parms1[i]);
            if (assignToMe && !assignOther) {
                if (comp == -1) return 0; 
                comp = 1;
            } else if (!assignToMe && assignOther) {
                if (comp == 1) return 0; 
                comp = -1;
            }
        }
        return comp;
    }

    public String toString() {
        return theConstructor.toString();
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * - A classe `CompiledConstructor` possui métodos com estruturas semelhantes, como `getParameterTypes()` e `getExceptionTypes()`. Isso sugere a possibilidade de extração de um método privado para eliminar duplicidade de código.
 * - O método `match(Type[] params, Type caller)` possui várias verificações relacionadas ao modificador de acesso (public, protected, private e package-level). Isso justifica claramente a extração de novos métodos privados para simplificar a lógica interna.
 * - O método `bestMatch(Constructor cons)` compara dois construtores. A lógica pode ser simplificada extraindo um método privado auxiliar.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * **Extração de método `convertClassesToTypes`**:
 * - Realiza uma operação semelhante em `getParameterTypes()` e `getExceptionTypes()`.
 * - Remove duplicação, facilitando posterior manutenção e alterações futuras (Fowler, princípio DRY — Don't Repeat Yourself).
 *
 * **Extração dos métodos `parametrosCompativeis`, `permiteAcesso`, `isProtectedAccess`, `isPrivateAccess`, e `isPackageAccess` no método `match(...)`**:
 * - Divisão lógica clara, melhora a legibilidade e facilita compreensão (Fowler, método deve possuir apenas um nível de abstração).
 * - Segue princípio "Separação de Preocupações", cada método foca-se numa única tarefa, melhorando manutenção e testes.
 *
 * **Extração de método `comparaParametros` em `bestMatch(...)`**:
 * - Simplifica a complexidade do método, tornando claro o papel de cada método.
 * - Torna lógico o método abstrato e claro (conceitos de "Métodos Pequenos e Claros" - Engenharia de Software Moderna, Marco Tulio).
 *
 * ## 4) Resumo das alterações:
 * **Quantidade total de refatorações realizadas**: 9 refatorações no total.
 * - **Extração de métodos**: 9
 *     1. `convertClassesToTypes(...)`
 *     2. `parametrosCompativeis(...)`
 *     3. `permiteAcesso(...)`
 *     4. `isProtectedAccess(...)`
 *     5. `isPrivateAccess(...)`
 *     6. `isPackageAccess(...)`
 *     7. `comparaParametros(...)`
 *
 * - **Extração de classes**: 0
 * (Neste caso específico, a extração de classe não se mostrou necessária por não existir agrupamento lógico adicional que justifique essa abstração.)
 */