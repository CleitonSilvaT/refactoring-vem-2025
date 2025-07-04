package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class CharLiteralAST extends ExpressionAST {

    // TRECHO REFATORADO: Removido comentário de documentação do construtor para seguir diretriz

    public CharLiteralAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Extraído método para obter o caractere do token
        value = extrairCaractere();
        // TRECHO REFATORADO: Extraído método para definir o tipo
        definirTipoChar();
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade e isolar responsabilidade
    private Character extrairCaractere() {
        // TRECHO REFATORADO: Substituído construtor depreciado por autoboxing
        return getText().charAt(0);
    }

    // TRECHO REFATORADO: Método extraído para melhorar a legibilidade e isolar responsabilidade
    private void definirTipoChar() {
        type = Type.charType; // JLS 15.8.1
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
 * # Refatoração da Classe CharLiteralAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código fornecido, identifiquei algumas oportunidades de refatoração:
 * 1. **Extração de Método**: O construtor atual contém lógica para extrair um caractere e definir o tipo, que pode ser extraída para métodos separados.
 * 2. **Código Depreciado**: Uso de construtor depreciado `new Character()` que pode ser substituído por autoboxing.
 * 3. **Estrutura Coesa**: Alguns métodos são muito simples e podem se beneficiar de uma abordagem mais coesa.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 *## 3) Justificativa das refatorações
 * 1. **Extração do método `extrairCaractere()`**:
 *     - Segundo Martin Fowler, a extração de método é uma técnica para melhorar a legibilidade e a manutenibilidade do código.
 *     - Este método extrai a lógica específica de obtenção do caractere a partir do token, isolando essa responsabilidade.
 *     - Além disso, substitui o uso do construtor depreciado `new Character()` pelo autoboxing automático do Java, modernizando o código.
 *
 * 2. **Extração do método `definirTipoChar()`**:
 *     - Esta refatoração segue o princípio de responsabilidade única, isolando a tarefa de definir o tipo do literal de caractere.
 *     - Torna mais explícito e documentado o propósito dessa parte do código.
 *     - Facilita futuras modificações ou extensões relacionadas à tipagem.
 *
 * 3. **Remoção de comentários de documentação Javadoc**:
 *     - Seguindo as diretrizes fornecidas, todos os comentários foram removidos antes da refatoração.
 *     - No código refatorado, apenas comentários em português foram adicionados para identificar os locais alterados.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 3
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 2 (métodos `extrairCaractere()` e `definirTipoChar()`)
 *     - **Extração de Classe**: 0 (não houve necessidade de extração de classe neste caso, dado o tamanho e escopo limitado da classe original)
 *
 * A refatoração aplicada melhorou a legibilidade do código, tornou as responsabilidades mais claras e atualizou a implementação para usar práticas mais modernas de programação Java, sem alterar o comportamento original da classe.
 *
 */