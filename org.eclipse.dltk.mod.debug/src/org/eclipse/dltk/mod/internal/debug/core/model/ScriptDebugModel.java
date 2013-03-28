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
package org.eclipse.dltk.mod.internal.debug.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.debug.core.ScriptDebugManager;
import org.eclipse.dltk.mod.debug.core.model.IScriptLineBreakpoint;
import org.eclipse.dltk.mod.debug.core.model.IScriptMethodEntryBreakpoint;
import org.eclipse.dltk.mod.debug.core.model.IScriptSpawnpoint;
import org.eclipse.dltk.mod.debug.core.model.IScriptWatchpoint;

public class ScriptDebugModel {

	public static String getDebugModelId(IResource resource) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.findToolkitForResource(resource);
		if (toolkit != null) {
			String natureId = toolkit.getNatureId();
			return ScriptDebugManager.getInstance().getDebugModelByNature(
					natureId);
		}
		return null;
	}

	public static IScriptLineBreakpoint createLineBreakpoint(
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map attributes) throws CoreException {

		return new ScriptLineBreakpoint(getDebugModelId(resource), resource,
				path, lineNumber, charStart, charEnd, register);
	}

	public static IScriptLineBreakpoint createLineBreakpoint(
			String debugModelId, IResource resource, IPath path,
			int lineNumber, int charStart, int charEnd, boolean register,
			Map attributes) throws CoreException {

		return new ScriptLineBreakpoint(debugModelId, resource, path,
				lineNumber, charStart, charEnd, register);
	}

	public static IScriptSpawnpoint createSpawnpoint(String debugModelId,
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map attributes) throws CoreException {
		return new ScriptSpawnpoint(debugModelId, resource, path, lineNumber,
				charStart, charEnd, register);
	}

	public static IScriptMethodEntryBreakpoint createMethodEntryBreakpoint(
			IResource resource, IPath path, int lineNumber, int charStart,
			int charEnd, boolean register, Map attributes, String methodName)
			throws CoreException {

		return new ScriptMethodEntryBreakpoint(getDebugModelId(resource),
				resource, path, lineNumber, charStart, charEnd, register,
				methodName);
	}

	public static IScriptWatchpoint createWatchPoint(IResource resource,
			IPath path, int lineNumber, int start, int end, String fieldName)
			throws CoreException {
		return new ScriptWatchpoint(getDebugModelId(resource), resource, path,
				lineNumber, start, end, fieldName);
	}

	public static ScriptExceptionBreakpoint createExceptionBreakpoint(
			String debugModelId, IResource resource, String typename,
			boolean caught, boolean uncaught, boolean register, Map attributes)
			throws CoreException {
		if (attributes == null)
			attributes = new HashMap();

		return new ScriptExceptionBreakpoint(debugModelId, resource, typename,
				caught, uncaught, register, attributes);
	}

	// EBAY MOD START
	public static IScriptMethodEntryBreakpoint createMethodEntryBreakpoint(
			String debugModelId, IResource resource, IPath path,
			int lineNumber, int charStart, int charEnd, boolean register,
			Map attributes, String methodName) throws CoreException {
		return new ScriptMethodEntryBreakpoint(debugModelId, resource, path,
				lineNumber, charStart, charEnd, register, methodName);
	}
	// EBAY MOD END
}
