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
package org.eclipse.dltk.mod.ui;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;

public class DLTKExecuteExtensionHelper {

	public static String getNatureId(IConfigurationElement config,
			String propertyName, Object data) {
		if (data instanceof String) {
			return (String) data;
		}

		if (data instanceof Map) {
			return (String) ((Map) data).get("nature"); //$NON-NLS-1$
		}

		throw new RuntimeException(
				Messages.DLTKExecuteExtensionHelper_natureAttributeMustBeSpecifiedAndCorrect);
	}

	public static IDLTKLanguageToolkit getLanguageToolkit(
			IConfigurationElement config, String propertyName, Object data) {
		String nature = getNatureId(config, propertyName, data);
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(nature);

		if (toolkit == null) {
			throw new RuntimeException(
					Messages.DLTKExecuteExtensionHelper_natureAttributeMustBeSpecifiedAndCorrect);
		}
		return toolkit;
	}
}
