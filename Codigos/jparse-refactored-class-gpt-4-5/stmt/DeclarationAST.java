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

    public DeclarationAST(final ModifierAST mods, final TypeAST type, AST vars) {
        initialize(VARIABLE_DEFS, "VARIABLE_DEFS");
        modifiers = mods;
        typeSpec = type;
        variables = coletarVariaveis(vars); // TRECHO REFATORADO - Extração para método específico
    }

    // TRECHO REFATORADO - Extração de método: Isola a lógica de extração das variáveis
    private VarAST[] coletarVariaveis(AST vars) {
        final ArrayList list = new ArrayList();
        while (vars != null) {
            list.add(vars.getFirstChild());
            vars = vars.getNextSibling();
            if (vars == null)
                break;
            vars = vars.getNextSibling();
        }
        VarAST[] varsArray = new VarAST[list.size()];
        list.toArray(varsArray);
        return varsArray;
    }

    public void parseComplete() {
        super.parseComplete();
        context.mods = modifiers;
        context.type = typeSpec;
        parseCompleteVariaveis(); // TRECHO REFATORADO - Extração para método específico
    }

    // TRECHO REFATORADO - Extração de método: Isola iteração e invocação de parseComplete dos VarAST
    private void parseCompleteVariaveis() {
        for (VarAST variable : variables) {
            variable.parseComplete();
        }
    }

    protected Type[] computeExceptions() {
        Type[] exceptions = noTypes;
        for (VarAST variable : variables)
            exceptions = Type.mergeTypeLists(exceptions, variable.getExceptionTypes());
        return exceptions;
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[]{next};
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
 * ### 1) Oportunidades de refatoração encontradas
 * Identifiquei as seguintes oportunidades:
 * **Extração de Método:**
 * - A lógica para coletar variáveis no construtor `DeclarationAST` é uma boa candidata para extração de método pela clareza e isolamento de responsabilidades ao construir o array de variáveis.
 * - A lógica presente no método `parseComplete` para aplicar `parseComplete` nas variáveis também é possível extrair, isolando esta responsabilidade e tornando o método principal mais claro.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * - A técnica de **Extração de Método**, conforme recomendado por Martin Fowler (2018), é aplicada para promover clareza e organização do código. Ao extrair trechos logicamente coesos em métodos próprios (como `coletarVariaveis` e `parseCompleteVariaveis`), facilitamos futuras alterações e permitimos um entendimento mais claro sobre as responsabilidades de cada método na classe.
 * - Esta técnica auxilia ainda no **princípio da responsabilidade única (SRP)**, citado por Marco Tulio, ao garantir que cada método desempenha apenas uma tarefa específica e clara, diminuindo a chance de erro humano durante futuras evoluções do sistema.
 * - As refatorações propostas objetivam otimizar a manutenibilidade desta classe, tornando-a mais simples, reduzindo maior complexidade cognitiva e evitando duplicação caso a coleta ou parse completo de variáveis fosse necessário em outro ponto futuro da classe.
 *
 * ### 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas:** 2
 * - **Tipo de Refatoração:**
 *     - Extração de Métodos: 2 (métodos `coletarVariaveis` e `parseCompleteVariaveis`)
 *     - Extração de Classes: 0 (não foram identificadas oportunidades sólidas para criação de nova classe)
 */