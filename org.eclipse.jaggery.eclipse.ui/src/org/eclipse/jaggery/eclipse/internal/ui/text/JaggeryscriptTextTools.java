package org.eclipse.jaggery.eclipse.internal.ui.text;

import org.eclipse.dltk.mod.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.mod.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.ui.texteditor.ITextEditor;

public class JaggeryscriptTextTools extends ScriptTextTools {
	
	private IPartitionTokenScanner fPartitionScanner;
	
	private final static String[] LEGAL_CONTENT_TYPES = new String[] {
		IJaggeryScriptPartitions.JAG_STRING,
		IJaggeryScriptPartitions.JAG_SINGLE_COMMENT,
		IJaggeryScriptPartitions.JAG_MULTI_COMMENT,
		IJaggeryScriptPartitions.JAG_DOC };

	protected JaggeryscriptTextTools( boolean autoDisposeOnDisplayDispose) {
		super(IJaggeryScriptPartitions.JAG_PARTITIONING, LEGAL_CONTENT_TYPES, autoDisposeOnDisplayDispose);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		// TODO Auto-generated method stub
		return null;
	}

}
