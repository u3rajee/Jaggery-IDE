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
package org.eclipse.dltk.mod.internal.debug.core.eval;

import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationCommand;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationEngine;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationListener;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.mod.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.mod.debug.core.model.IScriptStackFrame;

public class ScriptEvaluationCommand implements IScriptEvaluationCommand {
	private final IScriptEvaluationEngine engine;
	private final String snippet;
	private final IScriptStackFrame frame;

	public ScriptEvaluationCommand(IScriptEvaluationEngine engine,
			String snippet, IScriptStackFrame frame) {
		this.snippet = snippet;
		this.engine = engine;
		this.frame = frame;
	}

	public IScriptDebugTarget getScriptDebugTarget() {
		return engine.getScriptDebugTarget();
	}

	public IScriptEvaluationResult syncEvaluate() {
		return engine.syncEvaluate(snippet, frame);
	}

	public void asyncEvaluate(IScriptEvaluationListener listener) {
		engine.asyncEvaluate(snippet, frame, listener);
	}

	public void dispose() {
		engine.dispose();
	}
}
