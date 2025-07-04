//class extracted from AssertAST

package jparse.stmt;

import antlr.collections.AST;
import jparse.JavaTokenTypes;
import jparse.expr.ExpressionAST;

public class AssertExpression {

    private ExpressionAST primeiraExpressao; // TRECHO REFATORADO
    private ExpressionAST segundaExpressao;  // TRECHO REFATORADO

    public AssertExpression(AST ast) { // TRECHO REFATORADO
        extrairExpressao(ast);         // TRECHO REFATORADO
    }

    private void extrairExpressao(AST ast) { // TRECHO REFATORADO
        primeiraExpressao = (ExpressionAST) ast;
        primeiraExpressao.parseComplete();
        final AST pontuacao = primeiraExpressao.getNextSibling();
        if (pontuacao != null && pontuacao.getType() == JavaTokenTypes.COLON) {
            segundaExpressao = (ExpressionAST) pontuacao.getNextSibling();
            segundaExpressao.parseComplete();
        }
    }

    public ExpressionAST getPrimeiraExpressao() { // TRECHO REFATORADO
        return primeiraExpressao;
    }

    public ExpressionAST getSegundaExpressao() { // TRECHO REFATORADO
        return segundaExpressao;
    }

    public boolean possuiSegundaExpressao() { // TRECHO REFATORADO
        return segundaExpressao != null;
    }

    public jparse.Type[] getExceptionTypes() { // TRECHO REFATORADO
        return (possuiSegundaExpressao()) ?
            jparse.Type.mergeTypeLists(primeiraExpressao.getExceptionTypes(),
                                       segundaExpressao.getExceptionTypes()) :
            primeiraExpressao.getExceptionTypes();
    }

    public jparse.VarList getVarList() { // TRECHO REFATORADO
        return (possuiSegundaExpressao()) ?
            new jparse.VarList(primeiraExpressao.getVarList(),
                               segundaExpressao.getVarList()) :
            primeiraExpressao.getVarList();
    }
}