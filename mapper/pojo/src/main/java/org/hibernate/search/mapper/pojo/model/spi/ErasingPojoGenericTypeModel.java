/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.mapper.pojo.model.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.search.mapper.pojo.util.impl.GenericTypeContext;
import org.hibernate.search.mapper.pojo.util.impl.ReflectionUtils;

/**
 * An implementation of {@link PojoGenericTypeModel} that will erase generics information
 * when accessing property types.
 * <p>
 * For instance, given the following model:
 * <pre><code>
 * class A&lt;T extends C&gt; {
 *   List&lt;T&gt; propertyOfA;
 * }
 * class B extends A&lt;D&gt; {
 * }
 * class C {
 * }
 * class D extends C {
 * }
 * </code></pre>
 * ... if an instance of this implementation was used to model the type of {@code B.propertyOfA},
 * then the property {@code B.propertyOfA} would appear to have type {@code List<T>} with {@code T extends C}
 * instead of type {@code List<D>} as one would expect.
 * <p>
 * This behavior is clearly not ideal, but it's by far the easiest way to implement {@link ErasingPojoGenericTypeModel},
 * because it allows to only implement {@link PojoTypeModel} for raw types.
 * One could imagine going one step further and retain generic information from the holding type
 * when inspecting the type of a property, but this would require some additional work,
 * both in the engine and in the type model implementations.
 */
public final class ErasingPojoGenericTypeModel<T> implements PojoGenericTypeModel<T> {

	private final PojoIntrospector introspector;
	private final PojoRawTypeModel<? super T> rawTypeModel;
	private final Type type;
	private final GenericTypeContext typeContext;

	public ErasingPojoGenericTypeModel(PojoIntrospector introspector, PojoRawTypeModel<T> rawTypeModel) {
		this( introspector, rawTypeModel, rawTypeModel.getJavaClass() );
	}

	public ErasingPojoGenericTypeModel(PojoIntrospector introspector, PojoRawTypeModel<? super T> rawTypeModel,
			Type type) {
		this.introspector = introspector;
		this.rawTypeModel = rawTypeModel;
		this.type = type;
		this.typeContext = new GenericTypeContext( type );
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + type.getTypeName() + "]";
	}

	public PojoRawTypeModel<? super T> getRawType() {
		return rawTypeModel;
	}

	@Override
	public <U> Optional<PojoTypeModel<U>> getSuperType(Class<U> superClassCandidate) {
		return rawTypeModel.getSuperType( superClassCandidate );
	}

	@Override
	public <A extends Annotation> Optional<A> getAnnotationByType(Class<A> annotationType) {
		return rawTypeModel.getAnnotationByType( annotationType );
	}

	@Override
	public <A extends Annotation> Stream<A> getAnnotationsByType(Class<A> annotationType) {
		return rawTypeModel.getAnnotationsByType( annotationType );
	}

	@Override
	public Stream<? extends Annotation> getAnnotationsByMetaAnnotationType(
			Class<? extends Annotation> metaAnnotationType) {
		return rawTypeModel.getAnnotationsByMetaAnnotationType( metaAnnotationType );
	}

	@Override
	public PojoPropertyModel<?> getProperty(String propertyName) {
		return rawTypeModel.getProperty( propertyName );
	}

	@Override
	public Stream<PojoPropertyModel<?>> getDeclaredProperties() {
		return rawTypeModel.getDeclaredProperties();
	}

	@Override
	@SuppressWarnings("unchecked") // We cannot perform runtime checks of generics on an instance
	public T cast(Object instance) {
		return (T) rawTypeModel.cast( instance );
	}

	@Override
	public Optional<PojoGenericTypeModel<?>> getTypeArgument(Class<?> rawSuperType, int typeParameterIndex) {
		return typeContext.resolveTypeArgument( rawSuperType, typeParameterIndex )
				.map( type -> new ErasingPojoGenericTypeModel<>(
						introspector, introspector.getTypeModel( ReflectionUtils.getRawType( type ) ), type
				) );
	}

	@Override
	public Optional<PojoGenericTypeModel<?>> getArrayElementType() {
		return ReflectionUtils.getArrayElementType( type )
				.map( type -> new ErasingPojoGenericTypeModel<>(
						introspector, introspector.getTypeModel( ReflectionUtils.getRawType( type ) ), type
				) );
	}

}