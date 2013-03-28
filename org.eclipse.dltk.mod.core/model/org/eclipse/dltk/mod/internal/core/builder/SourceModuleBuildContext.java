/*******************************************************************************
 * Copyright (c) 2005, 2012 xored software, Inc, and eBay Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.builder;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.mod.compiler.problem.IProblemReporter;
import org.eclipse.dltk.mod.compiler.task.ITaskReporter;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IBuildProblemReporterFactory;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

public class SourceModuleBuildContext extends AbstractBuildContext {

	// EBAY - START MOD
	// final BuildProblemReporter reporter;
	BuildProblemReporter reporter = null;

	// EBAY -- STOP MOD

	/**
	 * @param module
	 */
	public SourceModuleBuildContext(ISourceModule module) {
		super(module);
		final IResource resource = module.getResource();

		// EBAY - START MOD
		if (resource != null) {

			// get corresponding toolkit
			IDLTKLanguageToolkit toolkit = DLTKLanguageManager
					.findToolkitForResource(resource);

			if (toolkit != null) {

				// get nature for resource
				String natureId = toolkit.getNatureId();
				IBuildProblemReporterFactory factory = DLTKLanguageManager
						.getBuildProblemReporterFactory(natureId);

				// if factory is configured via extension point use it otherwise
				// create default problem reporter
				reporter = factory != null ? factory.createReporter(resource)
						: new BuildProblemReporter(resource);

			}
		}
		// EBAY -- STOP MOD
	}

	public IFileHandle getFileHandle() {
		return null;
	}

	public IProblemReporter getProblemReporter() {
		return reporter;
	}

	public ITaskReporter getTaskReporter() {
		return reporter;
	}

}
