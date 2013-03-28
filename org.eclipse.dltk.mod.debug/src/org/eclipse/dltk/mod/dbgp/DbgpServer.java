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
package org.eclipse.dltk.mod.dbgp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.mod.dbgp.internal.DbgpDebugingEngine;
import org.eclipse.dltk.mod.dbgp.internal.DbgpSession;
import org.eclipse.dltk.mod.dbgp.internal.DbgpWorkingThread;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPlugin;

public class DbgpServer extends DbgpWorkingThread {
	private final int port;
	private ServerSocket server;

	private final int clientTimeout;

	public static int findAvailablePort(int fromPort, int toPort) {
		if (fromPort > toPort) {
			throw new IllegalArgumentException(
					Messages.DbgpServer_startPortShouldBeLessThanOrEqualToEndPort);
		}

		int port = fromPort;
		ServerSocket socket = null;
		while (port <= toPort) {
			try {
				socket = new ServerSocket(port);
				return port;
			} catch (IOException e) {
				++port;
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}

		return -1;
	}

	private static final int STATE_NONE = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_CLOSED = 2;

	private final Object stateLock = new Object();
	private int state = STATE_NONE;

	public boolean isStarted() {
		synchronized (stateLock) {
			return state == STATE_STARTED;
		}
	}

	public boolean waitStarted() {
		return waitStarted(15000);
	}

	public boolean waitStarted(long timeout) {
		synchronized (stateLock) {
			if (state == STATE_STARTED) {
				return true;
			} else if (state == STATE_CLOSED) {
				return false;
			}
			try {
				stateLock.wait(timeout);
			} catch (InterruptedException e) {
				// ignore
			}
			return state == STATE_STARTED;
		}
	}

	protected void workingCycle() throws Exception, IOException {
		try {
			server = new ServerSocket(port);
			synchronized (stateLock) {
				state = STATE_STARTED;
				stateLock.notifyAll();
			}
			while (!server.isClosed()) {
				final Socket client = server.accept();
				client.setSoTimeout(clientTimeout);
				createSession(client);
			}
		} finally {
			if (server != null && !server.isClosed()) {
				server.close();
			}
			synchronized (stateLock) {
				state = STATE_CLOSED;
				stateLock.notifyAll();
			}
		}
	}

	private void createSession(final Socket client) {
		Job job = new Job(
				Messages.DbgpServer_acceptingDebuggingEngineConnection) {
			protected IStatus run(IProgressMonitor monitor) {
				// copy to local variable to prevent NPE
				final IDbgpServerListener savedListener = listener;
				if (savedListener != null) {
					DbgpDebugingEngine dbgpDebugingEngine = null;
					DbgpSession session = null;

					try {
						dbgpDebugingEngine = new DbgpDebugingEngine(client);
						session = new DbgpSession(dbgpDebugingEngine);
						savedListener.clientConnected(session);
					} catch (Exception e) {
						DLTKDebugPlugin.log(e);
						if (dbgpDebugingEngine != null)
							dbgpDebugingEngine.requestTermination();
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public DbgpServer(int port, int clientTimeout) {
		super("DbgpServer"); //$NON-NLS-1$

		this.port = port;
		this.clientTimeout = clientTimeout;
	}

	/**
	 * @param port
	 * @param serverTimeout
	 * @param clientTimeout
	 * @deprecated use {@link #DbgpServer(int, int)}
	 */
	public DbgpServer(int port, int serverTimeout, int clientTimeout) {
		this(port, clientTimeout);
	}

	public void requestTermination() {
		try {
			if (server != null) {
				server.close();
			}
		} catch (IOException e) {
			DLTKDebugPlugin.log(e);
		}
		super.requestTermination();
	}

	private IDbgpServerListener listener;

	public void setListener(IDbgpServerListener listener) {
		this.listener = listener;
	}
}
