/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.backend.lucene.impl;

import java.lang.invoke.MethodHandles;

import org.hibernate.search.backend.lucene.analysis.model.impl.LuceneAnalysisDefinitionRegistry;
import org.hibernate.search.backend.lucene.document.model.dsl.impl.LuceneIndexSchemaRootNodeBuilder;
import org.hibernate.search.backend.lucene.index.impl.DirectoryProvider;
import org.hibernate.search.backend.lucene.types.dsl.LuceneIndexFieldTypeFactoryContext;
import org.hibernate.search.backend.lucene.types.dsl.impl.LuceneIndexFieldTypeFactoryContextImpl;
import org.hibernate.search.engine.backend.Backend;
import org.hibernate.search.engine.backend.index.spi.IndexManagerBuilder;
import org.hibernate.search.backend.lucene.LuceneBackend;
import org.hibernate.search.backend.lucene.document.impl.LuceneRootDocumentBuilder;
import org.hibernate.search.backend.lucene.index.impl.IndexingBackendContext;
import org.hibernate.search.backend.lucene.index.impl.LuceneIndexManagerBuilder;
import org.hibernate.search.backend.lucene.logging.impl.Log;
import org.hibernate.search.backend.lucene.multitenancy.impl.MultiTenancyStrategy;
import org.hibernate.search.backend.lucene.orchestration.impl.LuceneQueryWorkOrchestrator;
import org.hibernate.search.backend.lucene.orchestration.impl.LuceneStubQueryWorkOrchestrator;
import org.hibernate.search.backend.lucene.search.query.impl.SearchBackendContext;
import org.hibernate.search.backend.lucene.work.impl.LuceneWorkFactory;
import org.hibernate.search.engine.backend.spi.BackendImplementor;
import org.hibernate.search.engine.cfg.ConfigurationPropertySource;
import org.hibernate.search.engine.backend.spi.BackendBuildContext;
import org.hibernate.search.util.common.reporting.EventContext;
import org.hibernate.search.engine.reporting.spi.EventContexts;
import org.hibernate.search.util.common.impl.Closer;
import org.hibernate.search.util.common.logging.impl.LoggerFactory;

/**
 * @author Guillaume Smet
 */
public class LuceneBackendImpl implements BackendImplementor<LuceneRootDocumentBuilder>, LuceneBackend {

	private static final Log log = LoggerFactory.make( Log.class, MethodHandles.lookup() );

	private final String name;

	private final DirectoryProvider directoryProvider;

	private final LuceneAnalysisDefinitionRegistry analysisDefinitionRegistry;

	private final LuceneQueryWorkOrchestrator queryOrchestrator;
	private final MultiTenancyStrategy multiTenancyStrategy;

	private final EventContext eventContext;
	private final IndexingBackendContext indexingContext;
	private final SearchBackendContext searchContext;

	LuceneBackendImpl(String name, DirectoryProvider directoryProvider, LuceneWorkFactory workFactory,
			LuceneAnalysisDefinitionRegistry analysisDefinitionRegistry,
			MultiTenancyStrategy multiTenancyStrategy) {
		this.name = name;
		this.directoryProvider = directoryProvider;

		this.analysisDefinitionRegistry = analysisDefinitionRegistry;

		this.queryOrchestrator = new LuceneStubQueryWorkOrchestrator();
		this.multiTenancyStrategy = multiTenancyStrategy;

		this.eventContext = EventContexts.fromBackendName( name );
		this.indexingContext = new IndexingBackendContext(
				eventContext, directoryProvider,
				workFactory, multiTenancyStrategy
		);
		this.searchContext = new SearchBackendContext(
				eventContext, workFactory, multiTenancyStrategy, queryOrchestrator
		);
	}

	@Override
	@SuppressWarnings("unchecked") // Checked using reflection
	public <T> T unwrap(Class<T> clazz) {
		if ( clazz.isAssignableFrom( LuceneBackend.class ) ) {
			return (T) this;
		}
		throw log.backendUnwrappingWithUnknownType(
				clazz, LuceneBackend.class, eventContext
		);
	}

	@Override
	public Backend toAPI() {
		return this;
	}

	@Override
	public IndexManagerBuilder<LuceneRootDocumentBuilder> createIndexManagerBuilder(
			String indexName, boolean multiTenancyEnabled, BackendBuildContext context, ConfigurationPropertySource propertySource) {
		if ( multiTenancyEnabled && !multiTenancyStrategy.isMultiTenancySupported() ) {
			throw log.multiTenancyRequiredButNotSupportedByBackend( indexName, eventContext );
		}

		EventContext indexEventContext = EventContexts.fromIndexName( indexName );

		LuceneIndexFieldTypeFactoryContext typeFactoryContext = new LuceneIndexFieldTypeFactoryContextImpl(
				indexEventContext, analysisDefinitionRegistry
		);
		LuceneIndexSchemaRootNodeBuilder indexSchemaRootNodeBuilder = new LuceneIndexSchemaRootNodeBuilder(
				indexEventContext, typeFactoryContext
		);

		/*
		 * We do not normalize index names: directory providers are expected to use the exact given index name,
		 * or a reversible conversion of that name, as an internal key (file names, ...),
		 * and therefore the internal key should stay unique.
		 */
		return new LuceneIndexManagerBuilder(
				indexingContext, searchContext,
				indexName, indexSchemaRootNodeBuilder
		);
	}

	@Override
	public void close() {
		try ( Closer<RuntimeException> closer = new Closer<>() ) {
			closer.push( LuceneQueryWorkOrchestrator::close, queryOrchestrator );
		}
	}

	@Override
	public String toString() {
		return new StringBuilder( getClass().getSimpleName() )
				.append( "[" )
				.append( "name=" ).append( name ).append( ", " )
				.append( "directoryProvider=" ).append( directoryProvider )
				.append( "]" )
				.toString();
	}
}
