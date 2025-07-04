package jparse;

import antlr.CommonHiddenStreamToken;
import java.io.File;
import java.util.HashMap;

/**
 * An AST node that represents the contents of a file
 */
public final class FileAST extends JavaAST implements JavaTokenTypes {

    static FileAST currFile;
    final File theFile;
    String pkg;
    String[] imports;
    TypeAST[] types;
    
    // TRECHO REFATORADO: Extraída uma classe para gerenciar os tipos
    private final TypeManager typeManager;

    public FileAST(final File file) {
        super();
        initialize(FILE, "");
        theFile = file;
        
        // TRECHO REFATORADO: Delegado ao TypeManager
        typeManager = new TypeManager();
        typeManager.initializePrimitiveTypes();
    }

    void setInitialHiddenToken(final CommonHiddenStreamToken hiddenTok) {
        hiddenAfter = hiddenTok;
    }

    public Type getType(final String name) throws ClassNotFoundException {
        // TRECHO REFATORADO: Delegado ao TypeManager
        return typeManager.getType(name, pkg, imports, types);
    }

    public void parseComplete() {
        for (int i = 0; i < types.length; i++) {
            types[i].parseComplete();
        }
    }

    public String toString() {
        return theFile.toString();
    }
    
    // TRECHO REFATORADO: Nova classe extraída para gerenciar tipos
    private class TypeManager {
        private final HashMap map = new HashMap();
        
        void initializePrimitiveTypes() {
            // TRECHO REFATORADO: Método extraído para inicializar tipos primitivos
            map.put("boolean", Type.booleanType);
            map.put("byte",    Type.byteType   );
            map.put("char",    Type.charType   );
            map.put("double",  Type.doubleType );
            map.put("float",   Type.floatType  );
            map.put("int",     Type.intType    );
            map.put("long",    Type.longType   );
            map.put("short",   Type.shortType  );
            map.put("void",    Type.voidType   );
        }
        
        Type getType(final String name, String pkg, String[] imports, TypeAST[] types) 
                throws ClassNotFoundException {
            // TRECHO REFATORADO: Verificar primeiro no cache
            Type type = checkCache(name);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar encontrar o tipo como um array
            type = tryResolveAsArray(name);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar como nome qualificado ou classe interna parcialmente qualificada
            type = tryResolveAsQualifiedName(name);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar como classe interna não qualificada neste arquivo
            final String dollarName = '$' + name;
            type = tryResolveAsInnerClassInThisFile(dollarName, types);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar no mesmo pacote
            type = tryResolveInSamePackage(name, pkg);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar no pacote java.lang
            type = tryResolveInJavaLang(name);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar como classe importada
            type = tryResolveAsImportedClass(name, imports);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar no pacote padrão
            type = tryResolveInDefaultPackage(name);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar como classe interna não qualificada em uma superclasse
            type = tryResolveAsInnerClassInSuperclass(dollarName, types);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tentar como classe interna não qualificada em uma classe importada
            type = tryResolveAsInnerClassInImportedClass(dollarName, imports);
            if (type != null) {
                return type;
            }
            
            // TRECHO REFATORADO: Tipo não encontrado
            throw new ClassNotFoundException(name);
        }
        
        // TRECHO REFATORADO: Métodos extraídos para cada caso de resolução de tipo
        
        private Type checkCache(final String name) {
            return (Type) map.get(name);
        }
        
        private Type tryResolveAsArray(final String name) throws ClassNotFoundException {
            if (!name.endsWith("[]")) {
                return null;
            }
            
            try {
                final int index = name.indexOf('[');
                final Type baseType = getType(name.substring(0, index), pkg, imports, types);
                if (baseType == null) {
                    System.err.print("**** Failed to resolve ");
                    System.err.println(name.substring(0, index));
                }
                Type type = Type.forName(baseType.getName() + name.substring(index));
                return registerType(name, type);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        
        private Type tryResolveAsQualifiedName(final String name) {
            final int index = name.lastIndexOf('.');
            if (index == -1) {
                return null;
            }
            
            // Tentar como nome completamente qualificado
            try {
                Type type = Type.forName(name);
                return registerType(name, type);
            } catch (ClassNotFoundException ex1) {
                // Tentar como classe interna parcialmente qualificada
                try {
                    final Type t = getType(name.substring(0, index), pkg, imports, types);
                    Type type = t.getInner('$' + name.substring(index + 1));
                    if (type != null) {
                        return registerType(name, type);
                    }
                } catch (ClassNotFoundException ex2) {
                    // Ignorar
                }
                return null;
            }
        }
        
        private Type tryResolveAsInnerClassInThisFile(final String dollarName, TypeAST[] types) {
            for (int i = 0; i < types.length; i++) {
                final TypeAST[] inner = types[i].inner;
                for (int j = 0; j < inner.length; j++) {
                    if (inner[j].name.endsWith(dollarName)) {
                        Type type = inner[j].retrieveType();
                        return registerType(dollarName.substring(1), type);
                    }
                }
            }
            return null;
        }
        
        private Type tryResolveInSamePackage(final String name, String pkg) {
            final String dotName = '.' + name;
            try {
                Type type = Type.forName(pkg + dotName);
                return registerType(name, type);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        
        private Type tryResolveInJavaLang(final String name) {
            try {
                Type type = Type.forName("java.lang." + name);
                return registerType(name, type);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        
        private Type tryResolveAsImportedClass(final String name, String[] imports) {
            final String dotName = '.' + name;
            for (int i = 0; i < imports.length; i++) {
                String fullName;
                final int lastIndex = imports[i].length() - 1;
                if (imports[i].charAt(lastIndex) == '*') {
                    fullName = imports[i].substring(0, lastIndex) + name;
                } else if (imports[i].endsWith(dotName)) {
                    fullName = imports[i];
                } else {
                    continue;
                }

                try {
                    Type type = Type.forName(fullName);
                    return registerType(name, type);
                } catch (ClassNotFoundException ex) {
                    // Continua para o próximo import
                }
            }
            return null;
        }
        
        private Type tryResolveInDefaultPackage(final String name) {
            try {
                Type type = Type.forName(name);
                return registerType(name, type);
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
        
        private Type tryResolveAsInnerClassInSuperclass(final String dollarName, TypeAST[] types) {
            for (int i = 0; i < types.length; i++) {
                Type type = types[i].retrieveType().getInner(dollarName);
                if (type != null) {
                    return registerType(dollarName.substring(1), type);
                }
            }
            return null;
        }
        
        private Type tryResolveAsInnerClassInImportedClass(final String dollarName, String[] imports) {
            for (int i = 0; i < imports.length; i++) {
                final int lastIndex = imports[i].length() - 1;
                if (imports[i].charAt(lastIndex) != '*') {
                    try {
                        final Type t = Type.forName(imports[i]);
                        Type type = t.getInner(dollarName);
                        if (type != null) {
                            return registerType(dollarName.substring(1), type);
                        }
                    } catch (ClassNotFoundException ex) {
                        // Continua para o próximo import
                    }
                }
            }
            return null;
        }
        
        // TRECHO REFATORADO: Método utilitário para registrar um tipo encontrado no mapa
        private Type registerType(final String name, final Type type) {
            map.put(name, type);
            return type;
        }
    }
}

/**
 * # Refatoração da Classe FileAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `FileAST`, identifiquei as seguintes oportunidades para refatoração:
 * 1. **Extração de Classe**: A funcionalidade de gerenciamento de tipos (incluindo o mapeamento de tipos primitivos e a busca de tipos) pode ser extraída para uma classe separada.
 * 2. **Extração de Métodos**: O método `getType` é muito extenso (mais de 100 linhas) e tem muitos casos diferentes, o que dificulta a compreensão. Cada caso pode ser extraído para um método separado.
 * 3. **Código duplicado**: Existem padrões repetitivos no método `getType` onde um tipo é encontrado, adicionado ao mapa e retornado. Este padrão pode ser extraído para um método.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da Classe TypeManager**:
 *     - A classe `FileAST` tinha responsabilidades divididas entre representar um arquivo AST e gerenciar tipos. Seguindo o Princípio da Responsabilidade Única (SRP), extraí o gerenciamento de tipos para uma classe `TypeManager`.
 *     - De acordo com Martin Fowler, a extração de classe é recomendada quando uma classe tem muitas responsabilidades ou quando parte da classe pode ser isolada em uma abstração coerente.
 *
 * 2. **Extração de Métodos em getType**:
 *     - O método `getType` original era extenso e difícil de entender, com vários casos condicionais. Extraí cada caso para um método específico com um nome descritivo para aumentar a legibilidade e manutenibilidade.
 *     - Conforme Fowler, métodos pequenos e focados são mais fáceis de entender, testar e reutilizar.
 *
 * 3. **Método utilitário registerType**:
 *     - Identifiquei um padrão recorrente no código: adicionar um tipo ao mapa e então retorná-lo. Extraí esse padrão para um método `registerType`, reduzindo a duplicação de código.
 *     - Conforme Marco Tulio em "Engenharia de Software Moderna", a eliminação de código duplicado aumenta a manutenibilidade e reduz a chance de erros.
 *
 * 4. **Reorganização do fluxo lógico**:
 *     - Reorganizei o fluxo do método `getType` para usar early returns em cada verificação, o que torna o código mais limpo e evita aninhamentos excessivos.
 *     - Esta abordagem facilita a compreensão do fluxo de execução e melhora a legibilidade do código.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 14
 * - **Divisão por tipo**:
 *     - **Extração de Classe**: 1 (TypeManager)
 *     - **Extração de Método**: 13 (initializePrimitiveTypes, checkCache, tryResolveAsArray, tryResolveAsQualifiedName, tryResolveAsInnerClassInThisFile, tryResolveInSamePackage, tryResolveInJavaLang, tryResolveAsImportedClass, tryResolveInDefaultPackage, tryResolveAsInnerClassInSuperclass, tryResolveAsInnerClassInImportedClass, registerType e o novo método getType na classe TypeManager)
 *
 * As refatorações realizadas melhoram significativamente a estrutura do código, tornando-o mais modular, legível e manutenível. A classe FileAST agora está focada apenas em suas responsabilidades principais, enquanto a nova classe TypeManager lida com a complexidade do gerenciamento de tipos. Os métodos pequenos e com nomes descritivos facilitam o entendimento do código e sua manutenção futura.
 */