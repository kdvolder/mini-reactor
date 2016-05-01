package com.github.kdvolder.minireactor.tests;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import com.github.kdvolder.minireactor.Flux;

public class FluxTest extends PublisherVerification<Integer> {

	public FluxTest() {
		super(new TestEnvironment());
	}

	@Override
	public Publisher<Integer> createPublisher(long elements) {
		return Flux.range(0, (int)elements);
	}

	@Override
	public Publisher<Integer> createFailedPublisher() {
		return Flux.error(new Exception("fast fail!"));
	}

}
