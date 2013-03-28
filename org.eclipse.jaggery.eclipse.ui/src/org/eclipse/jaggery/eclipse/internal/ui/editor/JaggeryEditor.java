package org.eclipse.jaggery.eclipse.internal.ui.editor;

import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.internal.ui.editor.ScriptEditor;
import org.eclipse.jaggery.eclipse.core.JaggeryLanguageToolkit;
import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;



public class JaggeryEditor extends ScriptEditor   {
	
	public static final String EDITOR_ID = "org.eclipse.jaggery.ui.JagEditor";

	public static final String EDITOR_CONTEXT = "#JaggeryScriptEditorContext";

	@Override
	protected IPreferenceStore getScriptPreferenceStore() {
		// TODO Auto-generated method stub
		return JaggeryUIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public String getEditorId() {
		// TODO Auto-generated method stub
		return EDITOR_ID;
	}

	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		// TODO Auto-generated method stub
		return JaggeryLanguageToolkit.getDefault();
	}
	
}
