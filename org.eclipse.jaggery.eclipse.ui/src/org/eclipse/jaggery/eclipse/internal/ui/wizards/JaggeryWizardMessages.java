package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class JaggeryWizardMessages {
	
private static final String BUNDLE_NAME = "org.eclipse.jaggery.eclipse.internal.ui.wizards.JaggeryWizardMessages";//$NON-NLS-1$

	
//	public static String FileCreationWizard_title;
//
	public static String ProjectCreationWizard_title;
	public static String ProjectCreationWizardFirstPage_description;
	public static String ProjectCreationWizardFirstPage_title;
//	public static String WizardPage_modifiers_public;
//	public static String WizardPage_modifiers_private;
//	public static String WizardPage_modifiers_protected;
//	public static String WizardPage_modifiers_default;
//	public static String WizardPage_modifiers_abstract;
//	public static String WizardPage_modifiers_final;
//	public static String WizardPage_modifiers_static;
//	public static String FileCreationWizard_page_title;
//	public static String FileCreationWizard_page_description;
//	public static String ClassCreationWizard_page_title;
//	public static String ClassCreationWizard_page_description;
//	public static String ClassCreationWizard_title;
//	public static String ClassCreationWizard_modifiers;
//	public static String ClassCreationWizard_abstract;
//	public static String ClassCreationWizard_methods_label;
//	public static String ClassCreationWizard_methods_main;
//	public static String ClassCreationWizard_main_method_content;
//	public static String ClassCreationWizard_file_content;
//	public static String ClassCreationWizard_invalid_package_name;
//	public static String ClassCreationWizard_invalid_file_name;
//	public static String InterfaceCreationWizard_page_description;
//	public static String InterfaceCreationWizard_page_title;
//	public static String InterfaceCreationWizard_title;
//	public static String ClassCreationWizard_superclass;
//	public static String ClassCreationWizard_browse;
//	public static String ClassCreationWizard_superclass_selection;
//	public static String ClassCreationWizard_satisfier_selection;
//	public static String Convention_type_uppercaseName;
//	public static String PackageCreationWizard_page_title;
//	public static String PackageCreationWizard_page_description;
//	public static String PackageCreationWizard_title;
//	public static String PackageCreationWizard_invalid_package_name;
//	public static String PackageCreationWizard_discouraged_package_name;
//
//	public static String VjoSourceModulePage_pathCannotBeEmpty;
//	public static String VjoSourceModulePage_file;
//	public static String EnumCreationWizard_page_description;
//	public static String EnumCreationWizard_page_title;
//	public static String EnumCreationWizard_title;
//	public static String MixinCreationWizard_page_description;
//	public static String MixinCreationWizard_page_title;
//	public static String MixinCreationWizard_title;
//	public static String OTypeCreationWizard_page_description;
//	public static String OTypeCreationWizard_page_title;
//	public static String OTypeCreationWizard_title;
//
	public static String NewScriptProjectWizardPage_LayoutGroup_option_separateFolders;
	public static String NewScriptProjectWizardPage_LayoutGroup_title;
	public static String NewScriptProjectWizardPage_LayoutGroup_option_oneFolder;
	public static String NewScriptProjectWizardPage_LayoutGroup_link_description;
//
//	public static String NewTypeWizardPage_interfaces_class_label;
//	public static String NewTypeWizardPage_interfaces_ifc_label;
//	public static String NewTypeWizardPage_interfaces_add;
//	public static String NewTypeWizardPage_interfaces_remove;
//
//	public static String NewTypeWizardPage_package_label;
//	public static String NewTypeWizardPage_package_button;
//	public static String convention_package_nullName;
//	public static String convention_package_emptyName;
//	public static String convention_package_dotName;
//	public static String convention_package_nameWithBlanks;
//	public static String convention_package_consecutiveDotsName;
//	public static String convention_package_uppercaseName;
//
//	public static String NewClassWizardPage_methods_constructors;
//	public static String ClassCreationWizard_constructor_content;
//
//	public static String NewPackageWizardPage_warning_DiscouragedPackageName;
//	public static String NewPackageWizardPage_error_InvalidPackageName;
//	public static String NewTypeWizardPage_error_ModifiersFinalAndAbstract;
//
//	public static String NewTypeWizardPage_addcomment_description;
//	public static String NewTypeWizardPage_addcomment_label;
//	public static String NewTypeWizardPage_configure_templates_title;
//	public static String NewTypeWizardPage_configure_templates_message;
//
//	public static String NewSourceFolderCreationWizard_title;
//	public static String NewSourceFolderCreationWizard_edit_title;
//	public static String NewSourceFolderCreationWizard_link_title;
//	public static String NewSourceFolderWizardPage_title;
//	public static String NewSourceFolderWizardPage_description;
//	public static String NewSourceFolderWizardPage_root_label;
//	public static String NewSourceFolderWizardPage_root_button;
//	public static String NewSourceFolderWizardPage_project_label;
//	public static String NewSourceFolderWizardPage_project_button;
//	public static String NewSourceFolderWizardPage_operation;
//	public static String NewSourceFolderWizardPage_exclude_label;
//	public static String NewSourceFolderWizardPage_ChooseExistingRootDialog_title;
//	public static String NewSourceFolderWizardPage_ChooseExistingRootDialog_description;
//	public static String NewSourceFolderWizardPage_ChooseProjectDialog_title;
//	public static String NewSourceFolderWizardPage_ChooseProjectDialog_description;
//	public static String NewSourceFolderWizardPage_error_EnterRootName;
//	public static String NewSourceFolderWizardPage_error_InvalidRootName;
//	public static String NewSourceFolderWizardPage_error_NotAFolder;
//	public static String NewSourceFolderWizardPage_error_AlreadyExisting;
//	public static String NewSourceFolderWizardPage_error_AlreadyExistingDifferentCase;
//	public static String NewSourceFolderWizardPage_error_EnterProjectName;
//	public static String NewSourceFolderWizardPage_error_InvalidProjectPath;
//	public static String NewSourceFolderWizardPage_error_NotAScriptProject;
//	public static String NewSourceFolderWizardPage_error_ProjectNotExists;
//	public static String NewSourceFolderWizardPage_warning_ReplaceSF;
//	public static String NewSourceFolderWizardPage_warning_AddedExclusions;
//	public static String NewSourceFolderWizardPage_ReplaceExistingSourceFolder_label;
//	public static String NewSourceFolderWizardPage_edit_description;
//
//	public static String Javascript_UI_Wizard_New_Description;
//	public static String Javascript_UI_Wizard_New_Heading;
//	public static String Javascript_UI_Wizard_New_Title;
//	public static String Javascript_Error_Filename_Must_End_JS;
//	public static String Javascript_Resource_Group_Name_Exists;
//	public static String Javascript_Warning_Folder_Must_Be_Inside_Web_Content;
//	
	public static String Jaggery_UI_Wizard_New_Description;
	public static String Jaggery_UI_Wizard_New_Heading;
	public static String Jaggery_UI_Wizard_New_Title;
	public static String Jaggery_Error_Filename_Must_End_JAG;
	public static String Jaggery_Resource_Group_Name_Exists;
	public static String Jaggery_Warning_Folder_Must_Be_Inside_Web_Content;

	
	static {
		NLS.initializeMessages(BUNDLE_NAME, JaggeryWizardMessages.class);
	}

}
