/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.PriorityClassDLTKExtensionManager;

public class InternalDLTKLanguageManager {
	private final static String LANGUAGE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".language"; //$NON-NLS-1$
	private final static String SOURCE_ELEMENT_PARSERS_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".sourceElementParsers"; //$NON-NLS-1$
	private final static String SOURCE_PARSERS_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".sourceParsers"; //$NON-NLS-1$
	private final static String PROBLEM_FACTORY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".problemFactory"; //$NON-NLS-1$
	private final static String COMPLETION_ENGINE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".completionEngine"; //$NON-NLS-1$
	private final static String SELECTION_ENGINE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".selectionEngine"; //$NON-NLS-1$
	private final static String SEARCH_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".search"; //$NON-NLS-1$
	private final static String CALLHIERARCHY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".callHierarchy"; //$NON-NLS-1$

	private final static String FILE_HIERARCHY_RESOLVER_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".fileHierarchyResolvers";//$NON-NLS-1$

	private final static String INTERPRETER_CONTAINER_EXTENSION_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".interpreterContainerExtension";//$NON-NLS-1$

	// EBAY - START MOD
	private final static String SOURCE_MODULE_FACTORY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".sourceModuleFactories"; //$NON-NLS-1$
	private final static String BUILD_PROBLEM_REPORTER_FACTORY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".buildProblemReporterFactory"; //$NON-NLS-1$
	// EBAY -- STOP MOD
	private static PriorityClassDLTKExtensionManager languageToolkitsManager = new PriorityClassDLTKExtensionManager(
			LANGUAGE_EXTPOINT);

	// Inner managers
	private static PriorityClassDLTKExtensionManager sourceElementParsersManager = new NewInstanceClassBasedDLTKExtensionManager(
			SOURCE_ELEMENT_PARSERS_EXTPOINT);
	private static PriorityClassDLTKExtensionManager problemFactoryManager = new PriorityClassDLTKExtensionManager(
			PROBLEM_FACTORY_EXTPOINT);

	private static PriorityClassDLTKExtensionManager selectionEngineManager = new NewInstanceClassBasedDLTKExtensionManager(
			SELECTION_ENGINE_EXTPOINT);
	private static PriorityClassDLTKExtensionManager completionEngineManager = new NewInstanceClassBasedDLTKExtensionManager(
			COMPLETION_ENGINE_EXTPOINT, true);
	private static PriorityClassDLTKExtensionManager sourceParsersManager = new NewInstanceClassBasedDLTKExtensionManager(
			SOURCE_PARSERS_EXTPOINT);

	private static PriorityClassDLTKExtensionManager searchManager = new PriorityClassDLTKExtensionManager(
			SEARCH_EXTPOINT);
	private static PriorityClassDLTKExtensionManager callHierarchyManager = new PriorityClassDLTKExtensionManager(
			CALLHIERARCHY_EXTPOINT);
	private static PriorityClassDLTKExtensionManager fileHierarchyResolversManager = new PriorityClassDLTKExtensionManager(
			FILE_HIERARCHY_RESOLVER_EXTPOINT);

	private static PriorityClassDLTKExtensionManager interoreterContainerExtensionManager = new PriorityClassDLTKExtensionManager(
			INTERPRETER_CONTAINER_EXTENSION_EXTPOINT);

	// EBAY - START MOD
	private static PriorityClassDLTKExtensionManager sourceModuleFactoriesManager = new PriorityClassDLTKExtensionManager(
			SOURCE_MODULE_FACTORY_EXTPOINT);
	private static PriorityClassDLTKExtensionManager buildProblemReporterFactoryManager = new PriorityClassDLTKExtensionManager(
			BUILD_PROBLEM_REPORTER_FACTORY_EXTPOINT);

	// EBAY -- STOP MOD
	public static PriorityClassDLTKExtensionManager getSourceElementParsersManager() {
		return sourceElementParsersManager;
	}

	public static PriorityClassDLTKExtensionManager getProblemFactoryManager() {
		return problemFactoryManager;
	}

	public static PriorityClassDLTKExtensionManager getSelectionEngineManager() {
		return selectionEngineManager;
	}

	public static PriorityClassDLTKExtensionManager getCompletionEngineManager() {
		return completionEngineManager;
	}

	public static PriorityClassDLTKExtensionManager getSourceParsersManager() {
		return sourceParsersManager;
	}

	public static PriorityClassDLTKExtensionManager getSearchManager() {
		return searchManager;
	}

	public static PriorityClassDLTKExtensionManager getCallHierarchyManager() {
		return callHierarchyManager;
	}

	public static PriorityClassDLTKExtensionManager getFileHierarchyResolversManager() {
		return fileHierarchyResolversManager;
	}

	public static PriorityClassDLTKExtensionManager getLanguageToolkitsManager() {
		return languageToolkitsManager;
	}

	public static PriorityClassDLTKExtensionManager getInterpreterContainerExtensionManager() {
		return interoreterContainerExtensionManager;
	}

	// EBAY - START MOD
	public static PriorityClassDLTKExtensionManager getSourceModuleFactoriesManager() {
		return sourceModuleFactoriesManager;
	}

	public static PriorityClassDLTKExtensionManager getBuildProblemReporterFactoryManager() {
		return buildProblemReporterFactoryManager;
	}
	// EBAY -- STOP MOD
}
