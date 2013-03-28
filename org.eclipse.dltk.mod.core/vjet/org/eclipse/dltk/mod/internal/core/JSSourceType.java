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

import java.util.ArrayList;

import org.eclipse.vjet.eclipse.core.ClassFileConstants;
import org.eclipse.vjet.eclipse.core.IJSMethod;
import org.eclipse.vjet.eclipse.core.IJSType;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.mod.core.IField;
import org.eclipse.dltk.mod.core.IMethod;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.ModelException;

public class JSSourceType extends SourceType implements IJSType {
	// Type decl kinds
	public static final int CLASS_DECL = 1;
	public static final int ENUM_DECL = 3;
	public static final int INTERFACE_DECL = 2;

	public final static int kind(int flags) {
		switch (flags
				& (ClassFileConstants.AccInterface
						| ClassFileConstants.AccAnnotation | ClassFileConstants.AccEnum)) {
		case ClassFileConstants.AccInterface:
			return INTERFACE_DECL;
		case ClassFileConstants.AccEnum:
			return ENUM_DECL;
		default:
			return CLASS_DECL;
		}
	}

	public JSSourceType(ModelElement parent, String name) {
		super(parent, name);
	}

	/**
	 * @see IJSType
	 */
	public IJSInitializer getInitializer(int count) {
		return new JSInitializer(this, count);
	}

	/**
	 * @see IJSType
	 */
	public IJSInitializer[] getInitializers() throws ModelException {
		ArrayList list = getChildrenOfType(IJSInitializer.ELEMENT_TYPE);
		IJSInitializer[] array = new IJSInitializer[list.size()];
		list.toArray(array);
		return array;
	}

	public IJSMethod getMethod(String selector, String[] parameterTypeSignatures) {
		return new JSSourceMethod(this, selector, parameterTypeSignatures);
	}

	@Override
	public IMethod getMethod(String selector) {
		return new JSSourceMethod(this, selector);
	}

	/**
	 * @see IJSType
	 */
	public IProjectFragment getProjectFragment() {
		IModelElement parentElement = this.parent;
		while (parentElement != null) {
			if (parentElement.getElementType() == IModelElement.PROJECT_FRAGMENT) {
				return (IProjectFragment) parentElement;
			} else {
				parentElement = parentElement.getParent();
			}
		}
		Assert.isTrue(false); // should not happen
		return null;
	}

	/**
	 * @see IJSType
	 */
	public String[] getSuperInterfaceNames() throws ModelException {
		JSSourceTypeElementInfo info = (JSSourceTypeElementInfo) getElementInfo();
		String[] names = info.getInterfaceNames();
		return names;
	}

	public boolean isClass() throws ModelException {
		SourceTypeElementInfo info = (SourceTypeElementInfo) getElementInfo();
		return kind(info.getModifiers()) == CLASS_DECL;
	}

	/**
	 * @see IJSType#isInterface()
	 */
	public boolean isInterface() throws ModelException {
		SourceTypeElementInfo info = (SourceTypeElementInfo) getElementInfo();
		switch (kind(info.getModifiers())) {
		case INTERFACE_DECL:
			return true;
		}
		return false;
	}

	/**
	 * @see IJSType#isEnum()
	 */
	public boolean isEnum() throws ModelException {
		SourceTypeElementInfo info = (SourceTypeElementInfo) getElementInfo();
		switch (kind(info.getModifiers())) {
		case ENUM_DECL:
			return true;
		}
		return false;
	}

	@Override
	public IField getField(String fieldName) {
		return new JSSourceField(this, fieldName);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof JSSourceType)) {
			return false;
		}

		if (!this.getFullyQualifiedName(".").equals(
				((JSSourceType) o).getFullyQualifiedName("."))) {
			return false;
		}
		return super.equals(o);
	}
}
