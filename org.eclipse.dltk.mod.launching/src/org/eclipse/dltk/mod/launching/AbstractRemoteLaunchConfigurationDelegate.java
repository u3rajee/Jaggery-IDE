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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Base class for remote launch configuration delegates.
 */
public abstract class AbstractRemoteLaunchConfigurationDelegate extends
		AbstractScriptLaunchConfigurationDelegate {

	/*
	 * @see org.eclipse.dltk.mod.launching.AbstractScriptLaunchConfigurationDelegate#createInterpreterConfig(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.debug.core.ILaunch)
	 */
	protected InterpreterConfig createInterpreterConfig(
			ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		return new InterpreterConfig();
	}

	/**
	 * Returns the remote engine runner.
	 */
	protected abstract RemoteDebuggingEngineRunner getDebuggingRunner(
			IInterpreterInstall install);

	/*
	 * @see org.eclipse.dltk.mod.launching.AbstractScriptLaunchConfigurationDelegate#getInterpreterRunner(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String)
	 */
	public IInterpreterRunner getInterpreterRunner(
			ILaunchConfiguration configuration, String mode)
			throws CoreException {
		IInterpreterInstall install = verifyInterpreterInstall(configuration);
		return getDebuggingRunner(install);
	}

	/*
	 * @see org.eclipse.dltk.mod.launching.AbstractScriptLaunchConfigurationDelegate#validateLaunchConfiguration(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String)
	 */
	protected void validateLaunchConfiguration(
			ILaunchConfiguration configuration, String mode, IProject project)
			throws CoreException {
		// nothing to validate 
	}

}
