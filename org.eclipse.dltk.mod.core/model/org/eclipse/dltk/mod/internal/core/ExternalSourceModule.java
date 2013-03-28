/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IModelStatusConstants;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.WorkingCopyOwner;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

/**
 * Represents an external source module.
 */
public class ExternalSourceModule extends AbstractExternalSourceModule {

	private IStorage storage;

	public ExternalSourceModule(ScriptFolder parent, String name,
			WorkingCopyOwner owner, IStorage storage) {
		this(parent, name, owner, true, storage);
	}

	public ExternalSourceModule(ScriptFolder parent, String name,
			WorkingCopyOwner owner, boolean readOnly, IStorage storage) {
		super(parent, name, owner, readOnly);
		this.storage = storage;
	}

	/*
	 * @see org.eclipse.dltk.mod.internal.core.AbstractSourceModule#equals(java.lang.
	 *      Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ExternalSourceModule)) {
			return false;
		}

		return super.equals(obj);
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getContents()
	 */
	public InputStream getContents() throws CoreException {
		return storage.getContents();
	}

	/*
	 * @see org.eclipse.dltk.mod.compiler.env.IDependent#getFileName()
	 */
	public char[] getFileName() {
		return getPath().toOSString().toCharArray();
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getFullPath()
	 */
	public IPath getFullPath() {
		if (this.storage != null) {
			return storage.getFullPath();
		} else {
			return getPath();
		}
	}

	/*
	 * @see org.eclipse.core.resources.IStorage#getName()
	 */
	public String getName() {
		return storage.getName();
	}

	public IResource getResource() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.mod.internal.core.AbstractSourceModule#getBufferContent()
	 */
	protected char[] getBufferContent() throws ModelException {
		IFileHandle file = EnvironmentPathUtils.getFile(getPath());
		InputStream stream = null;

		try {
			try {
				if (file != null && file.exists()) {
					stream = new BufferedInputStream(file.openInputStream(null));
				} else {
					// This is an archive entry
					boolean inProjectArchive = false;
					ProjectFragment projectFragment = this.getProjectFragment();
					if (projectFragment.isArchive()) {
						if (projectFragment.getResource() != null) {
							inProjectArchive = projectFragment.getResource()
									.exists();
						}
					}
					if (!inProjectArchive) {
						throw newNotPresentException();
					}
					stream = new BufferedInputStream(storage.getContents());
				}

			} catch (IOException e) {
				throw new ModelException(e,
						IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
			} catch (CoreException e) {
				throw new ModelException(e,
						IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
			}

			char[] content;
			content = org.eclipse.dltk.mod.compiler.util.Util
					.getInputStreamAsCharArray(stream, -1, "utf-8"); //$NON-NLS-1$
			return content;
		} catch (IOException e) {
			throw new ModelException(e, IModelStatusConstants.IO_EXCEPTION);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}

	}

	/*
	 * @see org.eclipse.dltk.mod.internal.core.AbstractExternalSourceModule#getModuleType ()
	 */
	protected String getModuleType() {
		return "DLTK External Source Moule: "; //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.dltk.mod.internal.core.AbstractSourceModule#getNatureId()
	 */
	protected String getNatureId() throws CoreException {
		IPath path = getFullPath();
		IDLTKLanguageToolkit toolkit = lookupLanguageToolkit(path);
		return (toolkit != null) ? toolkit.getNatureId() : null;
	}

	/*
	 * @see org.eclipse.dltk.mod.internal.core.AbstractSourceModule#getOriginalSourceModule ()
	 */
	protected ISourceModule getOriginalSourceModule() {
		return new ExternalSourceModule((ScriptFolder) getParent(),
				getElementName(), DefaultWorkingCopyOwner.PRIMARY, storage);
	}
}
