package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class BitwiseAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST left;
    private ExpressionAST right;

    public BitwiseAST(final Token token) {
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
        if (leftType == Type.booleanType)
            return leftType;
        final Type rightType = right.retrieveType();
        return Type.arithType(leftType, rightType);
    }

    protected Type[] computeExceptions() {
        return Type.mergeTypeLists(left.getExceptionTypes(),
                right.getExceptionTypes());
    }

    protected Object computeValue() {
        final Object leftObj = left.getValue();
        final Object rightObj = right.getValue();

        // TRECHO REFATORADO: delegação à classe BitwiseEvaluator
        return BitwiseEvaluator.evaluate(getType(), retrieveType(), leftObj, rightObj);
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
 * ### 1) Oportunidades de Refatoração Encontradas
 * - O método `computeValue()` apresenta lógica complexa, com blocos condicionais grandes e repetição de estruturas semelhantes (casting de valores constantes e operações bitwise utilizando switch-case). Esse método é um bom candidato para a refatoração "Extração de Métodos" a fim de reduzir sua complexidade e eliminar a duplicação de código.
 * - Há também lógica relacionada especificamente à avaliação de valores constantes, identificando oportunidades para extrair essa lógica para uma classe separada ("Extração de Classe"), mais coesa e especializada em manipulação de constantes em operações bitwise.
 *
 * ### 2) Classe Refatorada com os Devidos Comentários
 *
 * ### 3) Justificativa das Refatorações
 * Com base em Martin Fowler (2018) e Marco Tulio, as decisões seguiram estes princípios:
 * - **Extração de Classes**:
 * Foi criada a classe `BitwiseEvaluator` para encapsular a lógica específica de avaliação bitwise de valores constantes. Desta forma, a responsabilidade é bem demarcada, seguindo o princípio de alta coesão e baixa acoplamento. Agora `BitwiseAST` delega essa responsabilidade, tornando-se mais simples e claro seu papel principal de gerenciar a estrutura da árvore sintática.
 * - **Extração de Métodos**:
 * Dentro de `BitwiseEvaluator`, extraímos três métodos (`evaluateBoolean`, `evaluateLong`, `evaluateInt`) removendo a duplicação de estruturas similares existentes em `computeValue`. Esta refatoração simplifica a compreensão dos métodos separadamente, promovendo maior clareza e manutenção direcionada (cada método manipula um único tipo de dado).
 *
 * Esta abordagem aumenta significativamente a clareza de cada método/classe e reduz complexidade (Fowler, 2018).
 * ### 4) Resumo das Alterações
 *
 * | Tipo de Refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Classe | 1 |
 * | Extração de Métodos | 3 |
 * | **Total geral** | **4** |
 * Resumo da refatoração realizada:
 * - Classe extraída (`BitwiseEvaluator`) com a lógica específica para bitwise.
 * - Métodos extraídos (`evaluateBoolean`, `evaluateLong`, `evaluateInt`) com responsabilidade única.
 * - Redução de duplicação de código e simplificação da lógica em métodos complexos.
 * - O código após refatoração está mais claro, coeso e fácil de ser mantido.
 */