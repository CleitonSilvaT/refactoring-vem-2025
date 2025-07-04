package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import jparse.*;

public final class MethodCallAST extends ExpressionAST implements JavaTokenTypes {

    private ExpressionAST object;
    private IdentifierAST method;
    private ListAST parameters;
    private Method theMethod;
    private Constructor theConstructor;

    public MethodCallAST(final Token token) {
        super(token);
        setType(METHOD_CALL);
    }

    public void parseComplete() {
        defineParametrosEMetodo(); // TRECHO REFATORADO

        definirObjetoDaChamada(); // TRECHO REFATORADO

        definirTipoChamada(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Extração de método para definição de parâmetros e métodos
    private void defineParametrosEMetodo() {
        AST last, a, m;
        for (last = a = getFirstChild(); !(a instanceof ListAST); a = a.getNextSibling())
            last = a;
        for (m = last.getFirstChild(); m != null; m = m.getNextSibling())
            last = m;
        method = (IdentifierAST) last;
        parameters = (ListAST) a;
    }

    // TRECHO REFATORADO - Extração de método para definição do objeto de chamada
    private void definirObjetoDaChamada() {
        object = (ExpressionAST) getFirstChild();
        if (object == method) {
            object = null;
        } else if (object.getType() == DOT) {
            object = (ExpressionAST) object.getFirstChild();
            object.parseComplete();
        }
        parameters.parseComplete();
    }

    // TRECHO REFATORADO - Extração de método: definição do tipo de chamada (método ou construtor)
    private void definirTipoChamada() {
        final String name = method.getName();
        if (name.equals("super") || name.equals("this")) {
            setType(CONSTRUCTOR_CALL);
        }
    }

    protected Type computeType() {
        Chamada cham = definirMetodoOuConstrutor(); // TRECHO REFATORADO
        return cham.definirRetorno(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Delegação para nova classe Chamada (Extração de Classe)
    private Chamada definirMetodoOuConstrutor() {
        final String nomeMetodo = method.getName();
        final Type tipoObjeto = (object != null)
                ? object.retrieveType()
                : (nomeMetodo.equals("super")
                ? method.retrieveType()
                : typeAST.retrieveType());

        return new Chamada(getType(), tipoObjeto, parameters, typeAST, nomeMetodo);
    }

    protected Type[] computeExceptions() {
        retrieveType();
        return combinarExcecoes(); // TRECHO REFATORADO
    }

    // TRECHO REFATORADO - Extração de método específico para combinação de exceções
    private Type[] combinarExcecoes() {
        final Type[] excecoesParams = parameters.getExceptionTypes();
        final Type[] evalExcepts = (object == null)
                ? excecoesParams
                : Type.mergeTypeLists(object.getExceptionTypes(), excecoesParams);
        return Type.mergeTypeLists(evalExcepts,
                (theMethod == null)
                        ? theConstructor.getExceptionTypes()
                        : theMethod.getExceptionTypes());
    }

    protected Object computeValue() {
        return nonconstant;
    }

    public VarList getVarList() {
        return (object == null)
                ? parameters.getVarList()
                : new VarList(object.getVarList(), parameters.getVarList());
    }

    public ExpressionAST getObject() {
        return object;
    }

    public IdentifierAST getMethodName() {
        return method;
    }

    public ListAST getParameters() {
        return parameters;
    }

    public Method getMethod() {
        retrieveType();
        return theMethod;
    }

    public Constructor getConstructor() {
        retrieveType();
        return theConstructor;
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        if (object != null) {
            buf.append(object.toString());
            buf.append('.');
        }
        buf.append(method.getName());
        buf.append('(');
        buf.append(parameters.toString());
        buf.append(')');
        return buf.toString();
    }
}

/**
 * ### 1) Oportunidades de Refatoração Encontradas
 * - **Método `parseComplete()`** muito longo e executando diversas responsabilidades: separação clara e coesão poderiam ser garantidas através da extração de métodos menores.
 * - **Método `computeType()`** misturando lógica interna e decisão de tipo (método ou construtor): cabe extração de métodos para clareza.
 * - **Método `computeExceptions()`** fazendo diferentes tipos de operações em único método, proporcionando oportunidade de extração de método específico para agrupamento e simplificação.
 * - Percebe-se responsabilidade em potencial para uma classe separada: lógica relacionada à diferenciação entre chamada de método ou construtor.
 *
 * ### 2) Classe Refatorada com comentários em português e indicação "TRECHO REFATORADO"
 *
 * ### 3) Justificativa das Refatorações
 * - **Extração de Métodos:**
 * Realizada em locais onde eram executadas várias operações complexas ou responsabilidades distintas em um único método. Seguindo Fowler (2018), métodos menores são preferíveis pois tornam o código mais fácil de ler e entender.
 * - **Extração de Classe (Call):**
 * A decisão entre método e construtor foi isolada em nova classe, deixando claro e coeso o papel dessa decisão segundo os padrões de Marco Tulio (Engenhar. Software Moderna), melhorando legibilidade, clareza do código original e facilitando manutenção futura.
 *
 * ### 4) Resumo das Alterações Realizadas
 * - **Quantidade total de refatorações:** 5
 * - **Divisão por tipo:**
 *     - **Extração de Método:** 4 métodos extraídos
 *     - **Extração de Classe:** 1 classe extraída (`Call`)
 */