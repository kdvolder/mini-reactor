package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class ErrorFlux<T> extends Flux<T> {

	private final Throwable error;

	public ErrorFlux(Throwable error) {
		this.error = error;
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		EmptySubsciption<T> sub = new EmptySubsciption<T>(s);
		s.onSubscribe(sub);
		sub.onError(error);
	}

}
