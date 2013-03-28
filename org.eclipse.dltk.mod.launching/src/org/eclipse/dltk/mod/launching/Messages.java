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
package org.eclipse.dltk.mod.launching;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.mod.launching.messages"; //$NON-NLS-1$
	public static String EnvironmentVariable_variableNameAndValueMustNotBeEmpty;
	public static String InternalScriptExecutor_iInterpreterInstallMustNotBeNull;
	public static String InternalScriptExecutor_iProcessHandlerMustNotBeNull;
	public static String InterpreterConfig_interpreterArgumentCannotBeNull;
	public static String InterpreterConfig_scriptArgumentCannotBeNull;
	public static String InterpreterConfig_scriptFileCannotBeNull;
	public static String InterpreterConfig_workingDirectoryCannotBeNull;
	public static String InterpreterSearcher_foundSearching;
	public static String ScriptLaunchConfigurationConstants_2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
