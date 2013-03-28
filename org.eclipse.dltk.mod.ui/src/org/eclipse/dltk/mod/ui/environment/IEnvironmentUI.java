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
package org.eclipse.dltk.mod.ui.environment;

import org.eclipse.swt.widgets.Shell;

public interface IEnvironmentUI {
	public static final int DEFAULT = 0;
	public static final int EXECUTABLE = 1;
	public static final int ARCHIVE = 2;

	/**
	 * Open directory selection dialog. Dialog allow creation of new
	 * directories.
	 */
	String selectFolder(Shell shell);

	/**
	 * Open file selection dialog.
	 */
	String selectFile(Shell shell, int executable2);

}
