package jparse;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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

	SourceType(final TypeAST root) {
		root.type = this;
		theType = root;
		file = root.topLevel;
		dim = 0;

		modifiers = root.modifiers.mods;

		if (root.constructors == null) {
			final TypeAST temp = TypeAST.currType;
			TypeAST.currType = root;
			constrs = new ConstrAST[] { new ConstrAST() };
			TypeAST.currType = temp;
		} else {
			constrs = root.constructors;
		}

		for (int i = 0; i < root.inner.length; i++) {
			root.inner[i].type.outer = this;
		}
	}

	public boolean isAssignableFrom(final Type type) {
		// TRECHO REFATORADO
		if (isExactlyEquivalent(type)) return true;
		if (isNullType(type)) return true;
		if (type.isPrimitive()) return false;
		if (type.isArray()) return checkArrayAssignable(type);
		if (isInterface()) return checkInterfaceAssignable(type);
		return superClassOf(type);
	}

	// TRECHO REFATORADO
	private boolean isExactlyEquivalent(final Type type) {
		return this == type;
	}

	// TRECHO REFATORADO
	private boolean isNullType(final Type type) {
		return type == null;
	}

	// TRECHO REFATORADO
	private boolean checkArrayAssignable(final Type type) {
		return (dim != 0) && getComponentType().isAssignableFrom(type.getComponentType());
	}

	// TRECHO REFATORADO
	private boolean checkInterfaceAssignable(final Type type) {
		return type.isInterface()
				? superInterfaceOf(type)
				: type.implementsInterface(this);
	}

	public Method[] getMethods() {
		// TRECHO REFATORADO
		return new MethodCollector(this).collectMethods();
	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isArray() {
		return dim != 0;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isInner() {
		return outer != null;
	}

	public String getName() {
		if (dim == 0)
			return theType.name;
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

	public int getModifiers() {
		return modifiers;
	}

	public Type getComponentType() {
		try {
			if (dim > 0) {
				final String name = getName();
				return forName(name.substring(0, name.length() - 2));
			}
		} catch (ClassNotFoundException classEx) {}
		return null;
	}

	public Type getDeclaringClass() {
		return outer;
	}

	public Type[] getInterfaces() {
		if (interfaces == null) {
			final String[] theInterfaces = theType.interfaces;
			interfaces = new Type[theInterfaces.length];
			for (int i = 0; i < theInterfaces.length; i++) {
				try {
					interfaces[i] = file.getType(theInterfaces[i]);
				} catch (ClassNotFoundException ex2) {}
			}
		}
		return interfaces;
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
}



// TRECHO REFATORADO - Classe extraída para responsabilidade única

public class MethodCollector {

	private final SourceType sourceType;

	public MethodCollector(SourceType sourceType) {
		this.sourceType = sourceType;
	}

	public Method[] collectMethods() {
		if (sourceType.dim != 0) {
			return new Method[0];
		}

		final List<Method> methods = new ArrayList<>();
		addSuperclassMethods(methods);
		addDeclaredMethods(methods);
		addInterfaceMethods(methods);

		return methods.toArray(new Method[0]);
	}

	private void addSuperclassMethods(List<Method> methods) {
		try {
			final Type parent = sourceType.getSuperclass();
			final Method[] parentMeths = parent.getMethods();
			for (Method m : parentMeths) {
				methods.add(m);
			}
		} catch (ClassNotFoundException ignored) {}
	}

	private void addDeclaredMethods(List<Method> methods) {
		final Method[] meths = sourceType.theType.symTable.getMeths();
		outer:
		for (Method m : meths) {
			for (int j = 0; j < methods.size(); j++) {
				if (m.exactMatch(methods.get(j))) {
					methods.set(j, m);
					continue outer;
				}
			}
			methods.add(m);
		}
	}

	private void addInterfaceMethods(List<Method> methods) {
		final int mods = sourceType.getModifiers();
		if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)) {
			final Type[] interfaces = sourceType.getInterfaces();
			for (Type inter : interfaces) {
				final Method[] interMeths = inter.getMethods();
				outer2:
				for (Method interMeth : interMeths) {
					for (Method existing : methods) {
						if (interMeth.exactMatch(existing)) {
							continue outer2;
						}
					}
					methods.add(interMeth);
				}
			}
		}
	}
}


/**
 * 1) Oportunidades de Refatoração Encontradas:
Extração de Métodos:

O método isAssignableFrom() possui múltiplas condições claras e independentes (Cases 1-6) que podem ser transformadas em métodos separados para aumentar a clareza.

A inicialização dos métodos internos em getMethods() poderia ser extraída para métodos separados.

Extração de Classe:

O método getMethods() possui uma lógica extensa, envolvendo manipulação complexa da lista de métodos (superclasse, métodos declarados, interfaces). Isso sugere uma classe especializada responsável exclusivamente por montar a lista final de métodos.

2) Classe refatorada com os devidos comentários:

3) Justificativa das Refatorações:
Extração de Métodos:

Cada condição complexa do método isAssignableFrom() foi isolada, conforme orientação de Fowler (2018), que defende métodos pequenos, específicos e claros, facilitando a leitura e manutenção do código.

Ao extrair essas condições para métodos independentes, fica explícito o que cada condição verifica, melhorando a legibilidade e organização do código.

Extração de Classe:

A lógica complexa de construção da lista de métodos em getMethods() foi transferida para a nova classe MethodCollector, conforme orientação de Marco Tulio (Engenharia de Software Moderna, cap. 9). Essa classe é especializada exclusivamente na tarefa de coletar métodos, aderindo ao princípio de responsabilidade única (SRP - Single Responsibility Principle), o que reduz a complexidade da classe original e melhora a modularidade.

4) Resumo das Alterações:
Quantidade total de refatorações realizadas: 6

Extração de Método: 4

Extração de Classe: 1

Atualização do método getMethods() para utilizar nova classe: 1
 */