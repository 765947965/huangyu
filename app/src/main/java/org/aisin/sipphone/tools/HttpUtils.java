package org.aisin.sipphone.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class HttpUtils {
	public static final String URL = "http://mobile.zjtytx.com:8899/";
	public static final String PAY = "http://pay.zjtytx.com:8060/recharge/service/pay.php";
	public static final String LS = "http://user.zjtytx.com:8060/usercenter/mobile_chargelog.php";
	public static final String HD = "http://user.zjtytx.com:8060/usercenter/mobile_callsearch.php";
	public static final String MSG_URL = "http://mobile.zjtytx.com:8899/config/msglist";
	public static final String ACTIONREPORT = "http://mobile.zjtytx.com:8899/action_report";
	public static final String CALL = "http://mobile.zjtytx.com:8899/call";
	public static final String UPUSERDATAURL = "http://mobile.zjtytx.com:8899/user/uploadUserProfile";
	public static final String UPUserHeadURL = "http://mobile.zjtytx.com:8899/user/uploadUserHead";

	// ------------V2地址-------------------
	public static final String REG_GETCODE = "http://mobile.zjtytx.com:8899/v2/reg_sendauthcode_v2";
	public static final String REG_V2 = "http://mobile.zjtytx.com:8899/v2/reg_v2";
	public static final String Loding_V2 = "http://mobile.zjtytx.com:8899/v2/login_v2";
	public static final String ChangPhone_V2 = "http://mobile.zjtytx.com:8899/v2/change_phone_v2";
	public static final String GetPWD_YZCode_V2 = "http://mobile.zjtytx.com:8899/v2/send_findpwd_authcode_v2";
	public static final String GetPWD_URL_V2 = "http://mobile.zjtytx.com:8899/v2/find_password_v2";
	public static final String ChangePWD_URL_V2 = "http://mobile.zjtytx.com:8899/v2/change_pwd_v2";

	private static HttpClient httpclient = null;

	public static final String RED_URL = "http://mobile.zjtytx.com:8899/gift/add_gift_record_v2";
	public static final String RED_Checkout_URL = "http://mobile.zjtytx.com:8899/gift/checkout_gift_v2";
	public static final String RED_GETDATA_URL = "http://mobile.zjtytx.com:8899/gift/get_gift_records_v2";
	public static final String GETUXXDATA_URL = "http://mobile.zjtytx.com:8899/user/getUserProfile";// 获取个人资料
	public static final String STARTPAGER_URL = "http://mobile.zjtytx.com:8899/config/start_page";
	public static final String COMMITFRIEND = "http://mobile.zjtytx.com:8899/friend/uploadAixinContact";
	public static final String GETAixinFriends = "http://mobile.zjtytx.com:8899/friend/getAixinFriends";
	public static final String GETAixinFriendInfo = "http://mobile.zjtytx.com:8899/friend/getAixinFriendInfo";
	public static final String QUERYAixinFriendInfo = "http://mobile.zjtytx.com:8899/friend/queryAixinFriendInfo";
	public static final String add_gift_record_v2 = "http://mobile.zjtytx.com:8899/gift/add_gift_record_v2";
	public static final String get_set_gift_command = "http://mobile.zjtytx.com:8899/gift/get_set_gift_command";
	public static final String get_sended_giftinfo_by_command = "http://mobile.zjtytx.com:8899/gift/get_sended_giftinfo_by_command";
	public static final String add_gift_thankyou = "http://mobile.zjtytx.com:8899/gift/add_gift_thankyou";
	public static final String get_gift_info_v2 = "http://mobile.zjtytx.com:8899/gift/get_gift_info_v2";
	public static final String get_gift_receive_info_v2 = "http://mobile.zjtytx.com:8899/gift/get_gift_receive_info_v2";
	public static final String get_gift_info_v2_shareurl = "http://user.8hbao.com:8060/share_instructions.php";
	public static final String getShareCode = "http://mobile.8hbao.com:8899/v2/get_my_invite_flag_v2";//获取专属邀请码

	// 分享红包图标地址
	public static final String redsharedImageurl = "http://img.zjtytx.com:8060/group1/M00/00/0B/d5OYZ1W2_J2AQ9q2AAAcD7bibuo522.png";
	// 环宇图标网络地址
	public static final String aixinImageurl = "http://m.zjtytx.com:8060/download/image/calltc.512x512.png";
	public static final String ADVICE = "http://mobile.zjtytx.com:8899/config/advice";

	// 发现页面网址
	public static final String Find_aisin_mall_URL = "http://1000shishang.taobao.com";
	public static final String Find_phone_recharge_URL = "http://epay.uc.cn";
	public static final String Find_express_query_URL = "http://M.kuaidi100.com";
	public static final String Find_attractions_tickets_URL = "http://touch.piao.qunar.com/touch/index.htm?bd_source=aixin";
	public static final String Find_train_tickets_URL = "http://touch.qunar.com/h5/train/?from=touchindex";
	public static final String Find_flight_ticket_URL = "http://touch.qunar.com/h5/flight/";
	public static final String Find_transfer_machine_URL = "http://car.qunar.com/?from=9&bd_source=aixin";
	public static final String Find_hotel_URL = "http://touch.qunar.com/h5/hotel/";
	public static final String servicePage_URL = "http://mobile.zjtytx.com:8899/config/get_service_page_config";

	public static synchronized String result_url_get(String url,
			String defaultvalue) {
		HttpClient httpclient = getmyHttpClient();
		url = url.replaceAll(" ", "");
		HttpGet request = new HttpGet(url.trim());
		try {
			HttpResponse response = httpclient.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				return defaultvalue;
			}
		} catch (Exception e) {
			return defaultvalue;
		} finally {
			request.abort();
		}
	}

	private static synchronized HttpClient getmyHttpClient() {
		if (httpclient != null) {
			return httpclient;
		} else {
			httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			return httpclient;
		}
	}

	public static synchronized String result_post(String url, String[] keys,
			String[] values) {
		return null;
	}

	public static boolean downloadImage(Context context, String url, String name) {

		HttpClient httpclient = new DefaultHttpClient();
		// 请求超时
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		// 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);
		HttpGet httpget = new HttpGet(url);
		try {
			HttpResponse response = httpclient.execute(httpget);
			InputStream is = response.getEntity().getContent();
			Long imagelenth = response.getEntity().getContentLength();
			FileOutputStream out = context.openFileOutput(name,
					Context.MODE_PRIVATE);
			byte[] bts = new byte[1024];
			int num = 0;
			while ((num = is.read(bts)) != -1) {
				out.write(bts, 0, num);
			}
			out.flush();
			out.close();
			is.close();
			File filetemps = context.getFileStreamPath(name);
			if (!filetemps.exists()) {
				return false;
			} else {
				if (imagelenth != filetemps.length()) {
					filetemps.delete();
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			httpget.abort();
		}
	}

	public static Map<String, String> URLRequest(String URL) {
		Map<String, String> mapRequest = new HashMap<String, String>();
		String[] arrSplit = null;
		String strUrlParam = TruncateUrlPage(URL);
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			String[] arrSplitEqual = null;
			arrSplitEqual = strSplit.split("[=]");
			// 解析出键值
			if (arrSplitEqual.length > 1) {
				// 正确解析
				mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
			} else {
				if (arrSplitEqual[0] != "") {
					// 只有参数没有值，不加入
					mapRequest.put(arrSplitEqual[0], "");
				}
			}
		}
		return mapRequest;
	}

	private static String TruncateUrlPage(String strURL) {
		String strAllParam = null;
		String[] arrSplit = null;
		strURL = strURL.trim().toLowerCase();
		arrSplit = strURL.split("[?]");
		if (strURL.length() > 1) {
			if (arrSplit.length > 1) {
				if (arrSplit[1] != null) {
					strAllParam = arrSplit[1];
				}
			}
		}
		return strAllParam;
	}
}
