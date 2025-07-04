package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ArrayInitAST extends ExpressionAST implements JavaTokenTypes {

    private TypeAST baseType;
    private InitializerList initializerList; // TRECHO REFATORADO: Nova classe extraída
    private int dim;

    public ArrayInitAST(final Token token) {
        super(token);
        setType(ARRAY_INIT);
    }

    public void parseComplete() {
        setBaseTypeFromContext(); // TRECHO REFATORADO: método extraído
        initializerList = new InitializerList(getFirstChild()); // TRECHO REFATORADO: classe extraída
        initializerList.completeInitializers(); // TRECHO REFATORADO: método extraído
    }

    private void setBaseTypeFromContext() { // TRECHO REFATORADO: método contém lógica antes no método parseComplete()
        this.baseType = context.type;
    }

    protected Type computeType() {
        defineMaxDimension(); // TRECHO REFATORADO: método extraído
        return buildArrayType(); // TRECHO REFATORADO: método extraído
    }

    private void defineMaxDimension() { // TRECHO REFATORADO
        for (ExpressionAST initializer : initializerList.getInitializers()) {
            if (initializer instanceof ArrayInitAST) {
                final ArrayInitAST init = (ArrayInitAST) initializer;
                if (init.dim > dim)
                    dim = init.dim;
            }
        }
        dim++;
    }

    private Type buildArrayType() { // TRECHO REFATORADO: método extraído da lógica original em computeType()
        final Type bType = baseType.retrieveType();
        final String bTypeName = bType.getName();
        final int index = bTypeName.indexOf('[');
        try {
            return Type.forName(bTypeName.substring(0, index + 2 * dim));
        } catch (Exception ex) {
            return null;
        }
    }

    protected Type[] computeExceptions() {
        return initializerList.mergeExceptionTypes(); // TRECHO REFATORADO: método movido para nova classe
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        return initializerList.buildCombinedVarList(); // TRECHO REFATORADO: método movido para nova classe
    }

    public ExpressionAST[] getInitializers() {
        return initializerList.getInitializers(); // TRECHO REFATORADO: método delegado para nova classe
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * - **Método `parseComplete()` longo**: Possui múltiplas responsabilidades como tratar o tipo base, montar a lista de inicializadores e realizar o parse completo dos inicializadores. Possível uso da técnica "Extração de Método".
 * - **Método `computeType()` complexo**: O cálculo da dimensão e construção do tipo resultante estão em um único método, causando complexidade excessiva. Pode ser realizada uma nova extração de métodos para simplificar essa lógica.
 * - **Métodos relacionados ao tratamento da lista de inicializadores** (`parseComplete()` e `getInitializers()`): Podem levar à duplicação e responsabilidade excessiva. Potencial para extração de classe que represente a lista dos inicializadores.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 * **Classe Original Refatorada (`ArrayInitAST`):**
 *
 * ## 3) Justificativa das refatorações:
 * **Extração de Métodos**: Segundo Fowler (2018), métodos longos possuem excessiva complexidade, sendo ideal dividi-los claramente em responsabilidades específicas. Aqui foram extraídos métodos como:
 * - `setBaseTypeFromContext()`
 * - `defineMaxDimension()`
 * - `buildArrayType()`
 * - `completeInitializers()`
 * - `mergeExceptionTypes()`
 * - `buildCombinedVarList()`
 *
 * Essas extrações tornam cada método simples, claros e com propósito único, melhorando fortemente a legibilidade e manutenção.
 * **Extração de Classe**: A criação da classe `InitializerList` é uma aplicação clara da técnica de Fowler (2018), buscando melhor organização do código e aderência ao princípio da responsabilidade única, já que:
 * - Centraliza operações relacionadas à lista dos inicializadores (`initializers`).
 * - Evita replicação do mesmo processo em métodos diferentes na classe original.
 * - A abstração simplifica os métodos da classe original, tornando o código mais compreensível e modular.
 *
 * ## 4) Resumo das alterações realizadas:
 * - **Quantidade total das refatorações:** 7.
 * - **Extrações de Classe realizadas:** 1 (`InitializerList`)
 * - **Extrações de Método realizadas:** 6
 *     - `setBaseTypeFromContext()`
 *     - `defineMaxDimension()`
 *     - `buildArrayType()`
 *     - `completeInitializers()`
 *     - `mergeExceptionTypes()`
 *     - `buildCombinedVarList()`
 */