package com.github.kdvolder.minireactor.util;

public class Assert {

	public static void isLegal(boolean test) {
		if (!test) {
			throw new AssertionError();
		}
	}

}
