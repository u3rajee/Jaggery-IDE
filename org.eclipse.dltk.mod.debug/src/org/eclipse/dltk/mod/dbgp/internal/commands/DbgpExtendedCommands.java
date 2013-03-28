/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     eBay Inc - modification
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp.internal.commands;

import org.eclipse.dltk.mod.dbgp.IDbgpProperty;
import org.eclipse.dltk.mod.dbgp.commands.IDbgpExtendedCommands;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.mod.dbgp.internal.DbgpRequest;
import org.eclipse.dltk.mod.dbgp.internal.utils.DbgpXmlEntityParser;
import org.eclipse.dltk.mod.dbgp.internal.utils.DbgpXmlParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpExtendedCommands extends DbgpBaseCommands implements
		IDbgpExtendedCommands {
	static final String DEPTH_OPTION = "-d";

	private IDbgpProperty parseResponse(Element response) {
		if (DbgpXmlParser.parseSuccess(response)) {
			NodeList list = response
					.getElementsByTagName(DbgpXmlEntityParser.TAG_PROPERTY);
			return DbgpXmlEntityParser.parseProperty((Element) list.item(0));
		}
		return null;
	}

	public DbgpExtendedCommands(IDbgpCommunicator communicator)
			throws DbgpException {
		super(communicator);
	}

	public boolean makeBreak() throws DbgpException {
		return DbgpXmlParser
				.parseSuccess(communicate(createAsyncRequest(BREAK_COMMAND)));
	}

	public boolean configureStdin(int value) throws DbgpException {
		DbgpRequest request = createRequest(STDIN_COMMAND);
		request.addOption("-c", value); //$NON-NLS-1$
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public boolean sendStdin(String data) throws DbgpException {
		DbgpRequest request = createRequest(STDIN_COMMAND);
		request.setData(data);
		return DbgpXmlParser.parseSuccess(communicate(request));
	}

	public IDbgpProperty evaluate(String snippet) throws DbgpException {
		DbgpRequest request = createRequest(EVAL_COMMAND);
		request.setData(snippet);
		return parseResponse(communicate(request));
	}

	/**
	 * Ebay change, to support eval in different frame
	 * 
	 * @deprecated
	 */
	public IDbgpProperty evaluate(String snippet, int depth)
			throws DbgpException {
		DbgpRequest request = createRequest(EVAL_COMMAND);
		request.addOption(DEPTH_OPTION, depth);
		request.setData(snippet);
		return parseResponse(communicate(request));
	}

	public IDbgpProperty expression(String expression) throws DbgpException {
		DbgpRequest request = createRequest(EXPR_COMMAND);
		request.setData(expression);
		return parseResponse(communicate(request));
	}

	public IDbgpProperty execute(String code) throws DbgpException {
		DbgpRequest request = createRequest(EXEC_COMMAND);
		request.setData(code);
		return parseResponse(communicate(request));
	}
}
