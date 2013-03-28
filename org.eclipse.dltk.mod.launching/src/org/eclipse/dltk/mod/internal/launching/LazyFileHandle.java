/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.launching;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.compiler.util.Util;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.mod.utils.PlatformFileUtils;

public class LazyFileHandle implements IFileHandle {
	private String environment = null;
	private IPath path = null;
	private IFileHandle handle = null;

	public LazyFileHandle(String environment, IPath path) {
		this.environment = environment;
		this.path = path;
	}

	private void initialize() {
		if (handle == null) {
			IEnvironment environment = EnvironmentManager
					.getEnvironmentById(this.environment);
			if (environment != null) {
				handle = PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(
						environment, this.path);
			}
			// Local environment for update from latest versions.
			else if (this.environment.equals(Util.EMPTY_STRING)) {
				handle = EnvironmentManager.getLocalEnvironment().getFile(
						this.path);
			}
		}
	}

	public boolean exists() {
		initialize();
		if (handle != null) {
			return this.handle.exists();
		}
		return false;
	}

	public String getCanonicalPath() {
		initialize();
		if (handle != null) {
			return this.handle.getCanonicalPath();
		}
		return null;
	}

	public IFileHandle getChild(String bundlePath) {
		initialize();
		if (handle != null) {
			return this.handle.getChild(bundlePath);
		}
		return null;
	}

	public IFileHandle[] getChildren() {
		initialize();
		if (handle != null) {
			return this.handle.getChildren();
		}
		return null;
	}

	public IEnvironment getEnvironment() {
		initialize();
		if (handle != null) {
			return this.handle.getEnvironment();
		}
		return null;
	}

	public IPath getFullPath() {
		initialize();
		if (handle != null) {
			return this.handle.getFullPath();
		}
		return null;
	}

	public String getName() {
		initialize();
		if (handle != null) {
			return this.handle.getName();
		}
		return null;
	}

	public IFileHandle getParent() {
		initialize();
		if (handle != null) {
			return this.handle.getParent();
		}
		return null;
	}

	public IPath getPath() {
		return this.path;
	}

	public boolean isDirectory() {
		initialize();
		if (handle != null) {
			return this.handle.isDirectory();
		}
		return false;
	}

	public boolean isFile() {
		initialize();
		if (handle != null) {
			return this.handle.isFile();
		}
		return false;
	}

	public boolean isSymlink() {
		initialize();
		if (handle != null) {
			return this.handle.isSymlink();
		}
		return false;
	}

	public long lastModified() {
		initialize();
		if (handle != null) {
			return this.handle.lastModified();
		}
		return 0;
	}

	public long length() {
		initialize();
		if (handle != null) {
			return this.handle.length();
		}
		return 0;
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		initialize();
		if (handle != null) {
			return this.handle.openInputStream(monitor);
		}
		return null;
	}

	public String toOSString() {
		IEnvironment environment = EnvironmentManager
				.getEnvironmentById(this.environment);
		if (environment != null) {
			IPath newPath = PlatformFileUtils
					.findAbsoluteOrEclipseRelativeFile(environment, this.path)
					.getPath();
			return environment.convertPathToString(newPath);
		}
		initialize();
		if (this.handle != null) {
			return this.handle.toOSString();
		}
		return null;
	}

	public URI toURI() {
		initialize();
		if (handle != null) {
			return this.handle.toURI();
		}
		return null;
	}

	public String getEnvironmentId() {
		if (this.environment.equals(Util.EMPTY_STRING)) {
			return LocalEnvironment.ENVIRONMENT_ID;
		}
		return this.environment;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof IFileHandle))
			return false;
		IFileHandle other = (IFileHandle) obj;
		if (environment == null) {
			if (other.getEnvironment() != null)
				return false;
		} else {
			final IEnvironment otherEnvironment = other.getEnvironment();
			if (otherEnvironment == null
					|| !environment.equals(otherEnvironment.getId()))
				return false;
		}
		if (path == null) {
			if (other.getPath() != null)
				return false;
		} else if (!path.equals(other.getPath()))
			return false;
		return true;
	}

	public String toString() {
		initialize();
		if (handle != null) {
			return this.handle.toString();
		}
		return "[UNRESOLVED FILE HANDLE]"; //$NON-NLS-1$
	}
}
