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
package org.eclipse.dltk.mod.core.search;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.mod.core.ISearchFactory;
import org.eclipse.dltk.mod.core.ISearchPatternProcessor;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.mod.core.search.matching.MatchLocator;

public abstract class AbstractSearchFactory implements ISearchFactory {
	public SourceIndexerRequestor createSourceRequestor() {
		return new SourceIndexerRequestor();
	}

	public DLTKSearchParticipant createSearchParticipant() {
		return null;
	}

	public MatchLocator createMatchLocator(SearchPattern pattern,
			SearchRequestor requestor, IDLTKSearchScope scope,
			SubProgressMonitor monitor) {
		return new MatchLocator(pattern, requestor, scope, monitor);
	}

	public ISearchPatternProcessor createSearchPatternProcessor() {
		return null;
	}

	public String getNormalizedTypeName(IType type) {
		return type.getElementName();
	}
}
