
package jparse;

import java.util.ArrayList;
import java.util.HashMap;
import jparse.expr.VarAST;
import jparse.stmt.StatementAST;


public final class SymbolTable {

    final SymbolTable parent;
    private TypeAST type;
    private final HashMap varMap = new HashMap();
    private MethAST[] methods = new MethAST[0];
    private final HashMap labelMap = new HashMap();

    public SymbolTable() {
	parent = JavaAST.currSymTable;
    }

    void setEnclosingType(final TypeAST enclosingType) {
	type = enclosingType;
    }

    public void addVar(final VarAST ast) {
	varMap.put(ast.getName(), ast);
    }

    public VarAST getVar(final String name) {
	final Object ret = varMap.get(name);
	return (ret != null)
	    ? (VarAST)ret
	    : ((parent != null) ? parent.getVar(name) : null);
    }

    public void addMeth(final MethAST meth) {
	// Find an index where this entry can go in alphabetical order
	int low = 0;
	int high = methods.length - 1;
	while (low <= high) {
	    final int mid = (low + high) / 2;
	    final int compare = meth.compareTo(methods[mid]);
	    if (compare < 0) {
		high = mid - 1;
	    } else if (compare > 0) {
		low = mid + 1;
	    } else {
		low = mid;
		high = mid - 1;
	    }
	}

	// Now make a bigger array, and leave a gap for the new one
	final MethAST[] newMeths = new MethAST[methods.length + 1];
	System.arraycopy(methods, 0, newMeths, 0, low);
	newMeths[low] = meth;
	System.arraycopy(methods, low, newMeths, low+1, methods.length - low);
	methods = newMeths;
    }

    public Method getMeth(final String name, final Type[] params,
			  final Type caller) {
	// If the query is made on a subordinate symbol table, ask the parent
	if (type == null)
	    return parent.getMeth(name, params, caller);

	// Otherwise, get all matching methods
	Method[] matches = getMeths(name, params, caller);

	// If we didn't get a match, then check the interfaces we implement or
	// extend
	if (matches.length == 0) {
	    final Type[] interfaces = type.retrieveType().getInterfaces();
	    for (int i = 0; i < interfaces.length; i++) {
		final Method match =
		    interfaces[i].getMethod(name, params, caller);
		if (match != null) {
		    final Method[] newMatches = new Method[matches.length + 1];
		    System.arraycopy(matches, 0, newMatches, 0,
				     matches.length);
		    newMatches[matches.length] = match;
		    matches = newMatches;
		}
	    }
	}

	// Did we get a match?
	if (matches.length == 0) {
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

    public Method[] getMeths(final String name, final Type[] params,
			     final Type caller) {
	// Get all methods with the correct name and parameter types from the
	// supertypes
	Method[] matches;
	try {
	    final Type myType = type.retrieveType();
	    final Type superType = myType.isInterface()
		? Type.objectType
		: myType.getSuperclass();
	    matches = superType.getMeths(name, params, caller);
	} catch (ClassNotFoundException classEx) {
	    matches = new Method[0];
	}

	// Find an index where this method name might be stored
	int low = 0;
	int high = methods.length - 1;
	while (low <= high) {
	    final int mid = (low + high) / 2;
	    final int compare = name.compareTo(methods[mid].getName());
	    if (compare < 0) {
		high = mid - 1;
	    } else if (compare > 0) {
		low = mid + 1;
	    } else {
		low = mid;
		high = mid - 1;
	    }
	}

	// Go backwards until we find the first one
	int index;
	for (index = low; index >= 0 && index < methods.length &&
		 name.equals(methods[index].getName()); index--);

	// Now go forwards, adding all candidate methods to the pool
	for (int i = index + 1;
	     i < methods.length && name.equals(methods[i].getName()); i++){
	    if (methods[i].match(params, caller)) {
		final Method[] newMatches = new Method[matches.length + 1];
		System.arraycopy(matches, 0, newMatches, 0, matches.length);
		newMatches[matches.length] = methods[i];
		matches = newMatches;
	    }
	}

	return matches;
    }

    public Method[] getMeths() {
	return methods;
    }

    public void addLabel(final String label, final JavaAST stmt) {
	labelMap.put(label, stmt);
    }

    public StatementAST getLabel(final String label) {
	final Object ret = labelMap.get(label);
	return (ret != null)
	    ? (StatementAST)ret
	    : ((parent != null) ? parent.getLabel(label) : null);
    }

    public String toString() {
	final StringBuffer buf = new StringBuffer("Symbol Table:\n");
	if (!varMap.isEmpty()) {
	    buf.append("** Variables **\n");
	    buf.append(varMap.toString());
	    buf.append('\n');
	}
	if (methods.length > 0) {
	    buf.append("** Methods **\n");
	    for (int i = 0; i < methods.length; i++) {
		buf.append(methods[i].toString());
		buf.append('\n');
	    }
	}
	if (!labelMap.isEmpty()) {
	    buf.append("** Labels **\n");
	    buf.append(labelMap.toString());
	    buf.append('\n');
	}
	if (parent != null) {
	    buf.append(parent.toString());
	}
	return buf.toString();
    }
}
