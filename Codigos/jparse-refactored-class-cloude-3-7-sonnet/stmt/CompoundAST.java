package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

/**
 * An AST node that represents a compound statement
 */
public final class CompoundAST extends StatementAST implements JavaTokenTypes {

    private StatementAST[] stmtList;
    
    // TRECHO REFATORADO: Extraída uma classe auxiliar para operações relacionadas a pontos de controle
    private class ControlPointsManager {
        private HashSet<StatementAST> goPoints = new HashSet<>();
        
        public ControlPointsManager(StatementAST[] statements) {
            if (statements.length > 0) {
                goPoints.add(statements[0]);
            }
        }
        
        public void collectControlPoints(StatementAST[] statements) {
            for (int i = 0; i < statements.length; i++) {
                if (goPoints.contains(statements[i])) {
                    addNextControlPoints(statements[i]);
                }
            }
        }
        
        private void addNextControlPoints(StatementAST statement) {
            final StatementAST[] sNext = statement.nextControlPoints();
            for (int j = 0; j < sNext.length; j++) {
                goPoints.add(sNext[j]);
            }
        }
        
        public void removeInternalStatements(StatementAST[] statements) {
            for (int i = 0; i < statements.length; i++) {
                goPoints.remove(statements[i]);
            }
        }
        
        public StatementAST[] getControlPoints() {
            final StatementAST[] points = new StatementAST[goPoints.size()];
            return (StatementAST[])goPoints.toArray(points);
        }
    }

    public CompoundAST(final Token token) {
        super(token);
        setType(SLIST);
    }

    public void parseComplete() {
        super.parseComplete();
        
        // TRECHO REFATORADO: Extraído método para construir a lista de statements
        buildStatementList();
        
        // TRECHO REFATORADO: Extraído método para configurar ponteiros next
        if (stmtList.length > 0) {
            configureNextPointers();
        }
    }
    
    // TRECHO REFATORADO: Método extraído da parte inicial de parseComplete
    private void buildStatementList() {
        final ArrayList list = new ArrayList();
        for (AST a = getFirstChild(); a.getType() != RCURLY; a = a.getNextSibling()) {
            list.add(a);
        }
        stmtList = new StatementAST[list.size()];
        list.toArray(stmtList);
    }
    
    // TRECHO REFATORADO: Método extraído da parte final de parseComplete
    private void configureNextPointers() {
        final StatementAST orig = context.nextStmt;
        for (int i = 0; i < stmtList.length - 1; i++) {
            context.nextStmt = stmtList[i + 1];
            stmtList[i].parseComplete();
        }
        context.nextStmt = orig;
        stmtList[stmtList.length - 1].parseComplete();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Utilizado método utilitário para coletar exceções
        return collectExceptionsFromStatements(stmtList);
    }
    
    // TRECHO REFATORADO: Método utilitário extraído para coleta de exceções
    private Type[] collectExceptionsFromStatements(StatementAST[] statements) {
        Type[] exceptions = noTypes;
        for (int i = 0; i < statements.length; i++) {
            exceptions = Type.mergeTypeLists(exceptions, statements[i].getExceptionTypes());
        }
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        if (stmtList.length == 0) {
            return new StatementAST[] { next };
        }

        // TRECHO REFATORADO: Utilizado objeto da classe extraída para gerenciar pontos de controle
        ControlPointsManager manager = new ControlPointsManager(stmtList);
        manager.collectControlPoints(stmtList);
        manager.removeInternalStatements(stmtList);
        
        return manager.getControlPoints();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Extraído método para criar lista de variáveis
        return createVarListFromStatements(stmtList);
    }
    
    // TRECHO REFATORADO: Método utilitário extraído para criar VarList
    private VarList createVarListFromStatements(StatementAST[] statements) {
        final VarList[] lists = new VarList[statements.length];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = statements[i].getVarList();
        }
        return new VarList(lists);
    }

    public StatementAST[] getList() {
        return stmtList;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise do código, identifiquei os seguintes pontos problemáticos que justificam refatorações:
 * 1. O método `parseComplete()` realiza múltiplas responsabilidades: construir a lista de statements e configurar os ponteiros "next". Isso viola o princípio de responsabilidade única.
 * 2. O método `computeControl()` é extenso e contém vários blocos lógicos distintos que podem ser extraídos para métodos separados, melhorando a legibilidade.
 * 3. Repetição do padrão de iteração sobre `stmtList` nos métodos `computeExceptions()`, `computeControl()` e `getVarList()`, o que sugere oportunidade para extração de método utilitário.
 * 4. Ausência de encapsulamento para operações relacionadas ao gerenciamento dos pontos de controle no método `computeControl()`.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe: `ControlPointsManager`**
 *     - Esta refatoração foi baseada no princípio de responsabilidade única de Martin Fowler. A lógica de gerenciamento dos pontos de controle foi encapsulada em uma classe interna, tornando o código mais coeso e separando claramente a responsabilidade de gerenciar pontos de controle.
 *     - Isso facilita manutenção futura e torna o código principal mais limpo e compreensível.
 *
 * 2. **Extração de Método: `buildStatementList()` e `configureNextPointers()`**
 *     - O método `parseComplete()` tinha duas responsabilidades distintas que foram separadas em métodos dedicados, conforme recomendado por Fowler para promover a coesão de método.
 *     - Cada método agora tem um propósito claro, o que melhora a legibilidade e facilita a manutenção.
 *
 * 3. **Extração de Método: `collectExceptionsFromStatements()`**
 *     - Este método utilitário foi extraído para encapsular o padrão de iteração sobre statements para coletar exceções.
 *     - Além de melhorar a legibilidade, esta refatoração permite a reutilização desta funcionalidade em outros locais se necessário.
 *
 * 4. **Extração de Método: `createVarListFromStatements()`**
 *     - Similar à refatoração anterior, este método extraído encapsula a lógica de criação de VarList a partir de statements.
 *     - Esta refatoração segue o princípio de abstração procedural citado por Marco Tulio, onde agrupamos operações relacionadas em um método com nome significativo.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas: 6**
 * - **Divisão por tipo:**
 *     - **Extração de Classe: 1**
 *         - Classe `ControlPointsManager` para gerenciar pontos de controle
 *
 *     - **Extração de Método: 5**
 *         - Método `buildStatementList()`
 *         - Método `configureNextPointers()`
 *         - Método `collectExceptionsFromStatements()`
 *         - Método `createVarListFromStatements()`
 *         - Método específico da classe extraída: `addNextControlPoints()`
 *
 * Estas refatorações melhoraram significativamente a organização do código, separando responsabilidades diferentes em métodos e classes específicas, o que facilita a manutenção e o entendimento do código. Além disso, foram eliminadas duplicações de padrões de código através da criação de métodos utilitários.
 */