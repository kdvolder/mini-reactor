package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

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
public class IdentityTransformerFlux<T> extends Flux<T> {
	
	//TODO: this 'transformer' is a special case when 'input type' and 'output type' are the same
	// However, when transformations actually change the type of values in the stream than this
	// won't work. It should be possible to generalize and extract an abstract superclass
	// that has two type parameters one for 'input' and one for 'output'.
	
	protected final Publisher<? extends T> in;

	public IdentityTransformerFlux(Publisher<? extends T> in) {
		this.in = in;
	}

	@Override
	public void subscribe(Subscriber<? super T> out) {
		in.subscribe(createSubscription(out));
	}

	protected Subscriber<T> createSubscription(Subscriber<? super T> out) {
		return new IdentityTransformerSubscription<>(in, out);
	}
	
}
