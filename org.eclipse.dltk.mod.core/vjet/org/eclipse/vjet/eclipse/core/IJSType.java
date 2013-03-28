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

import org.eclipse.dltk.mod.core.IProjectFragment;
import org.eclipse.dltk.mod.core.IType;
import org.eclipse.dltk.mod.core.ModelException;
import org.eclipse.dltk.mod.internal.core.IJSInitializer;

public interface IJSType extends IType {
	/**
	 * Returns the initializer with the specified position relative to the order
	 * they are defined in the source. Numbering starts at 1 (thus the first
	 * occurrence is occurrence 1, not occurrence 0). This is a handle-only
	 * method. The initializer may or may not be present.
	 * 
	 * @param occurrenceCount
	 *            the specified position
	 * @return the initializer with the specified position relative to the order
	 *         they are defined in the source
	 */
	IJSInitializer getInitializer(int occurrenceCount);

	/**
	 * Returns the initializers declared by this type. For binary types this is
	 * an empty collection. If this is a source type, the results are listed in
	 * the order in which they appear in the source.
	 * 
	 * @exception ModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the initializers declared by this type
	 */
	IJSInitializer[] getInitializers() throws ModelException;

	/**
	 * Returns the method with the specified name and parameter types in this
	 * type (for example, <code>"foo", {"I", "QString;"}</code>). To get the
	 * handle for a constructor, the name specified must be the simple name of
	 * the enclosing type. This is a handle-only method. The method may or may
	 * not be present.
	 * <p>
	 * The type signatures may be either unresolved (for source types) or
	 * resolved (for binary types), and either basic (for basic types) or rich
	 * (for parameterized types). See {@link Signature} for details.
	 * </p>
	 * 
	 * @param name
	 *            the given name
	 * @param parameterTypeSignatures
	 *            the given parameter types
	 * @return the method with the specified name and parameter types in this
	 *         type
	 */
	IJSMethod getMethod(String name, String[] parameterTypeSignatures);

	public IProjectFragment getProjectFragment();

	public String[] getSuperInterfaceNames() throws ModelException;

	/**
	 * Returns whether this type represents a class.
	 * <p>
	 * Note that a class can neither be an interface, an enumeration class, nor
	 * an annotation type.
	 * </p>
	 * 
	 * @exception JavaModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return true if this type represents a class, false otherwise
	 */
	boolean isClass() throws ModelException;

	/**
	 * Returns whether this type represents an interface.
	 * <p>
	 * Note that an interface can also be an annotation type, but it can neither
	 * be a class nor an enumeration class.
	 * </p>
	 * 
	 * @exception ModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return true if this type represents an interface, false otherwise
	 */
	boolean isInterface() throws ModelException;

	/**
	 * Returns whether this type represents an enumeration class.
	 * <p>
	 * Note that an enumeration class can neither be a class, an interface, nor
	 * an annotation type.
	 * </p>
	 * 
	 * @exception ModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return true if this type represents an enumeration class, false
	 *         otherwise
	 */
	boolean isEnum() throws ModelException;
}
