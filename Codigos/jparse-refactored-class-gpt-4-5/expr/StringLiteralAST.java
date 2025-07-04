package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class StringLiteralAST extends ExpressionAST {

    public StringLiteralAST(final Token token) {
        super(token);
        inicializarTipo(); // TRECHO REFATORADO
        inicializarValor(); // TRECHO REFATORADO
    }

    // Método extraído para inicializar o tipo
    private void inicializarTipo() {  // TRECHO REFATORADO
        type = Type.stringType;
    }

    // Método extraído para inicializar o valor
    private void inicializarValor() {  // TRECHO REFATORADO
        value = getText();
    }

    protected Type computeType() {
        return type;
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        return value;
    }

    public VarList getVarList() {
        return new VarList();
    }
}
/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar a classe `StringLiteralAST`, identifiquei os seguintes pontos oportunos para refatoração:
 * - **Extração de Método:**
 * No construtor da classe, as operações de inicialização de valores (`type` e `value`) são feitas diretamente ali. É mais claramente entendido ao extrair essas operações para métodos separados, melhorando a legibilidade e isolando preocupações.
 * - **Extração de Classe:**
 * Não identifiquei a necessidade de extração de nova classe nesta situação específica, pois não existem múltiplas responsabilidades ou agrupamentos claros de atributos que justifiquem este tipo de refatoração.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * - **Extração dos métodos `inicializarTipo()` e `inicializarValor()`:** Seguindo as recomendações de Fowler (2018), extrações de método permitem tornar a lógica de métodos longos ou pouco claros mais simples de entender. Neste caso específico, mesmo o construtor sendo razoavelmente pequeno, essa extração promove clareza e atribui responsabilidades mais específicas, tornando cada método responsável por uma única operação lógica. Também permite futuras alterações na forma como os tipos e valores são inicializados, sem que o código cliente ou estrutura base sejam afetados radicalmente.
 * - **Ausência de Extração de Classe:**
 * Ao seguir os princípios mencionados na literatura de referência, não encontrei justificativas suficientes para extrair novas classes – tal extração seria desnecessária e forçada, prejudicando o design atual ao invés de beneficiá-lo. Como enfatizado por Marco Túlio, apenas extraímos classes quando claramente detectamos que existe mais de uma responsabilidade que deva estar isolada em classes distintas – algo inexistente claramente nesta classe simples e focada.
 *
 * ## 4) Resumo das alterações
 *
 * | Tipo de Refatoração | Quantidade |
 * | --- | --- |
 * | Extração de Método | 2 |
 * | Extração de Classe | 0 |
 * | **Total** | **2** |
 * - **Quantidade total de refatorações realizadas:** 2
 * - **Extração de Método:** 2
 * - **Extração de Classe:** 0
 */