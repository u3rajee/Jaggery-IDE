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

import org.eclipse.vjet.eclipse.core.IJSType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.Flags;
import org.eclipse.dltk.mod.core.IMember;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IModelStatusConstants;
import org.eclipse.dltk.mod.core.ISourceManipulation;
import org.eclipse.dltk.mod.core.ISourceRange;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.core.util.Util;
import org.eclipse.dltk.mod.utils.CorePrinter;

/**
 * @see IJSInitializer
 */

public class JSInitializer extends Member implements IJSInitializer {

	public static final char JEM_INITIALIZER = '|';

	public JSInitializer(ModelElement parent, int count) {
		super(parent);
		// 0 is not valid: this first occurrence is occurrence 1.
		if (count <= 0)
			throw new IllegalArgumentException();
		this.occurrenceCount = count;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof JSInitializer))
			return false;
		return super.equals(o);
	}

	/**
	 * @see IJavaElement
	 */
	public int getElementType() {
		return ELEMENT_TYPE;
	}

	/**
	 * @see JavaElement#getHandleMemento(StringBuffer)
	 */
	@Override
	protected void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(this.occurrenceCount);
	}

	/**
	 * @see JavaElement#getHandleMemento()
	 */
	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_INITIALIZER;
	}

	/**
	 * @see IMember
	 */
	@Override
	public ISourceRange getNameRange() {
		return null;
	}

	/*
	 * @see ModelElement#getPrimaryElement(boolean)
	 */
	@Override
	public IModelElement getPrimaryElement(boolean checkOwner) {
		if (checkOwner) {
			SourceModule cu = (SourceModule) getAncestor(SOURCE_MODULE);
			if (cu == null || cu.isPrimary())
				return this;
		}
		IModelElement primaryParent = this.parent.getPrimaryElement(false);
		return ((IJSType) primaryParent).getInitializer(this.occurrenceCount);
	}

	@Override
	public int hashCode() {
		return Util.combineHashCodes(this.parent.hashCode(),
				this.occurrenceCount);
	}

	@Override
	public void printNode(CorePrinter output) {

	}

	/**
	 */
	public String readableName() {

		return ((ModelElement) getDeclaringType()).getElementName();
	}

	/**
	 * @see ISourceManipulation
	 */
	public void rename(String newName, boolean force, IProgressMonitor monitor)
			throws ModelException {
		throw new ModelException(new ModelStatus(
				IModelStatusConstants.INVALID_ELEMENT_TYPES, this));
	}

	/**
	 * @private Debugging purposes
	 */
	@Override
	protected void toStringInfo(int tab, StringBuffer buffer, Object info,
			boolean showResolvedInfo) {
		buffer.append(this.tabString(tab));
		if (info == null) {
			buffer.append("<initializer #"); //$NON-NLS-1$
			buffer.append(this.occurrenceCount);
			buffer.append("> (not open)"); //$NON-NLS-1$
		} else if (info == NO_INFO) {
			buffer.append("<initializer #"); //$NON-NLS-1$
			buffer.append(this.occurrenceCount);
			buffer.append(">"); //$NON-NLS-1$
		} else {
			try {
				buffer.append("<"); //$NON-NLS-1$
				if (Flags.isStatic(this.getFlags())) {
					buffer.append("static "); //$NON-NLS-1$
				}
				buffer.append("initializer #"); //$NON-NLS-1$
				buffer.append(this.occurrenceCount);
				buffer.append(">"); //$NON-NLS-1$
			} catch (ModelException e) {
				buffer.append("<ModelException in toString of " + getElementName()); //$NON-NLS-1$
			}
		}
	}
}
