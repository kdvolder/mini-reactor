package com.github.kdvolder.minireactor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import com.github.kdvolder.minireactor.internal.BasicSubscriber;
import com.github.kdvolder.minireactor.internal.DataFlux;
import com.github.kdvolder.minireactor.internal.DropFlux;
import com.github.kdvolder.minireactor.internal.EmptyFlux;
import com.github.kdvolder.minireactor.internal.ErrorFlux;
import com.github.kdvolder.minireactor.internal.FilterFlux;
import com.github.kdvolder.minireactor.internal.IdentityTransformerFlux;
import com.github.kdvolder.minireactor.internal.MapFlux;
import com.github.kdvolder.minireactor.internal.SubcriptionCancelation;
import com.github.kdvolder.minireactor.internal.TakeFlux;

public abstract class Flux<T> implements Publisher<T> {

	@SuppressWarnings("unchecked")
	public static <T, A extends T> Flux<T> from(Publisher<A> source) {
		if (source instanceof Flux) {
			return (Flux<T>) source;
		}
		return new IdentityTransformerFlux<T>((Publisher<T>)source);
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
			return new DataFlux<Integer>(() -> new Iterator<Integer>() {
				int next = from;
				@Override public boolean hasNext() {
					return next<to;
				}
				@Override public Integer next() {
					return next++;
				}
			});
		}
		return empty();
	}

	@SafeVarargs
	public static <T> Flux<T> of(T... elements) {
		if (elements.length==0) {
			return empty();
		}
		return new DataFlux<T>(Arrays.asList(elements));
	}

	public <R> Flux<R> map(Function<T, R> f) {
		return new MapFlux<T, R>(this, f);
	}

}
