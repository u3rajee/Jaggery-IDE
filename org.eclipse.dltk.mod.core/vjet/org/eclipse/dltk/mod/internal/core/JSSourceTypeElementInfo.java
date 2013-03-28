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
import org.eclipse.dltk.mod.core.IType;

public class JSSourceTypeElementInfo extends SourceTypeElementInfo implements
		IJSMemberElementInfo {
	/**
	 * The names of the interfaces this type implements or extends. These names
	 * are fully qualified in the case of a binary type, and are NOT fully
	 * qualified in the case of a source type
	 */
	protected String[] m_superInterfaceNames;

	/*
	 * The type parameters of this source type. Empty if none.
	 */
	protected IJSTypeParameter[] m_typeParameters = JSTypeParameter.NO_TYPE_PARAMETERS;

	/**
	 * @see IJSSourceType
	 */
	public String[] getInterfaceNames() {
		if (this.handle.getElementName().length() == 0) { // if anonymous type
			return null;
		}
		return this.m_superInterfaceNames;
	}

	@Override
	public void setFlags(int flags) {
		super.setFlags(flags);
	}

	@Override
	public void setHandle(IType handle) {
		super.setHandle(handle);
	}

	@Override
	public void setNameSourceEnd(int end) {
		super.setNameSourceEnd(end);
	}

	@Override
	public void setNameSourceStart(int start) {
		super.setNameSourceStart(start);
	}

	@Override
	public void setSourceRangeEnd(int end) {
		super.setSourceRangeEnd(end);
	}

	@Override
	public void setSourceRangeStart(int start) {
		super.setSourceRangeStart(start);
	}

	@Override
	public void setSuperclassNames(String[] superclassNames) {
		super.setSuperclassNames(superclassNames);
	}

	/**
	 * Sets the (unqualified) names of the interfaces this type implements or
	 * extends
	 */
	public void setSuperInterfaceNames(String[] superInterfaceNames) {
		this.m_superInterfaceNames = superInterfaceNames;
	}
}
