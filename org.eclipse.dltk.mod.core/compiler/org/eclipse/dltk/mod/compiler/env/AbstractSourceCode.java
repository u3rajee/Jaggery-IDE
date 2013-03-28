/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.compiler.env;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.IModelElement;

public abstract class AbstractSourceCode implements ISourceModule {

	/*
	 * @see org.eclipse.dltk.mod.compiler.env.ISourceModule#getModelElement()
	 */
	public IModelElement getModelElement() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.mod.compiler.env.ISourceModule#getScriptFolder()
	 */
	public IPath getScriptFolder() {
		return Path.EMPTY;
	}

	/*
	 * @see org.eclipse.dltk.mod.compiler.env.IDependent#getFileName()
	 */
	public char[] getFileName() {
		return CharOperation.NO_CHAR;
	}

}
