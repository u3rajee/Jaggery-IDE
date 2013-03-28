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
package org.eclipse.dltk.mod.core;

import org.eclipse.vjet.eclipse.core.ClassFileConstants;
import org.eclipse.dltk.mod.ast.Modifiers;

public class Flags implements Modifiers {

	/**
	 * Returns whether the given integer includes the <code>private</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>private</code> modifier is
	 *         included
	 */
	public static boolean isPrivate(int flags) {
		return (flags & AccPrivate) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>protected</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>protected</code> modifier is
	 *         included
	 */
	public static boolean isProtected(int flags) {
		return (flags & AccProtected) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>public</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>public</code> modifier is included
	 */
	public static boolean isPublic(int flags) {
		return (flags & AccPublic) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>static</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>static</code> modifier is included
	 */
	public static boolean isStatic(int flags) {
		return (flags & AccStatic) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>final</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>final</code> modifier is included
	 */
	public static boolean isFinal(int flags) {
		return (flags & AccFinal) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>abstract</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>abstract</code> modifier is
	 *         included
	 */
	public static boolean isAbstract(int flags) {
		return (flags & AccAbstract) != 0;
	}

	/**
	 * Returns whether the given integer includes the <code>interface</code>
	 * modifier.
	 * 
	 * @param flags
	 *            the flags
	 * @return <code>true</code> if the <code>interface</code> modifier is
	 *         included
	 */
	public static boolean isInterface(int flags) {
		return (flags & AccInterface) != 0;
	}

	public static boolean isAnnotation(int flags) {
		return (flags & ClassFileConstants.AccAnnotation) != 0;
	}

	public static boolean isEnum(int flags) {
		return (flags & ClassFileConstants.AccEnum) != 0;
	}

}
