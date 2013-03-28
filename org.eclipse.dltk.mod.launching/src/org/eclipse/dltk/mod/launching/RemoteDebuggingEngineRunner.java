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
package org.eclipse.dltk.mod.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.mod.core.PreferencesLookupDelegate;
import org.eclipse.dltk.mod.debug.core.IDbgpService;
import org.eclipse.dltk.mod.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.mod.internal.debug.core.model.RemoteScriptDebugTarget;

public abstract class RemoteDebuggingEngineRunner extends DebuggingEngineRunner {

	public RemoteDebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	protected IScriptDebugTarget createDebugTarget(ILaunch launch,
			IDbgpService dbgpService) throws CoreException {
		return new RemoteScriptDebugTarget(getDebugModelId(), dbgpService,
				getSessionId(launch.getLaunchConfiguration()), launch, null);
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getSessionId(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	protected String getSessionId(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_SESSION_ID,
				""); //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#addEngineConfig(org.eclipse.dltk.mod.launching.InterpreterConfig,
	 *      org.eclipse.dltk.mod.core.IScriptProject)
	 */
	/**
	 * @deprecated Use {@link #addEngineConfig(InterpreterConfig,PreferencesLookupDelegate,ILaunch)} instead
	 */
	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate) {
				return addEngineConfig(config, delegate, null);
			}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#addEngineConfig(org.eclipse.dltk.mod.launching.InterpreterConfig,
	 *      org.eclipse.dltk.mod.core.IScriptProject)
	 */
	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate, ILaunch launch) {
		return config;
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#run(org.eclipse.dltk.mod.launching.InterpreterConfig,
	 *      org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(InterpreterConfig config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		try {
			initializeLaunch(launch, config,
					createPreferencesLookupDelegate(launch));
			waitDebuggerConnected(null, launch, monitor);
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		}
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getDebuggingEngineId()
	 */
	protected String getDebuggingEngineId() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getDebuggingEnginePreferenceQualifier()
	 */
	protected String getDebuggingEnginePreferenceQualifier() {
		return getDebugPreferenceQualifier();
	}
	
	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getLoggingEnabledPreferenceKey()
	 */
	protected String getLoggingEnabledPreferenceKey() {
		// not supported on the client side
		return null;
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getLogFileNamePreferenceKey()
	 */
	protected String getLogFileNamePreferenceKey() {
		// not supported on the client side
		return null;
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.DebuggingEngineRunner#getLogFilePathPreferenceKey()
	 */
	protected String getLogFilePathPreferenceKey() {
		// not supported on the client side
		return null;
	}
}
