/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.processing.building.impl;

import org.hibernate.search.engine.mapper.mapping.building.spi.IndexModelBindingContext;
import org.hibernate.search.mapper.pojo.reporting.impl.PojoEventContexts;
import org.hibernate.search.mapper.pojo.mapping.building.impl.PojoMappingHelper;
import org.hibernate.search.mapper.pojo.model.path.PojoModelPath;
import org.hibernate.search.mapper.pojo.model.path.impl.BoundPojoModelPath;
import org.hibernate.search.engine.reporting.spi.ContextualFailureCollector;

abstract class AbstractPojoProcessorNodeBuilder {

	final PojoMappingHelper mappingHelper;
	final IndexModelBindingContext bindingContext;

	AbstractPojoProcessorNodeBuilder(
			PojoMappingHelper mappingHelper, IndexModelBindingContext bindingContext) {
		this.mappingHelper = mappingHelper;
		this.bindingContext = bindingContext;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + getModelPath() + "]";
	}

	abstract BoundPojoModelPath getModelPath();

	abstract void closeOnFailure();

	public final ContextualFailureCollector getFailureCollector() {
		BoundPojoModelPath modelPath = getModelPath();

		ContextualFailureCollector failureCollector = mappingHelper.getFailureCollector()
				.withContext(
						PojoEventContexts.fromType( modelPath.getRootType().getRawType() )
				);

		PojoModelPath unboundPath = modelPath.toUnboundPath();
		if ( unboundPath != null ) {
			failureCollector = failureCollector.withContext(
					PojoEventContexts.fromPath( modelPath.toUnboundPath() )
			);
		}

		return failureCollector;
	}
}
