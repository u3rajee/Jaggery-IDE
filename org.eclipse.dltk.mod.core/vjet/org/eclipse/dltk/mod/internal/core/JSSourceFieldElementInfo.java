/*******************************************************************************
 * Copyright (c) 2012 eBay Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     eBay Inc. - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.dltk.mod.internal.core;

/**
 * @author MPeleshchyshyn
 * 
 */
public class JSSourceFieldElementInfo extends SourceFieldElementInfo implements
		IJSMemberElementInfo {

	protected String m_initializationSource;

	protected String m_type;

	public String getInitializationSource() {
		return m_initializationSource;
	}

	public String getType() {
		return m_type;
	}

	@Override
	public void setFlags(int flags) {
		super.setFlags(flags);
	}

	public void setInitializationSource(String source) {
		m_initializationSource = source;
	}

	@Override
	public void setNameSourceEnd(int end) {
		super.setNameSourceEnd(end);
	}

	@Override
	public void setNameSourceStart(int start) {
		super.setNameSourceStart(start);
	}

	@Override
	public void setSourceRangeEnd(int end) {
		super.setSourceRangeEnd(end);
	}

	@Override
	public void setSourceRangeStart(int start) {
		super.setSourceRangeStart(start);
	}

	public void setType(String type) {
		this.m_type = type;
	}
}
