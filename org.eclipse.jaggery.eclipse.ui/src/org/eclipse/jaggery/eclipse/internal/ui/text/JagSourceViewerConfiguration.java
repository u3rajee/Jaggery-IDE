package org.eclipse.jaggery.eclipse.internal.ui.text;

import org.eclipse.dltk.mod.ui.text.IColorManager;
import org.eclipse.dltk.mod.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.mod.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class JagSourceViewerConfiguration  extends ScriptSourceViewerConfiguration{

	public JagSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		// TODO Auto-generated method stub
		return null;
	}

}
