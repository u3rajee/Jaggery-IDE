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
package org.eclipse.dltk.mod.debug.core.eval;

import org.eclipse.dltk.mod.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.mod.debug.core.model.IScriptStackFrame;

public interface IScriptEvaluationEngine {
	IScriptDebugTarget getScriptDebugTarget();

	IScriptEvaluationResult syncEvaluate(String snippet, IScriptStackFrame frame);

	void asyncEvaluate(String snippet, IScriptStackFrame frame,
			IScriptEvaluationListener listener);

	void dispose();
}
