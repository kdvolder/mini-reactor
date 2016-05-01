package com.github.kdvolder.minireactor.util;

public interface ExceptionalFunction<A, R> {
	
	R apply(A a) throws Exception;

}
