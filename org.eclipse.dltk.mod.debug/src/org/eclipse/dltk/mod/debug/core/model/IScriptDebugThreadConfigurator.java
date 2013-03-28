/*******************************************************************************
 * Copyright (c) 2008, 2012 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.mod.debug.core.model;

import org.eclipse.dltk.mod.internal.debug.core.model.ScriptThread;
import org.eclipse.dltk.mod.internal.debug.core.model.operations.DbgpDebugger;

/**
 * This class called to configure advanced thread parameters. It could be
 * registered from debugger runner to ScriptDebugTarget. One instance per
 * target.
 */
public interface IScriptDebugThreadConfigurator {
	void configureThread(DbgpDebugger engine, ScriptThread scriptThread);
}
