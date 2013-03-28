/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *     Erling Ellingsen -  patch for bug 125570
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.compiler.lookup;

import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.compiler.env.lookup.Scope;

public class SourceModuleScope extends Scope {
	public LookupEnvironment environment;

	public ModuleDeclaration referenceContext;

	public SourceModuleScope(ModuleDeclaration unit,
			LookupEnvironment environment) {
		super(COMPILATION_UNIT_SCOPE, null);
		
		this.referenceContext = unit;
		this.environment = environment;
		
		unit.scope = this;
	}
}
