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
package org.eclipse.dltk.mod.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is extension, which is used in fileHierarchyResolver extension point.
 */
public interface IFileHierarchyResolver {

	/**
	 * Gathers information on all files, which are referenced by the given file either directly or through other files.
	 * @param file Source module to resolve file hierarchy information for
	 * @param monitor Progress monitor
	 * @return file hierarchy information
	 */
	public IFileHierarchyInfo resolveUp(ISourceModule file, IProgressMonitor monitor);

	/**
	 * Gathers information on all files that reference given file either directly or through other files.
	 * @param file Source module to resolve file hierarchy information for
	 * @param monitor Progress monitor
	 * @return file hierarchy information
	 */
	public IFileHierarchyInfo resolveDown(ISourceModule file, IProgressMonitor monitor);
}
