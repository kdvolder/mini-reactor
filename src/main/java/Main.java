import com.github.kdvolder.minireactor.Flux;

/**
 * Class with some ad-hoc testing/sample code.
 * 
 * @author Kris De Volder
 */
public class Main {

	public static void main(String[] args) {
		//Edit line below to pick one of the numbered 'main' methods to run.
		main3();
	}

	private static void main3() {
		Flux.range(0, 1000)
		.drop(14)
		.take(4)
		.consume(System.out::println);
	}

	public static void main1() {
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
	
	public static void main2() {
		Flux<Integer> num = Flux.range(0, 10);
		num.consume((i) -> System.out.println(i));
	}
	
}
