/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.ui;

import java.util.List;

import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.mod.ui.search.ISearchQueryFactory;
import org.eclipse.dltk.mod.ui.viewsupport.ScriptUILabelProvider;

public class DLTKUILanguageManager extends PriorityClassDLTKExtensionManager {
	private static DLTKUILanguageManager instance = new DLTKUILanguageManager();
	private final static String LANGUAGE_EXTPOINT = DLTKUIPlugin.PLUGIN_ID
			+ ".language"; //$NON-NLS-1$

	// EBAY - START MOD
	private final static String SEARCH_QUERY_FACTORY_EXTPOINT = DLTKUIPlugin.PLUGIN_ID
			+ ".searchQueryFactories"; //$NON-NLS-1$
	private static PriorityClassDLTKExtensionManager searchQueryFactoryManager = createManager(SEARCH_QUERY_FACTORY_EXTPOINT);

	private static PriorityClassDLTKExtensionManager createManager(String point) {
		return new PriorityClassDLTKExtensionManager(point);
	}

	// EBAY -- STOP MOD

	private DLTKUILanguageManager() {
		super(LANGUAGE_EXTPOINT);
	}

	public static IDLTKUILanguageToolkit[] getLanguageToolkits() {
		List toolkits = instance.getObjectList();
		return (IDLTKUILanguageToolkit[]) toolkits
				.toArray(new IDLTKUILanguageToolkit[toolkits.size()]);
	}

	public static IDLTKUILanguageToolkit getLanguageToolkit(String natureId) {
		return (IDLTKUILanguageToolkit) instance.getObject(natureId);
	}

	public static IDLTKUILanguageToolkit getLanguageToolkit(
			IModelElement element) {
		IDLTKLanguageToolkit coreToolkit = DLTKLanguageManager
				.getLanguageToolkit(element);
		if (coreToolkit != null) {
			return (IDLTKUILanguageToolkit) instance.getObject(coreToolkit
					.getNatureId());
		}
		return null;
	}

	public static ScriptUILabelProvider createLabelProvider(
			IModelElement element) {
		IDLTKUILanguageToolkit languageToolkit = getLanguageToolkit(element);
		if (languageToolkit != null) {
			ScriptUILabelProvider provider = languageToolkit
					.createScriptUILabelProvider();
			if (provider != null) {
				return provider;
			}
		}
		return new ScriptUILabelProvider();
	}

	public static ScriptUILabelProvider createLabelProvider(String nature) {
		IDLTKUILanguageToolkit languageToolkit = getLanguageToolkit(nature);
		if (languageToolkit != null) {
			ScriptUILabelProvider provider = languageToolkit
					.createScriptUILabelProvider();
			if (provider != null) {
				return provider;
			}
		}
		return new ScriptUILabelProvider();
	}

	public static IDLTKUILanguageToolkit getLanguageToolkitLower(String natureId) {
		return (IDLTKUILanguageToolkit) instance.getObjectLower(natureId);
	}

	// EBAY - START MOD
	public static ISearchQueryFactory getSearchQueryFactory(String natureId) {
		return (ISearchQueryFactory) getSearchQueryFactoryManager().getObject(
				natureId);
	}

	public static PriorityClassDLTKExtensionManager getSearchQueryFactoryManager() {
		return searchQueryFactoryManager;
	}
	// EBAY -- STOP MOD

}
