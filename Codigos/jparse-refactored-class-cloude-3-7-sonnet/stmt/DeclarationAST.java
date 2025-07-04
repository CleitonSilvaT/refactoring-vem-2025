package jparse.stmt;

import antlr.collections.AST;
import java.util.ArrayList;
import jparse.JavaTokenTypes;
import jparse.ModifierAST;
import jparse.Type;
import jparse.VarList;
import jparse.expr.VarAST;
import jparse.expr.TypeAST;

public final class DeclarationAST extends StatementAST
    implements JavaTokenTypes {

    private final ModifierAST modifiers;
    private final TypeAST typeSpec;
    private final VarAST[] variables;
    
    // TRECHO REFATORADO: Extraída uma nova classe VariableCollector para lidar com a coleta de variáveis
    private static class VariableCollector {
        public static VarAST[] collectVariables(AST vars) {
            final ArrayList list = new ArrayList();
            while (vars != null) {
                list.add(vars.getFirstChild());
                vars = vars.getNextSibling();
                if (vars == null)
                    break;
                vars = vars.getNextSibling();
            }
            
            VarAST[] variables = new VarAST[list.size()];
            list.toArray(variables);
            return variables;
        }
    }

    public DeclarationAST(final ModifierAST mods, final TypeAST type, AST vars) {
        initialize(VARIABLE_DEFS, "VARIABLE_DEFS");
        modifiers = mods;
        typeSpec = type;
        
        // TRECHO REFATORADO: Usando a classe extraída para coletar variáveis
        variables = VariableCollector.collectVariables(vars);
    }

    public void parseComplete() {
        super.parseComplete();
        context.mods = modifiers;
        context.type = typeSpec;
        
        // TRECHO REFATORADO: Extraído método para processar as variáveis
        processVariables();
    }
    
    // TRECHO REFATORADO: Método extraído para processar as variáveis
    private void processVariables() {
        for (int i = 0; i < variables.length; i++) {
            variables[i].parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        Type[] exceptions = noTypes;
        
        // TRECHO REFATORADO: Extraído método para calcular exceções
        exceptions = mergeExceptionsFromVariables(exceptions);
        return exceptions;
    }
    
    // TRECHO REFATORADO: Método extraído para mesclar exceções
    private Type[] mergeExceptionsFromVariables(Type[] exceptions) {
        for (int i = 0; i < variables.length; i++) {
            exceptions = Type.mergeTypeLists(exceptions,
                variables[i].getExceptionTypes());
        }
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[] { next };
    }

    public VarList getVarList() {
        return new VarList(variables);
    }

    public ModifierAST getModifiers() {
        return modifiers;
    }

    public TypeAST getTypeName() {
        return typeSpec;
    }

    public VarAST[] getVariables() {
        return variables;
    }
}

/**
 * # Análise de Refatoração da Classe DeclarationAST
 * Vou realizar a refatoração da classe `DeclarationAST` com foco em Extração de Classe e Extração de Métodos, conforme solicitado. Seguirei a estrutura de resposta em quatro passos.
 * ## 1) Oportunidades de refatoração encontradas
 * - O construtor `DeclarationAST` contém lógica complexa para coletar e processar declarações de variáveis que pode ser extraída para um método separado.
 * - O método `parseComplete()` contém um loop para processar variáveis que pode ser extraído para um método separado.
 * - As operações relacionadas à manipulação das variáveis (coleta, processamento e conversão) poderiam ser encapsuladas em uma classe própria para gerenciar os aspectos específicos das variáveis declaradas.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração da classe `VariableCollector`**:
 *     - Seguindo o princípio de Responsabilidade Única de Martin Fowler, extraí a lógica de coleta de variáveis para uma classe separada.
 *     - Esta classe encapsula a complexidade de percorrer a estrutura AST e convertê-la em um array de variáveis, o que torna o código principal mais limpo e focado em sua responsabilidade principal.
 *     - Esta refatoração melhora a coesão do código, isolando a lógica específica de coleta de variáveis.
 *
 * 2. **Extração do método `processVariables()`**:
 *     - O loop para processar as variáveis foi extraído para um método com nome descritivo.
 *     - Essa refatoração melhora a legibilidade do código e facilita a compreensão da intenção do processamento de variáveis.
 *     - Conforme destaca Martin Fowler, métodos pequenos e com nomes significativos melhoram a legibilidade e manutenibilidade do código.
 *
 * 3. **Extração do método `mergeExceptionsFromVariables()`**:
 *     - A lógica de mesclagem de exceções foi extraída para um método separado.
 *     - Esta refatoração facilita a compreensão do propósito deste trecho de código.
 *     - Marco Tulio, em "Engenharia de Software Moderna", enfatiza que métodos devem ser curtos e realizar uma única tarefa bem definida, o que é alcançado com essa extração.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas:** 4
 * - **Divisão por tipo:**
 *     - **Extração de Método:** 3 (processVariables, mergeExceptionsFromVariables, collectVariables [como método estático da nova classe])
 *     - **Extração de Classe:** 1 (VariableCollector)
 *
 * As refatorações realizadas melhoraram significativamente a estrutura do código, tornando-o mais modular, coeso e de fácil manutenção. A classe principal agora está mais focada em suas responsabilidades essenciais, enquanto aspectos específicos foram delegados a métodos especializados e uma classe auxiliar.
 */