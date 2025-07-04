package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

/**
 * An AST node that represents an index expression
 */
public final class IndexAST extends ExpressionAST implements JavaTokenTypes {

    /**
     * O componente de índice que encapsula a parte do índice da expressão
     */
    // TRECHO REFATORADO: Extraída uma classe para encapsular as operações relacionadas a indexação
    private IndexComponent indexComponent;

    /**
     * Create a new index expression AST
     *
     * @param token the token represented by this AST node
     */
    public IndexAST(final Token token) {
        super(token);
        setType(INDEX_OP);
        indexComponent = new IndexComponent();
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído método para inicializar os componentes
        inicializarComponentes();
        // TRECHO REFATORADO: Delegada responsabilidade para o método específico
        processarNosFilhos();
    }

    // TRECHO REFATORADO: Método extraído para melhor organização
    private void inicializarComponentes() {
        ExpressionAST base = (ExpressionAST)getFirstChild();
        ExpressionAST index = (ExpressionAST)base.getNextSibling();
        indexComponent.setBase(base);
        indexComponent.setIndex(index);
    }

    // TRECHO REFATORADO: Método extraído para processar nós filhos
    private void processarNosFilhos() {
        indexComponent.getBase().parseComplete();
        indexComponent.getIndex().parseComplete();
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Delegada a responsabilidade para o componente de índice
        return indexComponent.computeComponentType();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Extraído método para mergeamento de tipos de exceção
        return mesclarTiposDeExcecao();
    }

    // TRECHO REFATORADO: Método extraído para calcular tipos de exceção
    private Type[] mesclarTiposDeExcecao() {
        return Type.mergeTypeLists(
            indexComponent.getBase().getExceptionTypes(),
            indexComponent.getIndex().getExceptionTypes()
        );
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Delegada a responsabilidade para o componente de índice
        return indexComponent.criarVarList();
    }

    /**
     * Get the base of this index expression; i.e., the value being indexed
     *
     * @return the base of the expression
     */
    public ExpressionAST getBase() {
        return indexComponent.getBase();
    }

    /**
     * Get the index of this index expression
     *
     * @return the index of the expression
     */
    public ExpressionAST getIndex() {
        return indexComponent.getIndex();
    }

    // TRECHO REFATORADO: Classe extraída para encapsular operações de indexação
    private class IndexComponent {
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

        public Type computeComponentType() {
            return base.retrieveType().getComponentType();
        }

        public VarList criarVarList() {
            return new VarList(base.getVarList(), index.getVarList());
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * - O método `parseComplete()` realiza várias operações que poderiam ser divididas em métodos menores e mais específicos.
 * - O método `computeType()` é muito simples, mas depende diretamente da variável `base`.
 * - O método `computeExceptions()` contém lógica que poderia ser extraída para um método utilitário.
 * - O gerenciamento dos componentes `base` e `index` poderia ser encapsulado melhor.
 * - A classe poderia se beneficiar da extração de uma classe para lidar com operações específicas de indexação.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 *
 */