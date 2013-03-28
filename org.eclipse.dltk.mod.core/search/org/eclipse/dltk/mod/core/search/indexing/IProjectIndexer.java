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
package org.eclipse.dltk.mod.core.search.indexing;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceModule;

public interface IProjectIndexer {

	/**
	 * @param project
	 */
	void indexProject(IScriptProject project);

	void indexLibrary(IScriptProject project, IPath path);

	/**
	 * @param module
	 * @param toolkit
	 */
	void indexSourceModule(ISourceModule module, IDLTKLanguageToolkit toolkit);

	/**
	 * @param project
	 * @param path
	 */
	void removeSourceModule(IScriptProject project, String path);

	/**
	 * @param project
	 * @param folder
	 */
	void indexProjectFragment(IScriptProject project, IPath path);

	/**
	 * @param scriptProject
	 * @param sourceFolder
	 */
	void removeProjectFragment(IScriptProject project, IPath path);

	/**
	 * @param projectPath
	 */
	void removeProject(IPath projectPath);

	/**
	 * @param project
	 * @param path
	 */
	void removeLibrary(IScriptProject project, IPath path);

	/**
	 * @param workingCopy
	 * @param toolkit
	 */
	void reconciled(ISourceModule workingCopy, IDLTKLanguageToolkit toolkit);

	/**
	 * Is called just after initialization to verify the indexes
	 */
	void startIndexing();

}
