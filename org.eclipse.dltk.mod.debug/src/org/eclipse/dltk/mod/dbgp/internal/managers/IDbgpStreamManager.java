/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp.internal.managers;

import org.eclipse.dltk.mod.dbgp.IDbgpContinuationHandler;

public interface IDbgpStreamManager {
	void addListener(IDbgpContinuationHandler listener);

	void removeListener(IDbgpContinuationHandler listener);
}
