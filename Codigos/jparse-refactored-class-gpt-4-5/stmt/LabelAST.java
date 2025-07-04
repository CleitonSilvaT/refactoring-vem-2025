package jparse.stmt;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class LabelAST extends StatementAST implements JavaTokenTypes {

    private LabeledStatement labeled;

    public LabelAST(final Token token) {
        super(token);
        setType(LABELED_STAT);
    }

    public void parseComplete() {
        super.parseComplete();
        labeled = criarLabeledStatement(); // TRECHO REFATORADO - Extração de Método para clareza
        labeled.parseComplete();
    }

    // TRECHO REFATORADO - Método extraído para encapsular obtenção de StatementAST
    private LabeledStatement criarLabeledStatement() {
        StatementAST statement = (StatementAST)getFirstChild().getNextSibling();
        return new LabeledStatement(statement);
    }

    protected Type[] computeExceptions() {
        return labeled.computeExceptions(); // TRECHO REFATORADO - delegação a nova classe
    }

    protected StatementAST[] computeControl() {
        return labeled.computeControl(); // TRECHO REFATORADO - delegação a nova classe
    }

    public VarList getVarList() {
        return labeled.getVarList(); // TRECHO REFATORADO - delegação a nova classe
    }

    public StatementAST getStatement() {
        return labeled.getStatement(); // TRECHO REFATORADO - delegação a nova classe
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * - **Método `parseComplete()`**: Possui múltiplas atividades no mesmo método (violação do princípio da responsabilidade única), primeiro ele realiza atividade própria e em seguida obtém um nó filho e faz o parsing do mesmo. Cabe aqui uma **Extração de Método**, para melhorar clareza e coesão.
 * - **Métodos delegadores** (`computeExceptions()`, `computeControl()`, `getVarList()`): Esses métodos fazem chamadas diretas ao atributo interno `labeled`. Poderiam ser abstraídos, melhorando a legibilidade e separando a delegação em uma nova classe encapsuladora. Cabe aqui uma oportunidade de **Extração de Classe** (Delegação).
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * As decisões foram embasadas conforme Martin Fowler (Refactoring, 2018) e Marco Túlio (Engenharia de Software Moderna), objetivando clareza, modularização, e redução das responsabilidades múltiplas dentro de um único método ou classe:
 * - **Extração de Classe** (LabeledStatement):
 *     - Removeu duplicações relacionadas a delegações.
 *     - Separou responsabilidades diferentes de gerenciamento do nó rotulado. Agora há uma classe única e coesa tratando do comportamento específico do statement rotulado.
 *
 * - **Extração de Método** (`criarLabeledStatement()`):
 *     - Isolou a lógica de obtenção e preparo do tipo `StatementAST` em um método separado, favorecendo uma melhor legibilidade e coesão dentro do método `parseComplete()`.
 *
 * Estas melhorias seguem os bons princípios de modularidade, coesão e legibilidade defendidos pelos autores supracitados.
 * ## 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - Extração de Método: 1
 *     - Extração de Classe: 1 (resultando em remoção de três duplicações por delegação)
 */