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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;

/**
 * This class should register to deployment manager for correct undeployment actions.
 */
public interface IDeployment {
	/**
	 * Extract and deploy specific folder or file from plugin bundle to environment file system.
	 */
	IPath add(Bundle bundle, String bundlePath) throws IOException;
	/**
	 * Deploy specific stream as file into environment file system.
	 */
	IPath add(InputStream stream, String filename) throws IOException;
	
	/**
	 * Make specific folders
	 * @param path
	 */
	void mkdirs(IPath path);
	
	/**
	 * Undeploy deployment and unregister it from deployment manager
	 */
	void dispose();
	IFileHandle getFile(IPath deploymentPath);
	IPath getAbsolutePath();
}
