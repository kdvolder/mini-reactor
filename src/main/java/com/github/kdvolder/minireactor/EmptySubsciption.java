package com.github.kdvolder.minireactor;

import org.reactivestreams.Subscriber;

/**
 * A subscription for used by data-less publishers (i.e. Flux.error() or Flux.empty()).
 */
public class EmptySubsciption<T> extends BaseSubscription<T> {
	
	private Throwable error = null;
	
	public EmptySubsciption(Subscriber<? super T> out) {
		this(out, null);
	}

	public EmptySubsciption(Subscriber<? super T> out, Throwable error) {
		super(out);
		this.error = error;
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
