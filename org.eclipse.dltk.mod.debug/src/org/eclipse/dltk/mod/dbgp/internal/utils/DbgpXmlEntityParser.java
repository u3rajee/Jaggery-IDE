/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.dbgp.internal.utils;

import java.net.URI;
import java.text.MessageFormat;

import org.eclipse.dltk.mod.dbgp.IDbgpProperty;
import org.eclipse.dltk.mod.dbgp.IDbgpSessionInfo;
import org.eclipse.dltk.mod.dbgp.IDbgpStatus;
import org.eclipse.dltk.mod.dbgp.breakpoints.IDbgpBreakpoint;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.mod.dbgp.exceptions.DbgpProtocolException;
import org.eclipse.dltk.mod.dbgp.internal.DbgpFeature;
import org.eclipse.dltk.mod.dbgp.internal.DbgpProperty;
import org.eclipse.dltk.mod.dbgp.internal.DbgpSessionInfo;
import org.eclipse.dltk.mod.dbgp.internal.DbgpStackLevel;
import org.eclipse.dltk.mod.dbgp.internal.DbgpStatus;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpCallBreakpoint;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpConditionalBreakpoint;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpExceptionBreakpoint;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpLineBreakpoint;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpReturnBreakpoint;
import org.eclipse.dltk.mod.dbgp.internal.breakpoints.DbgpWatchBreakpoint;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpXmlEntityParser extends DbgpXmlParser {
	private static final IDbgpProperty[] NO_CHILDREN = new IDbgpProperty[0];

	private static final String ENCODING_NONE = "none"; //$NON-NLS-1$
	private static final String ENCODING_BASE64 = "base64"; //$NON-NLS-1$

	public static final String TAG_PROPERTY = "property"; //$NON-NLS-1$

	protected DbgpXmlEntityParser() {

	}

	private static final String ATTR_LEVEL = "level"; //$NON-NLS-1$
	private static final String ATTR_CMDBEGIN = "cmdbegin"; //$NON-NLS-1$
	private static final String ATTR_CMDEND = "cmdend"; //$NON-NLS-1$
	private static final String ATTR_LINENO = "lineno"; //$NON-NLS-1$
	private static final String ATTR_FILENAME = "filename"; //$NON-NLS-1$
	private static final String ATTR_WHERE = "where"; //$NON-NLS-1$

	public static DbgpStackLevel parseStackLevel(Element element)
			throws DbgpException {
		int level = Integer.parseInt(element.getAttribute(ATTR_LEVEL));

		// TODO: understand type attribute
		// String type = element.getAttribute(ATTR_TYPE);

		String cmdBegin = element.getAttribute(ATTR_CMDBEGIN);
		String cmdEnd = element.getAttribute(ATTR_CMDEND);

		int lineBegin = -1;
		int lineEnd = -1;
		if (!"".equals(cmdBegin) && !"".equals(cmdEnd)) { //$NON-NLS-1$ //$NON-NLS-2$
			lineBegin = getPosition(cmdBegin);
			lineEnd = getPosition(cmdEnd);
		}

		int lineNumber = Integer.parseInt(element.getAttribute(ATTR_LINENO));

		final URI fileUri = parseURI(element.getAttribute(ATTR_FILENAME));

		final String where = element.getAttribute(ATTR_WHERE);

		return new DbgpStackLevel(fileUri, where, level, lineNumber, lineBegin,
				lineEnd);
	}

	private static final String FILE_SCHEME = "file:///"; //$NON-NLS-1$

	private static URI parseURI(String fileName) {
		/*
		 * ActiveState python debugger on windows sends URI as
		 * "file:///C|/path/to/file.py" we need to convert it.
		 */
		if (fileName.startsWith(FILE_SCHEME)) {
			final int pos = FILE_SCHEME.length();
			if (fileName.length() > pos + 3) {
				if (Character.isLetter(fileName.charAt(pos))
						&& fileName.charAt(pos + 1) == '|'
						&& fileName.charAt(pos + 2) == '/') {
					fileName = fileName.substring(0, pos + 1) + ':'
							+ fileName.substring(pos + 2);
				}
			}
		}
		return URI.create(fileName);
	}

	private static final String ATTR_FEATURE_NAME = "feature_name"; //$NON-NLS-1$
	private static final String ATTR_SUPPORTED = "supported"; //$NON-NLS-1$

	public static DbgpFeature parseFeature(Element element)
			throws DbgpProtocolException {
		String name = element.getAttribute(ATTR_FEATURE_NAME);
		boolean supported = makeBoolean(element.getAttribute(ATTR_SUPPORTED));
		String value = parseContent(element);
		return new DbgpFeature(supported, name, value);
	}

	private static final String ATTR_NAME = "name"; //$NON-NLS-1$
	private static final String ATTR_FULLNAME = "fullname"; //$NON-NLS-1$
	private static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	private static final String ATTR_CHILDREN = "children"; //$NON-NLS-1$
	private static final String ATTR_NUMCHILDREN = "numchildren"; //$NON-NLS-1$
	private static final String ATTR_CONSTANT = "constant"; //$NON-NLS-1$
	private static final String ATTR_KEY = "key"; //$NON-NLS-1$
	private static final String ATTR_PAGE = "page"; //$NON-NLS-1$
	private static final String ATTR_PAGE_SIZE = "pagesize"; //$NON-NLS-1$
	private static final String ATTR_ADDRESS = "address"; //$NON-NLS-1$

	public static IDbgpProperty parseProperty(Element property) {
		/*
		 * attributes: name, fullname, type, children, numchildren, constant,
		 * encoding, size, key, address
		 */

		// may exist as an attribute of the property or as child element
		final String name = getFromChildOrAttr(property, ATTR_NAME);
		final String fullName = getFromChildOrAttr(property, ATTR_FULLNAME);

		final String type = property.getAttribute(ATTR_TYPE);

		// hasChildren
		boolean hasChildren = false;
		if (property.hasAttribute(ATTR_CHILDREN)) {
			hasChildren = makeBoolean(property.getAttribute(ATTR_CHILDREN));
		}

		// Children count
		int childrenCount = -1;
		if (property.hasAttribute(ATTR_NUMCHILDREN)) {
			childrenCount = Integer.parseInt(property
					.getAttribute(ATTR_NUMCHILDREN));

			// fix bug 3329
			// if children count is zero, reset 'hasChildren' field as false.
			if (childrenCount == 0)
				hasChildren = false;
		}

		// Page
		int page = 0;
		if (property.hasAttribute(ATTR_PAGE)) {
			page = Integer.parseInt(property.getAttribute(ATTR_PAGE));
		}

		// Page Size
		int pagesize = -1;
		if (property.hasAttribute(ATTR_PAGE_SIZE)) {
			pagesize = Integer.parseInt(property.getAttribute(ATTR_PAGE_SIZE));
		}

		// Constant
		boolean constant = false;
		if (property.hasAttribute(ATTR_CONSTANT)) {
			constant = makeBoolean(property.getAttribute(ATTR_CONSTANT));
		}

		// Key
		String key = null;
		if (property.hasAttribute(ATTR_KEY)) {
			key = property.getAttribute(ATTR_KEY);
		}

		// memory address
		String address = null;
		if (property.hasAttribute(ATTR_ADDRESS)) {
			address = property.getAttribute(ATTR_ADDRESS);
		}

		// Value
		String value = ""; //$NON-NLS-1$

		NodeList list = property.getElementsByTagName("value"); //$NON-NLS-1$
		if (list.getLength() == 0) {
			value = getEncodedValue(property);
		} else {
			value = getEncodedValue((Element) list.item(0));
		}

		// Children
		IDbgpProperty[] availableChildren = NO_CHILDREN;
		if (hasChildren) {
			final NodeList children = property
					.getElementsByTagName(TAG_PROPERTY);
			final int length = children.getLength();
			if (length > 0) {
				availableChildren = new IDbgpProperty[length];
				for (int i = 0; i < length; ++i) {
					Element child = (Element) children.item(i);
					availableChildren[i] = parseProperty(child);
				}
			}
		}

		if (childrenCount < 0) {
			childrenCount = availableChildren.length;
		}

		return new DbgpProperty(name, fullName, type, value, childrenCount,
				hasChildren, constant, key, address, availableChildren, page,
				pagesize);
	}

	private static final String ATTR_REASON = "reason"; //$NON-NLS-1$
	private static final String ATTR_STATUS = "status"; //$NON-NLS-1$

	public static IDbgpStatus parseStatus(Element element)
			throws DbgpProtocolException {

		String status = element.getAttribute(ATTR_STATUS);
		String reason = element.getAttribute(ATTR_REASON);
		return DbgpStatus.parse(status, reason);
	}

	private static final String LINE_BREAKPOINT = "line"; //$NON-NLS-1$
	private static final String CALL_BREAKPOINT = "call"; //$NON-NLS-1$
	private static final String RETURN_BREAKPOINT = "return"; //$NON-NLS-1$
	private static final String EXCEPTION_BREAKPOINT = "exception"; //$NON-NLS-1$
	private static final String CONDITIONAL_BREAKPOINT = "conditional"; //$NON-NLS-1$
	private static final String WATCH_BREAKPOINT = "watch"; //$NON-NLS-1$

	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_STATE = "state"; //$NON-NLS-1$
	private static final String ATTR_HIT_COUNT = "hit_count"; //$NON-NLS-1$
	private static final String ATTR_HIT_VALUE = "hit_value"; //$NON-NLS-1$
	private static final String ATTR_HIT_CONDITION = "hit_condition"; //$NON-NLS-1$
	private static final String ATTR_LINE = "line"; //$NON-NLS-1$
	private static final String ATTR_FUNCTION = "function"; //$NON-NLS-1$
	private static final String ATTR_EXCEPTION = "exception"; //$NON-NLS-1$
	private static final String ATTR_EXPRESSION = "expression"; //$NON-NLS-1$

	public static IDbgpBreakpoint parseBreakpoint(Element element) {
		// ActiveState Tcl

		// ActiveState Python
		// <response xmlns="urn:debugger_protocol_v1" command="breakpoint_get"
		// transaction_id="1">
		// <breakpoint id="1"
		// type="line"
		// filename="c:\distrib\dbgp\test\test1.py"
		// lineno="8"
		// state="enabled"
		// temporary="0">
		// </breakpoint>
		// </response>

		String type = element.getAttribute(ATTR_TYPE);

		String id = element.getAttribute(ATTR_ID);
		boolean enabled = element.getAttribute(ATTR_STATE).equals("enabled"); //$NON-NLS-1$

		// not all dbgp implementations have these
		int hitCount = getIntAttribute(element, ATTR_HIT_COUNT, 0);
		int hitValue = getIntAttribute(element, ATTR_HIT_VALUE, 0);
		String hitCondition = getStringAttribute(element, ATTR_HIT_CONDITION);

		if (type.equals(LINE_BREAKPOINT)) {
			String fileName = element.getAttribute(ATTR_FILENAME);

			// ActiveState's dbgp implementation is slightly inconsistent
			String lineno = element.getAttribute(ATTR_LINENO);
			if ("".equals(lineno)) { //$NON-NLS-1$
				lineno = element.getAttribute(ATTR_LINE);
			}

			int lineNumber = Integer.parseInt(lineno);
			return new DbgpLineBreakpoint(id, enabled, hitValue, hitCount,
					hitCondition, fileName, lineNumber);
		} else if (type.equals(CALL_BREAKPOINT)) {
			String function = element.getAttribute(ATTR_FUNCTION);
			return new DbgpCallBreakpoint(id, enabled, hitValue, hitCount,
					hitCondition, function);
		} else if (type.equals(RETURN_BREAKPOINT)) {
			String function = element.getAttribute(ATTR_FUNCTION);
			return new DbgpReturnBreakpoint(id, enabled, hitValue, hitCount,
					hitCondition, function);
		} else if (type.equals(EXCEPTION_BREAKPOINT)) {
			String exception = element.getAttribute(ATTR_EXCEPTION);
			return new DbgpExceptionBreakpoint(id, enabled, hitValue, hitCount,
					hitCondition, exception);
		} else if (type.equals(CONDITIONAL_BREAKPOINT)) {
			String expression = element.getAttribute(ATTR_EXPRESSION);
			return new DbgpConditionalBreakpoint(id, enabled, hitValue,
					hitCount, hitCondition, expression);
		} else if (type.equals(WATCH_BREAKPOINT)) {
			String expression = element.getAttribute(ATTR_EXPRESSION);
			return new DbgpWatchBreakpoint(id, enabled, hitValue, hitCount,
					hitCondition, expression);
		}

		return null;
	}

	private static final String ATTR_APPID = "appid"; //$NON-NLS-1$
	private static final String ATTR_IDEKEY = "idekey"; //$NON-NLS-1$
	private static final String ATTR_SESSION = "session"; //$NON-NLS-1$
	private static final String ATTR_THREAD = "thread"; //$NON-NLS-1$
	private static final String ATTR_PARENT = "parent"; //$NON-NLS-1$
	private static final String ATTR_LANGUAGE = "language"; //$NON-NLS-1$

	public static IDbgpSessionInfo parseSession(Element element) {
		String appId = element.getAttribute(ATTR_APPID);
		String ideKey = element.getAttribute(ATTR_IDEKEY);
		String session = element.getAttribute(ATTR_SESSION);
		String threadId = element.getAttribute(ATTR_THREAD);
		String parentId = element.getAttribute(ATTR_PARENT);
		String language = element.getAttribute(ATTR_LANGUAGE);
		DbgpException error = DbgpXmlParser.checkError(element);
		return new DbgpSessionInfo(appId, ideKey, session, threadId, parentId,
				language, null, error);
	}

	protected static String getFromChildOrAttr(Element property, String name) {
		NodeList list = property.getElementsByTagName(name);

		if (list.getLength() == 0) {
			return property.getAttribute(name);
		}

		/*
		 * this may or may not need to be base64 decoded - need to see output
		 * from an ActiveState's python debugging session to determine. gotta
		 * love protocol changes that have made their way back into the
		 * published spec
		 */
		return getEncodedValue((Element) list.item(0));
	}

	private static final String ATTR_ENCODING = "encoding"; //$NON-NLS-1$

	protected static String getEncodedValue(Element element) {
		String encoding = ENCODING_NONE;
		if (element.hasAttribute(ATTR_ENCODING)) {
			encoding = element.getAttribute(ATTR_ENCODING);
		}

		if (ENCODING_NONE.equals(encoding)) {
			return parseContent(element);
		}

		if (ENCODING_BASE64.equals(encoding)) {
			return parseBase64Content(element);
		}

		throw new AssertionError(MessageFormat.format(
				Messages.DbgpXmlEntityParser_invalidEncoding,
				new Object[] { encoding }));
	}

}
