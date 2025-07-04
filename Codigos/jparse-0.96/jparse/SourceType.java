
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

	SourceType(final SourceType original, final int dims) {
		final TypeAST root = original.theType;
		theType = root;
		file = root.topLevel;
		modifiers = root.modifiers.mods;
		dim = dims;
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
		// Case 1: The types are exactly equivalent
		if (this == type)
			return true;

		// Case 2: The parameter is null. This means that the Java keyword
		// null was used in the source. So we return true since a source type
		// must be either a class or an interface.
		if (type == null)
			return true;

		// Case 3: The parameter is a primitive type. Since this is *not* a
		// primitive type, return false.
		if (type.isPrimitive())
			return false;

		// Case 4: The parameter is an array type. Then this must represent
		// an array with the same dimension and the same type or a supertype.
		if (type.isArray()) {
			return (dim != 0)
					? getComponentType().isAssignableFrom(type.getComponentType())
					: false;
		}

		// Case 5: This is an interface.
		if (isInterface()) {
			return type.isInterface()
					// Case 5a: Type is also an interface. Check whether this is
					// a superinterface of type.
					? superInterfaceOf(type)

					// Case 5b: Type is not an interface. Check whether it
					// implements this interface
					: type.implementsInterface(this);
		}

		// Case 6: Check whether type is a subclass (or subinterface) of this
		// type
		return superClassOf(type);

	}

	public boolean isInterface() {
		return Modifier.isInterface(modifiers);
	}

	public boolean isArray() {
		return dim != 0;
	}

	public boolean isPrimitive() {
		return false; // Can't have a source file for a primitive type!
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
			// Get the superclass
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
			final String[] theInterfaces = theType.interfaces;
			interfaces = new Type[theInterfaces.length];
			for (int i = 0; i < theInterfaces.length; i++) {
				try {
					interfaces[i] = file.getType(theInterfaces[i]);
				} catch (ClassNotFoundException ex2) {
				}
			}
		}
		return interfaces;
	}

	public Type getComponentType() {
		try {
			if (dim > 0) {
				final String name = getName();
				return forName(name.substring(0, name.length() - 2));
			}
		} catch (ClassNotFoundException classEx) {
		}
		return null;
	}

	public int getModifiers() {
		return modifiers;
	}

	public Type getDeclaringClass() {
		return outer;
	}

	public Type[] getClasses() {
		if (inner == null) {
			Type[] parentTypes;
			try {
				parentTypes = getSuperclass().getClasses();
			} catch (ClassNotFoundException classEx) {
				parentTypes = new Type[0];
			}

			final TypeAST[] myInner = theType.inner;
			inner = new Type[parentTypes.length + myInner.length];
			for (int i = 0; i < myInner.length; i++) {
				inner[i] = myInner[i].retrieveType();
			}
			System.arraycopy(parentTypes, 0, inner, myInner.length,
					parentTypes.length);

			for (int i = 0; i < myInner.length; i++) {
				inner = mergeTypeLists(inner,
						myInner[i].retrieveType().getClasses());
			}
		}
		return inner;
	}

	public Method[] getMethods() {
		// Should we bother?
		if (dim != 0) {
			return new Method[0];
		}

		// All of the methods go here
		final ArrayList methods = new ArrayList();

		// Get the superclass methods
		try {
			final Type parent = getSuperclass();
			final Method[] parentMeths = parent.getMethods();
			for (int i = 0; i < parentMeths.length; i++) {
				methods.add(parentMeths[i]);
			}
		} catch (ClassNotFoundException classEx) {
			// This is java.lang.Object (we hope). Don't do anything.
		}

		// Add the methods declared in this class, overwriting those it
		// overrides
		final Method[] meths = theType.symTable.getMeths();
		outer: for (int i = 0; i < meths.length; i++) {
			for (int j = 0; j < methods.size(); j++) {
				if (meths[i].exactMatch((Method) methods.get(j))) {
					methods.set(j, meths[i]);
					continue outer;
				}
			}
			methods.add(meths[i]);
		}

		// Add abstract methods declared in interfaces, if not implemented in
		// this type. That means we only have to do this if this type is
		// abstract or an interface, since otherwise it won't compile! If
		// anything overrides an interface method, just forget it.
		final int mods = getModifiers();
		if (Modifier.isAbstract(mods) || Modifier.isInterface(mods)) {
			final Type[] interfaces = getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				final Method[] interMeths = interfaces[i].getMethods();
				outer2: for (int j = 0; j < interMeths.length; j++) {
					for (int k = 0; k < methods.size(); k++) {
						if (interMeths[j].exactMatch((Method) methods.get(k))) {
							continue outer2;
						}
					}
					methods.add(interMeths[j]);
				}
			}
		}

		// We have the whole list, so return it
		final Method[] retMeths = new Method[methods.size()];
		methods.toArray(retMeths);
		return retMeths;
	}

	public Method getMethod(final String methName, final Type[] paramTypes,
			final Type caller) {
		final Method meth = theType.symTable.getMeth(methName, paramTypes, this);
		return (meth != null || outer == null)
				? meth
				: outer.getMethod(methName, paramTypes, caller);
	}

	public Constructor getConstructor(final Type[] params, final Type caller) {
		Constructor best = null;
		for (int i = 0; i < constrs.length; i++) {
			final Constructor c = constrs[i];
			if (c.match(params, caller)) {
				best = (best == null) ? c : best.bestMatch(c);
			}
		}
		if (best == null && anonymous) {
			try {
				best = getSuperclass().getConstructor(params, caller);
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
		try {
			return forName(getName() + "[]");
		} catch (ClassNotFoundException classEx) {
			return null;
		}
	}

	public Type varType(final String varName) {

		final VarAST var = theType.symTable.getVar(varName);
		if (var != null)
			return var.retrieveType();

		try {
			if (getSuperclass() != null) {
				final Type type = parent.varType(varName);
				if (type != null)
					return type;
			}
		} catch (ClassNotFoundException classEx) {
		}

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
			final Method[] outerMeths = outer.getMeths(name, params, caller);
			if (meths.length == 0) {
				meths = outerMeths;
			} else if (outerMeths.length > 0) {
				// Merge
				final Method[] newMeths = new Method[meths.length + outerMeths.length];
				System.arraycopy(meths, 0, newMeths, 0, meths.length);
				System.arraycopy(outerMeths, 0, newMeths, meths.length,
						outerMeths.length);
				meths = newMeths;
			}
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
}
