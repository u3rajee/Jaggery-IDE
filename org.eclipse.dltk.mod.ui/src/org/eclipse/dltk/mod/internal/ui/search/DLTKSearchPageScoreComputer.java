/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.search;

import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.dltk.mod.ui.search.ScriptSearchPage;
import org.eclipse.search.ui.ISearchPageScoreComputer;


public class DLTKSearchPageScoreComputer implements ISearchPageScoreComputer {

	public int computeScore(String id, Object element) {
		if (!ScriptSearchPage.EXTENSION_POINT_ID.equals(id))
			// Can't decide
			return ISearchPageScoreComputer.UNKNOWN;
		
		if (element instanceof IModelElement || element instanceof ExternalStorageEditorInput )
			return 90;
		
		return ISearchPageScoreComputer.LOWEST;
	}
}
