package com.github.kdvolder.minireactor.util;

import java.util.HashSet;
import java.util.Set;

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
