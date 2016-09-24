package org.aisin.sipphone.tools;

import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

public class AddRedToold {
	@SuppressLint("SimpleDateFormat")
	public static synchronized void addred(Context context, RedObject rob_new,
			String direct) { // direct 写入收红包方默认为空字符串"" 写入发红包数据必须传"sended"
		// 当前未拆红包数
		int tempcnot = 0;
		if ("".equals(direct)) {
			tempcnot = SharedPreferencesTools.getSharedPreferences_4RED_COUT(
					context).getInt(SharedPreferencesTools.REhasnum_key, 0);
		}

		if ("".equals(direct) && rob_new.getHas_open() == 0) {
			tempcnot += 1;
			// 存储未拆数就行了
			SharedPreferencesTools.getSharedPreferences_4RED_COUT(context)
					.edit()
					.putBoolean(SharedPreferencesTools.REDCOUT_key, true)
					.putInt(SharedPreferencesTools.REhasnum_key, tempcnot)
					.commit();
			// 发广播更新红点提醒
			context.sendBroadcast(new Intent(Constants.BrandName
					+ ".redcedbd.upreddate.cannotcheck"));
		}
		// 红包数据加入数据库
		RedData_DBHelp.addRedDatas(context, rob_new);
	}
}
