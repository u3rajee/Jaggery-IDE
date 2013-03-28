/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.IMember;
import org.eclipse.dltk.mod.core.ISourceRange;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.utils.CorePrinter;
import org.eclipse.vjet.eclipse.core.IJSTypeParameter;

public class JSTypeParameter extends SourceRefElement implements
		IJSTypeParameter {

	static final IJSTypeParameter[] NO_TYPE_PARAMETERS = new IJSTypeParameter[0];

	protected String m_name;

	/**
	 * Constant representing a type parameter declaration. A Java element with
	 * this type can be safely cast to <code>ITypeParameter</code>.
	 * 
	 * @since 3.1
	 */
	int TYPE_PARAMETER = 15;

	public JSTypeParameter(ModelElement parent, String name) {
		super(parent);
		this.m_name = name;
	}

	@Override
	protected void closing(Object info) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof JSTypeParameter))
			return false;
		return super.equals(o);
	}

	/*
	 * @see JavaElement#generateInfos
	 */
	@Override
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

		// TODO do we need BinaryModule?
		// if (openableParent.getElementType() == IModelElement.BINARY_MODULE) {
		// ClassFileInfo classFileInfo = (ClassFileInfo) (openableParentInfo ==
		// null ? newElements.get(openableParent) : openableParentInfo);
		// if (classFileInfo == null) return;
		// classFileInfo.getBinaryChildren(newElements); // forces the
		// initialization
		// }
	}

	public String[] getBounds() throws ModelException {
		JSTypeParameterElementInfo info = (JSTypeParameterElementInfo) getElementInfo();
		return CharOperation.toStrings(info.m_bounds);
	}

	public IMember getDeclaringMember() {
		return (IMember) getParent();
	}

	@Override
	public String getElementName() {
		return this.m_name;
	}

	public int getElementType() {
		return TYPE_PARAMETER;
	}

	// public ISourceRange getNameRange() throws ModelException {
	// SourceMapper mapper= getSourceMapper();
	// if (mapper != null) {
	// // ensure the class file's buffer is open so that source ranges are
	// computed
	// ClassFile classFile = (ClassFile)getClassFile();
	// if (classFile != null) {
	// classFile.getBuffer();
	// return mapper.getNameRange(this);
	// }
	// }
	// TypeParameterElementInfo info = (TypeParameterElementInfo)
	// getElementInfo();
	// return new SourceRange(info.nameStart, info.nameEnd - info.nameStart +
	// 1);
	// }

	// /*
	// * @see ISourceReference
	// */
	// public ISourceRange getSourceRange() throws JavaModelException {
	// SourceMapper mapper= getSourceMapper();
	// if (mapper != null) {
	// // ensure the class file's buffer is open so that source ranges are
	// computed
	// ClassFile classFile = (ClassFile)getClassFile();
	// if (classFile != null) {
	// classFile.getBuffer();
	// return mapper.getSourceRange(this);
	// }
	// }
	// return super.getSourceRange();
	// }

	// public IClassFile getClassFile() {
	// return ((JavaElement)getParent()).getClassFile();
	// }

	@Override
	protected char getHandleMementoDelimiter() {
		return ModelElement.JEM_TYPE_PARAMETER;
	}

	public ISourceRange getNameRange() throws ModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printNode(CorePrinter output) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void toStringName(StringBuffer buffer) {
		buffer.append('<');
		buffer.append(getElementName());
		buffer.append('>');
	}
}
