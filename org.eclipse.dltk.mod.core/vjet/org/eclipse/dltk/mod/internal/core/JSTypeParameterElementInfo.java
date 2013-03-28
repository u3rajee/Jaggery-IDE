/*******************************************************************************
 * Copyright (c) 2004, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

public class JSTypeParameterElementInfo extends SourceRefElementInfo implements
		IJSModelElementInfo {

	/*
	 * The bounds names of this type parameter.
	 */
	public char[][] m_bounds;

	/*
	 * The last position of this type parameter name in the its openable's
	 * buffer.
	 */
	public int m_nameEnd = -1;

	/*
	 * The start position of this type parameter's name in the its openable's
	 * buffer.
	 */
	public int m_nameStart = -1;
}
