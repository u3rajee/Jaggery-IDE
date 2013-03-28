package org.eclipse.jaggery.eclipse.internal.ui.text;

import org.eclipse.jface.text.IDocument;

public class IJaggeryScriptPartitions {
	
	public final static String JAG_PARTITIONING = IJaggeryScriptConstants.JAG_PARTITIONING;
	
	/**
	 * The identifier of the JavaScript partitioning.
	 */
	public final static String JAVA_PARTITIONING= "___java_partitioning";  //$NON-NLS-1$

	public final static String JAG_SINGLE_COMMENT = "__jaggeryscript_single_comment";
	public final static String JAG_MULTI_COMMENT = "__jaggeryscript_multi_comment";

	public final static String JAG_STRING = "__jaggeryscript_string";
	public static final String JAG_DOC = "__jaggeryscript_doc";

	public final static String[] JAG_PARTITION_TYPES = new String[] {
			IDocument.DEFAULT_CONTENT_TYPE, IJaggeryScriptPartitions.JAG_STRING,
			IJaggeryScriptPartitions.JAG_SINGLE_COMMENT,
			IJaggeryScriptPartitions.JAG_MULTI_COMMENT,
			IJaggeryScriptPartitions.JAG_DOC };

}
