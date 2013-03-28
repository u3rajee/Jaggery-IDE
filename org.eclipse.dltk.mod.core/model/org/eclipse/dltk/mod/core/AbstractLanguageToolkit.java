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
package org.eclipse.dltk.mod.core;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

public abstract class AbstractLanguageToolkit implements IDLTKLanguageToolkit {
	public AbstractLanguageToolkit() {
	}

	public boolean languageSupportZIPBuildpath() {
		return false;
	}

	public boolean validateSourcePackage(IPath path, IEnvironment environment) {
		return true;
	}

	public IStatus validateSourceModule(IResource resource) {
		return Status.OK_STATUS;
	}

	protected static boolean isEmptyExtension(String name) {
		return name.indexOf('.') == -1;
	}

	public boolean canValidateContent(IResource resource) {
		final IProject project = resource.getProject();
		if (project == null) { // This is workspace root.
			return false;
		}
		final IEnvironment environment = EnvironmentManager
				.getEnvironment(project);
		if (environment == null || !environment.isLocal()) {
			return false;
		}
		return isEmptyExtension(resource.getName());
	}

	public boolean canValidateContent(File file) {
		return isEmptyExtension(file.getName());
	}

	public boolean canValidateContent(IFileHandle file) {
		return false;
	}

	public String getPreferenceQualifier() {
		return null;
	}

}
