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
package org.eclipse.dltk.mod.core;

import org.eclipse.core.runtime.Preferences;

public class PluginPreferencesLookupDelegate implements
		IPreferencesLookupDelegate {

	private Preferences store;

	public PluginPreferencesLookupDelegate(Preferences store) {
		this.store = store;
	}

	public boolean getBoolean(String qualifier, String key) {
		return store.getBoolean(key);
	}

	public int getInt(String qualifier, String key) {
		return store.getInt(key);
	}

	public String getString(String qualifier, String key) {
		return store.getString(key);
	}
}
