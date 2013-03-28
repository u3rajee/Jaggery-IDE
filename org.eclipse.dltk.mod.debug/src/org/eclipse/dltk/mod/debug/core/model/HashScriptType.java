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
package org.eclipse.dltk.mod.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

/**
 * Represents a 'hash' script type
 */
public class HashScriptType extends CollectionScriptType {

	private static String HASH = "hash"; //$NON-NLS-1$

	public HashScriptType() {
		super(HASH);
	}

	protected String buildDetailString(IVariable variable)
			throws DebugException {
		StringBuffer sb = new StringBuffer();

		sb.append(getVariableName(variable));
		sb.append("=>"); //$NON-NLS-1$
		sb.append(variable.getValue().getValueString());

		return sb.toString();
	}

	protected char getCloseBrace() {
		return '}';
	}

	protected char getOpenBrace() {
		return '{';
	}

	/**
	 * Returns the variable name (key) for the hash element.
	 * 
	 * <p>
	 * Subclasses may override this method if they need to process the variable
	 * name before it is displayed.
	 * </p>
	 */
	protected String getVariableName(IVariable variable) throws DebugException {
		return variable.getName();
	}
}
