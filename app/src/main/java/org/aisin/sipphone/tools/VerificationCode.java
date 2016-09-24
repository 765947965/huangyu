package org.aisin.sipphone.tools;

import java.util.Random;

public class VerificationCode {
	public static Random random = new Random();

	// 获取随机4位验证码
	public static String getCode() {
		StringBuilder strb = new StringBuilder();
		strb.append(random.nextInt(10));
		strb.append(random.nextInt(10));
		strb.append(random.nextInt(10));
		strb.append(random.nextInt(10));
		return strb.toString();
	}

	// 获取随机2位验证码
	public static String getCode2() {
		StringBuilder strb = new StringBuilder();
		strb.append(random.nextInt(10));
		strb.append(random.nextInt(10));
		return strb.toString();
	}

}
