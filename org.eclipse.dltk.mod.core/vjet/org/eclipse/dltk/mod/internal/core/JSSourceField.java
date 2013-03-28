/*******************************************************************************
 * Copyright (c) 2000, 2012 eBay Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.vjet.eclipse.core.IJSField;
import org.eclipse.dltk.mod.core.ModelException;

public class JSSourceField extends SourceField implements IJSField {

	/**
	 * @param parent
	 * @param name
	 */
	public JSSourceField(ModelElement parent, String name) {
		super(parent, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.vjet.eclipse.core.IJSField#getConstant()
	 */
	public Object getConstant() throws ModelException {
		Object constant = null;
		JSSourceFieldElementInfo info = (JSSourceFieldElementInfo) getElementInfo();
		final String constantSourceChars = info.getInitializationSource();
		if (constantSourceChars == null) {
			return null;
		}

		constant = constantSourceChars;
		return constant;
	}
}
