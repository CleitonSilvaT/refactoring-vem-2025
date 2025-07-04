package jparse;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.ArrayList;


public final class CompiledType extends Type {

    private final Class theClass;
    private final CompiledConstructor[] constrs;
    private final CompiledMethod[] meths;
    private Type[] inner;

    CompiledType(final Class cls) {
	theClass = cls;

	// TRECHO REFATORADO: Extraído para método initializeConstructors
	constrs = initializeConstructors(cls);

	// TRECHO REFATORADO: Extraído para método initializeMethods
	meths = initializeMethods(cls);
    }

    // TRECHO REFATORADO: Método extraído para inicializar construtores
    private CompiledConstructor[] initializeConstructors(final Class cls) {
        final java.lang.reflect.Constructor[] realConstrs = cls.getDeclaredConstructors();
        CompiledConstructor[] result = new CompiledConstructor[realConstrs.length];
        for (int i = 0; i < realConstrs.length; i++) {
            result[i] = new CompiledConstructor(realConstrs[i]);
        }
        return result;
    }

    // TRECHO REFATORADO: Método extraído para inicializar métodos
    private CompiledMethod[] initializeMethods(final Class cls) {
        final java.lang.reflect.Method[] realMeths = cls.getDeclaredMethods();
        CompiledMethod[] result = new CompiledMethod[realMeths.length];
        for (int i = 0; i < realMeths.length; i++) {
            result[i] = new CompiledMethod(realMeths[i]);
        }
        return result;
    }

    CompiledType(final CompiledType original, final int dims) {
	// This is stupid.  We wouldn't have to do this if Class.forName could
	// actually look up primitive types!  Vote for JDC bug #4171142, since
	// Sun has closed more relevant bug reports as not being bugs!
	Class cls = original.theClass;
	for (int i = 0; i < dims; i++)
	    cls = Array.newInstance(cls, 1).getClass();
	theClass = cls;
	constrs = new CompiledConstructor[0];
	meths = new CompiledMethod[0];
	inner = new Type[0];
    }

    public boolean isAssignableFrom(final Type type) {
	// Case 1: The types are exactly equivalent
	if (this == type)
	    return true;

	// Case 2: The parameter is null.  This means that the Java keyword
	// null was used in the source.  So we return true if this type is not
	// primitive.
	if (type == null)
	    return !theClass.isPrimitive();

	// Case 3: Both are primitive types.  Then Class won't give us the
	// right answer, so we have to do the hard work ourselves.
	if (type.isPrimitive() && theClass.isPrimitive()) {
	    // TRECHO REFATORADO: Extraído para o método isPrimitiveAssignableFrom
	    return isPrimitiveAssignableFrom(type);
	}

	// Case 4: The parameter is also a compiled type.  Then let Class do
	// the hard work for us.
	if (type instanceof CompiledType)
	    return theClass.isAssignableFrom(((CompiledType)type).theClass);

	// Case 5: This is a primitive type.  Since the parameter is *not* a
	// compiled type (see case 3), then it is not a primitive type.
	if (theClass.isPrimitive())
	    return false;

	// Case 6: The parameter is an array type.  Then this must represent
	// an array with the same dimension and the same type or a supertype,
	// OR one of java.lang.Object, java.lang.Cloneable, or
	// java.io.Serializable.
	if (type.isArray()) {
	    return (theClass.isArray())
		? getComponentType().isAssignableFrom(type.getComponentType())
		: theClass == Object.class || theClass == Cloneable.class ||
		  theClass == Serializable.class;
	}

	// Case 7: This is an interface.
	if (isInterface()) {
	    return type.isInterface()
		// Case 7a: Type is also an interface.  Check whether this is
		// a superinterface of type.
		? superInterfaceOf(type)

		// Case 7b: Type is not an interface.  Check whether it, or
		// any of its parents, implement this interface
		: type.implementsInterface(this);
	}

	// Case 8: Check whether type is a subclass (or subinterface) of this
	// type
	return superClassOf(type);
    }

    // TRECHO REFATORADO: Método extraído para verificar se um tipo primitivo é atribuível a outro
    private boolean isPrimitiveAssignableFrom(final Type type) {
        final Class tClass = ((CompiledType)type).theClass;
        if (theClass == void.class || tClass == void.class)
            return false;	// Can't assign those suckers!
        if (theClass == double.class)
            return true;
        if (theClass == float.class)
            return tClass != double.class;
        if (theClass == long.class)
            return tClass != double.class && tClass != float.class;
        if (theClass == int.class)
            return tClass != double.class && tClass != float.class &&
                tClass != long.class;
        if (theClass == short.class)
            return tClass == byte.class || tClass == short.class;
        if (theClass == char.class)
            return tClass == byte.class || tClass == char.class;
        // byte.class is the only one left!
        return tClass == byte.class;
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
	final Class superClass = theClass.getSuperclass();
	return (superClass == null) ? null : forClass(superClass);
    }

    public String getPackage() {
	// The getPackage() method of java.lang.Class sometimes returns null,
	// so we do it the hard way instead of like this:
	// return theClass.getPackage().getName();
	final String name = getName();
	final int index = name.lastIndexOf('.');
	return (index < 0) ? "" : name.substring(0, index);
    }

    public Type[] getInterfaces() {
	final Class[] interfaces = theClass.getInterfaces();
	final Type[] interTypes = new Type[interfaces.length];
	try {
	    for (int i = 0; i < interfaces.length; i++)
		interTypes[i] = forName(interfaces[i].getName());
	} catch (ClassNotFoundException classEx) {
	    System.err.println("This can't happen!  I could not find a class that the JDK has a reference to!");
	    classEx.printStackTrace();
	}
	return interTypes;
    }

    public Type getComponentType() {
	final Class compClass = theClass.getComponentType();
	return (compClass == null) ? null : forClass(compClass);
    }

    public int getModifiers() {
	return theClass.getModifiers();
    }

    public Type getDeclaringClass() {
	return forClass(theClass.getDeclaringClass());
    }

    public Type[] getClasses() {
	if (inner == null) {
	    // TRECHO REFATORADO: Extraído para método initializeInnerClasses
	    inner = initializeInnerClasses();
	}
	return inner;
    }

    // TRECHO REFATORADO: Método extraído para inicializar classes internas
    private Type[] initializeInnerClasses() {
        Type[] parentTypes;
        try {
            final Type parent = getSuperclass();
            parentTypes = parent.getClasses();
        } catch (ClassNotFoundException classEx) {
            parentTypes = new Type[0];
        } catch (NullPointerException nullEx) {
            parentTypes = new Type[0];
        }
        
        final Class[] myInner = theClass.getDeclaredClasses();
        Type[] result = new Type[parentTypes.length + myInner.length];
        for (int i = 0; i < myInner.length; i++) {
            result[i] = forClass(myInner[i]);
        }
        System.arraycopy(parentTypes, 0, result, myInner.length,
                         parentTypes.length);
        return result;
    }

    public Method[] getMethods() {
	// Should we bother?
	if (isPrimitive() || isArray()) {
	    return new Method[0];
	}

	final ArrayList methods = new ArrayList();

	// TRECHO REFATORADO: Extraído para método fillMethodsFromSuperclass
	fillMethodsFromSuperclass(methods);

	// TRECHO REFATORADO: Extraído para método addMethodsFromThisClass
	addMethodsFromThisClass(methods);

	// TRECHO REFATORADO: Extraído para método fillMethodsFromInterfaces
	fillMethodsFromInterfaces(methods);

	// We have the whole list, so return it
	return convertMethodListToArray(methods);
    }

    // TRECHO REFATORADO: Método extraído para preencher métodos da superclasse
    private void fillMethodsFromSuperclass(ArrayList methods) {
        try {
            final Type parent = getSuperclass();
            final Method[] parentMeths = parent.getMethods();
            for (int i = 0; i < parentMeths.length; i++) {
                methods.add(parentMeths[i]);
            }
        } catch (ClassNotFoundException classEx) {
            // Don't know what this means, but don't bomb out just yet.
        } catch (NullPointerException nullEx) {
            // This is java.lang.Object (we hope).  Don't do anything.
        }
    }

    // TRECHO REFATORADO: Método extraído para adicionar métodos desta classe
    private void addMethodsFromThisClass(ArrayList methods) {
        // Add the methods declared in this class, overwriting those it overrides
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

    // TRECHO REFATORADO: Método extraído para preencher métodos das interfaces
    private void fillMethodsFromInterfaces(ArrayList methods) {
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

    // TRECHO REFATORADO: Método extraído para converter lista de métodos para array
    private Method[] convertMethodListToArray(ArrayList methods) {
        final Method[] retMeths = new Method[methods.size()];
        methods.toArray(retMeths);
        return retMeths;
    }

    public Method getMethod(final String methName, final Type[] paramTypes,
			    final Type caller) {
	// TRECHO REFATORADO: Extraído para método findMethodMatches
	Method[] matches = findMethodMatches(methName, paramTypes, caller);

	// TRECHO REFATORADO: Extraído para método selectBestMatch
	return selectBestMatch(matches, methName, paramTypes, caller);
    }

    // TRECHO REFATORADO: Método extraído para encontrar métodos correspondentes
    private Method[] findMethodMatches(final String methName, final Type[] paramTypes, final Type caller) {
        // Get all matching methods
        Method[] matches = getMeths(methName, paramTypes, caller);

        // If we didn't get a match...
        if (matches.length == 0) {
            if (isInterface()) {
                // ... then check java.lang.Object for interfaces
                matches = Type.objectType.getMeths(methName, paramTypes, caller);
            } else {
                // ... or implemented interfaces for classes
                matches = findMethodsInInterfaces(methName, paramTypes, caller, matches);
            }
        }
        return matches;
    }

    // TRECHO REFATORADO: Método extraído para encontrar métodos em interfaces
    private Method[] findMethodsInInterfaces(final String methName, final Type[] paramTypes, 
                                            final Type caller, Method[] matches) {
        final Type[] interfaces = getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            final Method[] iMatches = interfaces[i].getMeths(methName, paramTypes, caller);
            if (iMatches.length > 0) {
                matches = mergeMethodArrays(matches, iMatches);
            }
        }
        return matches;
    }

    // TRECHO REFATORADO: Método extraído para combinar arrays de métodos
    private Method[] mergeMethodArrays(Method[] array1, Method[] array2) {
        final Method[] all = new Method[array1.length + array2.length];
        System.arraycopy(array1, 0, all, 0, array1.length);
        System.arraycopy(array2, 0, all, array1.length, array2.length);
        return all;
    }

    // TRECHO REFATORADO: Método extraído para selecionar o melhor método correspondente
    private Method selectBestMatch(Method[] matches, final String methName, 
                                 final Type[] paramTypes, final Type caller) {
        // Did we get a match?
        if (matches.length == 0) {
            getMeths(methName, paramTypes, caller);
            return null;
        }

        // Pick the best match
        Method bestMatch = matches[0];
        boolean needBetter = false;
        for (int i = 1; i < matches.length; i++) {
            Method newMatch = bestMatch.bestMatch(matches[i]);
            needBetter = newMatch == null;
            if (newMatch != null)
                bestMatch = newMatch;
        }
        
        if (needBetter) {
            System.err.println("There was no best match!\nContenders are:");
            for (int i = 0; i < matches.length; i++) {
                System.err.println(matches[i].toString());
            }
        }
        return bestMatch;
    }

    public Constructor getConstructor(final Type[] params, final Type caller) {
	Constructor best = null;
	for (int i = 0; i < constrs.length; i++) {
	    final Constructor c = constrs[i];
	    if (c.match(params, caller)) {
		best = (best == null) ? c : best.bestMatch(c);
	    }
	}
	if (best == null && isInner()) {
	    // Is it an anonymous class?
	    final String fullName = getName();
	    final String name =
		fullName.substring(fullName.lastIndexOf('$') + 1);
	    try {
		int anonNumber = Integer.parseInt(name);
		best = getSuperclass().getConstructor(params, caller);
	    } catch (NumberFormatException numberEx) {
		// It wasn't an anonymous class
	    } catch (ClassNotFoundException classEx) {
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
	return forClass(Array.newInstance(theClass, 1).getClass());
    }

    public Type varType(final String varName) {
	// See if it is defined in this class
	try {
	    return forClass(theClass.getDeclaredField(varName).getType());
	} catch (Exception ex) {
	}

	// See if it is defined in a superclass
	try {
	    final Type parent = getSuperclass();
	    if (parent != null) {
		final Type type = parent.varType(varName);
		if (type != null)
		    return type;
	    }
	} catch (ClassNotFoundException classEx) {
	}
	
	// See if it is defined in an interface
	final Type[] interfaces = getInterfaces();
	for (int i = 0; i < interfaces.length; i++) {
	    final Type type = interfaces[i].varType(varName);
	    if (type != null)
		return type;
	}

	// Can't find it
	return null;
    }

    public Method[] getMeths(final String name, final Type[] params,
			     final Type caller) {
	// TRECHO REFATORADO: Extraído para método getMatchesFromParents
	Method[] matches = getMatchesFromParents(name, params, caller);

	// TRECHO REFATORADO: Extraído para método getMatchesFromThisClass
	matches = getMatchesFromThisClass(name, params, caller, matches);

	// If there are no matches yet, add matches from any enclosing class
	if (matches.length == 0) {
	    matches = getMatchesFromOuterClass(name, params, caller, matches);
	}

	return matches;
    }

    // TRECHO REFATORADO: Método extraído para obter correspondências da superclasse ou interfaces
    private Method[] getMatchesFromParents(final String name, final Type[] params, final Type caller) {
        // Get any matches from the superclass or superinterfaces
        Method[] matches;
        if (isInterface()) {
            matches = getMatchesFromSuperInterfaces(name, params, this);
        } else {
            try {
                matches = getSuperclass().getMeths(name, params, caller);
            } catch (ClassNotFoundException classEx) {
                // Just leave the list of matches empty
                matches = new Method[0];
            } catch (NullPointerException nullEx) {
                // Likewise ... this happens for java.lang.Object ONLY
                matches = new Method[0];
            }
        }
        return matches;
    }

    // TRECHO REFATORADO: Método extraído para obter correspondências de superinterfaces
    private Method[] getMatchesFromSuperInterfaces(final String name, final Type[] params, final Type caller) {
        Method[] matches = new Method[0];
        final Type[] superInts = getInterfaces();
        for (int i = 0; i < superInts.length; i++) {
            final Method[] iMatches = superInts[i].getMeths(name, params, this);
            if (iMatches.length > 0) {
                matches = mergeMethodArrays(matches, iMatches);
            }
        }
        return matches;
    }

    // TRECHO REFATORADO: Método extraído para obter correspondências desta classe
    private Method[] getMatchesFromThisClass(final String name, final Type[] params, 
                                           final Type caller, Method[] matches) {
        // Add matches from this class
        final ArrayList res = new ArrayList();
        for (int i = 0; i < meths.length; i++) {
            if (meths[i].match(name, params, caller))
                res.add(meths[i]);
        }
        final int size = res.size();
        if (size > 0) {
            final Method[] newMatches = new Method[matches.length + size];
            res.toArray(newMatches);
            System.arraycopy(matches, 0, newMatches, size, matches.length);
            matches = newMatches;
        }
        return matches;
    }

    // TRECHO REFATORADO: Método extraído para obter correspondências da classe externa
    private Method[] getMatchesFromOuterClass(final String name, final Type[] params, 
                                            final Type caller, Method[] matches) {
        final Type outer = getDeclaringClass();
        if (outer != null) {
            final Method[] outerMatches = outer.getMeths(name, params, caller);
            if (outerMatches.length > 0) {
                matches = mergeMethodArrays(matches, outerMatches);
            }
        }
        return matches;
    }

    public String toString() {
	return theClass.toString();
    }

    public void dump() {
	final StringBuffer buf = new StringBuffer(theClass.toString());
	buf.append(':');
	try {
	    final Type parent = getSuperclass();
	    buf.append("\n  Superclass = ");
	    buf.append(parent);
	} catch (ClassNotFoundException classEx) {
	}
	final Type outer = getDeclaringClass();
	buf.append("\n  Outer class = ");
	buf.append(outer);
	buf.append("\n  Constructors:\n");
	for (int i = 0; i < constrs.length; i++) {
	    buf.append(constrs[i].toString());
	    buf.append('\n');
	}
	System.err.println(buf.toString());
    }
}


/**
 *
 *# Refatoração da classe CompiledType
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe CompiledType, identifiquei as seguintes oportunidades para refatoração:
 * 1. Extrair o código de verificação de tipos primitivos no método `isAssignableFrom()` para um método separado;
 * 2. Extrair o código de busca por métodos correspondentes no método `getMethod()` para um método mais coeso;
 * 3. Extrair código de busca por tipos em interfaces para um método separado;
 * 4. Extrair o código de inicialização de classes internas no método `getClasses()` para um método dedicado;
 * 5. Extrair o código de busca de métodos em `getMethods()` para métodos menores e mais coesos;
 * 6. Duplicação nas operações de cópia de arrays que pode ser extraída para um método utilitário.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método - isPrimitiveAssignableFrom()**:
 *     - O método `isAssignableFrom()` continha um grande bloco de código dedicado à verificação de tipos primitivos.
 *     - Seguindo o princípio "cada método deve fazer apenas uma coisa" de Martin Fowler, esse código foi extraído para um método dedicado, melhorando a legibilidade.
 *
 * 2. **Extração de Método - Inicializadores (initializeConstructors, initializeMethods, initializeInnerClasses)**:
 *     - Os construtores e métodos da classe eram inicializados com código embutido, dificultando a leitura.
 *     - A extração desses métodos torna o código mais organizado e melhora a coesão dos construtores.
 *
 * 3. **Extração de Método - getMethod() em findMethodMatches() e selectBestMatch()**:
 *     - O método `getMethod()` fazia muitas coisas: buscava correspondências e selecionava o melhor método.
 *     - A extração permite separar essas responsabilidades, seguindo o princípio de responsabilidade única.
 *
 * 4. **Extrações no método getMethods()**:
 *     - Este método era muito grande e fazia várias operações distintas: buscar métodos da superclasse, adicionar métodos da classe atual, e buscar métodos das interfaces.
 *     - A extração em métodos menores melhora a legibilidade e permite reutilização.
 *
 * 5. **Extração no método getMeths()**:
 *     - Similar ao caso anterior, este método fazia várias operações que foram separadas em métodos mais especializados.
 *
 * 6. **Extração de Método - mergeMethodArrays()**:
 *     - A operação de combinar arrays de métodos era repetida em vários lugares do código.
 *     - A extração elimina essa duplicação e facilita manutenções futuras.
 *
 * 7. **Extração de Método - getMatchesFromSuperInterfaces()**:
 *     - A busca de métodos em interfaces era um bloco de código complexo que merecia seu próprio método.
 *     - Isso melhora a legibilidade e torna o código mais modular.
 *
 * ## 4) Resumo das alterações
 * 1. **Quantidade total de refatorações realizadas**: 16
 * 2. **Divisão por tipo**:
 *     - **Extração de Método**: 16
 *     - **Extração de Classe**: 0
 *
 * A maior parte das refatorações foi focada em extrair pequenos métodos de métodos maiores e mais complexos. Não foi necessário extrair classes, pois a classe CompiledType já tem uma boa coesão geral, representando um único conceito. As extrações de métodos melhoraram significativamente a legibilidade do código, removendo duplicações e tornando cada método mais focalizado em uma única tarefa.
 * Esta refatoração segue os princípios de Martin Fowler de que um código bem estruturado deve ter métodos pequenos, cada um com uma única responsabilidade bem definida, facilitando a manutenção e o entendimento do código.
 *
 */