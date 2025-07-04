package jparse.expr;

import antlr.Token;
import jparse.Type;
import jparse.VarList;

// TRECHO REFATORADO: Classe criada para encapsular o valor do literal caractere
class CharLiteralValue {
    private final char valor;

    public CharLiteralValue(String texto) {
        valor = texto.charAt(0);
    }

    public Character getValor() {
        return valor;
    }
}

public final class CharLiteralAST extends ExpressionAST {

    public CharLiteralAST(final Token token) {
        super(token);
        this.value = extrairValor(token); // TRECHO REFATORADO: chamada ao novo método
        this.type = extrairTipo(); // TRECHO REFATORADO: chamada ao novo método
    }

    // TRECHO REFATORADO: Novo método extrai a criação do valor literal do caractere
    private Character extrairValor(Token token) {
        CharLiteralValue charLiteralValue = new CharLiteralValue(token.getText());
        return charLiteralValue.getValor();
    }

    // TRECHO REFATORADO: Novo método extrai a inicialização do tipo do caractere
    private Type extrairTipo() {
        return Type.charType;
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
 * ### 1) Oportunidades de refatoração encontradas:
 * - O construtor contém lógica que pode ser extraída para métodos separados, melhorando clareza e facilitando futuras alterações.
 * - A criação de objetos como Character e inicialização do tipo podem ser extraídas em métodos específicos para encapsular responsabilidades distintas.
 * - Possibilidade de extração de uma classe separada para gerenciar o valor literal do caractere, delegando assim responsabilidades para classes especializadas.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * Com base nos princípios de Fowler (2018) e Marco Tulio, as seguintes refatorações são justificadas desta forma:
 * - **Extração de Classe** (_Extract Class_): A criação da classe `CharLiteralValue` remove responsabilidade da classe `CharLiteralAST` quanto à manipulação e transformação direta da string. Isso segue o princípio da responsabilidade única, planejado por Fowler e Marco Tulio, melhorando a clareza, organização e facilitando futuras alterações sem afetar outras responsabilidades da classe principal.
 * - **Extração de Métodos** (_Extract Method_): Os métodos `extrairValor` e `extrairTipo` encapsulam responsabilidades específicas de construção de objetos e inicialização de variáveis. Esta refatoração reduz complexidade dentro do construtor, melhora legibilidade e facilita eventuais manutenções posteriores, alinhando com práticas recomendadas especialmente por Fowler.
 *
 * ### 4) Resumo das alterações realizadas:
 * - **Quantidade total de refatorações**: 3
 *     - **Extração de Classe**: 1 refatoração (Classe CharLiteralValue)
 *     - **Extração de Métodos**: 2 refatorações (`extrairValor` e `extrairTipo`)
 */