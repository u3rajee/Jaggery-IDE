/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

/**
 * Element info for IImportDeclaration elements.
 * 
 * @see org.eclipse.jdt.core.IImportDeclaration
 */
public class ImportDeclarationElementInfo extends MemberElementInfo implements
		IJSMemberElementInfo {
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

	@Override
	public void setSourceRangeEnd(int end) {
		super.setSourceRangeEnd(end);
	}

	@Override
	public void setSourceRangeStart(int start) {
		super.setSourceRangeStart(start);
	}
}
