/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.compiler.task;

import org.eclipse.dltk.mod.ast.ASTNode;
import org.eclipse.dltk.mod.ast.ASTVisitor;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.ast.expressions.Literal;
import org.eclipse.dltk.mod.core.DLTKCore;

public class TodoTaskAstParser extends TodoTaskRangeParser {

	/**
	 * @param preferences
	 */
	public TodoTaskAstParser(ITodoTaskPreferences preferences) {
		super(preferences);
	}

	public void initialize(ModuleDeclaration ast) {
		resetRanges();
		if (ast != null) {
			setCheckRanges(true);
			final ASTVisitor visitor = new ASTVisitor() {

				public boolean visitGeneral(ASTNode node) throws Exception {
					if (isSimpleNode(node)) {
						excludeRange(node.sourceStart(), node.sourceEnd());
					}
					return true;
				}

			};
			try {
				ast.traverse(visitor);
			} catch (Exception e) {
				DLTKCore.error("Unexpected error", e); //$NON-NLS-1$
			}
		} else {
			// fix bug 1442
			setCheckRanges(true);
		}
	}

	protected boolean isSimpleNode(ASTNode node) {
		return node instanceof Literal;
	}

}
