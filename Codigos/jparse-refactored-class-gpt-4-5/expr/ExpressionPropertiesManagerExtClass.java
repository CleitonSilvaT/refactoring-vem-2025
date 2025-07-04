//class extracted from ExpressionAST

package jparse.expr;

import jparse.Type;
import java.util.function.Supplier;

//TRECHO REFATORADO: Nova Classe extraída para gerenciar atributos relacionados a expressão
class ExpressionProperties {
    private static final Object noVal = new Object();

    private Type tipo;
    private Type[] excecoes;
    private Object valor = noVal;

    //TRECHO REFATORADO: método genérico padrão Lazy Initialization para tipos
    public Type obterTipo(Supplier<Type> supplier) {
        if (tipo == null)
            tipo = supplier.get();
        return tipo;
    }

    //TRECHO REFATORADO: método genérico padrão Lazy Initialization para exceções
    public Type[] obterExcecoes(Supplier<Type[]> supplier) {
        if (excecoes == null)
            excecoes = supplier.get();
        return excecoes;
    }

    //TRECHO REFATORADO: método genérico padrão Lazy Initialization para valores constantes
    public Object obterValorConstante(Supplier<Object> supplier) {
        if (valor == noVal)
            valor = supplier.get();
        return valor;
    }
}