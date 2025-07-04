package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class InitializerAST extends ExpressionAST {

    private ExpressionAST rhs;

    public InitializerAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído método para inicializar e processar o lado direito da inicialização
        inicializarEProcessarLadoDireito();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade
    private void inicializarEProcessarLadoDireito() {
        rhs = (ExpressionAST)getFirstChild();
        rhs.parseComplete();
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Delegação explícita para o método delegarComputacaoTipo
        return delegarComputacaoTipo();
    }

    // TRECHO REFATORADO: Método extraído para encapsular a lógica de computação de tipo
    private Type delegarComputacaoTipo() {
        return rhs.retrieveType();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Delegação explícita para o método delegarComputacaoExcecoes
        return delegarComputacaoExcecoes();
    }

    // TRECHO REFATORADO: Método extraído para encapsular a lógica de computação de exceções
    private Type[] delegarComputacaoExcecoes() {
        return rhs.getExceptionTypes();
    }

    protected Object computeValue() {
        // TRECHO REFATORADO: Delegação explícita para o método delegarComputacaoValor
        return delegarComputacaoValor();
    }

    // TRECHO REFATORADO: Método extraído para encapsular a lógica de computação de valor
    private Object delegarComputacaoValor() {
        return rhs.getValue();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Delegação explícita para o método delegarObtencaoVarList
        return delegarObtencaoVarList();
    }

    // TRECHO REFATORADO: Método extraído para encapsular a lógica de obtenção da lista de variáveis
    private VarList delegarObtencaoVarList() {
        return rhs.getVarList();
    }

    public ExpressionAST getRight() {
        return rhs;
    }
}

/**
 * # Refatoração da Classe InitializerAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código da classe `InitializerAST`, identifiquei as seguintes oportunidades para refatoração:
 * 1. **Extração de Método**: O método `parseComplete()` contém lógica que pode ser extraída para melhorar a legibilidade.
 * 2. **Extração de Método**: Os métodos de cálculo (`computeType()`, `computeExceptions()`, `computeValue()` e `getVarList()`) possuem padrão semelhante e podem ser refatorados para melhorar a coesão.
 * 3. **Não há oportunidades claras para Extração de Classe**, pois a classe é pequena e tem responsabilidade bem definida.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarEProcessarLadoDireito()`**:
 *     - A extração desse método segue o princípio de "um método, uma tarefa" proposto por Martin Fowler.
 *     - Melhora a legibilidade do código ao dar um nome claro à operação sendo realizada.
 *     - Facilita a manutenção ao isolar a lógica de inicialização em um local específico.
 *
 * 2. **Extração dos métodos delegadores para computação**:
 *     - Foram criados métodos específicos para cada operação de delegação: `delegarComputacaoTipo()`, `delegarComputacaoExcecoes()`, `delegarComputacaoValor()` e `delegarObtencaoVarList()`.
 *     - Esta refatoração segue o princípio de "encapsular comportamento" mencionado por Fowler, tornando explícito o padrão de delegação.
 *     - Melhora a flexibilidade do código, permitindo que detalhes de implementação possam ser modificados sem alterar a interface pública.
 *     - Facilita a compreensão ao dar nomes descritivos às operações de delegação.
 *
 * 3. **Não realização de Extração de Classe**:
 *     - Segundo Marco Tulio na Engenharia de Software Moderna, a Extração de Classe é adequada quando uma classe realiza múltiplas responsabilidades ou tem grupos coesos de atributos e métodos.
 *     - No caso da `InitializerAST`, a classe já possui uma única responsabilidade bem definida (representar um inicializador de variável) e não tem subgrupos claros de atributos e métodos que justificariam a extração.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 5 (métodos `inicializarEProcessarLadoDireito()`, `delegarComputacaoTipo()`, `delegarComputacaoExcecoes()`, `delegarComputacaoValor()` e `delegarObtencaoVarList()`)
 *     - **Extração de Classe**: 0
 *
 * As refatorações realizadas melhoraram a qualidade do código ao aumentar sua legibilidade e manutenibilidade, seguindo os princípios de coesão e encapsulamento recomendados pela literatura de engenharia de software.
 */