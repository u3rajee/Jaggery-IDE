/*******************************************************************************
 * Copyright (c) 2012 eBay Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.launching.sourcelookup;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.dltk.mod.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.mod.internal.core.ScriptProject;
import org.eclipse.dltk.mod.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.mod.internal.launching.LaunchConfigurationUtils;

public class RemoteScriptSourceLookupDirector extends
		AbstractSourceLookupDirector {

	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new RemoteScriptSourceLookupParticipant() });
	}

	public Object getSourceElement(Object element) {
		// source element was found inside the project
		Object o = super.getSourceElement(element);
		if (o instanceof IFile) {
			return o;
		}

		// time to ask for it remotely
		ScriptStackFrame frame = (ScriptStackFrame) element;

		URI uri = frame.getFileName();
		String path = uri.getPath();

		IProject project = LaunchConfigurationUtils
				.getProject(getLaunchConfiguration());

		if (project == null) {
			return null;
		}

		// XXX: May be we use here DLTKCore.create(), to support correct
		// selection, etc in opened remote source module.
		ScriptProject scriptProject = new ScriptProject(project, null);

		/*
		 * XXX: this should probably use some kind of IStorable implementation
		 * instead of directly relying on the stack frame - that allows for
		 * re-use of the ExternalStorageEditorInput object
		 */

		return new DBGPSourceModule(scriptProject, path,
				DefaultWorkingCopyOwner.PRIMARY, frame);
	}

}
