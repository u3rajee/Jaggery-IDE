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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.IAccessRule;
import org.eclipse.dltk.mod.core.IBuildpathAttribute;
import org.eclipse.dltk.mod.core.IBuildpathEntry;
import org.eclipse.dltk.mod.internal.core.util.Messages;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Internal model element to represent a user library and code to serialize /
 * deserialize.
 */
public class UserLibrary {

	private static final String CURRENT_VERSION = "1"; //$NON-NLS-1$

	private static final String TAG_VERSION = "version"; //$NON-NLS-1$
	private static final String TAG_USERLIBRARY = "userlibrary"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_ARCHIVE = "archive"; //$NON-NLS-1$
	private static final String TAG_SYSTEMLIBRARY = "systemlibrary"; //$NON-NLS-1$

	private boolean isSystemLibrary;
	private IBuildpathEntry[] entries;

	public UserLibrary(IBuildpathEntry[] entries, boolean isSystemLibrary) {
		Assert.isNotNull(entries);
		this.entries = entries;
		this.isSystemLibrary = isSystemLibrary;
	}

	public IBuildpathEntry[] getEntries() {
		return this.entries;
	}

	public boolean isSystemLibrary() {
		return this.isSystemLibrary;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == getClass()) {
			UserLibrary other = (UserLibrary) obj;
			if (this.entries.length == other.entries.length
					&& this.isSystemLibrary == other.isSystemLibrary) {
				for (int i = 0; i < this.entries.length; i++) {
					if (!this.entries[i].equals(other.entries[i])) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int hashCode = 0;
		if (this.isSystemLibrary) {
			hashCode++;
		}
		for (int i = 0; i < this.entries.length; i++) {
			hashCode = hashCode * 17 + this.entries.hashCode();
		}
		return hashCode;
	}

	public static String serialize(IBuildpathEntry[] entries,
			boolean isSystemLibrary) throws IOException {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(s, "UTF8"); //$NON-NLS-1$
		XMLWriter xmlWriter = new XMLWriter(writer, null/*
														 * use the workspace
														 * line delimiter
														 */, true/*
																 * print XML
																 * version
																 */);

		HashMap library = new HashMap();
		library.put(TAG_VERSION, String.valueOf(CURRENT_VERSION));
		library.put(TAG_SYSTEMLIBRARY, String.valueOf(isSystemLibrary));
		xmlWriter.printTag(TAG_USERLIBRARY, library, true, true, false);

		for (int i = 0, length = entries.length; i < length; ++i) {
			BuildpathEntry cpEntry = (BuildpathEntry) entries[i];

			HashMap archive = new HashMap();
			archive.put(TAG_PATH, cpEntry.getPath().toString());

			boolean hasExtraAttributes = cpEntry.extraAttributes != null
					&& cpEntry.extraAttributes.length != 0;
			boolean hasRestrictions = cpEntry.getAccessRuleSet() != null; // access
			// rule
			// set
			// is
			// null
			// if
			// no
			// access
			// rules
			xmlWriter.printTag(TAG_ARCHIVE, archive, true, true,
					!(hasExtraAttributes || hasRestrictions));

			// write extra attributes if necessary
			if (hasExtraAttributes) {
				cpEntry.encodeExtraAttributes(xmlWriter, true, true);
			}

			// write extra attributes and restriction if necessary
			if (hasRestrictions) {
				cpEntry.encodeAccessRules(xmlWriter, true, true);
			}

			// write archive end tag if necessary
			if (hasExtraAttributes || hasRestrictions) {
				xmlWriter.endTag(TAG_ARCHIVE, true/* insert tab */, true/*
																	 * insert
																	 * new line
																	 */);
			}
		}
		xmlWriter.endTag(TAG_USERLIBRARY, true/* insert tab */, true/*
																 * insert new
																 * line
																 */);
		writer.flush();
		writer.close();
		return s.toString("UTF8");//$NON-NLS-1$
	}

	public static UserLibrary createFromString(Reader reader)
			throws IOException {
		Element cpElement;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			cpElement = parser.parse(new InputSource(reader))
					.getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(Messages.file_badFormat);
		} catch (ParserConfigurationException e) {
			throw new IOException(Messages.file_badFormat);
		} finally {
			reader.close();
		}

		if (!cpElement.getNodeName().equalsIgnoreCase(TAG_USERLIBRARY)) {
			throw new IOException(Messages.file_badFormat);
		}
		// String version= cpElement.getAttribute(TAG_VERSION);
		// in case we update the format: add code to read older versions

		boolean isSystem = Boolean.valueOf(
				cpElement.getAttribute(TAG_SYSTEMLIBRARY)).booleanValue();

		NodeList list = cpElement.getChildNodes();
		int length = list.getLength();

		ArrayList res = new ArrayList(length);
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getNodeName().equals(TAG_ARCHIVE)) {
					String path = element.getAttribute(TAG_PATH);
					NodeList children = element.getElementsByTagName("*"); //$NON-NLS-1$
					boolean[] foundChildren = new boolean[children.getLength()];
					NodeList attributeList = BuildpathEntry.getChildAttributes(
							BuildpathEntry.TAG_ATTRIBUTES, children,
							foundChildren);
					IBuildpathAttribute[] extraAttributes = BuildpathEntry
							.decodeExtraAttributes(attributeList);
					attributeList = BuildpathEntry.getChildAttributes(
							BuildpathEntry.TAG_ACCESS_RULES, children,
							foundChildren);
					IAccessRule[] accessRules = BuildpathEntry
							.decodeAccessRules(attributeList);
					IBuildpathEntry entry = DLTKCore.newLibraryEntry(Path
							.fromPortableString(path), accessRules,
							extraAttributes, false, true);
					res.add(entry);
				}
			}
		}

		IBuildpathEntry[] entries = (IBuildpathEntry[]) res
				.toArray(new IBuildpathEntry[res.size()]);

		return new UserLibrary(entries, isSystem);
	}

	public String toString() {
		if (this.entries == null)
			return "null"; //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer();
		int length = this.entries.length;
		for (int i = 0; i < length; i++) {
			buffer.append(this.entries[i].toString() + '\n');
		}
		return buffer.toString();
	}
}
