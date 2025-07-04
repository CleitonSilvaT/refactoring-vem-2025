package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class BooleanLiteralAST extends ExpressionAST {

    public BooleanLiteralAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Extração de método para inicializar o valor booleano
        inicializarValorBooleano();
    }

    // TRECHO REFATORADO: Método extraído para inicializar o valor booleano a partir do texto
    private void inicializarValorBooleano() {
        value = new Boolean(getText());
        type = Type.booleanType;
    }

    protected Type computeType() {
        return type;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        return value;
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Extração de método para criar uma lista de variáveis vazia
        return criarListaVariaveisVazia();
    }
    
    // TRECHO REFATORADO: Método extraído para criar uma lista de variáveis vazia
    private VarList criarListaVariaveisVazia() {
        return new VarList();
    }
}


/**
 * ## 1) Oportunidades de refatoração encontradas
 * A classe `BooleanLiteralAST` apresenta as seguintes oportunidades de refatoração:
 * 1. **Extração de Método**: O construtor realiza múltiplas responsabilidades: inicialização do objeto pai, criação de um valor booleano e definição do tipo. A lógica relacionada à inicialização do valor booleano poderia ser extraída para um método separado.
 * 2. **Extração de Método**: A criação do objeto VarList vazio pode ser extraída para melhorar a legibilidade e manutenibilidade.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarValorBooleano()`**:
 *     - Segundo Martin Fowler, a Extração de Método é uma das refatorações mais fundamentais, que consiste em pegar um trecho de código e transformá-lo em um método com nome significativo.
 *     - Este método separado torna mais claro o propósito dessa parte do código: converter o texto do token em um valor booleano e definir o tipo.
 *     - A extração melhora a coesão do construtor, deixando-o com uma única responsabilidade: inicializar o objeto.
 *
 * 2. **Extração do método `criarListaVariaveisVazia()`**:
 *     - Esta extração melhora a legibilidade do código ao dar um nome significativo à operação.
 *     - Embora o código original já fosse curto, a introdução de um método com nome descritivo documenta a intenção da operação.
 *     - Se no futuro a criação da lista precisar incluir alguma lógica adicional, essa mudança será localizada em um único método.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 2
 *     - **Extração de Método**: 2
 *     - **Extração de Classe**: 0
 *
 * As refatorações realizadas melhoraram a estrutura do código sem alterar seu comportamento, tornando-o mais legível e fácil de manter. A classe original era pequena e bem focada, então não houve necessidade de extrair novas classes, mas as extrações de método ajudaram a documentar melhor o propósito de cada parte do código.
 */