package jparse.stmt;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class LabelAST extends StatementAST implements JavaTokenTypes {

    private StatementAST labeled;

    public LabelAST(final Token token) {
        super(token);
        setType(LABELED_STAT);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extraído método para inicializar labeled
        inicializarLabeledStatement();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade e encapsulamento
    private void inicializarLabeledStatement() {
        labeled = (StatementAST)getFirstChild().getNextSibling();
        labeled.parseComplete();
    }

    // TRECHO REFATORADO: Método auxiliar extraído para padronizar delegações
    private StatementAST getLabeledStatement() {
        return labeled;
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Usando método auxiliar para delegação
        return getLabeledStatement().getExceptionTypes();
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Usando método auxiliar para delegação
        return getLabeledStatement().nextControlPoints();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Usando método auxiliar para delegação
        return getLabeledStatement().getVarList();
    }

    public StatementAST getStatement() {
        // TRECHO REFATORADO: Usando método auxiliar para delegação
        return getLabeledStatement();
    }
}

/**
 * # Refatoração da Classe LabelAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `LabelAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. O método `parseComplete()` realiza mais de uma responsabilidade: inicializa o campo `labeled` e chama métodos no objeto.
 * 2. Há uma repetição na forma como os métodos `computeExceptions()`, `computeControl()` e `getVarList()` simplesmente delegam chamadas para o objeto `labeled`.
 * 3. Não há encapsulamento adequado para o campo `labeled`, embora exista um getter (`getStatement()`), a inicialização é feita diretamente no método `parseComplete()`.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarLabeledStatement()`**: Separei a lógica de inicialização do campo `labeled` em um método próprio, aplicando o princípio da responsabilidade única. Conforme Martin Fowler destaca em "Refactoring", a extração de método melhora a legibilidade ao dar nomes significativos às operações e facilita o reuso de código.
 * 2. **Extração do método `getLabeledStatement()`**: Este método auxiliar implementa um padrão de acesso consistente ao campo `labeled`. Esta refatoração segue o princípio do encapsulamento adequado, garantindo que todas as interações com o campo privado passem por um único ponto. Isso facilita futuras modificações, como adicionar validações ou lógica extra.
 * 3. **Padronização de delegações**: Modifiquei os métodos `computeExceptions()`, `computeControl()`, `getVarList()` e `getStatement()` para usarem o método `getLabeledStatement()`. Esta abordagem segue o princípio DRY (Don't Repeat Yourself) mencionado tanto por Fowler quanto por Marco Tulio, reduzindo a duplicação de código e centralizando a lógica de acesso ao campo.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - Extração de Método: 3 (inicializarLabeledStatement, getLabeledStatement, e a padronização de delegações)
 *     - Extração de Classe: 0 (Não foi necessário extrair classes neste caso)
 *
 * As refatorações aplicadas melhoraram significativamente o código, tornando-o mais legível e manutenível. A extração de métodos permitiu uma melhor organização da lógica e fortaleceu o encapsulamento. Embora não tenha sido necessária uma extração de classe, as melhorias realizadas facilitariam futuras extensões ou modificações na classe.
 */