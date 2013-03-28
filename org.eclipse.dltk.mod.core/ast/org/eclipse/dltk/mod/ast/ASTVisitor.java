/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
/*
 * (c) 2002, 2005 xored software and others all rights reserved. http://www.xored.com
 */

package org.eclipse.dltk.mod.ast;

import org.eclipse.dltk.mod.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.mod.ast.expressions.Expression;
import org.eclipse.dltk.mod.ast.statements.Statement;
import org.eclipse.dltk.mod.compiler.problem.IProblem;

public abstract class ASTVisitor {
	public void acceptProblem(IProblem problem) {
	}

	public boolean visitGeneral(ASTNode node) throws Exception {
		return true;
	}
	
	public void endvisitGeneral(ASTNode node) throws Exception {
	}

	public boolean visit(Statement s) throws Exception {
		return visitGeneral(s);
	}

	public boolean visit(Expression s) throws Exception {
		return visitGeneral(s);
	}

	public boolean visit(TypeDeclaration s) throws Exception {
		return visitGeneral(s);
	}

	public boolean visit(MethodDeclaration s) throws Exception {
		return visitGeneral(s);
	}

	public boolean visit(ModuleDeclaration s) throws Exception {
		return visitGeneral(s);
	}

	public boolean endvisit(Statement s) throws Exception {
		endvisitGeneral(s);
		return false;
	}

	public boolean endvisit(Expression s) throws Exception {
		endvisitGeneral(s);
		return false;
	}

	public boolean endvisit(TypeDeclaration s) throws Exception {
		endvisitGeneral(s);
		return false;
	}

	public boolean endvisit(MethodDeclaration s) throws Exception {
		endvisitGeneral(s);
		return false;
	}

	public boolean endvisit(ModuleDeclaration s) throws Exception {
		endvisitGeneral(s);
		return false;
	}
	
	public boolean visit (ASTNode s) throws Exception {
		return visitGeneral(s);		
	}
	
	public boolean endvisit (ASTNode s) throws Exception {
		endvisitGeneral(s);
		return false;
	}
	
}
