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

public class FormatterException extends Exception {

	private static final long serialVersionUID = -1771588890906618803L;

	public FormatterException() {
		// empty
	}

	/**
	 * @param message
	 * @param cause
	 */
	public FormatterException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public FormatterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public FormatterException(Throwable cause) {
		super(cause);
	}

}
