//package com.github.kdvolder.minireactor.tests;
//
//import org.reactivestreams.Publisher;
//import org.reactivestreams.tck.PublisherVerification;
//import org.reactivestreams.tck.TestEnvironment;
//
//import com.github.kdvolder.minireactor.Flux;
//
//public class EmptyFluxTest extends PublisherVerification<Integer> {
//
//	public EmptyFluxTest() {
//		super(new TestEnvironment(true));
//	}
//
//	@Override
//	public Publisher<Integer> createPublisher(long elements) {
//		return Flux.empty();
//	}
//	
//	@Override
//	public long maxElementsFromPublisher() {
//		return 0;
//	}
//
//	@Override
//	public Publisher<Integer> createFailedPublisher() {
//		return Flux.error(new Exception("failed"));
//	}
//
//}
