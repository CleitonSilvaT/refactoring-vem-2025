package jparse;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import jparse.expr.VarAST;

public final class VarList {

    // TRECHO REFATORADO: Arrays de variáveis
    public final VarAST[] read;
    public final VarAST[] write;
    public final VarAST[] decl;

    // TRECHO REFATORADO: Construtor padrão vazio
    public VarList() {
        this.read = new VarAST[0];
        this.write = new VarAST[0];
        this.decl = new VarAST[0];
    }

    // TRECHO REFATORADO: Construtor com variável de leitura
    public VarList(final VarAST readVar) {
        this.read = (readVar == null) ? new VarAST[0] : new VarAST[] { readVar };
        this.write = new VarAST[0];
        this.decl = new VarAST[0];
    }

    // TRECHO REFATORADO: Construtor com declarações
    public VarList(final VarAST[] decls) {
        this.read = new VarAST[0];
        this.write = new VarAST[0];
        this.decl = decls;
    }

    // TRECHO REFATORADO: Adiciona uma nova declaração
    public VarList(final VarList list, final VarAST declaration) {
        this.read = list.read;
        this.write = list.write;
        this.decl = Arrays.copyOf(list.decl, list.decl.length + 1);
        this.decl[list.decl.length] = declaration;
    }

    // TRECHO REFATORADO: Mescla dois VarLists (sem atribuição)
    public VarList(final VarList list1, final VarList list2) {
        this(list1, list2, false);
    }

    // TRECHO REFATORADO: Mescla dois VarLists com possível atribuição
    public VarList(final VarList list1, final VarList list2, final boolean assign) {
        this.read = VarListMerger.mergeRead(list1, list2, assign);
        this.write = VarListMerger.mergeWrite(list1, list2, assign);
        this.decl = VarListMerger.mergeDecl(list1, list2);
    }

    // TRECHO REFATORADO: Mescla três VarLists
    public VarList(final VarList list1, final VarList list2, final VarList list3) {
        this.read = VarListMerger.mergeRead(list1, list2, list3);
        this.write = VarListMerger.mergeWrite(list1, list2, list3);
        this.decl = VarListMerger.mergeDecl(list1, list2, list3);
    }

    // TRECHO REFATORADO: Mescla múltiplos VarLists
    public VarList(final VarList[] lists) {
        this.read = VarListMerger.mergeRead(lists);
        this.write = VarListMerger.mergeWrite(lists);
        this.decl = VarListMerger.mergeDecl(lists);
    }
}

// TRECHO REFATORADO: Nova classe auxiliar para manipulação de VarLists
final class VarListMerger {

    private static VarAST[] toArray(Set<VarAST> set) {
        return set.toArray(new VarAST[0]);
    }

    public static VarAST[] mergeRead(VarList l1, VarList l2, boolean assign) {
        if (assign || l1.read.length == 0) return l2.read;
        if (l2.read.length == 0) return l1.read;

        Set<VarAST> merged = new HashSet<>(Arrays.asList(l1.read));
        merged.addAll(Arrays.asList(l2.read));
        return toArray(merged);
    }

    public static VarAST[] mergeWrite(VarList l1, VarList l2, boolean assign) {
        Set<VarAST> merged = new HashSet<>(Arrays.asList(l1.write));
        merged.addAll(Arrays.asList(l2.write));
        if (assign) merged.addAll(Arrays.asList(l1.read));
        return toArray(merged);
    }

    public static VarAST[] mergeDecl(VarList l1, VarList l2) {
        if (l1.decl.length == 0) return l2.decl;
        if (l2.decl.length == 0) return l1.decl;

        Set<VarAST> merged = new HashSet<>(Arrays.asList(l1.decl));
        merged.addAll(Arrays.asList(l2.decl));
        return toArray(merged);
    }

    public static VarAST[] mergeRead(VarList l1, VarList l2, VarList l3) {
        Set<VarAST> merged = new HashSet<>();
        merged.addAll(Arrays.asList(l1.read));
        merged.addAll(Arrays.asList(l2.read));
        merged.addAll(Arrays.asList(l3.read));
        return toArray(merged);
    }

    public static VarAST[] mergeWrite(VarList l1, VarList l2, VarList l3) {
        Set<VarAST> merged = new HashSet<>();
        merged.addAll(Arrays.asList(l1.write));
        merged.addAll(Arrays.asList(l2.write));
        merged.addAll(Arrays.asList(l3.write));
        return toArray(merged);
    }

    public static VarAST[] mergeDecl(VarList l1, VarList l2, VarList l3) {
        Set<VarAST> merged = new HashSet<>();
        merged.addAll(Arrays.asList(l1.decl));
        merged.addAll(Arrays.asList(l2.decl));
        merged.addAll(Arrays.asList(l3.decl));
        return toArray(merged);
    }

    public static VarAST[] mergeRead(VarList[] lists) {
        Set<VarAST> merged = new HashSet<>();
        for (VarList v : lists) merged.addAll(Arrays.asList(v.read));
        return toArray(merged);
    }

    public static VarAST[] mergeWrite(VarList[] lists) {
        Set<VarAST> merged = new HashSet<>();
        for (VarList v : lists) merged.addAll(Arrays.asList(v.write));
        return toArray(merged);
    }

    public static VarAST[] mergeDecl(VarList[] lists) {
        Set<VarAST> merged = new HashSet<>();
        for (VarList v : lists) merged.addAll(Arrays.asList(v.decl));
        return toArray(merged);
    }
}


/**
 * 1) Oportunidades de refatoração encontradas
Duplicação e responsabilidade excessiva
A classe VarList possui diversos construtores sobrecarregados, todos com lógica de mesclagem de arrays (read, write, decl) extremamente repetitiva.

A lógica de mesclagem entre listas está duplicada em quase todos os construtores.

A responsabilidade de VarList ficou inchada com tarefas que não dizem respeito à definição de uma "lista de variáveis", mas sim à manipulação de listas de VarAST.

2) Classe refatorada com os devidos comentários

3) Justificativa das refatorações
Extração de Classe: VarListMerger
Seguindo Fowler (2018), ao aplicar Extrair Classe, isolamos o comportamento de "mesclagem de listas de variáveis", que não deveria ser responsabilidade direta da VarList.

Com isso, reduzimos o acoplamento e melhoramos a coesão da classe original.

Extração de Métodos: métodos mergeRead, mergeWrite, mergeDecl
Centralizamos toda a lógica de mesclagem repetida em métodos reutilizáveis, promovendo reuso, clareza e redução de duplicações, conforme defendido por Marco Tulio e Fowler.

A legibilidade também melhora, já que os construtores agora delegam funções específicas, tornando o código mais autoexplicativo.

4) Resumo das alterações
Total de refatorações: 13

Extração de Classe: 1 (VarListMerger)

Extração de Método: 12 (mergeRead, mergeWrite, mergeDecl, com sobrecargas)
 */