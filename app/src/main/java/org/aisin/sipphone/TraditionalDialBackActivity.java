package org.aisin.sipphone;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.aisin.sipphone.commong.SwitchState;
import org.aisin.sipphone.sqlitedb.CallhistoryDBTOOls;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.AvatarID;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.internal.telephony.ITelephony;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TraditionalDialBackActivity extends Activity implements
		OnClickListener {

	private LinearLayout traditionaldialbackactivity_linlayout;
	private ImageView callback_bk_image;
	private TextView callback_name;
	private TextView guishudi;
	private ImageView callback_image_tx;
	private TextView callback_textview_wating;
	private TextView callback_textview_wating_2;
	private TextView callback_phonenum;
	private TelephonyManager managr;
	private myPhoneReceiver mpreceive;
	private boolean answallflag = true;
	private int answallswitch;
	private MediaPlayer mp;
	private Context context = TraditionalDialBackActivity.this;
	private String name;
	private String phonenum;
	private String gsdstr;
	private myclosere receiver;
	private Intent intent_s;
	private AudioManager audio;
	private boolean canclose;

	private byte[] bitmapByte;
	private Bitmap bitmaptx;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {// 倒计时退出程序
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(6000);
							mHandler.sendEmptyMessage(2);
						} catch (InterruptedException e) {
						}
					}
				}).start();
			} else if (msg.what == 2) {// 退出程序
				finish();
			} else if (msg.what == 3) {
				guishudi.setText(gsdstr);
			}
		}
	};
	private SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sfd2 = new SimpleDateFormat("yyyyMMdd");

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traditionaldialbackactivity);
		traditionaldialbackactivity_linlayout = (LinearLayout) this
				.findViewById(R.id.tradback_linlayout);
		callback_bk_image = (ImageView) this
				.findViewById(R.id.callback_bk_image);
		callback_name = (TextView) this.findViewById(R.id.callback_name);
		guishudi = (TextView) this.findViewById(R.id.guishudi);
		callback_image_tx = (ImageView) this
				.findViewById(R.id.callback_image_tx);
		callback_textview_wating = (TextView) this
				.findViewById(R.id.callback_textview_wating);
		callback_textview_wating_2 = (TextView) this
				.findViewById(R.id.callback_textview_wating_2);
		callback_phonenum = (TextView) this
				.findViewById(R.id.callback_phonenum);
		bitmapByte = (byte[]) getIntent().getSerializableExtra("Contact.tx");
		name = (String) getIntent().getSerializableExtra("Contact.name");
		phonenum = (String) getIntent()
				.getSerializableExtra("Contact.phonenum");
		if (phonenum == null || "".equals(phonenum)) {
			finish();
		}
		if (name == null || "".equals(name)) {
			name = phonenum;
		}
		callback_name.setText(name);
		if (bitmapByte == null) {
			callback_image_tx.setImageResource(AvatarID.getAvatarID());
		} else {
			bitmaptx = BitmapFactory.decodeByteArray(bitmapByte, 0,
					bitmapByte.length);
			callback_image_tx.setImageBitmap(bitmaptx);
		}
		callback_phonenum.setText(UserInfo_db.getUserInfo(
				TraditionalDialBackActivity.this).getPhone());
		callback_bk_image.setOnClickListener(this);
		answallswitch = SharedPreferencesTools.getSharedPreferences_ALLSWITCH(
				context).getInt(
				SharedPreferencesTools.SPF_ALLSWITCH_ANSWALLFLAG,
				SwitchState.ANSWALL_Y);
		intent_s = new Intent(TraditionalDialBackActivity.this,
				org.aisin.sipphone.CallingService.class);

		// 注册电话监听
		managr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mpreceive = new myPhoneReceiver();
		managr.listen(mpreceive, PhoneStateListener.LISTEN_CALL_STATE);// 自动接听

		// 注册关闭广播
		receiver = new myclosere();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName
				+ ".TraditionalDialBackActivity.close");
		registerReceiver(receiver, filter);

		// 打出电话
		// 判断播出电话是否是本帐号号码
		if (phonenum.indexOf(UserInfo_db.getUserInfo(context).getPhone()) != -1) {
			callback_textview_wating.setText("呼叫失败!");
			callback_textview_wating_2.setText("拨出的电话号码不能是登录帐号!");
			mHandler.sendEmptyMessage(1);
		} else {
			new TraditionalDialBack().execute("call");
		}
		// 打开扬声器
		SharedPreferences spfus = SharedPreferencesTools
				.getSharedPreferences_ALLSWITCH(this);
		boolean OpenMUSIC = spfus.getBoolean(SharedPreferencesTools.OpenMUSIC,
				true);
		if (!OpenMUSIC) {
			// 进入听筒模式
			audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			audio.setMode(AudioManager.MODE_IN_CALL);
			audio.setSpeakerphoneOn(false);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				mp = MediaPlayer.create(context, R.raw.voice);
				mp.setLooping(true);
				mp.start();
			}
		}).start();

		// 开启线程查询归属地
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<String> arry = new ArrayList<String>();
				arry.add(phonenum);
				try {
					gsdstr = GetPhoneInfo4DB.getInfo(
							TraditionalDialBackActivity.this, arry).get(0);
					mHandler.sendEmptyMessage(3);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		disall();
		RecoveryTools.unbindDrawables(traditionaldialbackactivity_linlayout);// 回收容器
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(canclose){
			finish();
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
		if (audio != null) {
			audio.setMode(AudioManager.MODE_NORMAL);
			audio = null;
		}
		canclose = true;
	}

	private void disall() {
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
		// 注销注册的广播
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
		if (managr != null) {
			// 置监听器为空
			managr.listen(mpreceive, PhoneStateListener.LISTEN_NONE);
			managr = null;
		}
		if (bitmaptx != null) {
			bitmaptx.recycle();
			bitmaptx = null;
		}
		bitmapByte = null;
		// 回收背景
	}

	class myPhoneReceiver extends PhoneStateListener {
		private Handler mHandler_s = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == 1) {
					try {
						try {
							TelephonyManager tm = (TelephonyManager) context
									.getSystemService(context.TELEPHONY_SERVICE);
							Class c = Class.forName(tm.getClass().getName());
							Method m = c.getDeclaredMethod("getITelephony");
							m.setAccessible(true);
							ITelephony telephonyService;
							telephonyService = (ITelephony) m.invoke(tm);
							telephonyService.answerRingingCall();
						} catch (Exception e) {
							Intent intent = new Intent(
									"android.intent.action.MEDIA_BUTTON");
							KeyEvent keyEvent = new KeyEvent(
									KeyEvent.ACTION_DOWN,
									KeyEvent.KEYCODE_HEADSETHOOK);
							intent.putExtra("android.intent.extra.KEY_EVENT",
									keyEvent);
							context.sendOrderedBroadcast(intent,
									"android.permission.CALL_PRIVILEGED");
							intent = new Intent(
									"android.intent.action.MEDIA_BUTTON");
							keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
									KeyEvent.KEYCODE_HEADSETHOOK);
							intent.putExtra("android.intent.extra.KEY_EVENT",
									keyEvent);
							context.sendOrderedBroadcast(intent,
									"android.permission.CALL_PRIVILEGED");
						}
					} catch (Exception e) {
					} catch (Error e) {
					}
				}
			}
		};

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 电话呼入
				Log.i("环宇", "RINGING");
				if (answallflag) {
					answallflag = false;
				} else {
					break;
				}
				// 启动悬浮框
				if (intent_s != null) {
					intent_s.putExtra("outname", name);
					intent_s.putExtra("outphonenum", phonenum);
					context.startService(intent_s);
				}
				// 自动接听
				if (answallswitch == SwitchState.ANSWALL_Y
						&& PhoneInfo.SDKVersion < 20) {
					mHandler_s.sendEmptyMessage(1);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE:// 电话挂断
				Log.i("环宇", "IDLE");
				// 关闭悬浮框
				if (intent_s != null) {
					context.stopService(intent_s);
				}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.callback_bk_image) {
			finish();
		}
	}

	class TraditionalDialBack extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// 获取拨打电话的URL
			String url = URLTools.GetHttpURL_4CallOut(context,
					CallPhoneManage.fomartphonenum(phonenum));
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -11;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = json.optInt("result", -11);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:
				// 呼叫成功
				// 插入通话记录
				try {
					ContentValues values = new ContentValues();
					values.put("phone", phonenum);
					if (!phonenum.equals(name)) {
						values.put("name", name);
					}
					values.put("call_type", 2);// 1呼入 2呼出 3未接 0挂断
					values.put("call_type_name", "回拨通话");
					Date date = new Date();
					String call_time = sfd.format(date);
					values.put("call_time", call_time);// 时间
					values.put("calltime", Integer.parseInt(sfd2.format(date)));// 用来条件插叙的整型事件
					// values.put("duration", "00:00:00");
					CallhistoryDBTOOls.addhistory(
							TraditionalDialBackActivity.this, values);
				} catch (Exception e) {
				}
				// 清除拨号盘号码
				context.sendBroadcast(new Intent(Constants.BrandName
						+ ".dial.clearnum"));
				// 发送广播通知通话记录变更
				context.sendBroadcast(new Intent(Constants.BrandName
						+ ".insert.callhistory"));
				return;
			case -1:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("账户余额查找失败，请重新登录!");
				mHandler.sendEmptyMessage(1);
				break;
			case -2:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("密码错误，请重新登录!");
				mHandler.sendEmptyMessage(1);
				break;
			case -3:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("呼叫的号码错误或短号对应的号码不存在!");
				mHandler.sendEmptyMessage(1);
				break;
			case -4:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("绑定手机号码查找失败，请重新注册!");
				mHandler.sendEmptyMessage(1);
				break;
			case -6:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("Sign错误!");
				mHandler.sendEmptyMessage(1);
				break;
			case -8:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("您的帐户已过有效期，须充值激活!");
				mHandler.sendEmptyMessage(1);
				break;
			case -9:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("余额不足，请充值!");
				mHandler.sendEmptyMessage(1);
				break;
			case -10:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("帐户已被冻结，请联系客服人员!");
				mHandler.sendEmptyMessage(1);
				break;
			case -11:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("后台程序错误!");
				mHandler.sendEmptyMessage(1);
				break;
			case -12:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("后台程序错误!");
				mHandler.sendEmptyMessage(1);
				break;
			case -13:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("后台程序错误!");
				mHandler.sendEmptyMessage(1);
				break;
			default:
				callback_textview_wating.setText("呼叫失败!");
				callback_textview_wating_2.setText("联网失败,请检查网络连接!");
				mHandler.sendEmptyMessage(1);
				break;
			}
		}
	}

	class myclosere extends BroadcastReceiver {// 用户接收广播 关闭本界面
												// 广播在浮窗的service关闭的时候发送

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			TraditionalDialBackActivity.this.finish();
		}
	}
}
