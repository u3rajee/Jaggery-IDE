package org.eclipse.jaggery.eclipse.internal.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.jaggery.eclipse.ui.JaggeryUIPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewJagFileWizard extends Wizard implements INewWizard {
	
	private NewJagFileWizardPage		fNewFilePage;
	private IStructuredSelection	fSelection;
	
	public void addPages() {
		fNewFilePage = new NewJagFileWizardPage("JagWizardNewFileCreationPage", new StructuredSelection(IDE.computeSelectedResources(fSelection))); //$NON-NLS-1$
		fNewFilePage.setTitle(JaggeryWizardMessages.Jaggery_UI_Wizard_New_Heading); //$NON-NLS-1$
		fNewFilePage.setDescription(JaggeryWizardMessages.Jaggery_UI_Wizard_New_Description); //$NON-NLS-1$
		addPage(fNewFilePage);
	}

	@Override
	public boolean performFinish() {
		boolean performedOK = false;

		// no file extension specified so add default extension
		String fileName = fNewFilePage.getFileName();
		if (fileName.lastIndexOf('.') == -1) {
			String newFileName = fNewFilePage.addDefaultExtension(fileName);
			fNewFilePage.setFileName(newFileName);
		}

		// create a new empty file
		IFile file = fNewFilePage.createNewFile();

		// if there was problem with creating file, it will be null, so make
		// sure to check
		if (file != null) {
			// open the file in editor
			openEditor(file);

			// everything's fine
			performedOK = true;
		}
		
		return performedOK;
	}

	@Override
	public void init(IWorkbench aWorkbench, IStructuredSelection aSelection) {
		fSelection = aSelection;
		setWindowTitle(JaggeryWizardMessages.Jaggery_UI_Wizard_New_Title); 
		
	}
	
	private void openEditor(final IFile file) {
		if (file != null) {
			getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					try {
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						IDE.openEditor(page, file, true, false);
					}
					catch (PartInitException e) {
						// STP Logger.log(Logger.WARNING_DEBUG, e.getMessage(), e);
						JaggeryUIPlugin.log(e);
					}
				}
			});
		}
	}
	
	

}
