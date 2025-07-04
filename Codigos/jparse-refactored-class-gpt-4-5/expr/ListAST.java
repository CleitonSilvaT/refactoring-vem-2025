package jparse.expr;

import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

// TRECHO REFATORADO: classe ajustada após extrações de métodos e criação de ExpressionList
public final class ListAST extends ExpressionAST implements JavaTokenTypes {

    private final ExpressionList expressionList;
    private Type[] types;

    public ListAST(final AST firstExpr) {
        super();
        initialize(ELIST, "ELIST");
        // TRECHO REFATORADO: extração de classe (ExpressionList)
        expressionList = new ExpressionList(firstExpr);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: delegação de método
        expressionList.parseComplete();
    }

    protected Type computeType() {
        types = expressionList.retrieveTypes();
        return (types.length == 0) ? null : types[types.length - 1];
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: delegação de método
        return expressionList.computeExceptions();
    }

    protected Object computeValue() {
        // TRECHO REFATORADO: delegação de método
        return expressionList.retrieveLastValue();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: delegação de método
        return expressionList.aggregateVarLists();
    }

    public ExpressionAST[] getList() {
        return expressionList.getExpressions();
    }

    public Type[] getTypes() {
        if (types == null)
            retrieveType();
        return types;
    }

    public String toString() {
        Type[] types = getTypes();
        return formatTypesToString(types);
    }

    // TRECHO REFATORADO: Extração de método para formatar tipos em String
    private String formatTypesToString(Type[] types) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 0; i < types.length - 1; i++) {
            buf.append(types[i].getName());
            buf.append(',');
        }
        if (types.length > 0)
            buf.append(types[types.length - 1].getName());
        return buf.toString();
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * Os seguintes pontos mostram potenciais refatorações identificadas para melhorar o código atual:
 * - **Repetição lógica em loops e tratamentos**: Observa-se repetidamente a iteração através dos `ExpressionAST[]` com processos semelhantes em diferentes partes da classe.
 * - **Responsabilidades distintas na classe**: A classe atual mistura responsabilidades distintas como gerenciar listas, tipos e exceções.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * - **Extração de Classe (ExpressionList)**:
 *     - Motivação conforme Martin Fowler (2018): A nova classe foi criada para agrupar métodos relacionados à gestão da lista de expressões. Melhora a coesão interna e reduz complexidade.
 *     - Motivação conforme Marco Tulio (2020): Isolar responsabilidades claras aumenta legibilidade e manutenção, permitindo evolução independente de partes diferentes do código.
 *
 * - **Extração de Métodos**:
 *     - Motivação conforme Martin Fowler (2018): Reduzimos duplicação lógica para formatar tipos em String com o método `formatTypesToString`.
 *     - Motivação conforme Marco Tulio (2020): Extrair métodos curtos facilita a compreensão imediata do código e sua reutilização futura.
 *
 * ## 4) Resumo das alterações:
 * - **Total de Refatorações**: 6
 *     - **Extração de Classe**: 1 vez (`ExpressionList`)
 *     - **Extração de Método**: 1 vez (`formatTypesToString`)
 *     - **Delegações de Método**: 4 vezes (`parseComplete`, `computeExceptions`, `computeValue`, `getVarList`)
 */