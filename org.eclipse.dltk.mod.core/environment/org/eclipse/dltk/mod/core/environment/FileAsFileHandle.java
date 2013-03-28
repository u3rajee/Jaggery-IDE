/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.core.environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.DLTKCore;

/**
 * {@link File} as {@link IFileHandle} wrapper
 */
public class FileAsFileHandle implements IFileHandle {

	private final IEnvironment environment;
	private final File file;

	public FileAsFileHandle(IEnvironment environment, File file) {
		this.environment = environment;
		this.file = file;
	}

	public boolean exists() {
		return file.exists();
	}

	public String getCanonicalPath() {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return file.getPath();
		}
	}

	public IFileHandle getChild(String path) {
		return new FileAsFileHandle(environment, new File(file, path));
	}

	public IFileHandle[] getChildren() {
		final File[] children = file.listFiles();
		if (children != null) {
			IFileHandle[] result = new IFileHandle[children.length];
			for (int i = 0; i < children.length; ++i) {
				result[i] = new FileAsFileHandle(environment, children[i]);
			}
			return result;
		} else {
			return null;
		}
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public String getEnvironmentId() {
		return environment.getId();
	}

	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}

	public String getName() {
		return file.getName();
	}

	public IFileHandle getParent() {
		final File parentFile = file.getParentFile();
		if (parentFile != null) {
			return new FileAsFileHandle(environment, parentFile);
		} else {
			return null;
		}
	}

	public IPath getPath() {
		return new Path(file.getPath());
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public boolean isFile() {
		return file.isFile();
	}

	public boolean isSymlink() {
		return EFS.getLocalFileSystem().getStore(file.toURI()).fetchInfo()
				.getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	public long lastModified() {
		return file.lastModified();
	}

	public long length() {
		return file.length();
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		return new FileInputStream(file);
	}

	public String toOSString() {
		return file.getPath();
	}

	public URI toURI() {
		return file.toURI();
	}

}
