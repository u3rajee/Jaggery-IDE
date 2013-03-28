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

import org.eclipse.vjet.eclipse.core.IImportContainer;
import org.eclipse.vjet.eclipse.core.IImportDeclaration;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.utils.CorePrinter;

/**
 * Handle for an import declaration. Info object is a
 * ImportDeclarationElementInfo.
 * 
 * @see IImportDeclaration
 */

public class ImportDeclaration extends SourceRefElement implements
		IImportDeclaration {

	protected boolean m_isOnDemand;
	protected String m_name;

	/**
	 * Constructs an ImportDeclaration in the given import container with the
	 * given name.
	 */
	public ImportDeclaration(ImportContainer parent, String name,
			boolean isOnDemand) {
		super(parent);
		this.m_name = name;
		this.m_isOnDemand = isOnDemand;
	}

	protected void closing(Object info) throws ModelException {
		// TODO Do any necessary cleanup.

	}

	public void copy(IModelElement container, IModelElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws ModelException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented");
	}

	public void delete(boolean force, IProgressMonitor monitor)
			throws ModelException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented");
	}

	public boolean equals(Object o) {
		if (!(o instanceof ImportDeclaration))
			return false;
		return super.equals(o);
	}

	public String getElementName() {
		if (this.m_isOnDemand)
			return this.m_name + ".*"; //$NON-NLS-1$
		return this.m_name;
	}

	/**
	 * @see IJavaElement
	 */
	public int getElementType() {
		return ELEMENT_TYPE;
	}

	/**
	 * @see org.eclipse.jdt.core.IImportDeclaration#getFlags()
	 */
	public int getFlags() throws ModelException {
		ImportDeclarationElementInfo info = (ImportDeclarationElementInfo) getElementInfo();
		return info.getModifiers();
	}

	/**
	 * @see JavaElement#getHandleMemento(StringBuffer) For import declarations,
	 *      the handle delimiter is associated to the import container already
	 */
	protected void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		escapeMementoName(buff, getElementName());
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	/**
	 * @see JavaElement#getHandleMemento()
	 */
	protected char getHandleMementoDelimiter() {
		// For import declarations, the handle delimiter is associated to the
		// import container already
		Assert.isTrue(false, "Should not be called"); //$NON-NLS-1$
		return 0;
	}

	public String getNameWithoutStar() {
		return this.m_name;
	}

	/*
	 * @see JavaElement#getPrimaryElement(boolean)
	 */
	public IModelElement getPrimaryElement(boolean checkOwner) {
		SourceModule cu = (SourceModule) this.parent.getParent();
		if (checkOwner && cu.isPrimary())
			return this;
		// return cu.getImport(getElementName());

		IImportContainer container = new ImportContainer(cu);
		return container.getImport(getElementName());
	}

	public boolean isLibrary() {
		try {
			return getFlags() != 0;
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns true if the import is on-demand (ends with ".*")
	 */
	public boolean isOnDemand() {
		return this.m_isOnDemand;
	}

	public void move(IModelElement container, IModelElement sibling,
			String rename, boolean replace, IProgressMonitor monitor)
			throws ModelException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented");
	}

	public void printNode(CorePrinter output) {
		// do nothing

	}

	/**
	 */
	public String readableName() {

		return null;
	}

	public void rename(String name, boolean replace, IProgressMonitor monitor)
			throws ModelException {
		// TODO Auto-generated method stub
		throw new RuntimeException("Method not implemented");
	}

	/**
	 * @private Debugging purposes
	 */
	protected void toStringInfo(int tab, StringBuffer buffer, Object info,
			boolean showResolvedInfo) {
		buffer.append(this.tabString(tab));
		buffer.append("import "); //$NON-NLS-1$
		toStringName(buffer);
		if (info == null) {
			buffer.append(" (not open)"); //$NON-NLS-1$
		}
	}
}
