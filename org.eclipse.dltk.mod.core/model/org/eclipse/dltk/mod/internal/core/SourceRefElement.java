/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.IBuffer;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IOpenable;
import org.eclipse.dltk.mod.core.IParent;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.ISourceRange;
import org.eclipse.dltk.mod.core.ISourceReference;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.WorkingCopyOwner;
import org.eclipse.dltk.mod.internal.core.util.MementoTokenizer;

public abstract class SourceRefElement extends ModelElement implements
		ISourceReference {
	/*
	 * add by xingzhu, to support reset resource for a source reference element.
	 * for example, a field is defined in super type, so we could reset the
	 * resource property with the super type's corresponding resource.
	 * 
	 * Accordingly, add 'setResource(IResource)' method.
	 */
	private IResource resource;

	/*
	 * A count to uniquely identify this element in the case that a duplicate
	 * named element exists. For example, if there are two fields in a
	 * compilation unit with the same name, the occurrence count is used to
	 * distinguish them. The occurrence count starts at 1 (thus the first
	 * occurrence is occurrence 1, not occurrence 0).
	 */
	public int occurrenceCount = 1;

	protected SourceRefElement(ModelElement parent)
			throws IllegalArgumentException {
		super(parent);
	}

	protected Object createElementInfo() {
		// do not used for source elements
		return null;
	}

	protected void generateInfos(Object info, HashMap newElements,
			IProgressMonitor pm) throws ModelException {
		Openable openableParent = (Openable) getOpenableParent();
		if (openableParent == null)
			return;

		ModelElementInfo openableParentInfo = (ModelElementInfo) ModelManager
				.getModelManager().getInfo(openableParent);
		if (openableParentInfo == null) {
			openableParent.generateInfos(openableParent.createElementInfo(),
					newElements, pm);
		}
	}

	/**
	 * Return the first instance of IOpenable in the hierarchy of this type
	 * (going up the hierarchy from this type);
	 */
	public IOpenable getOpenableParent() {
		IModelElement current = getParent();
		while (current != null) {
			if (current instanceof IOpenable) {
				return (IOpenable) current;
			}
			current = current.getParent();
		}
		return null;
	}

	/*
	 * add by xingzhu, to support 'resource' field
	 * 
	 * @param resource
	 */
	public void setResource(IResource resource) {
		this.resource = resource;
	}

	/*
	 * rewritten by xingzhu.
	 * 
	 * now, if the source reference element has been set a resource, return it.
	 * otherwise, reuse the old logic, return parent's getResource.
	 * 
	 * @see org.eclipse.dltk.mod.core.IModelElement#getResource()
	 */
	public IResource getResource() {
		if (this.resource != null)
			return this.resource;
		else
			return getParent().getResource();
	}

	/**
	 * @see ISourceReference
	 */
	public ISourceRange getSourceRange() throws ModelException {
		SourceRefElementInfo info = (SourceRefElementInfo) getElementInfo();
		if (info != null) {
			return info.getSourceRange();
		} else {
			return new SourceRange(0, 0);
		}
	}

	public IPath getPath() {
		return getParent().getPath();
	}

	public IResource getUnderlyingResource() throws ModelException {
		if (!exists())
			throw newNotPresentException();
		return getParent().getUnderlyingResource();
	}

	/**
	 * @see IParent
	 */
	public boolean hasChildren() throws ModelException {
		return getChildren().length > 0;
	}

	/**
	 * @see IModelElement
	 */
	public boolean isStructureKnown() throws ModelException {
		// structure is always known inside an openable
		return true;
	}

	public boolean equals(Object o) {
		if (!(o instanceof SourceRefElement))
			return false;
		return this.occurrenceCount == ((SourceRefElement) o).occurrenceCount
				&& super.equals(o);
	}

	protected void toStringName(StringBuffer buffer) {
		super.toStringName(buffer);
		if (this.occurrenceCount > 1) {
			buffer.append("#"); //$NON-NLS-1$
			buffer.append(this.occurrenceCount);
		}
	}

	/**
	 * Elements within compilation units and class files have no corresponding
	 * resource.
	 * 
	 * @see IModelElement
	 */
	public IResource getCorrespondingResource() throws ModelException {
		if (!exists())
			throw newNotPresentException();
		return null;
	}

	public IModelElement getHandleUpdatingCountFromMemento(
			MementoTokenizer memento, WorkingCopyOwner owner) {
		if (!memento.hasMoreTokens())
			return this;
		this.occurrenceCount = Integer.parseInt(memento.nextToken());
		if (!memento.hasMoreTokens())
			return this;
		String token = memento.nextToken();
		return getHandleFromMemento(token, memento, owner);
	}

	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
		switch (token.charAt(0)) {
		case JEM_COUNT:
			return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
		}
		return this;
	}

	protected void getHandleMemento(StringBuffer buff) {
		super.getHandleMemento(buff);
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	public ISourceModule getSourceModule() {
		return ((ModelElement) getParent()).getSourceModule();
	}

	/**
	 * @see ISourceReference
	 */
	public String getSource() throws ModelException {
		IOpenable openable = getOpenableParent();
		IBuffer buffer = openable.getBuffer();

		ISourceRange range = getSourceRange();
		int offset = range.getOffset();
		int length = range.getLength();
		if (offset == -1 || length == 0) {
			return null;
		}
		int bufLen = -1;
		String content = null;
		if (buffer != null) {
			bufLen = buffer.getLength();
		} else {
			if (openable instanceof ISourceModule) {
				content = ((ISourceModule) openable).getSource();
				if (content == null) {
					return null;
				}
				bufLen = content.length();
			} else {
				return null;
			}
		}
		if (bufLen < offset + length)
			length--;
		try {
			if (buffer != null) {
				return buffer.getText(offset, length);
			} else {
				return content.substring(offset, offset + length);
			}
		} catch (RuntimeException e) {
			return null;
		}
	}
}
