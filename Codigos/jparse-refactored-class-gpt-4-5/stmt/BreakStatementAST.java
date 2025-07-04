package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.IdentifierAST;

public final class BreakAST extends StatementAST {

    private IdentifierAST label;

    public BreakAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        definirLabel(getFirstChild()); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO – Extração do método para definir o label.
    private void definirLabel(final AST maybeLabel) {
        if (maybeLabel instanceof IdentifierAST) {
            label = (IdentifierAST)maybeLabel;
        } else {
            definirAlvoPadrao(); // TRECHO REFATORADO
        }
    }

    // TRECHO REFATORADO – Extração do método com a responsabilidade de definir o alvo padrão do break.
    private void definirAlvoPadrao() {
        control = new StatementAST[] { context.breakTarget().next };
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[] { symTable.getLabel(label.getName()) };
    }

    public VarList getVarList() {
        return new VarList();
    }

    public IdentifierAST getLabel() {
        return label;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas:
 * - **Extração de método**: A lógica utilizada na verificação e atribuição do label no método `parseComplete()` está poluindo o método e reduzindo sua legibilidade. Essa lógica pode ser movida para um método privado com nome representativo.
 * - **Extração de método**: A forma de definir alvo de controle para casos sem label pode ser extraída em um método próprio, melhorando a abstração e clareza.
 * - **Extração de Classe**: Não há oportunidades relevantes para esse tipo de refatoração nesta classe, pois ela já representa uma única responsabilidade de forma coesa e clara.
 *
 * ## 2) Classe refatorada com os devidos comentários:
 *
 * ## 3) Justificativa das refatorações:
 * - **Extração do método `definirLabel(AST maybeLabel)`**:
 *     - Separação clara da responsabilidade de analisar se existe ou não um label associado ao break.
 *     - Melhora a legibilidade, seguindo as diretrizes do livro "Refactoring" de Fowler, que reforça que cada método deve possuir somente uma responsabilidade identificável.
 *
 * - **Extração do método `definirAlvoPadrao()`**:
 *     - Elimina detalhes técnicos e descritivos da implementação principal, proporcionando uma abstração melhor ao método principal.
 *     - Implementa o conceito de "Extração de Métodos" enfatizado pelos autores citados (Fowler e Marco Tulio), especialmente com o objetivo de torná-lo autoexplicativo e com intenção clara (clean code).
 *
 * Seguindo os princípios apresentados pelos livros citados, percebemos que ao extrair métodos, tornamos o código mais legível, com responsabilidades claramente isoladas, facilitando futuras manutenções e entendimentos do comportamento implementado.
 * ## 4) Resumo das alterações:
 * - Quantidade total de refatorações realizadas: **2**
 *     - Extração de Método: **2**
 *     - Extração de Classe: **0** (não havia justificativa suficiente para extrair uma nova classe)
 */