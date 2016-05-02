package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class IdentityTransformerSubscription<T> extends TransformerSubscription<T,T> {

	public IdentityTransformerSubscription(Publisher<? extends T> in, Subscriber<? super T> out) {
		super(in, out);
	}

	@Override
	protected T transform(T t) {
		return t;
	}
	
}