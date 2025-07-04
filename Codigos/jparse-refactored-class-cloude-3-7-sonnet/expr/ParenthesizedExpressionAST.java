package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ParenthesizedAST extends ExpressionAST
    implements JavaTokenTypes {

    private ExpressionAST parenthesized;
    
    // TRECHO REFATORADO: Adição de delegador para encapsular chamadas à expressão parentizada
    private final ExpressionDelegator delegator;

    public ParenthesizedAST(final Token token) {
        super(token);
        setType(PAREN_EXPR);
        // TRECHO REFATORADO: Inicialização do delegador
        delegator = new ExpressionDelegator();
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extração de método para obtenção do primeiro filho
        obterPrimeiroFilho();
        // TRECHO REFATORADO: Extração de método para inicialização da expressão parentizada
        inicializarExpressaoParentizada();
    }

    // TRECHO REFATORADO: Método extraído para obtenção do primeiro filho
    private void obterPrimeiroFilho() {
        parenthesized = (ExpressionAST)getFirstChild();
    }
    
    // TRECHO REFATORADO: Método extraído para inicialização da expressão parentizada
    private void inicializarExpressaoParentizada() {
        parenthesized.parseComplete();
        // TRECHO REFATORADO: Configuração do delegador com a expressão parentizada
        delegator.setTarget(parenthesized);
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Uso do delegador para obter o tipo
        return delegator.delegateRetrieveType();
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Uso do delegador para obter os tipos de exceção
        return delegator.delegateGetExceptionTypes();
    }

    protected Object computeValue() {
        // TRECHO REFATORADO: Uso do delegador para obter o valor
        return delegator.delegateGetValue();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Uso do delegador para obter a lista de variáveis
        return delegator.delegateGetVarList();
    }

    public ExpressionAST getParenExpression() {
        return parenthesized;
    }
    
    // TRECHO REFATORADO: Classe extraída para gerenciar delegação de chamadas
    private class ExpressionDelegator {
        private ExpressionAST target;
        
        public void setTarget(ExpressionAST target) {
            this.target = target;
        }
        
        public Type delegateRetrieveType() {
            return target.retrieveType();
        }
        
        public Type[] delegateGetExceptionTypes() {
            return target.getExceptionTypes();
        }
        
        public Object delegateGetValue() {
            return target.getValue();
        }
        
        public VarList delegateGetVarList() {
            return target.getVarList();
        }
    }
}

/**
 * # 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `ParenthesizedAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. O método `parseComplete()` possui dois passos lógicos distintos que podem ser extraídos: obter o primeiro filho e inicializar a propriedade.
 * 2. Os métodos `computeType()`, `computeExceptions()`, `computeValue()` e `getVarList()` possuem comportamento semelhante (delegação para o objeto `parenthesized`), o que sugere a possibilidade de extrair essa lógica de delegação para um método utilitário compartilhado.
 * 3. A funcionalidade de delegação pode ser extraída para uma classe auxiliar separada, criando assim uma camada de abstração adicional que permite reutilização.
 *
 * # 2) Classe refatorada com os devidos comentários
 *
 * # 3) Justificativa das refatorações
 * 1. **Extração de Métodos (`obterPrimeiroFilho` e `inicializarExpressaoParentizada`)**:
 *     - Seguindo o princípio de "um método, uma responsabilidade", extraí duas etapas distintas do método `parseComplete()` para métodos separados.
 *     - Esta refatoração melhora a legibilidade e facilita a manutenção, pois cada método agora tem um propósito claro e único, como recomendado por Martin Fowler em "Refactoring: Improving the Design of Existing Code".
 *
 * 2. **Extração de Classe (`ExpressionDelegator`)**:
 *     - Extraí a lógica de delegação para uma classe interna que encapsula as chamadas ao objeto `parenthesized`.
 *     - Esta refatoração segue o princípio "Alta Coesão, Baixo Acoplamento" mencionado em "Engenharia de Software Moderna".
 *     - Facilita futuras modificações e testes, pois isola a responsabilidade de delegação em uma única classe.
 *     - Reduz duplicação conceitual, já que todos os métodos delegados agora seguem um padrão consistente.
 *
 * 3. **Melhoria na Inicialização**:
 *     - Adicionei inicialização do delegador no construtor e configuração após obter a referência da expressão parentizada.
 *     - Esta mudança garante que o delegador esteja sempre em um estado consistente antes de ser utilizado.
 *
 * # 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 6
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 2 (obterPrimeiroFilho e inicializarExpressaoParentizada)
 *     - **Extração de Classe**: 1 (ExpressionDelegator)
 *     - **Outros ajustes complementares**: 3 (inicialização do delegador no construtor, configuração do delegador após inicialização e modificação dos métodos delegados para usar a classe extraída)
 *
 * Estas refatorações melhoram significativamente a estrutura interna do código, mantendo seu comportamento externo inalterado, o que é um princípio fundamental na refatoração de código, conforme enfatizado por Martin Fowler.
 */