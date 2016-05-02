package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public abstract class TransformerFlux<IN, OUT> extends Flux<OUT> {

	protected final Publisher<IN> in;
	
	public TransformerFlux(Publisher<IN> in) {
		this.in = in;
	}
	
	@Override
	public final void subscribe(Subscriber<? super OUT> out) {
		in.subscribe(createSubscription(out));
	}

	protected abstract TransformerSubscription<IN, OUT> createSubscription(Subscriber<? super OUT> out);

}
