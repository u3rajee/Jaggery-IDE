/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp.internal;

import java.io.IOException;

import org.eclipse.dltk.mod.dbgp.IDbgpNotificationManager;
import org.eclipse.dltk.mod.dbgp.IDbgpRawListener;
import org.eclipse.dltk.mod.dbgp.IDbgpSession;
import org.eclipse.dltk.mod.dbgp.IDbgpSessionInfo;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpCoreCommands;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpExtendedCommands;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpSpawnpointCommands;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.mod.dbgp.internal.commands.DbgpCoreCommands;
import org.eclipse.dltk.mod.dbgp.internal.commands.DbgpDebuggingEngineCommunicator;
import org.eclipse.dltk.mod.dbgp.internal.commands.DbgpExtendedCommands;
import org.eclipse.dltk.mod.dbgp.internal.commands.DbgpSpawnpointCommands;
import org.eclipse.dltk.mod.dbgp.internal.commands.IDbgpCommunicator;
import org.eclipse.dltk.mod.dbgp.internal.managers.DbgpNotificationManager;
import org.eclipse.dltk.mod.dbgp.internal.managers.DbgpStreamManager;
import org.eclipse.dltk.mod.dbgp.internal.managers.IDbgpStreamManager;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpResponsePacket;
import org.eclipse.dltk.mod.dbgp.internal.utils.DbgpXmlEntityParser;
import org.eclipse.dltk.mod.debug.core.IDebugOptions;
import org.eclipse.dltk.mod.debug.core.model.DefaultDebugOptions;

public class DbgpSession extends DbgpTermination implements IDbgpSession,
		IDbgpTerminationListener {
	private final IDbgpDebugingEngine engine;

	private final IDbgpCoreCommands coreCommands;

	private final IDbgpExtendedCommands extendedCommands;
	private final IDbgpSpawnpointCommands spawnpointCommands;

	private final DbgpNotificationManager notificationManager;

	private final DbgpStreamManager streamManager;

	private IDbgpSessionInfo info;

	private final Object terminatedLock = new Object();
	private boolean terminated = false;

	private DbgpDebuggingEngineCommunicator communicator;

	private void requestTerminateImpl(Object object) {
		if (object != engine) {
			engine.requestTermination();
		}

		if (object != streamManager && streamManager != null) {
			streamManager.requestTermination();
		}

		if (object != notificationManager && notificationManager != null) {
			notificationManager.requestTermination();
		}
	}

	private void waitTerminatedImpl(Object object) throws InterruptedException {
		if (object != engine) {
			engine.waitTerminated();
		}

		if (streamManager != null && object != streamManager) {
			streamManager.waitTerminated();
		}

		if (notificationManager != null && object != notificationManager) {
			notificationManager.waitTerminated();
		}
	}

	public DbgpSession(IDbgpDebugingEngine engine) throws DbgpException,
			IOException {
		if (engine == null) {
			throw new IllegalArgumentException();
		}

		this.engine = engine;

		try {
			DbgpResponsePacket responsePacket = engine.getResponsePacket(-1, 0);
			if (responsePacket == null) {
				throw new DbgpException();
			}
			info = DbgpXmlEntityParser
					.parseSession(responsePacket.getContent());
		} catch (InterruptedException e) {
		}

		// Engine
		this.engine.addTerminationListener(this);

		// Notification manager
		this.notificationManager = new DbgpNotificationManager(engine);
		this.notificationManager.addTerminationListener(this);

		// Stream manager
		this.streamManager = new DbgpStreamManager(engine,
				"DBGP - Stream manager"); //$NON-NLS-1$
		this.streamManager.addTerminationListener(this);

		communicator = new DbgpDebuggingEngineCommunicator(engine,
				DefaultDebugOptions.getDefaultInstance());

		this.coreCommands = new DbgpCoreCommands(communicator);
		this.extendedCommands = new DbgpExtendedCommands(communicator);
		this.spawnpointCommands = new DbgpSpawnpointCommands(communicator, this);

		// Starting all
		this.notificationManager.start();
		this.streamManager.start();
	}

	public IDbgpSessionInfo getInfo() {
		return info;
	}

	public String toString() {
		return "Session. appid: " + info.getApplicationId(); //$NON-NLS-1$
	}

	public IDbgpCoreCommands getCoreCommands() {
		return coreCommands;
	}

	public IDbgpExtendedCommands getExtendedCommands() {
		return extendedCommands;
	}

	public IDbgpStreamManager getStreamManager() {
		return streamManager;
	}

	public IDbgpNotificationManager getNotificationManager() {
		return notificationManager;
	}

	// IDbgpTermination
	public void requestTermination() {
		synchronized (terminatedLock) {
			if (terminated) {
				return;
			}

			requestTerminateImpl(null);
		}
	}

	public void waitTerminated() throws InterruptedException {
		synchronized (terminatedLock) {
			if (terminated) {
				return;
			}

			waitTerminatedImpl(null);
		}
	}

	// IDbgpTerminationListener
	public void objectTerminated(Object object, Exception e) {
		// Allows to unblock all terminating threads
		synchronized (terminatedLock) {
			if (terminated)
				return;
			terminated = true;
		}

		engine.removeTerminationListener(this);
		if (streamManager != null) {
			streamManager.removeTerminationListener(this);
		}
		if (notificationManager != null) {
			notificationManager.removeTerminationListener(this);
		}

		// Request terminate
		requestTerminateImpl(object);

		try {
			waitTerminatedImpl(object);
		} catch (InterruptedException ex) {
			// OK, interrrputed
		}
		fireObjectTerminated(e);
	}

	public void addRawListener(IDbgpRawListener listener) {
		engine.addRawListener(listener);

	}

	public void removeRawListenr(IDbgpRawListener listener) {
		engine.removeRawListenr(listener);
	}

	public IDbgpCommunicator getCommunicator() {
		return this.communicator;
	}

	public void configure(IDebugOptions debugOptions) {
		communicator.configure(debugOptions);
	}

	public Object get(Class type) {
		if (type == IDbgpSpawnpointCommands.class) {
			return spawnpointCommands;
		} else if (type == IDbgpCoreCommands.class) {
			return coreCommands;
		} else if (type == IDbgpExtendedCommands.class) {
			return extendedCommands;
		}
		return null;
	}
}
