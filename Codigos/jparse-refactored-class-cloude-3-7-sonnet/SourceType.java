package jparse;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import jparse.expr.VarAST;

public final class SourceType extends Type {
    final FileAST file;
    boolean anonymous;
    private final TypeAST theType;
    private final int dim;
    private Type parent;
    private Type[] interfaces;
    private Type[] inner;
    private SourceType outer;
    private final int modifiers;
    private ConstrAST[] constrs;
    
    // TRECHO REFATORADO: Extraída classe auxiliar para manipulação de array types
    private final ArrayTypeHandler arrayTypeHandler;
    
    // TRECHO REFATORADO: Extraída classe auxiliar para manipulação de métodos
    private final MethodHandler methodHandler;

    SourceType(final TypeAST root) {
        root.type = this;
        theType = root;
        file = root.topLevel;
        dim = 0;
        modifiers = root.modifiers.mods;
        
        // TRECHO REFATORADO: Inicialização das classes auxiliares extraídas
        this.arrayTypeHandler = new ArrayTypeHandler(this);
        this.methodHandler = new MethodHandler(this);

        initializeConstructors(root);

        for (int i = 0; i < root.inner.length; i++) {
            root.inner[i].type.outer = this;
        }
    }

    SourceType(final SourceType original, final int dims) {
        final TypeAST root = original.theType;
        theType = root;
        file = root.topLevel;
        modifiers = root.modifiers.mods;
        dim = dims;
        
        // TRECHO REFATORADO: Inicialização das classes auxiliares extraídas
        this.arrayTypeHandler = new ArrayTypeHandler(this);
        this.methodHandler = new MethodHandler(this);
    }
    
    // TRECHO REFATORADO: Método extraído para inicializar construtores
    private void initializeConstructors(final TypeAST root) {
        if (root.constructors == null) {
            final TypeAST temp = TypeAST.currType;
            TypeAST.currType = root;
            constrs = new ConstrAST[] { new ConstrAST() };
            TypeAST.currType = temp;
        } else {
            constrs = root.constructors;
        }
    }

    public void anonCheckSuper() {
        anonymous = true;
        try {
            final Type parent = getSuperclass();
            if (parent.isInterface()) {
                interfaces = new Type[] { parent };
                this.parent = Type.objectType;
            }
        } catch (ClassNotFoundException classEx) {
        }
    }

    public boolean isAssignableFrom(final Type type) {
        // TRECHO REFATORADO: Extraídos métodos para melhorar legibilidade
        if (this == type || type == null)
            return true;

        if (type.isPrimitive())
            return false;

        if (type.isArray()) {
            return isAssignableToArray(type);
        }

        if (isInterface()) {
            return handleInterfaceAssignment(type);
        }

        return superClassOf(type);
    }
    
    // TRECHO REFATORADO: Método extraído para verificar compatibilidade com arrays
    private boolean isAssignableToArray(final Type type) {
        return (dim != 0)
            ? getComponentType().isAssignableFrom(type.getComponentType())
            : false;
    }
    
    // TRECHO REFATORADO: Método extraído para verificar atribuição de interfaces
    private boolean handleInterfaceAssignment(final Type type) {
        return type.isInterface()
            ? superInterfaceOf(type)
            : type.implementsInterface(this);
    }

    public boolean isInterface() {
        return Modifier.isInterface(modifiers);
    }

    public boolean isArray() {
        return dim != 0;
    }

    public boolean isPrimitive() {
        return false;    // Can't have a source file for a primitive type!
    }

    public boolean isInner() {
        return outer != null;
    }

    public String getName() {
        // TRECHO REFATORADO: Extraído método para formatar nome de array
        if (dim == 0)
            return theType.name;
        return formatArrayName();
    }
    
    // TRECHO REFATORADO: Método extraído para formatar nome de array
    private String formatArrayName() {
        final StringBuffer buf = new StringBuffer(theType.name);
        for (int i = 0; i < dim; i++)
            buf.append("[]");
        return buf.toString();
    }

    public Type getSuperclass() throws ClassNotFoundException {
        if (parent == null) {
            parent = (dim > 0 || theType.superclass == null)
                ? Type.forClass(Object.class)
                : file.getType(theType.superclass);
        }
        return parent;
    }

    public String getPackage() {
        return file.pkg;
    }

    public Type[] getInterfaces() {
        if (interfaces == null) {
            initializeInterfaces();
        }
        return interfaces;
    }
    
    // TRECHO REFATORADO: Método extraído para inicializar interfaces
    private void initializeInterfaces() {
        final String[] theInterfaces = theType.interfaces;
        interfaces = new Type[theInterfaces.length];
        for (int i = 0; i < theInterfaces.length; i++) {
            try {
                interfaces[i] = file.getType(theInterfaces[i]);
            } catch (ClassNotFoundException ex2) {
            }
        }
    }

    public Type getComponentType() {
        return arrayTypeHandler.getComponentType();
    }

    public int getModifiers() {
        return modifiers;
    }

    public Type getDeclaringClass() {
        return outer;
    }

    public Type[] getClasses() {
        if (inner == null) {
            inner = initializeInnerClasses();
        }
        return inner;
    }
    
    // TRECHO REFATORADO: Método extraído para inicializar classes internas
    private Type[] initializeInnerClasses() {
        Type[] parentTypes;
        try {
            parentTypes = getSuperclass().getClasses();
        } catch (ClassNotFoundException classEx) {
            parentTypes = new Type[0];
        }

        final TypeAST[] myInner = theType.inner;
        Type[] result = new Type[parentTypes.length + myInner.length];
        
        // Inicializa as classes internas desta classe
        for (int i = 0; i < myInner.length; i++) {
            result[i] = myInner[i].retrieveType();
        }
        
        // Adiciona as classes internas da classe pai
        System.arraycopy(parentTypes, 0, result, myInner.length, parentTypes.length);

        // Inclui as classes internas das classes internas
        for (int i = 0; i < myInner.length; i++) {
            result = mergeTypeLists(result, myInner[i].retrieveType().getClasses());
        }
        
        return result;
    }

    public Method[] getMethods() {
        if (dim != 0) {
            return new Method[0];
        }
        
        return methodHandler.getAllMethods();
    }

    public Method getMethod(final String methName, final Type[] paramTypes,
                           final Type caller) {
        final Method meth =
            theType.symTable.getMeth(methName, paramTypes, this);
        return (meth != null || outer == null)
            ? meth
            : outer.getMethod(methName, paramTypes, caller);
    }

    public Constructor getConstructor(final Type[] params, final Type caller) {
        // TRECHO REFATORADO: Extraído método para encontrar o melhor construtor
        Constructor best = findBestConstructor(params, caller);
        
        if (best == null && anonymous) {
            try {
                best = getSuperclass().getConstructor(params, caller);
            } catch (ClassNotFoundException classEx) {
            }
        }
        return best;
    }
    
    // TRECHO REFATORADO: Método extraído para encontrar o melhor construtor
    private Constructor findBestConstructor(final Type[] params, final Type caller) {
        Constructor best = null;
        for (int i = 0; i < constrs.length; i++) {
            final Constructor c = constrs[i];
            if (c.match(params, caller)) {
                best = (best == null) ? c : best.bestMatch(c);
            }
        }
        return best;
    }

    public Type getInner(final String name) {
        final Type[] inner = getClasses();
        for (int i = 0; i < inner.length; i++) {
            if (inner[i].getName().endsWith(name)) {
                return inner[i];
            }
        }
        return null;
    }

    public Type getArrayType() {
        return arrayTypeHandler.getArrayType();
    }

    public Type varType(final String varName) {
        // TRECHO REFATORADO: Extraído método para busca de variáveis na hierarquia
        return findVariableTypeInHierarchy(varName);
    }
    
    // TRECHO REFATORADO: Método extraído para busca de variáveis na hierarquia
    private Type findVariableTypeInHierarchy(final String varName) {
        // Verifica na tabela de símbolos local
        final VarAST var = theType.symTable.getVar(varName);
        if (var != null)
            return var.retrieveType();

        // Verifica na classe pai
        try {
            if (getSuperclass() != null) {
                final Type type = parent.varType(varName);
                if (type != null)
                    return type;
            }
        } catch (ClassNotFoundException classEx) {
        }

        // Verifica nas interfaces
        return findVariableTypeInInterfaces(varName);
    }
    
    // TRECHO REFATORADO: Método extraído para busca de variáveis nas interfaces
    private Type findVariableTypeInInterfaces(final String varName) {
        final Type[] theInterfaces = getInterfaces();
        for (int i = 0; i < theInterfaces.length; i++) {
            final Type type = theInterfaces[i].varType(varName);
            if (type != null)
                return type;
        }
        return null;
    }

    public Method[] getMeths(final String name, final Type[] params,
                            final Type caller) {
        Method[] meths = theType.symTable.getMeths(name, params, caller);
        if (outer != null) {
            meths = methodHandler.mergeOuterMethods(outer, name, params, caller, meths);
        }
        return meths;
    }

    public String toString() {
        return (isInterface() ? "interface " : "class ") + getName();
    }

    public void dump() {
        final StringBuffer buf = new StringBuffer(toString());
        buf.append(":\n  Superclass = ");
        buf.append(parent);
        buf.append("\n  Outer class = ");
        buf.append(outer);
        buf.append("\n  Constructors:\n");
        for (int i = 0; i < constrs.length; i++) {
            buf.append(constrs[i].toString());
            buf.append('\n');
        }
        System.err.println(buf.toString());
    }
    
    // TRECHO REFATORADO: Classe extraída para manipulação de array types
    private class ArrayTypeHandler {
        private final SourceType sourceType;
        
        public ArrayTypeHandler(SourceType sourceType) {
            this.sourceType = sourceType;
        }
        
        public Type getComponentType() {
            try {
                if (dim > 0) {
                    final String name = sourceType.getName();
                    return forName(name.substring(0, name.length() - 2));
                }
            } catch (ClassNotFoundException classEx) {
            }
            return null;
        }
        
        public Type getArrayType() {
            try {
                return forName(sourceType.getName() + "[]");
            } catch (ClassNotFoundException classEx) {
                return null;
            }
        }
    }
    
    // TRECHO REFATORADO: Classe extraída para manipulação de métodos
    private class MethodHandler {
        private final SourceType sourceType;
        
        public MethodHandler(SourceType sourceType) {
            this.sourceType = sourceType;
        }
        
        public Method[] getAllMethods() {
            ArrayList methods = collectInheritedMethods();
            collectLocalMethods(methods);
            collectInterfaceMethods(methods);
            
            final Method[] retMeths = new Method[methods.size()];
            methods.toArray(retMeths);
            return retMeths;
        }
        
        private ArrayList collectInheritedMethods() {
            final ArrayList methods = new ArrayList();
            
            try {
                final Type parent = sourceType.getSuperclass();
                final Method[] parentMeths = parent.getMethods();
                for (int i = 0; i < parentMeths.length; i++) {
                    methods.add(parentMeths[i]);
                }
            } catch (ClassNotFoundException classEx) {
            }
            
            return methods;
        }
        
        private void collectLocalMethods(ArrayList methods) {
            final Method[] meths = theType.symTable.getMeths();
            outer:
            for (int i = 0; i < meths.length; i++) {
                for (int j = 0; j < methods.size(); j++) {
                    if (meths[i].exactMatch((Method)methods.get(j))) {
                        methods.set(j, meths[i]);
                        continue outer;
                    }
                }
                methods.add(meths[i]);
            }
        }
        
        private void collectInterfaceMethods(ArrayList methods) {
            final int mods = getModifiers();
            if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)) {
                final Type[] interfaces = getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    final Method[] interMeths = interfaces[i].getMethods();
                    outer2:
                    for (int j = 0; j < interMeths.length; j++) {
                        for (int k = 0; k < methods.size(); k++) {
                            if (interMeths[j].exactMatch((Method)methods.get(k))) {
                                continue outer2;
                            }
                        }
                        methods.add(interMeths[j]);
                    }
                }
            }
        }
        
        public Method[] mergeOuterMethods(SourceType outer, String name, Type[] params, 
                                         Type caller, Method[] meths) {
            final Method[] outerMeths = outer.getMeths(name, params, caller);
            if (meths.length == 0) {
                meths = outerMeths;
            } else if (outerMeths.length > 0) {
                // Merge
                final Method[] newMeths = new Method[meths.length + outerMeths.length];
                System.arraycopy(meths, 0, newMeths, 0, meths.length);
                System.arraycopy(outerMeths, 0, newMeths, meths.length, outerMeths.length);
                meths = newMeths;
            }
            return meths;
        }
    }
}

/**
 * # Análise e Refatoração da Classe SourceType
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código da classe SourceType, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Extração de Método**: O método `isAssignableFrom` contém várias verificações condicionais complexas que podem ser extraídas para métodos mais específicos.
 * 2. **Extração de Método**: O método `getName` contém lógica condicional que pode ser extraída.
 * 3. **Extração de Método**: Há duplicação de código nas verificações de interfaces e classes, que pode ser extraída para métodos auxiliares.
 * 4. **Extração de Método**: O método `getMethods` é longo e complexo, com vários blocos lógicos distintos que podem ser extraídos.
 * 5. **Extração de Classe**: As funcionalidades relacionadas à manipulação de array types podem ser extraídas para uma classe separada.
 * 6. **Extração de Classe**: As operações relacionadas à manipulação de métodos podem ser extraídas para uma classe auxiliar.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classes**:
 *     - **ArrayTypeHandler**: Extraí uma classe interna `ArrayTypeHandler` para encapsular a lógica relacionada à manipulação de tipos de array. Esta refatoração segue o princípio de Responsabilidade Única (SRP) mencionado por Fowler, onde uma classe deve ter apenas um motivo para mudar. Os métodos `getComponentType()` e `getArrayType()` agora pertencem a esta classe.
 *     - **MethodHandler**: Extraí uma classe interna `MethodHandler` para gerenciar as operações relacionadas aos métodos. Esta classe agora encapsula a lógica de busca, coleta e combinação de métodos. Segundo Marco Tulio (Engenharia de Software Moderna), esta refatoração aumenta a coesão da classe original, pois transfere responsabilidades específicas para classes dedicadas.
 *
 * 2. **Extração de Métodos**:
 *     - Nos métodos longos como `isAssignableFrom()`, extraí submétodos para melhorar a legibilidade e compreensão do código. Fowler descreve essa técnica como crucial para melhorar a compreensibilidade e manutenibilidade.
 *     - O método `getMethods()` foi refatorado para delegar à classe `MethodHandler`, que por sua vez divide a responsabilidade em métodos menores: `collectInheritedMethods()`, `collectLocalMethods()` e `collectInterfaceMethods()`.
 *     - Extraí `findVariableTypeInHierarchy()` e `findVariableTypeInInterfaces()` do método `varType()` para melhor expressar o propósito de cada parte do código.
 *
 * 3. **Inicialização refatorada**:
 *     - Extraí um método `initializeConstructors()` para melhorar a legibilidade do construtor.
 *     - Extraí métodos como `initializeInterfaces()` e `initializeInnerClasses()` para encapsular a lógica de inicialização, tornando o código mais fácil de entender e manter.
 *
 * 4. **Formatação de nomes**:
 *     - Extraí um método `formatArrayName()` do método `getName()` para separar a lógica de formatação do array.
 *
 * ## 4) Resumo das alterações
 * 1. **Quantidade total de refatorações realizadas**: 16 refatorações.
 * 2. **Divisão por tipo**:
 *     - **Extração de Método**: 14 refatorações
 *         - initializeConstructors
 *         - isAssignableToArray
 *         - handleInterfaceAssignment
 *         - formatArrayName
 *         - initializeInterfaces
 *         - initializeInnerClasses
 *         - findBestConstructor
 *         - findVariableTypeInHierarchy
 *         - findVariableTypeInInterfaces
 *         - collectInheritedMethods
 *         - collectLocalMethods
 *         - collectInterfaceMethods
 *         - mergeOuterMethods
 *         - getAllMethods
 *
 *     - **Extração de Classe**: 2 refatorações
 *         - ArrayTypeHandler
 *         - MethodHandler
 *
 *
 */