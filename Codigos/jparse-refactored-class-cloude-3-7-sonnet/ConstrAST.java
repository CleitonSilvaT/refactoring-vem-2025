package jparse;

import antlr.Token;
import antlr.collections.AST;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import jparse.JavaTokenTypes;
import jparse.expr.IdentifierAST;
import jparse.expr.VarAST;
import jparse.stmt.CompoundAST;


public final class ConstrAST extends JavaAST
    implements Constructor, JavaTokenTypes {

    private final ModifierAST modifiers;
    private final ParameterAST[] paramNames;
    private Type[] paramTypes;
    private final IdentifierAST[] exceptNames;
    private Type[] exceptions;
    private final CompoundAST body;

    // TRECHO REFATORADO: Extraído ConstructorFormatter como classe auxiliar para formatação de strings
    private final ConstructorFormatter formatter;
    
    // TRECHO REFATORADO: Extraído VisibilityChecker como classe auxiliar para verificações de visibilidade
    private final VisibilityChecker visibilityChecker;
  
    ConstrAST() {
        modifiers = new ModifierAST(Modifier.PUBLIC);
        paramNames = new ParameterAST[0];
        exceptNames = new IdentifierAST[0];
        body = null;
        // TRECHO REFATORADO: Inicialização das classes auxiliares
        formatter = new ConstructorFormatter(this);
        visibilityChecker = new VisibilityChecker(this);
    }

    ConstrAST(final ModifierAST mods, final JavaAST parameters,
              final JavaAST exceptions, final CompoundAST block) {
        setType(CTOR_DEF);
        modifiers = mods;

        // TRECHO REFATORADO: Extraído método para processar parâmetros
        paramNames = processParameters(parameters);

        // TRECHO REFATORADO: Extraído método para processar exceções
        exceptNames = processExceptions(exceptions);
        
        body = block;

        // Register the constructor
        TypeAST.currType.addConstructor(this);
        
        // TRECHO REFATORADO: Inicialização das classes auxiliares
        formatter = new ConstructorFormatter(this);
        visibilityChecker = new VisibilityChecker(this);
    }

    // TRECHO REFATORADO: Método extraído para processar parâmetros
    private ParameterAST[] processParameters(JavaAST parameters) {
        final ArrayList pTypes = new ArrayList();
        for (AST p = parameters.getFirstChild(); p != null;
             p = p.getNextSibling()) {
            pTypes.add(p);
            p = p.getNextSibling();  // Skip the comma
            if (p == null) {
                break;
            }
        }
        ParameterAST[] result = new ParameterAST[pTypes.size()];
        pTypes.toArray(result);
        return result;
    }

    // TRECHO REFATORADO: Método extraído para processar exceções
    private IdentifierAST[] processExceptions(JavaAST exceptions) {
        if (exceptions != null) {
            final ArrayList eTypes = new ArrayList();
            for (AST e = exceptions.getFirstChild(); e != null;
                 e = e.getNextSibling()) {
                eTypes.add(e);
                e = e.getNextSibling();  // Skip the comma
                if (e == null)
                    break;
            }
            IdentifierAST[] result = new IdentifierAST[eTypes.size()];
            eTypes.toArray(result);
            return result;
        } else {
            return new IdentifierAST[0];
        }
    }

    public void parseComplete() {
        context.isField = false;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i].parseComplete();
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
            // TRECHO REFATORADO: Extraído método para computar tipos de parâmetros
            paramTypes = computeParameterTypes();
        }
        return paramTypes;
    }
    
    // TRECHO REFATORADO: Método extraído para calcular tipos de parâmetros
    private Type[] computeParameterTypes() {
        Type[] result;
        int source;
        if (typeAST.outer != null && !typeAST.modifiers.isStatic() &&
            !((SourceType)typeAST.retrieveType()).anonymous) {
            // Add the hidden first parameter for non-static inner classes
            result = new Type[paramNames.length + 1];
            result[0] = typeAST.outer.retrieveType();
            source = 1;
        } else {
            result = new Type[paramNames.length];
            source = 0;
        }
        for (int i = 0; i < paramNames.length; i++, source++) {
            result[source] = paramNames[i].getParamName().retrieveType();
        }
        return result;
    }

    public final Type[] getExceptionTypes() {
        if (exceptions == null)
            exceptions = computeExceptions();
        return exceptions;
    }

    protected Type[] computeExceptions() {
        final Type[] exceptTypes = new Type[exceptNames.length];
        for (int i = 0; i < exceptNames.length; i++) {
            try {
                exceptTypes[i] = topLevel.getType(exceptNames[i].getName());
            } catch (ClassNotFoundException classEx) {
            }
        }
        return exceptTypes;
    }
  
    public CompoundAST getBody() {
        return body;
    }
   
    public boolean match(final Type[] params, final Type caller) {
        // TRECHO REFATORADO: Extraído método para verificar correspondência de parâmetros
        if (!parametersMatch(params)) {
            return false;
        }

        // TRECHO REFATORADO: Delegação para classe auxiliar para verificação de visibilidade
        return visibilityChecker.checkVisibility(caller);
    }
    
    // TRECHO REFATORADO: Método extraído para verificar correspondência de parâmetros
    private boolean parametersMatch(final Type[] params) {
        final Type[] formalParams = getParameterTypes();
        if (params.length != formalParams.length) {
            return false;
        }

        for (int i = 0; i < params.length; i++) {
            if (!formalParams[i].isAssignableFrom(params[i])) {
                return false;
            }
        }
        
        return true;
    }
    
    public Constructor bestMatch(final Constructor cons) {
        // Since they both match, these arrays have equal length
        final Type[] parms1 = getParameterTypes();
        final Type[] parms2 = cons.getParameterTypes();

        // TRECHO REFATORADO: Extraído método para comparar tipos de parâmetros
        int comp = compareParameterTypes(parms1, parms2);

        // What's the answer?
        switch (comp) {
        case -1:
            return this;
        case 1:
            return cons;
        default:
            return null;
        }
    }
    
    // TRECHO REFATORADO: Método extraído para comparar tipos de parâmetros
    private int compareParameterTypes(Type[] parms1, Type[] parms2) {
        int comp = 0;
        for (int i = 0; i < parms1.length; i++) {
            final boolean assignToMe  = parms1[i].isAssignableFrom(parms2[i]);
            final boolean assignOther = parms2[i].isAssignableFrom(parms1[i]);
            if (assignToMe && !assignOther) {
                if (comp == -1)
                    return 0; // Indica que não há melhor correspondência
                comp = 1;
            } else if (!assignToMe && assignOther) {
                if (comp == 1)
                    return 0; // Indica que não há melhor correspondência
                comp = -1;
            }
        }
        return comp;
    }
    
    public String toString() {
        // TRECHO REFATORADO: Delegação para classe auxiliar
        return formatter.formatToString();
    }
    
    // TRECHO REFATORADO: Classe extraída para tratar formatação
    private class ConstructorFormatter {
        private final ConstrAST constr;
        
        ConstructorFormatter(ConstrAST constr) {
            this.constr = constr;
        }
        
        String formatToString() {
            final StringBuffer buf = new StringBuffer(modifiers.toString());
            if (buf.length() != 0) {
                buf.append(' ');
            }
            buf.append(typeAST.name);
            appendParameters(buf);
            appendExceptions(buf);
            return buf.toString();
        }
        
        private void appendParameters(StringBuffer buf) {
            buf.append('(');
            final Type[] params = getParameterTypes();
            if (params.length > 0) {
                for (int i = 0; i < params.length - 1; i++) {
                    buf.append(params[i].getName());
                    buf.append(',');
                }
                buf.append(params[params.length - 1].getName());
            }
            buf.append(')');
        }
        
        private void appendExceptions(StringBuffer buf) {
            final Type[] exceptions = getExceptionTypes();
            if (exceptions.length > 0) {
                buf.append(" throws ");
                for (int i = 0; i < exceptions.length - 1; i++) {
                    buf.append(exceptions[i].getName());
                    buf.append(',');
                }
                buf.append(exceptions[exceptions.length - 1].getName());
            }
        }
    }
    
    // TRECHO REFATORADO: Classe extraída para verificar visibilidade
    private class VisibilityChecker {
        private final ConstrAST constr;
        
        VisibilityChecker(ConstrAST constr) {
            this.constr = constr;
        }
        
        boolean checkVisibility(Type caller) {
            // Is it public?
            final int mod = getModifiers();
            if (Modifier.isPublic(mod)) {
                return true;
            }

            // Is it protected?
            final Type myType = getDeclaringClass();
            if (Modifier.isProtected(mod)) {
                return myType.getPackage().equals(caller.getPackage()) ||
                    myType.superClassOf(caller);
            }

            // Is it private?
            if (Modifier.isPrivate(mod)) {
                return isCallerInnerClass(caller, myType);
            }

            // It must have package visibility
            return myType.getPackage().equals(caller.getPackage());
        }
        
        private boolean isCallerInnerClass(Type caller, Type myType) {
            // Is caller equal to or an inner class of myType?
            for (Type t = caller; t != null; t = t.getDeclaringClass()) {
                if (t == myType) {
                    return true;
                }
            }
            return false;
        }
    }
}

/**
 * Vou realizar a análise e refatoração do código da classe ConstrAST conforme solicitado, focando em Extração de Classe e Extração de Métodos.
 * ## 1) Oportunidades de refatoração encontradas
 * 1. **Código duplicado para processamento de coleções**: O construtor contém código duplicado para processar listas de parâmetros e exceções.
 * 2. **Método `toString()` muito longo e complexo**: Este método realiza muitas operações de formatação que poderiam ser extraídas.
 * 3. **Lógica de verificação de visibilidade em `match()`**: O método contém várias verificações de visibilidade que poderiam ser extraídas.
 * 4. **Processamento de parâmetros no método `getParameterTypes()`**: Contém lógica complexa que poderia ser extraída.
 * 5. **Responsabilidades misturadas**: Algumas funcionalidades poderiam ser encapsuladas em classes auxiliares.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Método - processParameters e processExceptions**:
 *     - A lógica para processar parâmetros e exceções seguia um padrão muito semelhante, mas estava duplicada no construtor. Segundo Martin Fowler, duplicação é um dos principais "maus cheiros" no código.
 *     - Ao extrair esses métodos, aumentamos a legibilidade e eliminamos duplicação, facilitando mudanças futuras.
 *
 * 2. **Extração de Método - computeParameterTypes**:
 *     - O método `getParameterTypes()` continha lógica complexa que foi extraída para um método auxiliar, seguindo o princípio de responsabilidade única.
 *     - Esta extração torna o código mais modular e facilita os testes unitários.
 *
 * 3. **Extração de Método - parametersMatch e compareParameterTypes**:
 *     - Os métodos `match()` e `bestMatch()` continham blocos de código com propósitos específicos que foram isolados em métodos próprios.
 *     - Isso melhora a legibilidade, isolando responsabilidades específicas e facilitando manutenção futura.
 *
 * 4. **Extração de Classe - ConstructorFormatter**:
 *     - A formatação de strings no método `toString()` é uma responsabilidade que pode ser isolada em uma classe própria.
 *     - Isso segue o princípio de responsabilidade única de SOLID, onde cada classe deve ter apenas uma razão para mudar.
 *
 * 5. **Extração de Classe - VisibilityChecker**:
 *     - A verificação de visibilidade em `match()` é uma funcionalidade complexa que merece sua própria classe.
 *     - Esta refatoração segue o princípio de coesão alta, agrupando comportamentos relacionados.
 *
 * Todas essas refatorações estão alinhadas com os princípios de Martin Fowler em "Refactoring: Improving the Design of Existing Code", onde ele destaca a importância de código legível, modular e com responsabilidades bem definidas.
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 9
 * - **Divisão por tipo**:
 *     - **Extração de Método**: 7
 *         - processParameters
 *         - processExceptions
 *         - computeParameterTypes
 *         - parametersMatch
 *         - compareParameterTypes
 *         - isCallerInnerClass (dentro de VisibilityChecker)
 *         - appendParameters e appendExceptions (dentro de ConstructorFormatter)
 *
 *     - **Extração de Classe**: 2
 *         - ConstructorFormatter
 *         - VisibilityChecker
 *
 * Estas refatorações melhoram significativamente a organização do código, tornando-o mais modular, legível e mais fácil de manter. Cada componente agora tem responsabilidades mais bem definidas e focadas, o que facilita eventuais modificações no futuro.

 *
 */