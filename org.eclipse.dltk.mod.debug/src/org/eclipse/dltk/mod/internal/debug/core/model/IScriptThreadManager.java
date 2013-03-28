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
package org.eclipse.dltk.mod.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.dltk.mod.dbgp.IDbgpThreadAcceptor;
import org.eclipse.dltk.mod.debug.core.model.IScriptDebugThreadConfigurator;
import org.eclipse.dltk.mod.debug.core.model.IScriptThread;
import org.eclipse.dltk.mod.internal.debug.core.model.operations.DbgpDebugger;

public interface IScriptThreadManager extends IDbgpThreadAcceptor, ITerminate,
		ISuspendResume {

	// Listener
	void addListener(IScriptThreadManagerListener listener);

	void removeListener(IScriptThreadManagerListener listener);

	// Thread management
	boolean hasThreads();

	IScriptThread[] getThreads();

	void terminateThread(IScriptThread thread);

	boolean isWaitingForThreads();

	void sendTerminationRequest() throws DebugException;

	public void refreshThreads();

	/**
	 * Used to configure thread with additional DBGp features, etc.
	 */
	void configureThread(DbgpDebugger engine, ScriptThread scriptThread);

	public void setScriptThreadConfigurator(
			IScriptDebugThreadConfigurator configurator);
}
