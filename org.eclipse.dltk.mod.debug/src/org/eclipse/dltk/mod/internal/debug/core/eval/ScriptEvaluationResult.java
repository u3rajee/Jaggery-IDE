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

import org.eclipse.debug.core.DebugException;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.mod.debug.core.model.IScriptThread;
import org.eclipse.dltk.mod.debug.core.model.IScriptValue;

public class ScriptEvaluationResult implements IScriptEvaluationResult {
	private final IScriptThread thread;
	private final String snippet;
	private final IScriptValue value;

	public ScriptEvaluationResult(IScriptThread thread, String snippet,
			IScriptValue value) {
		this.thread = thread;
		this.value = value;
		this.snippet = snippet;
	}

	public String getSnippet() {
		return snippet;
	}

	public IScriptValue getValue() {
		return value;
	}

	public IScriptThread getThread() {
		return thread;
	}

	public String[] getErrorMessages() {
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException() {
		return null;
	}

	public boolean hasErrors() {
		return false;
	}
}
