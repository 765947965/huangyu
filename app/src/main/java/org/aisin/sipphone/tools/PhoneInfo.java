package org.aisin.sipphone.tools;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.DisplayMetrics;

public class PhoneInfo {

	public static String cpuABI; // cpu信息
	public static String brand; // 手机品牌
	public static String phoneModel; // 手机型号
	public static int SDKVersion; // SDK版本
	public static String OSVersion; // 系统版本号
	public static int width;
	public static int height;
	public static float density; // 屏幕密度

	// 用来存储设备信息
	public static Map<String, String> infos = new HashMap<String, String>();

	public PhoneInfo(Activity activity) {
		cpuABI = android.os.Build.CPU_ABI;
		brand = android.os.Build.BRAND;
		phoneModel = android.os.Build.MODEL;
		SDKVersion = android.os.Build.VERSION.SDK_INT;
		OSVersion = android.os.Build.VERSION.RELEASE;
		collectDeviceInfo(activity);
		this.getScreenPixed(activity);
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}

			Field[] fields = Build.class.getDeclaredFields();
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					infos.put(field.getName(), field.get(null).toString());
				} catch (Exception e) {
				}
			}
		} catch (NameNotFoundException e) {
		}
	}

	public String getCPUAPI() {
		return cpuABI;
	}

	public String getBrand() {
		return brand;
	}

	public String getPhoneModel() {
		return phoneModel;
	}

	public int getSDKVersion() {
		return SDKVersion;
	}

	public String getOSVersion() {
		return OSVersion;
	}

	public void getScreenPixed(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		this.width = metrics.widthPixels;
		this.height = metrics.heightPixels;
		this.density = metrics.density;
	}

	public static int whichScreen() {
		if (width < 480) {
			return 1;
		} else if (width >= 480 && width < 720) {
			return 2;
		} else if (width >= 720 && width < 1080) {
			return 3;
		} else {
			return 4;
		}
	}
}
