package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

/**
 * Um AST node que representa uma expressão de deslocamento.
 */
public final class ShiftAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST left;
    private ExpressionAST right;

    public ShiftAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        left = (ExpressionAST) getFirstChild();
        right = (ExpressionAST) left.getNextSibling();
        left.parseComplete();
        right.parseComplete();
    }

    protected Type computeType() {
        final Type leftType = left.retrieveType();
        return (leftType == Type.byteType || leftType == Type.shortType ||
                leftType == Type.charType)
                ? Type.intType
                : leftType;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        final Object leftObj = left.getValue();
        final Object rightObj = right.getValue();

        if (leftObj == nonconstant || rightObj == nonconstant)
            return nonconstant;

        final Type myType = retrieveType();
        final int operator = getType();

        if (myType == Type.intType) {
            return calcularValorShift(((Number) leftObj).intValue(), ((Number) rightObj).intValue(), operator);
        } else { // long
            return calcularValorShift(((Number) leftObj).longValue(), ((Number) rightObj).longValue(), operator);
        }
    }

    // TRECHO REFATORADO - Extração de Método
    private Integer calcularValorShift(int leftVal, int rightVal, int operator) {
        switch (operator) {
            case SL:
                return leftVal << rightVal;
            case SR:
                return leftVal >> rightVal;
            default: // USR
                return leftVal >>> rightVal;
        }
    }

    // TRECHO REFATORADO - Extração de Método
    private Long calcularValorShift(long leftVal, long rightVal, int operator) {
        switch (operator) {
            case SL:
                return leftVal << rightVal;
            case SR:
                return leftVal >> rightVal;
            default: // USR
                return leftVal >>> rightVal;
        }
    }

    public VarList getVarList() {
        return new VarList(left.getVarList(), right.getVarList());
    }

    public ExpressionAST getLeft() {
        return left;
    }

    public ExpressionAST getRight() {
        return right;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - O método `computeValue()` contém uma lógica complexa com decisões aninhadas para determinar valores constantes. Esse método viola o princípio da responsabilidade única, uma vez que realiza operações que podem ser claramente separadas em métodos menores.
 * - A decisão sobre qual operação bitwise será executada (`SL`, `SR`, etc) está duplicada para tipos `int` e `long`, indicando oportunidade para extração de método que calcule este resultado independentemente do tipo dos parâmetros, diminuindo duplicidade e complexidade do código.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * - **Extração de Método** (`calcularValorShift(int, int, int)` e `calcularValorShift(long, long, int)`):
 * Conforme aponta Martin Fowler, métodos longos ou com lógica repetitiva devem ser divididos em métodos menores que representam claramente suas funções específicas. Neste caso, a parte do método `computeValue()` responsável por realizar a operação bitwise foi extraída para dois novos métodos especializados. Essa alteração melhora significativamente a legibilidade, diminuindo o nível de alinhamento da lógica e simplificando as decisões.
 * - A duplicação de lógica foi eliminada através da criação desses métodos especializados para cada tipo (`int` e `long`), facilitando a manutenção futura e prevenindo erros duplicados caso haja necessidade de mudanças nessa lógica específica.
 *
 * ### 4) Resumo das alterações
 * - Quantidade total de refatorações realizadas: **2**.
 * - Divisão por tipo:
 *     - Extração de Método: **2**.
 *     - Extração de Classe: **0** (não foi necessária).
 */