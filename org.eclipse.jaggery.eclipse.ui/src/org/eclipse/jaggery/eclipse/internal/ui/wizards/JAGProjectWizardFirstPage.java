package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.mod.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;
import org.eclipse.dltk.mod.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.jaggery.eclipse.core.JaggeryNature;
import org.eclipse.jaggery.eclipse.internal.ui.preferences.JAGBuildPathPreferencePage;
import org.eclipse.jaggery.eclipse.ui.JaggeryPreferenceConstants;
import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class JAGProjectWizardFirstPage extends ProjectWizardFirstPage {
	
	private JavascriptInterpreterGroup fInterpreterGroup;
	private LayoutGroup layoutGroup;

	@Override
	protected boolean interpeterRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean supportInterpreter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void createInterpreterGroup(Composite parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void handlePossibleInterpreterChange() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Observable getInterpreterGroupObservable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IInterpreterInstall getInterpreter() {
		// TODO Auto-generated method stub
		return null;
	}
	//private JavascriptInterpreterGroup fInterpreterGroup;
	

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent,
				IHelpContextIds.NEW_PROJECT);
	}

    
	protected void createCustomGroups(Composite composite) {
		this.layoutGroup = new LayoutGroup(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.mod.ui.wizards.ProjectWizardFirstPage#isSrc()
	 */
	public boolean isSrc() {
		return this.layoutGroup.isSrc();
	}
	
	private final class JavascriptInterpreterGroup extends
	AbstractInterpreterGroup {

public JavascriptInterpreterGroup(Composite composite) {
	super(composite);
}

@Override
protected String getCurrentLanguageNature() {
	return JaggeryNature.NATURE_ID;
}

@Override
protected String getIntereprtersPreferencePageId() {
	// Modify by Oliver. 2009-03-27. Fix the bug that can't open
	// the interpreter preference page.
	return "org.eclipse.jaggery.eclipse.preferences.interpreter";
}
}

//	private final class JavascriptInterpreterGroup extends
//			AbstractInterpreterGroup {
//
//		public JavascriptInterpreterGroup(Composite composite) {
//			super(composite);
//		}
//
//		@Override
//		protected String getCurrentLanguageNature() {
//			return JaggeryNature.NATURE_ID;
//		}
//
//		@Override
//		protected String getIntereprtersPreferencePageId() {
//			// Modify by Oliver. 2009-03-27. Fix the bug that can't open
//			// the interpreter preference page.
//			return "org.eclipse.vjet.eclipse.preferences.interpreter";
//		}
//	}

	/**
	 * a project layout for src folder configuration, similar to JDT
	 */
	private final class LayoutGroup implements Observer {

		private final SelectionButtonDialogField fStdRadio, fSrcRadio;
		private final Group fGroup;
		private final Link fPreferenceLink;

		public LayoutGroup(Composite composite) {

			fGroup = new Group(composite, SWT.NONE);
			fGroup.setFont(composite.getFont());
			fGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			fGroup.setLayout(initGridLayout(new GridLayout(3, false), true));
			fGroup
					.setText(JaggeryWizardMessages.NewScriptProjectWizardPage_LayoutGroup_title);

			fStdRadio = new SelectionButtonDialogField(SWT.RADIO);
			fStdRadio
					.setLabelText(JaggeryWizardMessages.NewScriptProjectWizardPage_LayoutGroup_option_oneFolder);

			fSrcRadio = new SelectionButtonDialogField(SWT.RADIO);
			fSrcRadio
					.setLabelText(JaggeryWizardMessages.NewScriptProjectWizardPage_LayoutGroup_option_separateFolders);

			fStdRadio.doFillIntoGrid(fGroup, 3);
			LayoutUtil
					.setHorizontalGrabbing(fStdRadio.getSelectionButton(null));

			fSrcRadio.doFillIntoGrid(fGroup, 2);

			fPreferenceLink = new Link(fGroup, SWT.NONE);
			fPreferenceLink
					.setText(JaggeryWizardMessages.NewScriptProjectWizardPage_LayoutGroup_link_description);
			fPreferenceLink.setLayoutData(new GridData(GridData.END,
					GridData.END, false, false));
			fPreferenceLink.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					String id = JAGBuildPathPreferencePage.ID;
					PreferencesUtil.createPreferenceDialogOn(getShell(), id,
							new String[] { id }, null).open();
				}

				public void widgetSelected(SelectionEvent e) {
					widgetDefaultSelected(e);
				}
			});

			boolean useSrcBin = JaggeryUIPlugin.getDefault().getPreferenceStore()
					.getBoolean(
							JaggeryPreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ);
			fSrcRadio.setSelection(useSrcBin);
			fStdRadio.setSelection(!useSrcBin);
		}

		public void update(Observable o, Object arg) {
			final boolean detect = fDetectGroup.mustDetect();
			fStdRadio.setEnabled(!detect);
			fSrcRadio.setEnabled(!detect);
			fPreferenceLink.setEnabled(!detect);
			fGroup.setEnabled(!detect);
		}

		public boolean isSrc() {
			return fSrcRadio.isSelected();
		}
	}

   
    

}
