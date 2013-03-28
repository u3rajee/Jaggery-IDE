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

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.ISourceRange;
import org.eclipse.dltk.mod.core.ISourceReference;
import org.eclipse.dltk.mod.core.ModelException;

public class VjoLocalVariable extends SourceField {

	public int declarationSourceStart, declarationSourceEnd;
	public int nameStart, nameEnd;
	String type;
	
	public VjoLocalVariable(
			ModelElement parent, 
			String name, 
			int declarationSourceStart, 
			int declarationSourceEnd,
			int nameStart, 
			int nameEnd,
			String type) {
		
		super(parent, name);
		this.declarationSourceStart = declarationSourceStart;
		this.declarationSourceEnd = declarationSourceEnd;
		this.nameStart = nameStart;
		this.nameEnd = nameEnd;
		this.type = type;
	}

	public boolean equals(Object o) {
		if (!(o instanceof VjoLocalVariable)) return false;
		VjoLocalVariable other = (VjoLocalVariable)o;
		return 
			this.declarationSourceStart == other.declarationSourceStart 
			&& this.declarationSourceEnd == other.declarationSourceEnd
			&& this.nameStart == other.nameStart
			&& this.nameEnd == other.nameEnd
			&& super.equals(o);
	}
	
	public boolean exists() {
		return this.parent.exists(); // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=46192
	}

	protected char getHandleMementoDelimiter() {
		return ModelElement.JEM_LOCALVARIABLE;
	}

	public String getElementName() {
		return this.name;
	}

	public int getElementType() {
		return IModelElement.FIELD;
	}

	public ISourceRange getNameRange() {
		return new SourceRange(this.nameStart, this.nameEnd-this.nameStart+1);
	}
	
//	public IPath getPath() {
//		return this.parent.getPath();
//	}

//	public IResource getResource() {
//		return this.parent.getResource();
//	}

	/**
	 * @see ISourceReference
	 */
//	public String getSource() throws ModelException {
//		IOpenable openable = this.parent.getOpenableParent();
//		IBuffer buffer = openable.getBuffer();
//		if (buffer == null) {
//			return null;
//		}
//		ISourceRange range = getSourceRange();
//		int offset = range.getOffset();
//		int length = range.getLength();
//		if (offset == -1 || length == 0 ) {
//			return null;
//		}
//		try {
//			return buffer.getText(offset, length);
//		} catch(RuntimeException e) {
//			return null;
//		}
//	}
	
	/**
	 * @see ISourceReference
	 */
	public ISourceRange getSourceRange() {
		return new SourceRange(this.declarationSourceStart, this.declarationSourceEnd-this.declarationSourceStart+1);
	}
	
	public String getTypeSignature() {
		return this.type;
	}

	public IResource getUnderlyingResource() throws ModelException {
		return this.parent.getUnderlyingResource();
	}

	public boolean isStructureKnown() throws ModelException {
        return true;
    }
}