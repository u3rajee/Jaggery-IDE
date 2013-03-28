/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.mod.ti;

import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.ISourceModule;

public class BasicContext implements IContext, ISourceModuleContext {

	private final ISourceModule sourceModule;
	private final ModuleDeclaration rootNode;

	public BasicContext(ISourceModule sourceModule, ModuleDeclaration rootNode) {
		this.sourceModule = sourceModule;
		this.rootNode = rootNode;
	}

	public BasicContext(ISourceModuleContext parent) {
		sourceModule = parent.getSourceModule();
		rootNode = parent.getRootNode();
	}

	public ModuleDeclaration getRootNode() {
		return rootNode;
	}

	public ISourceModule getSourceModule() {
		return sourceModule;
	}

	public String getLangNature() {
		if (sourceModule != null) {
			IDLTKLanguageToolkit languageToolkit = DLTKLanguageManager
					.getLanguageToolkit(sourceModule);
			if (languageToolkit != null) {
				return languageToolkit.getNatureId();
			}
		}
		return null;
	}

	public String toString() {
		return "BasicContext, module " + sourceModule.getElementName(); //$NON-NLS-1$
	}
}
