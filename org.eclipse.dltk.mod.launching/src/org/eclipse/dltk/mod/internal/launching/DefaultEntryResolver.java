/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.launching;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;
import org.eclipse.dltk.mod.launching.IRuntimeBuildpathEntry;
import org.eclipse.dltk.mod.launching.IRuntimeBuildpathEntry2;
import org.eclipse.dltk.mod.launching.IRuntimeBuildpathEntryResolver;
import org.eclipse.dltk.mod.launching.ScriptRuntime;


/**
 * Default resolver for a contributed buildpath entry
 */
public class DefaultEntryResolver implements IRuntimeBuildpathEntryResolver {

	public IRuntimeBuildpathEntry[] resolveRuntimeBuildpathEntry(IRuntimeBuildpathEntry entry, ILaunchConfiguration configuration) throws CoreException {
		IRuntimeBuildpathEntry2 entry2 = (IRuntimeBuildpathEntry2)entry;
		IRuntimeBuildpathEntry[] entries = entry2.getRuntimeBuildpathEntries(configuration);
		List resolved = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeBuildpathEntry[] temp = ScriptRuntime.resolveRuntimeBuildpathEntry(entries[i], configuration);
			for (int j = 0; j < temp.length; j++) {
				resolved.add(temp[j]);
			}
		}
		return (IRuntimeBuildpathEntry[]) resolved.toArray(new IRuntimeBuildpathEntry[resolved.size()]);
	}
	
	public IRuntimeBuildpathEntry[] resolveRuntimeBuildpathEntry(IRuntimeBuildpathEntry entry, IScriptProject project) throws CoreException {
		IRuntimeBuildpathEntry2 entry2 = (IRuntimeBuildpathEntry2)entry;
		IRuntimeBuildpathEntry[] entries = entry2.getRuntimeBuildpathEntries(null);
		List resolved = new ArrayList();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeBuildpathEntry[] temp = ScriptRuntime.resolveRuntimeBuildpathEntry(entries[i], project);
			for (int j = 0; j < temp.length; j++) {
				resolved.add(temp[j]);
			}
		}
		return (IRuntimeBuildpathEntry[]) resolved.toArray(new IRuntimeBuildpathEntry[resolved.size()]);
	}
		
	public IInterpreterInstall resolveInterpreterInstall(String lang, String environment, IBuildpathEntry entry) throws CoreException {
		return null;
	}
}
