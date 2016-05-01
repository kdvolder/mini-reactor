package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;

/**
 * A subscription for used by data-less publishers (i.e. Flux.error() or Flux.empty()).
 */
public class EmptySubsciption<T> extends BaseSubscription<T> {
	
	public EmptySubsciption(Subscriber<? super T> out) {
		super(out);
	}

	@Override
	public void request(long n) {
		//Ignore demand except for checking the validity of the argument as required by the spec.
		if (n<=0) {
			illegalArgument("Rule 3.9: request parameter MUST be positive");
		}
	}

	@Override
	public void cancel() {
		out = null;
	}

}
