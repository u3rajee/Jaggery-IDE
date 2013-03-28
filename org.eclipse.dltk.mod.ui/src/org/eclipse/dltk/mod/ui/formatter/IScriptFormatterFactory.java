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
package org.eclipse.dltk.mod.ui.formatter;

import java.net.URL;
import java.util.Map;

import org.eclipse.dltk.mod.core.IDLTKContributedExtension;
import org.eclipse.dltk.mod.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.mod.core.IPreferencesSaveDelegate;
import org.eclipse.dltk.mod.ui.preferences.PreferenceKey;

/**
 * Script source code formatter factory interface.
 */
public interface IScriptFormatterFactory extends IDLTKContributedExtension {

	/**
	 * Retrieves the formatting options from the specified <code>delegate</code>
	 * 
	 * @param delegate
	 * @return
	 */
	Map retrievePreferences(IPreferencesLookupDelegate delegate);

	PreferenceKey[] getPreferenceKeys();

	void savePreferences(Map preferences, IPreferencesSaveDelegate delegate);

	/**
	 * Creates the {@link IScriptFormatter} with the specified preferences.
	 * 
	 * @param lineDelimiter
	 *            the line delimiter to use
	 * @param preferences
	 *            the formatting options
	 */
	IScriptFormatter createFormatter(String lineDelimiter, Map preferences);

	/**
	 * Validates that this formatter factory is correctly installed.
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * Return the preview content to use with this formatter or
	 * <code>null</code> if no preview is available.
	 * 
	 * @return
	 */
	URL getPreviewContent();

	/**
	 * @return
	 */
	IFormatterModifyDialog createDialog(IFormatterModifyDialogOwner dialogOwner);

}
