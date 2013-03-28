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
package org.eclipse.dltk.mod.ui.editor.highlighting;

import org.eclipse.dltk.mod.ast.ASTVisitor;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.SourceParserUtil;

/**
 * Abstract base class for the semantic highlighters operating on the AST tree.
 */
public abstract class ASTSemanticHighlighter extends
		AbstractSemanticHighlighter {

	protected boolean doHighlighting(
			org.eclipse.dltk.mod.compiler.env.ISourceModule code) throws Exception {
		final ModuleDeclaration module = parseCode(code);
		if (module != null) {
			module.traverse(createVisitor(code));
			return true;
		}
		return false;
	}

	/**
	 * @param code
	 * @return
	 * @throws ModelException
	 */
	protected ModuleDeclaration parseCode(
			org.eclipse.dltk.mod.compiler.env.ISourceModule code)
			throws ModelException {
		if (code instanceof ISourceModule) {
			return parseSourceModule((ISourceModule) code);
		} else {
			return parseSourceCode(code);
		}
	}

	protected ModuleDeclaration parseSourceCode(
			org.eclipse.dltk.mod.compiler.env.ISourceModule code)
			throws ModelException {
		return SourceParserUtil.getModuleDeclaration(code.getFileName(), code
				.getContentsAsCharArray(), getNature(), null, null);
	}

	protected ModuleDeclaration parseSourceModule(
			final ISourceModule sourceModule) {
		return SourceParserUtil.getModuleDeclaration(sourceModule);
	}

	protected abstract String getNature();

	protected abstract ASTVisitor createVisitor(
			org.eclipse.dltk.mod.compiler.env.ISourceModule sourceCode)
			throws ModelException;

}
