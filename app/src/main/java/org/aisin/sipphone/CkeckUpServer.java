package org.aisin.sipphone;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.NotificationConstants;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class CkeckUpServer extends Service {

	private String ver;
	private String update_addr;
	private String update_tips;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				CkeckUpServer.this.stopSelf();
			} else if (msg.what == 2) {
				if (android.os.Build.VERSION.SDK_INT < 12) {
					CkeckUpServer.this.stopSelf();
					return;
				}
				// 发送通知栏 有新版本应用程序
				NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						CkeckUpServer.this);
				Intent service = new Intent(CkeckUpServer.this,
						org.aisin.sipphone.DwonNewAppService.class);
				service.putExtra("ver", ver);
				service.putExtra("update_addr", update_addr);
				service.putExtra("update_tips", update_tips);
				PendingIntent pendingIntent = PendingIntent.getService(
						CkeckUpServer.this,
						NotificationConstants.NrequestCode_UP, service,
						PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder.setLargeIcon(BitmapFactory.decodeResource(
						CkeckUpServer.this.getResources(),
						R.drawable.ic_launcher));
				mBuilder.setSmallIcon(R.drawable.notificationimage);
				mBuilder.setContentTitle("环宇新版本升级啦");
				mBuilder.setContentText(update_tips);
				mBuilder.setTicker("发现环宇新版本\n" + update_tips);
				mBuilder.setOngoing(false);// true无法通过左右滑动清除
				mBuilder.setAutoCancel(true);// 点击后消失
				mBuilder.setContentIntent(pendingIntent);
				mBuilder.setDefaults(Notification.DEFAULT_ALL);
				mBuilder.setNumber(1);
				mNotificationManager
						.notify(NotificationConstants.NrequestCode_UP,
								mBuilder.build());
				CkeckUpServer.this.stopSelf();
			}
		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		/*		if (!CheckUpadateTime.CheckResult_4Ckeckupserver_uptime(this)) {
			stopSelf();
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					String url = URLTools
							.GetHttpURL_4CheckUpdate(CkeckUpServer.this);
					String result = HttpUtils.result_url_get(url,
							"{'result':'-14'}");
					JSONObject jsonobject = new JSONObject(result.trim());
					update_addr = jsonobject.optString("update_addr");
					update_tips = jsonobject.optString("update_tips");
					ver = jsonobject.optString(SharedPreferencesTools.upAPPVer);
					int result_int = jsonobject.optInt("result");
					if (result_int != 0) {
						mHandler.sendEmptyMessage(1);
						return;
					}
					// 存储更新时间
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
					String date = sdf.format(new Date());
					SharedPreferencesTools
							.getSharedPreferences_4UPSERVER(CkeckUpServer.this)
							.edit()
							.putString(SharedPreferencesTools.CUPDATE, date)
							.commit();
					if (update_addr != null && !"".equals(update_addr)) {
						mHandler.sendEmptyMessage(2);
					} else {
						mHandler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					mHandler.sendEmptyMessage(1);
				}
			}
		}).start();*/
	}

}
