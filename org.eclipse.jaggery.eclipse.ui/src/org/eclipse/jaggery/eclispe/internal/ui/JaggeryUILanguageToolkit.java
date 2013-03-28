package org.eclipse.jaggery.eclispe.internal.ui;

import org.eclipse.dltk.mod.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.mod.ui.ScriptElementLabels;
import org.eclipse.dltk.mod.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.mod.ui.text.ScriptTextTools;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.jaggery.eclipse.core.*;
import org.eclipse.jaggery.eclipse.ui.JagElementLabels;
import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;

public class JaggeryUILanguageToolkit  extends AbstractDLTKUILanguageToolkit{
	
	private static ScriptElementLabels s_instance = new JagElementLabels();

	@Override
	public IDLTKLanguageToolkit getCoreToolkit() {
		// TODO Auto-generated method stub
		return JaggeryLanguageToolkit.getDefault();
	}

	@Override
	protected AbstractUIPlugin getUIPLugin() {
		// TODO Auto-generated method stub
		return JaggeryUIPlugin.getDefault();
	}
	
	@Override
	public String getEditorId(Object inputElement) {
		return "org.eclipse.jaggery.ui.JagEditor";
	}
	
	@Override
	public ScriptElementLabels getScriptElementLabels() {
		return s_instance;
		
	}
	
	@Override
	public ScriptTextTools getTextTools() {
		return super.getTextTools();
	}
	
	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new SimpleVjoSourceViewerConfiguration(getTextTools()
				.getColorManager(), getPreferenceStore(), null,
				getPartitioningId(), false);
	}
	
	

}


