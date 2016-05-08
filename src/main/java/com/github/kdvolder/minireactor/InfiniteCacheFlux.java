package com.github.kdvolder.minireactor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.github.kdvolder.minireactor.internal.BaseSubscription;

/**
 * A Flux that records the signals from another flux and replays them to 
 * each of its own subscribers.
 */
public class InfiniteCacheFlux<T> extends Flux<T> {

	/**
	 * How much data we have already requested from our input stream.
	 */
	private long requested = 0;
	
	private Flux<T> in;
	private Subscription inSub = null;
	
	private CachedStream cache = new CachedStream();
	
	public InfiniteCacheFlux(Flux<T> in) {
		this.in = in;
	}

	private Set<MySubscription> subscriptions = new HashSet<>();

	private class MySubscription extends BaseSubscription<T> {
		protected MySubscription(Subscriber<? super T> out) {
			super(out);
			subscriptions.add(this);
			out.onSubscribe(this);
		}

		long requested = 0;
		long next;
		AtomicBoolean busy = new AtomicBoolean(false);

		@Override
		protected void onCancel() {
			subscriptions.remove(this);
		}

		@Override
		protected void onRequest(long n) {
			requested+=n;
			satisfyDemand();
			requestFromIn();
		}

		private void satisfyDemand() {
			if (busy.compareAndSet(false, true)) {
				if (out!=null) {
					while (next<cache.size()) {
						sendNext(cache.values.get((int)(next++)));
					}
					if (cache.complete) {
						sendComplete();
					} else if (cache.error!=null) {
						sendError(cache.error);
					}
				}
				busy.set(false);
			}
		}
	}

	private class CachedStream implements Subscriber<T> {
		final ArrayList<T> values = new ArrayList<>();
		boolean complete = false;
		Throwable error;
		public long size() {
			return values.size();
		}
		public void add(T t) {
			values.add(t);
		}
		
		@Override
		public void onSubscribe(Subscription s) {
			inSub = s;
			requestFromIn();
		}

		@Override
		public void onNext(T t) {
			cache.add(t);
			requestFromIn();
			sendToAllSubscriptions();
		}

		@Override
		public void onError(Throwable t) {
			if (error==null) {
				error = t;
				sendToAllSubscriptions();
			}
			cancel();
		}

		@Override
		public void onComplete() {
			complete = true;
			sendToAllSubscriptions();
			cancel();
		}
		private void cancel() {
			inSub = null;
		}
		@Override
		public String toString() {
			return "CachedStream [\n   values=" + values + ",\n   complete=" + complete + ",\n   error=" + error + "]";
		}
	}

	private void requestFromIn() {
		System.out.println("requestFromIn");
		if (in!=null || inSub!=null) {
			if (inSub==null) {
				System.out.println("subscribe to in");
				in.subscribe(this.cache);
				in = null;
			}
			long maxRequested = 0;
			for (MySubscription sub : subscriptions) {
				maxRequested = Math.max(maxRequested, sub.requested);
			}
			System.out.println("maxRequested: "+maxRequested);
			long extra = maxRequested-requested;
			long alreadyGot = cache.size();
			if (alreadyGot==requested) {
				System.out.println("extra: "+extra);
				if (extra>0) {
					System.out.println("requesting from in: "+extra);
					requested+=1;
					inSub.request(1);
				}
			}
		}
	}

	private void sendToAllSubscriptions() {
		for (MySubscription sub : subscriptions) {
			sub.satisfyDemand();
		}
	}
	
	@Override
	public void subscribe(Subscriber<? super T> in) {
		new MySubscription(in); 
	}
	
}
