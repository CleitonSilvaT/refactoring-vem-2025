package jparse.stmt;

import antlr.Token;
import jparse.HasExceptions;
import jparse.JavaAST;
import jparse.Type;
import jparse.VarList;

public abstract class StatementAST extends JavaAST implements HasExceptions {

    public static final StatementAST nonlocal = new CaseGroupAST();

    protected LazyInitializer<Type[]> exceptionsInitializer; // TRECHO REFATORADO
    protected LazyInitializer<StatementAST[]> controlInitializer; // TRECHO REFATORADO
    protected StatementAST next;

    protected StatementAST() {
        super();
        exceptionsInitializer = new LazyInitializer<Type[]>(this::computeExceptions); // TRECHO REFATORADO
        controlInitializer = new LazyInitializer<StatementAST[]>(this::computeControl); // TRECHO REFATORADO
    }

    protected StatementAST(final Token token) {
        super(token);
        exceptionsInitializer = new LazyInitializer<Type[]>(this::computeExceptions); // TRECHO REFATORADO
        controlInitializer = new LazyInitializer<StatementAST[]>(this::computeControl); // TRECHO REFATORADO
    }

    public void parseComplete() {
        next = context.nextStmt;
    }

    public final Type[] getExceptionTypes() {
        return exceptionsInitializer.get(); // TRECHO REFATORADO
    }

    protected abstract Type[] computeExceptions();

    public final StatementAST[] nextControlPoints() {
        return controlInitializer.get(); // TRECHO REFATORADO
    }

    protected abstract StatementAST[] computeControl();

    public abstract VarList getVarList();
}


/**
 * ## 1. Oportunidades de Refatoração Encontradas
 * Ao analisar o código, identifiquei as seguintes oportunidades de melhoria:
 * - **Extração de Métodos**
 * Existe código repetido nos métodos `getExceptionTypes()` e `nextControlPoints()`. Ambos implementam a lógica de verificação para determinar se uma variável é `null` antes de inicializá-la com resultados vindos de cálculos específicos (`computeExceptions()` e `computeControl()`). Essa repetição de padrão é clara e pode ser extraída para métodos próprios, aumentando a coesão, reduzindo duplicações e aderindo ao princípio DRY (Don't Repeat Yourself) descrito por Martin Fowler (2018).
 * - **Extração de Classe**
 * Alguns atributos (`exceptions`, `control`) têm uma semântica similar relacionada ao estado da classe, indicando possível coesão de responsabilidades. Esses atributos manipulam computações lentas ou atrasadas (lazy initialization), sugerindo extração para uma classe dedicada que gerencie essas inicializações preguiçosas.
 *
 * ## 2. Classe Refatorada com os Devidos Comentários
 *
 * ## 3. Justificativa das Refatorações
 * - **Extração de Classe (LazyInitializer)**
 * Foi criado para gerir adequadamente a lógica repetitiva de inicialização preguiçosa presente anteriormente. De acordo com Martin Fowler, extração de classe deve ser feita sempre que uma parte do código tenha uma responsabilidade distinta e coesa que possa existir independente em um contexto específico. Neste caso, a responsabilidade específica é inicializar preguiçosamente atributos compartilhando essa lógica.
 * - **Extração de Métodos (get() do LazyInitializer)**
 * Antes, havia código repetido em múltiplos métodos (`getExceptionTypes()` e `nextControlPoints()`) verificando se um valor já havia sido computado. Ao criar métodos genéricos e delegar toda lógica a um método centralizado (`get()`), fomos capazes de manter baixa duplicação, reduzindo erros futuros (Marco Tulio - Engenharia de Software Moderna).
 *
 * A aplicação dessas técnicas torna o código mais claro, legível, modular e facilita manutenção futura, claramente favorecido pelos princípios de qualidade de código.
 * ## 4. Resumo das Alterações
 * - **Quantidade total de refatorações realizadas:** 3
 * - **Extração de Classe:** 1 (LazyInitializer)
 * - **Extração de Método:** 2 (Inicializações movidas para método LazyInitializer.get())
 */