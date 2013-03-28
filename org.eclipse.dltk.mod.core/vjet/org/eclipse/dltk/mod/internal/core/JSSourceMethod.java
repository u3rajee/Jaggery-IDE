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

import org.eclipse.vjet.eclipse.core.IJSMethod;
import org.eclipse.vjet.eclipse.core.IJSType;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.Flags;
import org.eclipse.dltk.mod.core.IMethod;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.core.util.Util;

public class JSSourceMethod extends SourceMethod implements IJSMethod {

	/**
	 * The parameter type signatures of the method - stored locally to perform
	 * equality test. <code>null</code> indicates no parameters.
	 */
	protected String[] m_parameterTypes;

	public JSSourceMethod(ModelElement parent, String name) {
		this(parent, name, null);
	}

	public JSSourceMethod(ModelElement parent, String name,
			String[] parameterTypes) {
		super(parent, name);
		Assert.isTrue(name.indexOf('.') == -1);
		if (parameterTypes == null) {
			this.m_parameterTypes = CharOperation.NO_STRINGS;
		} else {
			this.m_parameterTypes = parameterTypes;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof JSSourceMethod))
			return false;
		return super.equals(o)
				&& Util.equalArraysOrNull(this.m_parameterTypes,
						((JSSourceMethod) o).m_parameterTypes);
	}

	public int hashCode() {
		int hash = super.hashCode();
		for (int i = 0, length = this.m_parameterTypes.length; i < length; i++) {
			hash = Util.combineHashCodes(hash,
					this.m_parameterTypes[i].hashCode());
		}

		return hash;
	}

	/**
	 * @private Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info,
			boolean showResolvedInfo) {
		buffer.append(tabString(tab));
		if (info == null) {
			toStringName(buffer);
			buffer.append(" (not open)"); //$NON-NLS-1$
		} else if (info == NO_INFO) {
			toStringName(buffer);
		} else {
			SourceMethodElementInfo methodInfo = (SourceMethodElementInfo) info;
			int flags = methodInfo.getModifiers();
			if (Flags.isStatic(flags)) {
				buffer.append("static "); //$NON-NLS-1$
			}
			if (!methodInfo.isConstructor()) {
				buffer.append(methodInfo.getReturnTypeName());
				buffer.append(' ');
			}
			toStringName(buffer, flags);
		}
	}

	protected void toStringName(StringBuffer buffer) {
		toStringName(buffer, 0);
	}

	protected void toStringName(StringBuffer buffer, int flags) {
		buffer.append(getElementName());
		buffer.append('(');
		String[] parameters = getParameterTypes();
		int length;
		if (parameters != null && (length = parameters.length) > 0) {
			// currently parser do not support var args
			// boolean isVarargs = Flags.isVarargs(flags);
			for (int i = 0; i < length; i++) {
				try {
					if (i < length - 1) {
						// buffer.append(Signature.toString(parameters[i]));
						buffer.append(parameters[i]);
						buffer.append(", "); //$NON-NLS-1$
					}
					// else if (isVarargs) {
					// // remove array from signature
					// String parameter = parameters[i].substring(1);
					// buffer.append(Signature.toString(parameter));
					// buffer.append(" ..."); //$NON-NLS-1$
					// }
					else {
						// buffer.append(Signature.toString(parameters[i]));
						buffer.append(parameters[i]);
					}
				} catch (IllegalArgumentException e) {
					// parameter signature is malformed
					buffer.append("*** invalid signature: "); //$NON-NLS-1$
					buffer.append(parameters[i]);
				}
			}
		}
		buffer.append(')');
		if (this.occurrenceCount > 1) {
			buffer.append("#"); //$NON-NLS-1$
			buffer.append(this.occurrenceCount);
		}
	}

	/**
	 * @see ModelElement#getHandleMemento(StringBuffer)
	 */
	@Override
	protected void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		char delimiter = getHandleMementoDelimiter();
		buff.append(delimiter);
		escapeMementoName(buff, getElementName());
		for (int i = 0; i < this.m_parameterTypes.length; i++) {
			buff.append(delimiter);
			escapeMementoName(buff, this.m_parameterTypes[i]);
		}
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	/**
	 * @see IJSMethod
	 */
	public int getNumberOfParameters() {
		return this.m_parameterTypes == null ? 0 : this.m_parameterTypes.length;
	}

	/**
	 * @see IJSMethod
	 */
	public String[] getParameterTypes() {
		return this.m_parameterTypes;
	}

	/*
	 * @see JavaElement#getPrimaryElement(boolean)
	 */
	@Override
	public IModelElement getPrimaryElement(boolean checkOwner) {
		if (checkOwner) {
			SourceModule cu = (SourceModule) getAncestor(SOURCE_MODULE);
			if (cu.isPrimary())
				return this;
		}
		IModelElement primaryParent = this.parent.getPrimaryElement(false);
		return ((IJSType) primaryParent).getMethod(this.name,
				this.m_parameterTypes);
	}

	/**
	 * @see IJSMethod
	 */
	public String getReturnType() throws ModelException {
		SourceMethodElementInfo info = (SourceMethodElementInfo) getElementInfo();
		return new String(info.getReturnTypeName());
		// TODO return Signature.createTypeSignature(info.getReturnTypeName(),
		// false);
	}

	/**
	 * @see IMethod
	 */
	@Override
	public boolean isConstructor() throws ModelException {
		// if (!this.getElementName().equals(this.parent.getElementName())) {
		// use "constructs" instead of type name
		if (!this.getElementName().equals("constructs")) {
			// faster than reaching the info
			return false;
		}
		JSSourceMethodElementInfo info = (JSSourceMethodElementInfo) getElementInfo();
		return info.isConstructor();
	}

	public SourceField getLocalDeclaration(String name, String type) {
		return new VjoLocalVariable(this, name, 0, 0, 0, 0, type);
	}
}
