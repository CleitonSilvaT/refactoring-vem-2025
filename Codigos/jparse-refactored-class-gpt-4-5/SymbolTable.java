package jparse;

import java.util.HashMap;
import jparse.expr.VarAST;
import jparse.stmt.StatementAST;

public final class SymbolTable {

    final SymbolTable parent;
    private TypeAST type;
    private final HashMap varMap = new HashMap();
    private MethodCollection methodCollection = new MethodCollection(); // TRECHO REFATORADO
    private final HashMap labelMap = new HashMap();

    public SymbolTable() {
        parent = JavaAST.currSymTable;
    }

    void setEnclosingType(final TypeAST enclosingType) {
        type = enclosingType;
    }

    public void addVar(final VarAST ast) {
        varMap.put(ast.getName(), ast);
    }

    public VarAST getVar(final String name) {
        final Object ret = varMap.get(name);
        return (ret != null)
            ? (VarAST) ret
            : ((parent != null) ? parent.getVar(name) : null);
    }

    public void addMeth(final MethAST meth) {
        methodCollection.addMethod(meth); // TRECHO REFATORADO
    }

    public Method getMeth(final String name, final Type[] params, final Type caller) {
        return methodCollection.getBestMatch(name, params, caller, type, parent); // TRECHO REFATORADO
    }

    public Method[] getMeths() {
        return methodCollection.getMethods(); // TRECHO REFATORADO
    }

    public void addLabel(final String label, final JavaAST stmt) {
        labelMap.put(label, stmt);
    }

    public StatementAST getLabel(final String label) {
        final Object ret = labelMap.get(label);
        return (ret != null)
            ? (StatementAST) ret
            : ((parent != null) ? parent.getLabel(label) : null);
    }

    public String toString() {
        final StringBuffer buf = new StringBuffer("Symbol Table:\n");
        if (!varMap.isEmpty()) {
            buf.append("** Variables **\n");
            buf.append(varMap.toString());
            buf.append('\n');
        }
        if (methodCollection.getMethods().length > 0) { // TRECHO REFATORADO
            buf.append("** Methods **\n");
            buf.append(methodCollection.toString()); // TRECHO REFATORADO
        }
        if (!labelMap.isEmpty()) {
            buf.append("** Labels **\n");
            buf.append(labelMap.toString());
            buf.append('\n');
        }
        if (parent != null) {
            buf.append(parent.toString());
        }
        return buf.toString();
    }
}

package jparse;

import java.util.Arrays;

public class MethodCollection { // TRECHO REFATORADO

    private MethAST[] methods = new MethAST[0];

    public void addMethod(final MethAST meth) { // TRECHO REFATORADO
        int pos = findInsertPosition(meth);
        methods = expandArray(methods, pos, meth);
    }

    private int findInsertPosition(final MethAST meth) { // TRECHO REFATORADO
        int low = 0;
        int high = methods.length - 1;
        while (low <= high) {
            final int mid = (low + high) / 2;
            final int compare = meth.compareTo(methods[mid]);
            if (compare < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    private MethAST[] expandArray(MethAST[] array, int pos, MethAST item) { // TRECHO REFATORADO
        MethAST[] newArray = new MethAST[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, pos);
        newArray[pos] = item;
        System.arraycopy(array, pos, newArray, pos + 1, array.length - pos);
        return newArray;
    }

    public Method getBestMatch(String name, Type[] params, Type caller, TypeAST type, SymbolTable parent) { // TRECHO REFATORADO
        if (type == null) {
            return parent.getMeth(name, params, caller);
        }

        Method[] matches = getMatchingMethods(name, params, caller, type);
        if (matches.length == 0) {
            return null;
        }

        Method bestMatch = matches[0];
        boolean needBetter = false;
        for (int i = 1; i < matches.length; i++) {
            Method newMatch = bestMatch.bestMatch(matches[i]);
            needBetter = newMatch == null;
            if (newMatch != null)
                bestMatch = newMatch;
        }
        if (needBetter) {
            System.err.println("Não houve uma correspondência exata! Candidatos:");
            for (Method match : matches) {
                System.err.println(match);
            }
        }
        return bestMatch;
    }

    private Method[] getMatchingMethods(String name, Type[] params, Type caller, TypeAST type) { // TRECHO REFATORADO
        Method[] matches = type.retrieveSuperMatches(name, params, caller);
        int index = Arrays.binarySearch(methods, new MethAST(name));

        if (index < 0)
            index = -(index + 1);

        while (index > 0 && methods[index - 1].getName().equals(name))
            index--;

        for (; index < methods.length && methods[index].getName().equals(name); index++) {
            if (methods[index].match(params, caller)) {
                matches = Arrays.copyOf(matches, matches.length + 1);
                matches[matches.length - 1] = methods[index];
            }
        }

        return matches;
    }

    public MethAST[] getMethods() { // TRECHO REFATORADO
        return methods;
    }

    public String toString() { // TRECHO REFATORADO
        StringBuilder buf = new StringBuilder();
        for (MethAST method : methods) {
            buf.append(method).append('\n');
        }
        return buf.toString();
    }
}


/*
 * 1) Oportunidades de refatoração encontradas:
A análise da classe SymbolTable revelou as seguintes oportunidades para refatoração:

Extração de Classe:
Os métodos relacionados ao gerenciamento de métodos (addMeth, getMeth, getMeths) compõem um grupo coeso de funcionalidades específicas. Esses métodos realizam tarefas bem delimitadas e podem ser isolados em uma classe especializada.

Extração de Método:
Em addMeth há uma lógica para encontrar a posição correta para inserção em um array ordenado, sendo este código reutilizado parcialmente em getMeths. Esse comportamento é candidato a extração de método.

A lógica para expandir arrays (matches e methods) é repetida nos métodos getMeth, getMeths e addMeth.

2) Classe refatorada com os devidos comentários:

3) Justificativa das refatorações:
Extração da Classe MethodCollection:

Conforme Martin Fowler (2018), métodos que têm forte coesão interna devem ser extraídos para melhorar a clareza e a modularidade. A classe extraída concentra toda a responsabilidade referente à gestão dos métodos, simplificando SymbolTable.

Extração de Métodos (findInsertPosition, expandArray, getMatchingMethods):

Removem duplicação de código, seguindo os princípios de Fowler e Marco Túlio. Código duplicado dificulta manutenção e aumenta o risco de erros futuros. A extração torna mais explícito o propósito desses trechos e facilita futuras modificações.

4) Resumo das alterações realizadas:
Quantidade total: 4 refatorações.

Extração de Classe: 1

Extração de Métodos: 3
 */