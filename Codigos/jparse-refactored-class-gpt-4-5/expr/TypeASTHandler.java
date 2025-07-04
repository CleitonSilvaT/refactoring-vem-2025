package jparse.expr;

import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class TypeAST extends IdentifierAST implements JavaTokenTypes {

    public TypeAST(final String name) {
        super();
        inicializarAtributos(name); // TRECHO REFATORADO - extração de método
    }

    private void inicializarAtributos(final String name) { // TRECHO REFATORADO
        initialize(TYPE, name);
        setName(name);
    }

    protected Type computeType() {
        return obterTipoPorNome(); // TRECHO REFATORADO - extração de método
    }

    private Type obterTipoPorNome() { // TRECHO REFATORADO
        try {
            return topLevel.getType(getName());
        } catch (ClassNotFoundException classEx) {
            return null;
        }
    }

    protected Type[] computeExceptions() {
        return noTypes;
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        return new VarList();
    }
}

/**
 * ### 1) Oportunidades de refatoração encontradas
 * - O método `computeType()` tem uma responsabilidade bem definida: obter o tipo pelo nome, lidar com exceção. É uma oportunidade clara para Extração de Método, separando a lógica do tratamento da exceção.
 * - O construtor atual possui inicializações que têm claramente a responsabilidade de inicializar atributos do objeto AST. Isso também pode ser extraído em um método específico, melhorando legibilidade.
 *
 * ### 2) Classe refatorada com os devidos comentários
 *
 * ### 3) Justificativa das refatorações
 * Segundo Martin Fowler, o método de extração visa separar partes da lógica interna em métodos especificamente nomeados, aumentando a clareza da intenção do código. Ao extrairmos métodos, estamos explicitando melhor o propósito e encapsulando detalhes, permitindo que o leitor rapidamente entenda a intenção da classe sem precisar entender imediatamente todos os pormenores.
 * - **Extração do método** `inicializarAtributos(final String name)`:
 *     - O código que inicializa os atributos ficava no construtor, dificultando a compreensão imediata da responsabilidade deste bloco de inicialização. Reunir isso em um método torna claro o que esse trecho faz: inicializa atributos da classe.
 *
 * - **Extração do método** `obterTipoPorNome()`:
 *     - A captura e tratamento da exceção `ClassNotFoundException` foi isolada. Isto melhora a leitura por encapsular lógica específica. Agora, o método `computeType()` só chama este método secundário que trata detalhes específicos.
 *
 * ### 4) Resumo das alterações
 * - Quantidade total de refatorações realizadas: **2**
 *     - Extração de Método: **2**
 *     - Extração de Classe: **0** (não houve necessidade neste pequeno exemplo)
 */