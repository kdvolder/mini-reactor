package com.github.kdvolder.minireactor;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kdvolder.minireactor.internal.IdentityTransformerFlux;
import com.github.kdvolder.minireactor.internal.IdentityTransformerSubscription;
import com.github.kdvolder.minireactor.internal.TransformerSubscription;
import com.github.kdvolder.minireactor.util.ExceptionUtil;

public class LogFlux<T> extends IdentityTransformerFlux<T> {

	private final Logger logger;

	public LogFlux(Publisher<T> in, String prefix) {
		super(in);
		this.logger = LoggerFactory.getLogger(prefix);
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
				logger.info("onNext ("+t+")");
				super.onNext(t);
			}

			@Override
			public void onError(Throwable t) {
				logger.info("onError " + ExceptionUtil.getMessage(t));
				super.onError(t);
			}

			@Override
			public void onComplete() {
				logger.info("onComplete");
				super.onComplete();
			}

			@Override
			protected void onRequest(long n) {
				logger.info("request "+n);
				super.onRequest(n);
			}

			@Override
			protected void onCancel() {
				logger.info("cancel");
				super.onCancel();
			}
		};
	}

}
