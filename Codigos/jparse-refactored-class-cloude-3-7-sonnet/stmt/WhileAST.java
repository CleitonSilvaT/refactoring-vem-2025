package jparse.stmt;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class WhileAST extends StatementAST {

    private jparse.expr.ExpressionAST cond;
    private StatementAST stmt;

    public WhileAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        super.parseComplete();
        
        // TRECHO REFATORADO - Extração de método para inicializar os nós filhos
        inicializarNosFilhos();
        
        // TRECHO REFATORADO - Extração de método para configurar o contexto de execução
        configurarContextoExecucao();
    }

    // TRECHO REFATORADO - Método extraído para melhorar legibilidade
    private void inicializarNosFilhos() {
        cond = (jparse.expr.ExpressionAST)getFirstChild().getNextSibling();
        stmt = (StatementAST)cond.getNextSibling().getNextSibling();
    }
    
    // TRECHO REFATORADO - Método extraído para isolar a lógica de contexto
    private void configurarContextoExecucao() {
        context.pushContinue(this);
        cond.parseComplete();
        stmt.parseComplete();
        context.popContinue();
    }

    protected Type[] computeExceptions() {
        return Type.mergeTypeLists(cond.getExceptionTypes(),
                                 stmt.getExceptionTypes());
    }

    protected StatementAST[] computeControl() {
        return stmt.nextControlPoints();
    }

    public VarList getVarList() {
        // TRECHO REFATORADO - Método delegado para classe utilitária
        return ASTVarListUtils.combinarVarLists(cond.getVarList(), stmt.getVarList());
    }

    public jparse.expr.ExpressionAST getCondition() {
        return cond;
    }

    public StatementAST getBody() {
        return stmt;
    }
}

// TRECHO REFATORADO - Classe extraída para manipulação de VarList
class ASTVarListUtils {
    /**
     * Combina duas listas de variáveis em uma única lista
     *
     * @param lista1 a primeira lista de variáveis
     * @param lista2 a segunda lista de variáveis
     * @return a lista combinada
     */
    public static VarList combinarVarLists(VarList lista1, VarList lista2) {
        return new VarList(lista1, lista2);
    }
}

/**
 * # Refatoração da Classe WhileAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após análise da classe `WhileAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. O método `parseComplete()` realiza múltiplas responsabilidades e pode ser dividido em métodos menores.
 * 2. A lógica de navegação na árvore AST para extrair filhos (`getFirstChild().getNextSibling()`) está embutida no método, tornando o código menos legível e mais difícil de manter.
 * 3. O método `getVarList()` cria um novo objeto `VarList` com lógica que pode ser encapsulada.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `inicializarNosFilhos()`**: Esta refatoração segue o princípio da Responsabilidade Única (SRP) descrito por Martin Fowler. O método `parseComplete()` estava realizando múltiplas operações distintas. Ao extrair a lógica de inicialização dos nós filhos para um método separado, melhoramos a legibilidade e facilitamos a manutenção, pois cada método agora tem uma única responsabilidade bem definida.
 * 2. **Extração do método `configurarContextoExecucao()`**: Similar à refatoração anterior, este novo método encapsula a lógica específica de configuração do contexto de execução. Isso melhora a coesão do código, tornando mais claro o que cada parte da classe está fazendo, conforme as recomendações de Marco Tulio sobre coesão funcional.
 * 3. **Extração da classe `ASTVarListUtils`**: Esta refatoração segue o princípio de Extract Class de Martin Fowler. A manipulação das VarList não é uma responsabilidade principal da classe WhileAST. Ao mover essa lógica para uma classe utilitária, facilitamos a reutilização dessa funcionalidade em outras classes e reduzimos o acoplamento. Isso segue as recomendações de Marco Tulio sobre separação de responsabilidades.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 4 refatorações
 * - **Divisão por tipo**:
 *     - Extração de Método: 3 (inicializarNosFilhos, configurarContextoExecucao, combinarVarLists)
 *     - Extração de Classe: 1 (ASTVarListUtils)
 *
 * As refatorações realizadas melhoram significativamente a organização do código, aumentando sua modularidade e facilitando futuras manutenções. O princípio orientador foi a separação de responsabilidades e o aumento da coesão, conforme preconizado tanto por Martin Fowler quanto por Marco Tulio em suas obras de referência sobre engenharia de software.
 */