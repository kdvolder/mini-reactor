package com.github.kdvolder.minireactor.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.github.kdvolder.minireactor.Flux;
import com.github.kdvolder.minireactor.internal.BasicSubscriber;
import com.google.common.io.CharStreams;

public class FluxTest {

	/**
	 * A fixed seed so tests involving randomness are predictable.
	 */
	protected static final long SEED = 678; // make tests involving random, predictable.
	
	private OutputVerifier out;
	
	@BeforeMethod
	public void setupOutputCapture(Method m) {
		out = new OutputVerifier(m.getName());
		out.RECORD_OUTPUT = true;
	}
	
	private void println(Object m) {
		out.println(m);
	}
	
	@Test
	public void infinite_cache() throws Exception {
		Flux<Integer> diceRolls = diceRoller().cacheAll();

		println("Rolling and RECORDING 5 dice");
		diceRolls
		.take(5)
		.consume((roll) -> println(roll));
		
		println("Replaying 5 Recorded dice and getting 5 more rolls:");
		diceRolls
		.take(10)
		.subscribe(new BasicSubscriber<Integer>() {
			int count = 0;
			@Override
			public void onNext(Integer t) {
				println((++count)+": "+t);
			}
			@Override
			public void onError(Throwable t) {
				println("!!!ERROR: "+t.getMessage());
			}
			@Override
			public void onComplete() {
				println("!!!COMPLETE!!!");
			}
		});
		out.verify();
	}

	@Test
	public void hot_stream() throws Exception {
		Flux<Integer> diceRolls = diceRoller();

		println("Rolling the dice:");
		diceRolls
		.take(5)
		.consume(this::println);
		
		println("Rolling the dice again:");
		diceRolls
		.take(5)
		.subscribe(new BasicSubscriber<Integer>() {
			@Override
			public void onNext(Integer t) {
				println(t);
			}
			@Override
			public void onError(Throwable t) {
				println("!!!ERROR: "+t.getMessage());
			}
			@Override
			public void onComplete() {
				println("!!!COMPLETE!!!");
			}
		});
		
		out.verify();
	}

	/**
	 * Create an infinite stream of dice rolls.
	 * <p>
	 * Note that the dicerolls are generated on demand for each
	 * individual subscriber, so two subscribers will each
	 * observe a different sequence of values.
	 */
	private Flux<Integer> diceRoller() {
		Random generator = new Random(SEED);
		return Flux.fromIterable(() -> new Iterator<Integer>() {

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public Integer next() {
				return debug("roll", generator.nextInt(100));
			}

		});
	}
	
	private <T> T debug(String msg, T x) {
		//System.out.println(msg+": "+x);
		return x;
	}

	static void main4() {
		Flux.of("hello", "goodbye", "cat", "dog", "hoho")
		.map((word) -> word.length())
		.consume(System.out::println);
	}

	static void main3() {
		Flux.range(0, 1000)
		.drop(14)
		.take(4)
		.consume(System.out::println);
	}

	static void main1() {
		Flux<Integer> num = 
				Flux.range(0, 1000)
				.filter((x) -> x%3==0)
				.take(5)
				.take(10)
				;
		num.consume((x) -> {
			System.out.println(x);
		});
	}
	
	static void main2() {
		Flux<Integer> num = Flux.range(0, 10);
		num.consume((i) -> System.out.println(i));
	}

	
}
