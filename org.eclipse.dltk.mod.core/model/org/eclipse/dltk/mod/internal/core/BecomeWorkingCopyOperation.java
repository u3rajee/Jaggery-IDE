/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IModelElementDelta;
import org.eclipse.dltk.mod.core.IProblemRequestor;
import org.eclipse.dltk.mod.core.ModelException;


/**
 * Switch and ISourceModule to working copy mode
 * and signal the working copy addition through a delta.
 */
public class BecomeWorkingCopyOperation extends ModelOperation {
	
	IProblemRequestor problemRequestor;
	
	/*
	 * Creates a BecomeWorkingCopyOperation for the given working copy.
	 * perOwnerWorkingCopies map is not null if the working copy is a shared working copy.
	 */
	public BecomeWorkingCopyOperation(SourceModule workingCopy, IProblemRequestor problemRequestor) {
		super(new IModelElement[] {workingCopy});
		this.problemRequestor = problemRequestor;
	}
	
	protected void executeOperation() throws ModelException {

		// open the working copy now to ensure contents are that of the current state of this element
		SourceModule workingCopy = getWorkingCopy();
		// create if needed, record usage
		ModelManager.getModelManager().getPerWorkingCopyInfo(workingCopy, true,
				true, this.problemRequestor);
		workingCopy.openWhenClosed(workingCopy.createElementInfo(), this.progressMonitor);

		if (!workingCopy.isPrimary()) {
			// report added script delta for a non-primary working copy
			ModelElementDelta delta = new ModelElementDelta(this.getModel());
			delta.added(workingCopy);
			addDelta(delta);
		} else {
			if (workingCopy.getResource().isAccessible()) {
				// report a F_PRIMARY_WORKING_COPY change delta for a primary working copy
				ModelElementDelta delta = new ModelElementDelta(this.getModel());
				delta.changed(workingCopy, IModelElementDelta.F_PRIMARY_WORKING_COPY);
				addDelta(delta);
			} else {
				// report an ADDED delta
				ModelElementDelta delta = new ModelElementDelta(this.getModel());
				delta.added(workingCopy, IModelElementDelta.F_PRIMARY_WORKING_COPY);
				addDelta(delta);
			}
		}

		this.resultElements = new IModelElement[] {workingCopy};
	}
	/*
	 * Returns the working copy this operation is working on.
	 */
	protected SourceModule getWorkingCopy() {
		return (SourceModule)getElementToProcess();
	}
	/*
	 * @see ModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}

}
