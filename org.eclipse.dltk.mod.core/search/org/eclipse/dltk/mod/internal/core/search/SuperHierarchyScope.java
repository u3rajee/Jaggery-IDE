/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ITypeHierarchy;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.WorkingCopyOwner;

/**
 * Scope limited to the supertype hierarchy of a given type.
 */
public class SuperHierarchyScope extends HierarchyScope {

	public SuperHierarchyScope(IDLTKLanguageToolkit languageToolkit,
			IType type, WorkingCopyOwner owner) throws ModelException {
		super(languageToolkit, type, owner);
	}

	/**
	 * This implementation builds only supertype hierarchy for the given focus
	 * type.
	 * 
	 * @see HierarchyScope#createHierarchy(IType, WorkingCopyOwner,
	 *      IProgressMonitor)
	 */
	protected ITypeHierarchy createHierarchy(IType focusType,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws ModelException {
		return focusType.newSupertypeHierarchy(owner, monitor);
	}
}
