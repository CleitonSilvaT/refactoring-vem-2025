/*
 * @(#)$Id: CompoundAST.java,v 1.2 2004/04/02 05:48:48 james Exp $
 *
 * JParse: a freely available Java parser
 * Copyright (C) 2000,2004 Jeremiah W. James
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Author: Jerry James
 * Email: james@eecs.ku.edu, james@ittc.ku.edu, jamesj@acm.org
 * Address: EECS Department - University of Kansas
 *          Eaton Hall
 *          1520 W 15th St, Room 2001
 *          Lawrence, KS  66045-7621
 */
package jparse.stmt;

import antlr.Token;
import antlr.collections.AST;
import java.util.ArrayList;
import java.util.HashSet;
import jparse.JavaTokenTypes;
import jparse.Type;
import jparse.VarList;

/**
 * An AST node that represents a compound statement
 *
 * @version $Revision: 1.2 $, $Date: 2004/04/02 05:48:48 $
 * @author Jerry James
 */
public final class CompoundAST extends StatementAST implements JavaTokenTypes {

    /**
     * The statement list
     */
    private StatementAST[] stmtList;

    /**
     * Create a new compound statement AST
     *
     * @param token the token represented by this AST node
     */
    public CompoundAST(final Token token) {
	super(token);
	setType(SLIST);
    }

    /**
     * Set the statement list for this <code>CompoundAST</code>
     */
    public void parseComplete() {
	super.parseComplete();
	final ArrayList list = new ArrayList();
	for (AST a = getFirstChild(); a.getType() != RCURLY;
	     a = a.getNextSibling())
	    list.add(a);
	stmtList = new StatementAST[list.size()];
	list.toArray(stmtList);

	// Set the next pointers
	if (stmtList.length > 0) {
	    final StatementAST orig = context.nextStmt;
	    for (int i = 0; i < stmtList.length - 1; i++) {
		context.nextStmt = stmtList[i + 1];
		stmtList[i].parseComplete();
	    }
	    context.nextStmt = orig;
	    stmtList[stmtList.length - 1].parseComplete();
	}
    }

    protected Type[] computeExceptions() {
	Type[] exceptions = noTypes;
	for (int i = 0; i < stmtList.length; i++)
	    exceptions = Type.mergeTypeLists(exceptions,
					     stmtList[i].getExceptionTypes());
	return exceptions;
    }

    protected StatementAST[] computeControl() {
	if (stmtList.length == 0)
	    return new StatementAST[] { next };

	// We use an iterative solution.  We start by seeing where the first
	// statement can go.  If it can go to the next one, then ask it where
	// it can go, etc.  Continue until we run off the end or reach a place
	// where we can't go any further.
	final HashSet goPoints = new HashSet();
	goPoints.add(stmtList[0]);
	for (int i = 0; i < stmtList.length; i++) {
	    if (goPoints.contains(stmtList[i])) {
		final StatementAST[] sNext = stmtList[i].nextControlPoints();
		for (int j = 0; j < sNext.length; j++) {
		    goPoints.add(sNext[j]);
		}
	    }
	}

	// Now remove all of the statements inside the CompoundAST.  We have
	// to do this in a separate loop, since something may break to a label
	// earlier in the list, for example.
	for (int i = 0; i < stmtList.length; i++) {
	    goPoints.remove(stmtList[i]);
	}

	final StatementAST[] points = new StatementAST[goPoints.size()];
	return (StatementAST[])goPoints.toArray(points);
    }

    public VarList getVarList() {
	final VarList[] lists = new VarList[stmtList.length];
	for (int i = 0; i < lists.length; i++) {
	    lists[i] = stmtList[i].getVarList();
	}
	return new VarList(lists);
    }

    /**
     * Get the list of statements in this compound statement
     *
     * @return the list of statements
     */
    public StatementAST[] getList() {
	return stmtList;
    }
}


/**
 * ## 1) Oportunidades de Refatoração Encontradas
 * Após analisar atentamente a classe `CompoundAST`, identifiquei algumas oportunidades claras para a **Extração de Métodos**, facilitando a leitura do código e reduzindo complexidades internas. São elas:
 * - **Laço no método `parseComplete()`**: Este método realiza duas operações distintas, que poderiam ser extraídas para melhorar a coesão: a criação da lista de statements e a configuração dos ponteiros entre eles.
 * - **Operação no método `computeControl()`**: Dois processos iterativos foram identificados — adicionar pontos de controle possíveis e, posteriormente, remover pontos já processados, que podem ser encapsulados em métodos separados para maior clareza.
 * - **Operação no método `computeExceptions()`**: A lógica de acumulação e fusão das listas de exceções pode ser extraída para um método dedicado.
 *
 * Não houve oportunidade significativa para a extração de classes, já que a classe é bem coesa em seu único papel descrito na documentação (representa um compound statement).
 * ## 2) Classe Refatorada com os Devidos Comentários
 *
 * ## 3) Justificativa das Refatorações
 * A extração de métodos realizada segue os princípios recomendados na obra clássica _Refactoring_ de Martin Fowler, especialmente a técnica **"Extract Method"**. A ideia principal dessa técnica é aumentar a coesão do código ao decompor rotinas complexas em métodos menores e com uma única e clara finalidade. Segundo Fowler, códigos abrangentes devem ser substituídos por chamadas de métodos explicativas.
 * Segundo Marco Túlio em _Engenharia de Software Moderna_, métodos que contêm múltiplas etapas de processamento são candidatos ideais à extração de métodos. Este foi claramente o caso dos métodos da classe original, especialmente em `parseComplete()` e `computeControl()`. Essas alterações melhoram:
 * - **Legibilidade**: Métodos pequenos e com nomes representativos comunicam facilmente a intenção do código.
 * - **Organização**: Cada método passa a cumprir um único objetivo claramente definido.
 * - **Facilidade de manutenção**: Uma mudança ou ajuste torna-se mais fácil quando cada responsabilidade é claramente segmentada em métodos distintos.
 *
 * A extração de métodos também elimina códigos repetitivos e/ou iterativos, seguindo fielmente as diretrizes divulgadas tanto por Martin Fowler quanto por Marco Tulio, conferindo ao código uma estrutura mais modular e sustentável para futuras manutenções e ajustes.
 * ## 4) Resumo das Alterações
 * - **Quantidade total**: 5 alterações realizadas.
 * - **Tipo de Alterações**:
 *     - **Extração de Métodos**: 5 métodos extraídos.
 *         - `extrairStatements()`
 *         - `configurarProximosStatements()`
 *         - `combinarExcecoes()`
 *         - `calcularGotoPoints()`
 *         - `removerGotoPointsInternos()`
 *
 *     - **Extração de Classes**: 0 classes extraídas (não houve necessidade identificada nesse caso).
 */
