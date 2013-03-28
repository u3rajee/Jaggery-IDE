/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp;

import java.net.URI;

public interface IDbgpStackLevel {
	int getLevel();

	int getLineNumber();

	int getBeginLine();

	int getBeginColumn();

	int getEndLine();

	int getEndColumn();

	URI getFileURI();

	String getWhere();

	boolean isSameMethod(IDbgpStackLevel other);
}
