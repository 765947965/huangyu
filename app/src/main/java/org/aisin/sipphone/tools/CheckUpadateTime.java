package org.aisin.sipphone.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.LodingActivity;
import org.aisin.sipphone.sqlitedb.User_data_Ts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class CheckUpadateTime {
	public static synchronized boolean CheckResult_4getadlist_UP_time(
			Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_getadlist_uptime_share(context);
		String updatetime = sft.getString(
				SharedPreferencesTools.SPF_getadlist_uptime_uptime, "0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	public static synchronized boolean CheckResult_4msglist_UP_time(
			Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_msglist_uptime_share(context);
		String updatetime = sft.getString(
				SharedPreferencesTools.SPF_msglist_uptime_UPTIME, "0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	public static synchronized boolean CheckResult_4AdvertisingEffectUpload(
			Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_AdvertisingEffectUploadTIME(context);
		String updatetime = sft.getString(SharedPreferencesTools.SPF_AEUT_TIME,
				"0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	public static synchronized void ReSetValue(Context context) {
		// 置msglist更新时间为0
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_msglist_uptime_share(context);
		sft.edit()
				.putString(SharedPreferencesTools.SPF_msglist_uptime_UPTIME,
						"0000").commit();
		// 置广告更新时间为0，
		SharedPreferences sft_2 = SharedPreferencesTools
				.getSharedPreferences_getadlist_uptime_share(context);
		sft_2.edit()
				.putString(SharedPreferencesTools.SPF_getadlist_uptime_uptime,
						"0000").commit();
		// 置每日签到红包红包请求日期为0
		SharedPreferencesTools.getSharedPreferences_4REDDaily(context).edit()
				.putString(SharedPreferencesTools.REDDaily_key, "0000")
				.commit();
		// 置开屏页更新日期为0
		SharedPreferencesTools.getSharedPreferences_4startpager(context).edit()
				.putString(SharedPreferencesTools.startpagerdate, "0000")
				.commit();
		// 置服务页的更新时间为0
		SharedPreferencesTools.getSharedPreferences_ServicePage(context).edit()
				.putString(SharedPreferencesTools.SERVICE_PAGE_DATE, "0000")
				.commit();
	}

	// 检查每日签到红包
	public static synchronized boolean CheckResult_4REDDaily(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4REDDaily(context);
		String updatetime = sft.getString(SharedPreferencesTools.REDDaily_key,
				"0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检测是否有新版本 有则返回true
	public static synchronized boolean CheckResult_4newAPP(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(context);
		// 得到程序版本
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = "";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String ver_gt = sft.getString(SharedPreferencesTools.upAPPVer, version);
		if (version.compareTo(ver_gt) < 0) {
			return true;
		} else {
			return false;
		}
	}

	// 检测是否需要展示引导页
	public static boolean CheckResult_4bootpager(Context context) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = "1.0";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		return SharedPreferencesTools.getSharedPreferences_4bootpager(context)
				.getBoolean(version, true);
	}

	// 检测是否需要更新动态开屏页数据
	public static boolean CheckResult_4start_pager(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4startpager(context);
		String updatetime = sft.getString(
				SharedPreferencesTools.startpagerdate, "0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检测是否需要更新服务页数据
	public static boolean CheckResult_4Serverpager(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_ServicePage(context);
		String updatetime = sft.getString(
				SharedPreferencesTools.SERVICE_PAGE_DATE, "0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检测今天是否联网成功检测了未拆有效红包数量
	public static boolean CheckResult_4Ckeckredhaves(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4RED_COUT(context);
		String updatetime = sft.getString(SharedPreferencesTools.REDDATA_dian,
				"0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检查今天是否要已经拉取过通讯录好友
	public static boolean CheckResult_4Ckeckupfriendstime(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4upfriends(context);
		String updatetime = sft.getString(SharedPreferencesTools.upfrendstime,
				"0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检查今天是否同步过服务器好友数据
	public static boolean CheckResult_4Ckeckupfriends_uptime(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4upfriends(context);
		String updatetime = sft.getString(
				SharedPreferencesTools.upfrendsuptime, "0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}

	// 检查今天是否通过后台服务检查了更新
	public static boolean CheckResult_4Ckeckupserver_uptime(Context context) {
		SharedPreferences sft = SharedPreferencesTools
				.getSharedPreferences_4UPSERVER(context);
		String updatetime = sft.getString(SharedPreferencesTools.CUPDATE,
				"0000");
		SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
		String date = sdf.format(new Date());
		return !updatetime.equals(date);// 需要更新返回真
	}
}
