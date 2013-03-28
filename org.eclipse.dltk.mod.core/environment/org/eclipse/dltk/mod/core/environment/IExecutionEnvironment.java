/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.core.environment;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public interface IExecutionEnvironment {
	/**
	 * If realyNeed are set to true then values should be returned in any case.
	 * if realyNeed are set to false then values could be returned onle if
	 * platform require override of environment each time.
	 * 
	 * Local environment will return environment each time. RSE environment will
	 * return environment only if realYneed is true.
	 */
	Map getEnvironmentVariables(boolean realyNeed);

	IDeployment createDeployment();

	Process exec(String[] cmdLine, IPath workingDir, String[] environment)
			throws CoreException;

	IEnvironment getEnvironment();

	boolean isValidExecutableAndEquals(String name, IPath fName);
}
