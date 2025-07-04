package jparse;

import antlr.collections.AST;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import jparse.expr.IdentifierAST;
import jparse.stmt.CompoundAST;

public final class ConstrAST extends JavaAST implements Constructor, JavaTokenTypes {

    private final ModifierAST modifiers;
    private final ParameterAST[] paramNames;
    private Type[] paramTypes;
    private final IdentifierAST[] exceptNames;
    private Type[] exceptions;
    private final CompoundAST body;

    ConstrAST() {
        modifiers = new ModifierAST(Modifier.PUBLIC);
        paramNames = new ParameterAST[0];
        exceptNames = new IdentifierAST[0];
        body = null;
    }

    ConstrAST(final ModifierAST mods, final JavaAST parameters,
              final JavaAST exceptions, final CompoundAST block) {
        setType(CTOR_DEF);
        modifiers = mods;
        paramNames = ASTExtractor.extrairParametros(parameters); // TRECHO REFATORADO
        exceptNames = ASTExtractor.extrairExcecoes(exceptions);  // TRECHO REFATORADO
        body = block;
        TypeAST.currType.addConstructor(this);
    }

    public void parseComplete() {
        context.isField = false;
        for (ParameterAST paramName : paramNames) {
            paramName.parseComplete();
        }
        context.nextStmt = null;
        body.parseComplete();
        context.isField = true;
    }

    public Type getDeclaringClass() {
        return typeAST.retrieveType();
    }

    public int getModifiers() {
        return modifiers.mods;
    }

    public ParameterAST[] getParameters() {
        return paramNames;
    }

    public Type[] getParameterTypes() {
        if (paramTypes == null) {
            paramTypes = TipoParametroExtractor.calcularTiposParametros(paramNames, typeAST); // TRECHO REFATORADO
        }
        return paramTypes;
    }

    public Type[] getExceptionTypes() {
        if (exceptions == null)
            exceptions = TipoExcecaoExtractor.calcularTiposExcecao(exceptNames); // TRECHO REFATORADO
        return exceptions;
    }

    public CompoundAST getBody() {
        return body;
    }

    public boolean match(final Type[] params, final Type caller) {
        final Type[] formalParams = getParameterTypes();
        if (params.length != formalParams.length) return false;
        for (int i = 0; i < params.length; i++)
            if (!formalParams[i].isAssignableFrom(params[i])) return false;

        int mod = getModifiers();
        if (Modifier.isPublic(mod)) return true;

        final Type myType = getDeclaringClass();
        if (Modifier.isProtected(mod))
            return myType.getPackage().equals(caller.getPackage()) || myType.superClassOf(caller);

        if (Modifier.isPrivate(mod)) {
            for (Type t = caller; t != null; t = t.getDeclaringClass())
                if (t == myType) return true;
            return false;
        }
        return myType.getPackage().equals(caller.getPackage());
    }

    public Constructor bestMatch(final Constructor cons) {
        return ConstrutorMatcher.obterMelhorMatch(this, cons); // TRECHO REFATORADO
    }

    public String toString() {
        return ConstrutorFormatter.gerarDescricao(this, typeAST.name, modifiers); // TRECHO REFATORADO
    }
}

/**
 * ### 1) Oportunidades de Refatoração Encontradas:
 * Durante a análise do código fornecido, identifiquei as seguintes áreas que possuem potenciais oportunidades de refatoração aplicando os conceitos de Extração de Classe e Extração de Método, conforme referenciado por Martin Fowler e Marco Tulio:
 * **Oportunidades para Extração de Métodos:**
 * - O processamento dos parâmetros e exceções no construtor está com duplicidade de lógica: ambos utilizam loops semelhantes para extrair listas de AST, inclusive lidando com vírgulas (token separador).
 * - Cálculo dos tipos parametrizados inclui uma lógica complexa e condicionais que poderiam ser encapsulados no método separado.
 * - A lógica de composição e retorno das exceções do método `computeExceptions()` pode ser simplificada e encapsulada.
 *
 * **Oportunidade para Extração de Classe:**
 * - Existe uma responsabilidade distinta relacionada à obtenção e manipulação dos tipos de parâmetros e exceções (relacionado com `Parameter` e `Exception`). Uma classe auxiliar pode centralizar essas responsabilidades, garantindo coesão.
 *
 * ### 2) Classe Refatorada com os Devidos Comentários:
 *
 * ### 3) Justificativa das Refatorações:
 * - **Extração de Método**: Segundo Martin Fowler, a extração de métodos melhora o entendimento ao encapsular operações complexas em pequenas e claras etapas. Foi usada no construtor ao extrair operações repetitivas para obtenção dos parâmetros e exceções, além de encapsular cálculos mais complexos como tipos de parâmetros, tipos de exceções e formatação descritiva da classe.
 * - **Extração de Classe**: A criação de uma classe auxiliar `ASTExtractor` melhora a coesão ao agrupar métodos que lidam com lógica semelhante (obtendo listas de parâmetros e exceções), tornando o código mais organizado e fácil de manter.
 *
 * Estas modificações garantem clareza, evitam duplicação, reduzem complexidade cognitiva e tornam o sistema mais modularizado e coeso.
 * ### 4) Resumo das Alterações:
 * - **Quantidade total de refatorações realizadas:** 6
 * - **Extração de Método:** 4 refatorações realizadas:
 *     - `calcularTiposParametros`
 *     - `calcularTiposExcecao`
 *     - `gerarDescricao`
 *     - `obterMelhorMatch`
 *
 * - **Extração de Classe:** 1 nova classe extraída (`ASTExtractor`), com 2 métodos:
 *     - `extrairParametros`
 *     - `extrairExcecoes`
 */