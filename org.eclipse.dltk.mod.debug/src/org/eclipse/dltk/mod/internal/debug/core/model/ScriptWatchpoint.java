/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.debug.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.mod.debug.core.model.IScriptWatchpoint;

public class ScriptWatchpoint extends ScriptLineBreakpoint implements
		IScriptWatchpoint {

	private static final String FIELD_NAME = DLTKDebugPlugin.PLUGIN_ID
			+ ".fieldName"; //$NON-NLS-1$

	private static final String ACCESS = DLTKDebugPlugin.PLUGIN_ID + ".access"; //$NON-NLS-1$

	private static final String MODIFICATION = DLTKDebugPlugin.PLUGIN_ID
			+ ".modification"; //$NON-NLS-1$

	public ScriptWatchpoint(final String debugModelId,
			final IResource resource, final IPath path, final int lineNumber,
			final int start, final int end, final String fieldName)
			throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				// create the marker
				setMarker(resource.createMarker(getMarkerId()));

				final Map attributes = new HashMap();
				// add attributes
				addScriptBreakpointAttributes(attributes, debugModelId, true);
				addLineBreakpointAttributes(attributes, path, lineNumber,
						start, end);
				attributes.put(FIELD_NAME, fieldName);

				// set attributes
				ensureMarker().setAttributes(attributes);

				// add to breakpoint manager if requested
				register(true);
			}
		};
		run(getMarkerRule(resource), wr);
	}

	public ScriptWatchpoint() {
	}

	public String getFieldName() throws CoreException {
		return this.getMarker().getAttribute(FIELD_NAME, ""); //$NON-NLS-1$
	}

	public void setFieldName(String name) throws CoreException {
		this.getMarker().setAttribute(FIELD_NAME, name);
	}

	protected String getMarkerId() {
		return ScriptMarkerFactory.WATCHPOINT_MARKER_ID;
	}

	public boolean isAccess() throws CoreException {
		return (new Boolean(this.getMarker().getAttribute(ACCESS, "true"))) //$NON-NLS-1$
				.booleanValue();
	}

	public boolean isModification() throws CoreException {
		return (new Boolean(this.getMarker().getAttribute(MODIFICATION, "true"))) //$NON-NLS-1$
				.booleanValue();
	}

	public void setAccess(boolean access) throws CoreException {
		this.getMarker().setAttribute(ACCESS, Boolean.toString(access));
	}

	public void setModification(boolean modification) throws CoreException {
		this.getMarker().setAttribute(MODIFICATION,
				Boolean.toString(modification));
	}

	public boolean supportsAccess() {
		return true;
	}

	public boolean supportsModification() {
		return true;
	}

	private static final String[] UPDATABLE_ATTRS = new String[] { FIELD_NAME,
			ACCESS, MODIFICATION };

	public String[] getUpdatableAttributes() {
		List all = new ArrayList();
		all.addAll(Arrays.asList(super.getUpdatableAttributes()));
		all.addAll(Arrays.asList(UPDATABLE_ATTRS));
		return (String[]) all.toArray(new String[all.size()]);
	}
}
