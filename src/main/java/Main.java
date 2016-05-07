import java.util.Iterator;
import java.util.Random;

import com.github.kdvolder.minireactor.Flux;
import com.github.kdvolder.minireactor.internal.BasicSubscriber;

/**
 * Class with some ad-hoc testing/sample code.
 * 
 * @author Kris De Volder
 */
public class Main {

	public static void main(String[] args) {
		//Edit line below to pick one of the numbered 'main' methods to run.
		main6();
	}

	static void main6() {
		Flux<Integer> diceRolls = diceRoller().cache();

		System.out.println("Rolling and RECORDING 5 dice");
		diceRolls
		.take(5)
		.consume(System.out::println);
		
		System.out.println("Replaying 5 Recorded dice and getting 5 more rolls:");
		diceRolls
		.take(10)
		.subscribe(new BasicSubscriber<Integer>() {
			int count = 0;
			@Override
			public void onNext(Integer t) {
				System.out.println((++count)+": "+t);
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

	static void main5() {
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
	private static Flux<Integer> diceRoller() {
		return Flux.fromIterable(() -> new Iterator<Integer>() {
			Random generator = new Random();

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
	
	private static <T> T debug(String msg, T x) {
		System.out.println(msg+": "+x);
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
