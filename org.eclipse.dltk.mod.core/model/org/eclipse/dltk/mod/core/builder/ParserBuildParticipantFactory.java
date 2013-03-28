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
package org.eclipse.dltk.mod.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.ast.parser.ISourceParser;
import org.eclipse.dltk.mod.ast.parser.ISourceParserConstants;
import org.eclipse.dltk.mod.compiler.problem.ProblemCollector;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceModuleInfoCache.ISourceModuleInfo;
import org.eclipse.dltk.mod.core.SourceParserUtil;
import org.eclipse.dltk.mod.internal.core.ModelManager;

public class ParserBuildParticipantFactory extends AbstractBuildParticipantType
		implements IExecutableExtension {

	public IBuildParticipant createBuildParticipant(IScriptProject project)
			throws CoreException {
		if (natureId != null) {
			final ISourceParser parser = DLTKLanguageManager
					.getSourceParser(natureId);
			if (parser != null) {
				return new ParserBuildParticipant(parser);
			}
		}
		return null;
	}

	private String natureId = null;

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		natureId = config.getAttribute("nature"); //$NON-NLS-1$
	}

	private static class ParserBuildParticipant implements IBuildParticipant {

		private final ISourceParser parser;

		public ParserBuildParticipant(ISourceParser parser) {
			this.parser = parser;
		}

		public void build(IBuildContext context) throws CoreException {
			ModuleDeclaration moduleDeclaration = (ModuleDeclaration) context
					.get(IBuildContext.ATTR_MODULE_DECLARATION);
			if (moduleDeclaration != null) {
				// do nothing if already have AST - optimization for reconcile
				return;
			}
			// get cache entry
			final ISourceModuleInfo cacheEntry = ModelManager.getModelManager()
					.getSourceModuleInfoCache().get(context.getSourceModule());
			// check if there is cached AST
			moduleDeclaration = SourceParserUtil.getModuleFromCache(cacheEntry,
					ISourceParserConstants.DEFAULT,
					context.getProblemReporter());
			if (moduleDeclaration != null) {
				// use AST from cache
				context.set(IBuildContext.ATTR_MODULE_DECLARATION,
						moduleDeclaration);
				// eBay mod start
				// return;
				// eBay mod end
			}
			// create problem collector
			final ProblemCollector problemCollector = new ProblemCollector();
			// parse
			moduleDeclaration = parser.parse(context.getSourceModule()
					.getPath().toString().toCharArray(), context.getContents(),
					context.getProblemReporter());
			// put result to the cache
			SourceParserUtil.putModuleToCache(cacheEntry, moduleDeclaration,
					ISourceParserConstants.DEFAULT, problemCollector);
			// report errors to the build context
			problemCollector.copyTo(context.getProblemReporter());
		}
	}

}
