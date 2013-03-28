/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.mod.core.search.indexing;

import java.io.IOException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.mod.compiler.util.SimpleLookupTable;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.DLTKLanguageManager;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceElementParser;
import org.eclipse.dltk.mod.core.search.index.Index;
import org.eclipse.dltk.mod.internal.core.BuildpathEntry;
import org.eclipse.dltk.mod.internal.core.ScriptProject;
import org.eclipse.dltk.mod.internal.core.search.processing.JobManager;
import org.eclipse.dltk.mod.internal.core.util.Util;

public class IndexAllProject extends IndexRequest {
	private final IProject project;

	public IndexAllProject(IProject project, IndexManager manager) {
		super(project.getFullPath(), manager);
		this.project = project;
	}

	public boolean equals(Object o) {
		if (o instanceof IndexAllProject) {
			return this.project.equals(((IndexAllProject) o).project);
		}
		return false;
	}

	/**
	 * Ensure consistency of a project index. Need to walk all nested resources,
	 * and discover resources which have either been changed, added or deleted
	 * since the index was produced.
	 */
	public boolean execute(IProgressMonitor progressMonitor) {

		if (this.isCancelled || progressMonitor != null
				&& progressMonitor.isCanceled()) {
			return true;
		}
		if (!this.project.isAccessible()) {
			return true; // nothing to do
		}

		ReadWriteMonitor monitor = null;
		try {
			// Get source folder entries. Libraries are done as a separate job
			ScriptProject scriptProject = (ScriptProject) DLTKCore
					.create(this.project);
			IBuildpathEntry[] entries = scriptProject.getRawBuildpath(false,
					false);
			int length = entries.length;
			IBuildpathEntry[] sourceEntries = new IBuildpathEntry[length];
			int sourceEntriesNumber = 0;
			for (int i = 0; i < length; i++) {
				IBuildpathEntry entry = entries[i];
				if (entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
					sourceEntries[sourceEntriesNumber++] = entry;
				}
			}
			if (sourceEntriesNumber == 0) {
				IPath projectPath = scriptProject.getPath();
				for (int i = 0; i < length; i++) {
					IBuildpathEntry entry = entries[i];
					if (entry.getEntryKind() == IBuildpathEntry.BPE_LIBRARY
							&& entry.getPath().equals(projectPath)) {
						// the project is also a library folder (see
						// https://bugs.eclipse.org/bugs/show_bug.cgi?id=89815)
						// ensure a job exists to index it as a binary folder
						final BuildpathEntry bpEntry = (BuildpathEntry) entry;
						this.manager.indexLibrary(projectPath, project, bpEntry
								.fullInclusionPatternChars(), bpEntry
								.fullExclusionPatternChars());
						return true;
					}
				}

				// nothing to index but want to save an empty index file so its
				// not 'rebuilt' when part of a search request
				Index index = this.manager.getIndexForUpdate(
						this.containerPath, true, true);
				/* reuse index file */
				/* create if none */
				if (index != null) {
					this.manager.saveIndex(index);
				}
				return true;
			}
			if (sourceEntriesNumber != length) {
				System
						.arraycopy(
								sourceEntries,
								0,
								sourceEntries = new IBuildpathEntry[sourceEntriesNumber],
								0, sourceEntriesNumber);
			}

			Index index = this.manager.getIndexForUpdate(this.containerPath,
					true, /* reuse index file */true /* create if none */);
			if (index == null) {
				return true;
			}
			monitor = index.monitor;
			if (monitor == null) {
				return true; // index got deleted since acquired
			}

			monitor.enterRead(); // ask permission to read

			String[] paths = index.queryDocumentNames(""); // all file names //$NON-NLS-1$
			int max = paths == null ? 0 : paths.length;
			final SimpleLookupTable indexedFileNames = new SimpleLookupTable(
					max == 0 ? 33 : max + 11);
			final String OK = "OK"; //$NON-NLS-1$
			final String DELETED = "DELETED"; //$NON-NLS-1$
			if (paths != null) {
				for (int i = 0; i < max; i++) {
					indexedFileNames.put(paths[i], DELETED);
				}
			}
			final long indexLastModified = max == 0 ? 0L : index.getIndexFile()
					.lastModified();

			IWorkspaceRoot root = this.project.getWorkspace().getRoot();
			for (int i = 0; i < sourceEntriesNumber; i++) {
				if (this.isCancelled) {
					return false;
				}

				IBuildpathEntry entry = sourceEntries[i];
				IResource sourceFolder = root.findMember(entry.getPath());
				if (sourceFolder != null) {

					// collect output locations if source is project (see
					// http://bugs.eclipse.org/bugs/show_bug.cgi?id=32041)

					final char[][] inclusionPatterns = ((BuildpathEntry) entry)
							.fullInclusionPatternChars();
					final char[][] exclusionPatterns = ((BuildpathEntry) entry)
							.fullExclusionPatternChars();
					if (max == 0) {
						sourceFolder.accept(new IResourceProxyVisitor() {
							public boolean visit(IResourceProxy proxy) {
								if (IndexAllProject.this.isCancelled) {
									return false;
								}
								switch (proxy.getType()) {
								case IResource.FILE:
									// if
									//(org.eclipse.dltk.mod.internal.core.util.Util.
									// isValidSourceModuleName(parent,
									// proxy.getName())) {
									IScriptProject scriptProject = DLTKCore
											.create(IndexAllProject.this.project);
									IFile file = (IFile) proxy
											.requestResource();
									if (Util.isValidSourceModule(scriptProject,
											file)) {
										if (exclusionPatterns != null
												|| inclusionPatterns != null) {
											if (Util.isExcluded(file,
													inclusionPatterns,
													exclusionPatterns)) {
												return false;
											}
										}
										indexedFileNames.put(Util.relativePath(
												file.getFullPath(), 1), file);
										/* remove project segment */
									}
									return false;
								case IResource.FOLDER:
									if (exclusionPatterns != null
											&& inclusionPatterns == null) {
										// if there are inclusion patterns then
										// we must walk the children
										if (Util.isExcluded(proxy
												.requestFullPath(),
												inclusionPatterns,
												exclusionPatterns, true)) {
											return false;
										}
									}
								}
								return true;
							}
						}, IResource.NONE);
					} else {
						sourceFolder.accept(new IResourceProxyVisitor() {
							public boolean visit(IResourceProxy proxy)
									throws CoreException {
								if (IndexAllProject.this.isCancelled) {
									return false;
								}
								switch (proxy.getType()) {
								case IResource.FILE:
									IScriptProject scriptProject = DLTKCore
											.create(IndexAllProject.this.project);
									IFile file = (IFile) proxy
											.requestResource();
									if (Util.isValidSourceModule(scriptProject,
											file)) {
										URI location = file.getLocationURI();
										if (location == null) {
											return false;
										}
										if (exclusionPatterns != null
												|| inclusionPatterns != null) {
											if (Util.isExcluded(file,
													inclusionPatterns,
													exclusionPatterns)) {
												return false;
											}
										}
										String relativePathString = Util
												.relativePath(file
														.getFullPath(), 1);
										/* remove project segment */
										indexedFileNames
												.put(
														relativePathString,
														indexedFileNames
																.get(relativePathString) == null
																|| indexLastModified < EFS
																		.getStore(
																				location)
																		.fetchInfo()
																		.getLastModified() ? (Object) file
																: (Object) OK);
									}
									return false;
								case IResource.FOLDER:
									if (exclusionPatterns != null
											|| inclusionPatterns != null) {
										if (Util.isExcluded(proxy
												.requestResource(),
												inclusionPatterns,
												exclusionPatterns)) {
											return false;
										}
									}
								}
								return true;
							}
						}, IResource.NONE);
					}
				}
			}

			ISourceElementParser parser = this.manager
					.getSourceElementParser(scriptProject);
			SourceIndexerRequestor requestor = this.manager
					.getSourceRequestor(scriptProject);
			final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
					.getLanguageToolkit(scriptProject);
			Object[] names = indexedFileNames.keyTable;
			Object[] values = indexedFileNames.valueTable;
			for (int i = 0, namesLength = names.length; i < namesLength; i++) {
				String name = (String) names[i];
				if (name != null) {
					if (this.isCancelled) {
						return false;
					}

					Object value = values[i];
					if (value != OK) {
						if (value == DELETED) {
							this.manager.remove(name, this.containerPath);
						} else {
							this.manager.addSource((IFile) value,
									this.containerPath, parser, requestor,
									toolkit);
						}
					}
				}
			}

			// request to save index when all cus have been indexed... also sets
			// state to SAVED_STATE
			this.manager
					.request(new SaveIndex(this.containerPath, this.manager));
		} catch (CoreException e) {
			if (JobManager.VERBOSE) {
				Util
						.verbose(
								"-> failed to index " + this.project + " because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.containerPath);
			return false;
		} catch (IOException e) {
			if (JobManager.VERBOSE) {
				Util
						.verbose(
								"-> failed to index " + this.project + " because of the following exception:", System.err); //$NON-NLS-1$ //$NON-NLS-2$
				e.printStackTrace();
			}
			this.manager.removeIndex(this.containerPath);
			return false;
		} finally {
			if (monitor != null) {
				monitor.exitRead(); // free read lock
			}
		}
		return true;
	}

	public int hashCode() {
		return this.project.hashCode();
	}

	protected Integer updatedIndexState() {
		return IndexManager.REBUILDING_STATE;
	}

	public String toString() {
		return "indexing project " + this.project.getFullPath(); //$NON-NLS-1$
	}
}
