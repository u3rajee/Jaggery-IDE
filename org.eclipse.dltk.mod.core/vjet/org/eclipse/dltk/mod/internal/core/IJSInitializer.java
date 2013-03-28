/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.core;

import org.eclipse.dltk.mod.core.IMember;

/**
 * Represents a stand-alone instance or class (static) initializer in a type.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IJSInitializer extends IMember {
	/**
	 * Constant representing a stand-alone instance or class initializer.
	 */
	int ELEMENT_TYPE = 11;
}
