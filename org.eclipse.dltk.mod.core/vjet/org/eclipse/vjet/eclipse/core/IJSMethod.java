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

import org.eclipse.dltk.mod.core.IMethod;
import org.eclipse.dltk.mod.core.ModelException;

public interface IJSMethod extends IMethod {
	/**
	 * Returns the number of parameters of this method. This is a handle-only
	 * method.
	 * 
	 * @return the number of parameters of this method
	 */
	int getNumberOfParameters();

	/**
	 * Returns the type signatures for the parameters of this method. Returns an
	 * empty array if this method has no parameters. This is a handle-only
	 * method.
	 * <p>
	 * For example, a source method declared as
	 * <code>public void foo(String text, int length)</code> would return the
	 * array <code>{"QString;","I"}</code>.
	 * </p>
	 * <p>
	 * The type signatures may be either unresolved (for source types) or
	 * resolved (for binary types), and either basic (for basic types) or rich
	 * (for parameterized types). See {@link Signature} for details.
	 * </p>
	 * 
	 * @return the type signatures for the parameters of this method, an empty
	 *         array if this method has no parameters
	 * @see Signature
	 */
	String[] getParameterTypes();

	/**
	 * Returns the type signature of the return value of this method. For
	 * constructors, this returns the signature for void.
	 * <p>
	 * For example, a source method declared as
	 * <code>public String getName()</code> would return <code>"QString;"</code>
	 * .
	 * </p>
	 * <p>
	 * The type signature may be either unresolved (for source types) or
	 * resolved (for binary types), and either basic (for basic types) or rich
	 * (for parameterized types). See {@link Signature} for details.
	 * </p>
	 * 
	 * @exception JavaModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the type signature of the return value of this method, void for
	 *         constructors
	 * @see Signature
	 */
	String getReturnType() throws ModelException;
}
