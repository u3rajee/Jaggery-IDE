/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *     xored software, Inc. - Patch 228846 (Alex Panchenko <alex@xored.com>)
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.infoviews;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 */
final class InfoViewMessages extends NLS {

	private static final String BUNDLE_NAME = InfoViewMessages.class.getName();

	private InfoViewMessages() {
		// Do not instantiate
	}

	public static String ContentDescription_multipleMethodsWithSameName;
	public static String CopyAction_label;
	public static String CopyAction_tooltip;
	public static String CopyAction_description;
	public static String SelectAllAction_label;
	public static String SelectAllAction_tooltip;
	public static String SelectAllAction_description;
	public static String GotoInputAction_label;
	public static String GotoInputAction_tooltip;
	public static String GotoInputAction_description;
	public static String CopyToClipboard_error_title;
	public static String CopyToClipboard_error_message;
	public static String ScriptdocView_error_noBrowser_title;
	public static String ScriptdocView_error_noBrowser_message;
	public static String ScriptdocView_error_noBrowser_doNotWarn;
	public static String ScriptdocView_noAttachedInformation;
	public static String ScriptdocView_noAttachedInformationHeader;

	static {
		NLS.initializeMessages(BUNDLE_NAME, InfoViewMessages.class);
	}
}
