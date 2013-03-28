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
package org.eclipse.dltk.mod.ui.templates;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

public abstract class ScriptTemplateAccess {

	private ContributionContextTypeRegistry fRegistry;
	private TemplateStore fStore;

	public TemplateStore getTemplateStore()
	{
		if (fStore == null) 
		{
			fStore = new ContributionTemplateStore(getContextTypeRegistry(),
					getPreferenceStore(), getCustomTemplatesKey());
			loadTemplates();
		}
		
		return fStore;
	}
	
	public ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			fRegistry = new ContributionContextTypeRegistry();
			fRegistry.addContextType(getContextTypeId());
		}
		
		return fRegistry;
	}
	
	protected abstract String getContextTypeId();
	protected abstract String getCustomTemplatesKey();
	protected abstract IPreferenceStore getPreferenceStore();
	
	private void loadTemplates() {
		try {
			fStore.load();
		} catch (IOException e) {
			DLTKUIPlugin.logErrorMessage(MessageFormat.format(TemplateMessages.ScriptTemplateAccess_unableToLoadTemplateStore, new Object[] { e }));
		}
	}
}
