package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ReturnAST extends StatementAST implements JavaTokenTypes {

    private jparse.expr.ExpressionAST expr;

    public ReturnAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extraído para método separado a lógica de extração e validação da expressão
        extrairExpressaoRetorno();
    }

    // TRECHO REFATORADO: Método extraído para melhorar legibilidade e separação de responsabilidades
    private void extrairExpressaoRetorno() {
        final AST retVal = getFirstChild();
        if (retVal.getType() != SEMI) {
            expr = (jparse.expr.ExpressionAST)retVal;
            expr.parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Utilização do método auxiliar para tratar a expressão
        return tratarExpressaoNula() ? noTypes : expr.getExceptionTypes();
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[] { nonlocal };
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Utilização do método auxiliar para tratar a expressão
        return tratarExpressaoNula() ? new VarList() : expr.getVarList();
    }

    // TRECHO REFATORADO: Método extraído para evitar duplicação na verificação de expressão nula
    private boolean tratarExpressaoNula() {
        return expr == null;
    }

    public jparse.expr.ExpressionAST getReturnValue() {
        return expr;
    }
}

/**
 * # Refatoração da classe ReturnAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe ReturnAST, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Lógica de extração e validação da expressão**: A lógica de obter o primeiro filho e verificar seu tipo está misturada no método `parseComplete()`. Isso pode ser extraído para um método separado para melhor clareza.
 * 2. **Lógica condicional repetida**: Nos métodos `computeExceptions()` e `getVarList()`, existe um padrão repetido de verificação de `expr == null` e retorno de valores com base nesta condição.
 * 3. **Responsabilidades misturadas**: A análise e a manipulação da expressão de retorno poderiam ser melhor encapsuladas.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `extrairExpressaoRetorno()`**:
 *     - A lógica para extrair e validar a expressão de retorno foi movida para um método dedicado, melhorando a coesão e facilitando a compreensão do método `parseComplete()`.
 *     - Segundo Martin Fowler, a técnica de "Extract Method" (Extração de Método) é útil quando temos um fragmento de código que pode ser agrupado. Isso torna o código mais legível e evita duplicações.
 *
 * 2. **Extração do método `tratarExpressaoNula()`**:
 *     - A verificação `expr == null` era repetida em dois métodos diferentes (`computeExceptions()` e `getVarList()`).
 *     - Seguindo o princípio DRY (Don't Repeat Yourself), extraí essa verificação para um método separado.
 *     - Conforme Marco Tulio destaca em "Engenharia de Software Moderna", eliminar duplicações de código é um dos principais benefícios da refatoração.
 *
 * 3. **Simplificação dos métodos que utilizam a expressão nula**:
 *     - Ao usar o método auxiliar `tratarExpressaoNula()`, a lógica nos métodos `computeExceptions()` e `getVarList()` ficou mais clara e concisa.
 *     - Isso segue o princípio de que um método deve fazer apenas uma coisa, aumentando a coesão e diminuindo o acoplamento.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - Extração de Método: 3 (métodos `extrairExpressaoRetorno()`, `tratarExpressaoNula()` e refatoração de `computeExceptions()` e `getVarList()` para usar método auxiliar)
 *     - Extração de Classe: 0 (Não foi necessário extrair uma nova classe devido ao tamanho e responsabilidade já bem definida da classe original)
 *
 * O resultado final é um código mais organizado, com métodos menores e mais coesos, cada um com uma responsabilidade claramente definida. As refatorações melhoraram a manutenibilidade do código sem alterar seu comportamento original, seguindo as práticas recomendadas por Martin Fowler e Marco Tulio.
 */