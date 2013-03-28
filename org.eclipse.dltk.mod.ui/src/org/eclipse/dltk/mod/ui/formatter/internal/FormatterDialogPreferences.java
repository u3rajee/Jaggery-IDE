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
package org.eclipse.dltk.mod.ui.formatter.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.mod.compiler.util.Util;
import org.eclipse.dltk.mod.ui.preferences.IPreferenceDelegate;

public class FormatterDialogPreferences implements IPreferenceDelegate {

	private final Map preferences = new HashMap();

	public String getString(Object key) {
		final String value = (String) preferences.get(key);
		return value != null ? value : Util.EMPTY_STRING;
	}

	public boolean getBoolean(Object key) {
		return Boolean.valueOf(getString(key)).booleanValue();
	}

	public void setString(Object key, String value) {
		preferences.put(key, value);
	}

	public void setBoolean(Object key, boolean value) {
		setString(key, String.valueOf(value));
	}

	/**
	 * @return
	 */
	public Map get() {
		return Collections.unmodifiableMap(preferences);
	}

	/**
	 * @param prefs
	 */
	public void set(Map prefs) {
		preferences.clear();
		if (prefs != null) {
			preferences.putAll(prefs);
		}
	}

}
