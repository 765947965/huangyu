package org.aisin.sipphone;

import java.util.ArrayList;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tianyu.R;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CopyOfTraditionalDialBackActivity_v2 {

	private static View rootview;
	private static RelativeLayout callout_diel_gd_image;
	private static ImageView callback_image_tx;
	private static TextView callback_name;
	private static TextView callback_phonenum;
	private static TextView callback_phonenum_gsd, bttipss, ttttps;
	private static String gsdstr;
	private static WindowManager windowManager;
	private static Context context;
	private static String dephone;
	private static int jishiqi;
	private static int jishiqi2;
	public static boolean lianjiechengong;
	private static String[] tpissrc = { "卫星信号连接中.", "卫星信号连接中..", "卫星信号连接中..." };
	private static int diandiandian;

	private static Bitmap bitmaptx;
	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 4) {
				try {
					callback_phonenum_gsd.setText(gsdstr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (msg.what == 1) {
				try {
					ContentResolver resolver = context.getContentResolver();
					/* 这里涉及到内容提供者的知识，其实这里是直接在操作 Android 的数据库，十分痛苦 */
					resolver.delete(CallLog.Calls.CONTENT_URI, "number = ?",
							new String[] { dephone });
					if (dephone.contains(",")) {
						resolver.delete(CallLog.Calls.CONTENT_URI,
								"number = ?",
								new String[] { dephone.split(",")[0] });
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (msg.what == 2) {
				try {
					diandiandian = -1;
					lianjiechengong = true;
					if (jishiqi < 0) {
						mHandler.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (msg.what == 3) {
				try {
					if (lianjiechengong) {
						if (jishiqi2 >= 0) {
							bttipss.setText("等待系统接通(" + jishiqi2 + ")秒");
							jishiqi2--;
							mHandler.sendEmptyMessageDelayed(3, 1000);
						} else {
							bttipss = null;
							ttttps = null;
							mHandler.sendEmptyMessage(1);
							windowManager.removeViewImmediate(rootview);
						}
					} else {
						if (jishiqi >= 0) {
							bttipss.setText("卫星信号连接中(" + jishiqi + ")秒");
							jishiqi--;
							mHandler.sendEmptyMessageDelayed(3, 1000);
						} else {
							bttipss.setText("卫星信号连接中...");
						}
					}
				} catch (Exception e) {
				}
			} else if (msg.what == 5) {
				jishiqi2 = -1;
			} else if (msg.what == 6) {
				if (ttttps != null) {
					if (diandiandian >= 0) {
						ttttps.setText(tpissrc[diandiandian % 3]);
						diandiandian++;
						mHandler.sendEmptyMessageDelayed(6, 1000);
					} else {
						ttttps.setText("呼叫请求成功");
					}
				}
			}
		}
	};

	public static void addcreatview(final Context context, byte[] bitmapByte,
			String name, final String phonenum, String dephone) {

		try {
			CopyOfTraditionalDialBackActivity_v2.context = context;
			CopyOfTraditionalDialBackActivity_v2.dephone = dephone;
			rootview = LayoutInflater.from(context).inflate(
					R.layout.cptraditionaldialbackactivity_v2, null);
			callout_diel_gd_image = (RelativeLayout) rootview
					.findViewById(R.id.callout_diel_gd_image);
			callout_diel_gd_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 去除界面
					try {
						bttipss = null;
						ttttps = null;
						mHandler.sendEmptyMessage(1);
						windowManager.removeViewImmediate(rootview);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			callback_name = (TextView) rootview
					.findViewById(R.id.callback_name);
			callback_image_tx = (ImageView) rootview
					.findViewById(R.id.callback_image_tx);
			callback_phonenum = (TextView) rootview
					.findViewById(R.id.callback_phonenum);
			callback_phonenum_gsd = (TextView) rootview
					.findViewById(R.id.callback_phonenum_gsd);
			bttipss = (TextView) rootview.findViewById(R.id.bttipss);
			ttttps = (TextView) rootview.findViewById(R.id.ttttps);
			if (name == null || "".equals(name)) {
				name = phonenum;
			}
			callback_name.setText(name);
			if (bitmapByte == null) {
				callback_image_tx.setImageResource(R.drawable.defaultuserimage);
			} else {
				bitmaptx = BitmapFactory.decodeByteArray(bitmapByte, 0,
						bitmapByte.length);
				callback_image_tx.setImageBitmap(bitmaptx);
			}
			callback_phonenum.setText(phonenum);

			windowManager = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
			// 设置LayoutParams(全局变量）相关参数
			WindowManager.LayoutParams windowManagerParams = new WindowManager.LayoutParams();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
					|| "xiaomi".equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
				windowManagerParams.type = WindowManager.LayoutParams.TYPE_TOAST;
			} else {
				windowManagerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
			}
			windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
			// 设置Window flag
			windowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
					| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

			windowManagerParams.gravity = Gravity.TOP;
			// 以屏幕左上角为原点，设置x、y初始值
			windowManagerParams.x = 0;
			windowManagerParams.y = 0;
			// 设置悬浮窗口长宽数据
			windowManagerParams.width = WindowManager.LayoutParams.MATCH_PARENT;
			windowManagerParams.height = WindowManager.LayoutParams.MATCH_PARENT;

			// 显示myFloatView图像
			windowManager.addView(rootview, windowManagerParams);

			// 开启线程查询归属地
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<String> arry = new ArrayList<String>();
					arry.add(phonenum);
					try {
						gsdstr = GetPhoneInfo4DB.getInfo(context, arry).get(0);
						mHandler.sendEmptyMessage(4);
					} catch (Exception e) {
					}
				}
			}).start();
			jishiqi = 24;
			jishiqi2 = 14;
			diandiandian = 0;
			lianjiechengong = false;
			mHandler.sendEmptyMessage(3);
			mHandler.sendEmptyMessage(6);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void removewcreatview() {
		mHandler.sendEmptyMessage(2);
	}

	public static void removewcreatviewalll() {
		mHandler.sendEmptyMessage(5);
	}
}
