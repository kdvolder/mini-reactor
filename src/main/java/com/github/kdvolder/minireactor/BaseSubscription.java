package com.github.kdvolder.minireactor;

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
		onError(new IllegalArgumentException(s));
	}

	public void onError(Throwable err) {
		if (out!=null) {
			out.onError(err);
			cancel();
		}
	}

	public void onNext(T t) {
		if (out!=null) {
			out.onNext(t);
		}
	}

	public void onComplete() {
		if (out!=null) {
			out.onComplete();
			cancel();
		}
	}

	@Override
	public void cancel() {
		out = null;
	}

}
