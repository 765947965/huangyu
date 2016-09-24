package org.aisin.sipphone.tools;

public class Misc {

	// 加密
	public static String cryptDataByPwd(String data) {
		if (data == null || data.length() == 0)
			return null;
		int i, dataLen, value;
		char[] chs = new char[data.length()];

		dataLen = data.length();
		if (dataLen > 512)
			dataLen = 512;

		for (i = 0; i < dataLen; i++) {
			value = data.charAt(i);
			if ((value > 0x60) && (value < 0x7B)) {
				value = value - 0x20;
				value = 0x5A - value + 0x41;
			} else if ((value > 0x40) && (value < 0x5B)) {
				value = value + 0x20;
				value = 0x7A - value + 0x61;
			} else if ((value >= 0x30) && (value <= 0x34)) {
				value = value + 0x05;
			} else if ((value >= 0x35) && (value <= 0x39)) {
				value = value - 0x05;
			}
			chs[i] = (char) value;
		}
		return new String(chs);
	}
}
