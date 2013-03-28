/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IField;
import org.eclipse.dltk.mod.core.IMethod;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.mod.core.search.IDLTKSearchScope;
import org.eclipse.dltk.mod.core.search.SearchEngine;
import org.eclipse.dltk.mod.internal.core.JSSourceType;
import org.eclipse.dltk.mod.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.mod.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.mod.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.mod.internal.ui.search.SearchMessages;
import org.eclipse.dltk.mod.ui.DLTKPluginImages;
import org.eclipse.dltk.mod.ui.search.ElementQuerySpecification;
import org.eclipse.dltk.mod.ui.search.QuerySpecification;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IWorkbenchSite;

/**
 * Finds references of the selected element in its hierarchy. The action is
 * applicable to selections representing a Script element.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * 
 */
public abstract class FindSatisfiersInHierarchyAction extends
		FindReferencesInHierarchyAction {

	private boolean isInterfaceForSearchSatisfier = false;

	/**
	 * Creates a new <code>FindReferencesInHierarchyAction</code>. The action
	 * requires that the selection provided by the site's selection provider is
	 * of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site
	 *            the site providing context information for this action
	 */
	public FindSatisfiersInHierarchyAction(IWorkbenchSite site) {
		super(site);
	}

	public void run(ITextSelection selection) {
		IModelElement[] elements;
		try {
			elements = SelectionConverter.codeResolveForked(getEditor(), true);
			if (elements.length > 0 && canOperateOn(elements[0])) {
				IModelElement element = elements[0];
				if (element != null) {
					try {
						isInterfaceForSearchSatisfier = ((JSSourceType) element)
								.isInterface();
						// setEnabled(isInterfaceForSearchSatisfier);
					} catch (ModelException e) {
					}
				}
			}
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}
		if (!isInterfaceForSearchSatisfier) {
			MessageDialog
					.openInformation(
							getShell(),
							SearchMessages.DLTKElementAction_operationUnavailable_title,
							SearchMessages.DLTKElementAction_operationUnavailable_interface);
			return;
		}

		super.run(selection);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call
	 * this constructor.
	 * 
	 * @param editor
	 *            the Script editor
	 */
	public FindSatisfiersInHierarchyAction(ScriptEditor editor) {
		super(editor);
	}

	Class[] getValidTypes() {
		return new Class[] { ISourceModule.class, IType.class, IMethod.class,
				IField.class };
	}

	int getLimitTo() {
		return IDLTKSearchConstants.SATISFIER;
	}

	void init() {
		setText(SearchMessages.Search_FindHierarchyReferencesAction_label);
		setToolTipText(SearchMessages.Search_FindHierarchyReferencesAction_tooltip);
		setImageDescriptor(DLTKPluginImages.DESC_OBJS_SEARCH_REF);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.FIND_REFERENCES_IN_HIERARCHY_ACTION);
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add helkp support here..."); //$NON-NLS-1$
		}
	}

	QuerySpecification createQuery(IModelElement element) throws ModelException {
		IType type = getType(element);
		if (type == null) {
			return super.createQuery(element);
		}
		DLTKSearchScopeFactory factory = DLTKSearchScopeFactory.getInstance();
		IDLTKSearchScope scope = SearchEngine.createHierarchyScope(type);
		String description = factory.getHierarchyScopeDescription(type);
		return new ElementQuerySpecification(element, getLimitTo(), scope,
				description);
	}

}
