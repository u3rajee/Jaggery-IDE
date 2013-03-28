/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.dltk.mod.core.IModelStatusConstants;
import org.eclipse.dltk.mod.core.ModelException;

public class ArchiveEntryFile extends PlatformObject implements IStorage {
	private String entryName;
	private String zipName;
	private IPath path;
	private IResource zipResource;

	public ArchiveEntryFile(String entryName, String zipName,
			IPath parentRelativePath, IResource zipResource) {
		this.entryName = entryName;
		this.zipName = zipName;
		this.path = parentRelativePath;
		this.zipResource = zipResource;
	}

	// eBay mod, get return zip name
	public ZipFile getZipFile() throws CoreException, IOException {
		ZipFile zipFile;
		if (zipResource == null) {
			// eBay mod, to get external jar correctly
			zipFile = ModelManager.getModelManager().getZipFile(
					new Path(this.zipName));
			// eBay mod
		} else {
			zipFile = new ZipFile(this.zipResource.getLocation().toOSString());
		}
		return zipFile;
	}

	// eBay mod

	public InputStream getContents() throws CoreException {
		try {
			if (ModelManager.ZIP_ACCESS_VERBOSE) {
				System.out
						.println("(" + Thread.currentThread() + ") [JarEntryFile.getContents()] Creating ZipFile on " + this.zipName); //$NON-NLS-1$	//$NON-NLS-2$
			}
			ZipFile zipFile = null;
			try {
				zipFile = getZipFile();
				ZipEntry zipEntry = zipFile.getEntry(this.path.append(
						this.entryName).toString());
				if (zipEntry == null) {
					throw new ModelException(new ModelStatus(
							IModelStatusConstants.INVALID_PATH, this.entryName));
				}

				InputStream inputStream = zipFile.getInputStream(zipEntry);

				byte[] buf = new byte[1024];
				int len;
				ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
				while ((len = inputStream.read(buf)) > 0) {
					arrayOut.write(buf, 0, len);
				}
				return new ByteArrayInputStream(arrayOut.toByteArray());
			} finally {
				zipFile.close();
			}
		} catch (IOException e) {
			throw new ModelException(e, IModelStatusConstants.IO_EXCEPTION);
		}
	}

	/**
	 * @see IStorage#getFullPath
	 */
	public IPath getFullPath() {
		return new Path(this.entryName);
	}

	/**
	 * @see IStorage#getName
	 */
	public String getName() {
		return new Path(this.entryName).lastSegment();
		// return this.path.lastSegment();
	}

	/**
	 * @see IStorage#isReadOnly()
	 */
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * @see IStorage#isReadOnly()
	 */
	public String toString() {
		return "JarEntryFile[" + this.zipName + "::" + this.entryName + "]"; //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-1$
	}
}
