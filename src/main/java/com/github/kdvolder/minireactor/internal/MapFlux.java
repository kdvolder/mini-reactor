package com.github.kdvolder.minireactor.internal;

import java.util.function.Function;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class MapFlux<IN, OUT> extends Flux<OUT> {
	
	private final Flux<IN> in;
	private final Function<IN, OUT> f;

	public MapFlux(Flux<IN> in, Function<IN, OUT> f) {
		this.in = in;
		this.f = f;
	}

	@Override
	public void subscribe(Subscriber<? super OUT> out) {
		in.subscribe(new TransformerSubscription<IN, OUT>(in, out) {
			@Override
			protected OUT transform(IN t) {
				return f.apply(t);
			}
		});
	}

}
