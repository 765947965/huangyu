package org.aisin.sipphone;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class HttpTask_start_page extends AsyncTask<String, String, String> {
	private Context context;
	private Handler mHandler;
	private boolean downimage = false;

	public HttpTask_start_page(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
	}

	@Override
	protected String doInBackground(String... paramArrayOfParams) {
		String strtemp = SharedPreferencesTools
				.getSharedPreferences_4startpager(context).getString(
						SharedPreferencesTools.startpager_data, "");
		if (!Check_network.isNetworkAvailable(context)) {
			return strtemp;
		} else {
			if (CheckUpadateTime.CheckResult_4start_pager(context)) {
				// 获取获取动态开屏的URL
				String url = URLTools.GetHttpURL_4Start_PagerURL(context);
				String result = HttpUtils.result_url_get(url,
						"{'result':'-104'}");
				try {
					JSONObject json = new JSONObject(result);
					int doresult = json.getInt("result");
					if (doresult == 0) {
						if ("".equals(strtemp)) {
							downimage = true;
						} else {
							// 对之前的MD5做判断
							try {
								JSONObject jsonold = new JSONObject(strtemp);
								String picmd5old = "";
								switch (PhoneInfo.whichScreen()) {
								case 1:
									picmd5old = jsonold
											.optString("pic_ldpi_md5");
									break;
								case 2:
									picmd5old = jsonold
											.optString("pic_mdpi_md5");
									break;
								case 3:
									picmd5old = jsonold
											.optString("pic_hdpi_md5");
									break;
								case 4:
									picmd5old = jsonold
											.optString("pic_xhdpi_md5");
									break;
								}
								// 对现在的MD5做判断
								String picmd5 = "";
								switch (PhoneInfo.whichScreen()) {
								case 1:
									picmd5 = json.optString("pic_ldpi_md5");
									break;
								case 2:
									picmd5 = json.optString("pic_mdpi_md5");
									break;
								case 3:
									picmd5 = json.optString("pic_hdpi_md5");
									break;
								case 4:
									picmd5 = json.optString("pic_xhdpi_md5");
									break;
								}
								downimage = !picmd5old.equals(picmd5);
							} catch (Exception e) {
							}
						}

						// 存储更新的时间和数据及版本
						String ver = json.optString("ver", "1.0");
						SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
						String date = sdf.format(new Date());
						SharedPreferencesTools
								.getSharedPreferences_4startpager(context)
								.edit()
								.putString(
										SharedPreferencesTools.startpagerdate,
										date)
								.putString(
										SharedPreferencesTools.startpager_data,
										result)
								.putString(
										SharedPreferencesTools.startpager_ver,
										ver).commit();
						return result;
					} else if (doresult == 63) {
						// 存储更新的时间
						SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
						String date = sdf.format(new Date());
						SharedPreferencesTools
								.getSharedPreferences_4startpager(context)
								.edit()
								.putString(
										SharedPreferencesTools.startpagerdate,
										date).commit();
						return strtemp;
					} else {
						return strtemp;
					}
				} catch (Exception e) {
					return strtemp;
				}
			} else {
				return strtemp;
			}
		}
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
			String picurl = null;
			String picmd5 = null;
			String pic_prefix = json.optString("pic_prefix");
			switch (PhoneInfo.whichScreen()) {
			case 1:
				picurl = pic_prefix + json.optString("pic_ldpi");
				picmd5 = json.optString("pic_ldpi_md5");
				break;
			case 2:
				picurl = pic_prefix + json.optString("pic_mdpi");
				picmd5 = json.optString("pic_mdpi_md5");
				break;
			case 3:
				picurl = pic_prefix + json.optString("pic_hdpi");
				picmd5 = json.optString("pic_hdpi_md5");
				break;
			case 4:
				picurl = pic_prefix + json.optString("pic_xhdpi");
				picmd5 = json.optString("pic_xhdpi_md5");
				break;
			}
			// 判断显示时间 不在时间内 不显示
			Long start_time = Long.parseLong(json.optString("start_time", "0")
					.trim());
			Long end_time = Long.parseLong(json.optString("end_time", "0")
					.trim());
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmm");
			Long date1 = Long.parseLong(sdf1.format(new Date()));
			if (!(date1 >= start_time && start_time <= end_time)) {
				// 启动主Activity
				if (mHandler != null) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.AisinActivity.class);
					context.startActivity(intent);
					mHandler.sendEmptyMessage(-1);
				}
				return;
			}
			// 判断今天显示的次数
			int showtimes = json.optInt("show_times_daily", 1);
			SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
			String date = sdf2.format(new Date());
			String olddate = SharedPreferencesTools
					.getSharedPreferences_4startpager(context).getString(
							SharedPreferencesTools.startpager_showtimes,
							"00000");
			if (!olddate.startsWith(date)) {
				SharedPreferencesTools
						.getSharedPreferences_4startpager(context)
						.edit()
						.putString(SharedPreferencesTools.startpager_showtimes,
								date + 1).commit();
			} else {
				int times = Integer.parseInt(olddate.substring(4));
				if (times >= showtimes) {
					// 启动主Activity 显示次数到了
					if (mHandler != null) {
						Intent intent = new Intent(context,
								org.aisin.sipphone.AisinActivity.class);
						context.startActivity(intent);
						mHandler.sendEmptyMessage(-1);
					}
					return;
				} else {
					SharedPreferencesTools
							.getSharedPreferences_4startpager(context)
							.edit()
							.putString(
									SharedPreferencesTools.startpager_showtimes,
									date + (times + 1)).commit();
				}
			}
			File file = context.getFileStreamPath("start_pager.jpg");
			if (file == null || !file.exists() || downimage) {
				// 重置显示次数为0
				SharedPreferencesTools
						.getSharedPreferences_4startpager(context)
						.edit()
						.putString(SharedPreferencesTools.startpager_showtimes,
								date + 0).commit();
				// 开启service下载图片
				Intent intentser = new Intent(context,
						org.aisin.sipphone.DolowniamgeServer.class);
				intentser.putExtra("furl", picurl);
				intentser.putExtra("imagename", "start_pager.jpg");
				context.startService(intentser);
				// 启动主Activity
				if (mHandler != null) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.AisinActivity.class);
					context.startActivity(intent);
					mHandler.sendEmptyMessage(-1);
				}
				return;
			} else {
				// 启动开屏页
				if (mHandler != null) {
					mHandler.sendEmptyMessage(-2);
				}
			}
			break;
		default:
			if (mHandler != null) {
				// 启动主Activity
				Intent intent = new Intent(context,
						org.aisin.sipphone.AisinActivity.class);
				context.startActivity(intent);
				mHandler.sendEmptyMessage(-1);
			}
			break;
		}
	}
}
