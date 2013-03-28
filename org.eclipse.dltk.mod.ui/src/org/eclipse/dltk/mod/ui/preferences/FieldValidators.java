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
package org.eclipse.dltk.mod.ui.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.mod.core.environment.EnvironmentManager;
import org.eclipse.dltk.mod.core.environment.IEnvironment;
import org.eclipse.dltk.mod.core.environment.IFileHandle;
import org.eclipse.dltk.mod.internal.corext.util.Messages;
import org.eclipse.dltk.mod.internal.ui.dialogs.StatusInfo;
import org.eclipse.dltk.mod.utils.PlatformFileUtils;

public final class FieldValidators {

	public static class FilePathValidator implements IFieldValidator {
		public IStatus validate(String text) {
			return validate(text, EnvironmentManager.getLocalEnvironment());
		}
		public IStatus validate(String text, IEnvironment environment) {
			StatusInfo status = new StatusInfo();
			if( environment == null ) {
				status.setError(org.eclipse.dltk.mod.ui.preferences.Messages.FieldValidators_0);
				return status;
			}

			if (!(text.trim().length() == 0)) {
				IFileHandle file = PlatformFileUtils
						.findAbsoluteOrEclipseRelativeFile(environment, Path
								.fromPortableString(text));

				if (!file.exists()) {
					status.setError(Messages.format(
							ValidatorMessages.FilePathNotExists, text));
				} else if (file.isDirectory()) {
					status.setError(Messages.format(
							ValidatorMessages.FilePathIsInvalid, text));
				}
			}

			return status;
		}
	}

	public static class PositiveNumberValidator implements IFieldValidator {
		public IStatus validate(String text) {
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0) {
				status.setError(ValidatorMessages.PositiveNumberIsEmpty);
			} else {
				try {
					int value = Integer.parseInt(text);
					if (value < 0) {
						status
								.setError(Messages
										.format(
												ValidatorMessages.PositiveNumberIsInvalid,
												text));
					}
				} catch (NumberFormatException e) {
					status.setError(Messages.format(
							ValidatorMessages.PositiveNumberIsInvalid, text));
				}
			}

			return status;
		}
	}

	public static class PortValidator implements IFieldValidator {
		public IStatus validate(String text) {
			StatusInfo status = new StatusInfo();

			if (text.trim().length() == 0) {
				status.setError(ValidatorMessages.PortIsEmpty);
			} else {
				try {
					int value = Integer.parseInt(text);
					if (value < 1000 || value > 65535) {
						status.setError(Messages.format(
								ValidatorMessages.PortShouldBeInRange, text));
					}
				} catch (NumberFormatException e) {
					status.setError(Messages.format(
							ValidatorMessages.PortShouldBeInRange, text));
				}
			}

			return status;
		}
	}

	// Available validators
	public static IFieldValidator POSITIVE_NUMBER_VALIDATOR = new PositiveNumberValidator();
	public static IFieldValidator PORT_VALIDATOR = new PortValidator();
}
