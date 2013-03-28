/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.launching;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.mod.core.DLTKCore;
import org.eclipse.dltk.mod.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.mod.core.environment.IDeployment;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.launching.EnvironmentVariable;
import org.eclipse.dltk.mod.launching.IInterpreterInstall;
import org.eclipse.dltk.mod.launching.IInterpreterInstallType;
import org.eclipse.dltk.mod.launching.LaunchingMessages;
import org.eclipse.dltk.mod.launching.LibraryLocation;
import org.eclipse.dltk.mod.launching.ScriptRuntime;

/**
 * Abstract implementation of a interpreter install type. Subclasses should
 * implement
 * <ul>
 * <li><code>IInterpreterInstall doCreateInterpreterInstall(String id)</code></li>
 * <li><code>String getName()</code></li>
 * <li><code>IStatus validateInstallLocation(File installLocation)</code></li>
 * <li><code>String getLanguageId()</code></li>
 * </ul>
 * <p>
 * Clients implementing Interpreter install types should subclass this class.
 * </p>
 */
public abstract class AbstractInterpreterInstallType implements
		IInterpreterInstallType, IExecutableExtension {
	public interface ILookupRunnable {
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException;
	};

	private static final int NOT_WORK_COUNT = -2;

	private static final String DLTK_TOTAL_WORK_START = "%DLTK_TOTAL_WORK_START%:"; //$NON-NLS-1$
	private static final String DLTK_TOTAL_WORK_END = "%DLTK_TOTAL_WORK_END%"; //$NON-NLS-1$
	private static final String DLTK_TOTAL_WORK_INC = "%DLTK_TOTAL_WORK_INCREMENT%"; //$NON-NLS-1$

	public static final String DLTK_PATH_PREFIX = "DLTK:"; //$NON-NLS-1$

	private List fInterpreters;

	private String fId;

	private static HashMap fCachedLocations = new HashMap();

	protected AbstractInterpreterInstallType() {
		fInterpreters = new ArrayList();
	}

	public IInterpreterInstall[] getInterpreterInstalls() {
		return (IInterpreterInstall[]) fInterpreters
				.toArray(new IInterpreterInstall[fInterpreters.size()]);
	}

	public void disposeInterpreterInstall(String id) {
		Iterator it = fInterpreters.iterator();
		while (it.hasNext()) {
			IInterpreterInstall install = (IInterpreterInstall) it.next();
			if (install.getId().equals(id)) {
				it.remove();
				ScriptRuntime.fireInterpreterRemoved(install);
				return;
			}
		}
	}

	public IInterpreterInstall findInterpreterInstall(String id) {
		Iterator it = fInterpreters.iterator();
		while (it.hasNext()) {
			IInterpreterInstall install = (IInterpreterInstall) it.next();
			if (install.getId().equals(id)) {
				return install;
			}
		}

		return null;
	}

	public IInterpreterInstall createInterpreterInstall(String id)
			throws IllegalArgumentException {
		if (findInterpreterInstall(id) != null) {
			String format = LaunchingMessages.InterpreterInstallType_duplicateInterpreter;
			throw new IllegalArgumentException(MessageFormat.format(format,
					new String[] { id }));
		}

		IInterpreterInstall install = doCreateInterpreterInstall(id);
		fInterpreters.add(install);
		return install;
	}

	/**
	 * Subclasses should return a new instance of the appropriate
	 * <code>IInterpreterInstall</code> subclass from this method.
	 * 
	 * @param id
	 *            The Interpreter's id. The <code>IInterpreterInstall</code>
	 *            instance that is created must return <code>id</code> from its
	 *            <code>getId()</code> method. Must not be <code>null</code>.
	 * @return the newly created IInterpreterInstall instance. Must not return
	 *         <code>null</code>.
	 */
	protected abstract IInterpreterInstall doCreateInterpreterInstall(String id);

	/**
	 * Initializes the id parameter from the "id" attribute in the configuration
	 * markup. Subclasses should not override this method.
	 * 
	 * @param config
	 *            the configuration element used to trigger this execution. It
	 *            can be queried by the executable extension for specific
	 *            configuration properties
	 * @param propertyName
	 *            the name of an attribute of the configuration element used on
	 *            the <code>createExecutableExtension(String)</code> call. This
	 *            argument can be used in the cases where a single configuration
	 *            element is used to define multiple executable extensions.
	 * @param data
	 *            adapter data in the form of a <code>String</code>, a
	 *            <code>Hashtable</code>, or <code>null</code>.
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		fId = config.getAttribute("id"); //$NON-NLS-1$
	}

	public String getId() {
		return fId;
	}

	public IInterpreterInstall findInterpreterInstallByName(String name) {
		Iterator it = fInterpreters.iterator();
		while (it.hasNext()) {
			IInterpreterInstall install = (IInterpreterInstall) it.next();
			if (install.getName().equals(name)) {
				return install;
			}
		}

		return null;
	}

	protected static void storeFile(File dest, URL url) throws IOException {
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

	protected String[] extractEnvironment(IExecutionEnvironment exeEnv,
			EnvironmentVariable[] variables) {
		Map env = exeEnv.getEnvironmentVariables(false);

		filterEnvironment(env);

		List list = new ArrayList();

		EnvironmentVariable[] vars = EnvironmentResolver
				.resolve(env, variables);

		// Overwrite from variables with updates values.
		if (variables != null) {
			for (int i = 0; i < vars.length; i++) {
				env.put(vars[i].getName(), vars[i].getValue());
			}
		}

		Iterator it = env.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			list.add(entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$
		}

		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * filter out any undesirable entries from the system environment
	 * 
	 * <p>
	 * default implementation does nothing. subclasses are free to override.
	 * </p>
	 * 
	 * @param environment
	 *            system environment
	 */
	protected void filterEnvironment(Map environment) {
		// Nothing to do
	}

	/**
	 * Process should write one line into console with format 'path1 path2
	 * path3'
	 * 
	 * @param monitor
	 * @param process
	 * @return
	 */
	protected String[] readPathsFromProcess(final IProgressMonitor monitor,
			final Process process) {
		// DLTKLaunchingPlugin.log(new Status(IStatus.INFO,
		// DLTKLaunchingPlugin.PLUGIN_ID, IStatus.INFO,
		// "Start reading discovery script library paths", null));
		final BufferedReader dataIn = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		final List result = new ArrayList();

		// final Object lock = new Object();

		Thread tReading = new Thread(new Runnable() {
			public void run() {
				boolean workReceived = false;
				try {
					while (true) {
						if (monitor != null && monitor.isCanceled()) {
							monitor.worked(1);
							process.destroy();
							break;
						}
						String line = dataIn.readLine();
						if (line != null && monitor != null && !workReceived) {
							int work = extractWorkFromLine(line);
							if (work != NOT_WORK_COUNT) {
								monitor
										.beginTask(
												LaunchingMessages.AbstractInterpreterInstallType_fetchingInterpreterLibraryLocations,
												work);
								// monitor.subTask("Featching interpeter library
								// locations");
								workReceived = true;
							}
						}
						if (line != null && monitor != null
								&& detectWorkInc(line)) {
							monitor.worked(1);
						}
						if (line != null) {
							result.add(line);
						} else {
							break;
						}
					}

				} catch (IOException e) {
					DLTKLaunchingPlugin
							.log(new Status(
									IStatus.INFO,
									DLTKLaunchingPlugin.PLUGIN_ID,
									IStatus.INFO,
									MessageFormat
											.format(
													LaunchingMessages.AbstractInterpreterInstallType_failedToReadFromDiscoverScriptOutputStream,
													new Object[] { e
															.getMessage() }), e));
				} finally {
					if (monitor != null) {
						if (!workReceived) {
							monitor
									.beginTask(
											LaunchingMessages.AbstractInterpreterInstallType_fetchingInterpreterLibraryLocations,
											1);
						}
						monitor.done();
					}
				}
			}
		});
		tReading.start();
		try {
			tReading.join(10000);
		} catch (InterruptedException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return (String[]) result.toArray(new String[result.size()]);
	}

	private boolean detectWorkInc(String line) {
		return line.indexOf(DLTK_TOTAL_WORK_INC) != -1;

	}

	/**
	 * Extract work from specified line.
	 */
	private int extractWorkFromLine(String line) {
		int pos1 = line.indexOf(DLTK_TOTAL_WORK_START);
		int pos2 = line.indexOf(DLTK_TOTAL_WORK_END);
		if (pos1 != -1 && pos2 != -1) {
			String totalWork = line.substring(pos1
					+ DLTK_TOTAL_WORK_START.length(), pos2);
			int intValue = new Integer(totalWork).intValue();
			if (intValue == -1) {
				return IProgressMonitor.UNKNOWN;
			}
			return intValue;
		}
		return NOT_WORK_COUNT;
	}

	public static LibraryLocation[] correctLocations(final List locs) {
		return correctLocations(locs, null);
	}

	public static LibraryLocation[] correctLocations(final List locs,
			IProgressMonitor monitor) {
		List resolvedLocs = new ArrayList();
		if (monitor != null) {
			monitor
					.beginTask(
							LaunchingMessages.AbstractInterpreterInstallType_correctingLocations,
							locs.size());
		}
		for (Iterator iter = locs.iterator(); iter.hasNext();) {
			LibraryLocation n = (LibraryLocation) iter.next();
			// String res;
			// try {
			// File f = l.getLibraryPath().toFile();
			// if (f != null)
			// res = f.getCanonicalPath();
			// else
			// continue;
			// } catch (IOException e) {
			// continue;
			// }
			// LibraryLocation n = new LibraryLocation(new Path(res));
			if (!resolvedLocs.contains(n))
				resolvedLocs.add(n);
			if (monitor != null) {
				monitor.worked(1);
			}
		}

		LibraryLocation[] libs = (LibraryLocation[]) resolvedLocs
				.toArray(new LibraryLocation[resolvedLocs.size()]);
		if (monitor != null) {
			monitor.done();
		}
		return libs;
	}

	protected void fillLocationsExceptOne(IEnvironment env, final List locs,
			String[] paths, IPath path) {
		String sPath = path.toOSString();
		for (int i = 0; i < paths.length; i++) {
			if (!paths[i].equals(sPath)) {
				IFileHandle f = env.getFile(new Path(paths[i]));
				if (f.exists()) {
					LibraryLocation l = new LibraryLocation(
							EnvironmentPathUtils.getFullPath(env, f.getPath()));
					if (!locs.contains(l)) {
						locs.add(l);
					}
				}
			}
		}
	}

	/**
	 * run the interpreter library lookup in a
	 * <code>ProgressMonitorDialog</code>
	 */
	protected void runLibraryLookup(final ILookupRunnable runnable,
			IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {

		/*
		 * ProgressMonitorDialog progress = new ProgressMonitorDialog(null);
		 * Display current = Display.getCurrent(); if (current != null) { try {
		 * progress.run(true, false, runnable); } catch (SWTException ex) {
		 * runnable.run(new NullProgressMonitor()); } } else { runnable.run(new
		 * NullProgressMonitor()); }
		 */
		runnable.run(monitor);
	}

	protected abstract String[] getPossibleInterpreterNames();

	protected abstract String getPluginId();

	protected abstract ILog getLog();

	protected abstract IPath createPathFile(IDeployment deployment)
			throws IOException;

	protected String[] buildCommandLine(IFileHandle installLocation,
			IFileHandle pathFile) {
		String interpreterPath = installLocation.getCanonicalPath();
		String scriptPath = pathFile.getCanonicalPath();
		return new String[] { interpreterPath, scriptPath };
	}

	protected String getBuildPathDelimeter() {
		return " "; //$NON-NLS-1$
	}

	protected String[] parsePaths(String result) {
		String res = result;
		if (res.startsWith(DLTK_PATH_PREFIX)) {
			res = res.substring(DLTK_PATH_PREFIX.length());
		}
		String[] paths = res.split(getBuildPathDelimeter());
		List filtered = new ArrayList();
		for (int i = 0; i < paths.length; ++i) {
			if (!paths[i].equals(".")) { //$NON-NLS-1$
				filtered.add(paths[i].trim());
			}
		}

		return (String[]) filtered.toArray(new String[filtered.size()]);

	}

	/**
	 * Then multiple lines of output are provided, we parse only paths started
	 * from "DLTK:" sequence.
	 * 
	 * @param result
	 * @return
	 */
	protected String[] parsePaths(String[] result) {
		List filtered = new ArrayList();
		for (int k = 0; k < result.length; ++k) {
			String res = result[k];
			if (res.startsWith(DLTK_PATH_PREFIX)) {
				res = res.substring(DLTK_PATH_PREFIX.length());

				String[] paths = parsePaths(res);
				for (int i = 0; i < paths.length; ++i) {
					if (!paths[i].equals(".")) { //$NON-NLS-1$
						filtered.add(paths[i].trim());
					}
				}
			}
		}
		return (String[]) filtered.toArray(new String[filtered.size()]);
	}

	public IStatus validateInstallLocation(IFileHandle installLocation) {
		if (!installLocation.exists() || !installLocation.isFile()) {
			return createStatus(IStatus.ERROR,
					InterpreterMessages.errNonExistentOrInvalidInstallLocation,
					null);
		}
		return validatePossiblyName(installLocation);
	}

	public IStatus validatePossiblyName(IFileHandle installLocation) {
		String possibleNames[] = getPossibleInterpreterNames();

		boolean matchFound = false;
		final String name = installLocation.getName();
		IPath nPath = new Path(name);

		IExecutionEnvironment execEnv = (IExecutionEnvironment) installLocation
				.getEnvironment().getAdapter(IExecutionEnvironment.class);

		if (execEnv != null) {
			for (int i = 0; i < possibleNames.length; ++i) {
				final String possibleName = possibleNames[i].toLowerCase();
				if (execEnv.isValidExecutableAndEquals(possibleName, nPath)) {
					matchFound = true;
					break;
				}
			}
		}
		if (matchFound) {
			return createStatus(IStatus.OK, "", null); //$NON-NLS-1$
		} else {
			return createStatus(IStatus.ERROR,
					InterpreterMessages.errNoInterpreterExecutablesFound, null);
		}
	}

	protected String retrivePaths(IExecutionEnvironment exeEnv,
			final IFileHandle installLocation, final List locations,
			IProgressMonitor monitor, IFileHandle locator,
			EnvironmentVariable[] variables) {
		Process process = null;
		try {
			if (monitor != null) {
				// monitor.beginTask(InterpreterMessages.statusFetchingLibs, 1);
				if (monitor.isCanceled()) {
					return null;
				}
			}
			String[] cmdLine;
			String[] env = extractEnvironment(exeEnv, variables);

			cmdLine = buildCommandLine(installLocation, locator);
			try {
				if (DLTKLaunchingPlugin.TRACE_EXECUTION) {
					traceExecution(
							LaunchingMessages.AbstractInterpreterInstallType_libraryDiscoveryScript,
							cmdLine, env);
				}
				process = exeEnv.exec(cmdLine, null, env);
				if (process != null) {
					String result[] = readPathsFromProcess(monitor, process);
					if (result == null) {
						throw new IOException(
								LaunchingMessages.AbstractInterpreterInstallType_nullResultFromProcess);
					}
					if (DLTKLaunchingPlugin.TRACE_EXECUTION) {
						traceDiscoveryOutput(result);
					}
					String[] paths = null;
					if (result.length == 1) {
						paths = parsePaths(result[0]);
					} else {
						paths = parsePaths(result);
					}

					IPath path = new Path(locator.getCanonicalPath())
							.removeLastSegments(1);

					fillLocationsExceptOne(exeEnv.getEnvironment(), locations,
							paths, path);
					if (result != null) {
						StringBuffer resultBuffer = new StringBuffer();
						for (int i = 0; i < result.length; i++) {
							resultBuffer.append(result[i]).append("\n"); //$NON-NLS-1$
						}
						return resultBuffer.toString();
					}
				}
			} catch (CoreException e) {
				DLTKLaunchingPlugin.log(e);
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			if (DLTKCore.VERBOSE) {
				getLog()
						.log(
								createStatus(
										IStatus.ERROR,
										LaunchingMessages.AbstractInterpreterInstallType_unableToLookupLibraryPaths,
										e));
			}
		} finally {
			if (process != null) {
				process.destroy();
			}
			if (monitor != null) {
				monitor.done();
			}
		}
		return null;
	}

	private void traceDiscoveryOutput(String[] result) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		sb.append("Discovery script output:").append('\n'); //$NON-NLS-1$
		sb.append("Output Result:"); //$NON-NLS-1$
		if (result != null) {
			for (int i = 0; i < result.length; i++) {
				sb.append(" " + result[i]); //$NON-NLS-1$
			}
		} else {
			sb.append("Null"); //$NON-NLS-1$
		}
		sb.append("\n-----------------------------------------------\n"); //$NON-NLS-1$
		System.out.println(sb);
	}

	private void traceExecution(String processLabel, String[] cmdLineLabel,
			String[] environment) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		sb.append("Running ").append(processLabel).append('\n'); //$NON-NLS-1$
		// sb.append("Command line: ").append(cmdLineLabel).append('\n');
		sb.append("Command line: "); //$NON-NLS-1$
		for (int i = 0; i < cmdLineLabel.length; i++) {
			sb.append(" " + cmdLineLabel[i]); //$NON-NLS-1$
		}
		sb.append("\n"); //$NON-NLS-1$
		sb.append("Environment:\n"); //$NON-NLS-1$
		for (int i = 0; i < environment.length; i++) {
			sb.append('\t').append(environment[i]).append('\n');
		}
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		System.out.println(sb);
	}

	protected ILookupRunnable createLookupRunnable(
			final IFileHandle installLocation, final List locations,
			final EnvironmentVariable[] variables) {
		return new ILookupRunnable() {
			public void run(IProgressMonitor monitor) {
				try {
					IEnvironment env = installLocation.getEnvironment();
					IExecutionEnvironment exeEnv = (IExecutionEnvironment) env
							.getAdapter(IExecutionEnvironment.class);
					if (exeEnv == null)
						return;
					IDeployment deployment = exeEnv.createDeployment();
					try {
						IPath deploymentPath = createPathFile(deployment);
						IFileHandle locator = deployment
								.getFile(deploymentPath);
						String result = retrivePaths(exeEnv, installLocation,
								locations, monitor, locator, variables);
						String message = MessageFormat
								.format(
										LaunchingMessages.AbstractInterpreterInstallType_failedToResolveLibraryLocationsForWith,
										new Object[] {
												installLocation.getName(),
												locator.toOSString() });
						if (locations.size() == 0) {
							if (result == null) {
								DLTKLaunchingPlugin.log(message);
							} else {
								DLTKLaunchingPlugin
										.logWarning(
												message,
												new Exception(
														MessageFormat
																.format(
																		LaunchingMessages.AbstractInterpreterInstallType_output,
																		new Object[] { result })));
							}
						}
					} finally {
						if (deployment != null) {
							deployment.dispose();
						}
					}
				} catch (IOException e) {
					DLTKLaunchingPlugin
							.log(
									LaunchingMessages.AbstractInterpreterInstallType_problemWhileResolvingInterpreterLibraries,
									e);
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	public synchronized LibraryLocation[] getDefaultLibraryLocations(
			final IFileHandle installLocation) {
		return getDefaultLibraryLocations(installLocation, null);
	}

	public synchronized LibraryLocation[] getDefaultLibraryLocations(
			final IFileHandle installLocation, EnvironmentVariable[] variables) {
		return getDefaultLibraryLocations(installLocation, variables, null);
	}

	public synchronized LibraryLocation[] getDefaultLibraryLocations(
			final IFileHandle installLocation, EnvironmentVariable[] variables,
			IProgressMonitor monitor) {
		if (monitor != null) {
			monitor
					.beginTask(
							MessageFormat
									.format(
											LaunchingMessages.AbstractInterpreterInstallType_resolvingLibraryPaths,
											new Object[] { this.getName() }),
							100);
		}
		Object cacheKey = makeKey(installLocation, variables);
		if (fCachedLocations.containsKey(cacheKey)) {
			return (LibraryLocation[]) fCachedLocations.get(cacheKey);
		}

		final ArrayList locations = new ArrayList();

		final ILookupRunnable runnable = createLookupRunnable(installLocation,
				locations, variables);

		try {
			runLibraryLookup(runnable,
					monitor != null ? new SubProgressMonitor(monitor, 95)
							: null);
		} catch (InvocationTargetException e) {
			getLog()
					.log(
							createStatus(
									IStatus.ERROR,
									LaunchingMessages.AbstractInterpreterInstallType_errorResolvingDefaultLibraries,
									e));
		} catch (InterruptedException e) {
			getLog()
					.log(
							createStatus(
									IStatus.ERROR,
									LaunchingMessages.AbstractInterpreterInstallType_errorResolvingDefaultLibraries,
									e));
		}

		LibraryLocation[] libs = correctLocations(locations,
				monitor != null ? new SubProgressMonitor(monitor, 5) : null);
		if (libs.length != 0) {
			fCachedLocations.put(cacheKey, libs);
		}
		if (monitor != null) {
			monitor.done();
		}
		return libs;
	}

	public static Object makeKey(IFileHandle installLocation,
			EnvironmentVariable[] variables) {
		String key = installLocation.toURI().toString();
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				key += "|" + variables[i].getName() + ":" //$NON-NLS-1$ //$NON-NLS-2$
						+ variables[i].getValue();
			}
		}
		return key;
	}

	protected IStatus createStatus(int severity, String message,
			Throwable throwable) {
		return new Status(severity, getPluginId(), 0, message, throwable);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fId == null) ? 0 : fId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractInterpreterInstallType other = (AbstractInterpreterInstallType) obj;
		if (fId == null) {
			if (other.fId != null)
				return false;
		} else if (!fId.equals(other.fId))
			return false;
		return true;
	}
}
