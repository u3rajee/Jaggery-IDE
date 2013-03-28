/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.wizards.buildpath;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.dltk.mod.core.*;
import org.eclipse.dltk.mod.internal.ui.util.CoreUtility;
import org.eclipse.dltk.mod.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.*;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.util.ExceptionHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class VariableBlock {

	private final ListDialogField fVariablesList;
	private Control fControl;
	private CLabel fWarning;
	private boolean fHasChanges;

	private List fSelectedElements;
	private boolean fAskToBuild;
	private final boolean fEditOnDoubleclick;

	public VariableBlock(boolean inPreferencePage, String initSelection) {

		fSelectedElements = new ArrayList(0);
		fEditOnDoubleclick = inPreferencePage;
		fAskToBuild = true;

		String[] buttonLabels = new String[] {
				NewWizardMessages.VariableBlock_vars_add_button,
				NewWizardMessages.VariableBlock_vars_edit_button,
				NewWizardMessages.VariableBlock_vars_remove_button };

		VariablesAdapter adapter = new VariablesAdapter();

		BPVariableElementLabelProvider labelProvider = new BPVariableElementLabelProvider(
				inPreferencePage);

		fVariablesList = new ListDialogField(adapter, buttonLabels,
				labelProvider);
		fVariablesList.setDialogFieldListener(adapter);
		fVariablesList.setLabelText(NewWizardMessages.VariableBlock_vars_label);
		fVariablesList.setRemoveButtonIndex(2);

		fVariablesList.enableButton(1, false);

		// TODO: create a sorter or modify ListDialogField to work with
		// comparators
		// fVariablesList.setViewerComparator(new ViewerComparator() {
		// public int compare(Viewer viewer, Object e1, Object e2) {
		// if (e1 instanceof BPVariableElement
		// && e2 instanceof BPVariableElement) {
		// return getComparator().compare(
		// ((BPVariableElement) e1).getName(),
		// ((BPVariableElement) e2).getName());
		// }
		// return super.compare(viewer, e1, e2);
		// }
		// });
		refresh(initSelection);
	}

	public boolean hasChanges() {
		return fHasChanges;
	}

	public void setChanges(boolean hasChanges) {
		fHasChanges = hasChanges;
	}

	public Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		LayoutUtil.doDefaultLayout(composite,
				new DialogField[] { fVariablesList }, true, 0, 0);
		LayoutUtil.setHorizontalGrabbing(fVariablesList.getListControl(null));

		fWarning = new CLabel(composite, SWT.NONE);
		fWarning.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,
				fVariablesList.getNumberOfControls() - 1, 1));

		fControl = composite;

		return composite;
	}

	public void addDoubleClickListener(IDoubleClickListener listener) {
		fVariablesList.getTableViewer().addDoubleClickListener(listener);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fVariablesList.getTableViewer().addSelectionChangedListener(listener);
	}

	private Shell getShell() {
		if (fControl != null) {
			return fControl.getShell();
		}
		return DLTKUIPlugin.getActiveWorkbenchShell();
	}

	private class VariablesAdapter implements IDialogFieldListener,
			IListAdapter {

		// -------- IListAdapter --------

		public void customButtonPressed(ListDialogField field, int index) {
			switch (index) {
			case 0: /* add */
				editEntries(null);
				break;
			case 1: /* edit */
				List selected = field.getSelectedElements();
				editEntries((BPVariableElement) selected.get(0));
				break;
			}
		}

		public void selectionChanged(ListDialogField field) {
			doSelectionChanged(field);
		}

		public void doubleClicked(ListDialogField field) {
			if (fEditOnDoubleclick) {
				List selected = field.getSelectedElements();
				if (canEdit(selected, containsReadOnly(selected))) {
					editEntries((BPVariableElement) selected.get(0));
				}
			}
		}

		// ---------- IDialogFieldListener --------

		public void dialogFieldChanged(DialogField field) {
		}

	}

	private boolean containsReadOnly(List selected) {
		for (int i = selected.size() - 1; i >= 0; i--) {
			if (((BPVariableElement) selected.get(i)).isReadOnly()) {
				return true;
			}
		}
		return false;
	}

	private boolean canEdit(List selected, boolean containsReadOnly) {
		return selected.size() == 1 && !containsReadOnly;
	}

	/**
	 * @param field
	 *            the dialog field
	 */
	private void doSelectionChanged(DialogField field) {
		List selected = fVariablesList.getSelectedElements();
		boolean containsReadOnly = containsReadOnly(selected);

		// edit
		fVariablesList.enableButton(1, canEdit(selected, containsReadOnly));
		// remove button
		fVariablesList.enableButton(2, !containsReadOnly);

		fSelectedElements = selected;
	}

	private void editEntries(BPVariableElement entry) {
		List existingEntries = fVariablesList.getElements();

		VariableCreationDialog dialog = new VariableCreationDialog(getShell(),
				entry, existingEntries);
		if (dialog.open() != Window.OK) {
			return;
		}
		BPVariableElement newEntry = dialog.getBuildpathElement();
		if (entry == null) {
			fVariablesList.addElement(newEntry);
			entry = newEntry;
			fHasChanges = true;
		} else {
			boolean hasChanges = !(entry.getName().equals(newEntry.getName()) && entry
					.getPath().equals(newEntry.getPath()));
			if (hasChanges) {
				fHasChanges = true;
				entry.setName(newEntry.getName());
				entry.setPath(newEntry.getPath());
				fVariablesList.refresh();
			}
		}
		fVariablesList.selectElements(new StructuredSelection(entry));
	}

	public List getSelectedElements() {
		return fSelectedElements;
	}

	public boolean performOk() {
		ArrayList removedVariables = new ArrayList();
		ArrayList changedVariables = new ArrayList();
		removedVariables.addAll(Arrays.asList(DLTKCore
				.getBuildpathVariableNames()));

		// remove all unchanged
		List changedElements = fVariablesList.getElements();
		for (int i = changedElements.size() - 1; i >= 0; i--) {
			BPVariableElement curr = (BPVariableElement) changedElements.get(i);
			if (curr.isReadOnly()) {
				changedElements.remove(curr);
			} else {
				IPath path = curr.getPath();
				IPath prevPath = DLTKCore.getBuildpathVariable(curr.getName());
				if (prevPath != null && prevPath.equals(path)) {
					changedElements.remove(curr);
				} else {
					changedVariables.add(curr.getName());
				}
			}
			removedVariables.remove(curr.getName());
		}
		int steps = changedElements.size() + removedVariables.size();
		if (steps > 0) {

			boolean needsBuild = false;
			if (fAskToBuild
					&& doesChangeRequireFullBuild(removedVariables,
							changedVariables)) {
				String title = NewWizardMessages.VariableBlock_needsbuild_title;
				String message = NewWizardMessages.VariableBlock_needsbuild_message;

				MessageDialog buildDialog = new MessageDialog(getShell(),
						title, null, message, MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL,
								IDialogConstants.CANCEL_LABEL }, 2);
				int res = buildDialog.open();
				if (res != 0 && res != 1) {
					return false;
				}
				needsBuild = (res == 0);
			}

			final VariableBlockRunnable runnable = new VariableBlockRunnable(
					removedVariables, changedElements);
			final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
					getShell());
			try {
				dialog.run(true, true, runnable);
			} catch (InvocationTargetException e) {
				ExceptionHandler
						.handle(
								new InvocationTargetException(
										new NullPointerException()),
								getShell(),
								NewWizardMessages.VariableBlock_variableSettingError_titel,
								NewWizardMessages.VariableBlock_variableSettingError_message);
				return false;
			} catch (InterruptedException e) {
				return false;
			}

			if (needsBuild) {
				CoreUtility.getBuildJob(null).schedule();
			}
		}
		return true;
	}

	private boolean doesChangeRequireFullBuild(List removed, List changed) {
		try {
			IScriptModel model = DLTKCore.create(ResourcesPlugin.getWorkspace()
					.getRoot());
			IScriptProject[] projects = model.getScriptProjects();
			for (int i = 0; i < projects.length; i++) {
				IBuildpathEntry[] entries = projects[i].getRawBuildpath();
				for (int k = 0; k < entries.length; k++) {
					IBuildpathEntry curr = entries[k];
					if (curr.getEntryKind() == IBuildpathEntry.BPE_VARIABLE) {
						String var = curr.getPath().segment(0);
						if (removed.contains(var) || changed.contains(var)) {
							return true;
						}
					}
				}
			}
		} catch (ModelException e) {
			return true;
		}
		return false;
	}

	private class VariableBlockRunnable implements IRunnableWithProgress {
		private final List fToRemove;
		private final List fToChange;

		public VariableBlockRunnable(List toRemove, List toChange) {
			fToRemove = toRemove;
			fToChange = toChange;
		}

		/*
		 * @see IRunnableWithProgress#run(IProgressMonitor)
		 */
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			monitor
					.beginTask(NewWizardMessages.VariableBlock_operation_desc,
							1);
			try {
				setVariables(monitor);

			} catch (CoreException e) {
				throw new InvocationTargetException(e);
			} catch (OperationCanceledException e) {
				throw new InterruptedException();
			} finally {
				monitor.done();
			}
		}

		public void setVariables(IProgressMonitor monitor)
				throws ModelException, CoreException {
			int nVariables = fToChange.size() + fToRemove.size();

			String[] names = new String[nVariables];
			IPath[] paths = new IPath[nVariables];
			int k = 0;

			for (int i = 0; i < fToChange.size(); i++) {
				BPVariableElement curr = (BPVariableElement) fToChange.get(i);
				names[k] = curr.getName();
				paths[k] = curr.getPath();
				k++;
			}
			for (int i = 0; i < fToRemove.size(); i++) {
				names[k] = (String) fToRemove.get(i);
				paths[k] = null;
				k++;
			}
			DLTKCore.setBuildpathVariables(names, paths,
					new SubProgressMonitor(monitor, 1));
		}
	}

	/**
	 * If set to true, a dialog will ask the user to build on variable changed
	 * 
	 * @param askToBuild
	 *            The askToBuild to set
	 */
	public void setAskToBuild(boolean askToBuild) {
		fAskToBuild = askToBuild;
	}

	/**
	 * @param initSelection
	 *            the initial selection
	 */
	public void refresh(String initSelection) {
		BPVariableElement initSelectedElement = null;

		String[] entries = DLTKCore.getBuildpathVariableNames();
		ArrayList elements = new ArrayList(entries.length);
		for (int i = 0; i < entries.length; i++) {
			String name = entries[i];
			BPVariableElement elem;
			IPath entryPath = DLTKCore.getBuildpathVariable(name);
			if (entryPath != null) {
				elem = new BPVariableElement(name, entryPath);
				elements.add(elem);
				if (name.equals(initSelection)) {
					initSelectedElement = elem;
				}
			} else {
				DLTKUIPlugin
						.logErrorMessage("VariableBlock: Buildpath variable with null value: " + name); //$NON-NLS-1$
			}
		}

		fVariablesList.setElements(elements);

		if (initSelectedElement != null) {
			ISelection sel = new StructuredSelection(initSelectedElement);
			fVariablesList.selectElements(sel);
		} else {
			fVariablesList.selectFirstElement();
		}

		fHasChanges = false;
	}

	public void setSelection(String elementName) {
		for (int i = 0; i < fVariablesList.getSize(); i++) {
			BPVariableElement elem = (BPVariableElement) fVariablesList
					.getElement(i);
			if (elem.getName().equals(elementName)) {
				fVariablesList.selectElements(new StructuredSelection(elem));
				return;
			}
		}
	}

}
