package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Represents a subscription created by a Flux<OUT> to respond to a 'subscribe' request. 
 * The subscription takes data from a Flux<IN> and transforms the data stream
 * internally before presenting it to Flux<OUT> subscriber.
 * <p>
 * Because it takes in data from Flux<IN> it implements Subscriber<IN> (which allows it to attach to Flux<IN> to
 * receive data from it. 
 * <p>
 * It also implements {@link Subscription} to be able to fulfill the role of tracking the Flux<OUT> 
 * subscription state, and present transformed stream to the subscriber on Flux<OUT>.
 */
public abstract class TransformerSubscription<IN, OUT> extends BaseSubscription<OUT> implements Subscriber<IN> {

	protected final Publisher<? extends IN> in;
	private Subscription inSub;
	
	public TransformerSubscription(Publisher<? extends IN> in, Subscriber<? super OUT> out) {
		super(out);
		this.in = in;
	}
	
	@Override
	public void onSubscribe(Subscription inSub) {
		this.inSub = inSub;
		out.onSubscribe(this);
	}
	
	@Override
	public void onNext(IN t) {
		Subscriber<? super OUT> out = this.out;
		if (out!=null) {
			out.onNext(transform(t));
		}
	}

	protected abstract OUT transform(IN t);

	@Override
	public void onError(Throwable t) {
		Subscriber<? super OUT> out = this.out;
		if (out!=null) {
			out.onError(t);
			cancel();
		}
	}

	@Override
	public void onComplete() {
		Subscriber<? super OUT> out = this.out;
		if (out!=null) {
			out.onComplete();
			cancel();
		}
	}
	
	@Override
	protected void onRequest(long n) {
		Subscription inSub = this.inSub;
		if (inSub!=null) {
			inSub.request(n);
		}
	}

	@Override
	protected void onCancel() {
		Subscription inSub = this.inSub;
		if (inSub!=null) {
			inSub.cancel();
			this.inSub = null;
		}
		this.out = null;
	}
}
