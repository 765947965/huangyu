package org.aisin.sipphone.find;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class Http_task_servicepage extends AsyncTask<String, String, String> {

	private Context mContext;
	private ServicePageListener spListener;
    private String path;
	public interface ServicePageListener {
		void compelete();
	}

	public void setCompleteListener(ServicePageListener servicePageListener) {
		this.spListener = servicePageListener;
	}

	public Http_task_servicepage(Context context) {
		this.mContext = context;
		path=mContext.getFilesDir().getPath();
	}

	@Override
	protected String doInBackground(String... params) {
		SharedPreferences shared=SharedPreferencesTools
				.getSharedPreferences_ServicePage(mContext);
		String resulttemp = shared.getString(
						SharedPreferencesTools.SERVICE_Data, "");
		String oldVersion=shared.getString(SharedPreferencesTools.SERVICE_PAGE_VER, "1.0");
		if (!Check_network.isNetworkAvailable(mContext)) {
			return resulttemp;
		} else {
			if (!CheckUpadateTime.CheckResult_4Serverpager(mContext)) {
				return resulttemp;
			} else {
				String url = URLTools.GetHttpURL_ServicePage_url(mContext);
				String result = HttpUtils
						.result_url_get(url, "{'result':'45'}");
				try {
					JSONObject json = new JSONObject(result);
					int doresult = json.optInt("result", 104);
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
					String date = sdf.format(new Date());
					String ver="";
					if (doresult == 0) {
						
						 ver = json
									.optString(SharedPreferencesTools.SERVICE_PAGE_VER);
							String servicePageName = json
									.optString(SharedPreferencesTools.SERVICE_PAGE_SERVICEPAGE_NAME);
							
						// 下载图片
						JSONArray servicePages = json.getJSONArray("items");
						for (int i = 0; i < servicePages.length(); i++) {
							JSONObject servicePage = null;
							try {
								servicePage = servicePages.getJSONObject(i);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							String pic = servicePage
									.optString(SharedPreferencesTools.SERVICE_PAGE_PIC);
							final int location = i;
							File file = mContext.getFileStreamPath("img_"+ver
									+ location + ".png");
							File fileOld = mContext.getFileStreamPath("img_"+oldVersion
									+ location + ".png");
						if(!file.exists()){
							HttpUtils.downloadImage(mContext, pic, "img_"+
									ver+ location + ".png");
							if(fileOld.exists()){
								fileOld.delete();
							}
							 
							
						}
								 
						
						}
						
						// 存储更新日期，服务页名称, 数据 ，版本号
						SharedPreferencesTools
						.getSharedPreferences_ServicePage(mContext)
						.edit()
						.putString(
								SharedPreferencesTools.SERVICE_PAGE_DATE,
								date)
						.putString(
								SharedPreferencesTools.SERVICE_PAGE_SERVICEPAGE_NAME,
								servicePageName)
						.putString(
								SharedPreferencesTools.SERVICE_PAGE_VER,
								ver)
						.putString(SharedPreferencesTools.SERVICE_Data,
								result).commit();
						return result;
					} else if (doresult == 70) {
						// 存储更新时间
						SharedPreferencesTools
								.getSharedPreferences_ServicePage(mContext)
								.edit()
								.putString(
										SharedPreferencesTools.SERVICE_PAGE_DATE,
										date).commit();
						return resulttemp;
					} else {
						return resulttemp;
					}
				} catch (Exception e) {
					return resulttemp;
				}
			}
		}
	}

	@Override
	protected void onPostExecute(String result) {

		JSONObject json = null;
		int doresult = 104;
		try {
			if (result != null) {
				json = new JSONObject(result);
				doresult = Integer.parseInt(json.optString("result", "104"));

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		switch (doresult) {
		case 0:
			spListener.compelete();
			break;

		default:

			break;
		}

	}

}
