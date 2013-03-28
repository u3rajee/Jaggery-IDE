/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.actions;

import java.util.ArrayList;

import org.eclipse.dltk.mod.ui.viewsupport.AbstractModelElementFilter;
import org.eclipse.dltk.mod.ui.viewsupport.MemberFilter;
import org.eclipse.dltk.mod.ui.viewsupport.MemberFilterAction;
import org.eclipse.dltk.mod.ui.viewsupport.ModelElementFlagsFilter;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Action Group that contributes filter buttons for a view parts showing methods
 * and fields. Contributed filters are: hide fields, hide static members hide
 * non-public members and hide local types.
 * <p>
 * The action group installs a filter on a structured viewer. The filter is
 * connected to the actions installed in the view part's toolbar menu and is
 * updated when the state of the buttons changes.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * 
 */
public class MemberFilterActionGroup extends ActionGroup {

	IPreferenceStore fStore;
	private MemberFilterAction[] fFilterActions;
	private MemberFilter fFilter;
	public static final int FILTER_NONPUBLIC = MemberFilter.FILTER_NONPUBLIC;
	public static final int FILTER_STATIC = MemberFilter.FILTER_STATIC;
	public static final int FILTER_FIELDS = MemberFilter.FILTER_FIELDS;

	public static final int FILTER_LOCALTYPES = MemberFilter.FILTER_LOCALTYPES;
	public static final int ALL_FILTERS = FILTER_NONPUBLIC | FILTER_FIELDS
			| FILTER_STATIC | FILTER_LOCALTYPES;

	private StructuredViewer fViewer;
	private boolean fInViewMenu;

	String title, helpContext;

	/**
	 * Creates a new <code>MemberFilterActionGroup</code>.
	 * 
	 * @param viewer
	 *            the viewer to be filtered
	 * @param viewerId
	 *            a unique id of the viewer. Used as a key to to store the last
	 *            used filter settings in the preference store
	 */
	public MemberFilterActionGroup(StructuredViewer viewer,
			IPreferenceStore store) {
		this(viewer, store, false);
	}

	/**
	 * Creates a new <code>MemberFilterActionGroup</code>.
	 * 
	 * @param viewer
	 *            the viewer to be filtered
	 * @param viewerId
	 *            a unique id of the viewer. Used as a key to to store the last
	 *            used filter settings in the preference store
	 * @param inViewMenu
	 *            if <code>true</code> the actions are added to the view menu.
	 *            If <code>false</code> they are added to the toolbar.
	 */
	public MemberFilterActionGroup(StructuredViewer viewer,
			IPreferenceStore store, boolean inViewMenu) {

		fViewer = viewer;
		fStore = store;
		fInViewMenu = inViewMenu;
		ArrayList actions = new ArrayList(4);
		// IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		fFilter = new MemberFilter();

		// Add by oliver, BEGIN. Add some actions on the tool bar of members
		// view.
		// fields
		int filterProperty = FILTER_FIELDS;
		if (isSet(filterProperty, ALL_FILTERS)) {
			title = ActionMessages.MemberFilterActionGroup_hide_fields_label;
			helpContext = IJavaHelpContextIds.FILTER_FIELDS_ACTION;
			MemberFilterAction hideFields = new MemberFilterAction(this, title,
					new ModelElementFlagsFilter(filterProperty), helpContext,
					false);
			hideFields
					.setDescription(ActionMessages.MemberFilterActionGroup_hide_fields_description);
			hideFields
					.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_fields_tooltip);
			JavaPluginImages.setLocalImageDescriptors(hideFields,
					"fields_co.gif"); //$NON-NLS-1$
			actions.add(hideFields);
		}

		// static
		filterProperty = FILTER_STATIC;
		if (isSet(filterProperty, ALL_FILTERS)) {
			title = ActionMessages.MemberFilterActionGroup_hide_static_label;
			helpContext = IJavaHelpContextIds.FILTER_STATIC_ACTION;
			MemberFilterAction hideStatic = new MemberFilterAction(this, title,
					new ModelElementFlagsFilter(filterProperty), helpContext,
					false);
			hideStatic
					.setDescription(ActionMessages.MemberFilterActionGroup_hide_static_description);
			hideStatic
					.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_static_tooltip);
			JavaPluginImages.setLocalImageDescriptors(hideStatic,
					"static_co.gif"); //$NON-NLS-1$
			actions.add(hideStatic);
		}

		// non-public
		filterProperty = FILTER_NONPUBLIC;
		if (isSet(filterProperty, ALL_FILTERS)) {
			title = ActionMessages.MemberFilterActionGroup_hide_nonpublic_label;
			helpContext = IJavaHelpContextIds.FILTER_PUBLIC_ACTION;
			MemberFilterAction hideNonPublic = new MemberFilterAction(this,
					title, new ModelElementFlagsFilter(filterProperty),
					helpContext, false);
			hideNonPublic
					.setDescription(ActionMessages.MemberFilterActionGroup_hide_nonpublic_description);
			hideNonPublic
					.setToolTipText(ActionMessages.MemberFilterActionGroup_hide_nonpublic_tooltip);
			JavaPluginImages.setLocalImageDescriptors(hideNonPublic,
					"public_co.gif"); //$NON-NLS-1$
			actions.add(hideNonPublic);
		}
		// Add by oliver, END. Add some actions on the tool bar of members view.

		fFilterActions = (MemberFilterAction[]) actions
				.toArray(new MemberFilterAction[actions.size()]);

	}

	private boolean isSet(int flag, int set) {
		return (flag & set) != 0;
	}

	/**
	 * Set actions for group. You need for call this after constucting
	 * 
	 * @param actions
	 *            array of MemberFilterAction
	 */
	public void setActions(MemberFilterAction[] actions) {
		fFilterActions = new MemberFilterAction[actions.length];
		int i;
		for (i = 0; i < actions.length; i++) {
			MemberFilterAction action = actions[i];
			AbstractModelElementFilter filter = action.getFilter();

			boolean filterEnabled = false;
			if (fStore != null)
				filterEnabled = fStore.getBoolean(getPreferenceKey(action
						.getFilter().getFilteringType()));

			if (filterEnabled) {
				fViewer.addFilter(filter);
			} else
				fViewer.removeFilter(filter);

			action.setChecked(filterEnabled);
			fFilterActions[i] = action;
		}
	}

	private String getPreferenceKey(String filterProperty) {
		return "MemberFilterActionGroup." + filterProperty; //$NON-NLS-1$
	}

	public void processMemberFilterAction(MemberFilterAction action) {
		boolean set = action.isChecked();
		AbstractModelElementFilter filter = action.getFilter();
		if (fStore != null)
			fStore.setValue(getPreferenceKey(filter.getFilteringType()), set);
		if (set) {
			fViewer.addFilter(filter);
		} else {
			fViewer.removeFilter(filter);
		}

		// refresh
		fViewer.getControl().setRedraw(false);
		BusyIndicator.showWhile(fViewer.getControl().getDisplay(),
				new Runnable() {
					public void run() {
						fViewer.refresh();
					}
				});
		fViewer.getControl().setRedraw(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ActionGroup#fillActionBars(IActionBars)
	 */
	public void fillActionBars(IActionBars actionBars) {
		contributeToToolBar(actionBars.getToolBarManager());
	}

	/**
	 * Adds the filter actions to the given tool bar
	 * 
	 * @param tbm
	 *            the tool bar to which the actions are added
	 */
	public void contributeToToolBar(IToolBarManager tbm) {
		if (fInViewMenu || fFilterActions == null)
			return;
		for (int i = 0; i < fFilterActions.length; i++) {
			tbm.add(fFilterActions[i]);
		}
	}

	/**
	 * Adds the filter actions to the given menu manager.
	 * 
	 * @param menu
	 *            the menu manager to which the actions are added
	 * 
	 */
	public void contributeToViewMenu(IMenuManager menu) {
		if (!fInViewMenu)
			return;
		final String filters = "filters"; //$NON-NLS-1$
		if (menu.find(filters) != null) {
			for (int i = 0; i < fFilterActions.length; i++) {
				menu.prependToGroup(filters, fFilterActions[i]);
			}
		} else {
			for (int i = 0; i < fFilterActions.length; i++) {
				menu.add(fFilterActions[i]);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ActionGroup#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	public void saveState(IMemento memento) {
		// TODO
	}

	public void restoreState(IMemento memento) {
		// TODO
	}

}
