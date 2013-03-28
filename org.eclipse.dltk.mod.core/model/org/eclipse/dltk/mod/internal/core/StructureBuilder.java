/*******************************************************************************
 * Copyright (c) 2005, 2012 xored software, Inc.
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
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.mod.compiler.problem.IProblemReporter;
import org.eclipse.dltk.mod.compiler.task.ITaskReporter;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.builder.IBuildParticipant;
import org.eclipse.dltk.mod.core.builder.IBuildParticipantExtension;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.internal.core.builder.AbstractBuildContext;
import org.eclipse.dltk.mod.internal.core.builder.BuildParticipantManager;

// eBay mod start
// was: class StructureBuilder {
public class StructureBuilder {
	// eBay mod end

	private static class ReconcileBuildContext extends AbstractBuildContext {

		final AccumulatingProblemReporter reporter;

		/**
		 * @param module
		 */
		protected ReconcileBuildContext(ISourceModule module,
				AccumulatingProblemReporter reporter) {
			super(module);
			this.reporter = reporter;
		}

		/*
		 * @see org.eclipse.dltk.mod.core.builder.IBuildContext#getFileHandle()
		 */
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

	// private static boolean oldMarkersDeleted = false;

	// eBay mod start
	// was: static void build(String natureId, ISourceModule module,
	public static void build(String natureId, ISourceModule module,
	// ebay mod end
			AccumulatingProblemReporter reporter) {
		final NullProgressMonitor monitor = new NullProgressMonitor();
		final IScriptProject project = module.getScriptProject();
		final IBuildParticipant[] validators = BuildParticipantManager
				.getBuildParticipants(project, natureId);
		if (validators.length == 0) {
			return;
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant)
						.beginBuild(IBuildParticipantExtension.RECONCILE_BUILD);
			}
		}
		final ReconcileBuildContext context = new ReconcileBuildContext(module,
				reporter);
		try {
			for (int k = 0; k < validators.length; ++k) {
				final IBuildParticipant participant = validators[k];
				participant.build(context);
			}
		} catch (CoreException e) {
			DLTKCore.error("error", e);
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant).endBuild(monitor);
			}
		}

	}
}
