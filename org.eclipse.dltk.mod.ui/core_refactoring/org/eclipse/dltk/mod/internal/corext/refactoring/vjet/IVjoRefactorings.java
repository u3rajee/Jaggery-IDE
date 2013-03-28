/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.corext.refactoring.vjet;

import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;

/**
 * Interface for refactoring ids offered by the JDT tooling.
 * <p>
 * This interface provides refactoring ids for refactorings offered by the JDT
 * tooling. Refactoring instances corresponding to such an id may be
 * instantiated by the refactoring framework using
 * {@link RefactoringCore#getRefactoringContribution(String)}. The resulting
 * refactoring instance may be executed on the workspace with a
 * {@link PerformRefactoringOperation}.
 * <p>
 * Clients may obtain customizable refactoring descriptors for a certain
 * refactoring by calling
 * {@link RefactoringCore#getRefactoringContribution(String)} with the
 * appropriate refactoring id and then calling
 * {@link RefactoringContribution#createDescriptor()} to obtain a customizable
 * refactoring descriptor. The concrete subtype of refactoring descriptors is
 * dependent from the <code>id</code> argument.
 * </p>
 * <p>
 * Note: this interface is not intended to be implemented by clients.
 * </p>
 * 
 * @since 3.3
 */
public interface IVjoRefactorings {

	/**
	 * Refactoring id of the 'Rename Type' refactoring (value:
	 * <code>org.eclipse.jdt.ui.rename.type</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * 
	 * </p>
	 */
	public static final String RENAME_TYPE = "org.eclipse.vjet.eclipse.ui.rename.type"; //$NON-NLS-1$

	public static final String RENAME_SOURCE_MODULE = "org.eclipse.vjet.eclipse.ui.rename.sourcemodule"; //$NON-NLS-1$

	public static final String RENAME_SCRIPT_FOLDER = "org.eclipse.vjet.eclipse.ui.rename.scriptfolder";

}