package org.aisin.sipphone.tools;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

public class InitIsDoubleTelephone {

	public static boolean GetinitIsDoubleTelephone(Context context) {
		boolean isTK = true;
		try {
			TelephonyManager.class.getMethod("getSimStateGemini",
					new Class[] { int.class });
		} catch (Exception e) {
			isTK = false;
		} catch (Error e) {
			isTK = false;
		}
		return isTK;// 为true为双卡 false 单卡
	}

	@SuppressLint("UseValueOf")
	public static int GetinitIsDoubleTelephoneNUM(Context context) {//0单卡， 1 卡1可用， 2 卡2可用，3双卡可用,4 都不可用
		Method method = null;
		Object result_0 = null;
		Object result_1 = null;
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			// 只要在反射getSimStateGemini 这个函数时报了错就是单卡手机（这是我自己的经验，不一定全正确）
			method = TelephonyManager.class.getMethod("getSimStateGemini",
					new Class[] { int.class });
			// 获取SIM卡1
			result_0 = method.invoke(tm, new Object[] { new Integer(0) });
			// 获取SIM卡1
			result_1 = method.invoke(tm, new Object[] { new Integer(1) });
		} catch (Exception e) {
			return 0;
		} catch (Error e) {
			return 0;
		}
		// 如下判断哪个卡可用.双卡都可以用
		if (result_0.toString().equals("5") && result_1.toString().equals("5")) {
			return 3;
		} else if (!result_0.toString().equals("5")
				&& result_1.toString().equals("5")) {// 卡二可用
			return 2;
		} else if (result_0.toString().equals("5")
				&& !result_1.toString().equals("5")) {// 卡一可用
			return 1;
		} else {// 两个卡都不可用(飞行模式会出现这种种情况)
			return 4;
		}

	}
}
