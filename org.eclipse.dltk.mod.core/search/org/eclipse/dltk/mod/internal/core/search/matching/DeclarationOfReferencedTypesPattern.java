/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search.matching;

import org.eclipse.dltk.mod.compiler.util.SimpleSet;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IModelElement;

public class DeclarationOfReferencedTypesPattern extends TypeReferencePattern {
	protected SimpleSet knownTypes;
	protected IModelElement enclosingElement;

	public DeclarationOfReferencedTypesPattern(IModelElement enclosingElement) {
		super(null, null, R_PATTERN_MATCH, DLTKLanguageManager
				.getLanguageToolkit(enclosingElement));
		this.enclosingElement = enclosingElement;
	}
}
