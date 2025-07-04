package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.HashSet;
import jparse.Type;
import jparse.VarList;

public final class IfElseAST extends StatementAST {

    private jparse.expr.ExpressionAST condition;
    private StatementAST thenStmt;
    private StatementAST elseStmt;

    public IfElseAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        configuraCondition(); // TRECHO REFATORADO
        configuraThenStmt(); // TRECHO REFATORADO
        configuraElseStmt(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private void configuraCondition() {
        condition = (jparse.expr.ExpressionAST) getFirstChild().getNextSibling();
        condition.parseComplete();
    }

    // TRECHO REFATORADO
    private void configuraThenStmt() {
        thenStmt = (StatementAST) condition.getNextSibling().getNextSibling();
        thenStmt.parseComplete();
    }

    // TRECHO REFATORADO
    private void configuraElseStmt() {
        final AST elseLiteral = thenStmt.getNextSibling();
        if (elseLiteral != null) {
            elseStmt = (StatementAST) elseLiteral.getNextSibling();
            elseStmt.parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        final Type[] body = (elseStmt == null)
            ? thenStmt.getExceptionTypes()
            : Type.mergeTypeLists(thenStmt.getExceptionTypes(),
                                  elseStmt.getExceptionTypes());
        return Type.mergeTypeLists(condition.getExceptionTypes(), body);
    }

    protected StatementAST[] computeControl() {
        if (elseStmt == null)
            return thenStmt.nextControlPoints();

        return mergeControlPoints(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private StatementAST[] mergeControlPoints() {
        final HashSet control = new HashSet();
        adicionaControlPoints(control, thenStmt); // TRECHO REFATORADO
        adicionaControlPoints(control, elseStmt); // TRECHO REFATORADO

        return (StatementAST[]) control.toArray(new StatementAST[control.size()]);
    }

    // TRECHO REFATORADO
    private void adicionaControlPoints(HashSet control, StatementAST stmt) {
        StatementAST[] points = stmt.nextControlPoints();
        for (int i = 0; i < points.length; i++) {
            control.add(points[i]);
        }
    }

    public VarList getVarList() {
        return elseStmt == null ? 
            combinaConditionThen() : combinaConditionThenElse(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO
    private VarList combinaConditionThen() {
        return new VarList(condition.getVarList(), thenStmt.getVarList());
    }

    // TRECHO REFATORADO
    private VarList combinaConditionThenElse() {
        return new VarList(condition.getVarList(), thenStmt.getVarList(), elseStmt.getVarList());
    }

    public jparse.expr.ExpressionAST getCondition() {
        return condition;
    }

    public StatementAST getThen() {
        return thenStmt;
    }

    public StatementAST getElse() {
        return elseStmt;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Foram observados os seguintes trechos propensos a serem refatorados:
 * #### Extração de Método:
 * - No método `parseComplete` existem múltiplas atribuições de atributos e condições que podem ser extraídas e encapsuladas em métodos menores com nomes descritivos.
 * - O método `computeControl` possui uma lógica que inclui loop e validação, candidata à extração devido à complexidade e clareza.
 * - O método `getVarList` contém lógica repetida para validação da existência do else. O trecho pode ser simplificado com método extraído.
 *
 * #### Extração de Classe:
 * - Não foi observada necessidade direta de extração de classe, pois a classe já representa um conceito bem definido de forma coesa.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * - **Extração de Método (`parseComplete`)**
 * Segundo Martin Fowler, métodos longos dificultam o entendimento e manutenção. Extrair etapas específicas em métodos separados facilita a compreensão e manutenção ao tornar cada pequeno método responsável por uma única tarefa clara ("Single Responsibility Principle").
 * - **Extração de Método (`computeControl`)**
 * A lógica complexa de unir "control points" está encapsulada no método `mergeControlPoints`, aumentando a legibilidade ao expressar explicitamente a intenção ("coletar e unir pontos de controle").
 * - **Extração de Método (`getVarList`)**
 * Ao dividir as condições de composição das variáveis em métodos separados (`combinaConditionThen` e `combinaConditionThenElse`), eliminamos duplicação lógica e simplificamos a compreensão do fluxo do método original.
 *
 * Essas refatorações seguem o princípio apresentado por Marco Tulio sobre clareza e facilidade de manutenção do código ao torná-lo modular, coeso e com responsabilidades bem distribuídas entre métodos menores, autocontidos e expressivos.
 * ## 4) Resumo das alterações
 * - Quantidade total de refatorações realizadas: **7**
 * - Distribuídas por tipo:
 *     - **Extração de Método**: **7**
 *         - `configuraCondition`
 *         - `configuraThenStmt`
 *         - `configuraElseStmt`
 *         - `mergeControlPoints`
 *         - `adicionaControlPoints`
 *         - `combinaConditionThen`
 *         - `combinaConditionThenElse`
 *
 *     - **Extração de Classe**: **0** (não houve necessidade)
 */