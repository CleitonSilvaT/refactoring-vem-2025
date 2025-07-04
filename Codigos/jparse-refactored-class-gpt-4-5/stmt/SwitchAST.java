package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class SwitchAST extends StatementAST implements JavaTokenTypes {

    private jparse.expr.ExpressionAST expr;

    private CaseGroupManager caseGroupManager; // TRECHO REFATORADO - nova classe gerenciadora dos grupos de casos

    public SwitchAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        extrairEProcessarExpressao(); // TRECHO REFATORADO - método extraído
        caseGroupManager = new CaseGroupManager(expr); // TRECHO REFATORADO
        caseGroupManager.processarCases(context); // TRECHO REFATORADO
    }

    private void extrairEProcessarExpressao() { // TRECHO REFATORADO - extração de método
        expr = (jparse.expr.ExpressionAST)getFirstChild().getNextSibling();
        expr.parseComplete();
    }

    protected Type[] computeExceptions() {
        return caseGroupManager.calcularTiposExcecao(expr); // TRECHO REFATORADO - extração de método para classe auxiliar
    }

    protected StatementAST[] computeControl() {
        return caseGroupManager.calcularPontosControle(); // TRECHO REFATORADO - extração de método para classe auxiliar
    }

    public VarList getVarList() {
        return caseGroupManager.obterVarList(expr); // TRECHO REFATORADO
    }

    public jparse.expr.ExpressionAST getExpression() {
        return expr;
    }

    public CaseGroupAST[] getCaseGroups() {
        return caseGroupManager.getCaseGroups(); // TRECHO REFATORADO
    }
}
/**
 *
 * ### 1) Oportunidades de refatoração encontradas:
 * - O método `parseComplete()` realiza várias tarefas distintas, indicando oportunidade para "Extração de Método".
 * - Os métodos `computeExceptions()` e `computeControl()` possuem trechos que manipulam listas, também boas candidatas para "Extração de Método".
 * - A manipulação do array `CaseGroupAST` ocorre em múltiplos locais, apontando para a possibilidade de extrair uma classe auxiliar gerenciadora, caracterizando uma oportunidade de "Extração de Classe".
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * As refatorações seguiram os princípios recomendados por Martin Fowler e Marco Túlio, especialmente focando em:
 * - **Extração de Método**: Identificada em diversos pontos para simplificar o método original (`parseComplete()`, `computeExceptions()` e `computeControl()`). Cada tarefa clara e distinta foi encapsulada em métodos próprios, melhorando a legibilidade e facilitando alterações futuras (Fowler, 2018).
 * - **Extração de Classe**: Foi realizada a extração da classe `CaseGroupManager` para delegar responsabilidades relacionadas ao gerenciamento, processamento e manipulação das estruturas de dados dos grupos de casos (`CaseGroupAST`). Essa ação está de acordo com o princípio de responsabilidade única, melhorando a coesão e modularidade do código (Marco Túlio, 2021).
 *
 * Essas alterações melhoram significativamente o design, tornando mais simples mudanças futuras e compreensão do código.
 * ### 4) Resumo das alterações:
 * - Total: **2 tipos de refatorações aplicados**
 * - Total de refatorações realizadas: **7**
 *     - **Extração de Método**: 6 ocorrências.
 *         - `extrairEProcessarExpressao`
 *         - `extrairGrupos`
 *         - `processarCases`
 *         - `calcularPontosControle`
 *         - `calcularTiposExcecao`
 *         - `obterVarList`
 *
 *     - **Extração de Classe**: 1 ocorrência.
 *         - `CaseGroupManager`
 */