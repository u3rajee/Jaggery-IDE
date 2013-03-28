/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc., and eBay Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.mod.compiler.problem.IProblem;
import org.eclipse.dltk.mod.compiler.problem.ProblemCollector;
import org.eclipse.dltk.mod.core.IProblemRequestor;

// eBay mod start
// class AccumulatingProblemReporter extends ProblemCollector {
public class AccumulatingProblemReporter extends ProblemCollector {
	// eBay mod end

	private final IProblemRequestor problemRequestor;

	/**
	 * @param problemRequestor
	 */
	public AccumulatingProblemReporter(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}

	public void reportToRequestor() {
		problemRequestor.beginReporting();
		for (Iterator i = problems.iterator(); i.hasNext();) {
			final IProblem problem = (IProblem) i.next();
			problemRequestor.acceptProblem(problem);
		}
		problemRequestor.endReporting();
	}

	public List getProblems() {
		return problems;
	}
}
