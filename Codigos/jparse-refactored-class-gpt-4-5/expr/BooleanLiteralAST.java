package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

// TRECHO REFATORADO: Extração da classe para guardar e transformar valor booleano
public final class BooleanLiteralAST extends ExpressionAST {

    public BooleanLiteralAST(final Token token) {
        super(token);
        value = BooleanLiteral.fromTokenText(getText()); // TRECHO REFATORADO: Extração do método estático da nova classe
        type = Type.booleanType;
    }

    protected Type computeType() {
        return type;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        return value;
    }

    public VarList getVarList() {
        return new VarList();
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - Identificação da criação e inicialização do valor Booleano como candidata à extração de método. A passagem da responsabilidade para um método garante clareza e isolamento da lógica específica de criação do valor Booleano a partir do `Token`.
 * - A criação e manipulação da representação básica do valor poderiam constituir uma classe separada, aprimorando o Single Responsibility Principle (Princípio da Responsabilidade Única descrito por Martin Fowler e Marco Tulio).
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * - **Extração de método ('fromTokenText')**: Isola completamente a responsabilidade da tradução entre texto e valor booleano, melhorando a clareza do construtor original ao torná-lo mais conciso.
 * - **Extração de classe ('BooleanLiteral')**: Aplica o princípio da responsabilidade única (SRP) citado por Fowler e também detalhado por Marco Tulio. Garantir que cada classe tenha apenas uma razão para mudar melhora significativamente a clareza e facilita a manutenção futura da lógica específica de representação e manipulação dos valores booleanos.
 *
 * ### 4) Resumo das alterações
 * - _**Quantidade total de refatorações realizadas**_: 2 refatorações.
 *     - **Extração de Método**: 1 método extraído (`fromTokenText`).
 *     - **Extração de Classe**: 1 classe extraída (`BooleanLiteral`).
 */