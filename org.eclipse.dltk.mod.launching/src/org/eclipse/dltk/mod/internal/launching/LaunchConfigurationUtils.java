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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.mod.launching.ScriptLaunchConfigurationConstants;

public final class LaunchConfigurationUtils {

	/*
	 * does a class like this exist elsewhere?
	 */

	public static interface ILaunchConfigDefaultStringProvider {
		String getDefault();
	}

	public static interface ILaunchConfigDefaultBooleanProvider {
		boolean getDefault();
	}

	/**
	 * Retrieve a boolean value from a launch configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * @param name
	 *            launch attribute name
	 * @param defaultValue
	 *            default value to use if attribute does not exist
	 * 
	 * @return boolean value
	 */
	public static boolean getBoolean(ILaunchConfiguration configuration,
			String name, boolean defaultValue) {
		boolean value = defaultValue;
		try {
			if (configuration != null)
				value = configuration.getAttribute(name, defaultValue);
		} catch (CoreException e) {
			DLTKLaunchingPlugin.log(e);
		}

		return value;
	}

	/**
	 * Returns the project associated with the launch configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * 
	 * @return project instance associated with the configuration, or
	 *         <code>null</code> if the project can not be found
	 */
	public static IProject getProject(ILaunchConfiguration configuration) {
		String projectName = getProjectName(configuration);

		IProject project = null;
		if (projectName != null) {
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(
					projectName);
		}

		return project;
	}

	/**
	 * Returns the project name associated with the launch configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * 
	 * @return project name or <code>null</code> if no project has been
	 *         associated
	 */
	public static String getProjectName(ILaunchConfiguration configuration) {
		String projectName = null;
		try {
			projectName = configuration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					(String) null);
		} catch (CoreException e) {
			DLTKLaunchingPlugin.log(e);
		}

		return projectName;
	}

	/**
	 * Returns the 'break on first line' setting for the specified launch
	 * configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * 
	 * @return <code>true</code> if the option is enabled, <code>false</code>
	 *         otherwise
	 */
	public static boolean isBreakOnFirstLineEnabled(
			ILaunchConfiguration configuration, boolean defaultValue) {
		return getBoolean(configuration,
				ScriptLaunchConfigurationConstants.ENABLE_BREAK_ON_FIRST_LINE,
				defaultValue);
	}

	/**
	 * Returns the 'Dbgp logging enabled' setting for the specified launch
	 * configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * 
	 * @return <code>true</code> if the option is enabled, <code>false</code>
	 *         otherwise
	 */
	public static boolean isDbgpLoggingEnabled(
			ILaunchConfiguration configuration) {
		return isDbgpLoggingEnabled(configuration, false);
	}

	public static boolean isDbgpLoggingEnabled(
			ILaunchConfiguration configuration, boolean defaultValue) {
		return getBoolean(configuration,
				ScriptLaunchConfigurationConstants.ENABLE_DBGP_LOGGING,
				defaultValue);
	}

	/**
	 * Retrieve a string value from a launch configuration
	 * 
	 * @param configuration
	 *            launch configuration
	 * @param name
	 *            launch attribute name
	 * @param defaultValue
	 *            default value to use if attribute does not exist
	 * 
	 * @return String
	 */
	public static String getString(ILaunchConfiguration configuration,
			String name, String defaultValue) {
		String value = defaultValue;
		try {
			if (configuration != null)
				value = configuration.getAttribute(name, defaultValue);
		} catch (CoreException e) {
			DLTKLaunchingPlugin.log(e);
		}

		return value;
	}
}
