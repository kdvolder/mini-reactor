package com.github.kdvolder.minireactor;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.github.kdvolder.minireactor.SharedList.ElementHandle;
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
	
	final private AtomicReference<Flux<T>> in;
	private Subscription inSub = null;
	
	private CachedStream cache = new CachedStream();
	
	public InfiniteCacheFlux(Flux<T> in) {
		this.in = new AtomicReference<Flux<T>>(in);
	}

	private SharedList<MySubscription> subscriptions = new SharedList<>();

	private class MySubscription extends BaseSubscription<T> {
		private ElementHandle removeHandle;

		protected MySubscription(Subscriber<? super T> out) {
			super(out);
			removeHandle = subscriptions.add(this);
			out.onSubscribe(this);
		}

		long requested = 0;
		long next;
		AtomicBoolean busy = new AtomicBoolean(false);

		@Override
		protected void onCancel() {
			if (removeHandle!=null) {
				removeHandle.remove();
				removeHandle = null;
			}
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
		Flux<T> in = this.in.getAndSet(null);
		if (in!=null) {
			//this must be the first request.
			in.subscribe(this.cache);
		} else {
			//any
		}
		
		//TODO: this can be called on any subscriber's 'request' thread 
		//as well as on our incoming subscription 'publishing' thread. 
		//But the code below almost certainly is not threadsafe.
		
		//To solve this it is probably best split this up into two methods
		//The stuff above and the stuff below serve different purpose and
		//probably should not be called always together.
		//E.g. 'in.subscribe' only happens first time we get a request
		//
		
		Subscription inSub = this.inSub;
		if (inSub!=null) {
			long maxRequested = 0;
			for (MySubscription sub : subscriptions) {
				maxRequested = Math.max(maxRequested, sub.requested);
			}
			long alreadyGot = cache.size();
			if (alreadyGot<maxRequested && 
				requested < maxRequested
			) {
				requested+=1;
				inSub.request(1);
			}
			
			long extra = maxRequested-requested;
			if (alreadyGot==requested) {
				if (extra>0) {
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
