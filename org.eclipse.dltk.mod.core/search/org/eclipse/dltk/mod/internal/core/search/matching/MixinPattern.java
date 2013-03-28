/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search.matching;

import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.search.SearchPattern;
import org.eclipse.dltk.mod.core.search.indexing.IIndexConstants;

public class MixinPattern extends DLTKSearchPattern implements IIndexConstants {
	protected char[] mixinKey;
	
	public static char[] createIndexKey(char[] fieldName) {
		return fieldName;
	}

	public MixinPattern(char[] key, int matchRule, IDLTKLanguageToolkit toolkit) {
		super(FIELD_PATTERN, matchRule, toolkit);
		this.mixinKey = key;
	}

	public void decodeIndexKey(char[] key) {
		this.mixinKey = key;
	}

	public SearchPattern getBlankPattern() {
		return new MixinPattern(null, R_EXACT_MATCH | R_CASE_SENSITIVE, getToolkit());
	}

	public char[] getIndexKey() {
		return this.mixinKey;
	}

	public char[][] getIndexCategories() {
		return new char[][] {MIXIN};
	}

	public boolean matchesDecodedKey(SearchPattern decodedPattern) {
		return true; // index key is not encoded so query results all match
	}

	protected StringBuffer print(StringBuffer output) {
		
		if (mixinKey == null) {
			output.append("*"); //$NON-NLS-1$
		} else {
			output.append(mixinKey);
		}
		return super.print(output);
	}
}
