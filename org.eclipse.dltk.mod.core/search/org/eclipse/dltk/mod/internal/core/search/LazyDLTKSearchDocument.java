/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.core.search.SearchParticipant;
import org.eclipse.dltk.mod.internal.core.Model;
import org.eclipse.dltk.mod.internal.core.util.Util;

/**
 * This search document differs from {@link DLTKSearchDocument} with that it
 * only loads document contents when needed. This may be useful when adding
 * search documents into queue for further processing.
 * 
 * Loading of document is not thread safe.
 * 
 * @author michael
 */
public class LazyDLTKSearchDocument extends DLTKSearchDocument {

	public LazyDLTKSearchDocument(String path, SearchParticipant participant,
			boolean external, IProject project) {
		super(path, null, participant, external, project);
	}

	public LazyDLTKSearchDocument(String path, IPath containerPath,
			char[] contents, SearchParticipant participant, boolean external,
			IProject project) {
		super(path, containerPath, contents, participant, external, project);
	}

	private void loadContents() {
		if (charContents == null) {
			charContents = getDocumentContents(getPath());
		}
	}

	private char[] getDocumentContents(String documentPath) {
		Object target = Model.getTarget(ResourcesPlugin.getWorkspace()
				.getRoot(), new Path(documentPath), true);
		try {
			if (target instanceof IFile) {
				return Util.getResourceContentsAsCharArray((IFile) target);
			} else if (target instanceof IFileHandle) {
				return Util
						.getResourceContentsAsCharArray((IFileHandle) target);
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return CharOperation.NO_CHAR;
	}

	public String getContents() {
		loadContents();
		return new String(charContents);
	}

	public char[] getCharContents() {
		loadContents();
		return charContents;
	}
}
