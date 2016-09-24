package org.aisin.sipphone;

import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 启动“来电回拨”悬浮框service
 */
public class CallingService extends Service {

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams wmParams;
	private TextView textView;
	private String phonenum;
	private String name;
	private boolean addflag = false;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		name = intent.getStringExtra("outname");
		phonenum = intent.getStringExtra("outphonenum");
		textView = new TextView(getApplicationContext());
		if (phonenum == null || "".equals(phonenum)) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		// 显示悬浮框
		initView();
		return super.onStartCommand(intent, flags, startId);
	}

	private void initView() {

		int temp = 100;
		if ("".equals(name) || phonenum.equals(name)) {
			textView.setText(phonenum);
		} else {
			textView.setText(name + "\n" + phonenum);
			temp = 200;
		}

		textView.setTextSize(28);
		textView.setGravity(Gravity.CENTER);

		textView.setBackgroundColor(Color.parseColor("#11BB79"));
		textView.setWidth(PhoneInfo.width - 20);
		textView.setHeight(temp);
		// 获取WindowManager
		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		// wmParams = ((MyApplication) getApplication()).getMywmParams();
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		// wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;

		wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;// 不接受任何按键事件
		wmParams.gravity = Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.y = 15;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.format = PixelFormat.RGBA_8888;

		// 是否开启悬浮框
		String xfkkg = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(this)
				.getString(
						SharedPreferencesTools.SPF_msglist_date_acall_linkman_tips_open,
						null);
		if (PhoneInfo.SDKVersion < 20 && !TextUtils.isEmpty(xfkkg)
				&& "1".equals(xfkkg)) {
			mWindowManager.addView(textView, wmParams);
			addflag = true;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 发送广播 让呼出电话的界面关掉
		sendBroadcast(new Intent(Constants.BrandName
				+ ".TraditionalDialBackActivity.close"));
		if (PhoneInfo.SDKVersion < 20 && addflag) {
			mWindowManager.removeView(textView);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
