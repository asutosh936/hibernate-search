/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.analysis.model.dsl;

/**
 * @author Yoann Rodiere
 */
public interface LuceneAnalyzerDefinitionWithTokenizerContext extends LuceneCustomAnalysisDefinitionContext {

	/**
	 * Set a tokenizer parameter.
	 *
	 * @param name The name of the parameter.
	 * @param value The value of the parameter.
	 * @return This context, allowing to chain calls.
	 */
	LuceneAnalyzerDefinitionWithTokenizerContext param(String name, String value);

}
