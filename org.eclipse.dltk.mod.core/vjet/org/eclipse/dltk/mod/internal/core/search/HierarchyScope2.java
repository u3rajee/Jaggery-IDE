/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.search;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.vjet.eclipse.core.IJSType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IMember;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IModelElementDelta;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IScriptModel;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ITypeHierarchy;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.WorkingCopyOwner;
import org.eclipse.dltk.mod.internal.core.ArchiveProjectFragment;
import org.eclipse.dltk.mod.internal.core.Model;
import org.eclipse.dltk.mod.internal.core.ModelElement;
import org.eclipse.dltk.mod.internal.core.ModelManager;
import org.eclipse.dltk.mod.internal.core.ScriptProject;
import org.eclipse.dltk.mod.internal.core.SuffixConstants;
import org.eclipse.dltk.mod.internal.core.hierarchy.TypeHierarchy;

/**
 * Scope limited to the subtype and supertype hierarchy of a given type.
 * 
 * NOTE: This class was being overwritten by the VJET fragment. I have changed
 * the name to HierarchyScope2. This class and its constructors are incompatible
 * with HierarchyScope.
 * 
 * @author jricker
 * @since 2009.04.07
 */
public class HierarchyScope2 extends AbstractSearchScope implements
		SuffixConstants {

	protected int m_elementCount;
	protected IResource[] m_elements;
	private IPath[] m_enclosingProjectsAndJars;

	private String m_focusPath;
	public IJSType m_focusType;
	private ITypeHierarchy m_hierarchy;
	public boolean m_needsRefresh;

	private WorkingCopyOwner m_owner;
	private HashSet<String> m_resourcePaths;

	private IJSType[] m_types;

	/**
	 * Creates a new hiearchy scope for the given type.
	 */
	public HierarchyScope2(IType type, WorkingCopyOwner owner)
			throws ModelException {
		this.m_focusType = (IJSType) type;
		this.m_owner = owner;

		this.m_enclosingProjectsAndJars = this.computeProjectsAndJars(type);

		// resource path
		IProjectFragment root = (IProjectFragment) ((IJSType) type)
				.getProjectFragment().getParent();
		if (root.isArchive()) {
			IPath jarPath = root.getPath();
			Object target = Model.getTarget(ResourcesPlugin.getWorkspace()
					.getRoot(), jarPath, true);
			String zipFileName;
			if (target instanceof IFile) {
				// internal jar
				zipFileName = jarPath.toString();
			} else if (target instanceof File) {
				// external jar
				zipFileName = ((File) target).getPath();
			} else {
				return; // unknown target
			}

			this.m_focusPath = zipFileName + FILE_ENTRY_SEPARATOR
					+ type.getFullyQualifiedName().replace('.', '/') + ".js"
					+ SUFFIX_STRING_vjo;
		} else {
			this.m_focusPath = type.getPath().toString();
		}

		this.m_needsRefresh = true;

		// disabled for now as this could be expensive
		// JavaModelManager.getJavaModelManager().rememberScope(this);
	}

	/*
	 * (non-Javadoc) Adds the given resource to this search scope.
	 */
	public void add(IResource element) {
		if (this.m_elementCount == this.m_elements.length) {
			System.arraycopy(this.m_elements, 0,
					this.m_elements = new IResource[this.m_elementCount * 2],
					0, this.m_elementCount);
		}
		m_elements[m_elementCount++] = element;
	}

	private void buildResourceVector() {
		HashMap resources = new HashMap();
		HashMap paths = new HashMap();
		this.m_types = (IJSType[]) this.m_hierarchy.getAllTypes();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (int i = 0; i < this.m_types.length; i++) {
			IType type = this.m_types[i];
			IResource resource = type.getResource();
			if (resource != null && resources.get(resource) == null) {
				resources.put(resource, resource);
				add(resource);
			}
			IProjectFragment root = (IProjectFragment) ((IJSType) type)
					.getProjectFragment().getParent();
			if (root instanceof ArchiveProjectFragment) {
				// type in a jar
				ArchiveProjectFragment jar = (ArchiveProjectFragment) root;
				IPath jarPath = jar.getPath();
				Object target = Model.getTarget(workspaceRoot, jarPath, true);
				String zipFileName;
				if (target instanceof IFile) {
					// internal jar
					zipFileName = jarPath.toString();
				} else if (target instanceof File) {
					// external jar
					zipFileName = ((File) target).getPath();
				} else {
					continue; // unknown target
				}
				// TODO Fix resourcePath
				String resourcePath = zipFileName + FILE_ENTRY_SEPARATOR
						+ type.getFullyQualifiedName().replace('.', '/')
						+ SUFFIX_STRING_vjo;

				this.m_resourcePaths.add(resourcePath);
				paths.put(jarPath, type);
			} else {
				// type is a project
				paths.put(type.getScriptProject().getProject().getFullPath(),
						type);
			}
		}
		this.m_enclosingProjectsAndJars = new IPath[paths.size()];
		int i = 0;
		for (Iterator iter = paths.keySet().iterator(); iter.hasNext();) {
			this.m_enclosingProjectsAndJars[i++] = (IPath) iter.next();
		}
	}

	private void computeDependents(IScriptProject project, HashSet set,
			HashSet visited) {
		if (visited.contains(project))
			return;
		visited.add(project);
		IProject[] dependents = project.getProject().getReferencingProjects();
		for (int i = 0; i < dependents.length; i++) {
			try {
				IScriptProject dependent = DLTKCore.create(dependents[i]);
				IProjectFragment[] roots = dependent.getProjectFragments();
				set.add(dependent.getPath());
				for (int j = 0; j < roots.length; j++) {
					IProjectFragment pkgFragmentRoot = roots[j];
					if (pkgFragmentRoot.isArchive()) {
						set.add(pkgFragmentRoot.getPath());
					}
				}
				this.computeDependents(dependent, set, visited);
			} catch (ModelException e) {
				// project is not a script project
			}
		}
	}

	/*
	 * Computes the paths of projects and jars that the hierarchy on the given
	 * type could contain. This is a super set of the project and jar paths once
	 * the hierarchy is computed.
	 */
	private IPath[] computeProjectsAndJars(IType type) throws ModelException {
		HashSet set = new HashSet();
		IProjectFragment root = (IProjectFragment) ((IJSType) type)
				.getProjectFragment().getParent();
		if (root.isArchive()) {
			// add the root
			set.add(root.getPath());
			// add all projects that reference this archive and their dependents
			IPath rootPath = root.getPath();
			IScriptModel model = ModelManager.getModelManager().getModel();
			IScriptProject[] projects = model.getScriptProjects();
			HashSet visited = new HashSet();
			for (int i = 0; i < projects.length; i++) {
				ScriptProject project = (ScriptProject) projects[i];
				IBuildpathEntry[] classpath = project.getResolvedBuildpath(
						true/* ignoreUnresolvedEntry */, false/*
															 * don't
															 * generateMarkerOnError
															 */, false/*
																	 * don't
																	 * returnResolutionInProgress
																	 */);
				for (int j = 0; j < classpath.length; j++) {
					if (rootPath.equals(classpath[j].getPath())) {
						// add the project and its binary pkg fragment roots
						IProjectFragment[] roots = project
								.getAllProjectFragments();
						set.add(project.getPath());
						for (int k = 0; k < roots.length; k++) {
							IProjectFragment pkgFragmentRoot = roots[k];
							if (pkgFragmentRoot.getKind() == IProjectFragment.K_BINARY) {
								set.add(pkgFragmentRoot.getPath());
							}
						}
						// add the dependent projects
						this.computeDependents(project, set, visited);
						break;
					}
				}
			}
		} else {
			// add all the project's pkg fragment roots
			ScriptProject project = (ScriptProject) root.getParent();
			IProjectFragment[] roots = project.getAllProjectFragments();
			for (int i = 0; i < roots.length; i++) {
				IProjectFragment pkgFragmentRoot = roots[i];
				if (pkgFragmentRoot.getKind() == IProjectFragment.K_BINARY) {
					set.add(pkgFragmentRoot.getPath());
				} else {
					set.add(pkgFragmentRoot.getParent().getPath());
				}
			}
			// add the dependent projects
			this.computeDependents(project, set, new HashSet());
		}
		IPath[] result = new IPath[set.size()];
		set.toArray(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#encloses(IJavaElement)
	 */
	public boolean encloses(IModelElement element) {
		if (this.m_hierarchy == null) {
			if (this.m_focusType
					.equals(element.getAncestor(IModelElement.TYPE))) {
				return true;
			} else {
				if (this.m_needsRefresh) {
					try {
						this.initialize();
					} catch (ModelException e) {
						return false;
					}
				} else {
					// the scope is used only to find enclosing projects and
					// jars
					// clients is responsible for filtering out elements not in
					// the hierarchy (see SearchEngine)
					return true;
				}
			}
		}
		if (this.m_needsRefresh) {
			try {
				this.refresh();
			} catch (ModelException e) {
				return false;
			}
		}
		IType type = null;
		if (element instanceof IType) {
			type = (IType) element;
		} else if (element instanceof IMember) {
			type = ((IMember) element).getDeclaringType();
		}
		if ((type != null) && (this.m_hierarchy.contains(type))) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#encloses(String)
	 */
	public boolean encloses(String resourcePath) {
		if (this.m_hierarchy == null) {
			if (resourcePath.equals(this.m_focusPath)) {
				return true;
			} else {
				if (this.m_needsRefresh) {
					try {
						this.initialize();
					} catch (ModelException e) {
						return false;
					}
				} else {
					// the scope is used only to find enclosing projects and
					// jars
					// clients is responsible for filtering out elements not in
					// the hierarchy (see SearchEngine)
					return true;
				}
			}
		}
		if (this.m_needsRefresh) {
			try {
				this.refresh();
			} catch (ModelException e) {
				return false;
			}
		}
		int separatorIndex = resourcePath.indexOf(FILE_ENTRY_SEPARATOR);
		if (separatorIndex != -1) {
			return this.m_resourcePaths.contains(resourcePath);
		} else {
			for (int i = 0; i < this.m_elementCount; i++) {
				if (resourcePath.startsWith(this.m_elements[i].getFullPath()
						.toString())) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IJavaSearchScope#enclosingProjectsAndJars()
	 * 
	 * @deprecated
	 */
	public IPath[] enclosingProjectsAndZips() {
		if (this.m_needsRefresh) {
			try {
				this.refresh();
			} catch (ModelException e) {
				return new IPath[0];
			}
		}
		return this.m_enclosingProjectsAndJars;
	}

	public IDLTKLanguageToolkit getLanguageToolkit() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void initialize() throws ModelException {
		this.m_resourcePaths = new HashSet();
		this.m_elements = new IResource[5];
		this.m_elementCount = 0;
		this.m_needsRefresh = false;
		if (this.m_hierarchy == null) {
			this.m_hierarchy = this.m_focusType.newTypeHierarchy(this.m_owner,
					null);
		} else {
			this.m_hierarchy.refresh(null);
		}
		this.buildResourceVector();
	}

	/*
	 * @see AbstractSearchScope#processDelta(IJavaElementDelta)
	 */
	@Override
	public void processDelta(IModelElementDelta delta) {
		if (this.m_needsRefresh)
			return;
		this.m_needsRefresh = this.m_hierarchy == null ? false
				: ((TypeHierarchy) this.m_hierarchy).isAffected(delta);
	}

	protected void refresh() throws ModelException {
		if (this.m_hierarchy != null) {
			this.initialize();
		}
	}

	@Override
	public String toString() {
		return "HierarchyScope on " + ((ModelElement) this.m_focusType).toStringWithAncestors(); //$NON-NLS-1$
	}

}
