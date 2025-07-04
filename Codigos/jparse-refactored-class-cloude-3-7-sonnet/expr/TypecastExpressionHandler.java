package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class TypecastAST extends ExpressionAST
    implements JavaTokenTypes {

    private TypeAST castType;
    private ExpressionAST castExpr;

    public TypecastAST(final Token token) {
        super(token);
        setType(TYPECAST);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído inicialização dos componentes para método separado
        inicializarComponentes();
        
        castType.parseComplete();
        context.negative = false;
        castExpr.parseComplete();
    }

    // TRECHO REFATORADO: Método extraído para inicializar os componentes de casting
    private void inicializarComponentes() {
        castType = (TypeAST)getFirstChild();
        castExpr = (ExpressionAST)castType.getNextSibling().getNextSibling();
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

        // TRECHO REFATORADO: Extraída a lógica de converter o valor numérico para um método separado
        return converterValorNumerico((Number)castVal);
    }
    
    // TRECHO REFATORADO: Método extraído para lidar com a conversão de valores numéricos
    private Object converterValorNumerico(Number num) {
        final Type theType = castType.retrieveType();
        
        if (theType == Type.byteType)
            return new Byte(num.byteValue());
        if (theType == Type.shortType)
            return new Short(num.shortValue());
        if (theType == Type.intType)
            return new Integer(num.intValue());
        if (theType == Type.longType)
            return new Long(num.longValue());
        if (theType == Type.floatType)
            return new Float(num.floatValue());
        return new Double(num.doubleValue());
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
}

/**
 * # Refatoração do código TypecastAST.java
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `TypecastAST.java`, identifiquei as seguintes oportunidades para refatoração:
 * 1. **Extração de Método**: O método `computeValue()` contém um longo bloco condicional para converter valores numéricos. Este bloco pode ser extraído para um método separado, seguindo o princípio de responsabilidade única.
 * 2. **Extração de Método**: O método `parseComplete()` realiza várias operações distintas que podem ser melhor organizadas em métodos separados para aumentar a legibilidade.
 * 3. **Remoção de comentários**: Conforme solicitado, todos os comentários serão removidos na versão refatorada.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarComponentes()`**:
 *     - O método `parseComplete()` realizava duas operações distintas: inicializar os componentes do casting e processá-los. Seguindo o princípio da responsabilidade única de Martin Fowler, extraí a primeira parte para um método separado.
 *     - Isso torna o código mais legível e cada método tem uma responsabilidade clara. Esse tipo de refatoração ajuda a entender melhor o fluxo de inicialização.
 *
 * 2. **Extração do método `converterValorNumerico()`**:
 *     - O método `computeValue()` continha um longo bloco condicional para converter valores numéricos entre diferentes tipos primitivos.
 *     - Segundo Fowler, quando um método tem um longo bloco de código que pode ser agrupado, é candidato a extração. A refatoração de "Extração de Método" aplicada aqui melhora a legibilidade e manutenção, criando um método com nome auto-explicativo.
 *     - Com isso, o código fica mais coeso, com o método original focando na lógica de determinar se o valor precisa de conversão, e o novo método tratando apenas da conversão em si.
 *
 * 3. **Remoção de comentários**:
 *     - Todos os comentários originais foram removidos conforme solicitado. Apenas os comentários de refatoração foram adicionados para indicar as mudanças realizadas.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 2
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 2 (métodos `inicializarComponentes()` e `converterValorNumerico()`)
 *     - **Extração de Classe**: 0 (Não foram identificadas oportunidades adequadas para extração de classe neste código)
 *
 * Estas refatorações melhoram a legibilidade e a manutenibilidade do código, seguindo os princípios de organização e coesão recomendados por Martin Fowler, sem alterar o comportamento funcional do programa.
 */