package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

public final class AssertAST extends StatementAST {

    // TRECHO REFATORADO: Extraída a lógica de gerenciamento das expressões para uma classe à parte
    private ExpressionPair expressionPair;

    public AssertAST(final Token token) {
        super(token);
        // TRECHO REFATORADO: Inicialização do ExpressionPair
        expressionPair = new ExpressionPair();
    }

    public void parseComplete() {
        super.parseComplete();
        
        // TRECHO REFATORADO: Extraído o processamento de expressões para um método específico
        processExpressions();
    }

    // TRECHO REFATORADO: Método extraído para processar as expressões
    private void processExpressions() {
        jparse.expr.ExpressionAST firstExpr = (jparse.expr.ExpressionAST)getFirstChild();
        firstExpr.parseComplete();
        
        final AST punctuation = firstExpr.getNextSibling();
        
        if (punctuation.getType() == JavaTokenTypes.COLON) {
            jparse.expr.ExpressionAST secondExpr = (jparse.expr.ExpressionAST)punctuation.getNextSibling();
            secondExpr.parseComplete();
            expressionPair.setExpressions(firstExpr, secondExpr);
        } else {
            expressionPair.setExpressions(firstExpr, null);
        }
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Uso do método da classe extraída para determinar exceções
        return expressionPair.getExceptionTypes();
    }

    protected StatementAST[] computeControl() {
        return new StatementAST[] { next };
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Uso do método da classe extraída para obter a lista de variáveis
        return expressionPair.getVarList();
    }

    public jparse.expr.ExpressionAST getFirstExpression() {
        return expressionPair.getFirstExpression();
    }

    public jparse.expr.ExpressionAST getSecondExpression() {
        return expressionPair.getSecondExpression();
    }
    
    // TRECHO REFATORADO: Classe extraída para gerenciar o par de expressões
    private class ExpressionPair {
        private jparse.expr.ExpressionAST expr1;
        private jparse.expr.ExpressionAST expr2;
        
        public void setExpressions(jparse.expr.ExpressionAST first, jparse.expr.ExpressionAST second) {
            this.expr1 = first;
            this.expr2 = second;
        }
        
        public jparse.expr.ExpressionAST getFirstExpression() {
            return expr1;
        }
        
        public jparse.expr.ExpressionAST getSecondExpression() {
            return expr2;
        }
        
        public boolean hasSecondExpression() {
            return expr2 != null;
        }
        
        public Type[] getExceptionTypes() {
            return (!hasSecondExpression())
                ? expr1.getExceptionTypes()
                : Type.mergeTypeLists(expr1.getExceptionTypes(),
                              expr2.getExceptionTypes());
        }
        
        public VarList getVarList() {
            return (!hasSecondExpression())
                ? expr1.getVarList()
                : new VarList(expr1.getVarList(), expr2.getVarList());
        }
    }
}

/**
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `AssertAST`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Lógica de processamento de expressões no método `parseComplete()`**: O método contém lógica que pode ser extraída para um método separado para melhorar a legibilidade.
 * 2. **Código condicional duplicado**: Existe um padrão condicional `(expr2 == null)` que se repete em dois métodos: `computeExceptions()` e `getVarList()`. Essa lógica pode ser extraída.
 * 3. **Gerenciamento de expressões**: O gerenciamento das expressões `expr1` e `expr2` pode ser encapsulado em uma classe separada, aplicando o princípio de responsabilidade única.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração do método `processExpressions()`**:
 *     - Seguindo o princípio da responsabilidade única de Martin Fowler, extraí a lógica de processamento de expressões do método `parseComplete()` para um método dedicado.
 *     - Isso aumenta a legibilidade, pois cada método agora tem uma única responsabilidade clara.
 *     - O nome do método transmite claramente sua função.
 *
 * 2. **Extração da classe `ExpressionPair`**:
 *     - Esta refatoração aplica o princípio "Extrair Classe" conforme descrito por Fowler, isolando a responsabilidade de gerenciar as expressões.
 *     - A classe interna `ExpressionPair` agora encapsula toda a lógica relacionada às duas expressões e suas operações.
 *     - Isso reduz a complexidade da classe principal e melhora a coesão.
 *
 * 3. **Método `hasSecondExpression()`**:
 *     - Criei este método para encapsular a verificação de expressão nula que era repetida em vários lugares.
 *     - Essa refatoração segue o princípio DRY (Don't Repeat Yourself) mencionado por Tulio na Engenharia de Software Moderna.
 *     - O nome do método aumenta a expressividade do código ao comunicar claramente seu propósito.
 *
 * 4. **Refatoração dos métodos `getExceptionTypes()` e `getVarList()` na classe extraída**:
 *     - Removi a duplicação de código condicional ao encapsular essa lógica na classe `ExpressionPair`.
 *     - Isto melhora a manutenibilidade do código, pois mudanças nessa lógica precisam ser feitas em apenas um lugar.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 4
 * - **Divisão por tipo**:
 *     - Extração de Método: 2 (`processExpressions()` e `hasSecondExpression()`)
 *     - Extração de Classe: 1 (`ExpressionPair`)
 *     - Movimentação de métodos: 1 (Movendo métodos relacionados às expressões para a classe extraída)
 *
 * Estas refatorações melhoram significativamente a organização do código, tornando-o mais modular, legível e de fácil manutenção. A responsabilidade de gerenciar as expressões agora está encapsulada em uma classe dedicada, seguindo os princípios de design orientado a objetos e as boas práticas recomendadas por Fowler e Tulio.
 */