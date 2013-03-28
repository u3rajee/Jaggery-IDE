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

import org.eclipse.dltk.mod.compiler.env.ISourceModule;
import org.eclipse.dltk.mod.core.CompletionRequestor;
import org.eclipse.dltk.mod.core.IScriptProject;

public interface ICompletionEngine {
	void complete(ISourceModule module, int position, int i);

	void setRequestor(CompletionRequestor requestor);

	void setOptions(Map options);

	void setProject(IScriptProject project);
}
