/*******************************************************************************
 * Copyright (c) 2012 eBay Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.editor;

import java.io.File;

import org.eclipse.dltk.mod.core.IModelElement;

/**
 * get File from IModelElement, it can be used to support VjoEditor to open
 * none-source vjo type
 * 
 * @author jianliu
 * 
 */
public interface IModelElementFileAdivsor {

	/**
	 * Resolve the element into File
	 * 
	 * @param element
	 * @return
	 */
	File getFile(IModelElement element);

	/**
	 * The type which advisor supports
	 * 
	 * @return
	 */
	String getSupportedType();

}
