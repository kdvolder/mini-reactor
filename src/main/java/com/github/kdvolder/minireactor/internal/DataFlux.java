package com.github.kdvolder.minireactor.internal;

import java.util.Iterator;

import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.Flux;

public class DataFlux<T> extends Flux<T> {
	
	private Iterable<? extends T> data;

	public DataFlux(Iterable<? extends T> data) {
		this.data = data;
	}

	@Override
	public void subscribe(Subscriber<? super T> s) {
		s.onSubscribe(new DataSubscription<T>(s) {
			
			Iterator<? extends T> iter = data.iterator();

			@Override
			protected boolean hasNext() throws Exception {
				return iter.hasNext();
			}

			@Override
			protected T next() throws Exception {
				return iter.next();
			}
		});
	}

}
