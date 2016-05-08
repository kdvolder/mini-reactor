package com.github.kdvolder.minireactor.internal;

import java.util.function.Predicate;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class FilterFlux<T> extends TransformerFlux<T, T> {
	
	private final Predicate<? super T> pred;
	
	public FilterFlux(Flux<T> in, Predicate<? super T> pred) {
		super(in);
		this.pred = pred;
	}

	@Override
	protected TransformerSubscription<T, T> createSubscription(Subscriber<? super T> out) {
		return new IdentityTransformerSubscription<T>(in, out) {
			@Override
			public void onNext(T t) {
				if (pred.test(t)) {
					super.onNext(t);
				} else {
					super.request(1);
				}
			}
		};
	}
}
