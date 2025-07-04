package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import java.lang.reflect.Modifier;
import jparse.*;

public final class NewAST extends ExpressionAST implements JavaTokenTypes {

    private IdentifierAST typeName;
    private jparse.TypeAST anonymous;
    private int dimensions;
    private ListAST parameters;
    private Constructor theCons;

    public NewAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        typeName = (IdentifierAST) getFirstChild();
        final AST a = typeName.getNextSibling();
        if (a.getType() == LPAREN) {
            processObjectType(a);        // TRECHO REFATORADO: extração de método
        } else {
            processArrayType(a);         // TRECHO REFATORADO: extração de método
        }
    }

    // TRECHO REFATORADO
    // Método extraído para processar tipos do tipo objeto.
    private void processObjectType(final AST a) {
        parameters = (ListAST) a.getNextSibling();
        parameters.parseComplete();
        final AST anon = parameters.getNextSibling().getNextSibling();
        if (anon != null) {
            anonymous = (jparse.TypeAST) anon;
            anonymous.parseComplete();
        }
    }

    // TRECHO REFATORADO
    // Método extraído para processar tipos do tipo array.
    private void processArrayType(final AST a) {
        int i;
        AST b = a;
        for (i = 0; b != null && b.getType() == ARRAY_DECLARATOR; i++) {
            b = b.getFirstChild();
            final AST c = b.getNextSibling();
            if (c != null) {
                ((JavaAST) c).parseComplete();
            }
        }
        if (b != null) {
            ((JavaAST) b).parseComplete();
        }

        dimensions = i;
        b = a.getNextSibling();
        if (b != null) {
            context.type = new TypeAST(typeName.getName());
            ((ArrayInitAST) b).parseComplete();
        }
    }

    protected Type computeType() {
        final Type type = resolveType();     // TRECHO REFATORADO: extração de métodos
        if (dimensions == 0) {
            return resolveObjectType(type);  // TRECHO REFATORADO
        }
        return resolveArrayType(type);       // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    // Método extraído para resolver o tipo básico (anon ou nomeado).
    private Type resolveType() {
        if (anonymous == null) {
            return typeName.retrieveType();
        } else {
            Type anonType = anonymous.retrieveType();
            ((SourceType) anonType).anonCheckSuper();
            return anonType;
        }
    }

    // TRECHO REFATORADO
    // Método extraído para computar tipo objeto e obter o construtor apropriado
    private Type resolveObjectType(final Type type) {
        final Type myType = typeAST.retrieveType();
        Type[] paramTypes;
        if (anonymous == null && type.isInner() && !Modifier.isStatic(type.getModifiers())) {
            paramTypes = adjustInnerClassParams(parameters.getTypes(), myType, type);  // TRECHO REFATORADO
        } else {
            paramTypes = parameters.getTypes();
            theCons = type.getConstructor(paramTypes, myType);
        }
        return type;
    }

    // TRECHO REFATORADO
    // Método extraído para construir o array corretamente.
    private Type resolveArrayType(final Type type) {
        StringBuffer buf = new StringBuffer(type.getName());
        for (int i = 0; i < dimensions; i++)
            buf.append("[]");
        try {
            return topLevel.getType(buf.toString());
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    // TRECHO REFATORADO
    // Método extraído para ajustar parâmetros em classes internas não-estáticas
    private Type[] adjustInnerClassParams(Type[] origParams, Type myType, Type type) {
        Type[] paramTypes = new Type[origParams.length + 1];
        paramTypes[0] = myType;
        System.arraycopy(origParams, 0, paramTypes, 1, origParams.length);
        theCons = type.getConstructor(paramTypes, myType);
        while (theCons == null && paramTypes[0].isInner()) {
            paramTypes[0] = paramTypes[0].getDeclaringClass();
            theCons = type.getConstructor(paramTypes, myType);
        }
        return paramTypes;
    }

    protected Object computeValue() {
        return nonconstant;
    }

    protected Type[] computeExceptions() {
        if (dimensions != 0)
            return noTypes;

        retrieveType();
        return Type.mergeTypeLists(parameters.getExceptionTypes(), theCons.getExceptionTypes());
    }

    public VarList getVarList() {
        return (parameters == null) ? new VarList() : parameters.getVarList();
    }

    public IdentifierAST getTypeName() {
        return typeName;
    }

    public int getDimensions() {
        return dimensions;
    }

    public ListAST getParameters() {
        return parameters;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - **Método parseComplete():**
 *     - É muito extenso, tratando diversos casos e envolve lógica de decisão que dificulta a compreensão.
 *     - Há comentários explícitos indicando dois casos distintos ("An object type" e "An array type...").
 *     - Aqui podemos utilizar Extração de Métodos para separar claramente a lógica que determina o tipo (Objeto ou Array).
 *
 * - **Método computeType():**
 *     - Métodos grandes dificultam entender a responsabilidade de cada trecho do componente.
 *     - Possui lógica complexa para determinar se está criando um objeto ou um array e para buscar o construtor correto, especialmente no caso das classes internas.
 *     - Devemos aplicar aqui Extração de Métodos para as tarefas separadas:
 *         1. Construção do array (quando `dimensions > 0`).
 *         2. Configuração dos parâmetros e obtenção do construtor apropriado quando criando objeto (`dimensions == 0`).
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * - Seguindo Martin Fowler e Marco Tulio, a extração de métodos é feita para:
 *     - Criar funções com responsabilidades específicas e claras.
 *     - Remover a duplicação implícita (tanto em lógica quanto em responsabilização).
 *     - Tornar o código mais fácil de entender rapidamente, sem precisar entender toda sua lógica cognitiva de uma só vez.
 *
 * - Cada método extraído é coeso, possui um objetivo claro, e pode ser facilmente entendido isoladamente.
 * - O método original agora fica menor, explicitando claramente a lógica que executa.
 *
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 6
 * - **Divisão por tipo**:
 *     - Extrações de Método: 6
 *     - Extração de Classe: 0 (não foi percebida necessidade neste contexto específico)
 */