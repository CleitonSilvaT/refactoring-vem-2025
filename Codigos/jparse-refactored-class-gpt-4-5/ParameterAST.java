package jparse;

import jparse.expr.VarAST;

// TRECHO REFATORADO: Classe criada para agrupar detalhes do parâmetro
class DetalhesParametro {
    private final ModifierAST mods;
    private final jparse.expr.TypeAST typeAST;
    private final VarAST var;

    DetalhesParametro(ModifierAST mods, jparse.expr.TypeAST typeAST, VarAST var) {
        this.mods = mods;
        this.typeAST = typeAST;
        this.var = var;
    }

    ModifierAST getMods() {
        return mods;
    }

    jparse.expr.TypeAST getTypeAST() {
        return typeAST;
    }

    VarAST getVar() {
        return var;
    }

    void completarParse(Contexto contexto) {
        contexto.mods = mods;
        contexto.type = typeAST;
        var.parseComplete();
    }

    @Override
    public String toString() {
        return var.toString();
    }
}

public final class ParameterAST extends JavaAST implements JavaTokenTypes {

    private final DetalhesParametro detalhes; // TRECHO REFATORADO: Utilizando a nova classe

    public ParameterAST(final ModifierAST mod, final jparse.expr.TypeAST type, final VarAST variable) {
        setType(PARAMETER_DEF);
        detalhes = new DetalhesParametro(mod, type, variable); // TRECHO REFATORADO
    }

    public void parseComplete() {
        detalhes.completarParse(context); // TRECHO REFATORADO: Método extraído
    }

    public ModifierAST getModifiers() {
        return detalhes.getMods(); // TRECHO REFATORADO
    }

    public jparse.expr.TypeAST getTypeName() {
        return detalhes.getTypeAST(); // TRECHO REFATORADO
    }

    public VarAST getParamName() {
        return detalhes.getVar(); // TRECHO REFATORADO
    }

    @Override
    public String toString() {
        return detalhes.toString(); // TRECHO REFATORADO
    }
}

/**
 * 1) Oportunidades de refatoração encontradas
Após remover os comentários originais, identifiquei as seguintes oportunidades:

Extração de Classe:

A classe original possui claramente três atributos distintos relacionados ao parâmetro: modificadores (ModifierAST), tipo (TypeAST) e nome (VarAST). Esses três atributos sugerem uma nova classe para agrupar esses elementos, reduzindo a complexidade da classe principal.

Extração de Método:

O método parseComplete() possui chamadas diretas relacionadas à configuração de contexto e conclusão de parse. Esse trecho possui lógica específica que pode ser abstraída em métodos menores, tornando seu objetivo mais claro.

2) Classe refatorada com os devidos comentários

3) Justificativa das refatorações
Extração de Classe:
A criação da classe DetalhesParametro é uma aplicação clara do princípio de responsabilidade única (SRP) mencionado por Martin Fowler (2018). Os detalhes relacionados aos modificadores, tipo e nome foram encapsulados em uma nova classe, simplificando a classe ParameterAST e deixando-a mais clara e fácil de manter.

Extração de Método:
A lógica de parse (completarParse(Contexto contexto)) foi extraída para um método específico na nova classe DetalhesParametro. Isso segue a recomendação de Marco Tulio sobre tornar explícito o propósito dos métodos, facilitando a manutenção e compreensão rápida do que o método realiza. A refatoração deixa clara a responsabilidade de completar o parsing dos detalhes em um ponto único e específico.

4) Resumo das alterações realizadas:
Quantidade total de refatorações realizadas: 2

Divisão por tipo:

Extração de Classe: 1

Extração de Método: 1
 */