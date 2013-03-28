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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.mod.internal.corext.util.Messages;
import org.eclipse.dltk.mod.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;
import org.eclipse.dltk.mod.launching.IInterpreterInstallType;
import org.eclipse.dltk.mod.launching.InterpreterStandin;
import org.eclipse.dltk.mod.launching.ScriptRuntime;
import org.eclipse.dltk.mod.launching.ScriptRuntime.DefaultInterpreterEntry;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.environment.IEnvironmentUI;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * The first page of the <code>SimpleProjectWizard</code>.
 */
public abstract class ProjectWizardFirstPage extends WizardPage {

	boolean undefinedInterpreter = false;

	/**
	 * Request a project name. Fires an event whenever the text field is
	 * changed, regardless of its content.
	 */
	private final class NameGroup extends Observable implements
			IDialogFieldListener {
		protected final StringDialogField fNameField;

		public NameGroup(Composite composite, String initialName) {
			final Composite nameComposite = new Composite(composite, SWT.NONE);
			nameComposite.setFont(composite.getFont());
			nameComposite.setLayout(initGridLayout(new GridLayout(2, false),
					false));
			nameComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			// text field for project name
			fNameField = new StringDialogField();
			fNameField
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_NameGroup_label_text);
			fNameField.setDialogFieldListener(this);
			setName(initialName);
			fNameField.doFillIntoGrid(nameComposite, 2);
			LayoutUtil.setHorizontalGrabbing(fNameField.getTextControl(null));
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		public String getName() {
			return fNameField.getText().trim();
		}

		public void postSetFocus() {
			fNameField.postSetFocusOnDialogField(getShell().getDisplay());
		}

		public void setName(String name) {
			fNameField.setText(name);
		}

		public void dialogFieldChanged(DialogField field) {
			fireEvent();
		}
	}

	/**
	 * Request a location. Fires an event whenever the checkbox or the location
	 * field is changed, regardless of whether the change originates from the
	 * user or has been invoked programmatically.
	 */
	protected final class LocationGroup extends Observable implements Observer,
			IStringButtonAdapter, IDialogFieldListener {
		protected final SelectionButtonDialogField fWorkspaceRadio;
		protected final SelectionButtonDialogField fExternalRadio;
		protected final StringButtonDialogField fLocation;
		protected final ComboDialogField fEnvironment;
		private IEnvironment[] environments;

		private String fPreviousExternalLocation;
		private static final String DIALOGSTORE_LAST_EXTERNAL_LOC = DLTKUIPlugin.PLUGIN_ID
				+ ".last.external.project"; //$NON-NLS-1$

		public LocationGroup(Composite composite) {
			final int numColumns = 3;
			final Group group = new Group(composite, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			group.setLayout(initGridLayout(new GridLayout(numColumns, false),
					true));
			group.setText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_title);
			fWorkspaceRadio = new SelectionButtonDialogField(SWT.RADIO);
			fWorkspaceRadio.setDialogFieldListener(this);
			fWorkspaceRadio
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_workspace_desc);
			fExternalRadio = new SelectionButtonDialogField(SWT.RADIO);
			fExternalRadio.setDialogFieldListener(this);
			fExternalRadio
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_external_desc);
			fLocation = new StringButtonDialogField(this);
			fLocation.setDialogFieldListener(this);
			fLocation
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_locationLabel_desc);
			fLocation
					.setButtonLabel(NewWizardMessages.ScriptProjectWizardFirstPage_LocationGroup_browseButton_desc);
			// fExternalRadio.attachDialogField(fLocation);
			fWorkspaceRadio.setSelection(true);
			fExternalRadio.setSelection(false);
			fPreviousExternalLocation = ""; //$NON-NLS-1$
			fWorkspaceRadio.doFillIntoGrid(group, numColumns);
			fExternalRadio.doFillIntoGrid(group, numColumns);
			fLocation.doFillIntoGrid(group, numColumns);
			LayoutUtil.setHorizontalGrabbing(fLocation.getTextControl(null));

			fEnvironment = new ComboDialogField(SWT.DROP_DOWN | SWT.READ_ONLY);
			fEnvironment
					.setLabelText(NewWizardMessages.ProjectWizardFirstPage_host);
			fEnvironment.setDialogFieldListener(this);
			fEnvironment.setDialogFieldListener(new IDialogFieldListener() {
				public void dialogFieldChanged(DialogField field) {
					updateInterpreters();
				}

			});
			environments = EnvironmentManager.getEnvironments();
			String[] items = new String[environments.length];
			int local = 0;
			for (int i = 0; i < items.length; i++) {
				items[i] = environments[i].getName();
				if (environments[i].isLocal()) {
					local = i;
				}
			}
			fEnvironment.setItems(items);
			fEnvironment.selectItem(local);
			fEnvironment.doFillIntoGrid(group, numColumns);
			LayoutUtil
					.setHorizontalGrabbing(fEnvironment.getComboControl(null));
			fExternalRadio.attachDialogFields(new DialogField[] { fLocation,
					fEnvironment });
		}

		protected void fireEvent() {
			setChanged();
			notifyObservers();
		}

		private void updateInterpreters() {
			Observable observable = ProjectWizardFirstPage.this
					.getInterpreterGroupObservable();
			if (observable != null
					&& observable instanceof AbstractInterpreterGroup) {
				((AbstractInterpreterGroup) observable)
						.handlePossibleInterpreterChange();
			}
		}

		protected String getDefaultPath(String name) {
			IEnvironment environment = this.getEnvironment();
			if (environment != null && environment.isLocal()) {
				final IPath path = Platform.getLocation().append(name);
				return path.toOSString();
			} else {
				return ""; //$NON-NLS-1$
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			if (isInWorkspace()) {
				fLocation.setText(getDefaultPath(fNameGroup.getName()));
			}
			fireEvent();
		}

		public IPath getLocation() {
			if (isInWorkspace()) {
				return Platform.getLocation();
			}
			return new Path(fLocation.getText().trim());
		}

		public boolean isInWorkspace() {
			return fWorkspaceRadio.isSelected();
		}

		public IEnvironment getEnvironment() {
			if (fWorkspaceRadio.isSelected()) {
				return EnvironmentManager
						.getEnvironmentById(LocalEnvironment.ENVIRONMENT_ID);
			}
			return environments[fEnvironment.getSelectionIndex()];
		}

		public void changeControlPressed(DialogField field) {
			IEnvironment environment = getEnvironment();
			IEnvironmentUI environmentUI = (IEnvironmentUI) environment
					.getAdapter(IEnvironmentUI.class);
			if (environmentUI != null) {
				String selectedDirectory = environmentUI
						.selectFolder(getShell());

				if (selectedDirectory != null) {
					fLocation.setText(selectedDirectory);
					DLTKUIPlugin
							.getDefault()
							.getDialogSettings()
							.put(DIALOGSTORE_LAST_EXTERNAL_LOC,
									selectedDirectory);
				}
			}
		}

		public void dialogFieldChanged(DialogField field) {
			if (field == fWorkspaceRadio) {
				final boolean checked = fWorkspaceRadio.isSelected();
				if (checked) {
					fPreviousExternalLocation = fLocation.getText();
					fLocation.setText(getDefaultPath(fNameGroup.getName()));
				} else {
					IEnvironment environment = this.getEnvironment();
					if (environment != null && environment.isLocal()) {
						fLocation.setText(fPreviousExternalLocation);
					} else {
						fLocation.setText(""); //$NON-NLS-1$
					}
				}
				updateInterpreters();
			}

			if (field == fExternalRadio) {
				updateInterpreters();
			}

			fireEvent();
		}
	}

	protected abstract class AbstractInterpreterGroup extends Observable
			implements Observer, SelectionListener, IDialogFieldListener {

		protected final SelectionButtonDialogField fUseDefaultInterpreterEnvironment,
				fUseProjectInterpreterEnvironment;
		protected final ComboDialogField fInterpreterEnvironmentCombo;
		private final Group fGroup;
		private String[] fComplianceLabels;
		private final Link fPreferenceLink;
		private IInterpreterInstall[] fInstalledInterpreters;

		public AbstractInterpreterGroup(Composite composite) {
			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(3, false), true));
			fGroup.setText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_title);

			fUseDefaultInterpreterEnvironment = new SelectionButtonDialogField(
					SWT.RADIO);
			fUseDefaultInterpreterEnvironment
					.setLabelText(getDefaultInterpreterLabel());
			fUseDefaultInterpreterEnvironment.doFillIntoGrid(fGroup, 2);
			fUseDefaultInterpreterEnvironment.setDialogFieldListener(this);

			fPreferenceLink = new Link(fGroup, SWT.NONE);
			fPreferenceLink.setFont(fGroup.getFont());
			fPreferenceLink
					.setText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END,
					GridData.CENTER, false, false));
			fPreferenceLink.addSelectionListener(this);

			fUseProjectInterpreterEnvironment = new SelectionButtonDialogField(
					SWT.RADIO);
			fUseProjectInterpreterEnvironment
					.setLabelText(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_specific_compliance);
			fUseProjectInterpreterEnvironment.doFillIntoGrid(fGroup, 1);
			fUseProjectInterpreterEnvironment.setDialogFieldListener(this);

			// Add by Oliver. 2009-07-30. Before the default interpreter is no
			// specific, so the build path has error--"Build path entry is
			// missing: org.eclipse.dltk.mod.launching.INTERPRETER_CONTAINER".
			// See
			// bug:6134
			fUseProjectInterpreterEnvironment.setSelection(true);

			fInterpreterEnvironmentCombo = new ComboDialogField(SWT.READ_ONLY);
			fillInstalledInterpreterEnvironments(fInterpreterEnvironmentCombo);
			fInterpreterEnvironmentCombo.setDialogFieldListener(this);

			Combo comboControl = fInterpreterEnvironmentCombo
					.getComboControl(fGroup);
			GridData gridData = new GridData(GridData.BEGINNING,
					GridData.CENTER, true, false);
			gridData.minimumWidth = 100;
			comboControl.setLayoutData(gridData); // make sure column 2 is
			// grabing (but no fill)
			comboControl.setVisibleItemCount(20);

			// Add by Oliver. 2009-07-30. Before the default interpreter is no
			// specific, so the build path has error--"Build path entry is
			// missing: org.eclipse.dltk.mod.launching.INTERPRETER_CONTAINER".
			// See
			// bug:6134
			if (comboControl.getItemCount() > 0) {
				comboControl.select(0);
			}
			// DialogField.createEmptySpace(fGroup);

			// fUseDefaultInterpreterEnvironment.setSelection(true);
			fInterpreterEnvironmentCombo
					.setEnabled(fUseProjectInterpreterEnvironment.isSelected());
		}

		private void fillInstalledInterpreterEnvironments(
				ComboDialogField comboField) {
			String selectedItem = null;
			int selectionIndex = -1;
			if (fUseProjectInterpreterEnvironment.isSelected()) {
				selectionIndex = comboField.getSelectionIndex();
				if (selectionIndex != -1) {// paranoia
					selectedItem = comboField.getItems()[selectionIndex];
				}
			}

			fInstalledInterpreters = getWorkspaceInterpeters();

			selectionIndex = -1;// find new index
			fComplianceLabels = new String[fInstalledInterpreters.length];
			for (int i = 0; i < fInstalledInterpreters.length; i++) {
				fComplianceLabels[i] = fInstalledInterpreters[i].getName();
				if (selectedItem != null
						&& fComplianceLabels[i].equals(selectedItem)) {
					selectionIndex = i;
				}
			}
			comboField.setItems(fComplianceLabels);
			if (selectionIndex == -1) {
				fInterpreterEnvironmentCombo
						.selectItem(getDefaultInterpreterName());
			} else {
				fInterpreterEnvironmentCombo.selectItem(selectedItem);
			}
			interpretersPresent = (fInstalledInterpreters.length > 0);
		}

		public IInterpreterInstall[] getWorkspaceInterpeters() {
			List standins = new ArrayList();
			IInterpreterInstallType[] types = ScriptRuntime
					.getInterpreterInstallTypes(getCurrentLanguageNature());
			IEnvironment environment = fLocationGroup.getEnvironment();
			for (int i = 0; i < types.length; i++) {
				IInterpreterInstallType type = types[i];
				IInterpreterInstall[] installs = type.getInterpreterInstalls();
				for (int j = 0; j < installs.length; j++) {
					IInterpreterInstall install = installs[j];
					IEnvironment env = install.getEnvironment();
					if (env != null && env.equals(environment)) {
						standins.add(new InterpreterStandin(install));
					}
				}
			}
			return ((IInterpreterInstall[]) standins
					.toArray(new IInterpreterInstall[standins.size()]));
		}

		private String getDefaultInterpreterName() {
			IInterpreterInstall inst = ScriptRuntime
					.getDefaultInterpreterInstall(new DefaultInterpreterEntry(
							getCurrentLanguageNature(), getEnvironment()
									.getId()));
			if (inst != null)
				return inst.getName();
			else
				return "undefined"; //$NON-NLS-1$
		}

		private String getDefaultInterpreterLabel() {
			return Messages
					.format(NewWizardMessages.ScriptProjectWizardFirstPage_InterpreterEnvironmentGroup_default_compliance,
							getDefaultInterpreterName());
		}

		public void update(Observable o, Object arg) {
			updateEnableState();
		}

		private void updateEnableState() {
			if (fDetectGroup == null)
				return;
			final boolean detect = fDetectGroup.mustDetect()
					&& interpretersPresent;
			fUseDefaultInterpreterEnvironment.setEnabled(!detect);
			fUseProjectInterpreterEnvironment.setEnabled(!detect);
			fInterpreterEnvironmentCombo.setEnabled(!detect
					&& fUseProjectInterpreterEnvironment.isSelected());
			fPreferenceLink.setEnabled(!detect);
			fGroup.setEnabled(!detect);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
			validateUndefinedInterpreter();

		}

		/**
		 * Shows window with appropriate language preference page.
		 * 
		 */
		void showInterpreterPreferencePage() {
			final String pageId = getIntereprtersPreferencePageId();

			PreferencesUtil.createPreferenceDialogOn(getShell(), pageId,
					new String[] { pageId }, null).open();
		}

		protected abstract String getIntereprtersPreferencePageId();

		protected abstract String getCurrentLanguageNature();

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			showInterpreterPreferencePage();
			handlePossibleInterpreterChange();
			updateEnableState();
			// fDetectGroup.handlePossibleJInterpreterChange();
		}

		public void handlePossibleInterpreterChange() {

			fUseDefaultInterpreterEnvironment
					.setLabelText(getDefaultInterpreterLabel());
			fillInstalledInterpreterEnvironments(fInterpreterEnvironmentCombo);

			setChanged();
			notifyObservers();
		}

		public void dialogFieldChanged(DialogField field) {
			validateUndefinedInterpreter();
			// updateEnableState();
			// fDetectGroup.handlePossibleInterpreterChange();
		}

		/**
		 * Set the interpreter whether is defined.
		 */
		public void validateUndefinedInterpreter() {
			if (fUseDefaultInterpreterEnvironment.isSelected()) {
				if ("Use def&ault interpreter (Currently 'undefined')"
						.equals(getDefaultInterpreterLabel())) {
					setErrorMessage(NewWizardMessages.ProjectWizardFirstPage_atLeastOneDefaultInterpreterMustBeConfigured);
					setPageComplete(false);
					undefinedInterpreter = true;
					return;
				}
			} else {
				setPageComplete(true);
				setErrorMessage(null);
				setMessage(null);
				undefinedInterpreter = false;
			}
			undefinedInterpreter = false;
		}

		public boolean isUseSpecific() {
			return fUseProjectInterpreterEnvironment.isSelected();
		}

		public IInterpreterInstall getSelectedInterpreter() {
			if (fUseProjectInterpreterEnvironment.isSelected()) {
				int index = fInterpreterEnvironmentCombo.getSelectionIndex();
				if (index >= 0 && index < fComplianceLabels.length) { // paranoia
					return fInstalledInterpreters[index];
				}
			}
			return null;
		}
	}

	/**
	 * Show a warning when the project location contains files.
	 */
	protected final class DetectGroup extends Observable implements Observer,
			SelectionListener {
		private final Link fHintText;
		private boolean fDetect;

		public DetectGroup(Composite composite) {
			fHintText = new Link(composite, SWT.WRAP);
			fHintText.setFont(composite.getFont());
			fHintText.addSelectionListener(this);
			GridData gd = new GridData(GridData.FILL, SWT.FILL, true, true);
			gd.widthHint = convertWidthInCharsToPixels(50);
			fHintText.setLayoutData(gd);
			if (supportInterpreter()) {
				handlePossibleInterpreterChange();
			}
		}

		private boolean isValidProjectName(String name) {
			if (name.length() == 0) {
				return false;
			}
			final IWorkspace workspace = DLTKUIPlugin.getWorkspace();
			return workspace.validateName(name, IResource.PROJECT).isOK()
					&& workspace.getRoot().findMember(name) == null;
		}

		public void update(Observable o, Object arg) {
			if (o instanceof LocationGroup) {
				boolean oldDetectState = fDetect;
				IPath location = fLocationGroup.getLocation();
				if (fLocationGroup.isInWorkspace()) {
					if (!isValidProjectName(getProjectName())) {
						fDetect = false;
					} else {
						IEnvironment environment = fLocationGroup
								.getEnvironment();
						final IFileHandle directory = environment
								.getFile(location.append(getProjectName()));
						fDetect = directory.isDirectory();
					}
				} else {
					IEnvironment environment = fLocationGroup.getEnvironment();
					if (location.toPortableString().length() > 0) {
						final IFileHandle directory = environment
								.getFile(location);
						fDetect = directory.isDirectory();
					}
				}
				if (oldDetectState != fDetect) {
					setChanged();
					notifyObservers();
					if (fDetect) {
						fHintText.setVisible(true);
						fHintText
								.setText(NewWizardMessages.ScriptProjectWizardFirstPage_DetectGroup_message);
					} else {
						fHintText.setVisible(false);
					}
					if (supportInterpreter()) {
						handlePossibleInterpreterChange();
					}

				}
			}
		}

		public boolean mustDetect() {
			return fDetect;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse
		 * .swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org
		 * .eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			if (DLTKCore.DEBUG) {
				System.err
						.println("DetectGroup show compilancePreferencePage..."); //$NON-NLS-1$
			}
			if (supportInterpreter()) {
				handlePossibleInterpreterChange();
			}
		}
	}

	private final class WorkingSetGroup {

		private WorkingSetConfigurationBlock fWorkingSetBlock;

		public WorkingSetGroup(Composite composite,
				IWorkingSet[] initialWorkingSets) {
			Group workingSetGroup = new Group(composite, SWT.NONE);
			workingSetGroup.setFont(composite.getFont());
			workingSetGroup
					.setText(NewWizardMessages.ScriptProjectWizardFirstPage_WorkingSets_group);
			workingSetGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
					false));
			workingSetGroup.setLayout(new GridLayout(1, false));
			// EBAY MOD
			String[] workingSetIds = new String[] {
					SimpleWorkingSetSelectionDialog.JavaWorkingSetUpdater_ID,
					"org.eclipse.ui.resourceWorkingSetPage" }; //$NON-NLS-1$
			// END EBAY MOD
			fWorkingSetBlock = new WorkingSetConfigurationBlock(
					workingSetIds,
					NewWizardMessages.ScriptProjectWizardFirstPage_EnableWorkingSet_button,
					JavaPlugin.getDefault().getDialogSettings());
			fWorkingSetBlock
					.setDialogMessage(NewWizardMessages.ScriptProjectWizardFirstPage_WorkingSetSelection_message);
			fWorkingSetBlock.setSelection(initialWorkingSets);
			fWorkingSetBlock.createContent(workingSetGroup);
		}

		public IWorkingSet[] getSelectedWorkingSets() {
			return fWorkingSetBlock.getSelectedWorkingSets();
		}
	}

	/**
	 * Validate this page and show appropriate warnings and error
	 * NewWizardMessages.
	 */
	private final class Validator implements Observer {
		public void update(Observable o, Object arg) {
			final IWorkspace workspace = DLTKUIPlugin.getWorkspace();
			final String name = fNameGroup.getName();
			// check whether the project name field is empty
			if (name.length() == 0) {
				setErrorMessage(null);
				// Modify by Oliver. 2009-11-05.
				setMessage(
						NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterProjectName,
						IMessageProvider.ERROR);
				setPageComplete(false);
				return;
			}

			// check whether the project name is valid
			final IStatus nameStatus = workspace.validateName(name,
					IResource.PROJECT);
			if (!nameStatus.isOK()) {
				setErrorMessage(nameStatus.getMessage());
				setPageComplete(false);
				return;
			}
			// check whether project already exists
			final IProject handle = getProjectHandle();
			if (handle.exists()) {
				setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_projectAlreadyExists);
				setPageComplete(false);
				return;
			}
			final String location = fLocationGroup.getLocation().toOSString();
			// check whether location is empty
			if (location.length() == 0) {
				setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterLocation);
				// setMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_enterLocation);
				setPageComplete(false);
				return;
			}
			// check whether the location is a syntactically correct path
			if (!Path.EMPTY.isValidPath(location)) {
				setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_invalidDirectory);
				setPageComplete(false);
				return;
			}
			// check whether the location has the workspace as prefix
			IPath projectPath = Path.fromOSString(location);
			if (!fLocationGroup.isInWorkspace()
					&& Platform.getLocation().isPrefixOf(projectPath)) {
				setErrorMessage(NewWizardMessages.ScriptProjectWizardFirstPage_Message_cannotCreateInWorkspace);
				setPageComplete(false);
				return;
			}
			// If we do not place the contents in the workspace validate the
			// location.
			if (!fLocationGroup.isInWorkspace()) {
				IEnvironment environment = getEnvironment();
				if (EnvironmentManager.isLocal(environment)) {
					final IStatus locationStatus = workspace
							.validateProjectLocation(handle, projectPath);
					if (!locationStatus.isOK()) {
						setErrorMessage(locationStatus.getMessage());
						setPageComplete(false);
						return;
					}
				}
			}
			if (supportInterpreter() && interpeterRequired()) {
				if (!interpretersPresent) {
					setErrorMessage(NewWizardMessages.ProjectWizardFirstPage_atLeastOneInterpreterMustBeConfigured);
					setPageComplete(false);
					return;
				}
			}

			if (undefinedInterpreter) {
				setErrorMessage(NewWizardMessages.ProjectWizardFirstPage_atLeastOneDefaultInterpreterMustBeConfigured);
				setPageComplete(false);
				return;
			}

			// Add by Oliver. 2009-11-05. If the external location does not
			// exist, will give the warning and will create the related folder
			// automatically.
			IPath path = fLocationGroup.getLocation();
			Object[] returnValues = validateNonExistingProjectPath(path);
			if (returnValues != null) {
				setErrorMessage(null);
				setMessage((String) returnValues[0],
						((Integer) returnValues[1]).intValue());
				setPageComplete(true);
				return;
			}

			setPageComplete(true);
			setErrorMessage(null);
			setMessage(null);
		}
	}

	/**
	 * @param path
	 * @return
	 */
	public static Object[] validateNonExistingProjectPath(IPath path) {
		if (!path.toFile().exists()) {
			return new Object[] {
					NewWizardMessages.ScriptProjectWizardFirstPage_Unexist_Location_message,
					new Integer(IMessageProvider.WARNING) };

		}
		return null;
	}

	private NameGroup fNameGroup;
	protected LocationGroup fLocationGroup;
	// private LayoutGroup fLayoutGroup;
	// private InterpreterEnvironmentGroup fInterpreterEnvironmentGroup;
	private boolean interpretersPresent;
	protected DetectGroup fDetectGroup;
	private Validator fValidator;
	private String fInitialName;
	private static final String PAGE_NAME = NewWizardMessages.ScriptProjectWizardFirstPage_page_pageName;

	/**
	 * Create a new <code>SimpleProjectFirstPage</code>.
	 */
	public ProjectWizardFirstPage() {
		super(PAGE_NAME);
		setPageComplete(false);
		setTitle(NewWizardMessages.ScriptProjectWizardFirstPage_page_title);
		setDescription(NewWizardMessages.ScriptProjectWizardFirstPage_page_description);
		fInitialName = ""; //$NON-NLS-1$
	}

	public void setName(String name) {
		fInitialName = name;
		if (fNameGroup != null) {
			fNameGroup.setName(name);
		}
	}

	protected abstract boolean interpeterRequired();

	protected abstract boolean supportInterpreter();

	protected abstract void createInterpreterGroup(Composite parent);

	protected abstract void handlePossibleInterpreterChange();

	protected abstract Observable getInterpreterGroupObservable();

	protected abstract IInterpreterInstall getInterpreter();

	private WorkingSetGroup fWorkingSetGroup;

	private IWorkingSet[] fInitWorkingSets;

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), true));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		// create UI elements
		fNameGroup = new NameGroup(composite, fInitialName);
		fLocationGroup = new LocationGroup(composite);

		// fInterpreterEnvironmentGroup= new
		// InterpreterEnvironmentGroup(composite);
		// ProjectWizardFirstPage.AbstractInterpreterGroup interpGroup = null;
		if (supportInterpreter()) {
			createInterpreterGroup(composite);
		}
		createCustomGroups(composite);
		// fLayoutGroup= new LayoutGroup(composite);
		fDetectGroup = new DetectGroup(composite);
		// establish connections
		fNameGroup.addObserver(fLocationGroup);
		// fDetectGroup.addObserver(fLayoutGroup);
		// fDetectGroup.addObserver(fInterpreterEnvironmentGroup);

		fLocationGroup.addObserver(fDetectGroup);
		// initialize all elements
		fNameGroup.notifyObservers();
		// create and connect validator
		fValidator = new Validator();
		Observable interpreterGroupObservable = getInterpreterGroupObservable();
		if (supportInterpreter() && interpreterGroupObservable != null) {
			// fDetectGroup.addObserver(getInterpreterGroupObservable());
			interpreterGroupObservable.addObserver(fValidator);
			handlePossibleInterpreterChange();
		}
		fNameGroup.addObserver(fValidator);
		fLocationGroup.addObserver(fValidator);

		// Add by Oliver. Make VJO project to be organized by work set.
		fWorkingSetGroup = new WorkingSetGroup(composite, fInitWorkingSets);

		setControl(composite);
		Dialog.applyDialogFont(composite);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
		// IDLTKHelpContextIds.NEW_JAVAPROJECT_WIZARD_PAGE);
	}

	protected void createCustomGroups(Composite composite) {
	}

	/**
	 * Returns the current project location path as entered by the user, or its
	 * anticipated initial value. Note that if the default has been returned the
	 * path in a project description used to create a project should not be set.
	 * <p>
	 * TODO At some point this method has to be converted to return an URI
	 * instead of an path. However, this first requires support from Platform/UI
	 * to specify a project location different than in a local file system.
	 * </p>
	 * 
	 * @return the project location path or its anticipated initial value.
	 */
	public URI getLocationURI() {
		IEnvironment environment = getEnvironment();
		return environment.getURI(fLocationGroup.getLocation());
	}

	public IEnvironment getEnvironment() {
		return fLocationGroup.getEnvironment();
	}

	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot()
				.getProject(fNameGroup.getName());
	}

	public boolean isInWorkspace() {
		return fLocationGroup.isInWorkspace();
	}

	public String getProjectName() {
		return fNameGroup.getName();
	}

	public boolean getDetect() {
		return fDetectGroup.mustDetect();
	}

	public boolean isSrc() {
		return false;
		// return true;//fLayoutGroup.isSrcBin();
	}

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			fNameGroup.postSetFocus();
		}
	}

	/**
	 * Initialize a grid layout with the default Dialog settings.
	 */
	protected GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}

	/**
	 * Set the layout data for a button.
	 */
	protected GridData setButtonLayoutData(Button button) {
		return super.setButtonLayoutData(button);
	}

	/**
	 * @param workingSets
	 *            the initial selected working sets or <b>null</b>
	 */
	public void setWorkingSets(IWorkingSet[] workingSets) {
		fInitWorkingSets = workingSets;
	}

	/**
	 * @return the selected working sets, not <b>null</b>
	 */
	public IWorkingSet[] getWorkingSets() {
		return fWorkingSetGroup.getSelectedWorkingSets();
	}
}
