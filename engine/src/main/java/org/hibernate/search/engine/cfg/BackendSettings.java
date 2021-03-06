/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.engine.cfg;

/**
 * Configuration properties common to all Hibernate Search backends regardless of the underlying technology.
 * <p>
 * Constants in this class are to be appended to a prefix to form a property key.
 * The exact prefix will depend on the integration, but should generally look like
 * "{@code hibernate.search.backends.<backend name>.}".
 */
public final class BackendSettings {

	private BackendSettings() {
	}

	/**
	 * The type of the backend.
	 * <p>
	 * Expects a String, such as "lucene" or "elasticsearch".
	 * See the documentation of your backend to find the appropriate value.
	 * <p>
	 * No default: this property must be set.
	 */
	public static final String TYPE = "type";

	/**
	 * The root property whose children are default properties to be applied to all indexes of this backend.
	 */
	public static final String INDEX_DEFAULTS = "index_defaults";

}
