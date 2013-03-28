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
package org.eclipse.dltk.mod.internal.corext.refactoring.vjet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IScriptFolder;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.corext.refactoring.rename.RenamingNameSuggestor;
import org.eclipse.dltk.mod.internal.corext.refactoring.rename.ScriptRenameProcessor;
import org.eclipse.dltk.mod.internal.corext.refactoring.vjet.descriptors.RenameModelElementDescriptor;
import org.eclipse.dltk.mod.internal.ui.refactoring.UserInterfaceStarter;
import org.eclipse.dltk.mod.internal.ui.refactoring.reorg.RenameRefactoringWizard;
import org.eclipse.dltk.mod.internal.ui.refactoring.reorg.RenameUserInterfaceStarter;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardPage;
import org.eclipse.swt.widgets.Shell;

public class VjetRenameHelper {

	private static String getContributionId(IModelElement modelElement) {
		String contributionId;
		int elementType = modelElement.getElementType();
		switch (elementType) {
		// case IJavaElement.JAVA_PROJECT:
		// contributionId= IJavaRefactorings.RENAME_JAVA_PROJECT;
		// break;
		// case IJavaElement.PACKAGE_FRAGMENT_ROOT:
		// contributionId= IJavaRefactorings.RENAME_SOURCE_FOLDER;
		// break;
		case IModelElement.SCRIPT_FOLDER:
			contributionId = IVjoRefactorings.RENAME_SCRIPT_FOLDER;
			break;
		case IModelElement.SOURCE_MODULE:
			contributionId = IVjoRefactorings.RENAME_SOURCE_MODULE;
			break;
		case IModelElement.TYPE:
			contributionId = IVjoRefactorings.RENAME_TYPE;
			break;
		// case IJavaElement.METHOD:
		// final IMethod method= (IMethod) javaElement;
		// if (method.isConstructor())
		// return createRenameDescriptor(method.getDeclaringType(), newName);
		// else
		// contributionId= IJavaRefactorings.RENAME_METHOD;
		// break;
		// case IJavaElement.FIELD:
		// IField field= (IField) javaElement;
		// if (field.isEnumConstant())
		// contributionId= IJavaRefactorings.RENAME_ENUM_CONSTANT;
		// else
		// contributionId= IJavaRefactorings.RENAME_FIELD;
		// break;
		// case IJavaElement.TYPE_PARAMETER:
		// contributionId= IJavaRefactorings.RENAME_TYPE_PARAMETER;
		// break;
		// case IJavaElement.LOCAL_VARIABLE:
		// contributionId= IJavaRefactorings.RENAME_LOCAL_VARIABLE;
		// break;
		default:
			return null;
		}
		return contributionId;

	}

	/**
	 * this method is used to search rename implementation from vjo project
	 * 
	 * @param modelElement
	 * @param newName
	 * @return
	 * @throws ModelException
	 */
	private static RenameModelElementDescriptor createVjoRenameDescriptor(
			IModelElement modelElement, String newName) throws ModelException {
		String contributionId = getContributionId(modelElement);
		// see RefactoringExecutionStarter#createRenameSupport(..):
		int elementType = modelElement.getElementType();

		RenameModelElementDescriptor descriptor = (RenameModelElementDescriptor) RefactoringCore
				.getRefactoringContribution(contributionId).createDescriptor();
		descriptor.setModelElement(modelElement);
		// descriptor.setNewName(newName);
		if (elementType != IModelElement.SCRIPT_FOLDER)
			descriptor.setUpdateReferences(true);

		IDialogSettings dltkSettings = DLTKUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings refactoringSettings = dltkSettings
				.getSection(RefactoringWizardPage.REFACTORING_SETTINGS); // TODO:
		// undocumented
		// API
		if (refactoringSettings == null) {
			refactoringSettings = dltkSettings
					.addNewSection(RefactoringWizardPage.REFACTORING_SETTINGS);
		}

		// switch (elementType) {
		// case IJavaElement.METHOD:
		// case IJavaElement.FIELD:
		// descriptor.setDeprecateDelegate(refactoringSettings.getBoolean(DelegateUIHelper.DELEGATE_DEPRECATION));
		// descriptor.setKeepOriginal(refactoringSettings.getBoolean(DelegateUIHelper.DELEGATE_UPDATING));
		// }
		switch (elementType) {
		case IModelElement.TYPE:
			// case IJavaElement.COMPILATION_UNIT: // TODO
			descriptor
					.setUpdateSimilarDeclarations(refactoringSettings
							.getBoolean(RenameRefactoringWizard.TYPE_UPDATE_SIMILAR_ELEMENTS));
			int strategy;
			try {
				strategy = refactoringSettings
						.getInt(RenameRefactoringWizard.TYPE_SIMILAR_MATCH_STRATEGY);
			} catch (NumberFormatException e) {
				strategy = RenamingNameSuggestor.STRATEGY_EXACT;
			}
			descriptor.setMatchStrategy(strategy);
		}
		switch (elementType) {
		case IModelElement.SCRIPT_FOLDER:
			descriptor
					.setUpdateHierarchy(refactoringSettings
							.getBoolean(RenameRefactoringWizard.PACKAGE_RENAME_SUBPACKAGES));
		}
		switch (elementType) {
		case IModelElement.SCRIPT_FOLDER:
		case IModelElement.TYPE:
			String fileNamePatterns = refactoringSettings
					.get(RenameRefactoringWizard.QUALIFIED_NAMES_PATTERNS);
			if (fileNamePatterns != null && fileNamePatterns.length() != 0) {
				descriptor.setFileNamePatterns(fileNamePatterns);
				boolean updateQualifiedNames = refactoringSettings
						.getBoolean(RenameRefactoringWizard.UPDATE_QUALIFIED_NAMES);
				descriptor.setUpdateQualifiedNames(updateQualifiedNames);
				// fShowPreview |= updateQualifiedNames;
			}
		}
		switch (elementType) {
		case IModelElement.SCRIPT_FOLDER:
		case IModelElement.TYPE:
		case IModelElement.FIELD:
			boolean updateTextualOccurrences = refactoringSettings
					.getBoolean(RenameRefactoringWizard.UPDATE_TEXTUAL_MATCHES);
			descriptor.setUpdateTextualOccurrences(updateTextualOccurrences);
			// fShowPreview |= updateTextualOccurrences;
		}
		// switch (elementType) {
		// case IModelElement.FIELD:
		// descriptor.setRenameGetters(refactoringSettings
		// .getBoolean(RenameRefactoringWizard.FIELD_RENAME_GETTER));
		// descriptor.setRenameSetters(refactoringSettings
		// .getBoolean(RenameRefactoringWizard.FIELD_RENAME_SETTER));
		// }
		return descriptor;
	}

	public static boolean startRenameModelElement(Shell parent,
			IModelElement modelElement, String newName) throws ModelException,
			CoreException {

		if (isRenameAvailable(modelElement)) {
			RenameModelElementDescriptor renameModelElementDescriptor = createVjoRenameDescriptor(
					modelElement, newName);

			openDialog(parent, renameModelElementDescriptor,
					getContributionId(modelElement));

			return true;

		} else {
			return false;
		}

	}

	private static void openDialog(Shell parent,
			RenameModelElementDescriptor descriptor, String contributionId)
			throws CoreException {
		RefactoringStatus refactoringStatus = new RefactoringStatus();
		UserInterfaceStarter starter = new RenameUserInterfaceStarter();

		AbstractVjoRenameRefactoringContribution contribution = (AbstractVjoRenameRefactoringContribution) RefactoringCore
				.getRefactoringContribution(contributionId);
		starter.initialize(contribution.getRenameWizard());

		RenameRefactoring renameRefactoring = (RenameRefactoring) descriptor
				.createRefactoring(refactoringStatus);
		starter.activate(renameRefactoring, parent,
				((ScriptRenameProcessor) renameRefactoring.getProcessor())
						.needsSavedEditors());

	}

	private static boolean isRenameAvailable(IModelElement modelElement) {
		if (modelElement instanceof IType) {
			return true;
		}

		if (modelElement instanceof ISourceModule) {
			return true;
		}

		if (modelElement instanceof IScriptFolder) {
			return true;
		}

		return false;

	}

}
