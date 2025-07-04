package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ArithmeticAST extends ExpressionAST
        implements JavaTokenTypes {

    private ExpressionAST left;
    private ExpressionAST right;

    public ArithmeticAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        defineLeftRightExpressions(); // TRECHO REFATORADO
        handleNegativeContext();      // TRECHO REFATORADO
        left.parseComplete();
        handleNegativeContext();      // TRECHO REFATORADO
        right.parseComplete();
    }

    // TRECHO REFATORADO
    private void defineLeftRightExpressions() {
        left = (ExpressionAST) getFirstChild();
        right = (ExpressionAST) left.getNextSibling();
    }

    // TRECHO REFATORADO 
    private void handleNegativeContext() {
        final int type = getType();
        if (type == STAR || type == DIV || type == MOD) {
            context.negative = false;
        }
    }

    protected Type computeType() {
        final Type leftType = left.retrieveType();
        final Type rightType = right.retrieveType();
        if (getType() == PLUS &&
                (leftType == Type.stringType || rightType == Type.stringType)) {
            setType(CONCATENATION);
            return Type.stringType;
        }
        return Type.arithType(leftType, rightType);
    }

    protected Type[] computeExceptions() {
        return Type.mergeTypeLists(left.getExceptionTypes(),
                right.getExceptionTypes());
    }

    protected Object computeValue() {
        final Object leftObj = left.getValue();
        if (leftObj == nonconstant)
            return nonconstant;
        final Object rightObj = right.getValue();
        if (rightObj == nonconstant)
            return nonconstant;

        return ArithmeticEvaluator.evaluate(getType(), retrieveType(), leftObj, rightObj); // TRECHO REFATORADO
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
 * ## 1) Oportunidades de Refatoração Encontradas
 * - **Duplicação de código:** método `computeValue()` apresenta uma lógica repetitiva, com operações aritméticas semelhantes sendo realizadas diversas vezes dependendo do tipo (int, long, float, double).
 * - **Altamente acoplado:** existe manipulação de diferentes tipos numéricos diretamente dentro do método, dificultando leitura e manutenção.
 * - **Falta de segmentação de responsabilidade:** os métodos `parseComplete()` e `computeValue()` estão com várias responsabilidades claramente identificáveis, sendo candidatas à extração de métodos e classe.
 *
 * ## 2) Classe Refatorada com os Devidos Comentários
 * Realizei as extrações de métodos e uma nova classe auxiliar `ArithmeticEvaluator` para simplificar e organizar o código.
 * **Classe Refatorada (`ArithmeticAST`):**
 *
 * ## 3) Justificativa das Refatorações
 * - **Extração de Métodos (`parseComplete()`):** separou tarefas específicas (definir esquerda/direita e configurar contexto negativo) em métodos distintos. Isso melhora a clareza e facilita manutenções futuras (Fowler, 2018).
 * - **Extração de Classe (`ArithmeticEvaluator`):** centraliza operações aritméticas em um único local, removendo duplicações e eliminação de complexidade desnecessária. Segue o princípio Single Responsibility Principle (Marco Tulio, Engenharia de Software Moderna, 2021). Cada método trata especificamente de uma operação matemática, facilitando testes e futuras alterações.
 *
 * ## 4) Resumo das Alterações
 * - **Total de refatorações realizadas:** 2
 *     - **Extração de Método:** 2 (métodos `defineLeftRightExpressions()` e `handleNegativeContext()`)
 *     - **Extração de Classe:** 1 classe (`ArithmeticEvaluator`) contendo métodos de operação aritmética.
 */