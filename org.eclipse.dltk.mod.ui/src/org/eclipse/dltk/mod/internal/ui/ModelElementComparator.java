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
package org.eclipse.dltk.mod.internal.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class ModelElementComparator extends ViewerComparator {

	public int category(Object element) {
		// TODO: provide real implementation
		return super.category(element);
	}
	
	public int compare(Viewer viewer, Object e1, Object e2) {
		// TODO: provide real implementation
		return super.compare(viewer, e1, e2);
	}
}
