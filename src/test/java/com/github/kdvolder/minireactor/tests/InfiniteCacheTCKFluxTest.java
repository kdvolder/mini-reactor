package com.github.kdvolder.minireactor.tests;

import java.util.Iterator;
import java.util.Random;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import com.github.kdvolder.minireactor.Flux;

public class InfiniteCacheTCKFluxTest extends PublisherVerification<Integer> {

	public InfiniteCacheTCKFluxTest() {
		super(new TestEnvironment());
	}

	@Override
	public Publisher<Integer> createPublisher(long elements) {
		return Flux.range(0, Integer.MAX_VALUE)
		.log("DATA ")
		.cacheAll()
		.log("CACH")
		.take(elements)
		.log("TAKE")
		;
	}

	@Override
	public Publisher<Integer> createFailedPublisher() {
		return null;
	}

}
