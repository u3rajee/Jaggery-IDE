/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.debug.core.model;

/**
 * Represents an 'complex' script type
 */
public class ComplexScriptType extends AtomicScriptType {

	public ComplexScriptType(String name) {
		super(name);
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isComplex() {
		return true;
	}

	// eBay mod start
	// public String formatDetails(IScriptValue value) {
	// StringBuffer sb = new StringBuffer();
	// sb.append(getName());
	//
	// String address = value.getMemoryAddress();
	// if (address == null) {
	// address = ScriptModelMessages.unknownMemoryAddress;
	// }
	//
	// sb.append("@" + address); //$NON-NLS-1$
	//
	// return sb.toString();
	// }
	// eBay mod end

	public String formatValue(IScriptValue value) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());

		appendInstanceId(value, sb);

		return sb.toString();
	}
}
