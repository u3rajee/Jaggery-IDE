/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.core;

import org.eclipse.core.resources.IProject;

/**
 * Selects a contributed extension implementation based upon the
 * <code>priority</code> it was registered with when the plugin containing its
 * extension definition was loaded.
 */
public class DLTKPriorityContributionSelector implements
		IDLTKContributionSelector {

	/*
	 * @see org.eclipse.dltk.mod.core.IDLTKContributionSelector#select(org.eclipse.dltk.mod.core.IDLTKContributedExtension[],
	 *      org.eclipse.core.resources.IProject)
	 */
	public IDLTKContributedExtension select(
			IDLTKContributedExtension[] contributions, IProject project) {
		int maxPriority = Integer.MIN_VALUE;
		IDLTKContributedExtension selected = null;

		for (int i = 0; i < contributions.length; i++) {
			IDLTKContributedExtension contrib = contributions[i];

			/*
			 * if more then one contribution has the same priority, the first
			 * one found in the array wins
			 */
			if (contrib.getPriority() > maxPriority) {
				selected = contrib;
				maxPriority = contrib.getPriority();
			}
		}

		return selected;
	}
}
