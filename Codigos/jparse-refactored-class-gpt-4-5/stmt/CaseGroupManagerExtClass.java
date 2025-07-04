//class extracted from SwitchAST

package jparse.stmt;

import antlr.collections.AST;
import jparse.Type;
import jparse.VarList;
import java.util.ArrayList;
import java.util.HashSet;

public class CaseGroupManager { // TRECHO REFATORADO - classe extraída

    private CaseGroupAST[] groupList;

    public CaseGroupManager(AST expr) {
        this.groupList = extrairGrupos(expr);
    }

    private CaseGroupAST[] extrairGrupos(AST expr) { // TRECHO REFATORADO
        final ArrayList listaAuxiliar = new ArrayList();
        for (AST a = expr.getNextSibling().getNextSibling().getNextSibling();
             a.getType() == JavaTokenTypes.CASE_GROUP; a = a.getNextSibling()) {
            listaAuxiliar.add(a);
        }
        CaseGroupAST[] groups = new CaseGroupAST[listaAuxiliar.size()];
        listaAuxiliar.toArray(groups);
        return groups;
    }

    public void processarCases(StatementContext context) { // TRECHO REFATORADO
        final StatementAST original = context.nextStmt;
        context.pushBreak(original);
        for (int i = 0; i < groupList.length - 1; i++) {
            context.nextStmt = groupList[i + 1];
            groupList[i].parseComplete();
        }
        context.nextStmt = original;
        if (groupList.length > 0) {
            groupList[groupList.length - 1].parseComplete();
        }
        context.popBreak();
    }

    public Type[] calcularTiposExcecao(jparse.expr.ExpressionAST expr) { // TRECHO REFATORADO
        Type[] tipos = expr.getExceptionTypes();
        for (CaseGroupAST grupo : groupList) {
            Type.mergeTypeLists(tipos, grupo.getExceptionTypes());
        }
        return tipos;
    }

    public StatementAST[] calcularPontosControle() { // TRECHO REFATORADO
        final HashSet<StatementAST> pontosControle = new HashSet<>();
        for (CaseGroupAST grupo : groupList) {
            StatementAST[] proximos = grupo.nextControlPoints();
            for (StatementAST stmt : proximos) {
                pontosControle.add(stmt);
            }
        }
        for (CaseGroupAST grupo : groupList) {
            pontosControle.remove(grupo);
        }
        StatementAST[] pontos = new StatementAST[pontosControle.size()];
        return pontosControle.toArray(pontos);
    }

    public VarList obterVarList(jparse.expr.ExpressionAST expr) { // TRECHO REFATORADO
        VarList listaVars = expr.getVarList();
        for (CaseGroupAST grupo : groupList) {
            listaVars = new VarList(listaVars, grupo.getVarList());
        }
        return listaVars;
    }

    public CaseGroupAST[] getCaseGroups() { // TRECHO REFATORADO
        return groupList;
    }
}