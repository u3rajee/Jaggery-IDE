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
package org.eclipse.dltk.mod.core;

import org.eclipse.core.resources.IStorage;
import org.eclipse.dltk.mod.internal.core.ExternalSourceModule;
import org.eclipse.dltk.mod.internal.core.ScriptFolder;

/**
 * @author MPeleshchyshyn
 * 
 */
public interface ISourceModuleFactory {
	public ISourceModule createSourceModule(ScriptFolder parent, String name,
			WorkingCopyOwner owner);

	public ExternalSourceModule createExternalSourceModule(ScriptFolder parent,
			String name, WorkingCopyOwner owner, boolean readOnly,
			IStorage storage);
}
