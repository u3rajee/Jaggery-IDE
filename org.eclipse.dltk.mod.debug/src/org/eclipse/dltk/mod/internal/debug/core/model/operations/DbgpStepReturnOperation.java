/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.debug.core.model.operations;

import org.eclipse.dltk.mod.dbgp.IDbgpStatus;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.mod.debug.core.model.IScriptThread;

public class DbgpStepReturnOperation extends DbgpStepOperation {
	private static final String JOB_NAME = Messages.DbgpStepReturnOperation_stepReturnOperation;

	public DbgpStepReturnOperation(IScriptThread thread, IResultHandler finish) {
		super(thread, JOB_NAME, finish);
	}

	protected IDbgpStatus step() throws DbgpException {
		return getCore().stepOut();
	}
}
