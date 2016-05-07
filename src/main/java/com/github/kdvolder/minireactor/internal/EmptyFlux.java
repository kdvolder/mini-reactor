package com.github.kdvolder.minireactor.internal;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class EmptyFlux<T> extends Flux<T> {
	
	@SuppressWarnings("rawtypes")
	public static final EmptyFlux THE = new EmptyFlux<>();
	
	private EmptyFlux() {
	}

	@Override
	public void subscribe(Subscriber<? super T> in) {
		BaseSubscription<T> sub = new BaseSubscription<T>(in) {
			@Override
			protected void onCancel() {
			}

			@Override
			protected void onRequest(long n) {
				in.onComplete();
			}
			
		};
		in.onSubscribe(sub);
	}

}
