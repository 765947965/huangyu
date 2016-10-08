package org.aisin.sipphone.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.aisin.sipphone.commong.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class URLTools {
	// 获取注册URL
	public static String GetHttpURL_4REG(Context context, String phonenumber,
			String pwd) {
		String[] keys = { "phone", "pwd", "agent_id", "v", "pv", "sign" };
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { phonenumber, UserInfo_db.getagent_id(context),
				version, "android", MD5.toMD5(phonenumber + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "reg" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取getadlist的URL
	public static String GetHttpURL_4Getadlist(Context context) {
		String[] keys = { "uid", "agent_id", "sign", "pv", "brand", "model",
				"v", "pictype", "brandname" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		if (userinfo == null)
			return null;
		String[] values = { userinfo.getUid(), userinfo.getAgent_id(),
				MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				userinfo.getPv(), PhoneInfo.brand, PhoneInfo.phoneModel,
				userinfo.getV(), PhoneInfo.whichScreen() + "",
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "config/getadlist" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取改绑手机的URL
	public static String GetHttpURL_4CAccount(Context context, String password,
			String newphone) {
		String[] keys = { "pwd", "old_phone", "new_phone", "sign" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { Misc.cryptDataByPwd(password.trim()),
				userinfo.getPhone(), newphone,
				MD5.toMD5(userinfo.getPhone() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "change_phone" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取得到密码的URL
	public static String GetHttpURL_4GetPassword(Context context,
			String newphone) {
		String[] keys = { "phone", "cmd", "sign" };
		String[] values = { newphone.trim(), "fdpwd",
				MD5.toMD5(newphone.trim() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "send_sms" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();

	}

	// 获取更改密码的URL
	public static String GetHttpURL_4ChangePassword(Context context,
			String oldpassowrd, String newPassowrd) {
		String[] keys = { "phone", "old_pwd", "new_pwd", "sign" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getPhone().trim(),
				Misc.cryptDataByPwd(oldpassowrd.trim()),
				Misc.cryptDataByPwd(newPassowrd.trim()),
				MD5.toMD5(userinfo.getPhone().trim() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "change_pwd" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取充值的URL
	public static String GetHttpURL_4Chongzhika(Context context,
			String paytype, String goodstype, String money, String cardno,
			String cardpwd) {
		String[] keys = { "uid", "src", "paytype", "goodstype", "money",
				"cardno", "cardpwd", "pv", "V", "sign" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getUid(), "20", paytype, goodstype, money,
				cardno, cardpwd, userinfo.getPv(), userinfo.getV(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.PAY + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();

	}

	// 获取发送验证码的URL
	public static String GetHttpURL_4YZM(Context context, String code) {
		String[] keys = { "phone", "message", "sign", "brandname" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getPhone(), "您好，本次操作的验证码为：" + code,
				MD5.toMD5(userinfo.getPhone().trim() + Common.SIGN_KEY),
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "send_changephone_authcode"
				+ "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();

	}

	// 获取查询账户的URL
	public static String GetHttpURL_4Accountquery_YE(Context context) {
		String[] keys = { "uid", "pwd", "sign" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getUid(),
				Misc.cryptDataByPwd(userinfo.getPwd().trim()),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.URL + "search_balance" + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取更新msglist的URL
	public static String GetHttpURL_4CheckUpdate(Context context) {
		String[] keys = { "pv", "v", "sc", "agent_id", "product", "account",
				"brandname" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getPv(), userinfo.getV(), "10240",
				userinfo.getAgent_id(), Constants.BrandName, userinfo.getUid(),
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.MSG_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取呼出电话的URL
	public static String GetHttpURL_4CallOut(Context context, String called) {
		String[] keys = { "uid", "pwd", "called", "echo", "pv", "v", "sign" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getUid(),
				Misc.cryptDataByPwd(userinfo.getPwd().trim()), called, "1",
				userinfo.getPv(), userinfo.getV(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.CALL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// -------------------------------V2接口-------------------------------
	// 获取注册验证码
	public static String GetHttpURL_4RegistGetCode_V2(Context context,
			String phone) {
		String[] keys = { "sn", "agent_id", "phone", "pv", "v", "sign",
				"brand", "model", "product", "brandname" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = "";
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (Exception e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				"android", version, MD5.toMD5(sn + phone + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.REG_GETCODE + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取注册URL
	public static String GetHttpURL_4REG_V2(Context context, String phone,
			String yzcode) {
		String[] keys = { "sn", "agent_id", "phone", "pv", "v", "sign",
				"brand", "model", "product", "authcode", "invitedby",
				"inviteflag", "brandname" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				"android", version, MD5.toMD5(sn + phone + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				yzcode, "", "0", Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.REG_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取登录URL
	public static String GetHttpURL_4LODING_V2(Context context, String phone,
			String pwd) {
		String[] keys = { "sn", "agent_id", "account", "pwd", "pv", "v",
				"sign", "brand", "model", "product", "netmode", "brandname" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		int netmode = Check_network.getNetworkClass(context);
		if (netmode == 4) {
			netmode = 3;
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				Misc.cryptDataByPwd(pwd.trim()), "android", version,
				MD5.toMD5(sn + phone + Common.SIGN_KEY), PhoneInfo.brand,
				PhoneInfo.phoneModel, Constants.BrandName, netmode + "",
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.Loding_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取改绑手机的URL
	public static String GetHttpURL_4CAccount_V2(Context context,
			String password, String newphone) {
		String[] keys = { "sn", "agent_id", "account", "pv", "v", "sign",
				"brand", "model", "product", "old_phone", "new_phone", "pwd" };
		String sn = VerificationCode.getCode2();
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { sn, userinfo.getAgent_id(), userinfo.getUid(),
				userinfo.getPv(), userinfo.getV(),
				MD5.toMD5(sn + userinfo.getUid() + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				userinfo.getPhone(), newphone,
				Misc.cryptDataByPwd(password.trim()) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.ChangPhone_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取得到密码的验证码的URL
	public static String GetHttpURL_4GetPassword_yzCode_V2(Context context,
			String phone) {
		String[] keys = { "sn", "agent_id", "account", "pv", "v", "sign",
				"brand", "model", "product", "brandname" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				"android", version, MD5.toMD5(sn + phone + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.GetPWD_YZCode_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();

	}

	// 获取得到密码的URL
	public static String GetHttpURL_4GetPassword_V2(Context context,
			String phone, String yzCode) {
		String[] keys = { "sn", "agent_id", "account", "pv", "v", "sign",
				"brand", "model", "product", "authcode" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				"android", version, MD5.toMD5(sn + phone + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				yzCode };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.GetPWD_URL_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	public static String GetHttpURL_4getShareSurl(Context context) {
		String[] keys = { "sn", "agent_id", "account","sign",
				"brand", "model", "product", "brandname" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), UserInfo_db.getUserInfo(context).getPhone(),
				 MD5.toMD5(sn + UserInfo_db.getUserInfo(context).getPhone() + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.getShareCode + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取更改密码的URL
	public static String GetHttpURL_4ChangePassword_V2(Context context,
			String oldpassowrd, String newPassowrd, String phone) {
		String[] keys = { "sn", "agent_id", "account", "pv", "v", "sign",
				"brand", "model", "product", "old_pwd", "new_pwd" };
		String sn = VerificationCode.getCode2();
		PackageManager manager = context.getPackageManager();
		PackageInfo info;
		String version = null;
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
		}
		String[] values = { sn, UserInfo_db.getagent_id(context), phone,
				"android", version, MD5.toMD5(sn + phone + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				Misc.cryptDataByPwd(oldpassowrd.trim()),
				Misc.cryptDataByPwd(newPassowrd.trim()) };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.ChangePWD_URL_V2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取每日签到的红包URL
	public static String GetHttpURL_4RedDaily_Url(Context context) {
		String[] keys = { "uid", "gift_id", "gift_type", "gift_subtype",
				"sign", "brandname" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmmss");
		String gift_id = sfd.format(new Date()) + userinfo.getUid()
				+ VerificationCode.getCode2();
		String[] values = { userinfo.getUid(), gift_id, "aixin_money",
				"logindaily", MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				Constants.BrandName };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.RED_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取拆红包的URl
	public static String GetHttpURL_4RedDaily_CheckOUT_Url(Context context,
			String gift_id, String action) {
		String[] keys = { "uid", "phone", "pv", "v", "gift_id", "sign",
				"brand", "model", "product", "agent_id", "action" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getUid(), userinfo.getPhone(),
				userinfo.getPv(), userinfo.getV(), gift_id,
				MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				userinfo.getAgent_id(), action };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.RED_Checkout_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取得到红包数据的URL
	public static String GetHttpURL_4Red_GETDATA_Url(Context context,
			String year, String direct) {
		String[] keys = { "uid", "phone", "pv", "v", "sign", "brand", "model",
				"product", "agent_id", "year", "direct" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = { userinfo.getUid(), userinfo.getPhone(),
				userinfo.getPv(), userinfo.getV(),
				MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, Constants.BrandName,
				userinfo.getAgent_id(), year, direct };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.RED_GETDATA_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取用户资料URL
	public static String GetHttpURL_4UserXXinfo_url(Context context,
			String uid, String ver) {
		String[] keys = { "uid", "ver" };
		String[] values = { uid, ver };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.GETUXXDATA_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取获取动态开屏的URL
	public static String GetHttpURL_4Start_PagerURL(Context context) {
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] keys = { "pv", "agent_id", "v", "pix_level", "brandname",
				"uid", "ver" };
		String Pix_level = "mdpi";
		switch (PhoneInfo.whichScreen()) {
		case 1:
			Pix_level = "ldpi";
			break;
		case 3:
			Pix_level = "hdpi";
			break;
		case 4:
			Pix_level = "xhdpi";
			break;
		}
		String ver = SharedPreferencesTools.getSharedPreferences_4startpager(
				context)
				.getString(SharedPreferencesTools.startpager_ver, "1.0");
		String[] values = { "android", userinfo.getAgent_id(), userinfo.getV(),
				Pix_level, Constants.BrandName, userinfo.getUid(), ver };
		StringBuilder reg_geturl_strb = new StringBuilder();
		reg_geturl_strb.append(HttpUtils.STARTPAGER_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_geturl_strb.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_geturl_strb.toString();
	}

	// 获取服务业数据
	public static String GetHttpURL_ServicePage_url(Context context) {

		String[] keys = { "uid", "sign", "brandname", "ver","v","pv" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);

		String[] values = {
				userinfo.getUid(),
				MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				Constants.BrandName,
				SharedPreferencesTools
						.getSharedPreferences_ServicePage(context).getString(
								SharedPreferencesTools.SERVICE_PAGE_VER, "1.0"),userinfo.getV(),"android" };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.servicePage_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();

	}

	// 获取上传本地联系人的URL
	public static String GetHttpURL_4Commitfriend(Context context) {
		String[] keys = { "uid" };
		String[] values = { UserInfo_db.getUserInfo(context).getUid() };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.COMMITFRIEND + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取拉取服务器好友列表的URL
	public static String GetHttpURL_4UPfriend(Context context) {
		String[] keys = { "uid", "ver" };
		String ver = SharedPreferencesTools.getSharedPreferences_4upfriends(
				context).getString(SharedPreferencesTools.upfrendver, "1.0");
		String[] values = { UserInfo_db.getUserInfo(context).getUid(), ver };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.GETAixinFriends + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取查询好友资料的URL
	public static String GetHttpURL_4UPfriend2One(Context context) {
		String[] keys = { "uid" };
		String[] values = { UserInfo_db.getUserInfo(context).getUid() };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.GETAixinFriendInfo + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取查询是否环宇好友的URL
	public static String GetHttpURL_4Friend(Context context, String checktext) {
		String[] keys = { "uid", "account" };
		String[] values = { UserInfo_db.getUserInfo(context).getUid(),
				checktext };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.QUERYAixinFriendInfo + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取发送个人红包的URL
	public static String GetHttpURL_4SendPersonRed(Context context,
			ArrayList<String> uidorphones, String addred_flag, String uid,
			String gift_type, String money, String money_type,
			String gift_name, String gift_tips, String fromnickname) {
		String[] keys = { addred_flag, "receiver_gifts_number", "from",
				"gift_id", "gift_type", "gift_subtype", "sign", "money",
				"money_type", "gift_name", "gift_tips", "fromnickname" };
		StringBuilder uidorphones_strb = new StringBuilder();
		for (String str : uidorphones) {
			uidorphones_strb.append(str + "%7C");
		}
		String uidorphones_str = uidorphones_strb.toString().substring(0,
				uidorphones_strb.toString().length() - 3);
		SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmmss");
		String gift_id = sfd.format(new Date()) + uid
				+ VerificationCode.getCode2();
		String gift_subtype = null;
		if ("uid".equals(addred_flag)) {
			gift_subtype = "personnocommand";
		} else if ("phone".equals(addred_flag)) {
			gift_subtype = "personwithcommand";
		}
		String[] values = { uidorphones_str, uidorphones.size() + "", uid,
				gift_id, gift_type, gift_subtype,
				MD5.toMD5(uid.trim() + Common.SIGN_KEY), money, money_type,
				gift_name, gift_tips, fromnickname };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.add_gift_record_v2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	public static String GetHttpURL_Add_Shared_RedPackage(Context context) {
		String[] keys = { "uid", "phone", "gift_id", "create_time",
				"gift_type", "gift_subtype", "pv", "v", "sign", "brand",
				"model", "agent_id", "brandname", "gift_name", "money" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmmss");
		SharedPreferences shared = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(context);
		String fee_rate = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_FEERATE, "19");
		String share_gift_minites = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_SHARE_GIFT_MINITES,
				"10");
		String money = Double.parseDouble(fee_rate)
				* Double.parseDouble(share_gift_minites) * 100 + "";
		String createtime = sfd.format(new Date());
		String gift_id = createtime + userinfo.getUid();
		String[] values = { userinfo.getUid(), userinfo.getPhone(), gift_id,
				createtime, "aixin_money", "snsshare", userinfo.getPv(),
				userinfo.getV(),
				MD5.toMD5(userinfo.getUid() + Common.SIGN_KEY),
				PhoneInfo.brand, PhoneInfo.phoneModel, userinfo.getAgent_id(),
				Constants.BrandName, "分享奖励红包", money };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.RED_URL + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取设置红包口令的URL
	public static String GetHttpURL_4SetSendRedCodeURL(Context context,
			String sended_gift_id, String action, String command) {
		String[] keys = null;
		String[] values = null;
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		if (command == null) {
			keys = new String[] { "uid", "sign", "action", "sended_gift_id" };
			values = new String[] { userinfo.getUid(),
					MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY),
					action, sended_gift_id };
		} else {
			keys = new String[] { "uid", "sign", "action", "sended_gift_id",
					"command" };
			values = new String[] { userinfo.getUid(),
					MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY),
					action, sended_gift_id, command };
		}
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.get_set_gift_command + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取发送群红包的URL
	public static String GetHttpURL_4SendGroupRed(Context context,
			String receiver_gifts_number, String uid, String gift_type,
			String money, String money_type, String gift_name,
			String gift_tips, String fromnickname) {
		String[] keys = { "receiver_gifts_number", "from", "gift_id",
				"gift_type", "gift_subtype", "sign", "money", "money_type",
				"gift_name", "gift_tips", "fromnickname" };
		SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmmss");
		String gift_id = sfd.format(new Date()) + uid
				+ VerificationCode.getCode2();
		String gift_subtype = "groupwithcommand";
		String[] values = { receiver_gifts_number, uid, gift_id, gift_type,
				gift_subtype, MD5.toMD5(uid.trim() + Common.SIGN_KEY), money,
				money_type, gift_name, gift_tips, fromnickname };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.add_gift_record_v2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 根据口令获取红包信息
	public static String GetHttpURL_4RedCodeTORedInfo(Context context,
			String redCode) {
		String[] keys = new String[] { "uid", "sign", "command" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = new String[] { userinfo.getUid(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY), redCode };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.get_sended_giftinfo_by_command + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();

	}

	// 获取答谢好友的URL
	public static String GetHttpURL_4thanksfriend(Context context,
			String gift_id, String thankyou) {
		String[] keys = new String[] { "uid", "sign", "gift_id", "thankyou" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = new String[] { userinfo.getUid(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY), gift_id,
				thankyou };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.add_gift_thankyou + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取单个红包的信息
	public static String GetHttpURL_4getoneRed(Context context, String gift_id) {
		String[] keys = new String[] { "uid", "sign", "gift_id" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = new String[] { userinfo.getUid(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY), gift_id };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.get_gift_info_v2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 获取分享说明
	public static String GetHttpURL_4Shareurl(Context context) {
		String[] keys = new String[] { "uid", "sign"};
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = new String[] { userinfo.getUid(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY)};
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.get_gift_info_v2_shareurl + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}

	// 看红包手气URL
	public static String GetHttpURL_4RedTAInfo(Context context, String gift_id) {
		String[] keys = new String[] { "uid", "sign", "sender_gift_id" };
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		String[] values = new String[] { userinfo.getUid(),
				MD5.toMD5(userinfo.getUid().trim() + Common.SIGN_KEY), gift_id };
		StringBuilder reg_service_page = new StringBuilder();
		reg_service_page.append(HttpUtils.get_gift_receive_info_v2 + "?");
		int i = 0;
		for (String str : keys) {
			reg_service_page.append(str + "=" + values[i] + "&");
			i += 1;
		}
		return reg_service_page.toString();
	}
}
