/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.debug.core.model.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.dbgp.IDbgpStatus;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpCommands;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpCoreCommands;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpExtendedCommands;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpOpertionCanceledException;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpTimeoutException;
import org.eclipse.dltk.mod.debug.core.model.IScriptThread;

public abstract class DbgpOperation {
	private static final boolean DEBUG = DLTKCore.DEBUG;

	public interface IResultHandler {
		void finish(IDbgpStatus status, DbgpException e);
	}

	private final Job job;
	private final IDbgpCommands commands;

	protected IDbgpCoreCommands getCore() {
		return commands.getCoreCommands();
	}

	protected IDbgpExtendedCommands getExtended() {
		return commands.getExtendedCommands();
	}

	private final IResultHandler resultHandler;

	protected void callFinish(IDbgpStatus status) {
		if (DEBUG) {
			System.out.println("Status: " + status); //$NON-NLS-1$
		}

		resultHandler.finish(status, null);
	}

	protected DbgpOperation(IScriptThread thread, String name,
			IResultHandler handler) {
		this.resultHandler = handler;

		this.commands = thread.getDbgpSession();

		job = new Job(name) {
			protected IStatus run(IProgressMonitor monitor) {
				// TODO: improve
				try {
					process();
				} catch (DbgpOpertionCanceledException e) {
					// Operation was canceled cause debugger is shutting down
				} catch (DbgpTimeoutException e) {
					System.err.println("Timeout exception!!!"); //$NON-NLS-1$
					e.printStackTrace();
				} catch (DbgpException e) {
					if (DLTKCore.DEBUG) {
						System.out.println("Exception: " + e.getMessage()); //$NON-NLS-1$
						System.out.println(e.getClass());
						e.printStackTrace();
					}
					resultHandler.finish(null, e);
				}

				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setUser(false);
	}

	public void schedule() {
		if (DEBUG) {
			System.out.println("Starting operation: " + job.getName()); //$NON-NLS-1$
		}

		job.schedule();
	}

	protected abstract void process() throws DbgpException;
}
