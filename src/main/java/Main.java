import com.github.kdvolder.minireactor.Flux;

/**
 * Class with some ad-hoc testing/sample code.
 * 
 * @author Kris De Volder
 */
public class Main {

//	public static void main(String[] args) {
//		Flux<Integer> empty = Flux.empty();
//		
//		empty.subscribe(new Subscriber<Integer>() {
//
//			@Override
//			public void onSubscribe(Subscription s) {
//				System.out.println("onSub: "+s);
//			}
//
//			@Override
//			public void onNext(Integer t) {
//				System.out.println("onNxt: "+t);
//			}
//
//			@Override
//			public void onError(Throwable t) {
//				System.out.println("onErr: "+t);
//			}
//
//			@Override
//			public void onComplete() {
//				System.out.println("onCmp!");
//			}
//		});
//	}
//	
	public static void main(String[] args) {
		Flux<Integer> num = 
				Flux.range(0, 1000)
				.filter((x) -> x%3==0)
				.take(5)
				;
		num.consume((x) -> {
			System.out.println(x);
		});
	}
	
//	public static void main(String[] args) {
//		Flux<Integer> num = Flux.range(0, 10);
//		num.consume((i) -> System.out.println(i));
//	}
	
}
