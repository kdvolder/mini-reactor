package com.github.kdvolder.minireactor.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Subscriber;

/**
 * Abstract superclass for subscriptions that send data synchronously to a subscriber.
 */
public abstract class DataSubscription<T> extends BaseSubscription<T> {
	
	private long demand = 0;
	
	private AtomicBoolean sending = new AtomicBoolean(false);

	public DataSubscription(Subscriber<? super T> out) {
		super(out);
	}

	@Override
	public void request(long n) {
		incrementDemand(n);
		satisfyDemand();
	}

	protected void satisfyDemand() {
		//use 'sending' atomic boolean to avoid recursion on same thread.
		//When already in a 'satisfyDemand' on current thread we can just assume that
		//the other call will already satisfy all the demand.
		if (sending.compareAndSet(false, true)) {
			while (out!=null && demand>0) {
				try {
					if (hasNext()) {
						onNext(next());
						decrementDemand();
					} else {
						onComplete();
					}
				} catch (Exception e) {
					onError(e);
				}
			}
			sending.set(false);
		}
	}

	protected abstract boolean hasNext() throws Exception;
	protected abstract T next() throws Exception;

	protected void decrementDemand() {
		if (demand!=Long.MAX_VALUE) {
			demand--;
		}
	}
	
	protected void incrementDemand(long n) {
		if (n<=0) {
			illegalArgument("Rule 3.9: request parameter MUST be positive");
		}
		try {
			demand = Math.addExact(demand, n);
		} catch (ArithmeticException e) {
			//Overflowed!
			demand = Long.MAX_VALUE;
		}
	}
	
	protected void send(T next) {
		if (out!=null) {
			out.onNext(next);
			decrementDemand();
		}
	}
	
}
