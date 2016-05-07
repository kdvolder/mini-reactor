package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class ErrorFlux<T> extends Flux<T> {

	private final Throwable error;

	public ErrorFlux(Throwable error) {
		this.error = error;
	}

	@Override
	public void subscribe(Subscriber<? super T> in) {
		BaseSubscription<T> sub = new BaseSubscription<T>(in) {
			@Override
			protected void onCancel() {
			}

			@Override
			protected void onRequest(long n) {
			}
		};
		in.onSubscribe(sub);
		sub.sendError(error);
	}

}
