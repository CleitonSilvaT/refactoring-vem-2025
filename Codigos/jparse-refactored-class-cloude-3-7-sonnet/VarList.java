package jparse;

import java.util.HashSet;
import jparse.expr.VarAST;

/**
 * A list of variables whose values are read by an expression or statement,
 * whose values are written by an expression or statement, or which are
 * declared by a statement
 */
public final class VarList {

    public final VarAST[] read;
    public final VarAST[] write;
    public final VarAST[] decl;

    public VarList() {
        decl = write = read = new VarAST[0];
    }

    public VarList(final VarAST readVar) {
        read = (readVar == null) ? new VarAST[0] : new VarAST[] { readVar };
        decl = write = new VarAST[0];
    }

    public VarList(final VarAST[] decls) {
        read = write = new VarAST[0];
        decl = decls;
    }

    public VarList(final VarList list, final VarAST declaration) {
        read = list.read;
        write = list.write;
        decl = new VarAST[list.decl.length + 1];
        System.arraycopy(list.decl, 0, decl, 0, list.decl.length);
        decl[list.decl.length] = declaration;
    }

    public VarList(final VarList list1, final VarList list2) {
        this(list1, list2, false);
    }

    public VarList(final VarList list1, final VarList list2, final boolean assign) {
        // TRECHO REFATORADO: Extração do método para mesclar listas read
        read = mergeReadLists(list1, list2, assign);
        
        // TRECHO REFATORADO: Extração do método para mesclar listas write
        write = mergeWriteLists(list1, list2, assign);
        
        // TRECHO REFATORADO: Extração do método para mesclar listas decl
        decl = mergeDeclLists(list1, list2);
    }

    public VarList(final VarList list1, final VarList list2, final VarList list3) {
        // TRECHO REFATORADO: Extração do método para mesclar multiple listas
        read = mergeMultipleLists(new VarAST[][] {list1.read, list2.read, list3.read});
        write = mergeMultipleLists(new VarAST[][] {list1.write, list2.write, list3.write});
        decl = mergeMultipleLists(new VarAST[][] {list1.decl, list2.decl, list3.decl});
    }

    public VarList(final VarList[] lists) {
        // TRECHO REFATORADO: Utilização do método utilitário para mesclar arrays de listas
        read = mergeListsFromArray(lists, ListType.READ);
        write = mergeListsFromArray(lists, ListType.WRITE);
        decl = mergeListsFromArray(lists, ListType.DECL);
    }
    
    // TRECHO REFATORADO: Enum auxiliar para identificar o tipo de lista
    private enum ListType {
        READ, WRITE, DECL
    }
    
    // TRECHO REFATORADO: Método para mesclar listas read com otimização para casos comuns
    private VarAST[] mergeReadLists(VarList list1, VarList list2, boolean assign) {
        if (assign || list1.read.length == 0) {
            return list2.read;
        } else if (list2.read.length == 0) {
            return list1.read;
        } else {
            return mergeArrays(list1.read, list2.read);
        }
    }
    
    // TRECHO REFATORADO: Método para mesclar listas write com otimização para casos comuns
    private VarAST[] mergeWriteLists(VarList list1, VarList list2, boolean assign) {
        if (list1.write.length == 0 && (!assign || list1.read.length == 0)) {
            return list2.write;
        } else if (list2.write.length == 0 && (!assign || list1.read.length == 0)) {
            return list1.write;
        } else {
            final HashSet merge = new HashSet();
            for (int i = 0; i < list1.write.length; i++) {
                merge.add(list1.write[i]);
            }
            for (int i = 0; i < list2.write.length; i++) {
                merge.add(list2.write[i]);
            }
            if (assign) {
                for (int i = 0; i < list1.read.length; i++) {
                    merge.add(list1.read[i]);
                }
            }
            return setToArray(merge);
        }
    }
    
    // TRECHO REFATORADO: Método para mesclar listas decl com otimização para casos comuns
    private VarAST[] mergeDeclLists(VarList list1, VarList list2) {
        if (list1.decl.length == 0) {
            return list2.decl;
        } else if (list2.decl.length == 0) {
            return list1.decl;
        } else {
            return mergeArrays(list1.decl, list2.decl);
        }
    }
    
    // TRECHO REFATORADO: Método utilitário para mesclar múltiplas listas
    private VarAST[] mergeMultipleLists(VarAST[][] arrays) {
        final HashSet merge = new HashSet();
        for (int i = 0; i < arrays.length; i++) {
            for (int j = 0; j < arrays[i].length; j++) {
                merge.add(arrays[i][j]);
            }
        }
        return setToArray(merge);
    }
    
    // TRECHO REFATORADO: Método utilitário para converter HashSet em array
    private VarAST[] setToArray(HashSet set) {
        VarAST[] result = new VarAST[set.size()];
        set.toArray(result);
        return result;
    }
    
    // TRECHO REFATORADO: Método utilitário para mesclar dois arrays em um só
    private VarAST[] mergeArrays(VarAST[] array1, VarAST[] array2) {
        final HashSet merge = new HashSet();
        for (int i = 0; i < array1.length; i++) {
            merge.add(array1[i]);
        }
        for (int i = 0; i < array2.length; i++) {
            merge.add(array2[i]);
        }
        return setToArray(merge);
    }
    
    // TRECHO REFATORADO: Método utilitário para mesclar arrays de listas
    private VarAST[] mergeListsFromArray(VarList[] lists, ListType type) {
        final HashSet merge = new HashSet();
        for (int i = 0; i < lists.length; i++) {
            final VarList theList = lists[i];
            VarAST[] array;
            switch (type) {
                case READ: array = theList.read; break;
                case WRITE: array = theList.write; break;
                default: array = theList.decl;
            }
            for (int j = 0; j < array.length; j++) {
                merge.add(array[j]);
            }
        }
        return setToArray(merge);
    }
}

/**
 * # Refatoração da Classe VarList
 * ## 1) Oportunidades de refatoração encontradas
 * Após analisar o código da classe `VarList`, identifiquei as seguintes oportunidades de refatoração:
 * 1. **Código duplicado para mesclar listas de variáveis**: Há padrões repetitivos nas operações de mesclagem de listas nos diferentes construtores. Esse código duplicado aparece em três lugares diferentes (mesclagem de listas read, write e decl).
 * 2. **Construtores longos e complexos**: Especialmente o construtor `VarList(VarList, VarList, boolean)` está realizando muitas operações em um só método.
 * 3. **Falta de encapsulamento em operações comuns**: Operações como mesclagem de arrays e verificações de casos especiais são repetidas em vários construtores.
 *
 * ## 2) Classe refatorada com os devidos comentários
 *
 * ## 3) Justificativa das refatorações
 * 1. **Extração de Métodos para Mesclagem de Listas**: Extraí o código repetitivo de mesclagem de listas em métodos específicos (`mergeReadLists`, `mergeWriteLists`, `mergeDeclLists`). Isso segue o princípio de Martin Fowler de "Extract Method", removendo duplicações e tornando o código mais legível.
 * 2. **Criação de Método Utilitário para Converter HashSet em Array**: O padrão de criar um array a partir de um HashSet aparece várias vezes no código original. A extração desse código para o método `setToArray` elimina essa duplicação.
 * 3. **Criação de Método Utilitário para Mesclar Arrays**: Extraí a lógica comum de mesclagem de dois arrays para o método `mergeArrays`, removendo mais uma fonte de duplicação.
 * 4. **Extração de Método para Mesclar Múltiplas Listas**: O método `mergeMultipleLists` elimina a duplicação presente no construtor que lida com três listas.
 * 5. **Criação de Enum para Tipos de Listas**: O enum `ListType` torna o código mais legível e tipado, evitando strings mágicas ou números para identificar os tipos de listas.
 * 6. **Método Unificado para Processar Arrays de Listas**: O método `mergeListsFromArray` unifica o processamento de arrays de listas, reduzindo a duplicação no construtor que recebe um array de `VarList`.
 *
 * ## 4) Resumo das alterações
 * - **Quantidade total de refatorações realizadas**: 8
 * - **Extração de Método**: 7 (mergeReadLists, mergeWriteLists, mergeDeclLists, mergeMultipleLists, setToArray, mergeArrays, mergeListsFromArray)
 * - **Extração de Classe**: 1 (enum ListType)
 */