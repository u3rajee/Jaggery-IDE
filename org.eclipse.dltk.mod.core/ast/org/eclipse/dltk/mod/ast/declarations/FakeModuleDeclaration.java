/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.ast.declarations;

public class FakeModuleDeclaration extends ModuleDeclaration {

	/**
	 * @param sourceLength
	 * @param rebuildEnabled
	 */
	public FakeModuleDeclaration(int sourceLength, boolean rebuildEnabled) {
		super(sourceLength, rebuildEnabled);
	}

	/**
	 * @param sourceLength
	 */
	public FakeModuleDeclaration(int sourceLength) {
		super(sourceLength);
	}

}
