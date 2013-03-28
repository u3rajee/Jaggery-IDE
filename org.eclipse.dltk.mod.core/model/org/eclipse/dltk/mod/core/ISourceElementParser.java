/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.mod.core;

import org.eclipse.dltk.mod.compiler.ISourceElementRequestor;
import org.eclipse.dltk.mod.compiler.problem.IProblemReporter;
import org.eclipse.dltk.mod.core.ISourceModuleInfoCache.ISourceModuleInfo;

public interface ISourceElementParser {
	/**
	 * Parses contents of the module with ast creation. Also it is recommended to
	 * use SourceParserUtils to put delcaration into cache, and retrieve it from
	 * it.
	 */
	void parseSourceModule(org.eclipse.dltk.mod.compiler.env.ISourceModule module,
			ISourceModuleInfo mifo);

	void setRequestor(ISourceElementRequestor requestor);

	void setReporter(IProblemReporter reporter);
}
