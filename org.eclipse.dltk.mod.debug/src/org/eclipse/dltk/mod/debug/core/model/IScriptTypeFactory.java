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
package org.eclipse.dltk.mod.debug.core.model;

public interface IScriptTypeFactory {

	static String STRING = "string"; //$NON-NLS-1$
	static String ARRAY = "array"; //$NON-NLS-1$
	static String HASH = "hash"; //$NON-NLS-1$

	IScriptType buildType(String type);
}
