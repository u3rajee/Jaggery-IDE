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
package org.eclipse.dltk.mod.internal.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.mod.debug.core.IDbgpService;
import org.eclipse.dltk.mod.debug.core.IDebugOptions;

public class RemoteScriptDebugTarget extends ScriptDebugTarget {

	private static final String LAUNCH_CONFIGURATION_ATTR_REMOTE_WORKING_DIR = "remoteWorkingDir"; //$NON-NLS-1$
	private static final String LAUNCH_CONFIGURATION_ATTR_STRIP_SRC_FOLDERS = "stripSourceFolders"; //$NON-NLS-1$

	/**
	 * @param modelId
	 * @param dbgpService
	 * @param sessionId
	 * @param launch
	 * @param process
	 */
	public RemoteScriptDebugTarget(String modelId, IDbgpService dbgpService,
			String sessionId, ILaunch launch, IProcess process) {
		super(modelId, dbgpService, sessionId, launch, process);
	}

	/**
	 * @param modelId
	 * @param dbgpService
	 * @param sessionId
	 * @param launch
	 * @param process
	 * @param options
	 */
	public RemoteScriptDebugTarget(String modelId, IDbgpService dbgpService,
			String sessionId, ILaunch launch, IProcess process,
			IDebugOptions options) {
		super(modelId, dbgpService, sessionId, launch, process, options);
	}

	protected IScriptBreakpointPathMapper createPathMapper() {
		String remoteWorkingDir = null;
		boolean stripSrcFolders = false;

		try {
			remoteWorkingDir = getLaunch().getLaunchConfiguration()
					.getAttribute(LAUNCH_CONFIGURATION_ATTR_REMOTE_WORKING_DIR,
							""); //$NON-NLS-1$
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}

		try {
			stripSrcFolders = getLaunch().getLaunchConfiguration()
					.getAttribute(LAUNCH_CONFIGURATION_ATTR_STRIP_SRC_FOLDERS,
							false);
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}

		return new ScriptBreakpointPathMapper(getScriptProject(),
				remoteWorkingDir, stripSrcFolders);
	}

}
