package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class AssignAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST lhs;
    private ExpressionAST rhs;

    public AssignAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extração de método para inicializar os lados da expressão
        inicializarExpressoes();
        completarAnaliseExpressoes();
    }

    // TRECHO REFATORADO: Novo método extraído para melhorar legibilidade
    private void inicializarExpressoes() {
        lhs = (ExpressionAST)getFirstChild();
        rhs = (ExpressionAST)lhs.getNextSibling();
    }
    
    // TRECHO REFATORADO: Novo método extraído para melhorar legibilidade
    private void completarAnaliseExpressoes() {
        lhs.parseComplete();
        rhs.parseComplete();
    }

    protected Type computeType() {
        final Type type = lhs.retrieveType();
        // TRECHO REFATORADO: Extração de método para verificação de tipo string
        verificarTipoStringEmAtribuicao(type);
        return type;
    }
    
    // TRECHO REFATORADO: Novo método extraído para verificação de tipo string
    private void verificarTipoStringEmAtribuicao(Type type) {
        if (type == Type.stringType && getType() == PLUS_ASSIGN) {
            setType(CONCAT_ASSIGN);
        }
    }

    protected Type[] computeExceptions() {
        return rhs.getExceptionTypes();
    }

    protected Object computeValue() {
        return rhs.getValue();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Extração de método para obter e combinar listas de variáveis
        return combinarListasVariaveis();
    }
    
    // TRECHO REFATORADO: Novo método extraído para combinar listas
    private VarList combinarListasVariaveis() {
        final VarList leftList = lhs.getVarList();
        final VarList rightList = rhs.getVarList();
        return new VarList(leftList, rightList, true);
    }

    public ExpressionAST getLeft() {
        return lhs;
    }

    public ExpressionAST getRight() {
        return rhs;
    }
}

/**
 * # Refatoração da Classe AssignAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe `AssignAST`, identifiquei os seguintes problemas que justificam refatoração:
 * 1. **Método `parseComplete()`**: Este método contém várias operações sequenciais que podem ser extraídas em um método mais descritivo.
 * 2. **Método `computeType()`**: Este método contém uma lógica condicional que poderia ser extraída para um método separado, melhorando a legibilidade.
 * 3. **Método `getVarList()`**: Este método contém operações de obtenção e combinação de listas que poderiam ser extraídas para métodos mais específicos.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração dos métodos `inicializarExpressoes()` e `completarAnaliseExpressoes()`**:
 *     - Segundo Martin Fowler, dividir um método com múltiplas operações em métodos menores melhora a legibilidade e facilita a manutenção.
 *     - Estes métodos extraídos têm nomes mais descritivos que ajudam a entender o propósito de cada parte do código original.
 *     - Cada método agora tem uma única responsabilidade, aplicando o princípio da Responsabilidade Única.
 *
 * 2. **Extração do método `verificarTipoStringEmAtribuicao()`**:
 *     - Esta refatoração isola a lógica condicional específica para manipulação de tipos de string.
 *     - A extração torna o método `computeType()` mais enxuto e com foco apenas no retorno do tipo.
 *     - O nome do método descreve claramente o que a condição está verificando.
 *
 * 3. **Extração do método `combinarListasVariaveis()`**:
 *     - Separa a lógica de obtenção e combinação das listas de variáveis em um método com nome descritivo.
 *     - Facilita a manutenção do código, caso a lógica de combinação precise ser alterada no futuro.
 *     - Melhora a legibilidade através de um nome que expressa claramente a intenção do código.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 5 (inicializarExpressoes, completarAnaliseExpressoes, verificarTipoStringEmAtribuicao, combinarListasVariaveis)
 *     - **Extração de Classe**: 0
 *
 * Todas as refatorações foram feitas com foco na extração de métodos, visando melhorar a legibilidade e manutenibilidade do código. Não foi identificada necessidade de extração de classe, pois a classe `AssignAST` já tem uma responsabilidade bem definida (representar uma expressão de atribuição) e seu tamanho é adequado.
 */