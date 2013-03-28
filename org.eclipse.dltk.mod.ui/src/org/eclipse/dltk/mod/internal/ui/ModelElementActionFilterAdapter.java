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
package org.eclipse.dltk.mod.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.ui.actions.IActionFilterTester;
import org.eclipse.ui.IActionFilter;

public class ModelElementActionFilterAdapter implements IActionFilter {
	private final static String ACTION_FILTER_TESTER = "org.eclipse.dltk.mod.ui.actionFilterTester"; //$NON-NLS-1$
	private static IdBasedExtensionManager actionFilterTesters = new IdBasedExtensionManager(ACTION_FILTER_TESTER);
	public boolean testAttribute(Object target, String name, String value) {
		try {
			IActionFilterTester tester = actionFilterTesters.getObject(name);
			if( tester == null ) {
				return false;
			}
			return tester.test(target, name, value);
		} catch (CoreException e) {
			if( DLTKCore.DEBUG ) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
