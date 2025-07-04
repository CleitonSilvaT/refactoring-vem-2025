package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public class IdentifierAST extends ExpressionAST implements JavaTokenTypes {

    private String name;
    private boolean isMethod = false;

    public IdentifierAST() {
        super();
    }

    public IdentifierAST(final Token token) {
        super(token);
        name = getText();
    }

    public final void setName(String theName) {
        name = theName;
    }

    public final String getName() {
        return name;
    }

    public final void setMethod() {
        isMethod = true;
    }

    public int compareTo(final IdentifierAST ident) {
        return name.compareTo(ident.name);
    }

    protected Type computeType() {
        Type computedType = computeFromChild();
        if (computedType != null) return computedType;

        computedType = computeFromName();
        if (computedType != null) return computedType;

        return computeAsType();
    }

    // TRECHO REFATORADO – método extraído
    private Type computeFromChild() {
        final ExpressionAST firstChild = (ExpressionAST)getFirstChild();
        if (firstChild != null) {
            final Type childType = firstChild.retrieveType();
            if (childType != null) {
                return analyzeSiblingType(firstChild, childType);   // TRECHO REFATORADO – método extraído
            }
        }
        return null;
    }

    // TRECHO REFATORADO – método extraído
    private Type analyzeSiblingType(ExpressionAST firstChild, Type childType) {
        switch (firstChild.getNextSibling().getType()) {
            case LITERAL_class:
                return Type.forClass(Class.class);
            case LITERAL_this:
                return childType;
            case LITERAL_super:
                return getSuperclassType(childType);  // TRECHO REFATORADO – método extraído
            default:
                return resolveDefaultSiblingCase(firstChild, childType); // TRECHO REFATORADO – método extraído
        }
    }

    // TRECHO REFATORADO – método extraído
    private Type getSuperclassType(Type childType) {
        try {
            return childType.getDeclaringClass().getSuperclass();
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    // TRECHO REFATORADO – método extraído
    private Type resolveDefaultSiblingCase(ExpressionAST firstChild, Type childType) {
        if (isMethod)
            return childType;

        final String nextText = firstChild.getNextSibling().getText();
        if ("length".equals(nextText) && childType.getComponentType() != null)
            return Type.intType;

        final Type varType = childType.varType(nextText);
        return varType;
    }

    // TRECHO REFATORADO – método extraído
    private Type computeFromName() {
        final int lastDot = name.lastIndexOf('.');
        if (lastDot < 0) {
            return computeSimpleName();  // TRECHO REFATORADO – método extraído
        }
        return null;
    }

    // TRECHO REFATORADO – método extraído
    private Type computeSimpleName() {
        final Type myType = typeAST.retrieveType();
        switch (getType()) {
            case LITERAL_this:
                return myType.getDeclaringClass();
            case LITERAL_super:
                return getSuperClassFromTypeAST();  // TRECHO REFATORADO – método extraído
            default:
                return resolveVarOrField(myType, name);  // TRECHO REFATORADO – método extraído
        }
    }

    // TRECHO REFATORADO – método extraído
    private Type getSuperClassFromTypeAST() {
        try {
            return topLevel.getType(typeAST.getSuperclass());
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    // TRECHO REFATORADO – método extraído
    private Type resolveVarOrField(Type myType, String name) {
        final VarAST varAST = symTable.getVar(name);
        if (varAST != null) {
            return varAST.retrieveType();
        }

        Type varType = myType.varType(name);
        if (varType != null) return varType;

        final Type outerType = myType.getDeclaringClass();
        if (outerType != null) {
            return outerType.varType(name);
        }
        return null;
    }

    // TRECHO REFATORADO – método extraído
    private Type computeAsType() {
        try {
            return topLevel.getType(name);
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        final VarAST var = symTable.getVar(name);
        return (var == null) ? nonconstant : var.getValue();
    }

    public VarList getVarList() {
        return new VarList(symTable.getVar(name));
    }

    public String toString() {
        return name;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * Após uma análise crítica embasada nos conceitos de Martin Fowler e Marco Tulio, as seguintes oportunidades foram identificadas claramente:
 * - O método `computeType()` possui diversas responsabilidades internamente, dificultando legibilidade e manutenção, sugerindo clara oportunidade para extração de métodos menores, coesos e com objetivos claros.
 * - Trechos semelhantes repetidos no método `computeType()` (como consultas recorrentes por tipos e variações condicionais) merecem uma refatoração para eliminar duplicidades.
 * - Responsabilidade específica associada à análise de nós filhos e recuperação de tipos aponta para possibilidade de extração para uma classe dedicada.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 * ### Novo Código Refatorado (`IdentifierAST.java`):
 *
 * ## 3) Justificativa das refatorações:
 * - **Extração de Métodos**: Realizada em trechos internos de `computeType()` para dar clareza ao fluxo condicional e melhorar leitura e manutenção — seguindo recomendações claras de Fowler. Cada método agora tem uma só responsabilidade ("Single Responsibility").
 * - **Eliminação de duplicações** com as extrações pequenas: trechos de recuperação de superclasses e manipulação de tipos foram encapsuladas em métodos pequenos e compreensíveis. Conforme Marco Tulio, reduzimos duplicações criando funções especializadas ("Cohesion").
 * - Não foram encontrados trechos com justificativa suficiente para criar uma nova classe, pois as funcionalidades estão intrinsecamente relacionadas ao próprio nó identificador.
 *
 * ## 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas:** 9
 * - **Divisão por tipo:**
 *     - **Extração de Método:** 9 métodos extraídos
 *     - **Extração de Classe:** 0 classes extraídas
 */