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
package org.eclipse.dltk.mod.core.internal.environment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

public class EFSFileHandle implements IFileHandle {
	private IFileStore file;
	private IEnvironment environment;

	public EFSFileHandle(IEnvironment env, IFileStore file) {
		this.environment = env;
		this.file = file;
	}

	public boolean exists() {
		try {
			return file.fetchInfo().exists();
		} catch (RuntimeException e) {
			return false;
		}
	}

	public String toOSString() {
		return this.environment.convertPathToString(getPath());
	}

	public String getCanonicalPath() {
		return this.environment.getCanonicalPath(getPath());
	}

	public IFileHandle getChild(final String childname) {
		return new EFSFileHandle(environment, file
				.getChild(new Path(childname)));
	}

	public IFileHandle[] getChildren() {
		try {
			IFileStore[] files = file.childStores(EFS.NONE, null);
			IFileHandle[] children = new IFileHandle[files.length];
			for (int i = 0; i < files.length; i++)
				children[i] = new EFSFileHandle(environment, files[i]);
			return children;
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			return null;
		}
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public URI toURI() {
		return file.toURI();
	}

	public String getName() {
		return file.getName();
	}

	public IFileHandle getParent() {
		IFileStore parent = file.getParent();
		if (parent == null)
			return null;
		return new EFSFileHandle(environment, parent);
	}

	public IPath getPath() {
		return new Path(file.toURI().getPath());
	}

	public boolean isDirectory() {
		return file.fetchInfo().isDirectory();
	}

	public boolean isFile() {
		final IFileInfo info = file.fetchInfo();
		return info.exists() && !info.isDirectory();
	}

	public boolean isSymlink() {
		return file.fetchInfo().getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		try {
			return file.openInputStream(EFS.NONE, monitor);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof EFSFileHandle) {
			EFSFileHandle anotherFile = (EFSFileHandle) obj;
			return this.file.equals(anotherFile.file);
		}
		return false;
	}

	public int hashCode() {
		return file.hashCode();
	}

	public String toString() {
		return toOSString();
	}

	public long lastModified() {
		return file.fetchInfo().getLastModified();
	}

	public long length() {
		return file.fetchInfo().getLength();
	}

	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}

	public String getEnvironmentId() {
		return environment.getId();
	}
}
