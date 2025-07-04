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
        processTryBlock(); // TRECHO REFATORADO
        processCatchClauses(); // TRECHO REFATORADO
        processFinallyClause(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private void processTryBlock() {
        block = (CompoundAST) getFirstChild();
        block.parseComplete();
    }

    // TRECHO REFATORADO
    private void processCatchClauses() {
        final ArrayList list = new ArrayList();
        AST a;
        for (a = block.getNextSibling(); a != null && a instanceof CatchAST;
             a = a.getNextSibling()) {
            list.add(a);
        }
        catchClause = new CatchAST[list.size()];
        list.toArray(catchClause);
        for (CatchAST catchAST : catchClause) {
            catchAST.parseComplete();
        }
    }

    // TRECHO REFATORADO
    private void processFinallyClause() {
        AST sibling = block.getNextSibling();
        while (sibling != null && sibling instanceof CatchAST) {
            sibling = sibling.getNextSibling();
        }

        if (sibling != null && sibling.getType() == LITERAL_finally) {
            finallyClause = (CompoundAST) sibling.getNextSibling();
            finallyClause.parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        Type[] exceptions = block.getExceptionTypes();

        for (CatchAST catchAST : catchClause) {
            exceptions = catchAST.removeCaughtException(exceptions);
            exceptions = Type.mergeTypeLists(exceptions,
                                             catchAST.getExceptionTypes());
        }

        if (finallyClause != null) {
            exceptions =
                Type.mergeTypeLists(exceptions, finallyClause.getExceptionTypes());
        }

        return exceptions;
    }

    protected StatementAST[] computeControl() {
        final HashSet goPoints = new HashSet();
        addControlPoints(goPoints, block.nextControlPoints()); // TRECHO REFATORADO

        for (CatchAST catchAST : catchClause) {
            addControlPoints(goPoints, catchAST.nextControlPoints()); // TRECHO REFATORADO
        }

        if (finallyClause != null) {
            addControlPoints(goPoints, finallyClause.nextControlPoints()); // TRECHO REFATORADO
        }

        final StatementAST[] points = new StatementAST[goPoints.size()];
        return (StatementAST[]) goPoints.toArray(points);
    }

    // TRECHO REFATORADO
    private void addControlPoints(HashSet goPoints, StatementAST[] sPoints) {
        for (StatementAST statementAST : sPoints) {
            goPoints.add(statementAST);
        }
    }

    public VarList getVarList() {
        VarList list = block.getVarList();
        if (catchClause.length > 0) {
            VarList[] lists = new VarList[catchClause.length];
            for (int i = 0; i < catchClause.length; i++) {
                lists[i] = catchClause[i].getVarList();
            }
            list = new VarList(list, new VarList(lists));
        }
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
 * ## 1. Oportunidades de refatoração encontradas
 * Analisando a classe `TryAST`, identifiquei as seguintes oportunidades para refatoração utilizando os métodos propostos por Fowler e Marco Tulio:
 * ### Extração de Métodos:
 * - Método `parseComplete()` - método muito longo, com responsabilidades distintas claramente identificáveis. Ideal para divisão dos trechos relacionados aos blocos 'try', 'catch' e 'finally'.
 * - Ciclos lógicos semelhantes presentes no método `computeControl()` indicando operação repetitiva passível de extração.
 *
 * ### Extração de Classe:
 * - Não foi identificada oportunidade significativa para extração de classe, uma vez que `TryAST` parece bastante coesa, tratando especificamente do AST relacionado ao try-catch-finally.
 *
 * ## 2. Classe refatorada com os devidos comentários
 * ## 3. Justificativa das refatorações
 * ### Quanto à extração de métodos do `parseComplete()`:
 * - Cada trecho (`try`, `catch`, `finally`) possui objetivos bem definidos e diferentes, justificando a criação de métodos separados para aumentar a clareza de propósitos conforme Fowler (2018): a separação torna o propósito explícito, aumentando legibilidade e facilitando manutenção futura.
 *
 * ### Quanto à extração do método `addControlPoints()`:
 * - O trecho de adicionar pontos de controle em `computeControl()` é um exemplo claro de duplicação de código (adicionar pontos ao hashset várias vezes). Extraí-lo evita duplicações, seguindo o princípio DRY ('Don't Repeat Yourself') explicitado por Marco Tulio, simplificando futuras manutenções.
 *
 * ## 4. Resumo das alterações
 *
 * | Tipo da refatoração | Quantidade |
 * | --- | --- |
 * | Extração de métodos | 4 |
 * | Extração de classes | 0 |
 * #### Total: **4 refatorações realizadas**
 *
 */