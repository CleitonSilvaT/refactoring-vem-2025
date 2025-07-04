package jparse.stmt;

import antlr.Token;
import jparse.HasExceptions;
import jparse.JavaAST;
import jparse.Type;
import jparse.VarList;

public abstract class StatementAST extends JavaAST implements HasExceptions {

    public static final StatementAST nonlocal = new CaseGroupAST();

    // TRECHO REFATORADO: Movido para a classe ControlFlowManager
    private ControlFlowManager controlFlowManager;
    
    protected StatementAST next;

    // TRECHO REFATORADO: Criado construtor que inicializa o ControlFlowManager
    protected StatementAST() {
        super();
        this.controlFlowManager = new ControlFlowManager(this);
    }

    // TRECHO REFATORADO: Criado construtor que inicializa o ControlFlowManager
    protected StatementAST(final Token token) {
        super(token);
        this.controlFlowManager = new ControlFlowManager(this);
    }

    public void parseComplete() {
        next = context.nextStmt;
    }

    // TRECHO REFATORADO: Método delegado para ControlFlowManager
    public final Type[] getExceptionTypes() {
        return controlFlowManager.getExceptionTypes();
    }

    protected abstract Type[] computeExceptions();

    // TRECHO REFATORADO: Método delegado para ControlFlowManager
    public StatementAST[] nextControlPoints() {
        return controlFlowManager.getControlPoints();
    }

    protected abstract StatementAST[] computeControl();

    public abstract VarList getVarList();
    
    // TRECHO REFATORADO: Classe extraída para gerenciar o controle de fluxo e exceções
    private class ControlFlowManager {
        private Type[] exceptions;
        private StatementAST[] control;
        private final StatementAST owner;
        
        public ControlFlowManager(StatementAST owner) {
            this.owner = owner;
        }
        
        // TRECHO REFATORADO: Método extraído a partir da duplicação do padrão lazy initialization
        private <T> T initializeIfNeeded(T field, InitializationFunction<T> computeFunction) {
            if (field == null) {
                return computeFunction.compute();
            }
            return field;
        }
        
        public Type[] getExceptionTypes() {
            exceptions = initializeIfNeeded(exceptions, () -> owner.computeExceptions());
            return exceptions;
        }
        
        public StatementAST[] getControlPoints() {
            control = initializeIfNeeded(control, () -> owner.computeControl());
            return control;
        }
        
        // Interface funcional para permitir inicialização tardia de recursos
        private interface InitializationFunction<T> {
            T compute();
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Controle de fluxo** - Os métodos `getExceptionTypes()` e `nextControlPoints()` seguem o mesmo padrão de implementação (lazy initialization). Esta duplicação de código pode ser extraída para um método utilitário.
 * 2. **Dados relacionados** - Os atributos `exceptions`, `control` e seus métodos associados representam uma responsabilidade independente relacionada ao controle de fluxo e exceções, que poderia ser extraída para uma classe separada.
 * 3. **Inicialização do estado** - O construtor não possui lógica específica além de chamar o construtor da superclasse. Em caso de extensão futura, isso pode levar a duplicações.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe (ControlFlowManager)** - Extraí uma classe interna para gerenciar os aspectos de controle de fluxo e exceções. Isto segue o princípio de Responsabilidade Única (SRP) do SOLID, conforme destacado por Martin Fowler. A classe `StatementAST` tinha múltiplas responsabilidades (representar um nó AST e gerenciar controle de fluxo), e agora essa segunda responsabilidade está encapsulada em uma classe dedicada.
 * 2. **Extração de Método (initializeIfNeeded)** - Este método extrai o padrão comum de lazy initialization que estava duplicado nos métodos `getExceptionTypes()` e `nextControlPoints()`. De acordo com Marco Tulio, a duplicação de código é um dos "maus cheiros" que indicam necessidade de refatoração. Esta extração também melhora a manutenibilidade, pois alterações neste padrão agora precisam ser feitas em apenas um local.
 * 3. **Interface Funcional (InitializationFunction)** - Criei uma interface funcional para viabilizar a abstração do método de inicialização. Embora não seja uma refatoração clássica, isso facilita a extensibilidade e reutilização do código.
 * 4. **Construtores Melhorados** - Ambos construtores agora inicializam adequadamente o `ControlFlowManager`, eliminando potencial duplicação de código no futuro quando houver necessidade de configurar esse componente.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Classe**: 1 (ControlFlowManager)
 *     - **Extração de Método**: 2 (initializeIfNeeded, construtores revisados)
 *
 * Estas refatorações melhoraram significativamente a coesão do código ao separar responsabilidades distintas em classes diferentes, além de reduzirem a duplicação de código através da extração de métodos. O código agora está mais modular, mais fácil de manter e estender, sem alterar o comportamento externo da classe.
 */