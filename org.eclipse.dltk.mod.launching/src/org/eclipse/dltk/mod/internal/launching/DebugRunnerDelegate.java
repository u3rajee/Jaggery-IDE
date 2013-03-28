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
package org.eclipse.dltk.mod.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.launching.AbstractScriptLaunchConfigurationDelegate;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;
import org.eclipse.dltk.mod.launching.IInterpreterRunner;
import org.eclipse.dltk.mod.launching.InterpreterConfig;
import org.eclipse.dltk.mod.launching.LaunchingMessages;
import org.eclipse.dltk.mod.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.mod.launching.debug.DebuggingEngineManager;
import org.eclipse.dltk.mod.launching.debug.IDebuggingEngine;

import com.ibm.icu.text.MessageFormat;

/**
 * Class used to delegate debug engine discovery before run operation is
 * executed.
 * 
 * @author haiodo
 * 
 */
public class DebugRunnerDelegate implements IInterpreterRunner {
	private IInterpreterInstall install;

	public DebugRunnerDelegate(IInterpreterInstall install) {
		this.install = install;
	}

	public void run(InterpreterConfig config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		ILaunchConfiguration launchConfiguration = launch
				.getLaunchConfiguration();
		IScriptProject scriptProject = AbstractScriptLaunchConfigurationDelegate
				.getScriptProject(launchConfiguration);

		DebuggingEngineManager manager = DebuggingEngineManager.getInstance();
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(scriptProject);
		IDebuggingEngine engine = manager.getSelectedDebuggingEngine(
				scriptProject.getProject(), toolkit.getNatureId());

		if (engine != null) {
			IInterpreterRunner runner = engine.getRunner(install);
			if (runner != null) {
				runner.run(config, launch, monitor);
			} else {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								DLTKLaunchingPlugin.PLUGIN_ID,
								ScriptLaunchConfigurationConstants.ERR_INTERPRETER_RUNNER_DOES_NOT_EXIST,
								MessageFormat
										.format(
												LaunchingMessages.InterpreterRunnerDoesntExist,
												new String[] {
														install.getName(),
														ILaunchManager.DEBUG_MODE }),
								null));
			}
		}
	}
}
