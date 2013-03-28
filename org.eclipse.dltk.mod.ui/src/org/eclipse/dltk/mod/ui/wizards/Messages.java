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
package org.eclipse.dltk.mod.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.mod.ui.wizards.messages"; //$NON-NLS-1$
	public static String GenericDLTKProjectWizard_createNewDltkProject;
	public static String GenericDLTKProjectWizard_natureMustBeSpecified;
	public static String GenericDLTKProjectWizard_newDltkProject;
	public static String NewSourceModulePage_file;
	public static String NewSourceModulePage_fileAlreadyExists;
	public static String NewSourceModulePage_typeAlreadyExists;
	public static String NewSourceModulePage_noFoldersAvailable;
	public static String NewSourceModulePage_pathCannotBeEmpty;
	public static String NewSourceModulePage_selectScriptFolder;
	public static String NewSourceModuleWizard_errorInOpenInEditor;
	// EBAY MOD
	public static String WorkingSetConfigurationBlock_WorkingSetText_name;
	public static String WorkingSetConfigurationBlock_SelectWorkingSet_button;

	public static String SimpleWorkingSetSelectionDialog_SimpleSelectWorkingSetDialog_title;
	public static String SimpleWorkingSetSelectionDialog_SelectAll_button;
	public static String SimpleWorkingSetSelectionDialog_DeselectAll_button;
	public static String SimpleWorkingSetSelectionDialog_New_button;
	// END EBAY MOD
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
