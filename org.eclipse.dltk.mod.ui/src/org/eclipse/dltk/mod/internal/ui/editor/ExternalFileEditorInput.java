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
package org.eclipse.dltk.mod.internal.ui.editor;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.sourcelookup.containers.LocalFileStorage;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.internal.ui.JarEntryFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * EditorInput for external files. Copied from
 * org.eclipse.ui.internal.editors.text.JavaFileEditorInput
 */
public class ExternalFileEditorInput implements IEditorInput,
		IStorageEditorInput {

	/**
	 * The workbench adapter which simply provides the label.
	 * 
	 * @see Eclipse 3.1
	 */
	private class WorkbenchAdapter implements IWorkbenchAdapter {
		/*
		 * @see
		 * org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object o) {
			return null;
		}

		/*
		 * @see
		 * org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang
		 * .Object)
		 */
		public ImageDescriptor getImageDescriptor(Object object) {
			return null;
		}

		/*
		 * @see
		 * org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
		 */
		public String getLabel(Object o) {
			return ((ExternalFileEditorInput) o).getName();
		}

		/*
		 * @see
		 * org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
		 */
		public Object getParent(Object o) {
			return null;
		}
	}

	public File getFile() {
		if (fFile != null) {
			return fFile;
		} else if (fAdvisor != null) {
			fFile = fAdvisor.getFile(fElement);
		}
		return fFile;
	}

	private File fFile;
	private static IModelElementFileAdivsor fAdvisor;
	private IModelElement fElement;
	private WorkbenchAdapter fWorkbenchAdapter = new WorkbenchAdapter();
	private IStorage m_storage;

	public ExternalFileEditorInput(File file) {
		super();
		fFile = file;
		fWorkbenchAdapter = new WorkbenchAdapter();
	}

	public ExternalFileEditorInput(IModelElement element) {
		super();
		fElement = element;
		fWorkbenchAdapter = new WorkbenchAdapter();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return true;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		if (fFile != null) {
			return fFile.getName();
		} else if (fElement != null) {
			return fElement.getElementName();
		} else if (fAdvisor != null) {
			fFile = fAdvisor.getFile(fElement);
		}
		if (fFile != null) {
			return fFile.getName();
		} else {
			return "";
		}
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return getFile().getAbsolutePath();
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter))
			return null;
		if (IWorkbenchAdapter.class.equals(adapter))
			return fWorkbenchAdapter;

		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	// /*
	// * @see
	// org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	// */
	// public IPath getPath(Object element) {
	// if (element instanceof ExternalFileEditorInput) {
	// ExternalFileEditorInput input = (ExternalFileEditorInput) element;
	// return Path.fromOSString(input.getFile().getAbsolutePath());
	// }
	// return null;
	// }

	// /*
	// * @see org.eclipse.ui.IPathEditorInput#getPath()
	// * @since 3.1
	// */
	// public IPath getPath() {
	// return Path.fromOSString(getFile().getAbsolutePath());
	// }

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (o instanceof ExternalFileEditorInput) {
			ExternalFileEditorInput input = (ExternalFileEditorInput) o;
			return getFile().equals(input.getFile());
		}

		if (o instanceof IPathEditorInput) {
			IPathEditorInput input = (IPathEditorInput) o;

			// fix bug 4984. if file not exist, return false
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file != null && !file.exists())
				return false;

			// return getPath().equals(input.getPath());
		}

		return false;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return getFile().hashCode();
	}

	public static void registryAdvisor(IModelElementFileAdivsor advisor) {
		fAdvisor = advisor;
	}

	public IModelElement getModelElement() {
		return fElement;
	}

	// public URI getURI(Object element) {
	// // TODO Auto-generated method stub
	// return getURI();
	// }

	// public URI getURI() {
	// File file = getFile();
	// if (file != null) {
	// if (file.exists()) {
	// return file.toURI();
	// } else {
	// String path = file.getPath();
	// try {
	// return new URI("jar:file:/" + path.replace("\\", "/"));
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	// return null;
	// }

	/*
	 * see IStorageEditorInput#getStorage()
	 */
	public IStorage getStorage() {
		if (m_storage == null) {
			m_storage = calculateStorage();
		}
		return m_storage;
	}

	private IStorage calculateStorage() {
		File file = getFile();
		if (file != null) {
			if (file.exists()) {
				return new LocalFileStorage(file);
			} else {
				String path = file.getPath();
				if (path.indexOf("!") > -1) {
					String[] strs = path.split("!");
					if (strs.length == 2) {
						try {
							File jarFile = new File(strs[0]);
							ZipFile zipFile = new ZipFile(jarFile);
							final String typeName = strs[1].replace("\\", "/");
							// Ensure the schema file exists in the jar archive
							return new JarEntryFile(zipFile, typeName) {
								public String getName() {
									return typeName;
								}
							};
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return null;
	}
}
