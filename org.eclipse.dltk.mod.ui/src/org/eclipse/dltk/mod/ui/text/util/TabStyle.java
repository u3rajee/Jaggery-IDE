/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.text.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.mod.ui.CodeFormatterConstants;

public final class TabStyle {

	private final String name;

	private TabStyle(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public static final TabStyle TAB = new TabStyle(CodeFormatterConstants.TAB);

	public static final TabStyle SPACES = new TabStyle(
			CodeFormatterConstants.SPACE);

	public static final TabStyle MIXED = new TabStyle(
			CodeFormatterConstants.MIXED);

	private static final Map byName = new HashMap();

	static {
		byName.put(TAB.getName(), TAB);
		byName.put(SPACES.getName(), SPACES);
		byName.put(MIXED.getName(), MIXED);
	}

	public static TabStyle forName(String name) {
		return (TabStyle) byName.get(name);
	}

	public static TabStyle forName(String name, TabStyle deflt) {
		TabStyle result = forName(name);
		if (result == null)
			return deflt;
		else
			return result;
	}

}
