/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.corext.refactoring.reorg;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IScriptFolder;
import org.eclipse.dltk.mod.core.ISourceModule;


public interface INewNameQueries {
	public INewNameQuery createNewSourceModuleNameQuery(ISourceModule cu, String initialSuggestedName);
	public INewNameQuery createNewResourceNameQuery(IResource res, String initialSuggestedName);
	public INewNameQuery createNewPackageNameQuery(IScriptFolder pack, String initialSuggestedName);
	public INewNameQuery createNewProjectFragmentNameQuery(IProjectFragment root, String initialSuggestedName);
	public INewNameQuery createNullQuery();
	public INewNameQuery createStaticQuery(String newName);
}
