/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ast.expressions;

import org.eclipse.dltk.mod.ast.DLTKToken;
import org.eclipse.dltk.mod.utils.CorePrinter;

/**
 * 
 * Numeric literal. Used to hold ints, floats and complex numbers.
 * 
 */
public class NumericLiteral extends Literal {

	private long intValue;
	
	/**
	 * Construct from ANTLR token. With appropriate position information.
	 * 
	 * @param number
	 */
	public NumericLiteral(DLTKToken number) {
		super(number);
	}

	public NumericLiteral(int start, int end, long value) {
		super(start, end);
		this.intValue = value;
	}
	
	public long getIntValue () {
		return intValue;
	}

	
	public String getValue() {
		return String.valueOf(intValue);
	}

	/**
	 * Return kind.
	 */

	public int getKind() {
		return NUMBER_LITERAL;
	}

	/**
	 * Testing purposes only. Used to print number.
	 */

	public void printNode(CorePrinter output) {
		output.formatPrintLn(this.getValue());
	}
}
