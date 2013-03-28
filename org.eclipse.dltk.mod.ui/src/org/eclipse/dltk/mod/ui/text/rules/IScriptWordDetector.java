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
package org.eclipse.dltk.mod.ui.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Defines the interface by which <code>WordRule</code> determines whether a
 * given character is valid as part of a script word in the current context.
 */
public interface IScriptWordDetector extends IWordDetector {

	/**
	 * Returns <code>true</code> if the character prior to the word start
	 * character is valid for the word to match.
	 * 
	 * <p>
	 * For instance, this can be used to prevent a method name invocation that
	 * also matches a builtin name from being matched.
	 * </p>
	 */
	boolean isPriorCharValid(char c);
}
