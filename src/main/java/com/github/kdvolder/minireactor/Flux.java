package com.github.kdvolder.minireactor;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

public abstract class Flux<T> implements Publisher<T> {

	public static <T> Flux<T> error(Throwable e) {
		return new ErrorFlux<T>(e);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Flux<T> empty() {
		return EmptyFlux.THE;
	}
	
	public final Flux<T> take(long n) {
		if (n>0) {
			return new TakeFlux<T>(this, n);
		} else {
			return empty();
		}
	}
	
	public final Flux<T> filter(Predicate<? super T> pred) {
		return new FilterFlux<T>(this, pred);
	}

	public final Cancellation consume(Consumer<? super T> consumer) {
		SubcriptionCancelation cancel = new SubcriptionCancelation();
		this.subscribe(new AbsractSubscriber<T>() {
			@Override
			public void onSubscribe(Subscription s) {
				cancel.bind(s);
				super.onSubscribe(s);
			}
			
			@Override
			public void onNext(T t) {
				consumer.accept(t);
			}
		});
		return cancel;
	}
	
	/**
	 * Returns a Flux that publishes a range of integers, starting with
	 * the 'from' element  upto but not including the 'to' element.
	 */
	public static Flux<Integer> range(int from, int to) {
		if (from<to) {
			return new RangeFlux<Integer>(from) {
				@Override
				protected Integer increment(Integer current) {
					return current + 1;
				}
				@Override
				protected boolean inRange(Integer current) {
					return current < to;
				}
				@Override
				public String toString() {
					return "["+from+".."+(to-1)+"]";
				}
			};
		}
		return empty();
	}
	
}
