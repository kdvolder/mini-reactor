package com.github.kdvolder.minireactor;

import java.util.function.Predicate;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class FilterFlux<T> extends Flux<T> {

	private final Predicate<? super T> pred;
	private final Flux<T> in;
	
	public FilterFlux(Flux<T> in, Predicate<? super T> pred) {
		this.pred = pred;
		this.in = in;
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		FilterSubscriber sub = new FilterSubscriber(s);
		in.subscribe(sub);
		s.onSubscribe(sub);
	}
	
	private class FilterSubscriber extends BaseSubscription<T> implements Subscriber<T> {

		private Subscription upstream;
		
		public FilterSubscriber(Subscriber<? super T> out) {
			super(out);
		}
		
		@Override
		public void onNext(T t) {
			if (pred.test(t)) {
				super.onNext(t);
			} else {
				upstream.request(1);
			}
		}
		
		@Override
		public void request(long n) {
			upstream.request(n);
		}

		@Override
		public void onSubscribe(Subscription s) {
			this.upstream = s;
		}
	}
}
