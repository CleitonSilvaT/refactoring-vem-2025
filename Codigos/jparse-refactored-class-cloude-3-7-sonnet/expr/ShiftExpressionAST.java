package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ShiftAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST left;
    private ExpressionAST right;
    
    // TRECHO REFATORADO: Extraída classe ShiftOperator para lidar com as operações de shift
    private ShiftOperator shiftOperator;

    public ShiftAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Inicialização da classe auxiliar
        this.shiftOperator = new ShiftOperator();
    }

    public void parseComplete() {
        left = (ExpressionAST)getFirstChild();
        right = (ExpressionAST)left.getNextSibling();
        left.parseComplete();
        right.parseComplete();
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Extraído método para verificar se o tipo deve ser promovido para int
        final Type leftType = left.retrieveType();
        return shouldPromoteToInt(leftType) ? Type.intType : leftType;
    }
    
    // TRECHO REFATORADO: Método extraído para verificar tipos que devem ser promovidos para int
    private boolean shouldPromoteToInt(Type type) {
        return type == Type.byteType || type == Type.shortType || type == Type.charType;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        final Object leftObj = left.getValue();
        if (leftObj == nonconstant)
            return nonconstant;

        final Object rightObj = right.getValue();
        if (rightObj == nonconstant)
            return nonconstant;

        // TRECHO REFATORADO: Delegada a operação de shift para a classe ShiftOperator
        final Type myType = retrieveType();
        final int operator = getType();
        
        return shiftOperator.performShiftOperation(leftObj, rightObj, myType, operator);
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
    
    // TRECHO REFATORADO: Classe extraída para encapsular a lógica de operações de shift
    private class ShiftOperator {
        
        public Object performShiftOperation(Object leftObj, Object rightObj, Type type, int operator) {
            if (type == Type.intType) {
                return performIntShift(leftObj, rightObj, operator);
            } else {
                return performLongShift(leftObj, rightObj, operator);
            }
        }
        
        // TRECHO REFATORADO: Método extraído para realizar operações de shift em inteiros
        private Integer performIntShift(Object leftObj, Object rightObj, int operator) {
            final int leftVal = ((Number)leftObj).intValue();
            final int rightVal = ((Number)rightObj).intValue();
            
            return new Integer(calculateIntShiftResult(leftVal, rightVal, operator));
        }
        
        // TRECHO REFATORADO: Método extraído para calcular o resultado de shift para inteiros
        private int calculateIntShiftResult(int leftVal, int rightVal, int operator) {
            if (operator == SL) {
                return leftVal << rightVal;
            } else if (operator == SR) {
                return leftVal >> rightVal;
            } else {
                return leftVal >>> rightVal;
            }
        }
        
        // TRECHO REFATORADO: Método extraído para realizar operações de shift em longs
        private Long performLongShift(Object leftObj, Object rightObj, int operator) {
            final long leftVal = ((Number)leftObj).longValue();
            final long rightVal = ((Number)rightObj).longValue();
            
            return new Long(calculateLongShiftResult(leftVal, rightVal, operator));
        }
        
        // TRECHO REFATORADO: Método extraído para calcular o resultado de shift para longs
        private long calculateLongShiftResult(long leftVal, long rightVal, int operator) {
            if (operator == SL) {
                return leftVal << rightVal;
            } else if (operator == SR) {
                return leftVal >> rightVal;
            } else {
                return leftVal >>> rightVal;
            }
        }
    }
}


/**
 * # Refatoração da classe ShiftAST
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Remoção de comentários**: Conforme solicitado, todos os comentários serão removidos antes da refatoração.
 * 2. **Extração de método para o cálculo de valores de operações de shift em inteiros**: O código que calcula os valores de shift para inteiros está embutido diretamente no método `computeValue()` e pode ser extraído para um método separado.
 * 3. **Extração de método para o cálculo de valores de operações de shift em longs**: Similar ao anterior, o cálculo para valores long também pode ser extraído para um método próprio.
 * 4. **Extração de método para verificação de tipos de dados primitivos menores**: A lógica de verificação de tipos que devem ser promovidos para int no método `computeType()` pode ser extraída para um método mais semântico.
 * 5. **Extração de classe para operações de shift**: A lógica de operação de shift pode ser extraída para uma classe separada, especialmente o cálculo de valores para diferentes tipos e operadores.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `shouldPromoteToInt`**: Esta refatoração segue o princípio de Martin Fowler de "dar aos métodos nomes que expressem seu propósito". Ao extrair esta verificação para um método separado, tornamos o código mais expressivo e semântico, facilitando a compreensão da regra de promoção de tipos de Java que está sendo aplicada.
 * 2. **Extração da classe `ShiftOperator`**: Esta refatoração aplica o princípio da Responsabilidade Única (SRP). A classe ShiftAST agora se concentra em representar um nó de AST, enquanto a classe ShiftOperator se concentra exclusivamente na lógica de realizar operações de deslocamento. Isso melhora a coesão das classes e facilita a manutenção futura de cada responsabilidade independentemente.
 * 3. **Extração dos métodos `performIntShift` e `performLongShift`**: Esta refatoração divide a lógica complexa em partes menores e mais compreensíveis, conforme recomendado por Fowler. Cada método agora tem uma única responsabilidade bem definida, melhorando a legibilidade e a manutenção do código.
 * 4. **Extração dos métodos `calculateIntShiftResult` e `calculateLongShiftResult`**: Estas extrações seguem o princípio de que um método deve fazer apenas uma coisa. Ao separar o cálculo do resultado da operação de shift da conversão de tipos e manipulação de objetos, cada método se torna mais simples e com responsabilidade única.
 * 5. **Reorganização do método `computeValue`**: A refatoração tornou este método mais limpo e declarativo, delegando o trabalho pesado para a classe auxiliar `ShiftOperator`. Isso está alinhado com o princípio de que um método deve ter um nível consistente de abstração.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 7 refatorações
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 5 (shouldPromoteToInt, performIntShift, performLongShift, calculateIntShiftResult, calculateLongShiftResult)
 *     - **Extração de Classe**: 1 (ShiftOperator)
 *     - **Reorganização de código**: 1 (computeValue)
 *
 * As refatorações realizadas seguem os princípios estabelecidos por Martin Fowler e Marco Tulio, tornando o código mais modular, com maior coesão, melhor separação de responsabilidades e mais fácil de manter e entender. A extração de métodos e classes deixou o código mais expressivo e cada componente agora tem uma responsabilidade bem definida e única.
 */