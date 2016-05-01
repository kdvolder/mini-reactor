package com.github.kdvolder.minireactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * A implementation of {@link Subscriber} meant to be subclassed.
 * <p>
 * If no methods are overridden the implementation is already complete
 * and does the following:
 * <ul>
 *   <li> Generates infinite demand on subscription
 *   <li> All incoming signals onNext, onError, onComplete are simply ignored.
 * </ul>
 * 
 * @author Kris De Volder
 */
public class AbsractSubscriber<T> implements Subscriber<T> {

	@Override
	public void onSubscribe(Subscription s) {
		s.request(Long.MAX_VALUE);
	}

	@Override
	public void onNext(T t) {
	}

	@Override
	public void onError(Throwable t) {
	}

	@Override
	public void onComplete() {
	}

}
