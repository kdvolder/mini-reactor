package com.github.kdvolder.minireactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class TakeFlux<T> extends Flux<T> {

	private final long allowedToTake;
	private Flux<T> in;

	private class TakeSubscriber extends BaseSubscription<T> implements Subscriber<T> {
		
		long allowedToRequest = allowedToTake;
		long taken = 0;
		
		private Subscription upstream;
		
		public TakeSubscriber(Subscriber<? super T> out) {
			super(out);
		}

		@Override
		public void request(long n) {
			Subscription upstream = this.upstream;
			if (upstream!=null) {
				n = Math.min(n, allowedToRequest);
				if (n>0) {
					allowedToRequest-=n;
					upstream.request(n);
				}
			}
		}
		
		@Override
		public void onNext(T t) {
			super.onNext(t);
			taken++;
			if (taken>=allowedToTake) {
				onComplete();
			}
		}

		@Override
		public void onSubscribe(Subscription s) {
			this.upstream = s;
		}

		@Override
		public void cancel() {
			super.cancel();
			upstream = null;
		}
	}

	public TakeFlux(Flux<T> in, long allowedToTake) {
		if (allowedToTake<=0) {
			throw new IllegalArgumentException("allowedToTake must be > 0");
		}
		this.in = in;
		this.allowedToTake = allowedToTake;
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		TakeSubscriber sub =new TakeSubscriber(s);
		in.subscribe(sub);
		s.onSubscribe(sub);
	}

}
