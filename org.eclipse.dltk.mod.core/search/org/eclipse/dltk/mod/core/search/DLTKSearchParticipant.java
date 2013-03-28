/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.core.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.search.index.Index;
import org.eclipse.dltk.mod.core.search.index.MixinIndex;
import org.eclipse.dltk.mod.core.search.indexing.IndexManager;
import org.eclipse.dltk.mod.core.search.indexing.SourceIndexer;
import org.eclipse.dltk.mod.core.search.matching.MatchLocator;
import org.eclipse.dltk.mod.internal.core.Model;
import org.eclipse.dltk.mod.internal.core.search.IndexSelector;
import org.eclipse.dltk.mod.internal.core.search.LazyDLTKSearchDocument;

/**
 * A search participant describes a particular extension to a generic search
 * mechanism, allowing thus to perform combined search actions which will
 * involve all required participants
 * 
 * A search scope defines which participants are involved.
 * 
 * A search participant is responsible for holding index files, and selecting
 * the appropriate ones to feed to index queries. It also can map a document
 * path to an actual document (note that documents could live outside the
 * workspace or no exist yet, and thus aren't just resources).
 */
public class DLTKSearchParticipant extends SearchParticipant {
	private IndexSelector indexSelector;
	private boolean bOnlyMixin = false;

	public void beginSearching() {
		super.beginSearching();
		this.indexSelector = null;
	}

	public void doneSearching() {
		this.indexSelector = null;
		super.doneSearching();
	}

	public String getDescription() {
		return "DLTK"; //$NON-NLS-1$
	}

	public SearchDocument getDocument(String documentPath, IProject project) {
		return new LazyDLTKSearchDocument(documentPath, this,
				isExternal(documentPath), project);
	}

	private boolean isExternal(String documentPath) {
		Object target = Model.getTarget(ResourcesPlugin.getWorkspace()
				.getRoot(), new Path(documentPath), true);
		if (target instanceof IResource)
			return false;
		else
			return true;

	}

	public void indexDocument(SearchDocument document, IPath indexPath) {
		// TODO must verify that the document + indexPath match, when this is
		// not called from scheduleDocumentIndexing
		document.removeAllIndexEntries(); // in case the document was already
		// indexed
		new SourceIndexer(document).indexDocument();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SearchParticipant#locateMatches(SearchDocument[], SearchPattern,
	 * IDLTKSearchScope, SearchRequestor, IProgressMonitor)
	 */
	public void locateMatches(SearchDocument[] indexMatches,
			SearchPattern pattern, IDLTKSearchScope scope,
			SearchRequestor requestor, IProgressMonitor monitor)
			throws CoreException {
		MatchLocator matchLocator = createMatchLocator(pattern, requestor,
				scope, monitor);
		/* eliminating false matches and locating them */
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		matchLocator.locateMatches(indexMatches);
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		// matchLocator.locatePackageDeclarations(this);
	}

	public ISourceModule[] locateModules(SearchDocument[] indexMatches,
			SearchPattern pattern, IDLTKSearchScope scope,
			IProgressMonitor monitor) throws CoreException {
		MatchLocator matchLocator = createMatchLocator(pattern, null, scope,
				monitor == null ? null : new SubProgressMonitor(monitor, 95));
		/* eliminating false matches and locating them */
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		ISourceModule[] modules = matchLocator.locateModules(indexMatches);
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
		// matchLocator.locatePackageDeclarations(this);
		return modules;
	}

	protected MatchLocator createMatchLocator(SearchPattern pattern,
			SearchRequestor requestor, IDLTKSearchScope scope,
			IProgressMonitor monitor) {
		IDLTKLanguageToolkit toolkit = scope.getLanguageToolkit();
		if (toolkit != null) {
			MatchLocator locator = DLTKLanguageManager.createMatchLocator(
					toolkit.getNatureId(), pattern, requestor, scope,
					monitor == null ? null
							: new SubProgressMonitor(monitor, 95));
			if (locator != null) {
				return locator;
			}
		}
		return new MatchLocator(pattern, requestor, scope,
				monitor == null ? null : new SubProgressMonitor(monitor, 95));
	}

	public IPath[] selectIndexes(SearchPattern pattern, IDLTKSearchScope scope) {
		if (this.indexSelector == null) {
			this.indexSelector = new IndexSelector(scope, pattern);
		}
		return this.indexSelector.getIndexLocations();
	}

	public IPath[] selectMixinIndexes(SearchPattern query,
			IDLTKSearchScope scope) {
		this.skipNotMixin();
		return selectIndexes(query, scope);
	}

	public void skipNotMixin() {
		this.bOnlyMixin = true;
	}

	public boolean isSkipped(Index index) {
		final boolean mixinIndex = index instanceof MixinIndex
				|| index.containerPath.startsWith(IndexManager.SPECIAL_MIXIN);
		return this.bOnlyMixin != mixinIndex;
	}
}
