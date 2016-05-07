package com.github.kdvolder.minireactor.tests;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import com.github.kdvolder.minireactor.Flux;

public class FluxTCKTest extends PublisherVerification<Integer> {

	public FluxTCKTest() {
		super(new TestEnvironment());
	}

	@Override
	public Publisher<Integer> createPublisher(long elements) {
		return Flux.range(0, (int)elements*3).filter((i) -> i%3==0);
	}

	@Override
	public Publisher<Integer> createFailedPublisher() {
		return Flux.error(new Exception("fast fail!"));
	}

}
