package org.aisin.sipphone.tools;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class CharTools {
	// 计算字符串占的长度
	public static double getStringLength(String str) {
		NumberFormat format = new DecimalFormat("#0.0");
		double length = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) <= 255) {
				length += 0.5;
			} else {
				length += 1;
			}
		}
		return Double.valueOf(format.format(length));
	}
}
