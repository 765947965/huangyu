package org.aisin.sipphone.tools;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class UserInfo_db {
	public static synchronized UserInfo getUserInfo(Context context) {
		SharedPreferences settingsPreference = SharedPreferencesTools
				.getSharedPreferences_UserInfo(context);
		String bding_phone = settingsPreference.getString(
				SharedPreferencesTools.SPF_USER_BD_PHONE, "");
		String phone = settingsPreference.getString(
				SharedPreferencesTools.SPF_USER_PHONE, "");
		String pwd = settingsPreference.getString(
				SharedPreferencesTools.SPF_USER_PWD, "");
		String uid = settingsPreference.getString(
				SharedPreferencesTools.SPF_USER_UID, "");
		String agent_id = settingsPreference.getString(
				SharedPreferencesTools.SPF_USER_AGENT_ID, "");
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = "";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		if (phone.length() == 0 || pwd.length() == 0 || uid.length() == 0) {
			return null;
		} else {
			UserInfo userinfo = new UserInfo();
			userinfo.setBding_phone(bding_phone);
			userinfo.setPhone(phone);
			userinfo.setPwd(pwd);
			userinfo.setUid(uid);
			userinfo.setAgent_id(agent_id);
			userinfo.setV(version);
			return userinfo;
		}

	}

	public static synchronized void SaveUserInfo(Context context,
			String bding_phone, String phone, String pwd, String uid) {
		SharedPreferences settingsPreference = SharedPreferencesTools
				.getSharedPreferences_UserInfo(context);
		Editor editor = settingsPreference.edit();
		editor.putString(SharedPreferencesTools.SPF_USER_BD_PHONE, bding_phone);
		editor.putString(SharedPreferencesTools.SPF_USER_PHONE, phone);
		editor.putString(SharedPreferencesTools.SPF_USER_PWD, pwd);
		editor.putString(SharedPreferencesTools.SPF_USER_UID, uid);
		editor.putString(SharedPreferencesTools.SPF_USER_AGENT_ID,
				getagent_id(context));
		editor.commit();
	}

	public static String getagent_id(Context context) {
		String devalue = Common.getValueForPro(context.getResources()
				.openRawResource(R.raw.aicall), "agent_id");
		SharedPreferences spf = SharedPreferencesTools
				.getSharedPreferences_4agent_id(context);
		if (spf == null) {
			return devalue;
		} else {
			return spf.getString(SharedPreferencesTools.agent_id, devalue);
		}
	}
}
