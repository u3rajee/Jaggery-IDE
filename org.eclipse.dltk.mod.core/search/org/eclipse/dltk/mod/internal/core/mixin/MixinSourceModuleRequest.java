/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core.mixin;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.mod.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.mod.core.IScriptProject;
import org.eclipse.dltk.mod.core.ISourceModule;
import org.eclipse.dltk.mod.core.search.index.Index;
import org.eclipse.dltk.mod.core.search.indexing.ReadWriteMonitor;

public class MixinSourceModuleRequest extends MixinIndexRequest {

	protected final ISourceModule module;
	protected final IDLTKLanguageToolkit toolkit;

	public MixinSourceModuleRequest(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		this.module = module;
		this.toolkit = toolkit;
	}

	protected String getName() {
		return module.getElementName();
	}

	protected void run() throws CoreException, IOException {
		final IScriptProject project = module.getScriptProject();
		final Index index = getProjectMixinIndex(project);
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			indexSourceModule(index, toolkit, module, project.getPath());
		} finally {
			imon.exitWrite();
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MixinSourceModuleRequest other = (MixinSourceModuleRequest) obj;
		if (module == null) {
			if (other.module != null)
				return false;
		} else if (!module.equals(other.module))
			return false;
		return true;
	}
}
