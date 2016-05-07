package com.github.kdvolder.minireactor.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Random;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.github.kdvolder.minireactor.Flux;
import com.github.kdvolder.minireactor.internal.BasicSubscriber;
import com.google.common.io.CharStreams;

public class FluxTest {

	/**
	 * When this is set to true, then a 'verify_output' which fails to find the
	 * the expected output resource to compare with will save the current output
	 * (so it can be compared with on the next test-run).
	 */
	private static final boolean RECORD_OUTPUT = true;
	
	/**
	 * When set to true the output sent via 'println' is also shown on System.out.
	 * If set to false it is only recorded silently for comparison to the expected
	 * output.
	 */
	private static final boolean SHOW_OUTPUT = true;

	/**
	 * A fixed seed so tests involving randomness are predictable.
	 */
	protected static final long SEED = 678; // make tests involving random, predictable.
	
	private StringBuilder out = new StringBuilder();
	
	private void println(Object msg) {
		if (SHOW_OUTPUT) {
			System.out.println("%out: "+msg);
		}
		out.append(msg+"\n");
	}
	
	private void verify_output(String testKey) throws Exception {
		String actualOutput = out.toString();
		String testFileName = testKey+".out.txt";
		try {
			InputStream is = this.getClass().getResourceAsStream(testFileName);
			assertNotNull(is, "resource at 'src/test/resources/"+testFileName+"'");
			String expectedOutput = CharStreams.toString(new InputStreamReader(is, "utf8"));
			assertEquals(expectedOutput, actualOutput);
		} catch (AssertionError e) {
			if (!RECORD_OUTPUT) {
				throw e;
			}
			URL myLocation = this.getClass().getResource(this.getClass().getSimpleName()+".class");
			File myFile = new File(myLocation.toURI());
			assertTrue(myFile.toString().contains("/target/test-classes/"));
			File resourcesDir = new File(myFile.getParentFile().toString().replace("/target/test-classes/", "/src/test/resources/"));
			resourcesDir.mkdirs();
			File dataFile = new File(resourcesDir, testFileName);
			try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(dataFile), "utf8")) {
				out.write(actualOutput);
			}
		}
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
		verify_output("infinite_cache");
	}

	void main5() {
		Flux<Integer> diceRolls = diceRoller();

		System.out.println("Rolling the dice:");
		diceRolls
		.take(5)
		.consume(System.out::println);
		
		System.out.println("Rolling the dice again:");
		diceRolls
		.take(5)
		.subscribe(new BasicSubscriber<Integer>() {
			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}
			@Override
			public void onError(Throwable t) {
				System.out.println("!!!ERROR: "+t.getMessage());
			}
			@Override
			public void onComplete() {
				System.out.println("===============");
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
		return Flux.fromIterable(() -> new Iterator<Integer>() {
			Random generator = new Random(SEED);

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


	@Test
	public void test() {
		
	}
	
}
