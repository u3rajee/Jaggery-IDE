/*******************************************************************************
 * Copyright (c) 2000, 2012 eBay Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.vjet.eclipse.core.IJSTypeParameter;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.ModelException;

public class JSSourceMethodElementInfo extends SourceMethodElementInfo
		implements IJSMemberElementInfo {
	private static final char[] CONSTRUCTOR_RETURN_TYPE_NAME = new char[] {
			'v', 'o', 'i', 'd' };

	private boolean m_isConstructor;

	/*
	 * Return type name for this method. The return type of constructors is
	 * equivalent to void.
	 */
	protected char[] returnType;

	/*
	 * The type parameters of this source type. Empty if none.
	 */
	protected IJSTypeParameter[] typeParameters = JSTypeParameter.NO_TYPE_PARAMETERS;

	@Override
	public char[] getReturnTypeName() {
		if (m_isConstructor) {
			return CONSTRUCTOR_RETURN_TYPE_NAME;
		}

		return this.returnType;
	}

	@Override
	public char[][][] getTypeParameterBounds() {
		int length = this.typeParameters.length;
		char[][][] typeParameterBounds = new char[length][][];
		for (int i = 0; i < length; i++) {
			try {
				JSTypeParameterElementInfo info = (JSTypeParameterElementInfo) ((ModelElement) this.typeParameters[i])
						.getElementInfo();
				typeParameterBounds[i] = info.m_bounds;
			} catch (ModelException e) {
				// type parameter does not exist: ignore
			}
		}
		return typeParameterBounds;
	}

	@Override
	public char[][] getTypeParameterNames() {
		int length = this.typeParameters.length;
		if (length == 0)
			return CharOperation.NO_CHAR_CHAR;
		char[][] typeParameterNames = new char[length][];
		for (int i = 0; i < length; i++) {
			typeParameterNames[i] = this.typeParameters[i].getElementName()
					.toCharArray();
		}
		return typeParameterNames;
	}

	@Override
	public boolean isConstructor() {
		return m_isConstructor;
	}

	@Override
	public void setArgumentInializers(String[] initializers) {
		super.setArgumentInializers(initializers);
	}

	@Override
	public void setArgumentNames(String[] names) {
		super.setArgumentNames(names);
	}

	public void setConstructor(boolean isConstructor) {
		this.m_isConstructor = isConstructor;
	}

	@Override
	public void setFlags(int flags) {
		super.setFlags(flags);
	}

	@Override
	public void setNameSourceEnd(int end) {
		super.setNameSourceEnd(end);
	}

	@Override
	public void setNameSourceStart(int start) {
		super.setNameSourceStart(start);
	}

	public void setReturnType(char[] type) {
		this.returnType = type;
	}

	@Override
	public void setSourceRangeEnd(int end) {
		super.setSourceRangeEnd(end);
	}

	@Override
	public void setSourceRangeStart(int start) {
		super.setSourceRangeStart(start);
	}
}
