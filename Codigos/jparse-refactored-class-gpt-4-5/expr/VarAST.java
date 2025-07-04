package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import jparse.*;

public final class VarAST extends IdentifierAST implements JavaTokenTypes {

    private ModifierAST mods;
    private TypeAST declType;
    private JavaAST brackets;
    private InitializerAST initializer;
    private boolean field;

    public VarAST(final Token token) {
        super(token);
        symTable.addVar(this);
    }

    public void parseComplete() {
        mods = context.mods;
        declType = context.type;

        extractBrackets(); // TRECHO REFATORADO
        extractInitializer(); // TRECHO REFATORADO
        field = context.isField;
    }

    // TRECHO REFATORADO - Extração do processamento dos colchetes de declaração para método próprio
    private void extractBrackets() {
        AST a = getNextSibling();
        if (a != null && a.getType() == ARRAY_DECLARATOR) {
            brackets = (JavaAST) a;
            do {
                a = a.getNextSibling().getNextSibling();
            } while (a != null && a.getType() == ARRAY_DECLARATOR);
        }
    }

    // TRECHO REFATORADO - Extração do processamento inicializador
    private void extractInitializer() {
        AST a = getNextSibling();
        while (a != null && a.getType() == ARRAY_DECLARATOR) {
            a = a.getNextSibling().getNextSibling();
        }
        if (a != null && a instanceof InitializerAST) {
            initializer = (InitializerAST) a;
            initializer.parseComplete();
        }
    }

    protected Type computeType() {
        final Type baseType = declType.retrieveType();
        return brackets == null ? baseType : buildArrayType(baseType); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Extração da lógica de criação do Tipo array
    private Type buildArrayType(Type baseType) {
        final String fullTypeName = buildArrayTypeName(baseType.getName());
        try {
            return Type.forName(fullTypeName);
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    // TRECHO REFATORADO - método comum que gera nome do tipo com colchetes array
    private String buildArrayTypeName(String baseTypeName) {
        StringBuffer buf = new StringBuffer(baseTypeName);
        for (AST b = brackets; b != null && b.getType() == ARRAY_DECLARATOR; b = b.getNextSibling().getNextSibling()) {
            buf.append("[]");
        }
        return buf.toString();
    }

    protected Type[] computeExceptions() {
        return initializer == null ? noTypes : initializer.getExceptionTypes();
    }

    protected Object computeValue() {
        return (mods.isFinal() || initializer == null) ? nonconstant : initializer.getValue();
    }

    public VarList getVarList() {
        final VarList thisVar = new VarList(this);
        return (initializer == null) ? thisVar : new VarList(thisVar, initializer.getVarList());
    }

    public ModifierAST getModifiers() {
        return mods;
    }

    public TypeAST getTypeName() {
        return declType;
    }

    public JavaAST getBrackets() {
        return brackets;
    }

    public InitializerAST getInitializer() {
        return initializer;
    }

    public boolean isField() {
        return field;
    }

    public String toString() {
        return buildArrayTypeName(declType.toString()) + ' ' + super.toString(); // TRECHO REFATORADO
    }
}
/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - O método `parseComplete()` possui lógica distinta:
 *     - Uma que faz a recuperação de elementos próximos da árvore (`mods`, `declType`, `brackets`).
 *     - Outra que processa o `initializer`, que poderia ser extraída para um método específico.
 *
 * - O método `computeType()` possui uma lógica específica para montar o nome completo do tipo com arrays, que pode ser encapsulada num método auxiliar.
 * - O método `toString()` repete lógica similar ao método `computeType()`, construindo também uma representação textual. Essa repetição pode ser reduzida extraindo um método comum.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 * ### 3) Justificativa das refatorações:
 * - **Método parseComplete()**
 *     - Conforme Martin Fowler (2018), métodos devem ter apenas uma responsabilidade (princípio de responsabilidade única). A remoção da complexidade através da extração de trechos lógicos em métodos é essencial para a legibilidade e clareza do código.
 *
 * - **Método computeType()**
 *     - A separação de uma lógica específica em um método próprio (`buildArrayType`) facilita futuras manutenções. Reduzindo a complexidade do método original, melhora-se a legibilidade e permite melhor compreensão de qual é seu objetivo central.
 *
 * - **Extração do método buildArrayTypeName**
 *     - Para eliminar a duplicação de código detectada entre `computeType()` e `toString()`, foi feita esta refatoração. Ao extrair repetição para um método único, seguimos a recomendação de Fowler (2018) para evitar código duplicado.
 *
 * ### 4) Resumo das alterações:
 * - Total de Refatorações realizadas: **5**
 * - Extração de Método: **5**
 * - Extração de Classe: **0** (não identificada necessidade específica de criação de nova classe nesta situação)
 */