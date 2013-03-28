/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.viewsupport;

import org.eclipse.dltk.mod.core.Flags;
import org.eclipse.dltk.mod.core.IMember;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.jface.viewers.Viewer;

public class ModelElementFlagsFilter extends AbstractModelElementFilter {
	public static final int FILTER_NONPUBLIC = 1;
	public static final int FILTER_STATIC = 2;
	public static final int FILTER_FIELDS = 4;
	public static final int FILTER_LOCALTYPES = 8;
	private int fFlags;

	public ModelElementFlagsFilter(int flags) {
		this.fFlags = flags;
		addFilter(this.fFlags);
	}

	public String getFilteringType() {
		return "ModelElementFlagsFilter:" + Integer.toString(fFlags); //$NON-NLS-1$
	}

	public boolean isFilterProperty(Object element, Object property) {
		return false;
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		// if (element instanceof IMember) {
		// IMember member = (IMember) element;
		// try {
		// if ((member.getFlags() & this.fFlags) != 0) {
		// return false;
		// }
		// } catch (ModelException e) {
		// if (DLTKCore.DEBUG) {
		// e.printStackTrace();
		// }
		// }
		// }
		//
		// return true;
		if (element instanceof IMember) {
			IMember member = (IMember) element;
			int memberType = member.getElementType();

			if (hasFilter(MemberFilter.FILTER_FIELDS)
					&& memberType == IModelElement.FIELD) {
				return false;
			}

			if (hasFilter(MemberFilter.FILTER_LOCALTYPES)
					&& memberType == IModelElement.TYPE
					&& isLocalType((IType) member)) {
				return false;
			}

			if (member.getElementName().startsWith("<")) { // filter out
				// <clinit>
				// //$NON-NLS-1$
				return false;
			}
			int flags = 0;
			try {
				flags = member.getFlags();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (hasFilter(FILTER_STATIC) && (Flags.isStatic(flags))) {
				return false;
			}
			if (hasFilter(FILTER_NONPUBLIC) && !Flags.isPublic(flags)) {
				return false;
			}
		}
		return true;
	}

	private boolean isLocalType(IType type) {
		IModelElement parent = type.getParent();
		return parent instanceof IMember && !(parent instanceof IType);
	}

	private int fFilterProperties;

	/**
	 * Modifies filter and add a property to filter for
	 */
	public final void addFilter(int filter) {
		fFilterProperties |= filter;
	}

	/**
	 * Modifies filter and remove a property to filter for
	 */
	public final void removeFilter(int filter) {
		fFilterProperties &= (-1 ^ filter);
	}

	/**
	 * Tests if a property is filtered
	 */
	public final boolean hasFilter(int filter) {
		return (fFilterProperties & filter) != 0;
	}

	private boolean isTopLevelType(IMember member) {
		IType parent = member.getDeclaringType();
		return parent == null;
	}

}
