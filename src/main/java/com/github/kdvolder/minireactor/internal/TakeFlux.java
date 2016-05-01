package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

public class TakeFlux<T> extends IdentityTransformerFlux<T> {

	private final long allowedToTake;

	public TakeFlux(Publisher<T> in, long allowedToTake) {
		super(in);
		if (allowedToTake<=0) {
			throw new IllegalArgumentException("allowedToTake must be > 0");
		}
		this.allowedToTake = allowedToTake;
	}
	
	@Override
	protected <R> Subscriber<R> createSubscription(Subscriber<R> out) {
		return new IdentityTransformerSubscription<R>(out) {

			long allowedToRequest = allowedToTake;
			long taken = 0;
			
			@Override
			public void request(long n) {
				n = Math.min(n, allowedToRequest);
				if (n>0) {
					allowedToRequest-=n;
					super.request(n);
				}
			}
			
			@Override
			public void onNext(R t) {
				super.onNext(t);
				taken++;
				if (taken>=allowedToTake) {
					onComplete();
				}
			}

		};
	}

}
