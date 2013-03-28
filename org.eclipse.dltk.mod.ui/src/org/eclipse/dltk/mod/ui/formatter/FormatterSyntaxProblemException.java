/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.formatter;

public class FormatterSyntaxProblemException extends FormatterException {

	private static final long serialVersionUID = 4527887872127464243L;

	public FormatterSyntaxProblemException() {
		// empty
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormatterSyntaxProblemException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FormatterSyntaxProblemException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormatterSyntaxProblemException(Throwable cause) {
		super(cause);
	}

}
