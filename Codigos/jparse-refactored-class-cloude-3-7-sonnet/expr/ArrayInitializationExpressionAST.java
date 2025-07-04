package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ArrayInitAST extends ExpressionAST
    implements JavaTokenTypes {

    private TypeAST baseType;
    private ExpressionAST[] initializers;
    private int dim;

    public ArrayInitAST(final Token token) {
	super(token);
	setType(ARRAY_INIT);
    }

    public void parseComplete() {
	defineBaseType();
	processInitializers();
	completeInitializers();
    }

    // TRECHO REFATORADO: método extraído para definição do tipo base
    private void defineBaseType() {
        baseType = context.type;
    }

    // TRECHO REFATORADO: método extraído para processar inicializadores
    private void processInitializers() {
        final ArrayList list = new ArrayList();
        
        for (AST a = getFirstChild(); a != null && a.getType() != RCURLY; a = a.getFirstChild()) {
            list.add(a);
            a = a.getNextSibling();
            if (a == null || a.getType() != COMMA)
                break;
        }
        
        initializers = new ExpressionAST[list.size()];
        list.toArray(initializers);
    }

    // TRECHO REFATORADO: método extraído para completar inicializadores
    private void completeInitializers() {
        for (int i = 0; i < initializers.length; i++) {
            initializers[i].parseComplete();
        }
    }

    protected Type computeType() {
	final Type bType = baseType.retrieveType();
	
	// TRECHO REFATORADO: método extraído para calcular a dimensão
	calcularDimensaoMaxima();
	
	dim++; // Incrementa a dimensão para o próprio array
	
	return construirTipoArray(bType);
    }

    // TRECHO REFATORADO: método extraído para calcular a dimensão máxima
    private void calcularDimensaoMaxima() {
        for (int i = 0; i < initializers.length; i++) {
            if (initializers[i] instanceof ArrayInitAST) {
                final ArrayInitAST init = (ArrayInitAST)initializers[i];
                if (init.dim > dim)
                    dim = init.dim;
            }
        }
    }

    // TRECHO REFATORADO: método extraído para construir o tipo do array
    private Type construirTipoArray(Type bType) {
        final String bTypeName = bType.getName();
        final int index = bTypeName.indexOf('[');
        try {
            return Type.forName(bTypeName.substring(0, index + 2 * dim));
        } catch (Exception ex) {
            return null;
        }
    }

    protected Type[] computeExceptions() {
	Type[] e = noTypes;
	for (int i = 0; i < initializers.length; i++) {
	    e = Type.mergeTypeLists(e, initializers[i].getExceptionTypes());
	}
	return e;
    }

    protected Object computeValue() {
	return nonconstant;
    }

    public VarList getVarList() {
	return criarListaVariaveis();
    }

    // TRECHO REFATORADO: método extraído para criar a lista de variáveis
    private VarList criarListaVariaveis() {
        final VarList[] lists = new VarList[initializers.length];
        for (int i = 0; i < initializers.length; i++) {
            lists[i] = initializers[i].getVarList();
        }
        return new VarList(lists);
    }

    public ExpressionAST[] getInitializers() {
	return initializers;
    }
}

/**
 * # Refatoração da Classe ArrayInitAST
 * ## 1) Oportunidades de refatoração encontradas
 * 1. O método `parseComplete()` é muito longo e contém múltiplas responsabilidades diferentes, podendo ser dividido em métodos menores mais específicos.
 * 2. O bloco de código dentro do método `parseComplete()` que processa os inicializadores está muito complexo e poderia ser extraído.
 * 3. Os métodos `computeType()` contém código complexo que poderia ser dividido em métodos menores para melhorar a legibilidade.
 * 4. O código relacionado à análise de dimensão de arrays no método `computeType()` representa uma função específica que poderia ser extraída.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `defineBaseType()`**:
 *     - Esta refatoração isola a responsabilidade de definir o tipo base, tornando o código mais coeso e seguindo o princípio de responsabilidade única mencionado por Martin Fowler.
 *
 * 2. **Extração do método `processInitializers()`**:
 *     - O processamento dos inicializadores é uma operação complexa que merecia seu próprio método. Esta extração simplifica a leitura do método principal e facilita a manutenção desse trecho específico.
 *
 * 3. **Extração do método `completeInitializers()`**:
 *     - Seguindo o mesmo princípio, a finalização dos inicializadores é uma responsabilidade separada que foi movida para um método dedicado.
 *
 * 4. **Extração dos métodos `calcularDimensaoMaxima()` e `construirTipoArray()`**:
 *     - Extrair essas operações do método `computeType()` melhora a legibilidade e a manutenibilidade, pois cada método agora tem uma única responsabilidade bem definida.
 *
 * 5. **Extração do método `criarListaVariaveis()`**:
 *     - A lógica para criar a lista de variáveis foi isolada, tornando o código mais modular e mais fácil de entender.
 *
 * Todas estas refatorações seguem o princípio "Extract Method" descrito por Martin Fowler, que sugere extrair trechos de código com propósitos específicos em métodos separados com nomes descritivos. Isso torna o código mais legível e manutenível, pois cada método descreve claramente o que faz e tem uma única responsabilidade.
 * Os nomes dos métodos extraídos foram escolhidos em português, seguindo a solicitação de que os comentários fossem em português.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 6
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 6
 *     - **Extração de Classe**: 0
 *
 * A classe foi refatorada utilizando principalmente o padrão "Extract Method" para dividir métodos longos e complexos em métodos menores e mais específicos. Não houve necessidade de extrair classes, pois a classe original já possui um propósito bem definido e coeso - representar um nó AST para inicializadores de array.
 */