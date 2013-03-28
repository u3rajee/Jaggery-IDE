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
package org.eclipse.dltk.mod.core;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;

import org.osgi.framework.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.URL;

/**
 * Utility functions useful for interacting with internal scripts that may need
 * to be excuted by the plugin itself to obtain information about the underlying
 * interpreter, etc.
 */
public abstract class ScriptUtils {

	/**
	 * Store an internal executable script inside a plugin's metadata directory.
	 * 
	 * <p>
	 * This is useful for storing internal scripts in a consistent location so
	 * they may be executed once the plugin is installed. For instance, if your
	 * source structure looks like the following:
	 * </p>
	 * 
	 * <pre>
	 * org.eclipse.dltk.mod.core
	 *   src/
	 *   scripts/
	 *     test.script
	 * </pre>
	 * 
	 * <p>
	 * you would invoke this method using the following invocation:
	 * </p>
	 * 
	 * <code>storeToMetaData(DLTKCore.getDefault(), "test.script", "scripts/test.script")</code>
	 * 
	 * @param plugin
	 *            plugin that owns the script to be saved
	 * @param name
	 *            name script will be saved as
	 * @param path
	 *            path to script (relative to project Bundle)
	 * 
	 * @return File object representing the path to the stored script
	 * 
	 * @throws IOException
	 */
	public static final File storeToMetadata(Plugin plugin, String name,
			String path) throws IOException {

		Bundle bundle = plugin.getBundle();

		File file = plugin.getStateLocation().append(name).toFile();
		storeFile(file, FileLocator.resolve(bundle.getEntry(path)));

		return file;
	}

	private static void storeFile(File dest, URL url) throws IOException {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new BufferedInputStream(url.openStream());
			output = new BufferedOutputStream(new FileOutputStream(dest));

			// Simple copy
			int ch = -1;
			while ((ch = input.read()) != -1) {
				output.write(ch);
			}
		} finally {
			if (input != null) {
				input.close();
			}

			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Tests if the nature of the specified input object is the same as
	 * specified value. If the input object does not implement neither
	 * {@link IScriptLanguageDependent} nor {@link IScriptNatureDependent} then
	 * <code>defaultValue<code> is returned.
	 * 
	 * @param natureId
	 * @param input
	 * @param defaultValue
	 * @return
	 */
	public static boolean checkNature(String natureId, Object input,
			boolean defaultValue) {
		if (input instanceof IScriptNatureDependent) {
			return natureId.equals(((IScriptNatureDependent) input)
					.getNatureId());
		} else if (input instanceof IScriptLanguageDependent) {
			return natureId.equals((((IScriptLanguageDependent) input)
					.getLanguageToolkit().getNatureId()));
		} else {
			return defaultValue;
		}
	}
}
