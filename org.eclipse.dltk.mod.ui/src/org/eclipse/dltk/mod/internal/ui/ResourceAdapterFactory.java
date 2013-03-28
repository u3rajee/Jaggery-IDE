/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.ui.part.FileEditorInput;

public class ResourceAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IModelElement.class };

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

	public Object getAdapter(Object element, Class key) {
		if (IModelElement.class.equals(key)) {

			// Performance optimization, see https://bugs.eclipse.org/bugs/
			// show_bug.cgi?id=133141
			if (element instanceof IFile) {
				IModelElement je = DLTKUIPlugin.getDefault()
						.getWorkingCopyManager().getWorkingCopy(
								new FileEditorInput((IFile) element));
				if (je != null && je.exists()) {
					return je;
				}
			}

			return DLTKCore.create((IResource) element);
		}
		return null;
	}
}
