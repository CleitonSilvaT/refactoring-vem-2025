package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class ThisLiteralAST extends IdentifierAST {

    public ThisLiteralAST(final Token token) {
	    super(token);
    }

    protected Type computeType() {
	    return getThisType(); // TRECHO REFATORADO: Extração de Método
    }

    protected Type[] computeExceptions() {
	    return ASTConstants.getNoTypes(); // TRECHO REFATORADO: Extração de Classe
    }

    protected Object computeValue() {
	    return getNonConstant(); // TRECHO REFATORADO: Extração de Método
    }

    public VarList getVarList() {
	    return criaVarListVazia(); // TRECHO REFATORADO: Extração de Método
    }

    // Métodos extraídos (Extração de Métodos)

    // TRECHO REFATORADO
    private Type getThisType() {
	    return typeAST.retrieveType();	
    }

    // TRECHO REFATORADO
    private Object getNonConstant() {
	    return ASTConstants.NON_CONSTANT;
    }

    // TRECHO REFATORADO
    private VarList criaVarListVazia() {
	    return new VarList();
    }
}

// Nova classe para conter constantes (Extração de Classe)

// TRECHO REFATORADO
class ASTConstants {
    public static final Type[] NO_TYPES = new Type[]{};
    public static final Object NON_CONSTANT = new Object();

    public static Type[] getNoTypes() {
        return NO_TYPES;
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas:
 * Após análise cuidadosa, percebemos que a classe original `ThisLiteralAST` possui pouca complexidade e está bem direcionada em relação à responsabilidade única. Ainda assim, existem pequenos métodos que recuperam valores constantes internos ou outros tipos de informações fixas que podem ser padronizadas:
 * - O método `computeExceptions()` retorna uma constante `noTypes`, que poderia estar encapsulada em uma classe auxiliar para gerenciar valores constantes.
 * - O método `computeValue()` retorna o valor `nonconstant`, que pode ser encapsulado em método específico identificado claramente.
 * - Criação de `VarList` vazia (`new VarList()`) retornada diretamente no método `getVarList()` poderia ser extraída para método separado que represente claramente seu objetivo (criação específica).
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * - **Extração de Classe (`ASTConstants`)**:
 *     - Segundo Martin Fowler (2018), ao extrair constantes literais para uma classe específica, centralizamos o gerenciamento de valores comuns, aumentando clareza e reduzindo repetição. Este método melhora a manutenção pela fácil localização de constantes usadas em diversos pontos da aplicação.
 *
 * - **Extração de Método**:
 *     - Os métodos `computeType()`, `computeValue()` e `getVarList()` originais apresentavam as seguintes justificativas claras para extração:
 *         - Melhoria na legibilidade e clareza do código ao nomear explicitamente ações internas.
 *         - Adição semântica explícita que facilita manutenção futura e entendimento instantâneo das ações realizadas pelos métodos.
 *         - Redução potencial de duplicação futura ou desvios semânticos pelo uso explícito de métodos bem definidos.
 *
 *     - De acordo com Marco Tulio e Martin Fowler, pequenas extrações de métodos são altamente recomendáveis, resultando em métodos curtos com responsabilidades bem definidas, contribuindo significativamente para manutenibilidade e entendimento geral do código.
 *
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 4
 * - **Tipos de refatoração**:
 *     - Extração de Método: 3 ocorrências
 *     - Extração de Classe: 1 ocorrência
 */