package com.github.kdvolder.minireactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class IdentityTransformerSubscription<T> extends BaseSubscription<T> implements Subscriber<T> {
	
	Subscription inSubscription;

	IdentityTransformerSubscription(Subscriber<? super T> out) {
		super(out);
	}

	@Override
	public void request(long n) {
		Subscription inSubscription = this.inSubscription;
		if (inSubscription!=null) {
			inSubscription.request(n);
		}
	}
	
	@Override
	public void cancel() {
		super.cancel();
		Subscription inSubscription = this.inSubscription;
		if (inSubscription!=null) {
			inSubscription.cancel();
		}
	}
	
	///^^^^ subscription role ^^^
	///==========================
	///vvvv subscriber role  vvvv
	
	@Override
	public void onSubscribe(Subscription inSubscription) {
		if (out!=null) {
			this.inSubscription = inSubscription;
			out.onSubscribe(this);
		}
	}

	//Note: already inheriting suitable implementation of onNext, onError, onComplete from BaseSubscription
}