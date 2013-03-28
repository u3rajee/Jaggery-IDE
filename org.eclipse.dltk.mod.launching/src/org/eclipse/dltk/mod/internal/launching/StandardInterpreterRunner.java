/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.launching;

import org.eclipse.dltk.mod.launching.AbstractInterpreterRunner;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;

public class StandardInterpreterRunner extends AbstractInterpreterRunner {
	public StandardInterpreterRunner(IInterpreterInstall install) {
		super(install);
	}

	protected String getPluginId() {
		return DLTKLaunchingPlugin.getUniqueIdentifier();
	}
}
