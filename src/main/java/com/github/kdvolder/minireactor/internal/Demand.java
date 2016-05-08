package com.github.kdvolder.minireactor.internal;

import com.github.kdvolder.minireactor.util.Assert;

public class Demand {
	
	private long demand = 0;
	
	public Demand(long initial) {
		this.demand = initial;
	}
	
	@Override
	public String toString() {
		return ""+demand;
	}

	public boolean isPositive() {
		return demand>0;
	}

	public void decrement() {
		Assert.isLegal(isPositive());
		demand --;
	}

	public void increment(long n) {
		Assert.isLegal(n>=0);
		demand = add(demand, n);
	}

	public static long add(long n1, long n2) {
		try {
			return Math.addExact(n1, n2);
		} catch (ArithmeticException e) {
			return Long.MAX_VALUE;
		}
	}

}
