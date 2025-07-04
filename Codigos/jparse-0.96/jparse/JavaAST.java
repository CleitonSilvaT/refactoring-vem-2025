/*
 * @(#)$Id: JavaAST.java,v 1.3 2004/07/08 18:03:54 bdg534 Exp $
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
package jparse;

import antlr.CommonASTWithHiddenTokens;
import antlr.CommonHiddenStreamToken;
import antlr.Token;
import java.io.*;

/**
 * An AST node that is a superclass for all Java AST types.
 *
 * @version $Revision: 1.3 $, $Date: 2004/07/08 18:03:54 $
 * @author Jerry James
 */
public class JavaAST extends CommonASTWithHiddenTokens {

    /**
     * An empty array of types, for use in parameter and exception lists
     * when there are none; avoids creating new arrays when it is
     * unnecessary without resorting to lots of <code>null</code> checks
     */
    protected static final Type[] noTypes = new Type[0];

    /**
     * The current symbol table under construction
     */
    protected static SymbolTable currSymTable;

    /**
     * The current compilation context
     */
    protected static CompileContext context;

    /**
     * The symbols in the context of this AST node
     */
    public final SymbolTable symTable;

    /**
     * The top-level node for this file
     */
    public final FileAST topLevel;

    /**
     * The type (class or interface) containing this AST node
     */
    public final TypeAST typeAST;

    /**
     * Create a new Java AST
     */
    public JavaAST() {
	super();
	symTable = currSymTable;
	topLevel = FileAST.currFile;
	typeAST = TypeAST.currType;
    }

    /**
     * Create a new Java AST with an existing symbol table
     *
     * @param table the symbol table to use
     */
    public JavaAST(final SymbolTable table) {
	super();
	symTable = currSymTable = table;
	topLevel = FileAST.currFile;
	typeAST = TypeAST.currType;
    }

    /**
     * Create a new Java AST from a token
     *
     * @param token the token represented by this AST node
     */
    public JavaAST(final Token token) {
	super(token);
	symTable = currSymTable;
	topLevel = FileAST.currFile;
	typeAST = TypeAST.currType;
    }

    /**
     * Create a new Java AST from a token, with an existing symbol table
     *
     * @param token the token represented by this AST node
     * @param table the symbol table to use
     */
    public JavaAST(final Token token, final SymbolTable table) {
	super(token);
	symTable = currSymTable = table;
	topLevel = FileAST.currFile;
	typeAST = TypeAST.currType;
    }

    /**
     * Print a representation of this AST node, and its following hidden
     * tokens
     *
     * @param output the output stream on which to print
     */
    public final void print(final OutputStreamWriter output) {

	try{
	    output.write(getText());	
	    for (CommonHiddenStreamToken t = getHiddenAfter(); t != null;
		 t = t.getHiddenAfter())
		output.write(t.getText());
	}catch (IOException ioex){
	    //Should not happen
    }
    }

    /**
     * Print any hidden tokens after this AST node
     *
     * @param output the output stream on which to print
     */
    public final void printHiddenAfter(final OutputStreamWriter output) {
	 try{
	     for (CommonHiddenStreamToken t = getHiddenAfter(); t != null;
		  t = t.getHiddenAfter())
		 output.write(t.getText());
	 }catch (IOException ioex){
	     //Should not happen
	 }
    }

    /**
     * Print any hidden tokens before this AST node
     *
     * @param output the output stream on which to print
     */
    public final void printHiddenBefore(final OutputStreamWriter output) {
	CommonHiddenStreamToken tok = getHiddenBefore();
	CommonHiddenStreamToken last = null;
	while (tok != null) {
	    last = tok;
	    tok = tok.getHiddenBefore();
	}
	 try{
	     for (CommonHiddenStreamToken t = last; t != null;
		  t = t.getHiddenAfter())
	         output.write(t.getText());
         }catch (IOException ioex){
	     //Should not happen
	}
    }

    /**
     * Compute any derived attributes that must be evaluated <em>after</em>
     * the initial parse.  The default action is to just tell your children
     * that the parse is complete.
     */
    public void parseComplete() {
	for (JavaAST a = (JavaAST)getFirstChild(); a != null;
	     a = (JavaAST)a.getNextSibling()) {
	    a.parseComplete();
	}
    }
}
