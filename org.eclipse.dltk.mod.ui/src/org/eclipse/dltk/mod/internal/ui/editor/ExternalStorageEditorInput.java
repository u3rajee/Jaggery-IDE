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
package org.eclipse.dltk.mod.internal.ui.editor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.internal.core.BuiltinSourceModule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;

// EBAY - START MOD
public class ExternalStorageEditorInput extends PlatformObject implements
		IEditorInput, IStorageEditorInput, ILocationProvider {
	private String m_name;
	// EBAY - END MOD
	private IStorage fStorage;

	public ExternalStorageEditorInput(IStorage storage) {
		this.fStorage = storage;
	}

	public boolean exists() {
		return fStorage != null;
	}

	public ImageDescriptor getImageDescriptor() {
		return PlatformUI.getWorkbench().getEditorRegistry()
				.getImageDescriptor(this.fStorage.getName());
	}

	public String getName() {
		// EBAY MOD START
		if (m_name == null && fStorage != null) {
			m_name = fStorage.getName();
		}
		return m_name;
		// EBAY MOD END
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		IPath path = fStorage.getFullPath();
		if (path == null) {
			return ""; //$NON-NLS-1$
		}
		if (fStorage instanceof IModelElement) {
			final IEnvironment environment = EnvironmentManager
					.getEnvironment((IModelElement) fStorage);
			if (environment != null) {
				return environment.convertPathToString(path);
			}
		}

		return path.toOSString();
	}

	public Object getAdapter(Class adapter) {
		// EBAY - START MOD
		if (adapter == null) {
			return null;
		}
		if (!(fStorage instanceof BuiltinSourceModule)
				&& (adapter == this.getClass() || adapter == ILocationProvider.class)) {
			return this;
		}
		if (adapter.isAssignableFrom(fStorage.getClass())) {
			return fStorage;
		}
		// EBAY - END MOD
		// if (!(fStorage instanceof BuiltinSourceModule)
		// && (adapter == this.getClass() || adapter ==
		// ILocationProvider.class)) {
		// return this;
		// }
		if (adapter == IModelElement.class && fStorage instanceof IModelElement) {
			return fStorage;
		}
		// EBAY MOD START
		return super.getAdapter(adapter);
		// EBAY MOD END
	}

	public IStorage getStorage() {
		return this.fStorage;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ExternalStorageEditorInput)) {
			return false;
		}
		ExternalStorageEditorInput other = (ExternalStorageEditorInput) obj;
		// EBAY MOD START
		boolean flag = fStorage.equals(other.fStorage);
		if (flag) {
			return true;
		}
		IPath fullPath = fStorage.getFullPath();
		if (fullPath != null) {
			return fullPath.equals(other.fStorage.getFullPath());
		}
		return false;
		// EBAY MOD END
	}

	/*
	 * (non-Javadoc) Method declared on Object.
	 */
	public int hashCode() {
		return fStorage.hashCode();
	}

	public IPath getPath(Object element) {
		return fStorage.getFullPath();
	}
}
