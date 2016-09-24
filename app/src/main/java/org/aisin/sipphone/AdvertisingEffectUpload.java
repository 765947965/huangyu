package org.aisin.sipphone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.Common;
import org.aisin.sipphone.tools.GetAction_reports;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.MD5;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class AdvertisingEffectUpload extends AsyncTask<String, String, String> {
	private Context context;

	public AdvertisingEffectUpload(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		HttpClient httpclient = new DefaultHttpClient();
		// 请求超时
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		// 读取超时
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				5000);
		HttpPost post = new HttpPost(HttpUtils.ACTIONREPORT);
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		UserInfo userinfo = UserInfo_db.getUserInfo(context);
		list.add(new BasicNameValuePair("uid", userinfo.getUid()));
		list.add(new BasicNameValuePair("phone", userinfo.getPhone()));
		list.add(new BasicNameValuePair("sign", MD5.toMD5(userinfo.getUid()
				.trim() + Common.SIGN_KEY)));
		list.add(new BasicNameValuePair("pv", userinfo.getPv()));
		list.add(new BasicNameValuePair("brand", PhoneInfo.brand));
		list.add(new BasicNameValuePair("model", PhoneInfo.phoneModel));
		list.add(new BasicNameValuePair("v", userinfo.getV()));
		try {
			SimpleDateFormat sfd = new SimpleDateFormat("MMdd");
			String date = sfd.format(new Date());
			SimpleDateFormat sfd2 = new SimpleDateFormat("yyyy-MM-dd");
			String date2 = sfd2.format(new Date());
			String str = GetAction_reports.ADDARSNUM(context, null, null, 0, 0);
			JSONObject json = new JSONObject();
			json.put("date", date2);
			json.put("adreport", str);
			list.add(new BasicNameValuePair("report", json.toString()));
			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(list,
					HTTP.UTF_8);
			post.setEntity(formEntiry);
			HttpResponse response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				// 重置广告效果记录的数据和登记上传日期
				SharedPreferencesTools
						.getSharedPreferences_AdvertisingEffectUploadTIME(
								context).edit()
						.putString(SharedPreferencesTools.SPF_AEUT_TIME, date)
						.commit();
				GetAction_reports.ADDARSNUM(context, "", null, 0, 0);
				return json.toString();
			}
		} catch (Exception e) {
		}
		post.abort();
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
