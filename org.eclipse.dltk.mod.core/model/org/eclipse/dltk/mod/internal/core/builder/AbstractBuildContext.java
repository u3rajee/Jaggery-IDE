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
package org.eclipse.dltk.mod.internal.core.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.builder.IBuildContext;
import org.eclipse.dltk.mod.core.builder.ISourceLineTracker;
import org.eclipse.dltk.mod.utils.TextUtils;

public abstract class AbstractBuildContext implements IBuildContext {

	private final Map attributes = new HashMap();

	public Object get(String attribute) {
		return attributes.get(attribute);
	}

	public void set(String attribute, Object value) {
		if (value == null) {
			attributes.remove(attribute);
		} else {
			attributes.put(attribute, value);
		}
	}

	protected final ISourceModule module;

	/**
	 * @param module
	 */
	protected AbstractBuildContext(ISourceModule module) {
		this.module = module;
	}

	private char[] contents;

	public final char[] getContents() {
		if (contents == null) {
			try {
				contents = module.getSourceAsCharArray();
			} catch (ModelException e) {
				DLTKCore.error("Error retrieving contents of "
						+ module.getElementName(), e);
				contents = CharOperation.NO_CHAR;
			}
		}
		return contents;
	}

	private ISourceLineTracker lineTracker = null;

	public ISourceLineTracker getLineTracker() {
		if (lineTracker == null) {
			lineTracker = TextUtils.createLineTracker(getContents());
		}
		return lineTracker;
	}

	public final ISourceModule getSourceModule() {
		return module;
	}

	/*
	 * @see org.eclipse.dltk.mod.core.builder.IBuildContext#getFile()
	 */
	public final IFile getFile() {
		return (IFile) module.getResource();
	}

}
