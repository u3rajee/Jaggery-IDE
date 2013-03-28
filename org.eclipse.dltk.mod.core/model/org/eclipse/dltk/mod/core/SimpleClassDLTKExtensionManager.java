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
package org.eclipse.dltk.mod.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

public class SimpleClassDLTKExtensionManager extends SimpleDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$
	
	public SimpleClassDLTKExtensionManager(String extension) {
		super(extension);
	}
	
	public Object[] getObjects() {
		ElementInfo[] infos = this.getElementInfos();
		List objs = new ArrayList();
		for (int i = 0; i < infos.length; i++) {
			Object o = getInitObject(infos[i]);
			if( o != null ) {
				objs.add(o);
			}
		}
		return objs.toArray(new Object[objs.size()]);
	}
	public Object getInitObject(ElementInfo ext) {
		try {
			if (ext != null) {
				if (ext.object != null) {
					return ext.object;
				}

				IConfigurationElement cfg = (IConfigurationElement) ext.config;
				Object object = createObject(cfg);
				ext.object = object;
				return object;
			}
		} catch (CoreException e) {
			if( DLTKCore.DEBUG ) {
				e.printStackTrace();
			}
		}
		return null;
	}

	protected Object createObject(IConfigurationElement cfg)
			throws CoreException {
		return cfg.createExecutableExtension(CLASS_ATTR);
	}
}
