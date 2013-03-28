/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search.matching;

import org.eclipse.dltk.mod.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.mod.compiler.CharOperation;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.search.matching.PatternLocator;

public class TypeDeclarationLocator extends PatternLocator {
	protected TypeDeclarationPattern pattern; // can be a
												// QualifiedTypeDeclarationPattern

	public TypeDeclarationLocator(TypeDeclarationPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}

	public int match(TypeDeclaration node, MatchingNodeSet nodeSet) {
		if (this.pattern.simpleName == null || matchesName(this.pattern.simpleName, node.getName().toCharArray())) {
			//	fully qualified name
			if (this.pattern instanceof QualifiedTypeDeclarationPattern) {
//				QualifiedTypeDeclarationPattern qualifiedPattern = (QualifiedTypeDeclarationPattern) this.pattern;
//					if( !matchesName(this.pattern.simpleName, enclosingNodeTypeName)) {
//					return IMPOSSIBLE_MATCH;
//				}
//				return resolveLevelForType(qualifiedPattern.simpleName, qualifiedPattern.qualification, node);
			} else {
				char[] enclosingTypeName = this.pattern.enclosingTypeNames == null ? null : CharOperation.concatWith(this.pattern.enclosingTypeNames, '$');
				char[] enclosingNodeTypeName = node.getEnclosingTypeName().toCharArray();
				if( !matchesName(enclosingTypeName, enclosingNodeTypeName)) {
					return IMPOSSIBLE_MATCH;
				}
			}
			if (DLTKCore.DEBUG) {
				System.err.println("TODO: Check here, may be needed POSSIBLE_PATCH..."); //$NON-NLS-1$
			}
			return nodeSet.addMatch(node, ACCURATE_MATCH);
		}
		return IMPOSSIBLE_MATCH;
	}

	public String toString() {
		return "Locator for " + this.pattern.toString(); //$NON-NLS-1$
	}
}
