/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 * 			(report 36180: Callers/Callees view)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.corext.callhierarchy;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IType;


public class DLTKImplementorFinder implements IImplementorFinder {    
	public Collection findImplementingTypes(IType type, IProgressMonitor progressMonitor) {
		if (DLTKCore.DEBUG) {
			System.err.println("TODO: Add findImplementinTypes call"); //$NON-NLS-1$
		}
//        ITypeHierarchy typeHierarchy;
//
//        try {
//            typeHierarchy = type.newTypeHierarchy(progressMonitor);
//
//            IType[] implementingTypes = typeHierarchy.getAllClasses();
//            HashSet result = new HashSet(Arrays.asList(implementingTypes));
//
//            return result;
//        } catch (ModelException e) {
//            DLTKUIPlugin.log(e);
//        }

        return null;
    }
}
