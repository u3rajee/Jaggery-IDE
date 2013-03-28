package org.eclipse.jaggery.eclipse.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jaggery.eclipse.ui.JaggeryPreferenceConstants;
import org.eclipse.jaggery.eclipse.ui.JaggeryPreferenceConstants;
import org.eclipse.dltk.mod.ui.preferences.AbstractConfigurationBlock;
import org.eclipse.dltk.mod.ui.preferences.OverlayPreferenceStore;
import org.eclipse.dltk.mod.ui.preferences.OverlayPreferenceStore.OverlayKey;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class JAGBuildPathConfigurationBlock extends AbstractConfigurationBlock {
	
	private Button fFoldersAsSourceFolder;
	private Button fProjectAsSourceFolder;
	
	private Label fSrcFolderNameLabel;
	private Text fSrcFolderNameText;
	
	private SelectionListener fSelectionListener;
	private ModifyListener fModifyListener;
	
	private static final String SRCBIN_FOLDERS_IN_NEWPROJ = JaggeryPreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ;
	private static final String SRCBIN_SRCNAME = JaggeryPreferenceConstants.SRC_SRCNAME;

	public JAGBuildPathConfigurationBlock(OverlayPreferenceStore store) {
		super(store);
	store.addKeys(this.createOverlayStoreKeys());
		
		this.fSelectionListener =  new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}

			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() == fProjectAsSourceFolder) {
					fSrcFolderNameLabel.setEnabled(false);
					fSrcFolderNameText.setEnabled(false);
				}
				else {
					fSrcFolderNameLabel.setEnabled(true);
					fSrcFolderNameText.setEnabled(true);
				}
			}
		};
		
		this.fModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				//TODO: validate source folder name
			}
		};
	}
	
	private OverlayKey[] createOverlayStoreKeys() {
		List<OverlayKey> overlayKeys = new ArrayList<OverlayKey>();
		overlayKeys.add(new OverlayKey(OverlayPreferenceStore.BOOLEAN,
				JaggeryPreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ));
		overlayKeys.add(new OverlayKey(OverlayPreferenceStore.STRING,
				JaggeryPreferenceConstants.SRC_SRCNAME));
		return overlayKeys.toArray(new OverlayKey[overlayKeys.size()]);
	}
	
	
	
	
	@Override
	public Control createControl(Composite parent) {
		// TODO Auto-generated method stub
		initializeDialogUnits(parent);
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		
		Group sourceFolderGroup= new Group(composite, SWT.NONE);
		sourceFolderGroup.setLayout(new GridLayout(2, false));
		sourceFolderGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sourceFolderGroup.setText(JaggeryPreferenceMessages.NewJAGProjectPreferencePage_sourcefolder_label); 
		
		this.fProjectAsSourceFolder = addRadioButton(sourceFolderGroup, JaggeryPreferenceMessages.NewJAGProjectPreferencePage_sourcefolder_project, SRCBIN_FOLDERS_IN_NEWPROJ, IPreferenceStore.FALSE, 0);
		this.fProjectAsSourceFolder.addSelectionListener(this.fSelectionListener);
		
		this.fFoldersAsSourceFolder = addRadioButton(sourceFolderGroup, JaggeryPreferenceMessages.NewJAGProjectPreferencePage_sourcefolder_folder, SRCBIN_FOLDERS_IN_NEWPROJ, IPreferenceStore.TRUE, 0);
		this.fFoldersAsSourceFolder.addSelectionListener(this.fSelectionListener);
		this.fSrcFolderNameLabel = new Label(sourceFolderGroup, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalIndent = 10;
		this.fSrcFolderNameLabel.setLayoutData(gridData);
		this.fSrcFolderNameLabel.setText(JaggeryPreferenceMessages.NewJAGProjectPreferencePage_folders_src);
		this.fSrcFolderNameText = addTextControl(sourceFolderGroup, this.fSrcFolderNameLabel, SRCBIN_SRCNAME, 10);
		this.fSrcFolderNameText.addModifyListener(this.fModifyListener);
		
		return composite;
	}
	
	
	private Button addRadioButton(Composite parent, String label, String key, String value, int indent) { 
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		gd.horizontalIndent= indent;
		
		Button button= new Button(parent, SWT.RADIO);
		button.setText(label);
		button.setLayoutData(gd);
		button.setSelection(value.equals(getPreferenceStore().getString(key)));
		return button;
	}
	
	private Text addTextControl(Composite parent, Label labelControl, String key, int indent) {
		GridData gd= new GridData();
		gd.horizontalIndent= indent;
		
		labelControl.setLayoutData(gd);
		
		gd= new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint= convertWidthInCharsToPixels(30);
		
		Text text= new Text(parent, SWT.SINGLE | SWT.BORDER);
		text.setText(getPreferenceStore().getString(key));
		text.setLayoutData(gd);
		return text;
	}

}




