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
package org.eclipse.dltk.mod.debug.core;

import org.eclipse.debug.core.ILaunch;

/**
 * DLTK specific {@link org.eclipse.debug.core.ILaunch} attributes.
 */
public class DLTKDebugLaunchConstants {

	/**
	 * Boolean launch attribute to specify if DBGP console redirection should be
	 * used. Default value is <code>true</code>.
	 */
	public static final String ATTR_DEBUG_CONSOLE = DLTKDebugPlugin.PLUGIN_ID
			+ ".debugConsole";

	public static boolean isDebugConsole(ILaunch launch) {
		final String value = launch.getAttribute(ATTR_DEBUG_CONSOLE);
		return !"false".equals(value);
	}

}
