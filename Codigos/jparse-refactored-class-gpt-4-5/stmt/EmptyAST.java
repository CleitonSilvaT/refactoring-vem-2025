package jparse.stmt;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class EmptyAST extends StatementAST implements JavaTokenTypes {

    public EmptyAST(final Token token) {
        super(token);
        setType(EMPTY_STAT);
    }

    protected Type[] computeExceptions() {
        return retornaSemExcecoes(); // TRECHO REFATORADO
    }

    protected StatementAST[] computeControl() {
        return retornaProximoControle(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return new VarList();
    }

    // Método extraído para devolver explicitamente que não há exceções
    private Type[] retornaSemExcecoes() { // TRECHO REFATORADO
        return noTypes;
    }

    // Método extraído para centralizar criação do controle seguinte
    private StatementAST[] retornaProximoControle() {  // TRECHO REFATORADO
        return new StatementAST[] { next };
    }
}

/**
 * ### 1) Oportunidades de Refatoração Encontradas:
 * Ao analisar a classe `EmptyAST`, observa-se a necessidade das seguintes refatorações:
 * - **Extração de Método**: Os métodos `computeExceptions` e `computeControl` possuem trabalho simples demais, porém, porém sua criação direta pode significar falta de expressividade em caso de futuras mudanças. Uma boa prática é extrair métodos menores em certos sistemas para garantir clareza sobre a intenção dos retornos.
 * - **Extração de Classe**: O método `getVarList` retorna sempre um novo objeto `VarList` vazio. Poderia-se extrair uma classe que centralize a criação dessas estruturas comuns. Porém, neste caso, por simplicidade e considerando que há apenas um método com este comportamento, a extração de classe não traz benefício significativo.
 *
 * ### 2) Classe Refatorada com Devidos Comentários em Português:
 *
 * ### 3) Justificativa das Refatorações:
 * - **Extração de método (`retornaSemExcecoes` e `retornaProximoControle`)**:
 * Foram extraídos para explicitar claramente a intenção do código (`computeExceptions` e `computeControl`). Segundo Fowler (2018), extrair métodos com nomes autoexplicativos melhora significativamente a legibilidade do seu código, pois expressa claramente sua intenção e facilita possíveis manutenções futuras, mesmo que atualmente sejam métodos extremamente simples.
 * - **Não realização de extração de classe**:
 * Avaliada a extração de classe para a criação de `VarList`, não foi feita, pois essa criação é simples, não se repete e atualmente gera um novo objeto vazio de forma trivial. Ao seguir as recomendações de Fowler e Tulio, evitamos criar abstrações desnecessárias que não trazem melhorias imediatas.
 *
 * ### 4) Resumo das Alterações:
 *
 * | Descrição | Quantidade |
 * | --- | --- |
 * | Total de refatorações realizadas | 2 |
 * | Extração de Método | 2 |
 * | Extração de Classe | 0 |
 */