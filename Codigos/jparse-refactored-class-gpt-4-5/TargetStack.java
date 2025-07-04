//class extracted from CompileContext

package jparse;

import java.util.LinkedList;
import jparse.stmt.StatementAST;

// TRECHO REFATORADO: Nova classe extraída para gestão especializada dos alvos break e continue
class TargetStack {
    private final LinkedList<JavaAST> breakTargets = new LinkedList<>();
    private final LinkedList<JavaAST> continueTargets = new LinkedList<>();

    public void pushBreak(JavaAST target) {
        breakTargets.addFirst(target);
    }

    public void popBreak() {
        breakTargets.removeFirst();
    }

    public StatementAST getBreakTarget() {
        return (StatementAST) breakTargets.getFirst();
    }

    public void pushContinue(JavaAST target) {
        breakTargets.addFirst(target);
        continueTargets.addFirst(target);
    }

    public void popContinue() {
        breakTargets.removeFirst();
        continueTargets.removeFirst();
    }

    public StatementAST getContinueTarget() {
        return (StatementAST) continueTargets.getFirst();
    }
}