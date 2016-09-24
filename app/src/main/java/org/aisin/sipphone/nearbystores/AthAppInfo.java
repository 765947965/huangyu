package org.aisin.sipphone.nearbystores;

import android.graphics.drawable.Drawable;

public class AthAppInfo {
	private int versionCode = 0;
	// 名称
	private String appname = "";
	// 包
	private String packagename = "";
	private String versionName = "";
	// 图标
	private Drawable appicon = null;

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public Drawable getAppicon() {
		return appicon;
	}

	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}

}
