package org.eclipse.jaggery.eclipse.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JaggeryUIPlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.jaggery.eclipse.ui"; //$NON-NLS-1$

	// The shared instance
	private static JaggeryUIPlugin plugin;
	
	
	
	/**
	 * The constructor
	 */
	public JaggeryUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static JaggeryUIPlugin getDefault() {
		return plugin;
	}

	@Override
	public void earlyStartup() {
		// TODO Auto-generated method stub
		
	}
	
	public static void log(int severity, String message) {
		log(new Status(severity, PLUGIN_ID, severity, message, null));
	}

	public static void log(Exception e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, null, e));
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	

}
