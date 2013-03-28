/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.dbgp.IDbgpRawPacket;
import org.eclipse.dltk.mod.dbgp.IDbgpRawListener;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpNotifyPacket;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpPacketReceiver;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpPacketSender;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpResponsePacket;
import org.eclipse.dltk.mod.dbgp.internal.packets.DbgpStreamPacket;
import org.eclipse.dltk.mod.dbgp.internal.packets.IDbgpRawLogger;
import org.eclipse.dltk.mod.debug.core.ExtendedDebugEventDetails;
import org.eclipse.dltk.mod.internal.debug.core.model.DebugEventHelper;

public class DbgpDebugingEngine extends DbgpTermination implements
		IDbgpDebugingEngine, IDbgpTerminationListener {
	private final Socket socket;

	private final DbgpPacketReceiver receiver;

	private final DbgpPacketSender sender;

	private final Object terminatedLock = new Object();
	private boolean terminated = false;

	private final int id;

	private static int lastId = 0;
	private static final Object idLock = new Object();

	public DbgpDebugingEngine(Socket socket) throws IOException {
		this.socket = socket;
		synchronized (idLock) {
			id = ++lastId;
		}

		receiver = new DbgpPacketReceiver(new BufferedInputStream(socket
				.getInputStream()));

		receiver.setLogger(new IDbgpRawLogger() {
			public void log(IDbgpRawPacket output) {
				firePacketReceived(output);
			}
		});

		receiver.addTerminationListener(this);

		receiver.start();

		sender = new DbgpPacketSender(new BufferedOutputStream(socket
				.getOutputStream()));

		sender.setLogger(new IDbgpRawLogger() {
			public void log(IDbgpRawPacket output) {
				firePacketSent(output);
			}
		});
		/*
		 * FIXME this event is delivered on the separate thread, so sometimes
		 * logging misses a few initial packets.
		 */
		DebugEventHelper.fireExtendedEvent(this,
				ExtendedDebugEventDetails.DGBP_NEW_CONNECTION);
	}

	public DbgpStreamPacket getStreamPacket() throws IOException,
			InterruptedException {
		return receiver.getStreamPacket();
	}

	public DbgpNotifyPacket getNotifyPacket() throws IOException,
			InterruptedException {
		return receiver.getNotifyPacket();
	}

	public DbgpResponsePacket getResponsePacket(int transactionId, int timeout)
			throws IOException, InterruptedException {
		return receiver.getResponsePacket(transactionId, timeout);
	}

	public void sendCommand(DbgpRequest command) throws IOException {
		sender.sendCommand(command);
	}

	// IDbgpTerminataion
	public void requestTermination() {
		// always just close the socket
		try {
			socket.close();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public void waitTerminated() throws InterruptedException {
		synchronized (terminatedLock) {
			if (terminated) {
				return;
			}

			receiver.waitTerminated();
		}
	}

	public void objectTerminated(Object object, Exception e) {
		synchronized (terminatedLock) {
			if (terminated)
				return;

			receiver.removeTerminationListener(this);
			try {
				receiver.waitTerminated();
			} catch (InterruptedException e1) {
				// OK, interrupted
			}

			terminated = true;
		}

		fireObjectTerminated(e);
	}

	private final ListenerList listeners = new ListenerList();

	protected void firePacketReceived(IDbgpRawPacket content) {
		Object[] list = listeners.getListeners();

		for (int i = 0; i < list.length; ++i) {
			((IDbgpRawListener) list[i]).dbgpPacketReceived(id, content);
		}
	}

	protected void firePacketSent(IDbgpRawPacket content) {
		Object[] list = listeners.getListeners();

		for (int i = 0; i < list.length; ++i) {
			((IDbgpRawListener) list[i]).dbgpPacketSent(id, content);
		}
	}

	public void addRawListener(IDbgpRawListener listener) {
		listeners.add(listener);
	}

	public void removeRawListenr(IDbgpRawListener listener) {
		listeners.remove(listener);
	}
}
