/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.mapping.building.impl;

import org.hibernate.search.engine.environment.bean.BeanHolder;
import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge;
import org.hibernate.search.mapper.pojo.bridge.RoutingKeyBridge;
import org.hibernate.search.mapper.pojo.model.path.impl.BoundPojoModelPathPropertyNode;

public interface PojoIdentityMappingCollector {

	<T> void identifierBridge(BoundPojoModelPathPropertyNode<?, T> modelPath,
			BeanHolder<? extends IdentifierBridge<T>> bridge);

	void routingKeyBridge(BeanHolder<? extends RoutingKeyBridge> bridgeHolder);
}
