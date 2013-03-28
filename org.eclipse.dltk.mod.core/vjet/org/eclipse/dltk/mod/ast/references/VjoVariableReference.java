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
package org.eclipse.dltk.mod.ast.references;

import org.eclipse.dltk.mod.ast.references.VariableKind;
import org.eclipse.dltk.mod.ast.references.VariableReference;

public class VjoVariableReference extends VariableReference {

	private String declaringTypeName;
	
	public VjoVariableReference(int start, int end, String name,
			VariableKind kind, String declaringTypeName) {
		super(start, end, name, kind);
		this.declaringTypeName = declaringTypeName;
	}

	public String getDeclaringTypeName() {
		return declaringTypeName;
	}
}
