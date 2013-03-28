/*******************************************************************************
 * Copyright (c) 2012 eBay Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc. - initial API and implementation
 *******************************************************************************/
/**
 *
 */
package org.eclipse.dltk.mod.ti.statistics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.mod.ti.GoalState;
import org.eclipse.dltk.mod.ti.goals.GoalEvaluator;
import org.eclipse.dltk.mod.ti.goals.IGoal;

public class GoalEvaluationStatistics {
	private IGoal goal;
	private GoalEvaluator evaluator;
	private GoalState state;
	private long timeStart;
	private long timeEnd;
	private List steps;
	private GoalEvaluationStatistics parentStat;

	public GoalEvaluationStatistics(IGoal goal) {
		super();
		this.timeStart = System.currentTimeMillis();
		this.timeEnd = -1;
		this.goal = goal;
		this.state = GoalState.WAITING;
		this.steps = new ArrayList();
	}

	public GoalEvaluator getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(GoalEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	public GoalState getState() {
		return state;
	}

	public void setState(GoalState state) {
		this.state = state;
	}

	public long getTimeEnd() {
		return timeEnd;
	}

	public void setTimeEnd(long timeEnd) {
		this.timeEnd = timeEnd;
	}

	public GoalEvaluationStatistics getParentStat() {
		return parentStat;
	}

	public void setParentStat(GoalEvaluationStatistics parentStat) {
		this.parentStat = parentStat;
	}

	public IGoal getGoal() {
		return goal;
	}

	public long getTimeStart() {
		return timeStart;
	}

	public List getSteps() {
		return steps;
	}

}