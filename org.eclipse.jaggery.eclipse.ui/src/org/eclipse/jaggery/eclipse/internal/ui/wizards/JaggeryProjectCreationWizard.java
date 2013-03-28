package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IBuildpathAttribute;
import org.eclipse.dltk.mod.core.IModelElement;
import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.ui.DLTKUIPlugin;
import org.eclipse.dltk.mod.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.mod.ui.util.IStatusChangeListener;
import org.eclipse.dltk.mod.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.mod.ui.wizards.NewElementWizard;
import org.eclipse.dltk.mod.ui.wizards.ProjectWizardFirstPage;
import org.eclipse.dltk.mod.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.dltk.mod.ui.wizards.WorkingSetConfigurationBlock;
import org.eclipse.jaggery.eclipse.core.JaggeryNature;
import org.eclipse.jaggery.eclipse.core.JaggeryPlugin;
import org.eclipse.jaggery.eclipse.internal.ui.preferences.JaggeryBuildpathBlock;
import org.eclipse.jaggery.eclipse.ui.JaggeryScriptImages;
import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.internal.core.BuildpathEntry;
import org.eclipse.dltk.mod.internal.core.ScriptProject;




public class JaggeryProjectCreationWizard  extends NewElementWizard implements
INewWizard, IExecutableExtension{
	
	private static final Path[] EMPTY_PATH = new Path[0];
	private static final String SOURCE_FOLDER_NAME = "src";
	private static final String ROOT_STRING = "/";
	private IConfigurationElement fConfigElement;
	private ProjectWizardFirstPage fFirstPage = new JAGProjectWizardFirstPage();
	private ProjectWizardSecondPage fSecondPage;
	
	public JaggeryProjectCreationWizard(){
		setDefaultPageImageDescriptor(JaggeryScriptImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(JaggeryWizardMessages.ProjectCreationWizard_title);
	}
	
	public void addPages(){
		super.addPages();
		fFirstPage.setTitle(JaggeryWizardMessages.ProjectCreationWizardFirstPage_title);
		fFirstPage.setDescription(JaggeryWizardMessages.ProjectCreationWizardFirstPage_description);
		addPage(fFirstPage);
		
		fSecondPage = new ProjectWizardSecondPage(fFirstPage) {
			
			@Override
			protected String getScriptNature() {
				// TODO Auto-generated method stub
				return JaggeryNature.NATURE_ID;
			}
			
			
			
			@Override
			protected IPreferenceStore getPreferenceStore() {
				// TODO Auto-generated method stub
				return JaggeryUIPlugin.getDefault().getPreferenceStore();
			}
			
			@Override
			public void createControl (Composite parent){
				super.createControl(parent);
				PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IHelpContextIds.NEW_PROJECT);
					
			}
			
			@Override
			public void init(IScriptProject jproject,IBuildpathEntry[] defaultEntries, boolean defaultsOverrideExistingBuildpath){
				List<IBuildpathEntry> entriesList = new ArrayList<IBuildpathEntry>();
				if(defaultEntries!=null && defaultEntries.length>0){
					for(IBuildpathEntry e: defaultEntries){
						entriesList.add(e);
					}
				}
				
				entriesList.add(DLTKCore.newContainerEntry(getJagSDKPath()));
				entriesList.add(DLTKCore.newContainerEntry(getBrowserSDKPath()));
				entriesList.add(DLTKCore.newContainerEntry(getJagPath()));
				IBuildpathEntry[] ary = new IBuildpathEntry[]{};
				super.init(jproject, entriesList.toArray(ary), defaultsOverrideExistingBuildpath);
				
			}

			@Override
			protected BuildpathsBlock createBuildpathBlock(
					IStatusChangeListener listener) {
				return new JaggeryBuildpathBlock(new BusyIndicatorRunnableContext(), listener, 0, useNewSourcePage(), null);
			}
		};
		addPage(fSecondPage);
	
	}
	
	private IPath getSdkPath() {
		IPath path = new Path(JaggeryPlugin.SDK_CONTAINER);
		path = path.append(JaggeryPlugin.ID_DEFAULT_SDK);
		return path;
	}
	
	private IPath getJagSDKPath() {
		IPath path = new Path(JaggeryPlugin.JAGNATIVESDK_ID);
		path = path.append(JaggeryPlugin.JAG_DEFAULT_SDK_LABEL);
		return path;
	}
	
	private IPath getBrowserSDKPath() {
		IPath path = new Path(JaggeryPlugin.BROWSERSDK_ID);
		path = path.append(JaggeryPlugin.BROWSERSDK_LABEL);
		return path;
	}
	
	private IPath getJagPath() {
		IPath path = new Path(JaggeryPlugin.JAGLIB_ID);
		path = path.append(JaggeryPlugin.JAGLIB_LABEL);
		return path;
	}
	
	
	

	@Override
	public void setInitializationData(IConfigurationElement cfig, String propertyName,
			Object data) throws CoreException {
		fConfigElement = cfig;
		
	}

	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException, Exception{
		fSecondPage.performFinish(monitor);
		
	}

	@Override
	public IModelElement getCreatedElement() {
		// TODO Auto-generated method stub
		return DLTKCore.create(fFirstPage.getProjectHandle());
	}
	
	private IBuildpathEntry[] getBuildpathEntries(IScriptProject project,
			IPath path) throws ModelException {
		IBuildpathEntry[] entries = project.getRawBuildpath();
		IBuildpathEntry[] newEntries = new BuildpathEntry[entries.length];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		newEntries[0] = new BuildpathEntry(IProjectFragment.K_SOURCE,
				BuildpathEntry.BPE_SOURCE, path, false, EMPTY_PATH, EMPTY_PATH,
				null, false, new IBuildpathAttribute[0], false);
		return newEntries;
	}
	
	private void addDefaultSourceFolder(ScriptProject project) {
		Workspace workspace = (Workspace) project.getProject().getWorkspace();
		IPath path = new Path(ROOT_STRING);
		path = path.append(project.getElementName());
		path = path.append(SOURCE_FOLDER_NAME);
		IFolder folder = (IFolder) workspace
				.newResource(path, IResource.FOLDER);
		try {
			folder.create(false, true, new NullProgressMonitor());
			IBuildpathEntry[] newEntries = getBuildpathEntries(project, path);
			project.saveBuildpath(newEntries);
		} catch (CoreException e) {
			DLTKCore.error(e.toString(), e);
		}
	}
	
	public boolean performFinish() {
		boolean res = super.performFinish();
		if (res) {
			BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
			ScriptProject project = (ScriptProject) fSecondPage
					.getScriptProject();
			
			// Add by Oliver. Make VJO project to be organized by work set.
			IWorkingSet[] workingSets= fFirstPage.getWorkingSets();
			WorkingSetConfigurationBlock.addToWorkingSets(project, workingSets);
			
			//delete by Kevin, to fix bug 2517, don't create source folder automatically
			//addDefaultSourceFolder(project);
			selectAndReveal(project.getProject());
			
			
			
			
		}
		return res;
	}
	
	@Override
	public boolean performCancel() {
		fSecondPage.performCancel();
		return super.performCancel();
	}




}
