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
	protected IdentityTransformerSubscription<T> createSubscription(Subscriber<? super T> out) {
		return new IdentityTransformerSubscription<T>(in, out) {

			long dropped = 0;
			boolean extrasRequested = false;
			
			@Override
			protected void onRequest(long n) {
				if (!extrasRequested) {
					n = Demand.add(n, toDrop);
					extrasRequested = true;
				}
				super.onRequest(n);
			}
			
			@Override
			public void onNext(T t) {
				if (dropped < toDrop) {
					dropped++;
				} else {
					super.onNext(t);
				}
			}

		};
	}

}
