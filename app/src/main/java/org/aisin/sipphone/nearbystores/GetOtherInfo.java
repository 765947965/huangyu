package org.aisin.sipphone.nearbystores;

import java.util.List;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.tools.Constants;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

public class GetOtherInfo {

	private static TreeMap<String, AthAppInfo> appList;

	private static String[] appditupackname = { "com.baidu.BaiduMap",
			"com.baidu.BaiduMap.pad", "com.autonavi.minimap" };

	private static TreeMap<String, AthAppInfo> getAPPOI(Context context) {
		if (appList != null) {
			return appList;
		}
		appList = new TreeMap<String, AthAppInfo>();
		List<PackageInfo> packages = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			for (String packename : appditupackname) {
				if (packename.equals(packageInfo.packageName)) {
					AthAppInfo tmpInfo = new AthAppInfo();
					tmpInfo.setAppname(packageInfo.applicationInfo.loadLabel(
							context.getPackageManager()).toString());
					tmpInfo.setPackagename(packageInfo.packageName);
					tmpInfo.setVersionName(packageInfo.versionName);
					tmpInfo.setVersionCode(packageInfo.versionCode);
					/*
					 * tmpInfo.setAppicon(packageInfo.applicationInfo
					 * .loadIcon(context.getPackageManager()));
					 */
					appList.put(tmpInfo.getPackagename(), tmpInfo);
				}
			}
		}
		return appList;
	}

	public static void doDH(final Context context, final double st_latitude,
			final double st_longitude, final double en_latitude,
			final double en_longitude) {
		getAPPOI(context);

		String[] strv2 = new String[2];
		if (appList.get(appditupackname[0]) != null
				|| appList.get(appditupackname[1]) != null) {
			strv2[0] = "";
		} else {
			strv2[0] = "下载";
		}
		if (appList.get(appditupackname[2]) != null) {
			strv2[1] = "";
		} else {
			strv2[1] = "下载";
		}

		AisinBuildDialog mybuild = new AisinBuildDialog(context);
		mybuild.setTitle("请选择导航应用!");
		mybuild.setListViewItem(new String[] { "百度地图", "高德地图" }, strv2,
				new Integer[] { R.drawable.icon_baidu, R.drawable.icon_gaode },
				new onMyItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// 移动APP调起Android百度地图方式举例
						DH(context, st_latitude, st_longitude, en_latitude,
								en_longitude, position);
					}
				}, true);
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();

	}

	@SuppressWarnings("deprecation")
	private static void DH(Context context, double st_latitude,
			double st_longitude, double en_latitude, double en_longitude,
			int position) {
		try {
			Intent intentbd = null;
			if (position == 0) {
				try {
					intentbd = Intent
							.getIntent("intent://map/direction?origin=latlng:"
									+ st_latitude
									+ ","
									+ st_longitude
									+ "|name:我的位置&destination=latlng:"
									+ en_latitude
									+ ","
									+ en_longitude
									+ "|name:"
									+ Constants.nearbyname
									+ "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
					context.startActivity(intentbd); // 启动调用
				} catch (Exception e) {
					intentbd = Intent
							.getIntent("intent://map/direction?origin=latlng:"
									+ st_latitude
									+ ","
									+ st_longitude
									+ "|name:我的位置&destination=latlng:"
									+ en_latitude
									+ ","
									+ en_longitude
									+ "|name:"
									+ Constants.nearbyname
									+ "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap.pad;end");
					context.startActivity(intentbd); // 启动调用
				}
			} else if (position == 1) {
				// 高德地图
				Intent intent = new Intent("android.intent.action.VIEW",
						android.net.Uri
								.parse("androidamap://navi?sourceApplication="
										+ Constants.BrandName + "&poiname="
										+ Constants.nearbyname + "&lat="
										+ en_latitude + "&lon=" + en_longitude
										+ "&dev=1&style=2"));
				intent.setPackage("com.autonavi.minimap");
				context.startActivity(intent);
			}
		} catch (Exception e) {
			// 没调起来 调自己的
			// Intent intent = new Intent(context,
			// org.aisin.sipphone.nearbystores.RoutePlanDemo.class);
			// intent.putExtra("RoutePlanDemo.st_latitude", st_latitude);
			// intent.putExtra("RoutePlanDemo.st_longitude", st_longitude);
			// intent.putExtra("RoutePlanDemo.en_latitude", en_latitude);
			// intent.putExtra("RoutePlanDemo.en_longitude", en_longitude);
			// context.startActivity(intent);
			String sertext = null;
			if (position == 0) {
				sertext = "百度地图";
			} else if (position == 1) {
				sertext = "高德地图";
			}
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri uri = Uri.parse("http://www.baidu.com/s?wd="
					+ Uri.encode(sertext));
			intent.setData(uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
}
