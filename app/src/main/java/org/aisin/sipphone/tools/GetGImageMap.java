package org.aisin.sipphone.tools;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.aisin.sipphone.commong.GimageInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GetGImageMap {
	public static ArrayList<GimageInfo> imagelist = null;

	public static synchronized ArrayList<GimageInfo> getImageMap(
			Context context, boolean Compulsory) {// Compulsory强制刷新
		if (imagelist != null) {
			if (!Compulsory) {
				return imagelist;// 非强制刷新 返回集合
			}
		} else {
			imagelist = new ArrayList<GimageInfo>();
		}

		SharedPreferences sPreference = SharedPreferencesTools
				.getSharedPreferences_getadlist_date_share(context);
		String str_json = sPreference.getString(
				SharedPreferencesTools.SPF_getadlist_date_date, "");
		if ("".equals(str_json)) {
			return imagelist;
		}
		try {
			JSONObject json = new JSONObject(str_json.trim());
			if ("".equals(json.optString("pn", ""))) {
				return imagelist;
			}
			JSONArray jsonarray_pn = json.getJSONArray("pn");
			if (jsonarray_pn == null || jsonarray_pn.length() == 0) {
				return imagelist;
			}
			imagelist.clear();
			String adid = json.optString("adid");
			String urlprefix = json.optString("urlprefix");
			for (int i = 0; i < jsonarray_pn.length(); i++) {
				JSONObject imagejson = jsonarray_pn.getJSONObject(i);
				String n = imagejson.getString("n");
				String to = imagejson.getString("to");
				FileInputStream inputstream = context.openFileInput(n.trim());
				if (inputstream != null) {
					Bitmap bitmap = null;
					try {
						bitmap = BitmapFactory.decodeStream(inputstream);
					} catch (Error e) {
					}
					inputstream.close();
					if (bitmap != null) {
						imagelist.add(new GimageInfo(n, to, bitmap, adid,
								urlprefix));
					} else {
						// 有图片下载失败了 删除该图片文件， 通知重新下载
						context.getFileStreamPath(n.trim()).delete();
						SharedPreferencesTools
								.getSharedPreferences_getadlist_uptime_share(
										context)
								.edit()
								.putString(
										SharedPreferencesTools.SPF_getadlist_uptime_uptime,
										"0000").commit();
					}
				}
			}
		} catch (Exception e) {
			return imagelist;
		}
		return imagelist;

	}
}
