/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.codeassist;

import java.util.Map;

import org.eclipse.dltk.mod.internal.codeassist.impl.AssistOptions;
import org.eclipse.dltk.mod.internal.codeassist.impl.Engine;
import org.eclipse.dltk.mod.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.dltk.mod.internal.core.SearchableEnvironment;

public abstract class ScriptSelectionEngine extends Engine implements
		ISelectionEngine {

	public ScriptSelectionEngine() {
		super(null);
	}
	public void setEnvironment(SearchableEnvironment environment) {
		this.nameEnvironment = environment;
		this.lookupEnvironment = new LookupEnvironment(this, nameEnvironment);
	}

	public void setOptions(Map options) {
		this.options = new AssistOptions(options);
	}
}
