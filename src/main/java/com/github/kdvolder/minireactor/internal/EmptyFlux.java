package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class EmptyFlux<T> extends Flux<T> {
	
	@SuppressWarnings("rawtypes")
	public static final EmptyFlux THE = new EmptyFlux<>();
	
	private EmptyFlux() {
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		EmptySubsciption<T> sub = new EmptySubsciption<>(s);
		s.onSubscribe(sub);
		//sub.onComplete();
	}

}
