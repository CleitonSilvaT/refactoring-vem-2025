package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class TypecastAST extends ExpressionAST implements JavaTokenTypes {

    private TypeAST castType;
    private ExpressionAST castExpr;

    public TypecastAST(final Token token) {
        super(token);
        setType(TYPECAST);
    }

    public void parseComplete() {
        inicializarCast(); // TRECHO REFATORADO - extração do método de inicialização
        castType.parseComplete();
        context.negative = false;
        castExpr.parseComplete();
    }

    // TRECHO REFATORADO - método extraído para inicialização de castType e castExpr
    private void inicializarCast() {
        castType = (TypeAST) getFirstChild();
        castExpr = (ExpressionAST) castType.getNextSibling().getNextSibling();
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

        final Number num = (Number)castVal;
        final Type theType = castType.retrieveType();

        // TRECHO REFATORADO - delegação para classe especializada na conversão de tipos
        return TipoWrapperConverter.converter(num, theType);
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
 * ## 1) Oportunidades de refatoração encontradas:
 * Os seguintes trechos foram identificados como candidatos para extração de métodos e classes devido a clareza de responsabilidades e eliminação da duplicação de código:
 * - O método `computeValue()` possui um trecho extenso verificando o tipo numérico e criando instâncias dos tipos wrappers. Este trecho pode ser extraído para um método separado, removendo o excesso de responsabilidades do método original.
 * - A lógica de inicialização em `parseComplete()` válida para definição da expressão e tipo do cast pode ser extraída para melhorar clareza.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * As decisões de extração adotadas seguiram os princípios propostos por Fowler (2018) e Marco Tulio (Engenharia de Software Moderna), baseando-se na separação clara de responsabilidades e remoção de duplicações ou métodos extensos:
 * - **Extração de Método (`inicializarCast()`)**: Simplifica e esclarece o fluxo inicial do método `parseComplete()`. Esta extração segue o princípio: "Extraia método quando uma fração do código está relacionada a uma tarefa conceitual bem definida". (Fowler, 2018).
 * - **Extração de Classe (`TipoWrapperConverter`)**: Isola a lógica de conversão entre tipos primitivos wrappers, removendo essa responsabilidade indireta da classe original. Conforme Fowler, uma classe deve ter somente uma responsabilidade clara e definida (Single Responsibility Principle). Além disso, facilita a manutenção e possível reutilização desta lógica futuramente.
 *
 * ## 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 2
 * - **Divisão por tipo**:
 *     - Extração de Método: 1 (`inicializarCast()`)
 *     - Extração de Classe: 1 (`TipoWrapperConverter`)
 */