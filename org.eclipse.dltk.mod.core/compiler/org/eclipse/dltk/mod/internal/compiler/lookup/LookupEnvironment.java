/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.compiler.lookup;

import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.compiler.env.INameEnvironment;
import org.eclipse.dltk.mod.internal.compiler.env.AccessRestriction;
import org.eclipse.dltk.mod.internal.compiler.impl.ITypeRequestor;

public class LookupEnvironment {
	public LookupEnvironment(ITypeRequestor typeRequestor,
			INameEnvironment nameEnvironment) {
	}
	
	public void buildTypeScope(ModuleDeclaration unit,
			AccessRestriction accessRestriction) {
		//TODO: fix this. Don't simply remove this line!!!
		/* SourceModuleScope scope = */new SourceModuleScope(unit, this);		
	}
}
