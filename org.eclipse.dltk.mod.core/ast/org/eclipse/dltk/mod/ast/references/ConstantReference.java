/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ast.references;

import org.eclipse.dltk.mod.ast.DLTKToken;

public class ConstantReference extends SimpleReference {
	public ConstantReference(DLTKToken token) {
		super(token);
	}

	public ConstantReference(int start, int end, String name) {
		super(start, end, name);
	}
	

}
