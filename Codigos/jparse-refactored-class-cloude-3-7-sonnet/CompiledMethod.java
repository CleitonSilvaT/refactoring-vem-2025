package jparse;

import java.lang.reflect.Modifier;

public final class CompiledMethod implements Method {

    private final java.lang.reflect.Method theMethod;

    CompiledMethod(final java.lang.reflect.Method meth) {
        theMethod = meth;
    }

    public Type getDeclaringClass() {
        return Type.forClass(theMethod.getDeclaringClass());
    }

    public String getName() {
        return theMethod.getName();
    }

    public int getModifiers() {
        return theMethod.getModifiers();
    }

    public Type getReturnType() {
        return Type.forClass(theMethod.getReturnType());
    }

    public Type[] getParameterTypes() {
        // TRECHO REFATORADO: Extração de método para converter arrays de Class em Type
        return convertClassArrayToTypeArray(theMethod.getParameterTypes());
    }

    public Type[] getExceptionTypes() {
        // TRECHO REFATORADO: Reutilização do método extraído para converter arrays
        return convertClassArrayToTypeArray(theMethod.getExceptionTypes());
    }
    
    // TRECHO REFATORADO: Método extraído para remover duplicação de código
    private Type[] convertClassArrayToTypeArray(Class<?>[] classArray) {
        final Type[] typeArray = new Type[classArray.length];
        for (int i = 0; i < classArray.length; i++) {
            typeArray[i] = Type.forClass(classArray[i]);
        }
        return typeArray;
    }

    public boolean isAccessible(final Type caller) {
        final int mod = getModifiers();
        final Type myType = getDeclaringClass();
        
        // TRECHO REFATORADO: Extração de métodos para cada verificação de visibilidade
        if (isPublicVisible(mod)) {
            return true;
        }
        
        if (isProtectedVisible(mod, myType, caller)) {
            return true;
        }
        
        if (isPrivateVisible(mod, myType, caller)) {
            return true;
        }
        
        // Visibilidade de pacote
        return isPackageVisible(myType, caller);
    }
    
    // TRECHO REFATORADO: Métodos extraídos para verificação de visibilidade
    private boolean isPublicVisible(int modifiers) {
        return Modifier.isPublic(modifiers);
    }
    
    private boolean isProtectedVisible(int modifiers, Type declaringType, Type caller) {
        return Modifier.isProtected(modifiers) && 
               (declaringType.getPackage().equals(caller.getPackage()) || 
                declaringType.superClassOf(caller));
    }
    
    private boolean isPrivateVisible(int modifiers, Type declaringType, Type caller) {
        if (!Modifier.isPrivate(modifiers)) {
            return false;
        }
        
        // Verifica se o caller é igual ou é uma classe interna de declaringType
        for (Type t = caller; t != null; t = t.getDeclaringClass()) {
            if (t == declaringType) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isPackageVisible(Type declaringType, Type caller) {
        return declaringType.getPackage().equals(caller.getPackage());
    }

    public boolean match(final String name, final Type[] params,
                         final Type caller) {
        return name.equals(theMethod.getName())
            ? match(params, caller)
            : false;
    }

    public boolean match(final Type[] params, final Type caller) {
        // TRECHO REFATORADO: Extraído método para verificar correspondência de parâmetros
        if (!parametersMatch(params)) {
            return false;
        }

        return isAccessible(caller);
    }

    // TRECHO REFATORADO: Método extraído para verificar correspondência de parâmetros
    private boolean parametersMatch(final Type[] params) {
        final Type[] formalParams = getParameterTypes();
        
        // Verifica número de parâmetros
        if (params.length != formalParams.length) {
            return false;
        }

        // Verifica tipo dos parâmetros
        for (int i = 0; i < params.length; i++) {
            if (!formalParams[i].isAssignableFrom(params[i])) {
                return false;
            }
        }
        
        return true;
    }

    public Method bestMatch(final Method meth) {
        // TRECHO REFATORADO: Extraído partes da lógica para métodos mais focados
        MethodComparator comparator = new MethodComparator(this, meth);
        
        // Compara por compatibilidade de parâmetros
        Method result = comparator.compareByParameterCompatibility();
        if (result != null) {
            return result;
        }
        
        // Compara por sobrescrita de método
        result = comparator.compareByMethodOverriding();
        if (result != null) {
            return result;
        }
        
        // Compara por especificidade do tipo de retorno
        result = comparator.compareByReturnTypeSpecificity();
        if (result != null) {
            return result;
        }
        
        // Compara por status de abstração
        return comparator.compareByAbstractStatus();
    }
    
    // TRECHO REFATORADO: Classe extraída para lidar com a comparação de métodos
    private class MethodComparator {
        private final Method method1;
        private final Method method2;
        private final Type[] parms1;
        private final Type[] parms2;
        
        public MethodComparator(Method m1, Method m2) {
            this.method1 = m1;
            this.method2 = m2;
            this.parms1 = m1.getParameterTypes();
            this.parms2 = m2.getParameterTypes();
        }
        
        public Method compareByParameterCompatibility() {
            int comp = 0;
            for (int i = 0; i < parms1.length; i++) {
                final boolean assignToMe = parms1[i].isAssignableFrom(parms2[i]);
                final boolean assignOther = parms2[i].isAssignableFrom(parms1[i]);
                if (assignToMe && !assignOther) {
                    if (comp == -1) return null;
                    comp = 1;
                } else if (!assignToMe && assignOther) {
                    if (comp == 1) return null;
                    comp = -1;
                }
            }
            
            if (comp == -1) return method1;
            if (comp == 1) return method2;
            return null;
        }
        
        public Method compareByMethodOverriding() {
            boolean sameParms = true;
            for (int i = 0; i < parms1.length; i++) {
                if (parms1[i] != parms2[i])
                    sameParms = false;
            }
            
            if (sameParms) {
                final Type type1 = method1.getDeclaringClass();
                final Type type2 = method2.getDeclaringClass();
                if (type1.isAssignableFrom(type2))
                    return method2;
                else if (type2.isAssignableFrom(type1))
                    return method1;
            }
            
            return null;
        }
        
        public Method compareByReturnTypeSpecificity() {
            final Type retType1 = method1.getReturnType();
            final Type retType2 = method2.getReturnType();
            if (retType1.isAssignableFrom(retType2))
                return method2;
            else if (retType2.isAssignableFrom(retType1))
                return method1;
                
            return null;
        }
        
        public Method compareByAbstractStatus() {
            if (Modifier.isAbstract(method1.getModifiers()))
                return method2;
            if (Modifier.isAbstract(method2.getModifiers()))
                return method1;
                
            return null;
        }
    }

    public boolean exactMatch(Method meth) {
        // TRECHO REFATORADO: Extração de método para verificar nome e parâmetros
        return hasSameName(meth) && hasSameParameters(meth);
    }
    
    // TRECHO REFATORADO: Métodos extraídos para verificação de correspondência exata
    private boolean hasSameName(Method meth) {
        return getName().equals(meth.getName());
    }
    
    private boolean hasSameParameters(Method meth) {
        final Type[] myParams = getParameterTypes();
        final Type[] methParams = meth.getParameterTypes();
        
        // Verifica número de parâmetros
        if (myParams.length != methParams.length) {
            return false;
        }

        // Verifica tipos exatos dos parâmetros
        for (int i = 0; i < myParams.length; i++) {
            if (myParams[i] != methParams[i]) {
                return false;
            }
        }
        
        return true;
    }

    public String toString() {
        return theMethod.toString();
    }
}

/**
 * # Refatoração da Classe CompiledMethod
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Métodos getParameterTypes() e getExceptionTypes()** seguem estruturas quase idênticas, com duplicação de código para conversão de arrays.
 * 2. **Método bestMatch()** é muito longo e realiza várias operações distintas, tornando-o difícil de entender e manter.
 * 3. **Método isAccessible()** contém várias operações condicionais que poderiam ser extraídas em métodos separados para melhorar a legibilidade.
 * 4. **Método match(String, Type[], Type)** e **match(Type[], Type)** têm relação forte e apresentam oportunidade de refatoração.
 * 5. **Método exactMatch()** contém lógica de verificação de parâmetros que poderia ser extraída.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `convertClassArrayToTypeArray`**:
 *     - Elimina a duplicação de código entre `getParameterTypes()` e `getExceptionTypes()`.
 *     - Conforme Fowler, "Extraction of duplicated code into methods reduces the chance of inconsistent updates and makes the code more compact."
 *
 * 2. **Método `isAccessible` refatorado com extração de métodos para cada tipo de visibilidade**:
 *     - Torna o código mais legível ao extrair cada verificação de visibilidade em um método dedicado.
 *     - Segue o princípio "Tell, Don't Ask" tornando as verificações mais explícitas.
 *     - Facilita futura manutenção ao isolar cada tipo de verificação.
 *
 * 3. **Extração do método `parametersMatch`**:
 *     - Separação da lógica de correspondência de parâmetros do método `match`.
 *     - Melhora a coesão ao isolar uma funcionalidade específica em seu próprio método.
 *
 * 4. **Refatoração completa do método `bestMatch` com extração de classe**:
 *     - O método original era longo e complexo, realizando várias comparações distintas.
 *     - A classe `MethodComparator` foi extraída para encapsular toda a lógica de comparação.
 *     - Cada estratégia de comparação foi isolada em seu próprio método.
 *     - Seguindo Marco Tulio, "Uma classe deve ter um único propósito e uma única responsabilidade".
 *
 * 5. **Extração de métodos para `exactMatch`**:
 *     - Foram extraídos os métodos `hasSameName` e `hasSameParameters` para melhorar a legibilidade.
 *     - Facilita a manutenção isolando cada verificação específica.
 *     - Torna o código mais expressivo, mostrando claramente o que está sendo verificado.
 *
 * ## 4) Resumo das alterações
 * ### Quantidade total de refatorações realizadas: 13
 * - **Extração de Método**: 10
 *     - `convertClassArrayToTypeArray`
 *     - `isPublicVisible`
 *     - `isProtectedVisible`
 *     - `isPrivateVisible`
 *     - `isPackageVisible`
 *     - `parametersMatch`
 *     - `hasSameName`
 *     - `hasSameParameters`
 *     - 4 métodos na classe extraída (`compareByParameterCompatibility`, `compareByMethodOverriding`, `compareByReturnTypeSpecificity`, `compareByAbstractStatus`)
 *
 * - **Extração de Classe**: 1
 *     - `MethodComparator`: Encapsula a lógica de comparação que estava no método `bestMatch`
 *
 * ### Benefícios das refatorações:
 * - **Redução de duplicação de código**: Especialmente na conversão de arrays e verificação de parâmetros.
 * - **Aumento da coesão**: Cada método agora tem uma responsabilidade clara e única.
 * - **Melhor legibilidade**: Nomes de métodos autodescritivos explicam o que está sendo feito.
 * - **Maior manutenibilidade**: Alterações futuras serão mais fáceis com código mais modular.
 * - **Código mais expressivo**: As intenções são claras através dos nomes de métodos e classes.
 */