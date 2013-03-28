/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.mod.internal.ui.text.hover;

import org.eclipse.dltk.mod.compiler.problem.IProblem;
import org.eclipse.dltk.mod.core.CorrectionEngine;
import org.eclipse.dltk.mod.utils.TextUtils;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * This annotation hover shows the description of the selected java annotation.
 * 
 * 
 * @since 3.0
 */
public class ProblemHover extends AbstractAnnotationHover {

	public ProblemHover() {
		super(false);
	}

	protected String postUpdateMessage(String message) {
		return super.postUpdateMessage(TextUtils.replace(message, '\n',
				"<br/>\n"));
	}

	protected String getMessageFromAnnotation(Annotation a) {
		if (a instanceof MarkerAnnotation) {
			MarkerAnnotation ma = (MarkerAnnotation) a;
			String[] arguments = CorrectionEngine.getProblemArguments(ma
					.getMarker());
			if (arguments != null) {
				return returnText(a, arguments);
			}
		}
		return a.getText();
	}

	private String returnText(Annotation a, String[] arguments) {
		for (int i = 0; i < arguments.length; i++) {
			String ar = arguments[i];
			if (ar.startsWith(IProblem.DESCRIPTION_ARGUMENT_PREFIX)) {
				return a.getText()
						+ "\n"
						+ ar.substring(IProblem.DESCRIPTION_ARGUMENT_PREFIX
								.length());
			}
		}
		return a.getText();
	}
}
