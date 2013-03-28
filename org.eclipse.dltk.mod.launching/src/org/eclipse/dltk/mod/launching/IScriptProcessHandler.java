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

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * Represents a handler that can be used to read/write data to/from a
 * <code>Process</code> object that has been created to execute an internal 
 * script.
 * 
 * @see InternalScriptExecutor
 */
public interface IScriptProcessHandler {

	/**
	 * Handles the <code>Process</code> object
	 * 
	 * @param process
	 *            process
	 * 
	 * @param stdin
	 *            data that should be fed to the script via stdin, may be
	 *            <code>null</code>.
	 * 
	 * @return object containing the results of the script execution
	 * 
	 * @throws CoreException
	 *             if there was a error handling the process
	 */
	ScriptResult handle(Process process, char[] stdin) throws CoreException;

	/**
	 * Simple value object to return the results of an internal script execution
	 */
	public static class ScriptResult {
		/** script exit code */
		public int exitValue;

		/** stderr of script as a single string */
		public String stderr;

		/** stderr split into individual lines */
		public List stderrLines;

		/** stdout of script as a single string */
		public String stdout;

		/** stdout split into individual lines */
		public List stdoutLines;
	}
}