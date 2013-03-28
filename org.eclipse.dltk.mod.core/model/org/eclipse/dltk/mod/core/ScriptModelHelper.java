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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class ScriptModelHelper {
	/**
	 * Returns the opened script projects in the model, opened script projects
	 * that have the specified nature id, or an empty array if there are none.
	 * 
	 * @param model
	 *            script model
	 * 
	 * @param natureId
	 *            nature id, if null then all opened script projects returned
	 * 
	 * @return the script projects in this model, or an empty array if there are
	 *         none
	 * 
	 * @throws ModelException
	 *             if the request fails
	 */
	public static IScriptProject[] getOpenedScriptProjects(IScriptModel model,
			String natureId) throws ModelException {
		final List list = new ArrayList();
		final IScriptProject[] projects = model.getScriptProjects();
		try {
			for (int i = 0; i < projects.length; ++i) {
				final IScriptProject scriptProject = projects[i];
				final IProject project = scriptProject.getProject();

				if (project.exists() && project.isOpen()
						&& (natureId == null || project.hasNature(natureId))) {
					list.add(scriptProject);
				}
			}
		} catch (CoreException e) {
			throw new ModelException(e);
		}

		return (IScriptProject[]) list.toArray(new IScriptProject[list.size()]);
	}
}
