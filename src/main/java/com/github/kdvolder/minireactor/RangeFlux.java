package com.github.kdvolder.minireactor;

import org.reactivestreams.Subscriber;

/**
 * A Flux producing a range of elements starting at a given 'from'
 * element and applying a 'increment' function repeatedly to obtain successive
 * elements, until the 'inRange' predicate returns false.
 * 
 * @author Kris De Volder
 */
public abstract class RangeFlux<T> extends Flux<T> {

	private static class RangeSubscription<T> extends DataSubscription<T> {
		T next;
		private RangeFlux<T> in;
		
		public RangeSubscription(RangeFlux<T> in, Subscriber<? super T> out) {
			super(out);
			this.in = in;
			this.next = in.from;
		}
		
		public void request(long n) {
			incrementDemand(n);
			satisfyDemand();
		}

		@Override
		protected boolean hasNext() throws Exception {
			return in.inRange(next);
		}

		@Override
		protected T next() throws Exception {
			T it = next;
			next = in.increment(next);
			return it;
		}
	}

	private T from;

	public RangeFlux(T from) {
		this.from = from;
	}
	
	/**
	 * Subclass must implement to define how the next element in the range is
	 * computed from its preceding element
	 */
	protected abstract T increment(T x) throws Exception;
	
	protected boolean inRange(T x) throws Exception {
		return true;
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		s.onSubscribe(new RangeSubscription<T>(this, s));
	}
	
}
