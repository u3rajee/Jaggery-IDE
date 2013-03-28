package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;

public interface IHelpContextIds {
	public static final String PREFIX = JaggeryUIPlugin.PLUGIN_ID + '.';

	// Wizard pages
	public static final String NEW_PROJECT = PREFIX
			+ "new_jaggeryproject_wizard_page_context"; //$NON-NLS-1$
	public static final String NEW_PACKAGE = PREFIX
			+ "new_package_wizard_page_context"; //$NON-NLS-1$
	public static final String NEW_ITYPE = PREFIX
			+ "new_interface_wizard_page_context"; //$NON-NLS-1$
	public static final String NEW_SOURCEFOLDER = PREFIX
			+ "new_sourcefolder_wizard_page_context"; //$NON-NLS-1$
	
	public static final String OUTLINE_VIEW = PREFIX
	+ "show_jaggery_outline_action"; //$NON-NLS-1$
	public static final String SCRIPT_EXPLORER_VIEW = PREFIX
	+ "script_explorer_view_context"; //$NON-NLS-1$	
	public static final String Hierarchy_VIEW = PREFIX
	+ "hierarchy_view_context"; //$NON-NLS-1$	
	
	
	public static final String JAGGERY_EDITOR = PREFIX
	+ "jaggery_editor_context"; //$NON-NLS-1$

}
