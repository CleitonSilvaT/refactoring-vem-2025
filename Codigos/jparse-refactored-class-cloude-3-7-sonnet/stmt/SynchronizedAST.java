package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class SynchronizedAST extends StatementAST {

    private jparse.expr.ExpressionAST lock;
    private CompoundAST body;

    public SynchronizedAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        // TRECHO REFATORADO: Extração de métodos para inicializar lock e body
        initializeLock();
        initializeBody();
        // TRECHO REFATORADO: Extração de método para completar o parsing dos componentes
        parseComponents();
    }

    // TRECHO REFATORADO: Método extraído para inicializar o lock
    private void initializeLock() {
        lock = (jparse.expr.ExpressionAST)getFirstChild().getNextSibling();
    }

    // TRECHO REFATORADO: Método extraído para inicializar o body
    private void initializeBody() {
        body = (CompoundAST)lock.getNextSibling().getNextSibling();
    }

    // TRECHO REFATORADO: Método extraído para completar o parsing dos componentes
    private void parseComponents() {
        lock.parseComplete();
        body.parseComplete();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Extração de método para combinar tipos de exceção
        return combineExceptionTypes();
    }

    // TRECHO REFATORADO: Método extraído para combinar tipos de exceção
    private Type[] combineExceptionTypes() {
        return Type.mergeTypeLists(lock.getExceptionTypes(),
                                  body.getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return body.nextControlPoints();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Extração de método para criar lista de variáveis combinada
        return createCombinedVarList();
    }

    // TRECHO REFATORADO: Método extraído para criar lista de variáveis combinada
    private VarList createCombinedVarList() {
        return new VarList(lock.getVarList(), body.getVarList());
    }

    public jparse.expr.ExpressionAST getLock() {
        return lock;
    }

    public StatementAST getBody() {
        return body;
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `SynchronizedAST`, identifiquei as seguintes oportunidades para refatoração:
 * 1. O método `parseComplete()` tem muitas responsabilidades: inicializa variáveis de instância, faz casting e chama métodos em outros objetos. Isso pode ser dividido em métodos menores e mais específicos.
 * 2. Existe um padrão de inicialização de membros (`lock` e `body`) que pode ser extraído para métodos auxiliares.
 * 3. O método `getVarList()` cria um novo objeto `VarList` combinando informações de outros objetos, uma responsabilidade que poderia ser extraída.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método `initializeLock` e `initializeBody`**:
 *     - Seguindo o princípio de responsabilidade única de Martin Fowler, cada método deve fazer apenas uma coisa. Extraí a inicialização de cada membro para seu próprio método, tornando o código mais legível e separando as responsabilidades.
 *
 * 2. **Extração de Método `parseComponents`**:
 *     - Agrupei as chamadas para `parseComplete()` dos componentes em um método separado, o que melhora a clareza e torna a função `parseComplete()` principal menos densa.
 *
 * 3. **Extração de Método `combineExceptionTypes`**:
 *     - A operação de combinar tipos de exceção foi movida para um método separado com um nome descritivo, o que melhora a legibilidade e documentação do código.
 *
 * 4. **Extração de Método `createCombinedVarList`**:
 *     - A criação do objeto `VarList` combinado foi extraída para um método separado, seguindo o princípio de que métodos devem fazer apenas uma coisa e ter nomes que descrevem claramente o que fazem.
 *
 * Todas essas refatorações seguem o princípio "Extract Method" descrito por Martin Fowler, que consiste em pegar um fragmento de código e transformá-lo em um método com um nome que descreve seu propósito. Isso torna o código mais legível, mais organizado e facilita a manutenção futura.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 5
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 5 (initializeLock, initializeBody, parseComponents, combineExceptionTypes, createCombinedVarList)
 *     - **Extração de Classe**: 0 (Não foram identificadas oportunidades claras para extração de classe nesta implementação)
 *
 * As refatorações realizadas melhoraram significativamente a estrutura do código, seguindo os princípios de "código limpo" e responsabilidade única. Cada método agora tem uma única responsabilidade bem definida, tornando o código mais modular, mais fácil de entender e de manter.
 */