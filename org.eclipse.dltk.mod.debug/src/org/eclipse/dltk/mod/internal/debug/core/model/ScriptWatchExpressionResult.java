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
package org.eclipse.dltk.mod.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationResult;

public class ScriptWatchExpressionResult implements IWatchExpressionResult {

	private final IScriptEvaluationResult result;

	public ScriptWatchExpressionResult(IScriptEvaluationResult result) {
		this.result = result;
	}

	public String[] getErrorMessages() {
		return result.getErrorMessages();
	}

	public DebugException getException() {
		return result.getException();
	}

	public String getExpressionText() {
		return result.getSnippet();
	}

	public IValue getValue() {
		return result.getValue();
	}

	public boolean hasErrors() {
		return result.hasErrors();
	}
}
