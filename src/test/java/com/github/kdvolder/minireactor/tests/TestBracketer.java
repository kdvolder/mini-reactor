package com.github.kdvolder.minireactor.tests;

import org.testng.ITestClass;
import org.testng.ITestNGListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.annotations.Listeners;

public class TestBracketer extends TestListenerAdapter {

	private boolean isEnabled(ITestResult result) {
		Class<?> klass = result.getMethod().getTestClass().getRealClass();
		Listeners listeners = klass.getAnnotation(Listeners.class);
		if (listeners!=null) {
			for (Class<? extends ITestNGListener> l : listeners.value()) {
				if (l==TestBracketer.class) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onTestStart(ITestResult result) {
		super.onTestStart(result);
		if (isEnabled(result)) {
			ITestNGMethod m = result.getMethod();
			String name = m.getMethodName();
			System.out.println(">>> "+name);
		}
	}
	
	@Override
	public void onTestFailure(ITestResult result) {
		super.onTestFailure(result);
		if (isEnabled(result)) {
			ITestNGMethod m = result.getMethod();
			String name = m.getMethodName();
			System.out.println("<<< "+name+": FAILED");
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		super.onTestSuccess(result);
		if (isEnabled(result)) {
			ITestNGMethod m = result.getMethod();
			String name = m.getMethodName();
			System.out.println("<<< "+name+": success!");
		}
	}

}

