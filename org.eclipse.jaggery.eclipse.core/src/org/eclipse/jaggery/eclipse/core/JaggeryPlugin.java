package org.eclipse.jaggery.eclipse.core;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class JaggeryPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.jaggery.eclipse.core"; //$NON-NLS-1$
	
	public static final String BUILDER_ID = PLUGIN_ID + ".builder";
	
	public static final String JAGNATIVESDK_ID = PLUGIN_ID + ".JAGNATIVE_CONTAINER";
	public static final String JAG_DEFAULT_SDK_LABEL = "JAG Native Types";
	
	public static final String BROWSERSDK_ID = "org.eclipse.jaggery.eclipse.core.BROWSER_CONTAINER";
	public static final String BROWSERSDK_LABEL = "Browser SDK";
	
	public static final String JAGLIB_ID = "org.eclipse.jaggery.eclipse.core.JAG_CONTAINER";
	public static final String JAGLIB_LABEL = "JAG LIB";
	
	public static final String SDK_CONTAINER = "org.eclipse.jaggery.eclipse.core" + ".SDK_CONTAINER";
	public static final String ID_DEFAULT_SDK = "DEFUALT_SDK";

	// The shared instance
	private static JaggeryPlugin plugin;
	
	/**
	 * The constructor
	 */
	public JaggeryPlugin() {
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
	public static JaggeryPlugin getDefault() {
		return plugin;
	}

}
