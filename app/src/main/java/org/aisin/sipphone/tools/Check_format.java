package org.aisin.sipphone.tools;

import java.util.ArrayList;

public class Check_format {

	private static ArrayList<String> specialPhonenum;

	public static boolean Check_mobilePhone(String mobile) {
		if (mobile.trim().length() != 11) {
			return false;
		}
		return mobile.trim().matches("[0-9]*");
	}

	public static boolean Check_num(String num) {
		return num.trim().matches("[0-9]*");
	}

	public static boolean check_password4regset(String password) {
		return password.trim().matches("[0-9a-zA-Z]*");
	}

	public static boolean check_ABC(String password) {
		return password.trim().matches("[a-zA-Z]*");
	}

	public static boolean check_SPnum(String phonenum) {// 检测特殊号码
		for (String strstart : getspecialPhonenums()) {
			if (phonenum.startsWith(strstart)) {
				return false;
			}
		}
		return true;
	}

	private static ArrayList<String> getspecialPhonenums() {
		if (specialPhonenum != null) {
			return specialPhonenum;
		}
		specialPhonenum = new ArrayList<String>();
		specialPhonenum.add("200");
		specialPhonenum.add("300");
		specialPhonenum.add("400");
		specialPhonenum.add("600");
		specialPhonenum.add("700");
		specialPhonenum.add("800");
		specialPhonenum.add("10");
		specialPhonenum.add("11");
		specialPhonenum.add("12");
		return specialPhonenum;
	}
}
