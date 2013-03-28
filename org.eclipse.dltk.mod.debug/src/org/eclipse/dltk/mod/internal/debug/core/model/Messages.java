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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.mod.internal.debug.core.model.messages"; //$NON-NLS-1$
	public static String HotCodeReplaceManager_hotCodeReplaceProviderForNotFound;
	public static String ScriptStackFrame_classVariables;
	public static String ScriptStackFrame_globalVariables;
	public static String ScriptStackFrame_stackFrame;
	public static String ScriptStackFrame_unableToLoadVariables;
	public static String ScriptValue_detailFormatterRequiredToContainIdentifier;
	public static String ScriptValue_unableToLoadChildrenOf;
	public static String ScriptVariable_cantAssignVariable;
	public static String DbgpService_ServerRestart;

	public static String AvailableChildrenExceedsVariableLength;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ErrorSetupDeferredBreakpoints;

	private Messages() {
	}
}
