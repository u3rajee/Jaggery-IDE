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
/**
 * 
 */
package org.eclipse.dltk.mod.ui.browsing.ext;

import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

class ExtendedClasesLabelProvider implements ILabelProvider {
	/**
	 * 
	 */
	private final ILabelProvider labelProvider;

	/**
	 * @param extendedClassesView
	 */
	ExtendedClasesLabelProvider(ILabelProvider extendedClassesView) {
		labelProvider = extendedClassesView;
	}

	public Image getImage(Object element) {
		if (element instanceof MixedClass) {
			MixedClass cl = (MixedClass) element;
			if (cl.getElements().size() > 0) {
				return labelProvider.getImage(cl.getElements().get(0));
			}
		}
		return labelProvider.getImage(element);
	}

	public String getText(Object element) {
		if (element instanceof IModelElement) {
			return ((IModelElement) element).getElementName();
		} else if (element instanceof MixedClass) {
			MixedClass cl = (MixedClass) element;
			// if (cl.getElements().size() > 1) {
			// return cl.getName() + "("
			// + Integer.toString(cl.getElements().size()) + ")";
			// } else {
			return cl.getName();
			// }
		} else if (element != null) {
			return element.toString();
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}
}