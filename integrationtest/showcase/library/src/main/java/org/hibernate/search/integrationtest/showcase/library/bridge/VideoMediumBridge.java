/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.integrationtest.showcase.library.bridge;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.integrationtest.showcase.library.model.BookMedium;
import org.hibernate.search.integrationtest.showcase.library.model.VideoMedium;

public class VideoMediumBridge implements ValueBridge<VideoMedium, String> {
	@Override
	public String toIndexedValue(VideoMedium value) {
		return value == null ? null : value.name();
	}

	@Override
	public Object fromIndexedValue(String indexedValue) {
		return indexedValue == null ? null : BookMedium.valueOf( indexedValue );
	}
}