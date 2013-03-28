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

import org.eclipse.dltk.mod.core.IPreferencesLookupDelegate;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreLookupDelegate implements
		IPreferencesLookupDelegate {

	private IPreferenceStore store;

	public PreferenceStoreLookupDelegate(IPreferenceStore store) {
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
