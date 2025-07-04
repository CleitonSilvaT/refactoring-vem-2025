package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class TypecastAST extends ExpressionAST
    implements JavaTokenTypes {

    private TypeAST castType;
    private ExpressionAST castExpr;

    // TRECHO REFATORADO: Extraída a classe NumberTypeConverter para lidar com conversões numéricas
    private final NumberTypeConverter numberTypeConverter = new NumberTypeConverter();

    public TypecastAST(final Token token) {
        super(token);
        setType(TYPECAST);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído o método para inicializar os elementos do cast
        initializeCastComponents();
    }

    // TRECHO REFATORADO: Método extraído para melhorar legibilidade
    private void initializeCastComponents() {
        castType = (TypeAST)getFirstChild();
        castExpr = (ExpressionAST)castType.getNextSibling().getNextSibling();
        castType.parseComplete();
        context.negative = false;
        castExpr.parseComplete();
    }

    protected Type computeType() {
        return castType.retrieveType();
    }

    protected Type[] computeExceptions() {
        return castExpr.getExceptionTypes();
    }

    protected Object computeValue() {
        final Object castVal = castExpr.getValue();
        if (!(castVal instanceof Number))
            return castVal;

        // TRECHO REFATORADO: Delegação da conversão de tipos numéricos para a classe extraída
        return numberTypeConverter.convertNumber((Number)castVal, castType.retrieveType());
    }

    public VarList getVarList() {
        return castExpr.getVarList();
    }

    public TypeAST getTypeName() {
        return castType;
    }

    public ExpressionAST getCastExpression() {
        return castExpr;
    }

    public String toString() {
        return '(' + castType.toString() + ')';
    }

    // TRECHO REFATORADO: Classe extraída para lidar com conversão de tipos numéricos
    private static class NumberTypeConverter {
        
        /**
         * Converte um valor numérico para o tipo especificado
         * 
         * @param number o número a ser convertido
         * @param targetType o tipo de destino da conversão
         * @return o valor convertido para o tipo apropriado
         */
        public Object convertNumber(Number number, Type targetType) {
            if (targetType == Type.byteType)
                return new Byte(number.byteValue());
            if (targetType == Type.shortType)
                return new Short(number.shortValue());
            if (targetType == Type.intType)
                return new Integer(number.intValue());
            if (targetType == Type.longType)
                return new Long(number.longValue());
            if (targetType == Type.floatType)
                return new Float(number.floatValue());
            return new Double(number.doubleValue());
        }
    }
}

/**
 * # Refatoração da classe TypecastAST
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Extração de Método**: O método `computeValue()` contém uma longa sequência de condicionais para converter um número entre diferentes tipos primitivos. Esta lógica pode ser extraída para um método separado.
 * 2. **Extração de Classe**: A conversão de tipos numéricos representa uma responsabilidade separada que pode ser extraída para uma classe específica de conversão de tipos.
 * 3. **Extração de Método**: O método `parseComplete()` contém várias etapas sequenciais que podem ser extraídas como um método separado para melhorar a legibilidade.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método - initializeCastComponents()**:
 *     - De acordo com Martin Fowler, a extração de método é aplicável quando um bloco de código pode ser agrupado juntos. O método `parseComplete()` continha várias operações sequenciais que formam uma unidade lógica (inicialização dos componentes do cast).
 *     - Esta refatoração melhora a legibilidade ao dar um nome descritivo para o conjunto de operações e facilita a manutenção, caso seja necessário alterar a inicialização dos componentes no futuro.
 *
 * 2. **Extração de Classe - NumberTypeConverter**:
 *     - Seguindo o princípio de responsabilidade única (também abordado por Marco Tulio na Engenharia de Software Moderna), uma classe deve ter apenas uma razão para mudar. A conversão de tipos numéricos representa uma responsabilidade distinta da operação geral de typecast.
 *     - Esta refatoração ajuda a encapsular a lógica de conversão numérica em uma classe separada, tornando o código mais modular e mais fácil de manter.
 *     - Se a lógica de conversão precisar ser modificada no futuro (por exemplo, para adicionar suporte a novos tipos), essas mudanças ficariam confinadas à classe `NumberTypeConverter`.
 *
 * 3. **Extração de Método - convertNumber()**:
 *     - O método `computeValue()` original continha uma longa sequência de verificações condicionais para converter um valor numérico entre diferentes tipos. Esta lógica foi encapsulada em um método separado na classe extraída.
 *     - Esta refatoração deixa o método `computeValue()` mais simples e com um nível mais alto de abstração, delegando detalhes específicos sobre como converter os números para a classe especializada.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 2 (initializeCastComponents e convertNumber)
 *     - **Extração de Classe**: 1 (NumberTypeConverter)
 *
 * As refatorações realizadas melhoraram significativamente a estrutura do código, seguindo os princípios de coesão e responsabilidade única. A classe TypecastAST agora delega a funcionalidade específica de conversão de tipos para uma classe especializada, enquanto mantém sua responsabilidade principal de representar um nó AST de typecast.
 */