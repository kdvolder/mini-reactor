import com.github.kdvolder.minireactor.Flux;

public class Main {

	public static void main(String[] args) {
		Flux.range(0, 5)
		.log("start")
		.map((x) -> x+1)
		.log("increment")
		.subscribe(System.out::println);
	}
	
}
