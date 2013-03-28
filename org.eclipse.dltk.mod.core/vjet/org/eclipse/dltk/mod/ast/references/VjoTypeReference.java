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

import org.eclipse.dltk.mod.ast.references.TypeReference;

public class VjoTypeReference extends TypeReference {

	private String m_packageName;
	
	public VjoTypeReference(int start, int end, String name) {
		super(start, end, name);
	}

	public String getPackageName() {
		return m_packageName;
	}

	public void setPackageName(String packageName) {
		this.m_packageName = packageName;
	}

}
