package org.aisin.sipphone.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Common {
	public static final String SIGN_KEY = "&%&aicall$#$";

	public static synchronized String getValueForPro(InputStream in, String key) {
		Properties pro = new Properties();
		try {
			pro.load(in);
			return pro.getProperty(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
