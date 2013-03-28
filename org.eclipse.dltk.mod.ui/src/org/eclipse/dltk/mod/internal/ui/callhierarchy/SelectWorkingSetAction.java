/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *          (report 36180: Callers/Callees view)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.callhierarchy;

import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.util.ExceptionHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkingSet;


class SelectWorkingSetAction extends Action {
	private final SearchScopeActionGroup fGroup;
	
	public SelectWorkingSetAction(SearchScopeActionGroup group) {
		super(CallHierarchyMessages.SearchScopeActionGroup_workingset_select_text); 
		this.fGroup = group;
		setToolTipText(CallHierarchyMessages.SearchScopeActionGroup_workingset_select_tooltip); 
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.CALL_HIERARCHY_SEARCH_SCOPE_ACTION);
		if (DLTKCore.DEBUG) {
			System.err.println("Add help support here..."); //$NON-NLS-1$
		}		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		try {
			IWorkingSet[] workingSets;
			workingSets = DLTKSearchScopeFactory.getInstance().queryWorkingSets();
			if (workingSets != null) {
				this.fGroup.setActiveWorkingSets(workingSets);
				SearchUtil.updateLRUWorkingSets(workingSets);
			} else {
				this.fGroup.setActiveWorkingSets(null);
			}
		} catch (ModelException e) {
			ExceptionHandler.handle(e, DLTKUIPlugin.getActiveWorkbenchShell(), 
					CallHierarchyMessages.SelectWorkingSetAction_error_title, 
					CallHierarchyMessages.SelectWorkingSetAction_error_message); 
		}
	}
}
