package jparse.expr;

import antlr.Token;
import antlr.collections.AST;
import jparse.*;

public final class VarAST extends IdentifierAST implements JavaTokenTypes {

    private ModifierAST mods;
    private TypeAST declType;
    private JavaAST brackets;
    private InitializerAST initializer;
    private boolean field;
    
    // TRECHO REFATORADO: Adicionado objeto para lidar com manipulação de arrays
    private ArrayBracketsHandler bracketsHandler;

    public VarAST(final Token token) {
        super(token);
        symTable.addVar(this);
        // TRECHO REFATORADO: Instanciando o manipulador de brackets
        bracketsHandler = new ArrayBracketsHandler();
    }

    public void parseComplete() {
        // TRECHO REFATORADO: Extraído código para métodos menores e mais específicos
        inicializarCamposBasicos();
        processarArrayDeclarators();
        processarInicializador();
    }
    
    // TRECHO REFATORADO: Método extraído da parseComplete
    private void inicializarCamposBasicos() {
        mods = context.mods;
        declType = context.type;
        field = context.isField;
    }
    
    // TRECHO REFATORADO: Método extraído da parseComplete
    private void processarArrayDeclarators() {
        AST a = getNextSibling();
        if (a != null && a.getType() == ARRAY_DECLARATOR) {
            brackets = (JavaAST)a;
            do {
                a = a.getNextSibling().getNextSibling();
            } while (a != null && a.getType() == ARRAY_DECLARATOR);
        }
    }
    
    // TRECHO REFATORADO: Método extraído da parseComplete
    private void processarInicializador() {
        AST a = getNextSibling();
        // Precisamos avançar além dos array declarators primeiro
        if (a != null && a.getType() == ARRAY_DECLARATOR) {
            do {
                a = a.getNextSibling().getNextSibling();
            } while (a != null && a.getType() == ARRAY_DECLARATOR);
        }
        
        if (a != null && a instanceof InitializerAST) {
            initializer = (InitializerAST)a;
            initializer.parseComplete();
        }
    }

    protected Type computeType() {
        // TRECHO REFATORADO: Delegado para o manipulador de brackets
        final Type baseType = declType.retrieveType();
        if (brackets == null) {
            return baseType;
        }
        return bracketsHandler.computeArrayType(baseType, brackets);
    }

    protected Type[] computeExceptions() {
        // TRECHO REFATORADO: Delegado para o manipulador de inicializadores
        return InitializerHandler.getExceptionTypes(initializer, noTypes);
    }

    protected Object computeValue() {
        // TRECHO REFATORADO: Delegado para o manipulador de inicializadores
        return InitializerHandler.computeValue(mods, initializer, nonconstant);
    }

    public VarList getVarList() {
        // TRECHO REFATORADO: Delegado para o manipulador de inicializadores
        final VarList thisVar = new VarList(this);
        return InitializerHandler.getVarList(initializer, thisVar);
    }

    public ModifierAST getModifiers() {
        return mods;
    }

    public TypeAST getTypeName() {
        return declType;
    }

    public JavaAST getBrackets() {
        return brackets;
    }

    public InitializerAST getInitializer() {
        return initializer;
    }

    public boolean isField() {
        return field;
    }

    public String toString() {
        // TRECHO REFATORADO: Delegado para o manipulador de brackets
        final StringBuffer buf = new StringBuffer(declType.toString());
        buf.append(' ');
        buf.append(super.toString());
        bracketsHandler.appendBracketsToBuffer(buf, brackets);
        return buf.toString();
    }
    
    // TRECHO REFATORADO: Classe extraída para manipular arrays e brackets
    private class ArrayBracketsHandler {
        public Type computeArrayType(Type baseType, AST brackets) {
            final StringBuffer buf = new StringBuffer(baseType.getName());
            appendBracketsToBuffer(buf, brackets);
            try {
                return Type.forName(buf.toString());
            } catch (ClassNotFoundException classEx) {
                return null;
            }
        }
        
        public void appendBracketsToBuffer(StringBuffer buf, AST brackets) {
            for (AST b = brackets; b != null && b.getType() == ARRAY_DECLARATOR;
                b = b.getNextSibling().getNextSibling()) {
                buf.append("[]");
            }
        }
    }
}

// TRECHO REFATORADO: Nova classe extraída para manipular operações relacionadas a inicializadores
class InitializerHandler {
    public static Type[] getExceptionTypes(InitializerAST initializer, Type[] defaultTypes) {
        return (initializer == null) ? defaultTypes : initializer.getExceptionTypes();
    }
    
    public static Object computeValue(ModifierAST mods, InitializerAST initializer, Object defaultValue) {
        return (mods.isFinal() || initializer == null) ? defaultValue : initializer.getValue();
    }
    
    public static VarList getVarList(InitializerAST initializer, VarList thisVar) {
        return (initializer == null) ? thisVar : new VarList(thisVar, initializer.getVarList());
    }
}

/**
 * # Análise e Refatoração da Classe VarAST
 * Vou realizar a refatoração da classe VarAST conforme solicitado, focando apenas em extração de classe e extração de métodos.
 * ## 1) Oportunidades de refatoração encontradas
 * 1. O método `toString()` possui código duplicado com `computeType()` para iterar sobre os brackets.
 * 2. O método `parseComplete()` é grande e realiza várias operações distintas que podem ser extraídas.
 * 3. O método `computeType()` é complexo e poderia ser dividido em métodos menores.
 * 4. A lógica de manipulação dos arrays e brackets pode ser extraída para uma classe própria, pois representa uma responsabilidade específica.
 * 5. As verificações condicionais relacionadas ao initializer estão espalhadas em vários métodos e poderiam ser encapsuladas.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Classe - ArrayBracketsHandler**:
 *     - A manipulação dos brackets e arrays representava uma responsabilidade específica que estava espalhada em vários métodos. Seguindo o princípio de Responsabilidade Única de Martin Fowler, extraí essa lógica para uma classe interna dedicada, eliminando duplicação de código entre os métodos `computeType()` e `toString()`.
 *
 * 2. **Extração de Classe - InitializerHandler**:
 *     - Operações relacionadas aos inicializadores estavam espalhadas em vários métodos, sempre com verificações similares como `if (initializer == null)`. Extraí essa lógica para uma classe utilitária, melhorando a coesão e reduzindo a duplicação de código.
 *
 * 3. **Extração de Método - processarArrayDeclarators() e processarInicializador()**:
 *     - O método `parseComplete()` realizava várias tarefas distintas. Seguindo o princípio de que métodos devem fazer apenas uma coisa, extraí partes importantes em métodos separados, melhorando a legibilidade e facilitando a manutenção.
 *
 * 4. **Extração de Método - inicializarCamposBasicos()**:
 *     - As atribuições iniciais foram extraídas para um método separado, tornando o código mais auto-documentado e melhorando a legibilidade.
 *
 * 5. **Delegação de responsabilidades**:
 *     - Os métodos principais agora delegam operações específicas para as classes extraídas, seguindo o princípio de "Tell, Don't Ask" de Martin Fowler.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 7 refatorações
 *     - **Extração de Método**: 5 (inicializarCamposBasicos, processarArrayDeclarators, processarInicializador, e os métodos dentro das classes extraídas)
 *     - **Extração de Classe**: 2 (ArrayBracketsHandler e InitializerHandler)
 *
 * Estas refatorações melhoraram significativamente a organização do código, reduzindo a duplicação e aplicando o princípio de responsabilidade única. Cada parte do código agora tem uma função bem definida e clara, tornando o código mais manutenível e facilitando futuras modificações.
 */