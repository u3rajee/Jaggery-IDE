/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/

package org.eclipse.dltk.mod.internal.ui.filters;

import org.eclipse.dltk.mod.ui.viewsupport.MemberFilter;


/**
 * Fields filter.
 * 
	 *
 */
public class FieldsFilter extends MemberFilter {
	public FieldsFilter() {
		addFilter(MemberFilter.FILTER_FIELDS);
	}
}
