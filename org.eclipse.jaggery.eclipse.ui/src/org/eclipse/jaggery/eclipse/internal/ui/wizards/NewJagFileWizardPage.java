package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewJagFileWizardPage extends WizardNewFileCreationPage {
	
	private IContentType	fContentType;
	private List			fValidExtensions = null;

	public NewJagFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		
	}
	
	protected void initialPopulateContainerNameField() {
		
		super.initialPopulateContainerNameField();
		
		IPath fullPath = getContainerFullPath();
		if (fullPath != null && fullPath.segmentCount() > 0) {
			IProject project = getProjectFromPath(fullPath);
			IPath webContentPath = getWebContentPath(project);
			IPath projectPath = project.getFullPath();
			if (projectPath.equals(fullPath))
				setContainerFullPath(webContentPath);
			else
				setContainerFullPath(fullPath);
		}
	
			
	}
	
	protected boolean validatePage() {
		setMessage(null);
		setErrorMessage(null);
		
		if (!super.validatePage()) {
			return false;
		}
		
		String fileName = getFileName();
		IPath fullPath = getContainerFullPath();
		if ((fullPath != null) && (fullPath.isEmpty() == false) && (fileName != null)) {
			// check that filename does not contain invalid extension
			if (!extensionValidForContentType(fileName)) {
				setErrorMessage(NLS.bind(JaggeryWizardMessages.Jaggery_Error_Filename_Must_End_JAG, getValidExtensions().toString()));
				return false;
			}
			// no file extension specified so check adding default
			// extension doesn't equal a file that already exists
			if (fileName.lastIndexOf('.') == -1) {
				String newFileName = addDefaultExtension(fileName);
				IPath resourcePath = fullPath.append(newFileName);

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IStatus result = workspace.validatePath(resourcePath.toString(), IResource.FOLDER);
				if (!result.isOK()) {
					// path invalid
					setErrorMessage(result.getMessage());
					return false;
				}

				if ((workspace.getRoot().getFolder(resourcePath).exists() || workspace.getRoot().getFile(resourcePath).exists())) {
					setErrorMessage(JaggeryWizardMessages.Jaggery_Resource_Group_Name_Exists);
					return false;
				}
			}
			
			// get the IProject for the selection path
			IProject project = getProjectFromPath(fullPath);
			// if inside web project, check if inside webContent folder
			if (project != null && isWebProject(project)) {
				// check that the path is inside the webContent folder
				IPath webContentPath = getWebContentPath(project);
				if (!webContentPath.isPrefixOf(fullPath)) {
					setMessage(JaggeryWizardMessages.Jaggery_Warning_Folder_Must_Be_Inside_Web_Content, WARNING);
				}
			}
		}

		return true;
	}

	private IPath getWebContentPath(IProject project) {
		IPath path = null;
	
		if (project != null && isWebProject(project)) {	
			path = project.getFullPath();
			path.append("/"); //$NON-NLS-1$
		}
	
		return path;
	}
	
	String addDefaultExtension(String filename) {
		StringBuffer newFileName = new StringBuffer(filename);
		newFileName.append("."); //$NON-NLS-1$
		newFileName.append("jag"); //$NON-NLS-1$
		return newFileName.toString();
	}


	private IProject getProjectFromPath(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = null;
	
		if (path != null) {
			if (workspace.validatePath(path.toString(), IResource.PROJECT).isOK()) {
				project = workspace.getRoot().getProject(path.toString());
			} else {
				project = workspace.getRoot().getFile(path).getProject();
			}
		}
	
		return project;
	}
	
	private boolean isWebProject(IProject project) {
		return true;
	}
	
	private boolean extensionValidForContentType(String fileName) {
		boolean valid = false;

		IContentType type = getContentType();
		// there is currently an extension
		if (fileName.lastIndexOf('.') != -1) {
			// check what content types are associated with current extension
			IContentType[] types = Platform.getContentTypeManager().findContentTypesFor(fileName);
			int i = 0;
			while (i < types.length && !valid) {
				valid = types[i].isKindOf(type);
				++i;
			}
		}
		else
			valid = true; // no extension so valid
		return valid;
	}
	
	private List getValidExtensions() {
		if (fValidExtensions == null) {
			IContentType type = getContentType();
			if(type!=null){
				fValidExtensions = new ArrayList(Arrays.asList(type.getFileSpecs(IContentType.FILE_EXTENSION_SPEC)));
			}
		}
		return fValidExtensions;
	}
	
	private IContentType getContentType() {
		if (fContentType == null){
			fContentType = Platform.getContentTypeManager().getContentType("org.eclipse.dltk.mod.jaggeryContentType"); //$NON-NLS-1$
		}
		return fContentType;
	}

}
