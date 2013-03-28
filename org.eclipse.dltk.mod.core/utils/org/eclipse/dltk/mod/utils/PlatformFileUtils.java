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
package org.eclipse.dltk.mod.utils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.core.internal.environment.LocalEnvironment;
import org.eclipse.osgi.service.datalocation.Location;

public class PlatformFileUtils {
	public static IFileHandle findAbsoluteOrEclipseRelativeFile(
			IEnvironment env, IPath path) {
		if (path == null || path.isEmpty()) {
			throw new InvalidParameterException(Messages.PlatformFileUtils_pathMustNotBeEmpty);
		}
		IFileHandle file = env.getFile(path);
		if (!env.getId().equals(LocalEnvironment.ENVIRONMENT_ID)) {
			return file;
		}
		if (EnvironmentManager.isLocal(env) && !file.exists()
				&& !path.isAbsolute()) {
			String loc;
			Location location = Platform.getInstallLocation();
			if (location != null) {
				try {
					loc = FileLocator.resolve(location.getURL()).getPath();
					IFileHandle nfile = env.getFile(new Path(loc
							+ env.getSeparator() + path.toOSString()));
					if (nfile.exists()) {
						return nfile;
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}

			location = Platform.getInstanceLocation();
			if (location != null) {
				try {
					loc = FileLocator.resolve(location.getURL()).getPath();
					IFileHandle nfile = env.getFile(new Path(loc
							+ env.getSeparator() + path.toOSString()));
					if (nfile.exists()) {
						return nfile;
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}

		}
		return file;
	}

	public static File findAbsoluteOrEclipseRelativeFile(File file) {
		return file;
	}
}
