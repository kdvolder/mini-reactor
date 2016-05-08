package com.github.kdvolder.minireactor.tests;

import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestBracketer extends TestListenerAdapter {
	
	@Override
	public void onTestStart(ITestResult result) {
		super.onTestStart(result);
		ITestNGMethod m = result.getMethod();
		String name = m.getMethodName();
		System.out.println(">>> "+name);
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		super.onTestFailure(result);
		ITestNGMethod m = result.getMethod();
		String name = m.getMethodName();
		System.out.println("<<< "+name+": FAILED");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		super.onTestSuccess(result);
		ITestNGMethod m = result.getMethod();
		String name = m.getMethodName();
		System.out.println("<<< "+name+": success!");
	}

}
