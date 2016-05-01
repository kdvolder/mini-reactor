package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class DropFlux<T> extends IdentityTransformerFlux<T> {

	final private long toDrop;

	public DropFlux(Publisher<T> in, long toDrop) {
		super(in);
		this.toDrop = toDrop;
	}
	
	@Override
	protected <R> Subscriber<R> createSubscription(Subscriber<R> out) {
		return new IdentityTransformerSubscription<R>(out) {

			long dropped = 0;
			boolean extrasRequested = false;
			
			@Override
			public void request(long n) {
				if (!extrasRequested) {
					n+=toDrop;
					extrasRequested = false;
				}
				super.request(n);
			}
			
			@Override
			public void onNext(R t) {
				if (dropped < toDrop) {
					dropped++;
				} else {
					super.onNext(t);
				}
			}

		};
	}

}
