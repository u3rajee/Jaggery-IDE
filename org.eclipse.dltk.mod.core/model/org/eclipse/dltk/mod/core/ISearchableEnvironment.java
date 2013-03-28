/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.core;

import org.eclipse.dltk.mod.compiler.env.INameEnvironment;
import org.eclipse.dltk.mod.internal.core.NameLookup;

public interface ISearchableEnvironment extends INameEnvironment {
	public NameLookup getNameLookup();
}
