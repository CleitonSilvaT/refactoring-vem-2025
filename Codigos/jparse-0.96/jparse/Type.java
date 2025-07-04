package jparse;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamHiddenTokenFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Type {

    public static final CompiledType booleanType =
	new CompiledType(boolean.class);
    public static final CompiledType byteType = new CompiledType(byte.class);
    public static final CompiledType charType = new CompiledType(char.class);
    public static final CompiledType doubleType =
	new CompiledType(double.class);
    public static final CompiledType floatType = new CompiledType(float.class);
    public static final CompiledType intType = new CompiledType(int.class);
    public static final CompiledType longType = new CompiledType(long.class);
    public static final CompiledType shortType = new CompiledType(short.class);
    public static final CompiledType voidType = new CompiledType(void.class);
    public static final CompiledType objectType =
	new CompiledType(Object.class);
    public static final CompiledType stringType =
	new CompiledType(String.class);
    protected static final HashMap map = new HashMap();

    static {
	map.put("boolean",	    booleanType);
	map.put("byte",		    byteType   );
	map.put("char",		    charType   );
	map.put("double",	    doubleType );
	map.put("float",	    floatType  );
	map.put("int",		    intType    );
	map.put("long",		    longType   );
	map.put("short",	    shortType  );
	map.put("void",		    voidType   );
	map.put("java.lang.Object", objectType );
	map.put("java.lang.String", stringType );
    }

    protected static final HashMap pkgMap = new HashMap();
    private static final HashMap parsedMap = new HashMap();
    private static final String[] classPath;

    static {
	final ArrayList paths = new ArrayList();
	final char sep = File.pathSeparatorChar;

	explodeString(System.getProperty("env.class.path"), sep, paths);
	explodeString(System.getProperty("sun.boot.class.path"), sep, paths);
	explodeString(System.getProperty("java.class.path"), sep, paths);
	explodeString(System.getProperty("java.ext.dirs"), sep, paths);

	classPath = new String[paths.size()];
	paths.toArray(classPath);
    }

    private static void explodeString(final String s, final char c,
				      final ArrayList list) {
	if (s == null || s.length() == 0)
	    return;

	int start, end;
	for (start = end = 0; ; start = end + 1) {
	    end = s.indexOf(c, start);
	    if (end < 0)
		break;
	    final String part = s.substring(start, end);
	    if (!list.contains(part))
		list.add(part);
	    start = end + 1;
	}
	final String part = s.substring(start);
	if (!list.contains(part))
	    list.add(part);
    }

    private static File findFile(final String name, final boolean source) {
	final int index = name.lastIndexOf('.');
	final String pkgName = (index == -1) ? "" : name.substring(0, index);
	final File pkg = (File)pkgMap.get(pkgName);
	if (pkg != null) {
	    final File file = new File(pkg, name.substring(index + 1));
	    return (file.exists()) ? file : null;
	}

	final String realName = File.separator
	    + name.replace('.', File.separatorChar)
	    + (source ? ".java" : ".class");
	for (int i = 0; i < classPath.length; i++) {
	    final File file = new File(classPath[i] + realName);
	    if (file.exists()) {
		pkgMap.put(pkgName, file.getParentFile());
		return file;
	    }
	}
	return null;
    }

    public static FileAST parseFile(final String name) throws IOException {
	return parseFile(new File(name));
    }

    public static FileAST parseFile(final File file) throws IOException {
	FileAST ast = (FileAST)parsedMap.get(file);
	if (ast != null)
	    return ast;

	final FileInputStream input = new FileInputStream(file);
	final JavaLexer lexer = new JavaLexer(input);
	lexer.setTokenObjectClass("antlr.CommonHiddenStreamToken");
	final TokenStreamHiddenTokenFilter filter =
	    new TokenStreamHiddenTokenFilter(lexer);
	filter.hide(JavaLexer.WS);
	filter.hide(JavaLexer.SL_COMMENT);
	filter.hide(JavaLexer.ML_COMMENT);
	final JavaParser parser = new JavaParser(filter);
	parser.setASTNodeClass("jparse.JavaAST");
	parser.setFile(file);
	parser.setFilename(file.getName());

	try {
	    parser.compilationUnit();
	} catch (RecognitionException recogEx) {
	    System.err.print("Could not parse ");
	    System.err.println(file.getName());
	    recogEx.printStackTrace();
	    return null;
	} catch (TokenStreamException tokenEx) {
	    System.err.print("Could not tokenize ");
	    System.err.println(file.getName());
	    tokenEx.printStackTrace();
	    return null;
	} finally {
	    input.close();
	}

	ast = (FileAST)parser.getAST();
	ast.setInitialHiddenToken(filter.getInitialHiddenToken());

	final TypeAST[] types = ast.types;
	if (types.length > 0) {	// Why wouldn't it be?  Dunno, but be safe.
	    final Type aType = (Type)map.get(types[0].name);
	    if (aType == null) {
		for (int i = 0; i < types.length; i++) {
		    final TypeAST type = types[i];
		    map.put(type.name, new SourceType(type));
		}
	    } else if (aType instanceof SourceType) {
		return ((SourceType)aType).file;
	    }
	}

	parsedMap.put(file, ast);

	CompileContext oldContext = JavaAST.context;
	JavaAST.context = new CompileContext();
	ast.parseComplete();
	JavaAST.context = oldContext;

	return ast;
    }

    public static Type forName(final String className)
	throws ClassNotFoundException {

	Type type = (Type)map.get(className);
	if (type != null) {
	    return type;
	}

	if (className.endsWith("[]")) {
	    final int index = className.indexOf('[');
	    final int dims = (className.length() - index) / 2;
	    final String baseName = className.substring(0, index);
	    final Type baseType = forName(baseName);
	    if (baseType instanceof CompiledType) {
		type = new CompiledType((CompiledType)baseType, dims);
	    } else {
		type = new SourceType((SourceType)baseType, dims);
	    }
	} else {
	    final File classFile = findFile(className, false);
	    final File sourceFile = findFile(className, true);
	    if (sourceFile != null &&
		(classFile == null ||
		 sourceFile.lastModified() > classFile.lastModified())) {
		try {
		    final FileAST file = parseFile(sourceFile);
		    for (int i = 0; i < file.types.length; i++) {
			if (file.types[i].name.endsWith(className))
			    return file.types[i].retrieveType();
		    }
		} catch (IOException ioEx) {
		}
	    }
	    try {
		type = new CompiledType
		    (Class.forName(className, false,
				   Type.class.getClassLoader()));
	    } catch (NoClassDefFoundError classErr) {
		throw new ClassNotFoundException(className);
	    } catch (ClassNotFoundException classEx) {
		final int index = className.lastIndexOf('.');
		if (index >= 0) {
		    final String prefix = className.substring(0, index);
		    final File pkg = (File)pkgMap.get(prefix);
		    if (pkg == null) {
			try {
			    final Type t = forName(prefix);
			    type = t.getInner(className.substring(index + 1));
			    if (type != null) {
				map.put(type.getName(), type); // $ conversion
				return type;
			    }
			} catch (ClassNotFoundException classEx2) {
			}}}
		throw classEx;
	    }
	}
	map.put(className, type);
	return type;
    }

    public static Type forClass(final Class theClass) {
	if (theClass == null)
	    return null;
	final String className = demangle(theClass.getName());
	Type type = (Type)map.get(className);
	if (type == null) {
	    type = new CompiledType(theClass);
	    map.put(className, type);
	}
	return type;
    }

    protected static String demangle(final String name) {

	if (name.charAt(0) != '[')
	    return name;
	final StringBuffer buf = new StringBuffer(name.length() * 2);
	for (int i = 0; i < name.length(); i++) {
	    switch(name.charAt(i)) {
	    case '[':
		buf.append("[]");
		break;
	    case 'B':
		buf.insert(0, "byte");
		break;
	    case 'C':
		buf.insert(0, "char");
		break;
	    case 'D':
		buf.insert(0, "double");
		break;
	    case 'F':
		buf.insert(0, "float");
		break;
	    case 'I':
		buf.insert(0, "int");
		break;
	    case 'J':
		buf.insert(0, "long");
		break;
	    case 'L':
		final int index = name.indexOf(';', i);
		buf.insert(0, name.substring(i + 1, index));
		i = index + 1;
		break;
	    case 'S':
		buf.insert(0, "short");
		break;
	    case 'Z':
		buf.insert(0, "boolean");
		break;
	    default:
		System.err.print("Tried to demangle ");
		System.err.print(name);
		System.err.println(" unsuccessfully.");
	    }
	}
	return buf.toString();
    }

    protected static String mangle(String name) {
	final StringBuffer buf = new StringBuffer(name.length() + 2);
	final int index = name.indexOf('[');
	if (index >= 0) {
	    for (int i = 0; i < (name.length() - index) / 2; i++)
		buf.append('[');
	    name = name.substring(0, index);
	}
	if (name.equals("boolean"))
	    buf.append('Z');
	else if (name.equals("byte"))
	    buf.append('B');
	else if (name.equals("char"))
	    buf.append('C');
	else if (name.equals("double"))
	    buf.append('D');
	else if (name.equals("float"))
	    buf.append('F');
	else if (name.equals("int"))
	    buf.append('I');
	else if (name.equals("long"))
	    buf.append('J');
	else if (name.equals("short"))
	    buf.append('S');
	else if (name.equals("void"))
	    buf.append('V');
	else {
	    buf.append('L');
	    buf.append(name);
	    buf.append(';');
	}
	return buf.toString();
    }

    public static boolean exists(final String className) {
	try {
	    return forName(className) != null;
	} catch (ClassNotFoundException noClassEx) {
	    return false;
	}
    }

    public static Type varType(final String className, final String varName) {
	try {
	    return forName(className).varType(varName);
	} catch (ClassNotFoundException classEx) {
	    return null;
	}}

    public static Type arithType(final Type t1, final Type t2) {
	if (t1 == doubleType || t2 == doubleType)
	    return doubleType;
	if (t1 == floatType || t2 == floatType)
	    return floatType;
	if (t1 == longType || t2 == longType)
	    return longType;
	return intType;
    }

    public static final Type[] mergeTypeLists(final Type[] list1,
					      final Type[] list2) {
	int length1 = list1.length;
	if (length1 == 0)
	    return list2;
	final int length2 = list2.length;
	if (length2 == 0)
	    return list1;

	final int size = length1 + length2;
	final Type[] bigResult = new Type[size];
	System.arraycopy(list1, 0, bigResult, 0, length1);

	int index = length1;
	for (int i = 0; i < length2; i++) {
	    final Type candidate = list2[i];
	    int found = 0;	// The number in list1 that list2[i] subsumes
	    for (int j = 0; j < length1; j++) {
		if (bigResult[j].superClassOf(candidate) && found == 0) {
		    found = 1;	// Something in list1 subsumes list2[i]
		} else if (candidate.superClassOf(bigResult[j])) {
		    bigResult[j] = (found == 0)
			? candidate
			: bigResult[found - 1];
		    found++;
		}
	    }
	    if (found == 0) {
		bigResult[index++] = candidate;
	    } else if (--found > 0) {
		System.arraycopy(bigResult, found, bigResult, 0,
				 length1 - found);
		length1 -= found;
		index -= found;
	    }
	}

	if (index == size)
	    return bigResult;
	final Type[] result = new Type[index];
	System.arraycopy(bigResult, 0, result, 0, index);
	return result;
    }

    public abstract boolean isAssignableFrom(Type type);
    public abstract boolean isInterface();
    public abstract boolean isArray();
    public abstract boolean isPrimitive();
    public abstract boolean isInner();
    public abstract String getName();
    public abstract Type getSuperclass() throws ClassNotFoundException;
    public abstract String getPackage();
    public abstract Type[] getInterfaces();
    public abstract Type getComponentType();
    public abstract int getModifiers();
    public abstract Type getDeclaringClass();
    public abstract Type[] getClasses();
    public abstract Method[] getMethods();
    public abstract Method getMethod(String methName, Type[] paramTypes, Type caller);
    public abstract Constructor getConstructor(Type[] params, Type caller);
    public abstract Type getInner(String name);
    public abstract Type getArrayType();
    public abstract Type varType(String varName);
    public abstract Method[] getMeths(String name, Type[] params, Type caller);
    public final boolean superClassOf(Type type) {
	try {
	    for ( ; type != null; type = type.getSuperclass()) {
		if (this == type)
		    return true;
	    }
	} catch (ClassNotFoundException classEx) {
	    // Do nothing
	}
	return false;
    }

    public final boolean superInterfaceOf(final Type type) {
	if (this == type) {
	    return true;
	}

	final Type[] interfaces = type.getInterfaces();
	for (int i = 0; i < interfaces.length; i++) {
	    if (superInterfaceOf(interfaces[i])) {
		return true;
	    }
	}
	return false;
    }

    public final boolean implementsInterface(final Type type) {
	final Type[] interfaces = getInterfaces();
	for (int i = 0; i < interfaces.length; i++) {
	    if (type.superInterfaceOf(interfaces[i])) {
		return true;
	    }
	}
	try {
	    final Type superclass = getSuperclass();
	    if (superclass != null)
		return superclass.implementsInterface(type);
	} catch (ClassNotFoundException classEx) {
	}
	return false;
    }
}
