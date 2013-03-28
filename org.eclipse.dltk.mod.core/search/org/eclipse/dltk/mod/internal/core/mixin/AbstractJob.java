/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.mixin;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.internal.core.search.processing.IJob;
import org.eclipse.dltk.mod.internal.core.util.Util;

public abstract class AbstractJob implements IJob {

	protected static final boolean DEBUG = false;

	protected boolean isCancelled = false;

	public boolean belongsTo(String jobFamily) {
		return false;
	}

	public void cancel() {
		isCancelled = true;
	}

	// private Exception constructedAt;

	public void ensureReadyToRun() {
		if (DEBUG) {
			// constructedAt = new Exception();
		}
	}

	protected abstract String getName();

	protected void log(Object message) {
		System.out.println('[' + toString() + "] " + message); //$NON-NLS-1$
	}

	private final String getShortClassName() {
		final String name = getClass().getName();
		final int pos = name.lastIndexOf('.');
		return pos >= 0 ? name.substring(pos + 1) : name;
	}

	private String savedName;

	public String toString() {
		final String shortClassName = getShortClassName();
		if (savedName == null) {
			try {
				savedName = getName();
			} catch (Exception e) {
				savedName = "<Unknown>"; //$NON-NLS-1$
				Util.log(e, getShortClassName() + " getName() error"); //$NON-NLS-1$
			}
		}
		return shortClassName + '|' + savedName;
	}

	public final boolean execute(IProgressMonitor monitor) {
		try {
			final long startTime = DEBUG ? System.currentTimeMillis() : 0;
			if (DEBUG) {
				log("BEGIN"); //$NON-NLS-1$
			}
			run();
			if (DEBUG) {
				log("END " + (System.currentTimeMillis() - startTime) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (Exception e) {
			if (DEBUG) {
				DLTKCore.error(toString() + " error", e); //$NON-NLS-1$
				e.printStackTrace();
				// DLTKCore.error("request was created at", constructedAt);
			}
			return FAILED;
		}
		return COMPLETE;
	}

	protected abstract void run() throws CoreException, IOException;

}
