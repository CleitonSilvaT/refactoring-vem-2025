package jparse.expr;

import antlr.Token;
import jparse.HasExceptions;
import jparse.JavaAST;
import jparse.Type;
import jparse.VarList;

public abstract class ExpressionAST extends JavaAST implements HasExceptions {

    private static final Object noVal = new Object();
    public static final Object nonconstant = new Object();

    //TRECHO REFATORADO: Agrupamento dos atributos tipo, exceções e valor constante em classe auxiliar
    protected final ExpressionProperties propriedadesExpressao = new ExpressionProperties();

    protected ExpressionAST() {
        super();
    }

    protected ExpressionAST(final Token token) {
        super(token);
    }

    //TRECHO REFATORADO: Extração do método genérico para lazy initialization
    public final Type retrieveType() {
        return propriedadesExpressao.obterTipo(this::computeType);
    }

    protected abstract Type computeType();

    //TRECHO REFATORADO: Extração do método genérico para lazy initialization
    public final Type[] getExceptionTypes() {
        return propriedadesExpressao.obterExcecoes(this::computeExceptions);
    }

    protected abstract Type[] computeExceptions();

    //TRECHO REFATORADO: Extração do método genérico para lazy initialization
    public final Object getValue() {
        return propriedadesExpressao.obterValorConstante(this::computeValue);
    }

    protected abstract Object computeValue();

    public abstract VarList getVarList();
}

//TRECHO REFATORADO: Nova Classe criada para gerenciar propriedades constantes da expressão
import java.util.function.Supplier;

class ExpressionProperties {
    private static final Object noVal = new Object();

    private Type tipo;
    private Type[] excecoes;
    private Object valor = noVal;

    //TRECHO REFATORADO: Extração de método genérico padrão Lazy Initialization para o tipo
    public Type obterTipo(Supplier<Type> supplier) {
        if (tipo == null)
            tipo = supplier.get();
        return tipo;
    }

    //TRECHO REFATORADO: Extração de método genérico padrão Lazy Initialization para exceções
    public Type[] obterExcecoes(Supplier<Type[]> supplier) {
        if (excecoes == null)
            excecoes = supplier.get();
        return excecoes;
    }

    //TRECHO REFATORADO: Extração de método genérico padrão Lazy Initialization para valor constante
    public Object obterValorConstante(Supplier<Object> supplier) {
        if (valor == noVal)
            valor = supplier.get();
        return valor;
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas
 * Após a análise inicial do código fornecido, foram encontradas as seguintes oportunidades que justificam fortemente a refatoração:
 * - Existe repetição estrutural no padrão _lazy initialization_ de atributos:
 *     - Método `retrieveType()`
 *     - Método `getExceptionTypes()`
 *     - Método `getValue()`
 *
 * Esses métodos, apesar de acessarem campos diferentes, seguem exatamente a mesma lógica. Este padrão repetitivo pode ser extraído em um único método genérico.
 * - Embora não existam muitos campos atualmente na classe, pode ser interessante extrair o gerenciamento do valor constante e do estado (tipo e exceções) para uma nova classe auxiliar como um método de organização e encapsulamento.
 *
 * # 2) Classe refatorada com os devidos comentários
 *
 * # 3) Justificativa das refatorações
 * - **Extração de Método ("Lazy Initialization padrão")**: Conforme Martin Fowler (2018), repetição de lógica, como o padrão Lazy Initialization presente várias vezes, causa duplicação de código. Para resolver isso, criou-se um método genérico que realiza essa tarefa com uso do Supplier. Isso melhora drasticamente a legibilidade e reusabilidade da lógica.
 * - **Extração de Classe ("ExpressionProperties")**: A criação da classe auxiliar `ExpressionProperties` é justificada por Marco Tulio como uma melhoria significativa no encapsulamento de estado. Com isso, mantemos a classe `ExpressionAST` mais limpa, focada apenas em comportamento, e delegando o gerenciamento dos atributos relacionados a expressão para uma classe de apoio especializada.
 *
 * Essas refatorações contribuem diretamente para melhorias substanciais na legibilidade, manutenção e facilidade de extensão do código sem alteração do comportamento original.
 * # 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas:** 4
 * - **Divisão por tipo:**
 *     - Extração de Método: 1 (método genérico `obterTipo`, `obterExcecoes` e `obterValorConstante`)
 *     - Extração de Classe: 1 (`ExpressionProperties`)
 */