package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class NullLiteralAST extends ExpressionAST {

    // TRECHO REFATORADO: Adicionado um objeto VarList compartilhado
    private static final VarList EMPTY_VAR_LIST = new VarList();

    public NullLiteralAST(final Token token) {
	super(token);
    }

    protected Type computeType() {
        return type;
    }

    protected Type[] computeExceptions() {
	return noTypes;
    }

    protected Object computeValue() {
	return null;
    }

    // TRECHO REFATORADO: Modificado para utilizar objeto compartilhado ao invés de criar nova instância
    public VarList getVarList() {
	return EMPTY_VAR_LIST;
    }
}


/**
 * ## 1) Oportunidades de refatoração encontradas
 * Na classe `NullLiteralAST`, identifiquei algumas oportunidades para melhorias através da refatoração:
 * 1. A classe é bem simples, com métodos pequenos e objetivos, mas o método `getVarList()` retorna sempre um novo objeto `VarList` vazio, o que poderia ser extraído ou otimizado.
 * 2. Os comentários podem ser removidos conforme solicitado nas diretrizes antes da refatoração.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Objeto Compartilhado (`EMPTY_VAR_LIST`)**:
 *     - Segundo Martin Fowler, devemos evitar criar novos objetos quando não é necessário. A criação de um objeto `VarList` vazio a cada chamada do método `getVarList()` é um desperdício de recursos, especialmente se esse objeto é imutável e sempre retorna o mesmo valor.
 *     - Esta refatoração utiliza o conceito de "Flyweight pattern" (ou objeto compartilhado), onde um único objeto imutável pode ser reutilizado em múltiplas situações.
 *     - A constante estática garante que apenas uma instância de `VarList` vazia será criada para todas as instâncias de `NullLiteralAST`, economizando memória e reduzindo o tempo de execução.
 *
 * 2. **Remoção dos comentários**:
 *     - Conforme solicitado nas diretrizes, todos os comentários originais foram removidos, mantendo apenas os comentários de refatoração.
 *     - Esta prática é frequentemente recomendada quando o código deve "se explicar", sendo a legibilidade mais importante que explicações em comentários.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 1
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 0
 *     - **Extração de Classe**: 0
 *     - **Outros tipos de refatoração**: 1 (Extração de Constante/Objeto Compartilhado)
 *
 * Observação: Esta classe é bastante simples e já bem estruturada, o que limitou as oportunidades de refatoração específicas de "Extração de Classe" e "Extração de Métodos". A refatoração realizada, embora não se encaixe diretamente nessas categorias, segue os princípios de melhoria da organização e eficiência do código mencionados por Martin Fowler.
 */