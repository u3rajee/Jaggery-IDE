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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.dltk.mod.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.mod.launching.ScriptLaunchConfigurationConstants;

public class RemoteScriptSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		ScriptStackFrame frame = (ScriptStackFrame) object;

		String path = frame.getFileName().getPath();
		
		/*
		 * XXX: we may also need to know the remote operating system type
		 * - see win32 check in ScriptSourceLookupParticipant
		 * 
		 * currently there is no real validation of the of this path value -
		 * the RemoteScriptSourceLookupDirector may be able to leverage this
		 * fact - it could check if the remote dir was specified, and if yes
		 * and there is no cooresponding IFile object that matches an entry
		 * in the workspace, we could return 'null' there and follow the 
		 * regular path of letting them specific where to look.
		 * 
		 * this would only apply for being looked for under the workspace -
		 * intrepreter library source would still need to be looked up
		 */
		String remoteDir = getDirector()
				.getLaunchConfiguration()
				.getAttribute(
						ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_REMOTE_WORKING_DIR,
						""); //$NON-NLS-1$

		if (path.indexOf(remoteDir) != -1) {
			return path.substring(remoteDir.length() + 1);
		}

		return path;
	}

}
