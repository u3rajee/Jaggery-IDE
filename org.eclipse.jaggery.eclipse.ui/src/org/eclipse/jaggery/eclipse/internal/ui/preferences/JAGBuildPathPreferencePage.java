package org.eclipse.jaggery.eclipse.internal.ui.preferences;

import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.dltk.mod.ui.preferences.AbstractConfigurationBlockPreferencePage;
import org.eclipse.dltk.mod.ui.preferences.IPreferenceConfigurationBlock;
import org.eclipse.dltk.mod.ui.preferences.OverlayPreferenceStore;

public class JAGBuildPathPreferencePage extends AbstractConfigurationBlockPreferencePage {

	public static final String ID = "org.eclipse.jaggery.eclipse.ui.jaggery.BuildPath";

	@Override
	protected String getHelpId() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected void setDescription() {
		// TODO Auto-generated method stub
		setDescription(JaggeryPreferenceMessages.NewJAGProjectPreferencePage_description);
		
	}

	@Override
	protected void setPreferenceStore() {
		// TODO Auto-generated method stub
		setPreferenceStore(JaggeryUIPlugin.getDefault().getPreferenceStore());
	}

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		// TODO Auto-generated method stub
		return new JAGBuildPathConfigurationBlock(overlayPreferenceStore);
	}
}

