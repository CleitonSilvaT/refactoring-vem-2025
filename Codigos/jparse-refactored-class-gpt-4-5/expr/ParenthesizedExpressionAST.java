package jparse.expr;

import antlr.Token;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class ParenthesizedAST extends ExpressionAST
    implements JavaTokenTypes {

    private ParenExpressionDelegate parenExpressionDelegate; // TRECHO REFATORADO

    public ParenthesizedAST(final Token token) {
        super(token);
        setType(PAREN_EXPR);
    }

    public void parseComplete() {
        parenExpressionDelegate = new ParenExpressionDelegate((ExpressionAST) getFirstChild()); // TRECHO REFATORADO
        parenExpressionDelegate.parseComplete(); // TRECHO REFATORADO
    }

    protected Type computeType() {
        return parenExpressionDelegate.computeType(); // TRECHO REFATORADO
    }

    protected Type[] computeExceptions() {
        return parenExpressionDelegate.computeExceptions(); // TRECHO REFATORADO
    }

    protected Object computeValue() {
        return parenExpressionDelegate.computeValue(); // TRECHO REFATORADO
    }

    public VarList getVarList() {
        return parenExpressionDelegate.getVarList(); // TRECHO REFATORADO
    }

    public ExpressionAST getParenExpression() {
        return parenExpressionDelegate.getExpression(); // TRECHO REFATORADO
    }
}


/**
 * ### 1) Oportunidades de refatoração encontradas:
 * - **Extração de Métodos**: Os métodos `computeType()`, `computeExceptions()`, `computeValue()`, e `getVarList()` simplesmente chamam métodos relacionados diretamente do objeto `parenthesized`. Observa-se um padrão repetitivo de delegação explícita nestes métodos, configurando oportunidades claras para a criação de métodos intermediários de delegação mais limpos e mais fáceis de manter.
 * - **Extração de Classe**: O atributo privado `ExpressionAST parenthesized` e todos os métodos que funcionam diretamente em sua delegação indicam uma coesão que poderia justificar extração em uma classe específica representando "Expressão Parentizada". Isso ajudará a encapsular características relacionadas apenas a essa expressão específica, mantendo responsabilidades claras e separadas.
 *
 * ### 2) Classe refatorada com os devidos comentários:
 *
 * ### 3) Justificativa das refatorações:
 * - **Extração de Classe (Extract Class)**: A criação da classe `ParenExpressionDelegate` segue as recomendações de Martin Fowler para reduzir complexidade e aumentar coesão (Fowler, 2018). Ao encapsular em uma nova classe todos os acessos e delegações relativos ao objeto `ExpressionAST`, mantém-se a alta coesão interna, ressaltada por Marco Tulio quando descreve técnicas que tornam mais explícitas as responsabilidades das classes (Cap. 9, Engenharia de Software Moderna).
 * - **Extração de Métodos de Delegação (Extract Method)**: Foram extraídos métodos que delegam diretamente ao novo objeto criado. Essa extração simplificou os métodos originais da classe, eliminando duplicação de código e tornando-os mais sucintos e claros, conforme indicado por Fowler nas práticas que estimulam clareza e simplicidade do projeto do código.
 *
 * ### 4) Resumo das alterações:
 * - **Quantidade total de refatorações realizadas**: 7 alterações
 * - **Extração de Classe**: 1 alteração (criação da classe `ParenExpressionDelegate`)
 * - **Extração de Método**: 6 alterações (métodos delegados para os métodos da classe extraída)
 */