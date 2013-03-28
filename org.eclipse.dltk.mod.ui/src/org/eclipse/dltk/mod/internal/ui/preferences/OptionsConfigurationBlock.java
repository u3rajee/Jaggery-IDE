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
package org.eclipse.dltk.mod.internal.ui.preferences;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.internal.ui.util.CoreUtility;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.preferences.PreferenceKey;
import org.eclipse.dltk.mod.ui.util.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.preferences.ScrolledPageContent;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.eclipse.ui.preferences.WorkingCopyManager;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Direct port from the jdt ui, this class should not be extended by anyone but
 * the internal dltk ui.
 * 
 * @see org.eclipse.dltk.mod.ui.preferences.AbstractOptionsBlock
 */
public abstract class OptionsConfigurationBlock {

	private static final String REBUILD_COUNT_KEY = "preferences_build_requested"; //$NON-NLS-1$

	protected IStatusChangeListener fContext;
	protected final IProject fProject; // project or null
	protected final PreferenceKey[] fAllKeys;

	private IScopeContext[] fLookupOrder;

	private Shell fShell;

	private final IWorkingCopyManager fManager;
	private IWorkbenchPreferenceContainer fContainer;

	// null when project specific settings are turned off
	private Map fDisabledProjectSettings;

	// used to prevent multiple dialogs that ask for a rebuild
	private int fRebuildCount;

	protected static class ControlData {
		private PreferenceKey fKey;
		private String[] fValues;

		public ControlData(PreferenceKey key, String[] values) {
			fKey = key;
			fValues = values;
		}

		public PreferenceKey getKey() {
			return fKey;
		}

		public String getValue(boolean selection) {
			int index = selection ? 0 : 1;
			return fValues[index];
		}

		public String getValue(int index) {
			return fValues[index];
		}

		public int getSelection(String value) {
			if (value != null) {
				for (int i = 0; i < fValues.length; i++) {
					if (value.equals(fValues[i])) {
						return i;
					}
				}
			}
			return fValues.length - 1; // assume the last option is the least
			// severe
		}
	}

	public OptionsConfigurationBlock(IStatusChangeListener context,
			IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container) {
		fContext = context;
		fProject = project;
		fAllKeys = allKeys;
		fContainer = container;
		if (container == null) {
			fManager = new WorkingCopyManager();
		} else {
			fManager = container.getWorkingCopyManager();
		}

		if (fProject != null) {
			fLookupOrder = new IScopeContext[] { new ProjectScope(fProject),
					new InstanceScope(), new DefaultScope() };
		} else {
			fLookupOrder = new IScopeContext[] { new InstanceScope(),
					new DefaultScope() };
		}
		this.initPreferenceKey();
		testIfOptionsComplete(allKeys);
		if (fProject == null || hasProjectSpecificOptions(fProject)) {
			fDisabledProjectSettings = null;
		} else {
			fDisabledProjectSettings = new IdentityHashMap();
			for (int i = 0; i < allKeys.length; i++) {
				PreferenceKey curr = allKeys[i];
				fDisabledProjectSettings.put(curr, curr.getStoredValue(
						fLookupOrder, false, fManager));
			}
		}

		fRebuildCount = getRebuildCount();
	}

	protected final IWorkbenchPreferenceContainer getPreferenceContainer() {
		return fContainer;
	}

	protected static PreferenceKey getKey(String plugin, String key) {
		return new PreferenceKey(plugin, key);
	}

	public void initPreferenceKey() {

	}

	public void testIfOptionsComplete(PreferenceKey[] allKeys) {
		for (int i = 0; i < allKeys.length; i++) {
			if (allKeys[i].getStoredValue(fLookupOrder, false, fManager) == null) {
				DLTKUIPlugin
						.logErrorMessage("preference option missing: " + allKeys[i] + " (" + this.getClass().getName() + ')'); //$NON-NLS-1$//$NON-NLS-2$
			}
		}
	}

	private int getRebuildCount() {
		return fManager.getWorkingCopy(
				new DefaultScope().getNode(DLTKUIPlugin.PLUGIN_ID)).getInt(
				REBUILD_COUNT_KEY, 0);
	}

	private void incrementRebuildCount() {
		fRebuildCount++;
		fManager.getWorkingCopy(
				new DefaultScope().getNode(DLTKUIPlugin.PLUGIN_ID)).putInt(
				REBUILD_COUNT_KEY, fRebuildCount);
	}

	public boolean hasProjectSpecificOptions(IProject project) {
		if (project != null) {
			IScopeContext projectContext = new ProjectScope(project);
			PreferenceKey[] allKeys = fAllKeys;
			for (int i = 0; i < allKeys.length; i++) {
				if (allKeys[i].getStoredValue(projectContext, fManager) != null) {
					return true;
				}
			}
		}
		return false;
	}

	protected Shell getShell() {
		return fShell;
	}

	protected void setShell(Shell shell) {
		fShell = shell;
	}

	protected abstract Control createContents(Composite parent);

	protected boolean checkValue(PreferenceKey key, String value) {
		return value.equals(getValue(key));
	}

	protected String getValue(PreferenceKey key) {
		if (fDisabledProjectSettings != null) {
			return (String) fDisabledProjectSettings.get(key);
		}
		return key.getStoredValue(fLookupOrder, false, fManager);
	}

	protected boolean getBooleanValue(PreferenceKey key) {
		return Boolean.valueOf(getValue(key)).booleanValue();
	}

	protected String setValue(PreferenceKey key, String value) {
		if (fDisabledProjectSettings != null) {
			return (String) fDisabledProjectSettings.put(key, value);
		}
		String oldValue = getValue(key);
		key.setStoredValue(fLookupOrder[0], value, fManager);
		return oldValue;
	}

	protected String setValue(PreferenceKey key, boolean value) {
		return setValue(key, String.valueOf(value));
	}

	private boolean getChanges(IScopeContext currContext, List changedSettings) {
		// complete when project settings are enabled
		boolean completeSettings = fProject != null
				&& fDisabledProjectSettings == null;
		boolean needsBuild = false;

		/*
		 * XXX: need to rework this once there are options this affects - this
		 * can cause an illegal state exception - probably due to the fact that
		 * key binding is different from the jdt implementation
		 */
		for (int i = 0; i < fAllKeys.length; i++) {
			try {
				PreferenceKey key = fAllKeys[i];
				String oldVal = key.getStoredValue(currContext, null);
				String val = key.getStoredValue(currContext, fManager);
				if (val == null) {
					if (oldVal != null) {
						changedSettings.add(key);
						needsBuild |= !oldVal.equals(key.getStoredValue(
								fLookupOrder, true, fManager));
					} else if (completeSettings) {
						key.setStoredValue(currContext, key.getStoredValue(
								fLookupOrder, true, fManager), fManager);
						changedSettings.add(key);
						// no build needed
					}
				} else if (!val.equals(oldVal)) {
					changedSettings.add(key);
					needsBuild |= oldVal != null
							|| !val.equals(key.getStoredValue(fLookupOrder,
									true, fManager));
				}
			} catch (IllegalStateException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return needsBuild;
	}

	public void useProjectSpecificSettings(boolean enable) {
		boolean hasProjectSpecificOption = fDisabledProjectSettings == null;
		if (enable != hasProjectSpecificOption && fProject != null) {
			if (enable) {
				for (int i = 0; i < fAllKeys.length; i++) {
					PreferenceKey curr = fAllKeys[i];
					String val = (String) fDisabledProjectSettings.get(curr);
					curr.setStoredValue(fLookupOrder[0], val, fManager);
				}
				fDisabledProjectSettings = null;
			} else {
				fDisabledProjectSettings = new IdentityHashMap();
				for (int i = 0; i < fAllKeys.length; i++) {
					PreferenceKey curr = fAllKeys[i];
					String oldSetting = curr.getStoredValue(fLookupOrder,
							false, fManager);
					fDisabledProjectSettings.put(curr, oldSetting);
					// clear project settings
					curr.setStoredValue(fLookupOrder[0], null, fManager);
				}
			}
		}
	}

	public boolean areSettingsEnabled() {
		return fDisabledProjectSettings == null || fProject == null;
	}

	public boolean performOk() {
		return processChanges(fContainer);
	}

	public boolean performApply() {
		// apply directly
		return processChanges(null);
	}

	protected boolean ruleChanged() {
		IScopeContext currContext = fLookupOrder[0];

		List /* <Key> */changedOptions = new ArrayList();
		getChanges(currContext, changedOptions);
		if (changedOptions.isEmpty()) {
			return false;
		}
		return true;
	}

	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		IScopeContext currContext = fLookupOrder[0];

		List /* <Key> */changedOptions = new ArrayList();
		boolean needsBuild = getChanges(currContext, changedOptions);
		if (changedOptions.isEmpty()) {
			return true;
		}
		if (needsBuild) {
			int count = getRebuildCount();
			if (count > fRebuildCount) {
				needsBuild = false; // build already requested
				fRebuildCount = count;
			}
		}

		boolean doBuild = false;
		if (needsBuild) {
			String[] strings = getFullBuildDialogStrings(fProject == null);
			if (strings != null) {
				MessageDialog dialog = new MessageDialog(getShell(),
						strings[0], null, strings[1], MessageDialog.QUESTION,
						new String[] { IDialogConstants.YES_LABEL,
								IDialogConstants.NO_LABEL,
								IDialogConstants.CANCEL_LABEL }, 2);
				int res = dialog.open();
				if (res == 0) {
					doBuild = true;
				} else if (res != 1) {
					return false; // cancel pressed
				}
			}
		}
		try {
			fManager.applyChanges();
			changesSaved();
		} catch (BackingStoreException e) {
			DLTKUIPlugin.log(e);
			return false;
		}
		if (container != null) {
			// no need to apply the changes to the original store: will be done
			// by the page container
			if (doBuild) { // post build
				incrementRebuildCount();
				container.registerUpdateJob(CoreUtility.getBuildJob(fProject));
			}
		} else {
			// apply changes right away
			if (doBuild) {
				CoreUtility.getBuildJob(fProject).schedule();
			}

		}
		return true;
	}

	/**
	 * Add by Jack, after the change has been saved, the child class may need do
	 * there own operations. add the additional here. for example: reload all
	 * the rules.
	 * 
	 * @param currContext
	 */
	protected void changesSaved() {
		// Do nothing

	}

	public void performDefaults() {
		for (int i = 0; i < fAllKeys.length; i++) {
			PreferenceKey curr = fAllKeys[i];
			String origValue = curr
					.getStoredValue(fLookupOrder, true, fManager);
			setValue(curr, origValue);
		}
	}

	protected void updateControls() {
		// update the UI
	}

	protected abstract String[] getFullBuildDialogStrings(
			boolean workspaceSettings);

	public void dispose() {
	}

	protected ExpandableComposite createStyleSection(Composite parent,
			String label, int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(parent,
				SWT.NONE, ExpandableComposite.TWISTIE
						| ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		makeScrollableCompositeAware(excomposite);
		return excomposite;
	}

	protected void makeScrollableCompositeAware(Control control) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.adaptChild(control);
		}
	}

	protected ScrolledPageContent getParentScrolledComposite(Control control) {
		Control parent = control.getParent();
		while (!(parent instanceof ScrolledPageContent) && parent != null) {
			parent = parent.getParent();
		}
		if (parent instanceof ScrolledPageContent) {
			return (ScrolledPageContent) parent;
		}
		return null;
	}

	protected final void expandedStateChanged(ExpandableComposite expandable) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.reflow(true);
		}
	}

}