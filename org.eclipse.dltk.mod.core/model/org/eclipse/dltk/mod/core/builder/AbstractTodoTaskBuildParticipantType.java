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
package org.eclipse.dltk.mod.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.mod.compiler.task.TodoTaskAstParser;
import org.eclipse.dltk.mod.core.IScriptProject;

/**
 * Abstract class that may be used to add build support for 'todo' type task
 * tags.
 * 
 * <p>
 * Subclasses should be sure to include an empty argument public constructor as
 * part of their implementation. The following snippet may be used in the
 * <code>plugin.xml</code> file.
 * </p>
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.dltk.mod.core.buildParticipant&quot;&gt;
 *   &lt;buildParticipant
 *     class=&quot;...&quot;
 *     id=&quot;...&quot;
 *     nature=&quot;...&quot; /&gt;  
 * &lt;extension&gt;
 * </pre>
 */
public abstract class AbstractTodoTaskBuildParticipantType extends
		AbstractBuildParticipantType {

	public final IBuildParticipant createBuildParticipant(IScriptProject project) {
		final ITodoTaskPreferences prefs = getPreferences(project);
		if (prefs.isEnabled()) {
			return getBuildParticipant(prefs);
		}

		return null;
	}

	/**
	 * @deprecated
	 */
	protected final void getPreferences() {
		//
	}

	/**
	 * Returns the <code>Preferences</code> object that contains the settings
	 * for 'todo' tasks.
	 */
	protected abstract ITodoTaskPreferences getPreferences(
			IScriptProject project);

	/**
	 * Returns the build participant that will be used to report 'todo' task
	 * tags.
	 * 
	 * <p>
	 * Default implementation returns an instance of
	 * <code>TodoTaskBuildParticipant</code>. Subclasses are free to override
	 * this method if they wish to provide a different implementation.
	 * </p>
	 */
	protected IBuildParticipant getBuildParticipant(
			ITodoTaskPreferences preferences) {
		return new TodoTaskBuildParticipant(preferences);
	}

	protected static class TodoTaskBuildParticipant extends TodoTaskAstParser
			implements IBuildParticipant {

		public TodoTaskBuildParticipant(ITodoTaskPreferences preferences) {
			super(preferences);
		}

		public void build(IBuildContext context) throws CoreException {
			final ModuleDeclaration ast = (ModuleDeclaration) context
					.get(IBuildContext.ATTR_MODULE_DECLARATION);
			initialize(ast);
			parse(context.getTaskReporter(), context.getContents());
		}
	}
}
