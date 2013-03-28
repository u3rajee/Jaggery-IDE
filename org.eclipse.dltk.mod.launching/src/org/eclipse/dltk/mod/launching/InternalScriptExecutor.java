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

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.mod.core.environment.IDeployment;
import org.eclipse.dltk.mod.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.launching.IScriptProcessHandler.ScriptResult;

/**
 * Utility class which may be used to execute a script, or perform an
 * interpreter action on a script, such as compilation.
 * 
 * @see IScriptProcessHandler
 */
public class InternalScriptExecutor {

	public interface IInternalScriptDeployer {
		/**
		 * Deploy the internal script to be executed.
		 */
		IPath deployScript(IDeployment deployment) throws IOException;
	}

	private IScriptProcessHandler handler;
	private IInterpreterInstall install;

	public InternalScriptExecutor(IInterpreterInstall install,
			IScriptProcessHandler handler) {
		Assert.isNotNull(install, Messages.InternalScriptExecutor_iInterpreterInstallMustNotBeNull);
		Assert.isNotNull(handler, Messages.InternalScriptExecutor_iProcessHandlerMustNotBeNull);

		this.install = install;
		this.handler = handler;
	}

	/**
	 * Execute a script or interpreter action
	 * 
	 * @param deployer
	 *            implementation of <code>IInternalScriptDeployer</code> to
	 *            deploy the script being executed.
	 * 
	 * @param interpreterArgs
	 *            command line arguments for the interpreter, may be
	 *            <code>null</code>
	 * 
	 * @param scriptArgs
	 *            command line arguments for the script, may be
	 *            <code>null</code>
	 * @param stdin
	 *            stdin to pass to script, may be <code>null</code>
	 * 
	 * @throws CoreException
	 *             if there was an error handling the process
	 * @throws IOException
	 *             if there was an error deploying the script
	 */
	public ScriptResult execute(IInternalScriptDeployer deployer,
			String[] interpreterArgs, String[] scriptArgs, char[] stdin)
			throws CoreException, IOException {
		IExecutionEnvironment execEnv = install.getExecEnvironment();

		IDeployment deployment = execEnv.createDeployment();
		IPath deploymentPath = deployer.deployScript(deployment);

		try {
			IFileHandle interpreter = install.getInstallLocation();
			IFileHandle script = deployment.getFile(deploymentPath);

			String[] cmdLine = buildCommandLine(interpreter, interpreterArgs,
					script, scriptArgs);

			Process process = execEnv.exec(cmdLine, null, null);
			ScriptResult result = handler.handle(process, stdin);

			return result;
		} finally {
			deployment.dispose();
		}
	}

	private void addArgs(ArrayList list, String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				list.add(args[i]);
			}
		}
	}

	private String[] buildCommandLine(IFileHandle interpreter,
			String[] interpreterArgs, IFileHandle script, String[] scriptArgs) {
		ArrayList cmdLine = new ArrayList();

		cmdLine.add(interpreter.getCanonicalPath());
		addArgs(cmdLine, interpreterArgs);

		cmdLine.add(script.getCanonicalPath());
		addArgs(cmdLine, scriptArgs);

		return (String[]) cmdLine.toArray(new String[cmdLine.size()]);
	}
}
