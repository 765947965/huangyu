package org.aisin.sipphone;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

public class HttpTask_Red extends AsyncTask<String, String, String> {
	private Context context;
	private Handler mHandler;

	public HttpTask_Red(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
	}

	@Override
	protected String doInBackground(String... paramArrayOfParams) {
		String url = URLTools.GetHttpURL_4RedDaily_Url(context);
		String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		JSONObject json = null;
		int doresult = -104;
		try {
			if (result != null) {
				json = new JSONObject(result);
				doresult = Integer.parseInt(json.optString("result", "-104"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		switch (doresult) {
		case 0:
			// 更新 每日签到红包请求红包日期
			SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
			String date = sdf.format(new Date());
			SharedPreferencesTools.getSharedPreferences_4REDDaily(context)
					.edit()
					.putString(SharedPreferencesTools.REDDaily_key, date)
					.commit();
			mHandler.sendEmptyMessage(8);
			return;
		case 54:// 今日签到红包已到上限
			// 更新 每日签到红包请求红包日期
			SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
			String date2 = sdf2.format(new Date());
			SharedPreferencesTools.getSharedPreferences_4REDDaily(context)
					.edit()
					.putString(SharedPreferencesTools.REDDaily_key, date2)
					.commit();
			mHandler.sendEmptyMessage(8);
			break;
		default:
			mHandler.sendEmptyMessage(8);
			break;
		}
	}
}
