package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class CompoundAST extends StatementAST implements JavaTokenTypes {

    private StatementAST[] stmtList;

    public CompoundAST(final Token token) {
        super(token);
        setType(SLIST);
    }

    public void parseComplete() {
        super.parseComplete();
        extrairStatements(); // TRECHO REFATORADO
        configurarProximosStatements(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Novo método para extrair statements filhos para o array stmtList
    private void extrairStatements() {
        final ArrayList list = new ArrayList();
        for (AST a = getFirstChild(); a.getType() != RCURLY; a = a.getNextSibling())
            list.add(a);
        stmtList = new StatementAST[list.size()];
        list.toArray(stmtList);
    }

    // TRECHO REFATORADO - Novo método para configurar os próximos statements
    private void configurarProximosStatements() {
        if (stmtList.length > 0) {
            final StatementAST original = context.nextStmt;
            for (int i = 0; i < stmtList.length - 1; i++) {
                context.nextStmt = stmtList[i + 1];
                stmtList[i].parseComplete();
            }
            context.nextStmt = original;
            stmtList[stmtList.length - 1].parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        return combinarExcecoes(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Novo método para encapsular combinação de exceções 
    private Type[] combinarExcecoes() {
        Type[] exceptions = noTypes;
        for (int i = 0; i < stmtList.length; i++)
            exceptions = Type.mergeTypeLists(exceptions, stmtList[i].getExceptionTypes());
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        if (stmtList.length == 0)
            return new StatementAST[] { next };
        final HashSet goPoints = calcularGotoPoints(); // TRECHO REFATORADO
        removerGotoPointsInternos(goPoints); // TRECHO REFATORADO
        final StatementAST[] points = new StatementAST[goPoints.size()];
        return goPoints.toArray(points);
    }

    // TRECHO REFATORADO - Novo método para encapsular cálculo dos Go Points
    private HashSet calcularGotoPoints() {
        final HashSet goPoints = new HashSet();
        goPoints.add(stmtList[0]);
        for (int i = 0; i < stmtList.length; i++) {
            if (goPoints.contains(stmtList[i])) {
                final StatementAST[] sNext = stmtList[i].nextControlPoints();
                for (int j = 0; j < sNext.length; j++) {
                    goPoints.add(sNext[j]);
                }
            }
        }
        return goPoints;
    }

    // TRECHO REFATORADO - Novo método para encapsular a remoção dos pontos internos
    private void removerGotoPointsInternos(final HashSet goPoints) {
        for (int i = 0; i < stmtList.length; i++) {
            goPoints.remove(stmtList[i]);
        }
    }

    public VarList getVarList() {
        final VarList[] lists = new VarList[stmtList.length];
        for (int i = 0; i < lists.length; i++) {
            lists[i] = stmtList[i].getVarList();
        }
        return new VarList(lists);
    }

    public StatementAST[] getList() {
        return stmtList;
    }
}

/**
 * ## 1) Oportunidades de Refatoração Encontradas
 * Após analisar atentamente a classe `CompoundAST`, identifiquei algumas oportunidades claras para a **Extração de Métodos**, facilitando a leitura do código e reduzindo complexidades internas. São elas:
 * - **Laço no método `parseComplete()`**: Este método realiza duas operações distintas, que poderiam ser extraídas para melhorar a coesão: a criação da lista de statements e a configuração dos ponteiros entre eles.
 * - **Operação no método `computeControl()`**: Dois processos iterativos foram identificados — adicionar pontos de controle possíveis e, posteriormente, remover pontos já processados, que podem ser encapsulados em métodos separados para maior clareza.
 * - **Operação no método `computeExceptions()`**: A lógica de acumulação e fusão das listas de exceções pode ser extraída para um método dedicado.
 *
 * Não houve oportunidade significativa para a extração de classes, já que a classe é bem coesa em seu único papel descrito na documentação (representa um compound statement).
 * ## 2) Classe Refatorada com os Devidos Comentários
 *
 * ## 3) Justificativa das Refatorações
 * A extração de métodos realizada segue os princípios recomendados na obra clássica _Refactoring_ de Martin Fowler, especialmente a técnica **"Extract Method"**. A ideia principal dessa técnica é aumentar a coesão do código ao decompor rotinas complexas em métodos menores e com uma única e clara finalidade. Segundo Fowler, códigos abrangentes devem ser substituídos por chamadas de métodos explicativas.
 * Segundo Marco Túlio em _Engenharia de Software Moderna_, métodos que contêm múltiplas etapas de processamento são candidatos ideais à extração de métodos. Este foi claramente o caso dos métodos da classe original, especialmente em `parseComplete()` e `computeControl()`. Essas alterações melhoram:
 * - **Legibilidade**: Métodos pequenos e com nomes representativos comunicam facilmente a intenção do código.
 * - **Organização**: Cada método passa a cumprir um único objetivo claramente definido.
 * - **Facilidade de manutenção**: Uma mudança ou ajuste torna-se mais fácil quando cada responsabilidade é claramente segmentada em métodos distintos.
 *
 * A extração de métodos também elimina códigos repetitivos e/ou iterativos, seguindo fielmente as diretrizes divulgadas tanto por Martin Fowler quanto por Marco Tulio, conferindo ao código uma estrutura mais modular e sustentável para futuras manutenções e ajustes.
 * ## 4) Resumo das Alterações
 * - **Quantidade total**: 5 alterações realizadas.
 * - **Tipo de Alterações**:
 *     - **Extração de Métodos**: 5 métodos extraídos.
 *         - `extrairStatements()`
 *         - `configurarProximosStatements()`
 *         - `combinarExcecoes()`
 *         - `calcularGotoPoints()`
 *         - `removerGotoPointsInternos()`
 *
 *     - **Extração de Classes**: 0 classes extraídas (não houve necessidade identificada nesse caso).
 */