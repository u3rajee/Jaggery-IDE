/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *     xored software, Inc. - Search All occurences bugfix, 
 *     						  hilight only class name when class is in search results ( Alex Panchenko <alex@xored.com>)
 *******************************************************************************/
/*
 * (c) 2002, 2005 xored software and others all rights reserved. http://www.xored.com
 */
package org.eclipse.dltk.mod.ast.declarations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.mod.ast.ASTListNode;
import org.eclipse.dltk.mod.ast.ASTNode;
import org.eclipse.dltk.mod.ast.ASTVisitor;
import org.eclipse.dltk.mod.ast.DLTKToken;
import org.eclipse.dltk.mod.ast.Modifiers;
import org.eclipse.dltk.mod.ast.references.SimpleReference;
import org.eclipse.dltk.mod.ast.statements.Block;
import org.eclipse.dltk.mod.ast.utils.ASTUtil;
import org.eclipse.dltk.mod.utils.CorePrinter;

/**
 * Used to represent types or classes.
 */
public class TypeDeclaration extends Declaration {
	/**
	 * Body start position in associated file.
	 */
	protected int bodyEnd;

	/**
	 * Body end position in associated file.
	 */
	protected int bodyStart;

	/**
	 * Parent classes end position in associated file.
	 */
	protected int parentEnd;

	/**
	 * Parent classes start position in associated file.
	 */
	protected int parentStart;

	/**
	 * List of all super classes. Expression may be complex such as templates or
	 * other constructions.
	 */
	protected ASTListNode fSuperClasses;

	/**
	 * List of body statements.
	 */
	protected Block fBody;

	protected List fMethods;

	protected List fTypes;

	protected List fVariables;

	protected String enclosingTypeName;

	public TypeDeclaration(DLTKToken name, int start, int end) {

		super(name, start, end);
	}

	/**
	 * Creates new type declaration from type name ANTLR token, start and end
	 * position.
	 * 
	 * @param name
	 *            type name.
	 * @param start
	 *            type start position in associated file.
	 * @param end
	 *            type end position in associated file.
	 */
	public TypeDeclaration(String name, int nameStart, int nameEnd, int start,
			int end) {

		super(start, end);
		setName(name);
		setNameStart(nameStart);
		setNameEnd(nameEnd);
		this.enclosingTypeName = ""; //$NON-NLS-1$
	}

	public List getMethodList() {
		if (this.fMethods == null) {
			initInners();
		}
		return this.fMethods;
	}

	public List getFieldList() {
		if (this.fVariables == null) {
			initInners();
		}
		return this.fVariables;
	}

	public List getTypeList() {
		if (this.fTypes == null) {
			initInners();
		}
		return this.fTypes;
	}

	public void setEnclosingTypeName(String name) {
		if (name.startsWith("$")) { //$NON-NLS-1$
			name = name.substring(1);
		}
		if (name != null && name.length() > 0) {
			this.enclosingTypeName = name;
		}
	}

	public String getEnclosingTypeName() {
		return this.enclosingTypeName;
	}

	/**
	 * Creates type declaration from name token.
	 * 
	 * @param name
	 *            name ANTRL token.
	 */
	public TypeDeclaration(DLTKToken name) {

		super();
		this.setName(name.getText());
	}

	/**
	 * Used to walk on tree. traverse order: superclasses, body.
	 */
	public void traverse(ASTVisitor visitor) throws Exception {

		if (visitor.visit(this)) {
			if (this.getSuperClasses() != null) {
				this.getSuperClasses().traverse(visitor);
			}
			if (this.fBody != null) {
				fBody.traverse(visitor);
			}
			visitor.endvisit(this);
		}
	}

	/**
	 * Return list of superclass declaration expressions.
	 * 
	 * @return
	 */
	public ASTListNode getSuperClasses() {

		return this.fSuperClasses;
	}

	/**
	 * Set superclases expression list.
	 * 
	 * @param exprList
	 */
	public void setSuperClasses(ASTListNode exprList) {

		this.fSuperClasses = exprList;
	}

	/**
	 * Add superclass expression to list of superclasses. List would be created
	 * if not yet.
	 * 
	 * @param expression
	 */
	public void addSuperClass(ASTNode expression) {

		if (this.fSuperClasses == null) {
			this.fSuperClasses = new ASTListNode();
		}

		this.fSuperClasses.addNode(expression);

	}

	/**
	 * Return TypeDeclaration kind.
	 */

	public int getKind() {

		return D_CLASS;
	}

	/**
	 * Return body end position in associated file.
	 * 
	 * @return
	 */
	public int getBodyEnd() {
		if (getBody() != null) {
			return getBody().sourceEnd();
		}
		return bodyEnd;
	}

	/**
	 * Sets body end position in associated file.
	 * 
	 * @param bodyEnd
	 */
	protected void setBodyEnd(int bodyEnd) {

		this.bodyEnd = bodyEnd;
	}

	/**
	 * Return body start position in associated file.
	 * 
	 * @return
	 */
	public int getBodyStart() {
		if (getBody() != null) {
			return getBody().sourceStart();
		}
		return bodyStart;
	}

	/**
	 * Set body start position in associated file.
	 */
	protected void setBodyStart(int bodyStart) {

		this.bodyStart = bodyStart;
	}

	/**
	 * Use sourceEnd() instead.
	 * 
	 * @return
	 * @deprecated
	 */

	public int getDeclarationSourceEnd() {

		return this.sourceEnd();
	}

	/**
	 * Use setEnd instead
	 * 
	 * @param declarationSourceEnd
	 * @deprecated
	 */

	protected void setDeclarationSourceEnd(int declarationSourceEnd) {
		this.setEnd(declarationSourceEnd);
	}

	/**
	 * Use sourceStart instead.
	 * 
	 * @return
	 * @deprecated
	 */

	public int getDeclarationSourceStart() {

		return this.sourceStart();
	}

	/**
	 * Used setStart instead
	 * 
	 * @param declarationSourceStart
	 * @deprecated
	 */

	protected void setDeclarationSourceStart(int declarationSourceStart) {

		this.setStart(declarationSourceStart);
	}

	/**
	 * Return parents end position in associated file.
	 * 
	 * @return
	 */
	public int getParentEnd() {

		return parentEnd;
	}

	/**
	 * Sets parents end position in associated file.
	 * 
	 * @param parentEnd
	 */
	protected void setParentEnd(int parentEnd) {

		this.parentEnd = parentEnd;
	}

	/**
	 * Return parents start position in associated file.
	 * 
	 * @return
	 */
	public int getParentStart() {

		return parentStart;
	}

	/**
	 * Sets parents start position in associated file.
	 * 
	 * @param parentStart
	 */
	protected void setParentStart(int parentStart) {

		this.parentStart = parentStart;
	}

	/**
	 * Set inner statements.
	 * 
	 * @param body
	 */
	public void setBody(Block body) {

		this.fBody = body;
		if (body != null) {
			this.bodyStart = body.sourceStart();
			this.bodyEnd = body.sourceEnd();
			// this.setEnd(body.sourceEnd()); //XXX: why?
		}
	}

	public Block getBody() {
		return this.fBody;
	}

	/**
	 * Set inner statements with start and end position in associated file.
	 * 
	 * @param startBody
	 *            start position.
	 * @param body
	 *            inner statements.
	 * @param endBody
	 *            end position.
	 */
	public void setBody(int startBody, Block body, int endBody) {

		this.setBody(body);
		this.setBodyStart(startBody);
		this.setBodyEnd(endBody);
	}

	/**
	 * Return super class names.
	 * 
	 * @return
	 */
	public List getSuperClassNames() {
		if (this.fSuperClasses != null) {
			final List superClasses = this.fSuperClasses.getChilds();
			final List names = new ArrayList(superClasses.size());
			for (Iterator i = superClasses.iterator(); i.hasNext();) {
				final ASTNode expr = (ASTNode) i.next();
				final String name = resolveSuperClassReference(expr);
				if (name != null) {
					names.add(name);
				}
			}
			return names;
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	public String resolveSuperClassReference(ASTNode node) {
		if (node instanceof SimpleReference) {
			return ((SimpleReference) node).getName();
		} else {
			return null;
		}
	}

	/**
	 * Testing purpose only. Prints type and all inner statements to printer.
	 */
	public void printNode(CorePrinter output) {

		output.formatPrintLn("Type" + this.getSourceRange().toString() //$NON-NLS-1$
				+ this.getNameSourceRange().toString() + ":"); //$NON-NLS-1$
		String name = this.getName();
		if (name != null) {
			output.formatPrintLn(name);
		}
		if (this.fSuperClasses != null) {
			output.formatPrintLn("("); //$NON-NLS-1$
			this.fSuperClasses.printNode(output);
			output.formatPrintLn(")"); //$NON-NLS-1$
		}
		if (this.fBody != null) {
			this.fBody.printNode(output);
		}
	}

	public MethodDeclaration[] getMethods() {
		if (this.fMethods == null) {
			initInners();
		}
		return ASTUtil.getMethods(this.getStatements(), this.fMethods);
	}

	public TypeDeclaration[] getTypes() {
		if (this.fTypes == null) {
			initInners();
		}
		return ASTUtil.getTypes(this.getStatements(), this.fTypes);
	}

	private void initInners() {
		this.fMethods = new ArrayList();
		this.fTypes = new ArrayList();
		this.fVariables = new ArrayList();
	}

	public List getStatements() {
		if (this.fBody == null) {
			this.fBody = new Block(this.sourceStart(), this.sourceEnd(), null);
		}
		return this.fBody.getStatements();
	}

	public ASTNode[] getNonTypeOrMethodNode() {
		List statements = getStatements();
		if (statements != null) {
			Iterator i = statements.iterator();
			List results = new ArrayList();
			while (i.hasNext()) {
				ASTNode node = (ASTNode) i.next();
				if (!(node instanceof TypeDeclaration)
						&& !(node instanceof MethodDeclaration)) {
					results.add(node);
				}
			}
			return (ASTNode[]) results.toArray(new ASTNode[results.size()]);
		}
		return null;
	}

	public FieldDeclaration[] getVariables() {
		if (this.fVariables == null) {
			initInners();
		}
		return ASTUtil.getVariables(this.getStatements(), this.fVariables);
	}

	public String debugString() {
		String prev = super.debugString();
		if ((this.getModifiers() & Modifiers.AccModule) != 0)
			prev += "(module)"; //$NON-NLS-1$
		return prev;
	}

	public int matchStart() {
		return getNameStart();
	}

	public int matchLength() {
		return getNameEnd() - getNameStart();
	}

}
