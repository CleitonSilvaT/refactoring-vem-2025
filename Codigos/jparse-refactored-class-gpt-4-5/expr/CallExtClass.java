//class extracted from MethodCallAST

package jparse.expr;

import jparse.*;

public class Chamada {

    private final int tipoChamada;
    private final Type tipoObjeto;
    private final ListAST parametros;
    private final TypeAST typeAST;
    private final String nomeMetodo;
    private Method method;
    private Constructor constructor;

    public Chamada(int tipoChamada, Type tipoObjeto, ListAST parametros, TypeAST typeAST, String nomeMetodo) {
        this.tipoChamada = tipoChamada;
        this.tipoObjeto = tipoObjeto;
        this.parametros = parametros;
        this.typeAST = typeAST;
        this.nomeMetodo = nomeMetodo;
    }

    // TRECHO REFATORADO - centralização de lógica e decisão entre método e construtor
    public Type definirRetorno() {
        if (tipoChamada == ExpressionAST.CONSTRUCTOR_CALL) {
            constructor = tipoObjeto.getConstructor(parametros.getTypes(), typeAST.retrieveType());
            return null;
        } else {
            method = tipoObjeto.getMethod(nomeMetodo, parametros.getTypes(), typeAST.retrieveType());
            return method.getReturnType();
        }
    }
}