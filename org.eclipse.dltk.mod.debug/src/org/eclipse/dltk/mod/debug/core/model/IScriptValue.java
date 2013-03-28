/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.mod.debug.core.eval.IScriptEvaluationCommand;

public interface IScriptValue extends IValue {
	String getInstanceId();

	IScriptType getType();

	String getEvalName();

	String getRawValue();

	/**
	 * Returns the physical memory address or <code>null</code> if it is not
	 * available.
	 */
	String getMemoryAddress();

	/**
	 * Returns the text that will be displayed in the 'details' pane of the
	 * 'Variables' view.
	 */
	String getDetailsString();

	IVariable getVariable(int offset) throws DebugException;

	IScriptEvaluationCommand createEvaluationCommand(String messageTemplate,
			IScriptThread thread);
}
