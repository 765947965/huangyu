package org.aisin.sipphone;

import java.io.File;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;
import org.aisin.sipphone.tools.NotificationConstants;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

public class DwonNewAppService extends Service {

	private String ver;
	private String update_addr;
	private String update_tips;
	private String ALBUM_PATH = FileManager.getFileDir() + "/"
			+ Constants.BrandName + "/";
	private int progress;

	private Handler mHandler;

	private void init() {
		mHandler = new Handler() {

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					DwonNewAppService.this);

			{
				mBuilder.setLargeIcon(BitmapFactory.decodeResource(
						DwonNewAppService.this.getResources(),
						R.drawable.ic_launcher));
				mBuilder.setSmallIcon(R.drawable.notificationimage);
				mBuilder.setContentTitle("正在下载新版本...");
				mBuilder.setContentText(update_tips);
				mBuilder.setTicker("正在下载新版本...");
				mBuilder.setOngoing(true);// true无法通过左右滑动清除
				mBuilder.setAutoCancel(false);// true 点击后消失
				mBuilder.setProgress(100, 0, false);
				mBuilder.setNumber(1);
				mNotificationManager.notify(
						NotificationConstants.NrequestCode_UPDown,
						mBuilder.build());
			}

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if (msg.what == 1) {
					mBuilder.setContentText(progress + "%");
					mBuilder.setProgress(100, progress, false);
					mNotificationManager.notify(
							NotificationConstants.NrequestCode_UPDown,
							mBuilder.build());
				} else if (msg.what == 2) {
					Intent install = new Intent(Intent.ACTION_VIEW);
					install.setDataAndType(
							Uri.fromFile(new File(ALBUM_PATH
									+ Constants.BrandName + ver + ".apk")),
							"application/vnd.android.package-archive");
					install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					DwonNewAppService.this.startActivity(install);
					mNotificationManager
							.cancel(NotificationConstants.NrequestCode_UPDown);
					DwonNewAppService.this.stopSelf();
				} else if (msg.what == 3) {
					mBuilder.setContentTitle("下载失败，请检查网络连接!");
					mBuilder.setTicker("下载失败!");
					mBuilder.setOngoing(false);// true无法通过左右滑动清除
					mBuilder.setAutoCancel(true);// true 点击后消失
					mNotificationManager.notify(
							NotificationConstants.NrequestCode_UPDown,
							mBuilder.build());
					DwonNewAppService.this.stopSelf();
				}
			}
		};
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent == null || android.os.Build.VERSION.SDK_INT < 12) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		ver = intent.getStringExtra("ver");
		update_addr = intent.getStringExtra("update_addr");
		update_tips = intent.getStringExtra("update_tips");
		if (ver == null || update_addr == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		init();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpUtils http = new HttpUtils();
				http.download(update_addr, ALBUM_PATH + Constants.BrandName
						+ ver + ".apk", true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
						false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
						new RequestCallBack<File>() {
							@Override
							public void onStart() {
								// testTextView.setText("conn...");
							}

							@Override
							public void onLoading(long total, long current,
									boolean isUploading) {
								progress = (int) (((double) current / (double) total) * 100d);
								mHandler.sendEmptyMessage(1);
							}

							@Override
							public void onSuccess(
									ResponseInfo<File> responseInfo) {
								mHandler.sendEmptyMessage(2);
							}

							@Override
							public void onFailure(HttpException error,
									String msg) {
								if (416 == error.getExceptionCode()) {
									mHandler.sendEmptyMessage(2);
								} else if (0 == error.getExceptionCode()) {
									mHandler.sendEmptyMessage(3);
								} else {
									mHandler.sendEmptyMessage(3);
								}
							}
						});
			}
		}).start();
		return super.onStartCommand(intent, flags, startId);
	}

}
