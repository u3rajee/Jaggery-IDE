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
package org.eclipse.dltk.mod.ui.preferences;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.ui.DLTKExecuteExtensionHelper;
import org.eclipse.dltk.mod.ui.DLTKUILanguageManager;
import org.eclipse.dltk.mod.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Abstract base class for script preference pages.
 * 
 * <p>
 * Preferenece pages that extend from this class will have their preference
 * values stored in the <code>IPreferenceStore</code> returned from the plugin's
 * {@link IDLTKUILanguageToolkit} implementation.
 * </p>
 * 
 * <p>
 * These pages will also need to be configured with the specific nature id for
 * the plugin in the <code>plugin.xml</code> file. ie:
 * </p>
 * 
 * <pre>
 * &lt;page 
 *   category=&quot;...&quot;
 *   class=&quot;class:nature_id&quot;
 *   id=&quot;...&quot;
 *   name=&quot;...&quot; /&gt;
 * </pre>
 */
public abstract class AbstractScriptPreferencePage extends
		AbstractConfigurationBlockPreferencePage implements
		IExecutableExtension {

	private IDLTKLanguageToolkit fToolkit;

	/*
	 * @see
	 * org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org
	 * .eclipse.core.runtime.IConfigurationElement, java.lang.String,
	 * java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		fToolkit = DLTKExecuteExtensionHelper.getLanguageToolkit(config,
				propertyName, data);

		Assert.isNotNull(fToolkit);
	}

	/*
	 * @see
	 * org.eclipse.dltk.mod.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #getHelpId()
	 */
	protected String getHelpId() {
		return null;
	}

	/*
	 * @see
	 * org.eclipse.dltk.mod.ui.preferences.AbstractConfigurationBlockPreferencePage
	 * #setPreferenceStore()
	 */
	protected final void setPreferenceStore() {
		IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
				.getLanguageToolkit(fToolkit.getNatureId());
		Assert.isNotNull(uiToolkit);

		IPreferenceStore store = uiToolkit.getPreferenceStore();

		Assert.isNotNull(store);
		setPreferenceStore(store);
	}

	protected final IDLTKLanguageToolkit getToolkit() {
		return fToolkit;
	}
}
