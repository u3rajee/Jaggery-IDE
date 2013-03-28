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
package org.eclipse.dltk.mod.internal.debug.core.model;

import org.eclipse.dltk.mod.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.mod.debug.core.ISmartStepEvaluator;

public class SmartStepEvaluatorManager {
	private static final String SMART_STEP_EXTENSION = DLTKDebugPlugin.PLUGIN_ID
			+ ".smartStepEvaluator"; //$NON-NLS-1$
	private static PriorityClassDLTKExtensionManager manager = new PriorityClassDLTKExtensionManager(
			SMART_STEP_EXTENSION, "nature"); //$NON-NLS-1$
	
	public static ISmartStepEvaluator getEvaluator(String nature) {
		return (ISmartStepEvaluator) manager.getObject(nature);
	}
} 
