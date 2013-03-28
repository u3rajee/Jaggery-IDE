/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.corext.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IModelMarker;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IScriptFolder;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.core.ScriptModelUtil;
import org.eclipse.dltk.mod.internal.corext.refactoring.changes.RenameResourceChange;
import org.eclipse.dltk.mod.internal.corext.refactoring.util.ResourceUtil;
import org.eclipse.dltk.mod.internal.corext.util.Messages;
import org.eclipse.dltk.mod.internal.corext.util.Resources;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * This class defines a set of reusable static checks methods.
 */
public class Checks {

	/*
	 * no instances
	 */
	private Checks() {
	}

	/* Constants returned by checkExpressionIsRValue */
	public static final int IS_RVALUE = 0;
	public static final int NOT_RVALUE_MISC = 1;
	public static final int NOT_RVALUE_VOID = 2;

	public static boolean isAvailable(IModelElement modelElement)
			throws ModelException {
		if (modelElement == null)
			return false;
		if (!modelElement.exists())
			return false;
		if (modelElement.isReadOnly())
			return false;
		// work around for https://bugs.eclipse.org/bugs/show_bug.cgi?id=48422
		// the Script project is now cheating regarding its children so we
		// shouldn't
		// call isStructureKnown if the project isn't open.
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=52474
		if (!(modelElement instanceof IScriptProject)
				&& !modelElement.isStructureKnown())
			return false;
		if (DLTKCore.DEBUG) {
			System.err.println("Add binary modules support."); //$NON-NLS-1$
		}
		// if (modelElement instanceof IMember &&
		// ((IMember)modelElement).isBinary())
		// return false;
		return true;
	}

	public static boolean isBuildpathDelete(IProjectFragment pkgRoot) {
		IResource res = pkgRoot.getResource();
		if (res == null)
			return true;
		IProject definingProject = res.getProject();
		if (res.getParent() != null && pkgRoot.isArchive()
				&& !res.getParent().equals(definingProject))
			return true;

		IProject occurringProject = pkgRoot.getScriptProject().getProject();
		return !definingProject.equals(occurringProject);
	}

	public static RefactoringStatus checkSourceModuleNewName(ISourceModule cu,
			String newName) {
		String newCUName = ScriptModelUtil.getRenamedCUName(cu, newName);
		if (resourceExists(RenameResourceChange.renamedResourcePath(
				ResourceUtil.getResource(cu).getFullPath(), newCUName)))
			return RefactoringStatus.createFatalErrorStatus(Messages.format(
					RefactoringCoreMessages.Checks_cu_name_used, newName));
		else
			return new RefactoringStatus();
	}

	public static boolean resourceExists(IPath resourcePath) {
		return ResourcesPlugin.getWorkspace().getRoot()
				.findMember(resourcePath) != null;
	}

	public static RefactoringStatus validateModifiesFiles(
			IFile[] filesToModify, Object context) {
		RefactoringStatus result = new RefactoringStatus();
		IStatus status = Resources.checkInSync(filesToModify);
		if (!status.isOK())
			result.merge(RefactoringStatus.create(status));
		status = Resources.makeCommittable(filesToModify, context);
		if (!status.isOK()) {
			result.merge(RefactoringStatus.create(status));
			if (!result.hasFatalError()) {
				result
						.addFatalError(RefactoringCoreMessages.Checks_validateEdit);
			}
		}
		return result;
	}

	public static boolean isAlreadyNamed(IModelElement element, String name) {
		return name.equals(element.getElementName());
	}

	public static boolean isTopLevel(IType type) {
		return type.getDeclaringType() == null;
	}

	public static RefactoringStatus checkTypeName(String name) {
		if (name.indexOf(".") != -1) //$NON-NLS-1$
			return RefactoringStatus
					.createFatalErrorStatus(RefactoringCoreMessages.Checks_no_dot);
		else
			return checkName(name, JavaConventions.validateJavaTypeName(name));
	}

	public static RefactoringStatus checkPackageName(String newName) {
		return checkName(newName, JavaConventions.validatePackageName(newName));

	}

	/**
	 * Returns a fatal error in case the name is empty. In all other cases, an
	 * error based on the given status is returned.
	 * 
	 * @param name
	 *            a name
	 * @param status
	 *            a status
	 * @return RefactoringStatus based on the given status or the name, if
	 *         empty.
	 */
	public static RefactoringStatus checkName(String name, IStatus status) {
		RefactoringStatus result = new RefactoringStatus();
		if ("".equals(name)) //$NON-NLS-1$
			return RefactoringStatus
					.createFatalErrorStatus(RefactoringCoreMessages.Checks_Choose_name);

		if (status.isOK())
			return result;

		switch (status.getSeverity()) {
		case IStatus.ERROR:
			return RefactoringStatus
					.createFatalErrorStatus(status.getMessage());
		case IStatus.WARNING:
			return RefactoringStatus.createWarningStatus(status.getMessage());
		case IStatus.INFO:
			return RefactoringStatus.createInfoStatus(status.getMessage());
		default: // no nothing
			return new RefactoringStatus();
		}
	}

	public static RefactoringStatus checkIfCuBroken(IType type)
			throws ModelException {
		ISourceModule cu = (ISourceModule) DLTKCore.create(type
				.getSourceModule().getResource());
		if (cu == null)
			return RefactoringStatus
					.createFatalErrorStatus(RefactoringCoreMessages.Checks_cu_not_created);
		else if (!cu.isStructureKnown())
			return RefactoringStatus
					.createFatalErrorStatus(RefactoringCoreMessages.Checks_cu_not_parsed);
		return new RefactoringStatus();
	}

	public static RefactoringStatus checkForMainMethods(IType type) {
		// TODO add a warning for main methods? I think not quite necessary. by
		// eric.
		return null;
	}

	public static void checkCompileErrorsInAffectedFile(
			RefactoringStatus result, IResource resource) throws ModelException {
		if (hasCompileErrors(resource))
			result.addWarning(Messages.format(
					RefactoringCoreMessages.Checks_cu_has_compile_errors,
					resource.getFullPath().makeRelative()));

	}

	private static boolean hasCompileErrors(IResource resource)
			throws ModelException {
		try {
			IMarker[] problemMarkers = resource.findMarkers(
					IModelMarker.SCRIPT_MODEL_PROBLEM_MARKER, true,
					IResource.DEPTH_INFINITE);
			for (int i = 0; i < problemMarkers.length; i++) {
				if (problemMarkers[i].getAttribute(IMarker.SEVERITY, -1) == IMarker.SEVERITY_ERROR)
					return true;
			}
			return false;
		} catch (ModelException e) {
			throw e;
		} catch (CoreException e) {
			throw new ModelException(e);
		}
	}

	public static IType findTypeInScriptFolder(IScriptFolder scriptFolder,
			String newElementName) throws ModelException {
		Assert.isTrue(scriptFolder.exists());
		Assert.isTrue(!scriptFolder.isReadOnly());

		/* ICompilationUnit.getType expects simple name */
		if (newElementName.indexOf(".") != -1) //$NON-NLS-1$
			newElementName = newElementName.substring(0, newElementName
					.indexOf(".")); //$NON-NLS-1$

		ISourceModule[] cus = scriptFolder.getSourceModules();
		for (int i = 0; i < cus.length; i++) {
			if (cus[i].getType(newElementName).exists())
				return cus[i].getType(newElementName);
		}
		return null;
	}

	public static IFile[] getModifiedFiles(Change[] changes) {
		List result = new ArrayList();
		getModifiedFiles(result, changes);
		return (IFile[]) result.toArray(new IFile[result.size()]);
	}

	private static void getModifiedFiles(List result, Change[] changes) {
		for (int i = 0; i < changes.length; i++) {
			Change change = changes[i];
			Object modifiedElement = change.getModifiedElement();
			if (modifiedElement instanceof IAdaptable) {
				IFile file = (IFile) ((IAdaptable) modifiedElement)
						.getAdapter(IFile.class);
				if (file != null)
					result.add(file);
			}
			if (change instanceof CompositeChange) {
				getModifiedFiles(result, ((CompositeChange) change)
						.getChildren());
			}
		}
	}

	public static RefactoringStatus checkSourceModuleName(String newName) {
		IStatus status = null;
		if (newName == null) {
			status = new Status(IStatus.ERROR, DLTKCore.PLUGIN_ID, -1,
					RefactoringCoreMessages.convention_unit_nullName, null);
		}
		if (!newName.endsWith(".js")) {
			status = new Status(IStatus.ERROR, DLTKCore.PLUGIN_ID, -1,
					RefactoringCoreMessages.convention_unit_notJavaScriptName,
					null);
		}

		if (status != null) {
			return checkName(newName, status);
		}

		newName = removeJavaScriptLikeExtension(newName);
		return checkTypeName(newName);
	}

	public static String getFullPath(ISourceModule cu) {
		Assert.isTrue(cu.exists());
		return cu.getResource().getFullPath().toString();
	}

	public static String removeJavaScriptLikeExtension(String name) {
		if (name.lastIndexOf(".") != -1)
			return name.substring(0, name.lastIndexOf("."));
		return name;
	}

}
