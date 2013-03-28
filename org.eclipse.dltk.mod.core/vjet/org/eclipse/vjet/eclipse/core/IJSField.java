/*******************************************************************************
 * Copyright (c) 2000, 2012 eBay Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc - initial API and implementation
 *******************************************************************************/
package org.eclipse.vjet.eclipse.core;

import org.eclipse.dltk.mod.core.IField;
import org.eclipse.dltk.mod.core.ModelException;

public interface IJSField extends IField {

	public Object getConstant() throws ModelException;
}
