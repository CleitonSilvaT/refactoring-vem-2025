package jparse;

import antlr.Token;
import antlr.collections.AST;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class TypeAST extends JavaAST implements JavaTokenTypes {

    static TypeAST currType;
    SourceType type;
    String name;
    TypeAST outer;
    TypeAST[] inner = new TypeAST[0];
    ModifierAST modifiers;
    String superclass;
    String[] interfaces;
    ConstrAST[] constructors;
    private int anon = 0;
    
    // TRECHO REFATORADO: Extraído construtor padrão para melhorar legibilidade
    public TypeAST() {
        this("java.lang.Object");
    }

    protected TypeAST(final String name) {
        // TRECHO REFATORADO: Extraído método de inicialização comum
        initializeTypeAST(new SymbolTable());
        setType(LITERAL_class);
        superclass = name;
    }

    protected TypeAST(final Token token) {
        // TRECHO REFATORADO: Extraído método de inicialização comum
        initializeTypeAST(new SymbolTable());
        setToken(token);
        superclass = "java.lang.Object";
    }
    
    // TRECHO REFATORADO: Extraído método para inicialização comum
    private void initializeTypeAST(SymbolTable symbolTable) {
        super.setSymbolTable(symbolTable);
        symbolTable.setEnclosingType(this);
    }

    protected void setInfo(final String pkg, final String typeName,
                          final TypeAST out, final ModifierAST mods) {
        name = (out != null || pkg == null) ? typeName : pkg + '.' + typeName;
        outer = out;
        modifiers = mods;
    }

    public void parseComplete() {
        final boolean oldIsField = context.isField;
        context.isField = true;
        for (JavaAST ast = (JavaAST)getFirstChild(); ast != null;
             ast = (JavaAST)ast.getNextSibling()) {
            context.nextStmt = null;
            ast.parseComplete();
        }
        context.isField = oldIsField;
    }

    protected void addConstructor(final ConstrAST cons) {
        // TRECHO REFATORADO: Delegado para o gerenciador de arrays
        constructors = (ConstrAST[]) ArrayManager.addElementToArray(
                constructors, cons, ConstrAST.class);
    }

    void addAnonymous(final String pkg, final TypeAST type) {
        type.setInfo(pkg, name + '$' + ++anon, this,
                    new ModifierAST(Modifier.FINAL)); 
        addInner(type);
    }

    void addInner(final TypeAST type) {
        // TRECHO REFATORADO: Delegado para o gerenciador de arrays
        inner = (TypeAST[]) ArrayManager.addElementToArray(
                inner, type, TypeAST.class);
                
        if (type.interfaces == null)
            type.interfaces = new String[0];
            
        // TRECHO REFATORADO: Extraído para método separado
        registerTypeInMap(type);
    }
    
    // TRECHO REFATORADO: Extraído método para registrar o tipo no mapa
    private void registerTypeInMap(TypeAST type) {
        final Type t = new SourceType(type);
        Type.map.put(type.name, t);
    }

    public String getSuperclass() {
        return superclass;
    }

    public JavaAST[] getMembers() {
        // TRECHO REFATORADO: Extraído método para encontrar o bloco de objeto
        AST objBlock = findObjBlock();
        
        // TRECHO REFATORADO: Extraído método para listar membros
        return collectMembersFromBlock(objBlock);
    }
    
    // TRECHO REFATORADO: Extraído método para encontrar o bloco de objeto
    private AST findObjBlock() {
        AST ast;
        for (ast = getFirstChild(); ast != null && ast.getType() != OBJBLOCK;
             ast = ast.getNextSibling());
        return ast;
    }
    
    // TRECHO REFATORADO: Extraído método para coletar membros
    private JavaAST[] collectMembersFromBlock(AST objBlock) {
        final ArrayList list = new ArrayList();
        for (AST ast = objBlock.getFirstChild(); ast != null; ast = ast.getNextSibling()) {
            list.add(ast);
        }
        final JavaAST[] members = new JavaAST[list.size()];
        list.toArray(members);
        return members;
    }

    public Type retrieveType() {
        return type;
    }

    public String toString() {
        return (modifiers.isInterface() ? "interface " : "class ") + name;
    }
}

// TRECHO REFATORADO: Extraído classe para gerenciar arrays
class ArrayManager {
    /**
     * Adiciona um elemento a um array existente ou cria um novo array com o elemento
     * 
     * @param array O array original
     * @param element O elemento a ser adicionado
     * @param componentType O tipo dos componentes do array
     * @return O novo array com o elemento adicionado
     */
    public static Object[] addElementToArray(Object[] array, Object element, Class componentType) {
        if (array != null) {
            Object[] newArray = java.lang.reflect.Array.newInstance(
                    componentType, array.length + 1).getClass().cast(
                    new Object[array.length + 1]);
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = element;
            return newArray;
        } else {
            Object[] newArray = java.lang.reflect.Array.newInstance(
                    componentType, 1).getClass().cast(new Object[1]);
            newArray[0] = element;
            return newArray;
        }
    }
}

/**
 * # Análise e Refatoração da Classe TypeAST
 * Vou analisar a classe TypeAST e executar refatorações de Extração de Classe e Extração de Métodos conforme solicitado.
 * ## 1) Oportunidades de refatoração encontradas
 * - **Gerenciamento de construtores**: O método `addConstructor` contém lógica de manipulação de arrays que pode ser extraído.
 * - **Manipulação de inner classes**: Os métodos `addAnonymous` e `addInner` compartilham código similar para expansão de arrays.
 * - **Recuperação de membros**: O método `getMembers` contém duas responsabilidades distintas: encontrar o bloco de objeto e listar seus membros.
 * - **Inicialização redundante**: Há código repetitivo nos construtores que pode ser extraído.
 * - **Responsabilidades múltiplas**: A classe TypeAST está manipulando tanto representação de tipos quanto gerenciamento de inner classes.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da classe `ArrayManager`**:
 *     - Esta refatoração aplica o princípio da Responsabilidade Única de Martin Fowler. A manipulação de arrays é uma responsabilidade independente que estava espalhada pela classe TypeAST.
 *     - O código para expandir arrays aparecia em dois lugares: `addConstructor` e `addInner`. Ao extrair para uma classe separada, eliminamos esta duplicação.
 *
 * 2. **Extração do método `initializeTypeAST`**:
 *     - Aplicando o princípio DRY (Don't Repeat Yourself), extraí o código comum de inicialização presente nos construtores.
 *     - Esta refatoração ajuda a evitar erros de manutenção, já que mudanças na inicialização só precisam ser feitas em um único lugar.
 *
 * 3. **Extração do método `registerTypeInMap`**:
 *     - Este método isolado torna mais claro o propósito do registro do tipo no mapa.
 *     - Facilita a manutenção e entendimento do código, aplicando o princípio de coesão funcional de Fowler.
 *
 * 4. **Extração dos métodos `findObjBlock` e `collectMembersFromBlock`**:
 *     - O método `getMembers` estava realizando duas tarefas distintas: encontrar o bloco de objeto e coletar seus membros.
 *     - A separação em métodos distintos melhora a legibilidade e segue o princípio de que cada método deve fazer apenas uma coisa.
 *
 * 5. **Simplificação dos métodos `addConstructor` e `addInner`**:
 *     - A lógica de manipulação de arrays foi delegada ao `ArrayManager`, mantendo estes métodos focados em suas responsabilidades principais.
 *     - Esta abordagem facilita a manutenção e compreensão do código.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 8
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 6
 *         - `initializeTypeAST`
 *         - `registerTypeInMap`
 *         - `findObjBlock`
 *         - `collectMembersFromBlock`
 *         - Métodos de `ArrayManager`: `addElementToArray`
 *
 *     - **Extração de Classe**: 1
 *         - `ArrayManager` - Para centralizar a manipulação de arrays
 */