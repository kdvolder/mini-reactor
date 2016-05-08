package com.github.kdvolder.minireactor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import com.github.kdvolder.minireactor.internal.IdentityTransformerFlux;
import com.github.kdvolder.minireactor.internal.IdentityTransformerSubscription;
import com.github.kdvolder.minireactor.internal.TransformerSubscription;

public class LogFlux<T> extends IdentityTransformerFlux<T> {

	private String prefix;

	public LogFlux(Publisher<T> in, String prefix) {
		super(in);
		this.prefix = prefix;
	}
	
	@Override
	protected TransformerSubscription<T, T> createSubscription(Subscriber<? super T> out) {
		return new IdentityTransformerSubscription<T>(in, out) {

//			@Override
//			public void onSubscribe(Subscription inSub) {
//				print()
//				super.onSubscribe(inSub);
//			}
			
			@Override
			public void onNext(T t) {
				log("onNext", t);
				super.onNext(t);
			}

			private void log(String string, Object t) {
				System.out.println(prefix+": "+string+paramStr(t));
			}

			private String paramStr(Object t) {
				if (t!=null) {
					return "("+t+")";
				}
				return "";
			}

			@Override
			public void onError(Throwable t) {
				log("onError", t.getMessage());
				super.onError(t);
			}

			@Override
			public void onComplete() {
				log("onComplete", null);
				// TODO Auto-generated method stub
				super.onComplete();
			}

			@Override
			protected void onRequest(long n) {
				log("request", n);
				// TODO Auto-generated method stub
				super.onRequest(n);
			}

			@Override
			protected void onCancel() {
				log("cancel", null);
				// TODO Auto-generated method stub
				super.onCancel();
			}
			
			
			
		};
	}

}
