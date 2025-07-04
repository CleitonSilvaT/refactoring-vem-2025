package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.HasExceptions;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;
import jparse.expr.ListAST;
import jparse.stmt.DeclarationAST;

public final class ForAST extends StatementAST implements JavaTokenTypes {

    // TRECHO REFATORADO: cláusulas agora encapsuladas na classe ForClauses.
    private ForClauses clauses;

    public ForAST(final Token token) {
        super(token);
    }

    // TRECHO REFATORADO: extraiu métodos para melhor organização do código
    public void parseComplete() {
        super.parseComplete();
        clauses = extrairClauses();
        contextoParseClauses();
    }

    // TRECHO REFATORADO: extração do método para inicializar cláusulas
    private ForClauses extrairClauses() {
        final AST initNode = getFirstChild().getNextSibling();
        HasExceptions init = (HasExceptions)initNode.getFirstChild();
        
        final AST condNode = initNode.getNextSibling();
        AST theCond = condNode.getFirstChild();
        ExpressionAST cond = (theCond.getType() == SEMI) ? null : (ExpressionAST)theCond;
        
        final AST incrNode = condNode.getNextSibling();
        ExpressionAST incr = (ExpressionAST)incrNode.getFirstChild();
        
        StatementAST stmt = (StatementAST)incrNode.getNextSibling().getNextSibling();
        
        return new ForClauses(init, cond, incr, stmt);
    }

    // TRECHO REFATORADO: extração do contexto de parsing para melhorar modularização
    private void contextoParseClauses() {
        context.pushContinue(this);
        if (clauses.getInit() != null && clauses.getInit() instanceof ListAST) {
            ((ListAST)clauses.getInit()).parseComplete();
        } else {
            ((DeclarationAST)clauses.getInit()).parseComplete();
        }
        if (clauses.getCond() != null)
            clauses.getCond().parseComplete();
        if (clauses.getIncr() != null)
            clauses.getIncr().parseComplete();
        clauses.getStmt().parseComplete();
        context.popContinue();
    }

    // TRECHO REFATORADO: extraiu lógica de tratamento de exceção em método separado
    protected Type[] computeExceptions() {
        return combinarExcecoesClauses();
    }

    private Type[] combinarExcecoesClauses() {
        Type[] retVal = (clauses.getCond() == null)
            ? clauses.getInit().getExceptionTypes()
            : Type.mergeTypeLists(clauses.getInit().getExceptionTypes(),
                                  clauses.getCond().getExceptionTypes());
        retVal = Type.mergeTypeLists(retVal, clauses.getIncr().getExceptionTypes());
        return Type.mergeTypeLists(retVal, clauses.getStmt().getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return clauses.getStmt().nextControlPoints();
    }

    public VarList getVarList() {
        final VarList initList = (clauses.getInit() instanceof ListAST)
            ? ((ListAST)clauses.getInit()).getVarList()
            : ((DeclarationAST)clauses.getInit()).getVarList();
        return new VarList(new VarList(initList, clauses.getCond().getVarList()),
                           new VarList(clauses.getIncr().getVarList(), clauses.getStmt().getVarList()));
    }

    public HasExceptions getInit() {
        return clauses.getInit();
    }

    public ExpressionAST getCondition() {
        return clauses.getCond();
    }

    public ExpressionAST getIncrement() {
        return clauses.getIncr();
    }

    public StatementAST getBody() {
        return clauses.getStmt();
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas:
 * Após analisar a classe `ForAST`, foram identificadas as seguintes oportunidades:
 * - **Extração de métodos**:
 *     - O método `parseComplete()` realiza múltiplas operações distintas que podem ser divididas para aumentar a legibilidade.
 *     - O método `computeExceptions()` tem uma lógica específica de combinação de exceções que merece ser separada.
 *
 * - **Extração de classe**:
 *     - Uma classe separada poderia encapsular as cláusulas `init`, `cond`, `incr` e `stmt`, criando uma nova entidade coesa exclusivamente focada nas cláusulas da construção do loop.
 *
 * # 2) Classe refatorada com os devidos comentários:
 *
 * # 3) Justificativa das refatorações:
 * - **Extração de Classe ("Extract Class")**: Conforme Fowler e Tulio, a classe original tinha atributos que formavam um grupo bem definido. Criar a classe `ForClauses` claramente reduz o tamanho e responsabilidade da classe original, seguindo o princípio da coesão e facilitando eventuais mudanças futuras.
 * - **Extração de Métodos ("Extract Method")**:
 *     - `extrairClauses()`: Este método cria claramente uma unidade de trabalho separada. Proporciona clareza sobre a tarefa do método principal. Segundo Martin Fowler, "Extract Method" facilita entender o propósito do código original, melhorando visivelmente a clareza.
 *     - `contextoParseClauses()`: Isola o contexto do loop `for`, tornando explícito as operações contextuais de parsing.
 *     - `combinarExcecoesClauses()`: Simplifica o entendimento da lógica ao deixar explícito que a combinação de exceções é destinada às cláusulas do loop.
 *
 * Estas mudanças auxiliam a criar métodos menores e mais autocontidos, melhorando a legibilidade e facilidade de manutenção conforme preconizado por Fowler.
 * # 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas:** 4 alterações.
 * - **Divisão das refatorações:**
 *     - Extração de Classe: 1
 *     - Extração de Método: 3
 */