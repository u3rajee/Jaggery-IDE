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

import org.eclipse.core.runtime.CoreException;

public class SimpleClassNewInstanceDLTKExtensionManager extends
		SimpleDLTKExtensionManager {
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	public SimpleClassNewInstanceDLTKExtensionManager(String extension) {
		super(extension);
	}

	public Object createObject(ElementInfo info) throws CoreException {
		return info.getConfig().createExecutableExtension(CLASS_ATTR);
	}
}
