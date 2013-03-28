/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.search;

import java.util.StringTokenizer;

import org.eclipse.dltk.mod.core.IField;
import org.eclipse.dltk.mod.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.mod.core.search.SearchMatch;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.search.ElementQuerySpecification;
import org.eclipse.dltk.mod.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.mod.ui.search.QuerySpecification;

abstract class MatchFilter {

	private static final String SETTINGS_LAST_USED_FILTERS = "filters_last_used"; //$NON-NLS-1$

	public static MatchFilter[] getLastUsedFilters() {
		String string = DLTKUIPlugin.getDefault().getDialogSettings().get(
				SETTINGS_LAST_USED_FILTERS);
		if (string != null && string.length() > 0) {
			return decodeFiltersString(string);
		}
		return getDefaultFilters();
	}

	public static void setLastUsedFilters(MatchFilter[] filters) {
		String encoded = encodeFilters(filters);
		DLTKUIPlugin.getDefault().getDialogSettings().put(
				SETTINGS_LAST_USED_FILTERS, encoded);
	}

	public static MatchFilter[] getDefaultFilters() {
		return new MatchFilter[] { POTENTIAL_FILTER };
	}

	private static String encodeFilters(MatchFilter[] enabledFilters) {
		StringBuffer buf = new StringBuffer();
		buf.append(enabledFilters.length);
		for (int i = 0; i < enabledFilters.length; i++) {
			buf.append(';');
			buf.append(enabledFilters[i].getID());
		}
		return buf.toString();
	}

	private static MatchFilter[] decodeFiltersString(String encodedString) {
		StringTokenizer tokenizer = new StringTokenizer(encodedString, String
				.valueOf(';'));
		int count = Integer.valueOf(tokenizer.nextToken()).intValue();
		MatchFilter[] res = new MatchFilter[count];
		for (int i = 0; i < count; i++) {
			res[i] = findMatchFilter(tokenizer.nextToken());
		}
		return res;
	}

	public abstract boolean isApplicable(DLTKSearchQuery query);

	public abstract boolean filters(DLTKElementMatch match);

	public abstract String getName();

	public abstract String getActionLabel();

	public abstract String getDescription();

	public abstract String getID();

	private static final MatchFilter POTENTIAL_FILTER = new PotentialFilter();
	private static final MatchFilter IMPORT_FILTER = new ImportFilter();
	private static final MatchFilter SCRIPTDOC_FILTER = new ScriptdocFilter();
	private static final MatchFilter PUBLIC_FILTER = new PublicFilter();
	private static final MatchFilter NON_PUBLIC_FILTER = new Non_PublicFilter();
	private static final MatchFilter STATIC_FILTER = new StaticFilter();
	private static final MatchFilter NON_STATIC_FILTER = new Non_StaticFilter();
	private static final MatchFilter READ_FILTER = new ReadFilter();
	private static final MatchFilter WRITE_FILTER = new WriteFilter();
	// private static final MatchFilter INEXACT_FILTER= new
	// InexactMatchFilter();
	// private static final MatchFilter ERASURE_FILTER= new
	// ErasureMatchFilter();

	private static final MatchFilter[] ALL_FILTERS = new MatchFilter[] {
	/* POTENTIAL_FILTER, */IMPORT_FILTER, SCRIPTDOC_FILTER, PUBLIC_FILTER,
			NON_PUBLIC_FILTER, STATIC_FILTER, NON_STATIC_FILTER /*
																 * ,
																 * READ_FILTER,
																 * WRITE_FILTER
																 */
	// INEXACT_FILTER,
	// ERASURE_FILTER
	};

	public static MatchFilter[] allFilters() {
		// TODO Disable menus
		// return ALL_FILTERS;

		return new MatchFilter[0];
	}

	private static MatchFilter findMatchFilter(String id) {
		for (int i = 0; i < ALL_FILTERS.length; i++) {
			if (ALL_FILTERS[i].getID().equals(id))
				return ALL_FILTERS[i];
		}
		return POTENTIAL_FILTER; // just return something, should not happen
	}

}

class PotentialFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.getAccuracy() == SearchMatch.A_INACCURATE;
	}

	public String getName() {
		return SearchMessages.MatchFilter_PotentialFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_PotentialFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_PotentialFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_potential"; //$NON-NLS-1$
	}
}

abstract class FieldFilter extends MatchFilter {
	public boolean isApplicable(DLTKSearchQuery query) {
		QuerySpecification spec = query.getSpecification();
		if (spec instanceof ElementQuerySpecification) {
			ElementQuerySpecification elementSpec = (ElementQuerySpecification) spec;
			return elementSpec.getElement() instanceof IField;
		} else if (spec instanceof PatternQuerySpecification) {
			PatternQuerySpecification patternSpec = (PatternQuerySpecification) spec;
			return patternSpec.getSearchFor() == IDLTKSearchConstants.FIELD;
		}
		return false;
	}

}

class WriteFilter extends FieldFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isWriteAccess() && !match.isReadAccess();
	}

	public String getName() {
		return SearchMessages.MatchFilter_WriteFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_WriteFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_WriteFilter_description;
	}

	public String getID() {
		return "filter_writes"; //$NON-NLS-1$
	}
}

class ReadFilter extends FieldFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isReadAccess() && !match.isWriteAccess();
	}

	public String getName() {
		return SearchMessages.MatchFilter_ReadFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_ReadFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_ReadFilter_description;
	}

	public String getID() {
		return "filter_reads"; //$NON-NLS-1$
	}
}

class ScriptdocFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isScriptdoc();
	}

	public String getName() {
		return SearchMessages.MatchFilter_DLTKdocFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_DLTKdocFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_DLTKdocFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_javadoc"; //$NON-NLS-1$
	}
}

class PublicFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isPublic();
	}

	public String getName() {
		return SearchMessages.MatchFilter_PublicFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_PublicFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_PublicFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_public"; //$NON-NLS-1$
	}
}

class Non_PublicFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return !match.isPublic();
	}

	public String getName() {
		return SearchMessages.MatchFilter_NonPublicFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_NonPublicFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_NonPublicFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_non_public"; //$NON-NLS-1$
	}
}

class StaticFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isStatic();
	}

	public String getName() {
		return SearchMessages.MatchFilter_StaticFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_StaticFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_StaticFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_Static"; //$NON-NLS-1$
	}
}

class Non_StaticFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return !match.isStatic();
	}

	public String getName() {
		return SearchMessages.MatchFilter_NonStaticFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_NonStaticFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_NonStaticFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_non_Static"; //$NON-NLS-1$
	}
}

class ImportFilter extends MatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isIsImport();
	}

	public String getName() {
		return "Needs";
	}

	public String getActionLabel() {
		return "Needs";
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_DLTKdocFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_need"; //$NON-NLS-1$
	}
}
