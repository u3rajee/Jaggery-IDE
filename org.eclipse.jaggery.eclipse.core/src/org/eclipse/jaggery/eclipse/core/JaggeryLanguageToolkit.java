package org.eclipse.jaggery.eclipse.core;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.mod.core.AbstractLanguageToolkit;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.environment.IFileHandle;

public class JaggeryLanguageToolkit extends AbstractLanguageToolkit{
	
	private static final String LANGUAGE_NAME = "Jaggery (JaggeryScript)";

	private static JaggeryLanguageToolkit s_instance = new JaggeryLanguageToolkit();

	public static final String JAGGERY_CONTENT_TYPE_ID = "org.eclipse.dtlk.mod.jaggeryContentType";
	
	

	public static IDLTKLanguageToolkit getDefault() {
		return s_instance;
	}

	@Override
	public String getLanguageContentType() {
		// TODO Auto-generated method stub
		return JAGGERY_CONTENT_TYPE_ID;
	}

	@Override
	public String getNatureId() {
		// TODO Auto-generated method stub
		return JaggeryNature.NATURE_ID;
	}

	@Override
	public String getLanguageName() {
		// TODO Auto-generated method stub
		return LANGUAGE_NAME;
	}
	
	
	@Override
	public boolean languageSupportZIPBuildpath() {
		return true;
	}
	
	
	public static boolean isJaggeryContentType(IFile file) {
		IContentType type1 = getJaggeryContentType();
		IContentType[] type = Platform.getContentTypeManager().findContentTypesFor(file.getName());

		boolean isKind = false;
		for (IContentType iContentType : type) {
			if(iContentType.isKindOf(type1)){
				isKind = true;
				break;
			}
		}

		return type != null && isKind;
	}
	
	// add by patrick
	public static IContentType getJaggeryContentType() {
		return Platform.getContentTypeManager().getContentType(JAGGERY_CONTENT_TYPE_ID);
	}

	@Override
	public boolean canValidateContent(File file) {
		IContentType contentType =getJaggeryContentType();
		return contentType.isAssociatedWith(file.getName());
	}

	@Override
	public boolean canValidateContent(IFileHandle file) {
		IContentType contentType = getJaggeryContentType();
		return contentType.isAssociatedWith(file.getName());
	}

	@Override
	public boolean canValidateContent(IResource resource) {
		if (IResource.FILE == resource.getType()) {
			return isJaggeryContentType((IFile) resource);
		}
		return false;
	}
	// end add

}
