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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.PreferencesLookupDelegate;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.dbgp.DbgpSessionIdGenerator;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.mod.debug.core.ExtendedDebugEventDetails;
import org.eclipse.dltk.mod.debug.core.IDbgpService;
import org.eclipse.dltk.mod.debug.core.ScriptDebugManager;
import org.eclipse.dltk.mod.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.mod.debug.core.model.IScriptDebugThreadConfigurator;
import org.eclipse.dltk.mod.internal.debug.core.model.DebugEventHelper;
import org.eclipse.dltk.mod.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.mod.internal.launching.InterpreterMessages;
import org.eclipse.dltk.mod.launching.debug.DbgpConstants;
import org.eclipse.dltk.mod.launching.debug.DebuggingEngineManager;
import org.eclipse.dltk.mod.launching.debug.IDebuggingEngine;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public abstract class DebuggingEngineRunner extends AbstractInterpreterRunner {
	// Launch attributes
	public static final String LAUNCH_ATTR_DEBUGGING_ENGINE_ID = "debugging_engine_id"; //$NON-NLS-1$

	public static final String OVERRIDE_EXE = "OVERRIDE_EXE"; //$NON-NLS-1$

	protected String getSessionId(ILaunchConfiguration configuration)
			throws CoreException {
		return DbgpSessionIdGenerator.generate();
	}

	protected IScriptDebugTarget addDebugTarget(ILaunch launch,
			IDbgpService dbgpService) throws CoreException {
		final IScriptDebugTarget target = createDebugTarget(launch, dbgpService);
		launch.addDebugTarget(target);
		return target;
	}

	protected IScriptDebugTarget createDebugTarget(ILaunch launch,
			IDbgpService dbgpService) throws CoreException {
		return new ScriptDebugTarget(getDebugModelId(), dbgpService,
				getSessionId(launch.getLaunchConfiguration()), launch, null);
	}

	public DebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	protected void initializeLaunch(ILaunch launch, InterpreterConfig config,
			PreferencesLookupDelegate delegate) throws CoreException {
		final IDbgpService service = DLTKDebugPlugin.getDefault()
				.getDbgpService();

		if (!service.available()) {
			abort(InterpreterMessages.errDbgpServiceNotAvailable, null);
		}

		final IScriptDebugTarget target = addDebugTarget(launch, service);

		String qualifier = getDebugPreferenceQualifier();

		target.toggleGlobalVariables(delegate.getBoolean(qualifier,
				showGlobalVarsPreferenceKey()));
		target.toggleClassVariables(delegate.getBoolean(qualifier,
				showClassVarsPreferenceKey()));
		target.toggleLocalVariables(delegate.getBoolean(qualifier,
				showLocalVarsPreferenceKey()));

		// Disable the output of the debugging engine process
		// if (DLTKDebugLaunchConstants.isDebugConsole(launch)) {
		// launch.setAttribute(DebugPlugin.ATTR_CAPTURE_OUTPUT, Boolean.FALSE
		// .toString());
		// }

		// Debugging engine id
		launch.setAttribute(LAUNCH_ATTR_DEBUGGING_ENGINE_ID,
				getDebuggingEngineId());

		config
				.setProperty(DbgpConstants.SESSION_ID_PROP, target
						.getSessionId());
		config.setProperty(DbgpConstants.PORT_PROP, Integer.toString(service
				.getPort()));
		config.setProperty(DbgpConstants.HOST_PROP, getBindAddress());

		/**
		 * use env vars instead of system properties, because: system properties
		 * belong to the current running vm instance; while env vars can work in
		 * the vm instance that will be created by this 'launch'.
		 */
		ILaunchConfigurationWorkingCopy copy = launch.getLaunchConfiguration()
				.getWorkingCopy();
		Map varMap = copy.getAttribute(
				ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map) null);
		if (varMap == null) {
			varMap = new HashMap();
			copy
					.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES,
							varMap);
		}

		StringBuilder vjetArgs = new StringBuilder();
		// vjetArgs.append("-DVJETDebugHost=").append(
		// config.getProperty(DbgpConstants.HOST_PROP));
		IScriptProject proj = AbstractScriptLaunchConfigurationDelegate
				.getScriptProject(launch.getLaunchConfiguration());

		// do not cross boundary to java sdk - looks like we are tied to Java
		// IDE here
		IJavaProject myJavaProject = JavaCore.create(proj.getProject());

		String javaprojectprop = "org.eclipse.jdt.launching.PROJECT_NAME";
		String projectName = myJavaProject.getProject().getName();
		copy.setAttribute(javaprojectprop, projectName);
		/**
		 * vmArgs.add( "-DVJETDebugHost=" +
		 * config.getProperty(DbgpConstants.HOST_PROP)); vmArgs.add(
		 * "-DVJETDebugPort=" + config.getProperty(DbgpConstants.PORT_PROP));
		 * vmArgs.add( "-DVJETDebugSessionID=" +
		 * config.getProperty(DbgpConstants.SESSION_ID_PROP));
		 */

		String vmargprop = "org.eclipse.jdt.launching.VM_ARGUMENTS";
		String vmArgs = copy.getAttribute(vmargprop, "");

		// TODO should we replace existing session?
		if (vmArgs.contains("-DVJET")) {
			vmArgs = "";
		}

		//
		// String absolutePath = proj.getProject().getProject().getLocationURI()
		//		
		IFileStore s = org.eclipse.core.filesystem.EFS.getStore(proj
				.getProject().getProject().getLocationURI());
		File f = s.toLocalFile(0, null);

		// IJavaProject myJavaProject = JavaCore.create(proj.getProject());
		// IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// IFile ifile = workspace.getRoot().getFile(proj.getPath());

		vjetArgs.append(" -DVJETDebugHost=").append(this.getBindAddress())
				.append(" -DVJETDebugPort=").append(
						Integer.toString(service.getPort())).append(
						" -DVJETDebugSessionID=").append(target.getSessionId())
				.append(" -DVJETProjectPath=" + f.getAbsolutePath());
		vmArgs = vmArgs + " " + vjetArgs;

		copy.setAttribute(vmargprop, vmArgs);

		copy.doSave();
	}

	private void setDBGPDebugSystemProperties(IScriptDebugTarget target,
			IDbgpService service) {
		System.setProperty("VJETDebugHost", this.getBindAddress());
		System
				.setProperty("VJETDebugPort", Integer.toString(service
						.getPort()));
		System.setProperty("VJETDebugSessionID", target.getSessionId());
	}

	private String getBindAddress() {
		return DLTKDebugPlugin.getDefault().getBindAddress();
	}

	/**
	 * Add the debugging engine configuration.
	 * 
	 * @param launch
	 *            TODO
	 */
	protected abstract InterpreterConfig addEngineConfig(
			InterpreterConfig config, PreferencesLookupDelegate delegate,
			ILaunch launch) throws CoreException;

	public void run(InterpreterConfig config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		monitor.beginTask(InterpreterMessages.DebuggingEngineRunner_launching,
				5);
		if (monitor.isCanceled()) {
			return;
		}
		try {
			PreferencesLookupDelegate prefDelegate = createPreferencesLookupDelegate(launch);

			initializeLaunch(launch, config, prefDelegate);
			InterpreterConfig newConfig = addEngineConfig(config, prefDelegate,
					launch);

			// Starting debugging engine
			IProcess process = null;
			try {
				DebugEventHelper.fireExtendedEvent(newConfig,
						ExtendedDebugEventDetails.BEFORE_VM_STARTED);

				// Running
				monitor
						.subTask(InterpreterMessages.DebuggingEngineRunner_running);
				process = rawRun(launch, newConfig);
			} catch (CoreException e) {
				abort(InterpreterMessages.errDebuggingEngineNotStarted, e);
			}
			monitor.worked(4);

			// Waiting for debugging engine connect
			waitDebuggerConnected(process, launch, monitor);
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		} finally {
			monitor.done();
		}
		// Happy debugging :)
	}

	protected String[] renderCommandLine(InterpreterConfig config) {
		String exe = (String) config.getProperty(OVERRIDE_EXE);
		if (exe != null) {
			return config.renderCommandLine(getInstall().getEnvironment(), exe);
		}

		return config.renderCommandLine(getInstall());
	}

	/**
	 * Used to create new script thread configurator.
	 */
	protected IScriptDebugThreadConfigurator createThreadConfigurator() {
		return null;
	}

	/**
	 * Waiting debugging process to connect to current launch
	 * 
	 * @param debuggingProcess
	 *            process that will connect to current launch or null if handle
	 *            to process is not available (remote debugging)
	 * @param launch
	 *            launch to connect to
	 * @param monitor
	 *            progress monitor
	 * @throws CoreException
	 *             if debuggingProcess terminated, monitor is canceled or // *
	 *             timeout
	 */
	protected void waitDebuggerConnected(IProcess debuggingProcess,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		final int WAIT_CHUNK = 100;

		ILaunchConfiguration configuration = launch.getLaunchConfiguration();
		int timeout = configuration
				.getAttribute(
						ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_WAITING_TIMEOUT,
						DLTKDebugPlugin.getConnectionTimeout());

		ScriptDebugTarget target = (ScriptDebugTarget) launch.getDebugTarget();
		IScriptDebugThreadConfigurator configurator = this
				.createThreadConfigurator();
		if (configurator != null) {
			target.setScriptDebugThreadConfigurator(configurator);
		}
		target.setProcess(debuggingProcess);

		try {
			int all = 0;
			while (timeout == 0 || all < timeout) {
				if (target.isInitialized()
						|| target.isTerminated()
						|| monitor.isCanceled()
						|| (debuggingProcess != null && debuggingProcess
								.isTerminated()))
					break;

				Thread.sleep(WAIT_CHUNK);
				all += WAIT_CHUNK;
			}
		} catch (InterruptedException e) {
			Thread.interrupted();
		}

		if (!target.isInitialized()) {
			if (debuggingProcess != null && debuggingProcess.canTerminate()) {
				debuggingProcess.terminate();
			}
			abort(InterpreterMessages.errDebuggingEngineNotConnected, null);
		}
	}

	public String getDebugModelId() {
		return ScriptDebugManager.getInstance().getDebugModelByNature(
				getInstall().getNatureId());
	}

	public IDebuggingEngine getDebuggingEngine() {
		return DebuggingEngineManager.getInstance().getDebuggingEngine(
				getDebuggingEngineId());
	}

	protected String showGlobalVarsPreferenceKey() {
		return DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_GLOBAL;
	}

	protected String showClassVarsPreferenceKey() {
		return DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_CLASS;
	}

	protected String showLocalVarsPreferenceKey() {
		return DLTKDebugPreferenceConstants.PREF_DBGP_SHOW_SCOPE_LOCAL;
	}

	protected abstract String getDebuggingEngineId();

	protected PreferencesLookupDelegate createPreferencesLookupDelegate(
			ILaunch launch) throws CoreException {
		IScriptProject sProject = ScriptRuntime.getScriptProject(launch
				.getLaunchConfiguration());
		return new PreferencesLookupDelegate(sProject.getProject());
	}

	/**
	 * Returns the id of the plugin whose preference store contains general
	 * debugging preference settings.
	 */
	protected abstract String getDebugPreferenceQualifier();

	/**
	 * Returns the id of the plugin whose preference store contains debugging
	 * engine preferences.
	 */
	protected abstract String getDebuggingEnginePreferenceQualifier();

	/**
	 * Returns the preference key used to store the enable logging setting.
	 * 
	 * <p>
	 * Note: this preference controls logging for the actual debugging engine,
	 * and not the DBGP protocol output.
	 * </p>
	 */
	// protected abstract String getLoggingEnabledPreferenceKey();
	/**
	 * Returns the preference key used to store the log file path
	 */
	// protected abstract String getLogFilePathPreferenceKey();
	/**
	 * Returns the preference key usd to store the log file name
	 */
	protected abstract String getLogFileNamePreferenceKey();

	/**
	 * Returns true if debugging engine logging is enabled.
	 * 
	 * <p>
	 * Subclasses should use this method to determine of logging is enabled for
	 * the given debugging engine.
	 * </p>
	 */
	// protected boolean isLoggingEnabled(PreferencesLookupDelegate delegate) {
	// String key = getLoggingEnabledPreferenceKey();
	// String qualifier = getDebuggingEnginePreferenceQualifier();
	//
	// return delegate.getBoolean(qualifier, key);
	// }
	/**
	 * Returns a fully qualifed path to a log file name.
	 * 
	 * <p>
	 * If the user chose to use '{0}' in their file name, it will be replaced
	 * with the debugging session id.
	 * </p>
	 */
	protected String getLogFileName(PreferencesLookupDelegate delegate,
			String sessionId) {
		String qualifier = getDebuggingEnginePreferenceQualifier();
		String keyValue = delegate.getString(qualifier,
				getLogFileNamePreferenceKey());

		Map logFileNames = EnvironmentPathUtils.decodePaths(keyValue);
		IEnvironment env = getInstall().getEnvironment();
		String pathString = (String) logFileNames.get(env);
		if (pathString != null && pathString.length() > 0) {
			return pathString;
			// IPath path = new Path(pathString);
			// return PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(env,
			// path).toString();
		} else {
			return null;
		}
	}
}
