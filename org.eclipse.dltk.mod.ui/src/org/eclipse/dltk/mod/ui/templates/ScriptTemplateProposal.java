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
package org.eclipse.dltk.mod.ui.templates;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class ScriptTemplateProposal extends TemplateProposal {

	public ScriptTemplateProposal(Template template, TemplateContext context,
			IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
	}

	private boolean isRelevanceOverriden;
	private int relevanceOverride;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateProposal#getRelevance()
	 */
	public int getRelevance() {
		return isRelevanceOverriden ? relevanceOverride: super.getRelevance();
	}

	public void setRelevance(int value) {
		this.relevanceOverride = value;
		this.isRelevanceOverriden = true;
	}

	public String getAdditionalProposalInfo() {
		TemplateContext context = getContext();
		if (context instanceof ScriptTemplateContext) {
			ScriptTemplateContext scriptContext = (ScriptTemplateContext) context;

			try {
				getContext().setReadOnly(true);
				TemplateBuffer templateBuffer;
				templateBuffer = scriptContext.evaluate(getTemplate());

				// restore indenting
				IDocument document = scriptContext.getDocument();
				String indenting = ScriptTemplateContext.calculateIndent(
						document, scriptContext.getStart());
				String delimeter = TextUtilities.getDefaultLineDelimiter(document);

				String info = templateBuffer.getString();
				return info.replaceAll(delimeter + indenting, delimeter);
			} catch (BadLocationException e) {
			} catch (TemplateException e1) {
			}
		}
		return null;
	}

	public String getPattern() {
		return getTemplate().getPattern();
	}

}
