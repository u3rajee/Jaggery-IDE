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

import org.eclipse.core.resources.IProject;

public interface IEnvironmentProvider {
	public IEnvironment[] getEnvironments();

	public IEnvironment getEnvironment(String envId);

	/**
	 * Wait until provider are initialzed
	 */
	public void waitInitialized();

	IEnvironment getProjectEnvironment(IProject project);
}
