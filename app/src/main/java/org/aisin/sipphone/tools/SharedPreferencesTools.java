package org.aisin.sipphone.tools;

import org.aisin.sipphone.commong.UserInfo;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTools {

	public static final String SPF_USER_BD_PHONE = "bding_phone";
	public static final String SPF_USER_PHONE = "phone";
	public static final String SPF_USER_PWD = "pwd";
	public static final String SPF_USER_UID = "uid";
	public static final String SPF_USER_AGENT_ID = "agent_id";

	public static final String SPF_msglist_uptime_UPTIME = "time";

	public static final String SPF_getadlist_date_date = "getadlist_date";

	public static final String SPF_getadlist_uptime_uptime = "time";

	public static final String SPF_ALLSWITCH_ANSWALLFLAG = "answallflag";
	public static final String SPF_NETWORK_3G_4G = "network3g4g";
	public static final String SPF_NETWORK_WIFI = "networkwifi";

	public static final String SPF_msglist_date_FEERATE = "fee_rate";
	public static final String SPF_msglist_date_INVITE_TITLE = "invite_title";
	public static final String SPF_msglist_date_INVITE_APP = "invite_app";
	public static final String SPF_msglist_date_INVITE_SMS_MESSAGE = "invite_sms_message";
	public static final String SPF_msglist_date_INVITE_SNS_MESSAGE = "invite_sns_message";
	public static final String SPF_msglist_date_INVITE_URL = "invite_url";
	public static final String SPF_msglist_date_SHOPMALL_TITLE = "shopmall_title";
	public static final String SPF_msglist_date_SHOPMALL_URL = "shopmall_url";
	public static final String SPF_msglist_date_VPS = "vps";
	public static final String SPF_msglist_date_service_phone = "service_phone";
	public static final String SPF_msglist_date_direct_open = "direct_open";
	public static final String SPF_msglist_date_giftshare_url = "giftshare_url";
	public static final String SPF_msglist_date_nearby_shop = "nearby_shop";
	public static final String SPF_msglist_date_ipcall_prefix = "96ipcall_prefix";
	public static final String SPF_msglist_date_020ipcall_prefix = "02096ipcall_prefix";
	public static final String SPF_msglist_date_0769ipcall_prefix = "0769ipcall_prefix";
	public static final String SPF_msglist_date_direct_fee_rate = "direct_fee_rate";
	public static final String SPF_msglist_date_ipcall_title = "ipcall_title";
	public static final String SPF_msglist_date_acall_linkman_tips_open = "acall_linkman_tips_open";
	public static final String SPF_msglist_date_general_agents_url = "general_agents_url";
	public static final String SPF_msglist_date_servicepage_company_url = "servicepage_company_url";
	public static final String SPF_msglist_date_servicepage_noticeboard_url = "servicepage_noticeboard_url";
	public static final String SPF_msglist_date_servicepage_recentevents_url = "servicepage_recentevents_url";
	public static final String SPF_msglist_date_servicepage_instructions_url = "servicepage_instructions_url";

	public static final String SPF_AEUT_TIME = "aeut_time";

	public static final String SPF_CALLOUTPHONENUM_NUMKEY = "outphonenum";
	public static final String SPF_CALLOUTPHONENUM_NAMEKEY = "outname";

	public static final String SPF_shortcut_key = "shortcut";

	public static final String REGChangePwd_key = "REGChangePwd_key";
	public static final String REDDaily_key = "REDDaily_key";
	public static final String REDDaily_codenums = "codenums";
	public static final String REDCOUT_key = "REDCOUT_key";
	public static final String REhasnum_key = "NUM";
	public static final String REDDATA_key = "DATE";
	public static final String REDDATA_dian = "diankey";
	public static final String upfrendstime = "committime";
	public static final String upfrendsuptime = "uptime";
	public static final String upfrendver = "ver";

	public static final String OraLodingUsers_key = "oralodingusers_key";

	public static final String upAPPVer = "update_ver";
	public static final String startpagerdate = "startpagerdate";
	public static final String startpager_data = "startpager_data";
	public static final String startpager_showtimes = "startpager_showtimes";
	public static final String startpager_ver = "ver";
	public static final String agent_id = "agent_id";

	public static final String SERVICE_PAGE_HAS_SUB_SECT = "has_sub_sect";
	public static final String SERVICE_PAGE_TO = "to";
	public static final String SERVICE_PAGE_PIC = "pic";
	public static final String SERVICE_PAGE_NAME = "name";
	public static final String SERVICE_PAGE_VER = "ver";
	public static final String SERVICE_PAGE_SERVICEPAGE_NAME = "name";
	public static final String SERVICE_PAGE_DATE = "update";
	public static final String SERVICE_Data = "SERVICE_Data";
	public static final String DIAL_VIDEO = "downloadvideo";
	public static final String DIAL_VIDEO_DOWNLOADCURR = "downcurrent";
	public static final String SPF_msglist_date_SHARE_GIFT_MINITES = "share_gift_minites";
	public static final String SPF_msglist_date_SHARE_TIMES_MINITES = "share_times_monthly";
	public static final String SPF_msglist_date_INVITE_GIFE_MINITES = "invite_gift_minites";
	public static final String SPF_msglist_date_INVITE_TIME_MONTHLY = "invite_times_monthly";

	public static final String KBG_KEY = "key";
	public static final String KBG_ZDY = "key_zdy";

	public static final String KEYMUSIC_KEY = "keyboardmusic";

	public static final String CUPDATE = "datekey";

	public static final String urlto = "urlto";
	public static final String command = "command";

	public static final String firstyhome = "firstyhome";
	public static final String zbzlling = "zbzlling";
	public static final String k96IP = "k96IP";

	public static final String UPContant2Service = "uc2s";

	public static final String OpenMUSIC = "OpenMUSIC";
	public static final String ycsz_outv = "ycsz_outv";
	public static final String ycsz_neary_name = "ycsz_neary_name";
	public static final String ycsz_neary_flag = "ycsz_neary_flag";

	// 用户数据SharedPreferences
	public static SharedPreferences getSharedPreferences_UserInfo(
			Context context) {
		return context.getSharedPreferences("userPreferences",
				Context.MODE_PRIVATE);
	}

	// 系统msglist数据SharedPreferences
	public static SharedPreferences getSharedPreferences_msglist_date_share(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "msglist_date", Context.MODE_PRIVATE);
	}

	// 系统msglist更新时间
	public static SharedPreferences getSharedPreferences_msglist_uptime_share(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "msglist_uptime", Context.MODE_PRIVATE);
	}

	// 系统getadlist更新数据SharedPreferences
	public static SharedPreferences getSharedPreferences_getadlist_date_share(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "getadlist_date", Context.MODE_PRIVATE);
	}

	// 系统getadlist(广告图片)更新时间SharedPreferences
	public static SharedPreferences getSharedPreferences_getadlist_uptime_share(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "getadlist_uptime", Context.MODE_PRIVATE);
	}

	// 系统广告图效果上传时间SharedPreferences
	public static SharedPreferences getSharedPreferences_AdvertisingEffectUploadTIME(
			Context context) {
		try {
			return context.getSharedPreferences("AdvertisingEffectUploadv2",
					Context.MODE_PRIVATE);
		} catch (Exception e) {
			return null;
		}
	}

	// 系统所有开关的数据SharedPreferences
	public static SharedPreferences getSharedPreferences_ALLSWITCH(
			Context context) {
		return context.getSharedPreferences("all_switch", Context.MODE_PRIVATE);
	}

	// 获取快捷方式是否已经创建的share
	public static SharedPreferences getSharedPreferences_4shortcut(
			Context context) {
		return context.getSharedPreferences("shortcut", Context.MODE_PRIVATE);
	}

	// 获取每天签到红包日期share
	public static SharedPreferences getSharedPreferences_4REDDaily(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "red_daily", Context.MODE_PRIVATE);
	}

	// 获取是否有红包新事物的shere
	public static SharedPreferences getSharedPreferences_4RED_COUT(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "red_cout", Context.MODE_PRIVATE);
	}

	// 获取登录过的用户的share
	public static SharedPreferences getSharedPreferences_4OraLodingUsers(
			Context context) {
		return context.getSharedPreferences("oralodingusers",
				Context.MODE_PRIVATE);
	}

	// 获取是否展示引导页的share
	public static SharedPreferences getSharedPreferences_4bootpager(
			Context context) {
		return context.getSharedPreferences("bootpager", Context.MODE_PRIVATE);
	}

	// 获取动态开屏页的share
	public static SharedPreferences getSharedPreferences_4startpager(
			Context context) {
		return context.getSharedPreferences("startpager", Context.MODE_PRIVATE);
	}

	// 得到当前用户的agent_idshare
	public static SharedPreferences getSharedPreferences_4agent_id(
			Context context) {
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		if (userinfo == null) {
			return null;
		} else {
			return context.getSharedPreferences(userinfo.getUid() + "_agentid",
					Context.MODE_PRIVATE);
		}
	}

	// 服务业share
	public static SharedPreferences getSharedPreferences_ServicePage(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "servicePage", Context.MODE_PRIVATE);
	}

	// video
	public static SharedPreferences getSharedPreferences_Video(Context context) {
		return context.getSharedPreferences("video_load", Context.MODE_PRIVATE);
	}

	// 得到记录今天是否更新过好友列表的share
	public static SharedPreferences getSharedPreferences_4upfriends(
			Context context) {
		return context.getSharedPreferences(UserInfo_db.getUserInfo(context)
				.getUid() + "upfreads", Context.MODE_PRIVATE);
	}

	// 得到从网页跳转过来的数据 红包口令
	public static SharedPreferences getSharedPreferences_4wwwdata(
			Context context) {
		return context.getSharedPreferences("wwwdata", Context.MODE_PRIVATE);
	}

	// 获取拨号盘设置的背景图片
	public static SharedPreferences getSharedPreferences_4keybackground(
			Context context) {
		return context.getSharedPreferences("keybackground",
				Context.MODE_PRIVATE);
	}

	// 获取后台服务检查更新记录
	public static SharedPreferences getSharedPreferences_4UPSERVER(
			Context context) {
		return context.getSharedPreferences("Checkupserver",
				Context.MODE_PRIVATE);
	}

	// 获取当前拨号盘默认的音乐名称
	public static SharedPreferences getSharedPreferences_4KEYMUSIC(
			Context context) {
		return context.getSharedPreferences("keyboardmusic",
				Context.MODE_PRIVATE);
	}

	// 获取当前默认首页
	public static SharedPreferences getSharedPreferences_4FIRSTY(Context context) {
		return context.getSharedPreferences("firstyhome", Context.MODE_PRIVATE);
	}

	// 是否开启直拨
	public static SharedPreferences getSharedPreferences_4ZBZL(Context context) {
		return context.getSharedPreferences("zbzlling", Context.MODE_PRIVATE);
	}

	// 是96IP
	public static SharedPreferences getSharedPreferences_496IP(Context context) {
		return context.getSharedPreferences("96IP", Context.MODE_PRIVATE);
	}

	// 影藏设置
	public static SharedPreferences getSharedPreferences_4YCSZ(Context context) {
		return context.getSharedPreferences("yczs", Context.MODE_PRIVATE);
	}

}
