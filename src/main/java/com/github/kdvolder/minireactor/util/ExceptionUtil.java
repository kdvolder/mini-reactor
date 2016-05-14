package com.github.kdvolder.minireactor.util;

public class ExceptionUtil {

	public static String getMessage(Throwable t) {
		String msg = t.getMessage();
		if (msg!=null && !"".equals(msg.trim())) {
			return msg;
		} else {
			return t.getClass().getName();
		}
		
	}

}
