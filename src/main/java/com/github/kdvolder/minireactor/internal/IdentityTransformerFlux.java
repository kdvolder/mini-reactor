package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

/**
 * A transformer flux wraps a 'source' Publisher and passes data from
 * the source publisher through to its own subscribers.
 * <p>
 * The {@link IdentityTransformerFlux} class is meant to be subclassed in order
 * to 'transform' the stream of data passing trough it somehow.
 * <p>
 * However the implementation is already complete and corresponds to
 * an 'identity' transformation (passing all data and signals unchanged).
 */
public class IdentityTransformerFlux<T> extends TransformerFlux<T, T> {

	public IdentityTransformerFlux(Publisher<T> in) {
		super(in);
	}

	@Override
	protected TransformerSubscription<T, T> createSubscription(Subscriber<? super T> out) {
		return new IdentityTransformerSubscription<>(in, out);
	}
	
}
