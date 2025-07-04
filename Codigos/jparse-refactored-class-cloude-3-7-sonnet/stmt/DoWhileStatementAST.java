package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class DoWhileAST extends StatementAST {

    private jparse.expr.ExpressionAST cond;
    private StatementAST stmt;

    public DoWhileAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        
        // TRECHO REFATORADO: Extraído método para inicializar nós filhos
        inicializarNosFilhos();
        
        // TRECHO REFATORADO: Extraído método para gerenciar contexto de break/continue
        processarBreakContinueContext();
    }

    // TRECHO REFATORADO: Método extraído para inicializar os nós filhos da árvore AST
    private void inicializarNosFilhos() {
        stmt = (StatementAST)getFirstChild();
        cond = extrairCondicao();
    }
    
    // TRECHO REFATORADO: Método extraído para obter a condição a partir da árvore AST
    private jparse.expr.ExpressionAST extrairCondicao() {
        return (jparse.expr.ExpressionAST)stmt.getNextSibling()
            .getNextSibling().getNextSibling();
    }
    
    // TRECHO REFATORADO: Método extraído para gerenciar o contexto de break/continue
    private void processarBreakContinueContext() {
        context.pushContinue(this);
        stmt.parseComplete();
        cond.parseComplete();
        context.popContinue();
    }

    protected Type[] computeExceptions() {
        return Type.mergeTypeLists(cond.getExceptionTypes(),
                           stmt.getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return stmt.nextControlPoints();
    }

    public VarList getVarList() {
        return new VarList(cond.getVarList(), stmt.getVarList());
    }

    public jparse.expr.ExpressionAST getCondition() {
        return cond;
    }

    public StatementAST getBody() {
        return stmt;
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe DoWhileAST, identifiquei as seguintes oportunidades para refatoração:
 * 1. O método `parseComplete()` tem várias responsabilidades diferentes e pode ser quebrado em métodos menores para melhorar a legibilidade.
 * 2. A navegação na árvore AST para encontrar a condição do loop é complexa e pode ser extraída para um método separado.
 * 3. O gerenciamento do contexto de break/continue pode ser encapsulado em um método próprio.
 *
 * # 2) Classe refatorada com os devidos comentários
 *
 * # 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarNosFilhos()`**:
 *     - Este método foi extraído do método `parseComplete()` para isolar a responsabilidade de inicializar os nós filhos da árvore AST.
 *     - Baseado no princípio da Responsabilidade Única (SRP) de Martin Fowler, cada método deve fazer apenas uma coisa.
 *     - Simplifica a compreensão do fluxo principal no método `parseComplete()`.
 *
 * 2. **Extração do método `extrairCondicao()`**:
 *     - Encapsula a complexa navegação pela árvore AST para obter o nó da condição.
 *     - Melhora a legibilidade, ocultando os detalhes de implementação de como a condição é encontrada.
 *     - Facilita mudanças futuras na estrutura da árvore AST, pois a lógica de navegação está isolada em um único lugar.
 *
 * 3. **Extração do método `processarBreakContinueContext()`**:
 *     - Isola o tratamento do contexto de break/continue em um método com nome descritivo.
 *     - Segue a recomendação de Marco Tulio em Engenharia de Software Moderna, onde métodos mais curtos e com nomes significativos melhoram a manutenibilidade.
 *     - Torna o fluxo do método `parseComplete()` mais claro e conciso.
 *
 * # 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 3 (inicializarNosFilhos, extrairCondicao, processarBreakContinueContext)
 *     - **Extração de Classe**: 0
 *
 * As refatorações realizadas focaram na melhoria da legibilidade e manutenibilidade do código, aplicando o princípio de "método com uma única responsabilidade" conforme recomendado por Martin Fowler. Não foi identificada necessidade de extração de classe neste caso específico, pois a classe já possui responsabilidades bem definidas e coesas para representar um nó AST do tipo do-while.
 */