/*******************************************************************************
 * Copyright (c) 2012 eBay Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.launching.debug;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.mod.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.mod.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.mod.internal.launching.debug.DebuggingEngine;
import org.eclipse.dltk.mod.launching.IInterpreterRunnerFactory;

public class DebuggingEngineManager extends DLTKContributionExtensionManager {
	private static final String DEBUGGING_ENGINE_EXT_POINT = DLTKLaunchingPlugin.PLUGIN_ID
			+ ".debuggingEngine"; //$NON-NLS-1$

	private static final String ENGINE_TAG = "engine"; //$NON-NLS-1$

	private static DebuggingEngineManager instance;

	public static DebuggingEngineManager getInstance() {
		if (instance == null) {
			instance = new DebuggingEngineManager();
		}

		return instance;
	}

	public IDebuggingEngine getDebuggingEngine(String id) {
		return (IDebuggingEngine) getContributionById(id);
	}

	/**
	 * Returns selected debugging engine for script language with natureId. Uses
	 * default debugging engine selector (priority based) if custom selector is
	 * not contributed.
	 * 
	 * @param natureId
	 * 
	 * @return Selected debugging engine or null (if there are no debugging
	 *         engines at all or there are no selected engines)
	 */
	public IDebuggingEngine getSelectedDebuggingEngine(IProject project, String natureId) {
		return (IDebuggingEngine) getSelectedContribution(project, natureId);
	}

	/**
	 * Returns if script language with nature natureId has selected debugging
	 * engine. If this method returns false then getSelectedDebuggingEngine
	 * returns null.
	 * 
	 * @param natureId
	 *            nature id
	 * 
	 * @return true if the nature has a selected debugging engine, false
	 *         otherwise
	 */
	public boolean hasSelectedDebuggingEngine(IProject project, String natureId) {
		return getSelectedDebuggingEngine(project, natureId) != null;
	}

	/*
	 * @see org.eclipse.dltk.mod.core.DLTKContributionExtensionManager#configureContribution(java.lang.Object,
	 *      org.eclipse.core.runtime.IConfigurationElement)
	 */
	protected Object configureContribution(Object object,
			IConfigurationElement config) {
		return new DebuggingEngine((IInterpreterRunnerFactory) object, config);
	}

	/*
	 * @see org.eclipse.dltk.mod.core.DLTKContributionExtensionManager#getContributionElementName()
	 */
	protected String getContributionElementName() {
		return ENGINE_TAG;
	}

	/*
	 * @see org.eclipse.dltk.mod.core.DLTKContributionExtensionManager#getExtensionPoint()
	 */
	protected String getExtensionPoint() {
		return DEBUGGING_ENGINE_EXT_POINT;
	}

	/*
	 * @see org.eclipse.dltk.mod.core.DLTKContributionExtensionManager#isValidContribution(java.lang.Object)
	 */
	protected boolean isValidContribution(Object object) {
		return (object instanceof IInterpreterRunnerFactory);
	}
}
