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
package org.eclipse.dltk.mod.internal.debug.core.model.operations;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.mod.internal.debug.core.model.operations.messages"; //$NON-NLS-1$
	public static String DbgpResumeOperation_resumeOperation;
	public static String DbgpStepIntoOperation_stepIntoOperation;
	public static String DbgpStepOverOperation_stepOverOperation;
	public static String DbgpStepReturnOperation_stepReturnOperation;
	public static String DbgpSuspendOperation_suspendOperation;
	public static String DbgpTerminateOperation_terminateOperation;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
