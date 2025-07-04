package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

/**
 * An AST node that represents an index expression
 *
 * @version $Revision: 1.2 $, $Date: 2004/04/02 05:48:48 $
 * @author Jerry James
 */
public final class IndexAST extends ExpressionAST implements JavaTokenTypes {

    // TRECHO REFATORADO: Encapsulei os atributos base e index em uma classe separada para melhor coesão
    private IndexComponents indexComponents;

    /**
     * Create a new index expression AST
     *
     * @param token the token represented by this AST node
     */
    public IndexAST(final Token token) {
        super(token);
        setType(INDEX_OP);
        indexComponents = new IndexComponents();
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído para método específico para melhorar legibilidade
        initializeComponents();
        completeComponentParsing();
    }

    // TRECHO REFATORADO: Método extraído para inicialização dos componentes
    private void initializeComponents() {
        ExpressionAST base = (ExpressionAST)getFirstChild();
        ExpressionAST index = (ExpressionAST)base.getNextSibling();
        indexComponents.setBase(base);
        indexComponents.setIndex(index);
    }

    // TRECHO REFATORADO: Método extraído para completar o parsing dos componentes
    private void completeComponentParsing() {
        indexComponents.getBase().parseComplete();
        indexComponents.getIndex().parseComplete();
    }

    protected Type computeType() {
        return indexComponents.getBase().retrieveType().getComponentType();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Extraído para método específico
        return mergeComponentExceptions();
    }

    // TRECHO REFATORADO: Método extraído para mesclar exceções
    private Type[] mergeComponentExceptions() {
        return Type.mergeTypeLists(
                indexComponents.getBase().getExceptionTypes(),
                indexComponents.getIndex().getExceptionTypes());
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        return new VarList(
                indexComponents.getBase().getVarList(),
                indexComponents.getIndex().getVarList());
    }

    /**
     * Get the base of this index expression; i.e., the value being indexed
     *
     * @return the base of the expression
     */
    public ExpressionAST getBase() {
        return indexComponents.getBase();
    }

    /**
     * Get the index of this index expression
     *
     * @return the index of the expression
     */
    public ExpressionAST getIndex() {
        return indexComponents.getIndex();
    }

    // TRECHO REFATORADO: Nova classe extraída para encapsular os componentes de indexação
    private static class IndexComponents {
        private ExpressionAST base;
        private ExpressionAST index;

        public ExpressionAST getBase() {
            return base;
        }

        public void setBase(ExpressionAST base) {
            this.base = base;
        }

        public ExpressionAST getIndex() {
            return index;
        }

        public void setIndex(ExpressionAST index) {
            this.index = index;
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Ao analisar a classe `IndexAST`, identifiquei os seguintes trechos problemáticos:
 * - O método `computeExceptions()` contém lógica que poderia ser extraída para um método separado, tornando o código mais legível e focado em uma única responsabilidade.
 * - O método `parseComplete()` realiza múltiplas operações que poderiam ser separadas em métodos mais específicos.
 * - A manipulação de expressões base e índice poderia ser melhor encapsulada em uma classe separada, já que representam um conceito coeso (um par de valores relacionados entre si).
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da Classe `IndexComponents`**:
 *     - Seguindo o princípio da Responsabilidade Única de Martin Fowler, encapsulei os atributos `base` e `index` em uma classe separada.
 *     - Isso melhora a coesão, pois esses atributos representam um conceito coeso (um par base-índice).
 *     - Facilita futuras expansões relacionadas a esta estrutura, mantendo o código mais organizado.
 *
 * 2. **Extração do Método `initializeComponents()`**:
 *     - Extraí o código de inicialização dos componentes para um método separado.
 *     - Isso torna o método `parseComplete()` mais legível e focado em sua responsabilidade específica.
 *     - Seguindo a diretriz de Fowler, cada método deve realizar apenas uma tarefa claramente definida.
 *
 * 3. **Extração do Método `completeComponentParsing()`**:
 *     - Separei a parte de execução do parsing em um método dedicado.
 *     - Aumenta a legibilidade e facilita a manutenção, permitindo modificar essa lógica em um único lugar.
 *
 * 4. **Extração do Método `mergeComponentExceptions()`**:
 *     - Melhorei a legibilidade do método `computeExceptions()` extraindo a lógica de mesclagem para um método específico.
 *     - Seguindo o princípio da responsabilidade única, cada método agora tem um propósito mais claro.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 4 (initializeComponents, completeComponentParsing, mergeComponentExceptions, e os métodos acessores dentro da classe extraída)
 *     - **Extração de Classe**: 1 (IndexComponents)
 *
 * Essas refatorações foram realizadas com o objetivo de melhorar a organização e legibilidade do código, mantendo seu comportamento original e seguindo os princípios de design recomendados por Martin Fowler e conceitos de Engenharia de Software Moderna.
 */