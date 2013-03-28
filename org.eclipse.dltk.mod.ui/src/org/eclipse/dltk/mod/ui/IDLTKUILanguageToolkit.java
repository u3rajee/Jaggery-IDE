/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui;

import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.mod.ui.text.ScriptTextTools;
import org.eclipse.dltk.mod.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;

public interface IDLTKUILanguageToolkit {
	ScriptElementLabels getScriptElementLabels();

	ScriptUILabelProvider createScriptUILabelProvider();
	
	IDLTKLanguageToolkit getCoreToolkit();

	IPreferenceStore getPreferenceStore();

	String getPartitioningId();

	String getEditorId(Object inputElement);

	String getInterpreterContainerId();
	
	ScriptTextTools getTextTools();
	
	ScriptSourceViewerConfiguration createSourceViewerConfiguration();
	
	// Per module script explorer show children way.
	boolean getProvideMembers(ISourceModule element);
	
	String getInterpreterPreferencePage();
	
	String getDebugPreferencePage();
	
	String[] getEditorPreferencePages();
}
