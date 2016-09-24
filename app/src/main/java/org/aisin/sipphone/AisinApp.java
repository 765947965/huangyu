package org.aisin.sipphone;

import org.aisin.sipphone.crash.CrashHandler;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Application;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;

public class AisinApp extends Application {

	private static AisinApp instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		/*CrashHandler crash = CrashHandler.getInstance();
		crash.init(this);*/
		addShortcut();
	}

	public static Application getInstance() {
		// TODO Auto-generated method stub
		return instance;
	}

	// 创建快捷方式
	private void addShortcut() {

		// 判断快捷方式是否已经创建
		if (SharedPreferencesTools.getSharedPreferences_4shortcut(this)
				.getBoolean(SharedPreferencesTools.SPF_shortcut_key, false)) {
			return;
		}
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建
		Intent shortIntent = new Intent(Intent.ACTION_MAIN);
		shortIntent.setClassName(this,
				org.aisin.sipphone.LodingActivity.class.getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortIntent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
		// 记录快捷方式已经创建
		SharedPreferencesTools.getSharedPreferences_4shortcut(this).edit()
				.putBoolean(SharedPreferencesTools.SPF_shortcut_key, true)
				.commit();
	}
}
