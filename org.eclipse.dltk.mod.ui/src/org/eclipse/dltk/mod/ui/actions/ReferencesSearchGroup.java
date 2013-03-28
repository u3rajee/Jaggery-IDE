/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.internal.ui.callhierarchy.SearchUtil;
import org.eclipse.dltk.mod.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.mod.internal.ui.search.SearchMessages;
import org.eclipse.dltk.mod.ui.IContextMenuConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * Action group that adds the search for references actions to a context menu
 * and the global menu bar.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * 
 */
public class ReferencesSearchGroup extends ActionGroup {

	private static final String MENU_TEXT = SearchMessages.group_references;

	private IWorkbenchSite fSite;

	private ScriptEditor fEditor;

	private IActionBars fActionBars;

	private String fGroupId;

	private FindReferencesAction fFindReferencesAction, fFindSatisfiersAction;

	private FindReferencesInProjectAction fFindReferencesInProjectAction,
			fFindSatisfiersInProjectAction;

	private FindReferencesInHierarchyAction fFindReferencesInHierarchyAction,
			fFindSatisfiersInHierarchyAction;

	private FindReferencesInWorkingSetAction fFindReferencesInWorkingSetAction,
			fFindSatisfiersInWorkingSetAction;

	private final IDLTKLanguageToolkit toolkit;

	/**
	 * Creates a new <code>ReferencesSearchGroup</code>. The group requires
	 * that the selection provided by the site's selection provider is of type
	 * <code>
	 * org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site
	 *            the view part that owns this action group
	 */
	public ReferencesSearchGroup(IWorkbenchSite site, IDLTKLanguageToolkit tk) {
		fSite = site;
		fGroupId = IContextMenuConstants.GROUP_SEARCH;
		this.toolkit = tk;

		fFindReferencesAction = new FindReferencesAction(site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersAction = new FindSatisfiersAction(site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};

		fFindReferencesAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_WORKSPACE);
		fFindSatisfiersAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.workspace");

		fFindReferencesInProjectAction = new FindReferencesInProjectAction(site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInProjectAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_PROJECT);

		fFindSatisfiersInProjectAction = new FindSatisfiersInProjectAction(site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInProjectAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.project");

		fFindReferencesInHierarchyAction = new FindReferencesInHierarchyAction(
				site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInHierarchyAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_HIERARCHY);

		fFindSatisfiersInHierarchyAction = new FindSatisfiersInHierarchyAction(
				site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInHierarchyAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.hierarchy");

		fFindReferencesInWorkingSetAction = new FindReferencesInWorkingSetAction(
				site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInWorkingSetAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_WORKING_SET);

		fFindSatisfiersInWorkingSetAction = new FindSatisfiersInWorkingSetAction(
				site) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInWorkingSetAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.working.set");

		// register the actions as selection listeners
		ISelectionProvider provider = fSite.getSelectionProvider();
		ISelection selection = provider.getSelection();
		registerAction(fFindReferencesAction, provider, selection);
		registerAction(fFindSatisfiersAction, provider, selection);
		registerAction(fFindReferencesInProjectAction, provider, selection);
		registerAction(fFindSatisfiersInProjectAction, provider, selection);
		registerAction(fFindReferencesInHierarchyAction, provider, selection);
		registerAction(fFindSatisfiersInHierarchyAction, provider, selection);
		registerAction(fFindReferencesInWorkingSetAction, provider, selection);
		registerAction(fFindSatisfiersInWorkingSetAction, provider, selection);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call
	 * this constructor.
	 * 
	 * @param editor
	 *            the Script editor
	 */
	public ReferencesSearchGroup(ScriptEditor editor, IDLTKLanguageToolkit tk) {
		Assert.isNotNull(editor);
		this.toolkit = tk;
		fEditor = editor;
		fSite = fEditor.getSite();
		fGroupId = ITextEditorActionConstants.GROUP_FIND;

		fFindReferencesAction = new FindReferencesAction(editor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_WORKSPACE);
		fEditor.setAction("SearchReferencesInWorkspace", fFindReferencesAction); //$NON-NLS-1$

		fFindSatisfiersAction = new FindSatisfiersAction(editor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.workspace");
		fEditor.setAction("SearchSatisfiersInWorkspace", fFindSatisfiersAction); //$NON-NLS-1$

		fFindReferencesInProjectAction = new FindReferencesInProjectAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInProjectAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_PROJECT);
		fEditor.setAction(
				"SearchReferencesInProject", fFindReferencesInProjectAction); //$NON-NLS-1$

		fFindSatisfiersInProjectAction = new FindSatisfiersInProjectAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInProjectAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.project");
		fEditor.setAction(
				"SearchReferencesInProject", fFindSatisfiersInProjectAction); //$NON-NLS-1$

		fFindReferencesInHierarchyAction = new FindReferencesInHierarchyAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInHierarchyAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_HIERARCHY);
		fEditor
				.setAction(
						"SearchReferencesInHierarchy", fFindReferencesInHierarchyAction); //$NON-NLS-1$

		fFindSatisfiersInHierarchyAction = new FindSatisfiersInHierarchyAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInHierarchyAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.hierarchy");
		fEditor
				.setAction(
						"SearchReferencesInHierarchy", fFindSatisfiersInHierarchyAction); //$NON-NLS-1$

		fFindReferencesInWorkingSetAction = new FindReferencesInWorkingSetAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindReferencesInWorkingSetAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SEARCH_REFERENCES_IN_WORKING_SET);
		fEditor
				.setAction(
						"SearchReferencesInWorkingSet", fFindReferencesInWorkingSetAction); //$NON-NLS-1$
		fFindSatisfiersInWorkingSetAction = new FindSatisfiersInWorkingSetAction(
				fEditor) {
			protected IDLTKLanguageToolkit getLanguageToolkit() {
				return toolkit;
			}
		};
		fFindSatisfiersInWorkingSetAction
				.setActionDefinitionId("org.eclipse.dltk.mod.ui.edit.text.script.search.satisfiers.in.working.set");
		fEditor
				.setAction(
						"SearchReferencesInWorkingSet", fFindSatisfiersInWorkingSetAction); //$NON-NLS-1$

	}

	private void registerAction(SelectionDispatchAction action,
			ISelectionProvider provider, ISelection selection) {
		action.update(selection);
		provider.addSelectionChangedListener(action);
	}

	/**
	 * Note: this method is for internal use only. Clients should not call this
	 * method.
	 * 
	 * @return the menu label
	 */
	protected String getName() {
		return MENU_TEXT;
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillActionBars(IActionBars actionBars) {
		Assert.isNotNull(actionBars);
		super.fillActionBars(actionBars);
		fActionBars = actionBars;
		updateGlobalActionHandlers();
	}

	private void addAction(IAction action, IMenuManager manager) {
		if (action.isEnabled()) {
			manager.add(action);
		}
	}

	private void addWorkingSetAction(IWorkingSet[] workingSets,
			IMenuManager manager) {
		FindAction action;
		if (fEditor != null)
			action = new WorkingSetFindAction(fEditor,
					new FindReferencesInWorkingSetAction(fEditor, workingSets) {
						protected IDLTKLanguageToolkit getLanguageToolkit() {
							return toolkit;
						}
					}, SearchUtil.toString(workingSets));
		else
			action = new WorkingSetFindAction(fSite,
					new FindReferencesInWorkingSetAction(fSite, workingSets) {
						protected IDLTKLanguageToolkit getLanguageToolkit() {
							return toolkit;
						}
					}, SearchUtil.toString(workingSets));
		action.update(getContext().getSelection());
		addAction(action, manager);
	}

	/*
	 * (non-Javadoc) Method declared on ActionGroup.
	 */
	public void fillContextMenu(IMenuManager manager) {
		MenuManager javaSearchMM = new MenuManager(getName(),
				IContextMenuConstants.GROUP_SEARCH);
		addAction(fFindReferencesAction, javaSearchMM);
		// addAction(fFindSatisfiersAction, javaSearchMM);

		// TODO Disable menus
		/*
		 * addAction(fFindReferencesInProjectAction, javaSearchMM);
		 * addAction(fFindReferencesInHierarchyAction, javaSearchMM);
		 * 
		 * javaSearchMM.add(new Separator());
		 * 
		 * Iterator iter = SearchUtil.getLRUWorkingSets().sortedIterator();
		 * while (iter.hasNext()) { addWorkingSetAction((IWorkingSet[])
		 * iter.next(), javaSearchMM); }
		 * addAction(fFindReferencesInWorkingSetAction, javaSearchMM);
		 */
		if (!javaSearchMM.isEmpty())
			manager.appendToGroup(fGroupId, javaSearchMM);
	}

	/*
	 * Overrides method declared in ActionGroup
	 */
	public void dispose() {
		ISelectionProvider provider = fSite.getSelectionProvider();
		if (provider != null) {
			disposeAction(fFindReferencesAction, provider);
			disposeAction(fFindSatisfiersAction, provider);
			disposeAction(fFindReferencesInProjectAction, provider);
			disposeAction(fFindSatisfiersInProjectAction, provider);
			disposeAction(fFindReferencesInHierarchyAction, provider);
			disposeAction(fFindSatisfiersInHierarchyAction, provider);
			disposeAction(fFindReferencesInWorkingSetAction, provider);
			disposeAction(fFindSatisfiersInWorkingSetAction, provider);
		}
		fFindReferencesAction = null;
		fFindSatisfiersAction = null;
		fFindReferencesInProjectAction = null;
		fFindSatisfiersInProjectAction = null;
		fFindReferencesInHierarchyAction = null;
		fFindSatisfiersInHierarchyAction = null;
		fFindReferencesInWorkingSetAction = null;
		fFindSatisfiersInWorkingSetAction = null;
		updateGlobalActionHandlers();
		super.dispose();
	}

	private void updateGlobalActionHandlers() {
		if (fActionBars != null) {
			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_REFERENCES_IN_WORKSPACE,
					fFindReferencesAction);
			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_SATISFIERS_IN_WORKSPACE,
					fFindSatisfiersAction);

			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_REFERENCES_IN_PROJECT,
					fFindReferencesInProjectAction);
			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_SATISFIERS_IN_PROJECT,
					fFindSatisfiersInProjectAction);

			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_REFERENCES_IN_HIERARCHY,
					fFindReferencesInHierarchyAction);
			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_SATISFIERS_IN_HIERARCHY,
					fFindSatisfiersInHierarchyAction);

			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_REFERENCES_IN_WORKING_SET,
					fFindReferencesInWorkingSetAction);
			fActionBars.setGlobalActionHandler(
					DLTKActionConstants.FIND_SATISFIERS_IN_WORKING_SET,
					fFindSatisfiersInWorkingSetAction);
		}
	}

	private void disposeAction(ISelectionChangedListener action,
			ISelectionProvider provider) {
		if (action != null)
			provider.removeSelectionChangedListener(action);
	}
}
