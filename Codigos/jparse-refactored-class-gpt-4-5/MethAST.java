import java.lang.reflect.Modifier;
import java.util.ArrayList;

import antlr.collections.AST;
import jparse.JavaAST;
import jparse.Method;
import jparse.ModifierAST;
import jparse.ParameterAST;
import jparse.expr.IdentifierAST;
import jparse.stmt.CompoundAST;

public final class MethAST extends JavaAST implements JavaTokenTypes, Method {

    private final ModifierAST modifiers;
    private final jparse.expr.TypeAST returnName;
    private final JavaAST returnBrackets;
    private Type returnType;
    private IdentifierAST methodName;
    private final ParameterAST[] paramNames;
    private Type[] paramTypes;
    private final IdentifierAST[] exceptNames;
    private Type[] exceptions;
    private final CompoundAST body;

    MethAST(final ModifierAST mods, final jparse.expr.TypeAST retType,
            final IdentifierAST name, final JavaAST parameters,
            final JavaAST brackets, final JavaAST exceptions,
            final CompoundAST block) {
        setType(METHOD_DEF);
        modifiers = mods;
        returnName = retType;
        methodName = name;

        // TRECHO REFATORADO: extração de métodos para clareza
        paramNames = extrairParametros(parameters);
        returnBrackets = brackets;
        exceptNames = extrairExcecoes(exceptions);
        body = block;
        
        registrarMetodo();
    }

    private ParameterAST[] extrairParametros(JavaAST parameters) {
        final ArrayList<AST> pTypes = extrairListaFiltrada(parameters);
        return pTypes.toArray(new ParameterAST[0]);
    }

    private IdentifierAST[] extrairExcecoes(JavaAST exceptions) {
        if (exceptions == null) {
            return new IdentifierAST[0];
        }
        final ArrayList<AST> eTypes = extrairListaFiltrada(exceptions);
        return eTypes.toArray(new IdentifierAST[0]);
    }

    private ArrayList<AST> extrairListaFiltrada(JavaAST lista) {
        final ArrayList<AST> tipos = new ArrayList<>();
        for (AST item = lista.getFirstChild(); item != null; item = item.getNextSibling()) {
            tipos.add(item);
            item = item.getNextSibling(); // pula vírgula
            if (item == null) {
                break;
            }
        }
        return tipos;
    }

    private void registrarMetodo() {
        symTable.addMeth(this);
    }

    public boolean isAccessible(final Type caller) {
        final int mod = getModifiers();
        final Type declaringClass = getDeclaringClass();

        // TRECHO REFATORADO: extração dos métodos auxiliares de checagem
        if (Modifier.isPublic(mod)) return true;
        if (Modifier.isProtected(mod)) return isProtectedAccessible(caller, declaringClass);
        if (Modifier.isPrivate(mod)) return isPrivateAccessible(caller, declaringClass);

        return isPackageAccessible(caller, declaringClass);
    }

    private boolean isProtectedAccessible(Type caller, Type declaringClass) {
        return declaringClass.getPackage().equals(caller.getPackage()) ||
                declaringClass.superClassOf(caller);
    }

    private boolean isPrivateAccessible(Type caller, Type declaringClass) {
        for (Type t = caller; t != null; t = t.getDeclaringClass()) {
            if (t == declaringClass) return true;
        }
        return false;
    }

    private boolean isPackageAccessible(Type caller, Type declaringClass) {
        return declaringClass.getPackage().equals(caller.getPackage());
    }

    public void parseComplete() {
	context.isField = false;
	for (int i = 0; i < paramNames.length; i++) {
	    paramNames[i].parseComplete();
	}
	if (body != null) {
	    context.nextStmt = null;
	    body.parseComplete();
	}
	context.isField = true;
    }

    public Type getDeclaringClass() {
	return typeAST.retrieveType();
    }

    public String getName() {
	return methodName.getName();
    }

    public int getModifiers() {
	return modifiers.mods;
    }

    public Type getReturnType() {
	if (returnType == null) {
	    if (returnBrackets == null) {
		returnType = returnName.retrieveType();
	    } else {
		final StringBuffer buf =
		    new StringBuffer(returnName.getName());
		for (AST b = returnBrackets;
		     b != null && b.getType() == ARRAY_DECLARATOR;
		     b = b.getNextSibling().getNextSibling())
		    buf.append("[]");
		try {
		    returnType = Type.forName(buf.toString());
		} catch (ClassNotFoundException classEx) {
		}
	    }
	}
	return returnType;
    }

    public ParameterAST[] getParameters() {
	return paramNames;
    }

    public Type[] getParameterTypes() {
	if (paramTypes == null) {
	    paramTypes = new Type[paramNames.length];
	    for (int i = 0; i < paramNames.length; i++) {
		paramTypes[i] = paramNames[i].getParamName().retrieveType();
	    }
	}
	return paramTypes;
    }

    public final Type[] getExceptionTypes() {
	if (exceptions == null)
	    exceptions = computeExceptions();
	return exceptions;
    }


    protected Type[] computeExceptions() {
	final Type[] exceptTypes = new Type[exceptNames.length];
	for (int i = 0; i < exceptNames.length; i++) {
	    try {
		exceptTypes[i] = topLevel.getType(exceptNames[i].getName());
	    } catch (ClassNotFoundException classEx) {
	    }
	}
	return exceptTypes;
    }

    public boolean isAccessible(final Type caller) {
	final int mod = getModifiers();
	if (Modifier.isPublic(mod)) {
	    return true;
	}

	final Type myType = getDeclaringClass();
	if (Modifier.isProtected(mod)) {
	    return myType.getPackage().equals(caller.getPackage()) ||
		myType.superClassOf(caller);
	}

	if (Modifier.isPrivate(mod)) {
	    for (Type t = caller; t != null; t = t.getDeclaringClass()) {
		if (t == myType) {
		    return true;
		}
	    }
	    return false;
	}

	return myType.getPackage().equals(caller.getPackage());
    }

    public CompoundAST getBody() {
	return body;
    }

    public boolean match(final String methName, final Type[] params,
			 final Type caller) {
	return getName().equals(methName) ? match(params, caller) : false;
    }

    public boolean match(final Type[] params, final Type caller) {
	final Type[] formalParams = getParameterTypes();
	if (params.length != formalParams.length) {
	    return false;
	}

	for (int i = 0; i < params.length; i++) {
	    if (!formalParams[i].isAssignableFrom(params[i])) {
		return false;
	    }
	}

	return isAccessible(caller);
    }

    public Method bestMatch(final Method meth) {
	final Type[] parms1 = getParameterTypes();
	final Type[] parms2 = meth.getParameterTypes();

	int comp = 0;
	for (int i = 0; i < parms1.length; i++) {
	    final boolean assignToMe  = parms1[i].isAssignableFrom(parms2[i]);
	    final boolean assignOther = parms2[i].isAssignableFrom(parms1[i]);
	    if (assignToMe && !assignOther) {
		if (comp == -1)
		    return null;
		comp = 1;
	    } else if (!assignToMe && assignOther) {
		if (comp == 1)
		    return null;
		comp = -1;
	    }
	}

	if (comp == -1)
	    return this;
	if (comp == 1)
	    return meth;

	boolean sameParms = true;
	for (int i = 0; i < parms1.length; i++) {
	    if (parms1[i] != parms2[i])
		sameParms = false;
	}
	if (sameParms) {
	    final Type type1 = getDeclaringClass();
	    final Type type2 = meth.getDeclaringClass();
	    if (type1.isAssignableFrom(type2))
		return meth;
	    else if (type2.isAssignableFrom(type1))
		return this;
	}

	final Type retType1 = getReturnType();
	final Type retType2 = meth.getReturnType();
	if (retType1.isAssignableFrom(retType2))
	    return meth;
	else if (retType2.isAssignableFrom(retType1))
	    return this;

	if (modifiers.isAbstract())
	    return meth;
	if (Modifier.isAbstract(meth.getModifiers()))
	    return this;

	return null;
    }

    public boolean exactMatch(Method meth) {
	if (!getName().equals(meth.getName())) {
	    return false;
	}

	final Type[] myParams = getParameterTypes();
	final Type[] methParams = meth.getParameterTypes();
	if (myParams.length != methParams.length) {
	    return false;
	}

	for (int i = 0; i < myParams.length; i++) {
	    if (myParams[i] != methParams[i]) {
		return false;
	    }
	}

	return true;
    }

    public int compareTo(final MethAST meth) {
	return methodName.compareTo(meth.methodName);
    }

    public String toString() {
	final StringBuffer buf = new StringBuffer();
	final int mods = getModifiers();
	if (mods != 0) {
	    buf.append(Modifier.toString(mods));
	    buf.append(' ');
	}
	buf.append(getReturnType().getName());
	buf.append(' ');
	buf.append(typeAST.name);
	buf.append('.');
	buf.append(getName());
	buf.append('(');
	final Type[] params = getParameterTypes();
	if (params.length > 0) {
	    for (int i = 0; i < params.length - 1; i++) {
		buf.append(params[i].getName());
		buf.append(',');
	    }
	    buf.append(params[params.length - 1].getName());
	}
	buf.append(')');
	final Type[] exceptions = getExceptionTypes();
	if (exceptions.length > 0) {
	    buf.append(" throws ");
	    for (int i = 0; i < exceptions.length - 1; i++) {
		buf.append(exceptions[i].getName());
		buf.append(',');
	    }
	    buf.append(exceptions[exceptions.length - 1].getName());
	}
	return buf.toString();
    }
}



/**
 * 1) Oportunidades de Refatoração Encontradas:
Método construtor muito longo e com diversas responsabilidades:

O construtor realiza múltiplas tarefas: processamento dos parâmetros, processamento das exceções e registro do método na tabela de símbolos. Cada uma dessas atividades poderia ser claramente separada em métodos menores, melhorando a clareza e manutenibilidade.

Duplicação de código:

O processamento das listas de parâmetros e exceções tem uma lógica semelhante, resultando em uma duplicação clara que pode ser reduzida através de um método auxiliar.

Lógica de acessibilidade (método isAccessible) complexa:

O método contém vários níveis de decisão (público, protegido, privado, pacote). Extraí-lo pode simplificar a lógica, separando claramente cada checagem.

2) Classe Refatorada com Comentários:

3) Justificativa das Refatorações:
Extração de Métodos (extrairParametros, extrairExcecoes):

Foram aplicadas para reduzir a complexidade do construtor. Seguindo o princípio de Fowler (2018), métodos menores e com responsabilidades únicas são mais fáceis de compreender e manter.

Remoção de Duplicação (extrairListaFiltrada):

Criação de um método auxiliar que elimina a repetição quase idêntica na lógica dos métodos de extração de parâmetros e exceções, aplicando diretamente o princípio DRY (Don't Repeat Yourself), citado por Marco Tulio em Engenharia de Software Moderna.

Extração de Métodos de Checagem (isProtectedAccessible, isPrivateAccessible, isPackageAccessible):

Decompõe a lógica de visibilidade em métodos individuais, permitindo fácil manutenção e entendimento. Este procedimento é defendido por Fowler (2018) ao destacar que métodos pequenos com uma única tarefa aumentam a legibilidade e facilitam testes unitários.

4) Resumo das Alterações Realizadas:
Quantidade total de refatorações realizadas: 6

Divisão por tipo:

Extração de Método: 6 refatorações

extrairParametros

extrairExcecoes

extrairListaFiltrada (para remoção de duplicação)

registrarMetodo

isProtectedAccessible

isPrivateAccessible

isPackageAccessible

Extração de Classe: 0 refatorações (não houve necessidade clara neste contexto)
 */