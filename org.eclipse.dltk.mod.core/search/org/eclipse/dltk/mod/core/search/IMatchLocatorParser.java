/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.core.search;

import org.eclipse.dltk.mod.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.mod.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.mod.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.mod.core.search.matching.PossibleMatch;
import org.eclipse.dltk.mod.internal.core.search.matching.MatchingNodeSet;

public interface IMatchLocatorParser {

	void setNodeSet(MatchingNodeSet nodeSet);

	ModuleDeclaration parse(PossibleMatch possibleMatch);

	void parseBodies(ModuleDeclaration unit);
	
	MethodDeclaration processMethod(MethodDeclaration m);
	TypeDeclaration processType(TypeDeclaration t);
}
