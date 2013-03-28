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

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.mod.ast.parser.SourceParserManager;
import org.eclipse.dltk.mod.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.mod.ui.util.IStatusChangeListener;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public abstract class AbstractSourceParserOptionsBlock extends
		ContributedExtensionOptionsBlock {

	public AbstractSourceParserOptionsBlock(IStatusChangeListener context,
			IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	/*
	 * @see org.eclipse.dltk.mod.ui.preferences.ContributedExtensionOptionsBlock#getExtensionManager()
	 */
	protected DLTKContributionExtensionManager getExtensionManager() {
		return SourceParserManager.getInstance();
	}

	/*
	 * @see org.eclipse.dltk.mod.ui.preferences.ContributedExtensionOptionsBlock#getSelectorGroupLabel()
	 */
	protected String getSelectorGroupLabel() {
		return PreferencesMessages.SourceParsers_groupLabel;
	}

	/*
	 * @see org.eclipse.dltk.mod.ui.preferences.ContributedExtensionOptionsBlock#getSelectorNameLabel()
	 */
	protected String getSelectorNameLabel() {
		return PreferencesMessages.SourceParsers_nameLabel;
	}

	/*
	 * @see org.eclipse.dltk.mod.ui.preferences.ContributedExtensionOptionsBlock#getPreferenceLinkMessage()
	 */
	protected String getPreferenceLinkMessage() {
		return PreferencesMessages.SourceParsers_LinkToPreferences;
	}

}
