package org.eclipse.jaggery.eclipse.ui;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.ui.PluginImagesHelper;
import org.eclipse.jface.resource.ImageDescriptor;

public class JaggeryScriptImages {
	private static final PluginImagesHelper helper = new PluginImagesHelper(JaggeryUIPlugin.getDefault()
			.getBundle(), new Path("/icons/"));

	public static final ImageDescriptor DESC_WIZBAN_PROJECT_CREATION = helper
			.createUnManaged(PluginImagesHelper.T_WIZBAN, "newjscriptfile_wiz.gif");

}
