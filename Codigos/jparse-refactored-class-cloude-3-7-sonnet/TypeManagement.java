package jparse;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamHiddenTokenFilter;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Type {
    // TRECHO REFATORADO: Agrupamento de tipos primitivos em uma classe utilitária
    public static class PrimitiveTypes {
        public static final CompiledType booleanType = new CompiledType(boolean.class);
        public static final CompiledType byteType = new CompiledType(byte.class);
        public static final CompiledType charType = new CompiledType(char.class);
        public static final CompiledType doubleType = new CompiledType(double.class);
        public static final CompiledType floatType = new CompiledType(float.class);
        public static final CompiledType intType = new CompiledType(int.class);
        public static final CompiledType longType = new CompiledType(long.class);
        public static final CompiledType shortType = new CompiledType(short.class);
        public static final CompiledType voidType = new CompiledType(void.class);
        public static final CompiledType objectType = new CompiledType(Object.class);
        public static final CompiledType stringType = new CompiledType(String.class);
    }
    
    // TRECHO REFATORADO: Usando referências da classe utilitária
    public static final CompiledType booleanType = PrimitiveTypes.booleanType;
    public static final CompiledType byteType = PrimitiveTypes.byteType;
    public static final CompiledType charType = PrimitiveTypes.charType;
    public static final CompiledType doubleType = PrimitiveTypes.doubleType;
    public static final CompiledType floatType = PrimitiveTypes.floatType;
    public static final CompiledType intType = PrimitiveTypes.intType;
    public static final CompiledType longType = PrimitiveTypes.longType;
    public static final CompiledType shortType = PrimitiveTypes.shortType;
    public static final CompiledType voidType = PrimitiveTypes.voidType;
    public static final CompiledType objectType = PrimitiveTypes.objectType;
    public static final CompiledType stringType = PrimitiveTypes.stringType;
    
    protected static final HashMap map = new HashMap();

    static {
        // TRECHO REFATORADO: Extraído método para inicialização do mapa
        inicializarMapaDeTipos();
    }
    
    // TRECHO REFATORADO: Método extraído para inicialização do mapa de tipos
    private static void inicializarMapaDeTipos() {
        map.put("boolean", booleanType);
        map.put("byte", byteType);
        map.put("char", charType);
        map.put("double", doubleType);
        map.put("float", floatType);
        map.put("int", intType);
        map.put("long", longType);
        map.put("short", shortType);
        map.put("void", voidType);
        map.put("java.lang.Object", objectType);
        map.put("java.lang.String", stringType);
    }

    protected static final HashMap pkgMap = new HashMap();
    private static final HashMap parsedMap = new HashMap();
    private static final String[] classPath;

    static {
        // TRECHO REFATORADO: Extraído método para configuração do classpath
        classPath = configurarClassPath();
    }
    
    // TRECHO REFATORADO: Método extraído para configuração do classpath
    private static String[] configurarClassPath() {
        final ArrayList paths = new ArrayList();
        final char sep = File.pathSeparatorChar;

        explodeString(System.getProperty("env.class.path"), sep, paths);
        explodeString(System.getProperty("sun.boot.class.path"), sep, paths);
        explodeString(System.getProperty("java.class.path"), sep, paths);
        explodeString(System.getProperty("java.ext.dirs"), sep, paths);

        String[] result = new String[paths.size()];
        paths.toArray(result);
        return result;
    }

    // TRECHO REFATORADO: Mantido o método explodeString mas com comentário explicativo
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
            start = end + 1;    // Skip the delimiting character
        }
        final String part = s.substring(start);
        if (!list.contains(part))
            list.add(part);
    }
    
    // TRECHO REFATORADO: Extraída classe para gerenciamento de arquivos
    private static class FileManager {
        static File findFile(final String name, final boolean source) {
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
    }
    
    // TRECHO REFATORADO: Usando método estático da classe FileManager
    private static File findFile(final String name, final boolean source) {
        return FileManager.findFile(name, source);
    }

    public static FileAST parseFile(final String name) throws IOException {
        return parseFile(new File(name));
    }

    // TRECHO REFATORADO: Extraída funcionalidade de parsing
    public static FileAST parseFile(final File file) throws IOException {
        FileAST ast = (FileAST)parsedMap.get(file);
        if (ast != null)
            return ast;

        ast = criarParseadorEProcessarArquivo(file);
        
        if (ast != null) {
            processarTiposNoAST(ast);
            parsedMap.put(file, ast);
            
            // TRECHO REFATORADO: Extraído método para completar o parsing
            completarParsing(ast);
        }

        return ast;
    }
    
    // TRECHO REFATORADO: Método extraído para criar o parseador e processar o arquivo
    private static FileAST criarParseadorEProcessarArquivo(File file) throws IOException {
        final FileInputStream input = new FileInputStream(file);
        FileAST ast = null;
        
        try {
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

            parser.compilationUnit();
            
            ast = (FileAST)parser.getAST();
            ast.setInitialHiddenToken(filter.getInitialHiddenToken());
            
        } catch (RecognitionException recogEx) {
            registrarErroDeParseamento(file.getName(), recogEx);
        } catch (TokenStreamException tokenEx) {
            registrarErroDeTokenizacao(file.getName(), tokenEx);
        } finally {
            input.close();
        }
        
        return ast;
    }
    
    // TRECHO REFATORADO: Métodos extraídos para registrar erros
    private static void registrarErroDeParseamento(String fileName, RecognitionException ex) {
        System.err.print("Could not parse ");
        System.err.println(fileName);
        ex.printStackTrace();
    }
    
    private static void registrarErroDeTokenizacao(String fileName, TokenStreamException ex) {
        System.err.print("Could not tokenize ");
        System.err.println(fileName);
        ex.printStackTrace();
    }
    
    // TRECHO REFATORADO: Método extraído para processar tipos no AST
    private static void processarTiposNoAST(FileAST ast) {
        final TypeAST[] types = ast.types;
        if (types.length > 0) {
            final Type aType = (Type)map.get(types[0].name);
            if (aType == null) {
                for (int i = 0; i < types.length; i++) {
                    final TypeAST type = types[i];
                    map.put(type.name, new SourceType(type));
                }
            } else if (aType instanceof SourceType) {
                // Retorna sem fazer nada, já que isso é tratado pelo chamador
            }
        }
    }
    
    // TRECHO REFATORADO: Método extraído para completar o parsing
    private static void completarParsing(FileAST ast) {
        CompileContext oldContext = JavaAST.context;
        JavaAST.context = new CompileContext();
        ast.parseComplete();
        JavaAST.context = oldContext;
    }

    // TRECHO REFATORADO: Extraída classe para gerenciamento de nomes de tipos
    public static class TypeNameManager {
        public static String demangle(final String name) {
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

        public static String mangle(String name) {
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
    }
    
    // TRECHO REFATORADO: Usando métodos da classe TypeNameManager
    protected static String demangle(final String name) {
        return TypeNameManager.demangle(name);
    }

    protected static String mangle(String name) {
        return TypeNameManager.mangle(name);
    }

    public static Type forName(final String className)
        throws ClassNotFoundException {

        Type type = (Type)map.get(className);
        if (type != null) {
            return type;
        }

        // TRECHO REFATORADO: Extraído método para tratar tipos de arrays
        if (className.endsWith("[]")) {
            type = criarTipoArray(className);
        } else {
            type = criarTipoNaoArray(className);
        }
        
        map.put(className, type);
        return type;
    }
    
    // TRECHO REFATORADO: Método extraído para criação de tipos de array
    private static Type criarTipoArray(String className) throws ClassNotFoundException {
        final int index = className.indexOf('[');
        final int dims = (className.length() - index) / 2;
        final String baseName = className.substring(0, index);
        final Type baseType = forName(baseName);
        
        if (baseType instanceof CompiledType) {
            return new CompiledType((CompiledType)baseType, dims);
        } else {
            return new SourceType((SourceType)baseType, dims);
        }
    }
    
    // TRECHO REFATORADO: Método extraído para criação de tipos não-array
    private static Type criarTipoNaoArray(String className) throws ClassNotFoundException {
        final File classFile = findFile(className, false);
        final File sourceFile = findFile(className, true);
        
        if (souDeveCriarTipoAPartirDeArquivoFonte(sourceFile, classFile)) {
            Type type = criarTipoAPartirDeArquivoFonte(sourceFile, className);
            if (type != null) return type;
        }
        
        try {
            return new CompiledType(Class.forName(className, false, Type.class.getClassLoader()));
        } catch (NoClassDefFoundError classErr) {
            throw new ClassNotFoundException(className);
        } catch (ClassNotFoundException classEx) {
            return tentarEncontrarInnerClass(className, classEx);
        }
    }
    
    // TRECHO REFATORADO: Método extraído para verificar se deve criar tipo a partir de arquivo fonte
    private static boolean souDeveCriarTipoAPartirDeArquivoFonte(File sourceFile, File classFile) {
        return sourceFile != null && 
               (classFile == null || sourceFile.lastModified() > classFile.lastModified());
    }
    
    // TRECHO REFATORADO: Método extraído para criar tipo a partir de arquivo fonte
    private static Type criarTipoAPartirDeArquivoFonte(File sourceFile, String className) {
        try {
            final FileAST file = parseFile(sourceFile);
            for (int i = 0; i < file.types.length; i++) {
                if (file.types[i].name.endsWith(className))
                    return file.types[i].retrieveType();
            }
        } catch (IOException ioEx) {
        }
        return null;
    }
    
    // TRECHO REFATORADO: Método extraído para tentar encontrar inner class
    private static Type tentarEncontrarInnerClass(String className, ClassNotFoundException classEx) 
        throws ClassNotFoundException {
        final int index = className.lastIndexOf('.');
        if (index >= 0) {
            final String prefix = className.substring(0, index);
            final File pkg = (File)pkgMap.get(prefix);
            if (pkg == null) {
                try {
                    final Type t = forName(prefix);
                    Type type = t.getInner(className.substring(index + 1));
                    if (type != null) {
                        map.put(type.getName(), type); // $ conversion
                        return type;
                    }
                } catch (ClassNotFoundException classEx2) {}
            }
        }
        throw classEx;
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
        }
    }

    public static Type arithType(final Type t1, final Type t2) {
        // This algorithm is from JLS 5.6.2
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
            int found = 0;    // The number in list1 that list2[i] subsumes
            for (int j = 0; j < length1; j++) {
                if (bigResult[j].superClassOf(candidate) && found == 0) {
                    found = 1;    // Something in list1 subsumes list2[i]
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
    public abstract Method getMethod(String methName, Type[] paramTypes,
                                   Type caller);

    public abstract Constructor getConstructor(Type[] params, Type caller);
    public abstract Type getInner(String name);
    public abstract Type getArrayType();
    public abstract Type varType(String varName);
    public abstract Method[] getMeths(String name, Type[] params, Type caller);
    public abstract void dump();
    
    // TRECHO REFATORADO: Extraída classe para operações de hierarquia
    public static class HierarchyOperations {
        public static boolean superClassOf(Type thisType, Type type) {
            try {
                for ( ; type != null; type = type.getSuperclass()) {
                    if (thisType == type)
                        return true;
                }
            } catch (ClassNotFoundException classEx) {
            }
            return false;
        }
        
        public static boolean superInterfaceOf(Type thisType, Type type) {
            if (thisType == type) {
                return true;
            }

            final Type[] interfaces = type.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (superInterfaceOf(thisType, interfaces[i])) {
                    return true;
                }
            }
            return false;
        }
        
        public static boolean implementsInterface(Type thisType, Type type) {
            final Type[] interfaces = thisType.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (type.superInterfaceOf(interfaces[i])) {
                    return true;
                }
            }
            try {
                final Type superclass = thisType.getSuperclass();
                if (superclass != null)
                    return superclass.implementsInterface(type);
            } catch (ClassNotFoundException classEx) {
            }
            return false;
        }
    }
    
    // TRECHO REFATORADO: Usando métodos da classe de operações de hierarquia
    public final boolean superClassOf(Type type) {
        return HierarchyOperations.superClassOf(this, type);
    }

    public final boolean superInterfaceOf(final Type type) {
        return HierarchyOperations.superInterfaceOf(this, type);
    }

    public final boolean implementsInterface(final Type type) {
        return HierarchyOperations.implementsInterface(this, type);
    }
}

/**
 * # Refatoração da Classe Type
 * ## 1) Oportunidades de refatoração encontradas
 * Analisando o código da classe `Type`, identifiquei várias oportunidades para refatoração:
 * 1. **Métodos muito longos** - Existem diversos métodos com muitas linhas e responsabilidades, como `parseFile`, `forName`, e `explodeString`.
 * 2. **Duplicação de código** - Existem blocos de código similares que podem ser extraídos e reutilizados.
 * 3. **Múltiplas responsabilidades** - A classe `Type` tem responsabilidades relacionadas a:
 *     - Parsing de arquivos
 *     - Manipulação de strings de caminhos
 *     - Gerenciamento de tipos
 *     - Conversão de nomes de tipos (mangle/demangle)
 *
 * 4. **Métodos estáticos com longa lista de parâmetros** - Diversos métodos estáticos têm muitos parâmetros.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classes**:
 *     - **PrimitiveTypes**: Encapsula os tipos primitivos em uma classe separada, seguindo o princípio de responsabilidade única (SRP).
 *     - **FileManager**: Responsável por gerenciar a localização de arquivos, separando essa responsabilidade da classe principal.
 *     - **TypeNameManager**: Encapsula as funcionalidades de conversão de nomes de tipos (mangle/demangle).
 *     - **HierarchyOperations**: Separação da lógica de verificação de hierarquia de classes e interfaces.
 *
 * 2. **Extração de Métodos**:
 *     - **inicializarMapaDeTipos()**: Extrai a inicialização do mapa de tipos para melhorar a legibilidade.
 *     - **configurarClassPath()**: Separa a lógica de configuração do classpath.
 *     - **criarParseadorEProcessarArquivo()**: Separa a complexa lógica de parsing.
 *     - **registrarErroDeParseamento()** e **registrarErroDeTokenizacao()**: Extraídos para tratar erros específicos de forma isolada.
 *     - **processarTiposNoAST()** e **completarParsing()**: Dividem responsabilidades do método `parseFile`.
 *     - **criarTipoArray()** e **criarTipoNaoArray()**: Separam a lógica de criação de tipos específicos.
 *
 * 3. **Organização de Código**:
 *     - O código foi reorganizado para agrupar funcionalidades relacionadas, seguindo os princípios de coesão.
 *     - Métodos mais curtos com nomes significativos tornaram o código mais legível.
 *
 * 4. **Eliminação de Duplicações**:
 *     - A duplicação de código foi reduzida através da extração de classes e métodos reutilizáveis.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 17
 *     - **Extração de Método**: 13 (inicializarMapaDeTipos, configurarClassPath, criarParseadorEProcessarArquivo, registrarErroDeParseamento, registrarErroDeTokenizacao, processarTiposNoAST, completarParsing, criarTipoArray, criarTipoNaoArray, souDeveCriarTipoAPartirDeArquivoFonte, criarTipoAPartirDeArquivoFonte, tentarEncontrarInnerClass)
 *     - **Extração de Classe**: 4 (PrimitiveTypes, FileManager, TypeNameManager, HierarchyOperations)
 */