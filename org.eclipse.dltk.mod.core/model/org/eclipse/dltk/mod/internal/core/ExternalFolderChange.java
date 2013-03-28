/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.core.ModelException;

public class ExternalFolderChange {

	private ScriptProject project;
	private IBuildpathEntry[] oldResolvedBuildpath;

	public ExternalFolderChange(ScriptProject project,
			IBuildpathEntry[] oldResolvedBuildpath) {
		this.project = project;
		this.oldResolvedBuildpath = oldResolvedBuildpath;
	}

	/*
	 * Update external folders
	 */
	public void updateExternalFoldersIfNecessary(boolean refreshIfExistAlready,
			IProgressMonitor monitor) throws ModelException {
		HashSet oldFolders = ExternalFoldersManager
				.getExternalFolders(this.oldResolvedBuildpath);
		IBuildpathEntry[] newResolvedBuildpath = this.project
				.getResolvedBuildpath();
		HashSet newFolders = ExternalFoldersManager
				.getExternalFolders(newResolvedBuildpath);
		if (newFolders == null)
			return;
		ExternalFoldersManager foldersManager = ModelManager
				.getExternalManager();
		Iterator iterator = newFolders.iterator();
		while (iterator.hasNext()) {
			Object folderPath = iterator.next();
			if (oldFolders == null || !oldFolders.remove(folderPath)) {
				try {
					foldersManager.createLinkFolder((IPath) folderPath,
							refreshIfExistAlready, monitor);
				} catch (CoreException e) {
					throw new ModelException(e);
				}
			}
		}
		// removal of linked folders is done during save
	}

	public String toString() {
		return "ExternalFolderChange: " + this.project.getElementName(); //$NON-NLS-1$
	}
}
