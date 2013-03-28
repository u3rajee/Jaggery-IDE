/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.mod.ti.goals;

import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.mod.core.search.SearchPattern;

public class MethodCallsGoalEvaluator extends SearchBasedGoalEvaluator {

	public MethodCallsGoalEvaluator(IGoal goal) {
		super(goal);
	}

	protected SearchPattern createSearchPattern(IDLTKLanguageToolkit toolkit) {
		MethodCallsGoal goal = (MethodCallsGoal) getGoal();
		String name = goal.getName();
		return SearchPattern.createPattern(name, IDLTKSearchConstants.METHOD,
				IDLTKSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH,
				toolkit);
	}

	protected IGoal createVerificationGoal(PossiblePosition pos) {
		MethodCallVerificationGoal g = new MethodCallVerificationGoal(this
				.getGoal().getContext(), (MethodCallsGoal) this.getGoal(), pos);
		return g;
	}

}
