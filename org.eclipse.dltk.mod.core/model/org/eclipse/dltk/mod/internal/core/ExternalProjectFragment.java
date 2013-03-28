/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IBuildpathContainer;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IModelStatusConstants;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IScriptFolder;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.WorkingCopyOwner;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.mod.internal.core.util.Util;

/**
 * Project fragment to external source folder.
 * 
 * @author haiodo
 * 
 */
public class ExternalProjectFragment extends ProjectFragment {
	public final static ArrayList EMPTY_LIST = new ArrayList();
	/**
	 * The path to the zip file (a workspace relative path if the archive is
	 * internal, or an OS path if the archive is external)
	 */
	protected final IPath fPath;
	protected final boolean fReadOnly;
	protected final boolean fOnlyScriptResources;

	protected ExternalProjectFragment(IPath path, ScriptProject project,
			boolean isReadOnly, boolean onlyScriptResources) {
		super(null, project);
		this.fPath = path;
		this.fReadOnly = isReadOnly;
		this.fOnlyScriptResources = onlyScriptResources;
	}

	/**
	 * Compute the package fragment children of this package fragment root.
	 */
	protected boolean computeChildren(OpenableElementInfo info, Map newElements)
			throws ModelException {
		ArrayList vChildren = new ArrayList(5);
		ArrayList vForeign = new ArrayList(5);
		char[][] inclusionPatterns = this.fullInclusionPatternChars();
		char[][] exclusionPatterns = this.fullExclusionPatternChars();
		Set realPaths = new HashSet();
		this.computeFolderChildren(this.fPath, !Util.isExcluded(this.fPath,
				inclusionPatterns, exclusionPatterns, true), vChildren,
				vForeign, newElements, inclusionPatterns, exclusionPatterns,
				realPaths);
		IModelElement[] children = new IModelElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;
	}

	/**
	 * Starting at this folder, create folders and add them to the collection of
	 * children.
	 * 
	 * @param newElements
	 * 
	 * @exception ModelException
	 *                The resource associated with this project fragment does
	 *                not exist
	 */
	protected void computeFolderChildren(IPath path, boolean isIncluded,
			ArrayList vChildren, ArrayList vForeign, Map newElements,
			char[][] inclusionPatterns, char[][] exclusionPatterns,
			Set realPaths) throws ModelException {
		IPath lpath = EnvironmentPathUtils.getLocalPath(path
				.removeFirstSegments(this.fPath.segmentCount()));
		IEnvironment environment = EnvironmentPathUtils
				.getPathEnvironment(path);
		if (environment != null) {
			IFileHandle file = environment.getFile(EnvironmentPathUtils
					.getLocalPath(path));
			String canonicalPath = file.getCanonicalPath();
			if (!realPaths.add(canonicalPath)) {
				return;
			}
		}

		ExternalScriptFolder fldr = (ExternalScriptFolder) this
				.getScriptFolder(lpath);
		boolean valid = Util.isValidSourcePackageName(this, path);
		if ((lpath.segmentCount() == 0 || valid) && isIncluded) {
			vChildren.add(fldr);
		} else {
			if (this.fOnlyScriptResources) {
				return;
			}
			if (!valid) {
				return;
			}
		}
		List scriptElements = new ArrayList();
		List nonScriptElements = new ArrayList();
		try {
			IFileHandle file = EnvironmentPathUtils.getFile(path);
			IFileHandle[] members = file.getChildren();
			if (members != null) {
				for (int i = 0, max = members.length; i < max; i++) {
					IFileHandle memberFile = members[i];
					IPath memberPath = memberFile.getFullPath();
					if (memberFile.isDirectory()) {
						boolean isMemberIncluded = !Util.isExcluded(memberPath,
								inclusionPatterns, exclusionPatterns, true);
						computeFolderChildren(memberPath, isMemberIncluded,
								vChildren, vForeign, newElements,
								inclusionPatterns, exclusionPatterns, realPaths);
					} else {
						if (Util.isValidSourceModule(this, memberPath)) {
							scriptElements.add(memberPath);
						} else {
							if (!this.fOnlyScriptResources || valid) {
								nonScriptElements.add(memberPath);
							}
						}
					}
				}
			}
			ExternalScriptFolderInfo fragInfo = new ExternalScriptFolderInfo();
			fldr.computeChildren(fragInfo, scriptElements);
			fldr.computeForeignResources(fragInfo, nonScriptElements);
			newElements.put(fldr, fragInfo);
		} catch (IllegalArgumentException e) {
			throw new ModelException(e,
					IModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
			/*
			 * could be thrown by ElementTree when path is not found
			 */
		} catch (CoreException e) {
			throw new ModelException(e);
		}
	}

	protected void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		escapeMementoName(buff, getElementName());
	}

	public IScriptFolder getScriptFolder(IPath path) {
		try {
			List childs = getChildrenOfType(SCRIPT_FOLDER);
			for (int i = 0; i < childs.size(); ++i) {
				IScriptFolder folder = (IScriptFolder) childs.get(i);
				if (folder.getElementName().equals(path.toPortableString())) {
					return folder;
				}
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return new ExternalScriptFolder(this, path);
	}

	public IScriptFolder getScriptFolder(String path) {
		return this.getScriptFolder(new Path(path));
	}

	public boolean isReadOnly() {
		return this.fReadOnly;
	}

	protected Object createElementInfo() {
		return new ExternalProjectFragmentInfo();
	}

	public boolean isArchive() {
		return false;
	}

	public boolean isExternal() {
		return true;
	}

	public IResource getUnderlyingResource() throws ModelException {
		return null;
	}

	public int hashCode() {
		return this.fPath.hashCode();
	}

	public IPath getPath() {
		return this.fPath;
	}

	public IResource getResource() {
		return null;
	}

	/**
	 * Returns whether the corresponding resource or associated file exists
	 */
	protected boolean resourceExists() {
		if (fPath.toString().startsWith(
				IBuildpathEntry.BUILTIN_EXTERNAL_ENTRY_STR)) {
			return true;
		}
		IFileHandle file = EnvironmentPathUtils.getFile(fPath);
		return file.exists() && file.isDirectory();
	}

	protected void toStringAncestors(StringBuffer buffer) {
	}

	public int getKind() {
		return IProjectFragment.K_SOURCE;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o instanceof ExternalProjectFragment) {
			ExternalProjectFragment other = (ExternalProjectFragment) o;
			IEnvironment environment = EnvironmentManager.getEnvironment(this);
			if (environment != null) {
				IEnvironment environmento = EnvironmentManager
						.getEnvironment((IModelElement) other);
				if (!environment.equals(environmento)) {
					return false;
				}
			}
			return this.fPath.equals(other.fPath);
		}
		return false;
	}

	public String getElementName() {
		IEnvironment env = EnvironmentManager.getEnvironment(this);
		String pathString = EnvironmentPathUtils.getLocalPathString(fPath);
		return pathString.replace(env.getSeparatorChar(), JEM_SKIP_DELIMETER);
	}

	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_SCRIPTFOLDER:
			String pkgName;
			if (memento.hasMoreTokens()) {
				pkgName = memento.nextToken();
				char firstChar = pkgName.charAt(0);
				if (firstChar == JEM_SOURCEMODULE || firstChar == JEM_COUNT) {
					token = pkgName;
					pkgName = IProjectFragment.DEFAULT_SCRIPT_FOLDER_NAME;
				} else {
					token = null;
				}
			} else {
				pkgName = IScriptFolder.DEFAULT_FOLDER_NAME;
				token = null;
			}
			ModelElement pkg = (ModelElement) this.getScriptFolder(pkgName);
			if (token == null) {
				return pkg.getHandleFromMemento(memento, owner);
			} else {
				return pkg.getHandleFromMemento(token, memento, owner);
			}
		}
		return null;
	}

	protected char getHandleMementoDelimiter() {
		return JEM_PROJECTFRAGMENT;
	}

	public IBuildpathEntry getBuildpathEntry() throws ModelException {
		IBuildpathEntry rawEntry = super.getRawBuildpathEntry();
		// try to guest map from internal element.
		if (rawEntry != null
				&& rawEntry.getEntryKind() == IBuildpathEntry.BPE_CONTAINER) {
			IBuildpathContainer container = DLTKCore.getBuildpathContainer(
					rawEntry.getPath(), this.getScriptProject());
			IBuildpathEntry entrys[] = container
					.getBuildpathEntries(getScriptProject());
			for (int i = 0; i < entrys.length; ++i) {
				if (entrys[i].getPath().equals(this.getPath())) {
					return entrys[i];
				}
			}
		}

		return rawEntry;
	}
}
