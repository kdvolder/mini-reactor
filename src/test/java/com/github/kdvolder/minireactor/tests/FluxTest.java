package com.github.kdvolder.minireactor.tests;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Random;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.github.kdvolder.minireactor.Flux;
import com.github.kdvolder.minireactor.internal.BasicSubscriber;

@Listeners(TestBracketer.class)
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
	
	@AfterMethod
	public void verifyOutputCapture(ITestResult result) throws Exception {
		out.verify();
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
		.subscribe((roll) -> println(roll));
		
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
	}

	@Test
	public void hot_stream() throws Exception {
		Flux<Integer> diceRolls = diceRoller();

		println("Rolling the dice:");
		diceRolls
		.take(5)
		.subscribe(this::println);
		
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
				return generator.nextInt(100);
			}
		});
	}
	
	@Test
	public void map() throws Exception {
		Flux.of("hello", "goodbye", "cat", "dog", "hoho")
		.map((word) -> word.length())
		.subscribe((wordLen) -> println(wordLen));
	}

	@Test
	public void map_empty_stream() throws Exception {
		Flux.<String>empty()
		.map((word) -> word.length())
		.subscribe(
				(wordLen) 	-> println(wordLen),
				(error) 	-> println(error.getMessage()),
				() 			-> println("!!!!complete!!!!")
		);
	}

	@Test
	public void drop() throws Exception {
		Flux.range(0, 5)
		.drop(3)
		.subscribe(
				(x) -> println(x),
				(e) -> println(e.getMessage()),
				() 	-> println("!!!!complete!!!!")
		);
	}
	
	@Test
	public void drop_and_take() throws Exception {
		Flux.range(0, 1000)
		.drop(14)
		.take(4)
		.subscribe(
				(wordLen) 	-> println(wordLen),
				(error) 	-> println(error.getMessage()),
				() 			-> println("!!!!complete!!!!")
		);
	}

	@Test
	public void filter() throws Exception {
		Flux.range(0, 1000)
		.filter((x) -> x%3==0)
		.take(5)
		.take(10)
		.subscribe(
				(i) 	-> println(i),
				(error) -> println(error.getMessage()),
				() 		-> println("!!!!complete!!!!")
		);
	}
	
	@Test
	public void range() throws Exception {
		Flux.range(0, 10)
		.subscribe(
				(i) 	-> println(i),
				(error) -> println(error.getMessage()),
				() 		-> println("!!!!complete!!!!")
		);
	}

	@Test
	public void range_non_zero_start() throws Exception {
		Flux.range(5, 10)
		.subscribe(
				(i) 	-> println(i),
				(error) -> println(error.getMessage()),
				() 		-> println("!!!!complete!!!!")
		);
	}
	
}
