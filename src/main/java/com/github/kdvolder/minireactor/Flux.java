package com.github.kdvolder.minireactor;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import com.github.kdvolder.minireactor.internal.BasicSubscriber;
import com.github.kdvolder.minireactor.internal.DropFlux;
import com.github.kdvolder.minireactor.internal.EmptyFlux;
import com.github.kdvolder.minireactor.internal.ErrorFlux;
import com.github.kdvolder.minireactor.internal.FilterFlux;
import com.github.kdvolder.minireactor.internal.IdentityTransformerFlux;
import com.github.kdvolder.minireactor.internal.RangeFlux;
import com.github.kdvolder.minireactor.internal.SubcriptionCancelation;
import com.github.kdvolder.minireactor.internal.TakeFlux;

public abstract class Flux<T> implements Publisher<T> {

	@SuppressWarnings("unchecked")
	public static <T> Flux<T> from(Publisher<? extends T> source) {
		if (source instanceof Flux) {
			return (Flux<T>) source;
		}
		return new IdentityTransformerFlux<T>(source);
	}

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

	public final Flux<T> drop(long n) {
		if (n>0) {
			return new DropFlux<T>(this, n);
		} else {
			return this;
		}
	}

	public final Flux<T> filter(Predicate<? super T> pred) {
		return new FilterFlux<T>(this, pred);
	}

	public final Cancellation consume(Consumer<? super T> consumer) {
		SubcriptionCancelation cancel = new SubcriptionCancelation();
		this.subscribe(new BasicSubscriber<T>() {
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
