package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * {@link BaseSubscription} is like a 'proxy' for given subscriber, it forwards signals
 * 'onNext', 'onComplete' and 'onError' to the underlying subscriber.
 * <p>
 * It does NOT track demand as this is not required for data-less publishers or publishers
 * who draw data from other publisher (who are already tracking the demand)
 */
public abstract class BaseSubscription<T> implements Subscription {

	protected Subscriber<? super T> out;
	
	protected BaseSubscription(Subscriber<? super T> out) {
		this.out = out;
	}

	public void illegalArgument(String s) {
		sendError(new IllegalArgumentException(s));
	}

	public final void sendNext(T t) {
		Subscriber<? super T> out = this.out;
		if (out!=null) {
			out.onNext(t);
		}
	}
	
	public final void sendError(Throwable t) {
		Subscriber<? super T> out = this.out;
		if (out!=null) {
			out.onError(t);
			cancel();
		}
	}

	public final void sendComplete() {
		Subscriber<? super T> out = this.out;
		if (out!=null) {
			out.onComplete();
			cancel();
		}
	}

	@Override
	public final void cancel() {
		this.out = null;
		onCancel();
	}

	@Override
	public final void request(long n) {
		if (n>0) {
			onRequest(n);
		} else {
			illegalArgument("Rule 3.9: request parameter MUST be positive");
		}
	}

	protected abstract void onCancel();

	protected abstract void onRequest(long n);
}
