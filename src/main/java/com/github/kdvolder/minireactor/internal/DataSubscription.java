package com.github.kdvolder.minireactor.internal;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Subscriber;

/**
 * Abstract superclass for subscriptions that send data synchronously to a subscriber.
 */
public abstract class DataSubscription<T> extends BaseSubscription<T> {
	
	private Demand demand = new Demand(0);
	
	private AtomicBoolean sending = new AtomicBoolean(false);

	public DataSubscription(Subscriber<? super T> out) {
		super(out);
	}

	@Override
	public void onRequest(long n) {
		demand.increment(n);
		satisfyDemand();
	}

	protected void satisfyDemand() {
		//use 'sending' atomic boolean to avoid recursion on same thread.
		//When already in a 'satisfyDemand' on current thread we can just assume that
		//the other call will already satisfy all the demand.
		if (sending.compareAndSet(false, true)) {
			while (out!=null && demand.isPositive()) {
				try {
					if (hasNext()) {
						sendNext(next());
						demand.decrement();
					} else {
						sendComplete();
					}
				} catch (Exception e) {
					sendError(e);
				}
			}
			sending.set(false);
		}
	}

	protected abstract boolean hasNext() throws Exception;
	protected abstract T next() throws Exception;

	protected void send(T next) {
		if (out!=null) {
			out.onNext(next);
			demand.decrement();
		}
	}
	
	@Override
	protected void onCancel() {
	}
}
