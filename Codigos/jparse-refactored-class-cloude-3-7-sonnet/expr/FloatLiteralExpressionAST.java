package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

public final class FloatLiteralAST extends ExpressionAST {

    public FloatLiteralAST(final Token token) {
        super(token);
    }

    public void parseComplete() {
        // TRECHO REFATORADO - Extraído método para obter a string do token com sinal
        final String tokenString = obterTokenComSinal();
        
        // TRECHO REFATORADO - Extraído método para determinar o tipo do literal
        final boolean floatLiteral = isFloatLiteral(tokenString);
        
        // TRECHO REFATORADO - Extraído método para definir o tipo
        definirTipo(floatLiteral);
        
        // TRECHO REFATORADO - Extraído método para criar o valor
        criarValor(tokenString, floatLiteral);
    }

    // TRECHO REFATORADO - Novo método para obter a string do token com o sinal correto
    private String obterTokenComSinal() {
        return (context.negative) ? "-" + getText() : getText();
    }

    // TRECHO REFATORADO - Novo método para verificar se é um literal float
    private boolean isFloatLiteral(String tokenString) {
        final int length = tokenString.length();
        final char lastChar = tokenString.charAt(length - 1);
        return lastChar == 'f' || lastChar == 'F';
    }

    // TRECHO REFATORADO - Novo método para definir o tipo do literal
    private void definirTipo(boolean floatLiteral) {
        type = floatLiteral ? Type.floatType : Type.doubleType;    // JLS 15.8.1
    }

    // TRECHO REFATORADO - Novo método para criar o valor do literal
    private void criarValor(String tokenString, boolean floatLiteral) {
        if (floatLiteral) {
            final int length = tokenString.length();
            value = (Object)Float.valueOf(tokenString.substring(0, length - 1));
        } else {
            value = (Object)Double.valueOf(tokenString);
        }
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
 * # Refatoração da Classe FloatLiteralAST
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `FloatLiteralAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Extração de Método**: O método `parseComplete()` realiza várias operações relacionadas ao processamento de literais de ponto flutuante, que podem ser extraídas para métodos mais específicos.
 * 2. **Extração de Método**: A lógica para determinar o tipo do literal (float ou double) e para criar o valor correspondente está misturada, o que prejudica a coesão.
 * 3. **Extração de Método**: A criação do valor a partir da string está diretamente no método `parseComplete()`, podendo ser extraída para um método separado que se concentre apenas nesta tarefa.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * ### Extração do método `obterTokenComSinal()`
 * - **Justificativa**: Segundo Martin Fowler, o método de Extração de Método é aplicado para melhorar a legibilidade do código, separando responsabilidades. Neste caso, a responsabilidade de formatar o token com seu sinal foi isolada para dar maior clareza ao código.
 * - **Benefício**: A leitura do código fica mais clara, pois o nome do método descreve exatamente o que ele faz, facilitando a compreensão e manutenção.
 *
 * ### Extração do método `isFloatLiteral()`
 * - **Justificativa**: De acordo com o princípio da Responsabilidade Única (mencionado em Engenharia de Software Moderna), cada método deve ter uma única responsabilidade bem definida. Este método tem a única responsabilidade de verificar se o literal é do tipo float.
 * - **Benefício**: A lógica para determinar o tipo do literal fica isolada e facilmente identificável, tornando o código mais fácil de entender e modificar.
 *
 * ### Extração do método `definirTipo()`
 * - **Justificativa**: Fowler sugere que operações que alteram o estado de um objeto devem ser isoladas em métodos específicos. Este método tem a responsabilidade única de definir o tipo do literal.
 * - **Benefício**: Separa a operação de definição do tipo, facilitando possíveis alterações futuras nessa lógica sem afetar outras partes do código.
 *
 * ### Extração do método `criarValor()`
 * - **Justificativa**: Este método extrai a lógica complexa de conversão da string para o valor adequado (Float ou Double). Conforme Fowler, métodos que realizam cálculos complexos devem ser extraídos para melhorar a clareza.
 * - **Benefício**: Isola a lógica de conversão, tornando o código mais modular e facilitando futuros ajustes ou correções nessa funcionalidade específica.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 4
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 4
 *     - **Extração de Classe**: 0
 *
 * Todas as refatorações realizadas foram do tipo "Extração de Método", seguindo as diretrizes de Martin Fowler e os princípios da Engenharia de Software Moderna. Essas refatorações melhoraram significativamente a legibilidade e manutenibilidade do código, mantendo exatamente o mesmo comportamento, mas com uma estrutura interna mais organizada e coesa.
 */