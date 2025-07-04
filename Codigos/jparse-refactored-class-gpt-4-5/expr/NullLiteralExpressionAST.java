package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class NullLiteralAST extends ExpressionAST {

    private static final LiteralValueProvider literalProvider = new LiteralValueProvider(); // TRECHO REFATORADO

    public NullLiteralAST(final Token token) {
        super(token);
    }

    protected Type computeType() {
        return literalProvider.getDefaultNullType(type); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() {
        return literalProvider.getDefaultExceptions(noTypes); // TRECHO REFATORADO
    }

    protected Object computeValue() {
        return literalProvider.getDefaultNullValue(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return literalProvider.getEmptyVarList(); // TRECHO REFATORADO
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Ao analisar a classe `NullLiteralAST`, foram encontrados alguns pontos que favorecem as técnicas de refatoração mencionadas (Extração de Método e Extração de Classe):
 * - Métodos que retornam valores constantes ou comportamentos previsíveis (`computeType()`, `computeExceptions()`, `computeValue()`, `getVarList()`), e poderiam ser delegados a métodos especializados em uma nova classe auxiliar, já que a lógica não é exclusiva desta classe, é simples e altamente coesa.
 * - Possibilidade de extração de métodos para tornar explícita a intenção de códigos constantes e melhorar sua legibilidade.
 * - Ausência de comentários desnecessários após limpeza, o que facilitará leitura após redução da duplicação lógica em métodos retornando valores constantes.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * As refatorações realizadas seguem os princípios descritos por Martin Fowler e Marco Túlio:
 * - **Extração de Classe:** A nova classe `LiteralValueProvider` foi extraída para concentrar valores constantes e métodos que fornecem valores padrão, reduzindo duplicações futuras em outras classes semelhantes. Isso segue o princípio de responsabilidade única definido em Engenharia de Software Moderna e torna o código mais organizado e fácil de manter (Fowler, 2018).
 * - **Extração de Métodos:** Cada chamada direta de retorno simplificado na classe original foi extraída para métodos específicos, delegando as responsabilidades claramente para a nova classe auxiliar, seguindo o princípio DRY (Don't Repeat Yourself), evitando duplicações de código simples e tornando-a mais legível (Fowler, 2018).
 *
 * Essas técnicas obtêm benefícios como:
 * - Maior coesão no código;
 * - Legibilidade e manutenção facilitadas;
 * - Código autoexplicativo devido à clareza dos métodos extraídos;
 * - Alta facilidade para reutilização dessas constantes e tão comuns na árvore sintática abstrata (AST).
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações:** 5
 * - **Divisão por tipo:**
 *     - Extração de Classe: 1 (`LiteralValueProvider`)
 *     - Extração de Método: 4 (delegação dos métodos originais da classe para nova classe auxiliar)
 */