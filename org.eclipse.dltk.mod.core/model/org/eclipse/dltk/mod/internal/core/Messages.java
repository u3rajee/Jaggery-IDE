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
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.mod.internal.core.messages"; //$NON-NLS-1$
	public static String Model_invalidResourceForTheProject;
	public static String ModelOperation_operationCancelled;
	public static String refreshing_external_folders;
	public static String Openable_completionRequesterCannotBeNull;
	public static String UserLibraryBuildpathContainerInitializer_dltkLanguageToolkitIsNull;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
