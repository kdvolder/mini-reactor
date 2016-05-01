package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscription;

import com.github.kdvolder.minireactor.Cancellation;

/**
 * Wrapper around a {@link Subscription} implementing the {@link Cancellation} interface.
 * <p>
 * Tricky bit: the {@link Cancellation} may be canceled even before its Subscription
 * is 'bound' to it. This case must be handled properly (canceling the subscription
 * as early as possible).
 */
public class SubcriptionCancelation implements Cancellation {

	private boolean isCanceled = false;
	private Subscription subscription = null;
	
	public void bind(Subscription s) {
		if (subscription!=null) {
			throw new IllegalStateException("Already bound!");
		}
		subscription = s;
		if (isCanceled) {
			s.cancel();
		}
	}

	@Override
	public void dispose() {
		if (subscription!=null) {
			subscription.cancel();
		}
	}

}
