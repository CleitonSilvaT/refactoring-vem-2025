package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class UnaryArithAST extends ExpressionAST
    implements JavaTokenTypes {

    private ExpressionAST operand;
    
    // TRECHO REFATORADO: Extraída uma classe para lidar com operações aritméticas unárias
    private final UnaryOperatorCalculator operatorCalculator = new UnaryOperatorCalculator();

    public UnaryArithAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        if (getType() == MINUS)
            context.negative = !context.negative;
        operand = (ExpressionAST)getFirstChild();
        operand.parseComplete();
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Extraído método para determinar o tipo de retorno
        return determineReturnType(getType(), operand.retrieveType());
    }
    
    // TRECHO REFATORADO: Novo método extraído para determinar o tipo de retorno
    private Type determineReturnType(final int tokenType, final Type operandType) {
        boolean shouldConvertToInt = tokenType != INC && tokenType != DEC &&
            (operandType == Type.byteType || 
             operandType == Type.shortType || 
             operandType == Type.charType);
             
        return shouldConvertToInt ? Type.intType : operandType;
    }

    protected Type[] computeExceptions() {
        return operand.getExceptionTypes();
    }

    protected Object computeValue() {
        // TRECHO REFATORADO: Extraída lógica para uma classe especializada
        final int operator = getType();
        return operatorCalculator.calculateValue(operator, operand, retrieveType());
    }

    public VarList getVarList() {
        return operand.getVarList();
    }

    public ExpressionAST getOperand() {
        return operand;
    }
    
    // TRECHO REFATORADO: Nova classe extraída para encapsular a lógica de cálculo de operadores unários
    private class UnaryOperatorCalculator {
    
        public Object calculateValue(int operator, ExpressionAST operand, Type returnType) {
            // TRECHO REFATORADO: Extraído método para verificar operadores de modificação
            if (isModifyingOperator(operator)) {
                return nonconstant;
            }
            
            final Object subval = operand.getValue();
            if (subval == nonconstant) {
                return nonconstant;
            }
            
            // TRECHO REFATORADO: Extraído método para processar operadores simples
            if (isSimpleOperator(operator)) {
                return subval;
            }
            
            // TRECHO REFATORADO: Extraído método para processar operador bitwise NOT
            return calculateBitwiseNot((Number)subval, returnType);
        }
        
        // TRECHO REFATORADO: Novo método extraído para verificar operadores que modificam variáveis
        private boolean isModifyingOperator(int operator) {
            return operator == INC || operator == DEC || 
                   operator == POST_INC || operator == POST_DEC;
        }
        
        // TRECHO REFATORADO: Novo método extraído para verificar operadores simples
        private boolean isSimpleOperator(int operator) {
            return operator == PLUS || operator == MINUS;
        }
        
        // TRECHO REFATORADO: Novo método extraído para calcular o bitwise NOT
        private Object calculateBitwiseNot(Number num, Type returnType) {
            return (returnType == Type.intType)
                ? (Object)new Integer(~num.intValue())
                : (Object)new Long(~num.longValue());
        }
    }
}

/**
 *
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código da classe `UnaryArithAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. O método `computeValue()` é muito extenso e realiza várias operações distintas que podem ser extraídas em métodos separados:
 *     - Verificação se o operador modifica uma variável
 *     - Validação do valor do operando
 *     - Manipulação dos diferentes tipos de operadores (PLUS, MINUS, BNOT)
 *     - Cálculo do valor do bitwise NOT
 *
 * 2. A lógica de determinação do tipo de retorno no método `computeType()` pode ser extraída em um método separado para melhorar a legibilidade.
 * 3. Os operadores aritméticos e suas operações poderiam ser extraídos para uma classe separada, aplicando o princípio de responsabilidade única.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da classe `UnaryOperatorCalculator`**:
 *     - Esta refatoração segue o princípio de Responsabilidade Única de Martin Fowler. A nova classe encapsula toda a lógica relacionada ao cálculo de operadores unários, tornando o código mais modular e coeso.
 *     - A classe principal `UnaryArithAST` fica mais focada em sua responsabilidade principal de ser um nó da AST, delegando os cálculos complexos para uma classe especializada.
 *
 * 2. **Extração do método `determineReturnType`**:
 *     - Este método extraído melhora a legibilidade ao encapsular a lógica de determinação de tipos de retorno que estava dentro de `computeType()`.
 *     - A extração facilita o entendimento do propósito específico dessa parte do código, além de facilitar a manutenção futura.
 *
 * 3. **Extração dos métodos `isModifyingOperator`, `isSimpleOperator` e `calculateBitwiseNot`**:
 *     - Cada método extraído representa uma operação específica, anteriormente combinada no extenso método `computeValue()`.
 *     - Estes métodos têm nomes descritivos que expressam claramente seu propósito, melhorando a legibilidade e seguindo o princípio de "código autoexplicativo" de Martin Fowler.
 *     - A extração destes métodos facilita a manutenção, pois cada comportamento específico agora está isolado em seu próprio método.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 6 refatorações
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 5 (determineReturnType, isModifyingOperator, isSimpleOperator, calculateBitwiseNot, calculateValue)
 *     - **Extração de Classe**: 1 (UnaryOperatorCalculator)
 *
 * Estas refatorações melhoram significativamente a estrutura do código, tornando-o mais modular, mais fácil de entender e de manter. A classe extraída encapsula comportamentos específicos, seguindo o princípio de responsabilidade única, enquanto os métodos extraídos tornam o código mais legível e expressivo, seguindo as orientações de refatoração de Martin Fowler.
 */