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
package org.eclipse.dltk.mod.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.mod.core.PriorityDLTKExtensionManager;
import org.eclipse.dltk.mod.ui.actions.IActionFilterTester;

class IdBasedExtensionManager extends PriorityDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	public IdBasedExtensionManager(String extension) {
		super(extension, "id"); //$NON-NLS-1$
	}

	public IActionFilterTester getObject(String id) throws CoreException {
		ElementInfo ext = this.getElementInfo(id);

		return (IActionFilterTester) getInitObject(ext);
	}

	public Object getInitObject(ElementInfo ext) throws CoreException {
		if (ext != null) {
			if (ext.object != null) {
				return ext.object;
			}

			IConfigurationElement cfg = (IConfigurationElement) ext.getConfig();
			Object object = createObject(cfg);
			ext.object = object;
			return object;
		}
		return null;
	}

	protected Object createObject(IConfigurationElement cfg)
			throws CoreException {
		return cfg.createExecutableExtension(CLASS_ATTR);
	}
}