/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search;

import org.eclipse.dltk.mod.core.IModelElementDelta;
import org.eclipse.dltk.mod.core.search.IDLTKSearchScope;

public abstract class AbstractSearchScope implements IDLTKSearchScope {

	/*
	 * (non-Javadoc) Process the given delta and refresh its internal state if
	 * needed. Returns whether the internal state was refreshed.
	 */
	public abstract void processDelta(IModelElementDelta delta);

}
