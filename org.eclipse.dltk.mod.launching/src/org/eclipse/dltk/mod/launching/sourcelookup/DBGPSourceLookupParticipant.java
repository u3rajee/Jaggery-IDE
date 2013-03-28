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
import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.mod.internal.core.ScriptProject;
import org.eclipse.dltk.mod.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.mod.internal.launching.LaunchConfigurationUtils;

/**
 * This class used to get source from DBGP remote debuger, if path starts with
 * dbgp scheme.
 * 
 * @author haiodo
 * 
 */
public class DBGPSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		if (!(object instanceof ScriptStackFrame)) {
			return null;
		}
		ScriptStackFrame frame = (ScriptStackFrame) object;

		URI uri = frame.getFileName();
		if ("dbgp".equals(uri.getScheme())) { //$NON-NLS-1$
			return MessageFormat.format(Messages.DBGPSourceLookupParticipant_debugResource, new Object[] { uri.getPath() });
		}
		return uri.toString();
	}

	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof ScriptStackFrame) {
			ScriptStackFrame frame = (ScriptStackFrame) object;
			ILaunchConfiguration launchConfiguration = this.getDirector()
					.getLaunchConfiguration();

			IProject project = LaunchConfigurationUtils
					.getProject(launchConfiguration);
			ScriptProject scriptProject = (ScriptProject) DLTKCore
					.create(project);

			URI uri = frame.getFileName();
			
			if (!("dbgp".equals(uri.getScheme()))) { //$NON-NLS-1$
				return null;
			}
			return new Object[] { new DBGPSourceModule(scriptProject, frame
					.getFileName().getPath(), DefaultWorkingCopyOwner.PRIMARY,
					frame) };
		}
		return new Object[0];
	}
}
