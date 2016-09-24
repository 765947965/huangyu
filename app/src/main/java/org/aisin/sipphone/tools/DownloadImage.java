package org.aisin.sipphone.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.aisin.sipphone.commong.ImageInfo;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class DownloadImage {

	public static synchronized void downling(final Context context,
			final Downloadcomplete dld) {
		// 判断是否需要更新广告

		if (CheckUpadateTime.CheckResult_4getadlist_UP_time(context)
				&& Check_network.isNetworkAvailable(context)) {

			// 得到下载广告请求代码
			String getadlist_url = URLTools.GetHttpURL_4Getadlist(context);
			// 请求广告地址
			String getadlist_result = HttpUtils.result_url_get(getadlist_url,
					"");
			if ("".equals(getadlist_result)) {// 如果结果不正确 结束
				return;
			}
			try {
				JSONObject json = new JSONObject(getadlist_result);
				int temp = json.optInt("result", -104);
				if (temp != 0) {
					return;
				}
			} catch (JSONException e) {
			}
			// 保存广告结果 备用
			SharedPreferences sPreference = SharedPreferencesTools
					.getSharedPreferences_getadlist_date_share(context);
			sPreference
					.edit()
					.putString(SharedPreferencesTools.SPF_getadlist_date_date,
							getadlist_result).commit();

			// 处理图片地址
			List<ImageInfo> list = ImageURL4JSON
					.getImageUrlList(getadlist_result);
			boolean downallflag = true;// 判断是否所有图片都下载成功了
			if (list != null && Check_network.isNetworkAvailable(context)) {
				for (ImageInfo imif : list) {
					// 判断该图片是否存在
					if (!context.getFileStreamPath(imif.getName()).exists()) {
						// 不存在 下载图片
						downallflag = HttpUtils.downloadImage(context,
								imif.getDLURL(), imif.getName());
					}
				}
				if (downallflag) {
					// 重置时间和需要更新的需求
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
					String date = sdf.format(new Date());
					SharedPreferences sft = SharedPreferencesTools
							.getSharedPreferences_getadlist_uptime_share(context);
					sft.edit()
							.putString(
									SharedPreferencesTools.SPF_getadlist_uptime_uptime,
									date).commit();
				}
			}
			if (dld != null) {// 真所有图片下载成功 显示
				dld.down();
			}
		}
	}
}
