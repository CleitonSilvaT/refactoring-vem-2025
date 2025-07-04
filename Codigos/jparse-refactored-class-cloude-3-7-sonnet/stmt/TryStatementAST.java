package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class TryAST extends StatementAST implements JavaTokenTypes {

    private CompoundAST block;
    private CatchAST[] catchClause;
    private CompoundAST finallyClause;

    public TryAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();

        // TRECHO REFATORADO: Extraído método para processar o bloco try
        processarBlocoTry();
        
        // TRECHO REFATORADO: Extraído método para processar os blocos catch
        processarBlocosCatch();
        
        // TRECHO REFATORADO: Extraído método para processar o bloco finally
        processarBlocoFinally();
    }
    
    // TRECHO REFATORADO: Método extraído para processar o bloco try
    private void processarBlocoTry() {
        block = (CompoundAST)getFirstChild();
        block.parseComplete();
    }
    
    // TRECHO REFATORADO: Método extraído para processar os blocos catch
    private void processarBlocosCatch() {
        final ArrayList list = new ArrayList();
        AST a;
        for (a = block.getNextSibling(); a != null && a instanceof CatchAST;
             a = a.getNextSibling()) {
            list.add(a);
        }
        catchClause = new CatchAST[list.size()];
        list.toArray(catchClause);
        for (int i = 0; i < catchClause.length; i++) {
            catchClause[i].parseComplete();
        }
    }
    
    // TRECHO REFATORADO: Método extraído para processar o bloco finally
    private void processarBlocoFinally() {
        AST a = block.getNextSibling();
        while (a != null && a instanceof CatchAST) {
            a = a.getNextSibling();
        }
        if (a != null && a.getType() == LITERAL_finally) {
            finallyClause = (CompoundAST)a.getNextSibling();
            finallyClause.parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Extraído método para calcular exceções do bloco try
        Type[] exceptions = calcularExcecoesBlocoTry();

        // TRECHO REFATORADO: Extraído método para processar exceções dos blocos catch
        exceptions = processarExcecoesBlocosCatch(exceptions);

        // TRECHO REFATORADO: Extraído método para adicionar exceções do bloco finally
        exceptions = adicionarExcecoesBlocoFinally(exceptions);

        return exceptions;
    }
    
    // TRECHO REFATORADO: Método extraído para calcular exceções do bloco try
    private Type[] calcularExcecoesBlocoTry() {
        return block.getExceptionTypes();
    }
    
    // TRECHO REFATORADO: Método extraído para processar exceções dos blocos catch
    private Type[] processarExcecoesBlocosCatch(Type[] exceptions) {
        for (int i = 0; i < catchClause.length; i++) {
            exceptions = catchClause[i].removeCaughtException(exceptions);
            exceptions = Type.mergeTypeLists(exceptions, 
                    catchClause[i].getExceptionTypes());
        }
        return exceptions;
    }
    
    // TRECHO REFATORADO: Método extraído para adicionar exceções do bloco finally
    private Type[] adicionarExcecoesBlocoFinally(Type[] exceptions) {
        if (finallyClause != null) {
            exceptions = Type.mergeTypeLists(exceptions, finallyClause.getExceptionTypes());
        }
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        // TRECHO REFATORADO: Extraído classe para gerenciar pontos de controle
        ControlPointsCollector pointsCollector = new ControlPointsCollector();
        
        // TRECHO REFATORADO: Uso da classe extraída para coletar pontos de controle
        pointsCollector.adicionarPontosControle(block.nextControlPoints());
        
        for (int i = 0; i < catchClause.length; i++) {
            pointsCollector.adicionarPontosControle(catchClause[i].nextControlPoints());
        }
        
        if (finallyClause != null) {
            pointsCollector.adicionarPontosControle(finallyClause.nextControlPoints());
        }

        return pointsCollector.obterPontosControle();
    }
    
    // TRECHO REFATORADO: Classe extraída para gerenciar pontos de controle
    private class ControlPointsCollector {
        private final HashSet pontosControle = new HashSet();
        
        public void adicionarPontosControle(StatementAST[] pontos) {
            for (int i = 0; i < pontos.length; i++) {
                pontosControle.add(pontos[i]);
            }
        }
        
        public StatementAST[] obterPontosControle() {
            final StatementAST[] points = new StatementAST[pontosControle.size()];
            return (StatementAST[])pontosControle.toArray(points);
        }
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Extraído métodos para processamento de VarList
        VarList list = obterVarListBlocoTry();
        
        if (catchClause.length > 0) {
            list = combinarVarListComBlocosCatch(list);
        }
        
        return combinarVarListComBlocoFinally(list);
    }
    
    // TRECHO REFATORADO: Método extraído para obter VarList do bloco try
    private VarList obterVarListBlocoTry() {
        return block.getVarList();
    }
    
    // TRECHO REFATORADO: Método extraído para combinar VarList com blocos catch
    private VarList combinarVarListComBlocosCatch(VarList list) {
        VarList[] lists = new VarList[catchClause.length];
        for (int i = 0; i < catchClause.length; i++) {
            lists[i] = catchClause[i].getVarList();
        }
        return new VarList(list, new VarList(lists));
    }
    
    // TRECHO REFATORADO: Método extraído para combinar VarList com bloco finally
    private VarList combinarVarListComBlocoFinally(VarList list) {
        return (finallyClause == null)
            ? list
            : new VarList(list, finallyClause.getVarList());
    }

    public CompoundAST getTryBlock() {
        return block;
    }

    public CatchAST[] getCatchClauses() {
        return catchClause;
    }

    public CompoundAST getFinallyClause() {
        return finallyClause;
    }
}

/**
 * # Análise de Refatoração da Classe TryAST
 * Vou iniciar a refatoração da classe TryAST, removendo os comentários e aplicando extrações de métodos e classes conforme solicitado.
 * ## 1) Oportunidades de refatoração encontradas
 * - **Método `parseComplete()`**: Método muito longo que realiza múltiplas responsabilidades distintas: processamento do bloco try, dos blocos catch e do bloco finally. Cada uma dessas partes pode ser extraída em métodos separados.
 * - **Método `computeControl()`**: Contém código duplicado para adicionar elementos a um conjunto. Este padrão é repetido três vezes e pode ser extraído para um método utilitário.
 * - **Método `getVarList()`**: Possui lógica complexa que pode ser dividida em métodos menores para melhorar a legibilidade.
 * - **Manipulação de exceções no método `computeExceptions()`**: O processamento das exceções para os blocos catch e finally pode ser extraído para métodos separados.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * ### Extração de Métodos:
 * 1. **Métodos `processarBlocoTry()`, `processarBlocosCatch()` e `processarBlocoFinally()`**:
 *     - O método original `parseComplete()` estava realizando várias tarefas diferentes. Seguindo o princípio da responsabilidade única (SRP) de Martin Fowler, dividi esse método em três métodos menores, cada um com uma única responsabilidade bem definida.
 *     - A extração facilita a compreensão e manutenção, pois cada método agora possui uma finalidade clara e específica.
 *
 * 2. **Métodos `calcularExcecoesBlocoTry()`, `processarExcecoesBlocosCatch()` e `adicionarExcecoesBlocoFinally()`**:
 *     - O método `computeExceptions()` também tratava diferentes aspectos do cálculo de exceções. A extração desses métodos ajuda a isolar e nomear adequadamente cada responsabilidade.
 *     - Esta refatoração facilita a manutenção futura, pois possíveis alterações em uma das etapas não afetarão as outras.
 *
 * 3. **Métodos `obterVarListBlocoTry()`, `combinarVarListComBlocosCatch()` e `combinarVarListComBlocoFinally()`**:
 *     - A lógica de obtenção da lista de variáveis foi dividida em métodos menores e mais coesos, seguindo o mesmo princípio de responsabilidade única.
 *     - Os nomes escolhidos descrevem claramente o propósito de cada método, melhorando a legibilidade do código.
 *
 * ### Extração de Classe:
 * 1. **Classe `ControlPointsCollector`**:
 *     - O padrão de adicionar elementos a um conjunto e depois convertê-los em um array estava duplicado em várias partes do método `computeControl()`.
 *     - Criei uma classe interna para encapsular essa responsabilidade, seguindo o princípio de encapsulamento de algoritmos comuns em uma única classe.
 *     - Esta refatoração elimina a duplicação de código e melhora a coesão, pois a lógica de coleta dos pontos de controle agora está centralizada.
 *
 * ## 4) Resumo das alterações
 * ### Quantidade total de refatorações realizadas: 11
 * ### Divisão por tipo:
 * - **Extração de Método**: 10
 *     - processarBlocoTry()
 *     - processarBlocosCatch()
 *     - processarBlocoFinally()
 *     - calcularExcecoesBlocoTry()
 *     - processarExcecoesBlocosCatch()
 *     - adicionarExcecoesBlocoFinally()
 *     - obterVarListBlocoTry()
 *     - combinarVarListComBlocosCatch()
 *     - combinarVarListComBlocoFinally()
 *     - métodos da classe interna ControlPointsCollector
 *
 * - **Extração de Classe**: 1
 *     - ControlPointsCollector
 *
 * As refatorações realizadas seguem os princípios descritos por Martin Fowler e Marco Tulio, onde buscamos melhorar a organização, legibilidade e manutenibilidade do código através da extração de métodos e classes, sem alterar seu comportamento original. A divisão de responsabilidades e a eliminação de duplicação de código são os principais benefícios obtidos.
 */