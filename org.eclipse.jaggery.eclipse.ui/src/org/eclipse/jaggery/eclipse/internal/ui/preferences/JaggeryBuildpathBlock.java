package org.eclipse.jaggery.eclipse.internal.ui.preferences;


import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.dltk.mod.ui.util.IStatusChangeListener;
import org.eclipse.dltk.mod.ui.wizards.BuildpathsBlock;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class JaggeryBuildpathBlock extends BuildpathsBlock {
	
	public JaggeryBuildpathBlock(IRunnableContext runnableContext,
			IStatusChangeListener context, int pageToShow, boolean useNewPage,
			IWorkbenchPreferenceContainer pageContainer){
		
		super(runnableContext, context, pageToShow, useNewPage, pageContainer);
	}

	@Override
	protected boolean supportZips() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected IPreferenceStore getPreferenceStore() {
		// TODO Auto-generated method stub
		return JaggeryUIPlugin.getDefault().getPreferenceStore();
	}

}


