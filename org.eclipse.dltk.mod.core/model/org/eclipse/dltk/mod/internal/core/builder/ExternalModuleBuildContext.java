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

import org.eclipse.dltk.mod.compiler.problem.IProblem;
import org.eclipse.dltk.mod.compiler.problem.IProblemReporter;
import org.eclipse.dltk.mod.compiler.task.ITaskReporter;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

public class ExternalModuleBuildContext extends AbstractBuildContext implements
		IProblemReporter, ITaskReporter {

	/**
	 * @param module
	 */
	protected ExternalModuleBuildContext(ISourceModule module) {
		super(module);
	}

	/*
	 * @see org.eclipse.dltk.mod.core.builder.IBuildContext#getFileHandle()
	 */
	public IFileHandle getFileHandle() {
		// TODO test!!
		return EnvironmentPathUtils.getFile(module.getPath());
	}

	public IProblemReporter getProblemReporter() {
		return this;
	}

	public ITaskReporter getTaskReporter() {
		return this;
	}

	public void reportTask(String message, int lineNumber, int priority,
			int charStart, int charEnd) {
		// NOP
	}

	public void reportProblem(IProblem problem) {
		// NOP
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
