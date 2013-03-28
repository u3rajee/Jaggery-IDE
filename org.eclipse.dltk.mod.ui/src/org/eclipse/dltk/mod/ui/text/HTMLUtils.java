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
package org.eclipse.dltk.mod.ui.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HTMLUtils {

	private static RGB BG_COLOR_RGB = null;

	static {
		final Display display = Display.getDefault();
		if (display != null && !display.isDisposed()) {
			try {
				display.asyncExec(new Runnable() {
					/*
					 * @see java.lang.Runnable#run()
					 */
					public void run() {
						BG_COLOR_RGB = display.getSystemColor(
								SWT.COLOR_INFO_BACKGROUND).getRGB();
					}
				});
			} catch (SWTError err) {
				// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=45294
				if (err.code != SWT.ERROR_DEVICE_DISPOSED)
					throw err;
			}
		}
	}

	public static RGB getBgColor() {
		if (BG_COLOR_RGB != null) {
			return BG_COLOR_RGB;
		} else {
			// RGB value of info bg color on WindowsXP
			return new RGB(255, 255, 225);
		}
	}

}
