package org.aisin.sipphone.directcall;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.sqlitedb.CallhistoryDBTOOls;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AisinOutCallActivity extends Activity implements OnClickListener {

	private static AisinOutCallActivity instance;
	private LinearLayout linear_layout, linear_outcall_bottom;
	private ImageButton img_micro, img_speaker; // 静音，免提
	private ImageView img_outcallImage; // 挂断
	private ImageButton btnContacts, img_hangUp; // 联系人
	private ImageButton btnDialkey; // 拨号盘
	private boolean isSpeakerEnabled = true, isMicMuted = false;
	private TextView tv_phoneNumOrName, tv_description, tv_back, tv_view,
			guishudi;
	private View outcall_view, outcall_img_padd;
	private Chronometer timer; // 拨打分钟数
	private String phoneNum;
	private String gsdstr;
	private Vibrator mVibrator;
	private MediaPlayer mRingerPlayer;
	private AudioManager audio;
	private String contactName; // 联系人姓名
	private int currVolume; // 当前音量
	private Timer timerRing, timerOutCall; // 响铃
	private int countRing = 0, countOutCall;
	private String[] dataring, dataOutCall;
	private int callDuration;
	private LinearLayout ll_outcall, ll_dialkey, ll_outcall_top; // 打电话界面,//拨号盘,
	private boolean show_dial = false; // 是否显示拨号盘
	private boolean show_back = true; // 通讯录是否显示返回按钮
	private boolean iscallError = false;
	private boolean istimerStart = false;
	private boolean isconnected = false;
	private boolean isregister;
	private ImageButton btn_dialkey_0, btn_dialkey_1, btn_dialkey_2,
			btn_dialkey_3, btn_dialkey_4, btn_dialkey_5, btn_dialkey_6,
			btn_dialkey_7, btn_dialkey_8, btn_dialkey_9, btn_dialkey_jin,
			btn_dialkey_mi;

	private byte[] bitmapimage;
	private SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sfd2 = new SimpleDateFormat("yyyyMMdd");

	private final String ACTION_NAME = "send_broadcast";
	SharedPreferences shared = null;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) { // 电话接通开始计时

				tv_description.setVisibility(View.GONE);
				timer.setVisibility(View.VISIBLE);
				isMicMuted = AisinManager.getLc().isMicMuted();
				callDuration = msg.arg1;
				timer.setBase(SystemClock.elapsedRealtime() - 1000
						* callDuration);
				timer.start();
				istimerStart = true;

			}

			if (msg.what == 1) {
				// 显示响铃状态

				if (countRing > 2)
					countRing = 0;

				tv_description.setText(dataring[countRing]);
				countRing++;
			}

			if (msg.what == 2) {
				stopTimerOutCall();
				stopTimerRing();
				stopRing();

				tv_description.setVisibility(View.VISIBLE);
				tv_description.setText("通话已结束");
				if (!iscallError && istimerStart) {
					timer.stop();
					String callDuration = timer.getText().toString();
					if (callDuration.length() <= 5)
						callDuration = "00:" + callDuration;
					addRecodes(phoneNum, contactName, callDuration, "直拨通话");

					timer.setVisibility(View.GONE);

				} else if (!iscallError && !isconnected) {
					addRecodes(phoneNum, contactName, "", "通话取消");
				}

			}

			if (msg.what == 3) {
				if (mVibrator != null) {
					mVibrator.cancel();
					AisinOutCallActivity.this.finish();
				}
			}
			if (msg.what == 4) {
				if (mVibrator != null) {
					mVibrator.cancel();

				}
			}

			if (msg.what == 5) {
				// 显示响铃状态

				if (countOutCall > 2)
					countOutCall = 0;

				tv_description.setText(dataOutCall[countOutCall]);
				countOutCall++;
			} else if (msg.what == 6) {
				guishudi.setText(gsdstr);
			}

		};
	};

	public static AisinOutCallActivity instance() {
		return instance;
	}

	public static boolean isInstanciated() {
		return instance != null;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_out_call);
		registerBoradcastReceiver();
		instance = this;
		if (!AisinManager.isInstanciated())
			AisinManager.createAndStart(instance);
		phoneNum = getIntent().getStringExtra("Contact.phonenum");
		contactName = getIntent().getStringExtra("Contact.name");
		bitmapimage = (byte[]) getIntent().getSerializableExtra("Contact.tx");

		initView();
		startRing();
		outCall();

	}

	private void call() {
		if (instance != null && Check_network.isNetworkAvailable(instance)) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					AisinManager.getInstance().newOutgoingCall(
							CallPhoneManage.fomartphonenum(phoneNum),
							contactName, bitmapimage);

				}
			});
		} else {
			callFinish();
			new AisinBuildDialog(instance, "提示", "请检查你的网络");

		}
	}

	@SuppressLint("InlinedApi")
	private void initView() {
		linear_layout = (LinearLayout) findViewById(R.id.linear_layout);

		img_micro = (ImageButton) findViewById(R.id.outcall_micro);
		img_speaker = (ImageButton) findViewById(R.id.outcall_speaker);
		btnContacts = (ImageButton) findViewById(R.id.outcall_contact);
		btnDialkey = (ImageButton) findViewById(R.id.outcall_dialkey);
		img_hangUp = (ImageButton) findViewById(R.id.outcall_hangUp);
		tv_phoneNumOrName = (TextView) findViewById(R.id.outcall_Num);
		guishudi = (TextView) findViewById(R.id.guishudi);
		tv_description = (TextView) findViewById(R.id.outcall_decription);
		img_outcallImage = (ImageView) findViewById(R.id.outcall_image);
		timer = (Chronometer) findViewById(R.id.outcall_callTimer);
		outcall_view = (View) findViewById(R.id.outcall_view);
		outcall_img_padd = (View) findViewById(R.id.outcall_img_padding);
		tv_view = (TextView) findViewById(R.id.tv_view);
		img_micro.setOnClickListener(this);
		img_speaker.setOnClickListener(this);
		img_hangUp.setOnClickListener(this);
		btnContacts.setOnClickListener(this);
		btnDialkey.setOnClickListener(this);

		if (TextUtils.isEmpty(contactName))
			tv_phoneNumOrName.setText(phoneNum);

		else
			tv_phoneNumOrName.setText(contactName);

		if (bitmapimage != null)
			img_outcallImage.setImageBitmap(BitmapFactory.decodeByteArray(
					bitmapimage, 0, bitmapimage.length));
		mVibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audio.setMode(AudioManager.MODE_IN_CALL);
		audio.setSpeakerphoneOn(false);
		timerRing = new Timer();
		timerOutCall = new Timer();
		dataring = new String[] { "正在响铃.", "正在响铃..", "正在响铃..." };
		dataOutCall = new String[] { "正在呼叫.", "正在呼叫..", "正在呼叫..." };
		currVolume = audio.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		// 开启线程查询归属地
		new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<String> arry = new ArrayList<String>();
				arry.add(phoneNum);
				try {
					gsdstr = GetPhoneInfo4DB.getInfo(AisinOutCallActivity.this,
							arry).get(0);
					mHandler.sendEmptyMessage(6);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	// 开始呼叫
	private void outCall() {

		timerOutCall.schedule(new TimerTask() {

			@Override
			public void run() {

				Message msg = mHandler.obtainMessage(5);
				mHandler.sendMessage(msg);
			}
		}, 0, 1000);

	}

	// 早期媒体流
	private void earlyMedia() {
		stopTimerOutCall();
		stopRing();
		AisinManager.getLc().muteMic(false);
		audio.setMode(AudioManager.MODE_IN_COMMUNICATION);

		timerRing.schedule(new TimerTask() {

			@Override
			public void run() {

				Message msg = mHandler.obtainMessage(1);
				mHandler.sendMessage(msg);
			}
		}, 0, 1000);

	}

	// 电话接通

	private void connected() {
		isconnected = true;
		long[] patern = { 0, 200, 2000, 100 };
		mVibrator.vibrate(patern, 1);
		Message msgfinish = mHandler.obtainMessage(4);
		mHandler.sendMessageDelayed(msgfinish, 500);
		stopTimerRing();
		// 清除拨号盘号码
		AisinOutCallActivity.this.sendBroadcast(new Intent(Constants.BrandName
				+ ".dial.clearnum"));
	}

	// 开始通话
	private void streamRunning(LinphoneCall call) {

		Message msg = mHandler.obtainMessage(0);
		msg.arg1 = call.getDuration();
		mHandler.sendMessage(msg);
	}

	// 通话结束

	private void callFinish() {

		if (mBroadcastReceiver != null) {
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}

		AisinService.instance().setIscall(false);
		AisinManager.getInstance().unregister();
		audio.setMode(AudioManager.MODE_NORMAL);
		img_hangUp.setImageResource(R.drawable.handup);
		long[] patern = { 0, 200, 3000, 100 };
		mVibrator.vibrate(patern, 1);
		Message msg = mHandler.obtainMessage(2);
		mHandler.sendMessage(msg);

		Message msgfinish = mHandler.obtainMessage(3);
		mHandler.sendMessageDelayed(msgfinish, 2000);
	}

	// 播放拨打音乐
	private synchronized void startRing() {
		audio.setSpeakerphoneOn(false);
		if (mRingerPlayer == null) {
			mRingerPlayer = MediaPlayer.create(this, R.raw.localring);
			mRingerPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mRingerPlayer.setLooping(true);

		}
	}

	// 停止播放
	public void stopRing() {

		if (mRingerPlayer != null) {
			mRingerPlayer.stop();
			mRingerPlayer.release();
			mRingerPlayer = null;
		}

	}

	private void stopTimerOutCall() {
		if (timerOutCall != null) {
			timerOutCall.cancel();
			timerOutCall = null;
		}
	}

	// 停止响铃
	private void stopTimerRing() {
		if (timerRing != null) {
			timerRing.cancel();
			timerRing = null;
		}
	}

	@Override
	public void onClick(View v) {
		String keyCode;

		switch (v.getId()) {
		case R.id.outcall_micro: // 静音
			toggleMicro();
			break;
		case R.id.outcall_speaker: // 免提
			toggleSpeaker();
			break;
		case R.id.outcall_hangUp: // 挂断
			handUp();
			break;
		case R.id.outcall_contact: // 显示通讯录
			Intent intent = new Intent(this, ContactsActivity.class);
			intent.putExtra("show_back", true);
			startActivity(intent);

			break;
		case R.id.outcall_dialkey: // 显示拨号盘
			initDialKey();
			ll_outcall = (LinearLayout) findViewById(R.id.outcall_show);
			ll_dialkey = (LinearLayout) findViewById(R.id.dialkey_show);
			linear_outcall_bottom = (LinearLayout) findViewById(R.id.outcall_bottom);

			ll_outcall_top = (LinearLayout) findViewById(R.id.outcall_top);
			show_dial = true;

			tv_back.setVisibility(View.VISIBLE);
			ll_dialkey.setVisibility(View.VISIBLE);
			ll_outcall.setVisibility(View.GONE);
			ll_outcall_top.setOrientation(LinearLayout.HORIZONTAL);
			ll_outcall_top.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 0, 2.0f));
			img_outcallImage.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.20f));
			img_outcallImage.setPadding(30, 0, 0, 0);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 3.0f);
			lp.gravity = Gravity.CENTER;
			tv_description.setLayoutParams(lp);
			tv_description.setGravity(Gravity.CENTER);
			timer.setLayoutParams(lp);
			timer.setGravity(Gravity.CENTER);
			tv_view.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.2f));
			outcall_view.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.WRAP_CONTENT, 1.15f));
			outcall_img_padd.setVisibility(View.VISIBLE);
			linear_outcall_bottom
					.setLayoutParams(new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT, 0, 2.0f));
			break;

		case R.id.outcall_go_back: // 打电话界面
			dialKeyBack();

			break;

		case R.id.dialkey_00:
			keyCode = "0";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_01:
			keyCode = "1";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_02:
			keyCode = "2";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_03:
			keyCode = "3";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_04:
			keyCode = "4";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_05:
			keyCode = "5";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_06:
			keyCode = "6";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_07:
			keyCode = "7";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_08:
			keyCode = "8";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_09:

			keyCode = "9";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_btn_jin:

			keyCode = "#";
			sendKey(keyCode.charAt(0));
			break;
		case R.id.dialkey_btn_mi:

			keyCode = "*";
			sendKey(keyCode.charAt(0));

			break;

		default:
			break;
		}

	}

	// 返回打电话界面
	private void dialKeyBack() {
		show_dial = false;

		tv_back.setVisibility(View.INVISIBLE);
		ll_dialkey.setVisibility(View.GONE);
		ll_outcall.setVisibility(View.VISIBLE);
		ll_outcall_top.setOrientation(LinearLayout.VERTICAL);
		ll_outcall_top.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 4.0f));
		img_outcallImage.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 4.0f));
		img_outcallImage.setPadding(0, 0, 0, 0);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 2.0f);
		lp.gravity = Gravity.CENTER;
		tv_description.setLayoutParams(lp);
		tv_description.setGravity(Gravity.CENTER);
		timer.setLayoutParams(lp);
		timer.setGravity(Gravity.CENTER);
		tv_view.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 4.0f));
		outcall_view.setLayoutParams(new LinearLayout.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 1.0f));
		outcall_img_padd.setVisibility(View.GONE);
		linear_outcall_bottom.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 2.2f));
	}

	// 挂断
	@SuppressLint("NewApi")
	private void handUp() {
		// img_hangUp.setBackground(null);
		img_hangUp.setImageResource(R.id.outcall_hangUp);
		if (audio != null)
			audio.setMode(AudioManager.MODE_NORMAL);
		LinphoneCore lc = AisinManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();
		Log.i("环宇", "114");
		if (currentCall != null) {
			lc.terminateCall(currentCall);
		} else if (lc.isInConference()) {
			lc.terminateConference();
		} else {
			lc.terminateAllCalls();
		}
		Log.i("环宇", "115");
		if (show_dial)
			dialKeyBack();
		callFinish();
	}

	// 静音
	private void toggleMicro() {
		LinphoneCore lc = AisinManager.getLc();
		isMicMuted = lc.isMicMuted();

		isMicMuted = !isMicMuted;
		lc.muteMic(isMicMuted);

		if (isMicMuted) {
			img_micro.setImageResource(R.drawable.micro_on);

		} else {

			img_micro.setImageResource(R.drawable.micro_off);

		}
	}

	// 麦克风
	private void toggleSpeaker() {

		audio.setSpeakerphoneOn(isSpeakerEnabled);
		if (isSpeakerEnabled) {
			AisinManager.getInstance().routeAudioToSpeaker();
			img_speaker.setImageResource(R.drawable.speaker_on);
			AisinManager.getLc().enableSpeaker(isSpeakerEnabled);
			isSpeakerEnabled = false;
		} else {

			AisinManager.getInstance().routeAudioToReceiver();
			img_speaker.setImageResource(R.drawable.speaker_off);
			isSpeakerEnabled = true;

		}
	}

	// 发送key
	private void sendKey(char keycode) {
		AisinManager.getInstance().playDtmf(this.getContentResolver(), keycode);
		LinphoneCore lc = AisinManager.getLc();
		lc.stopDtmf();

		if (lc.isIncall()) {
			lc.sendDtmf(keycode);
		}

	}

	@Override
	protected void onResume() {
		if (mRingerPlayer != null)
			mRingerPlayer.start();

		super.onResume();

	}

	@Override
	protected void onPause() {
		// stopRing();

		super.onPause();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mBroadcastReceiver != null)
			unregisterReceiver(mBroadcastReceiver);
		audio.setMode(AudioManager.MODE_NORMAL);
		audio = null;
		stopRing();
		stopTimerRing();
		stopTimerOutCall();
		timer.stop();
		instance = null;
		if (mVibrator != null) {
			mVibrator.cancel();
		}

		LinphoneCore lc = AisinManager.getLc();
		LinphoneCall currentCall = lc.getCurrentCall();

		if (currentCall != null) {
			lc.terminateCall(currentCall);
		}
		if (AisinService.instance().isIscall())
			AisinService.instance().setIscall(false);
		AisinManager.getInstance().unregister();
		Intent intent = new Intent(this,
				org.aisin.sipphone.directcall.AisinService.class);
		stopService(intent);
		super.onDestroy();
		if (linear_layout != null) {
			RecoveryTools.unbindDrawables(linear_layout);// 回收容器
		}
		System.gc();
	}

	public boolean isShow_back() {
		return show_back;
	}

	public void setShow_back(boolean show_back) {
		this.show_back = show_back;
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ACTION_NAME)) {
				int status = intent.getIntExtra("status", 0);
				switch (status) {

				case 1:

					tv_view.setVisibility(View.VISIBLE);
					tv_view.setText("服务注册失败");
					if (show_dial)
						dialKeyBack();
					callFinish();
					break;

				case 2:
					earlyMedia();
					break;
				case 3:
					connected();
					break;

				case 4:
					LinphoneCall call = AisinManager.getLc().getCurrentCall();
					streamRunning(call);
					break;
				case 5:
					iscallError = true;
					tv_view.setVisibility(View.VISIBLE);
					tv_view.setText("呼叫失败!");
					if (show_dial)
						dialKeyBack();
					callFinish();
					break;

				case 6:
					if (show_dial)
						dialKeyBack();
					callFinish();
					break;
				case 7:
					call();
					break;

				default:
					break;
				}
			}
		}

	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(ACTION_NAME);
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private void initDialKey() {

		tv_back = (TextView) findViewById(R.id.outcall_go_back);

		btn_dialkey_0 = (ImageButton) findViewById(R.id.dialkey_00);
		btn_dialkey_1 = (ImageButton) findViewById(R.id.dialkey_01);
		btn_dialkey_2 = (ImageButton) findViewById(R.id.dialkey_02);
		btn_dialkey_3 = (ImageButton) findViewById(R.id.dialkey_03);
		btn_dialkey_4 = (ImageButton) findViewById(R.id.dialkey_04);
		btn_dialkey_5 = (ImageButton) findViewById(R.id.dialkey_05);
		btn_dialkey_6 = (ImageButton) findViewById(R.id.dialkey_06);
		btn_dialkey_7 = (ImageButton) findViewById(R.id.dialkey_07);
		btn_dialkey_8 = (ImageButton) findViewById(R.id.dialkey_08);
		btn_dialkey_9 = (ImageButton) findViewById(R.id.dialkey_09);
		btn_dialkey_jin = (ImageButton) findViewById(R.id.dialkey_btn_jin);
		btn_dialkey_mi = (ImageButton) findViewById(R.id.dialkey_btn_mi);

		tv_back.setOnClickListener(this);
		btn_dialkey_0.setOnClickListener(this);
		btn_dialkey_1.setOnClickListener(this);
		btn_dialkey_2.setOnClickListener(this);
		btn_dialkey_3.setOnClickListener(this);
		btn_dialkey_4.setOnClickListener(this);
		btn_dialkey_5.setOnClickListener(this);
		btn_dialkey_6.setOnClickListener(this);
		btn_dialkey_7.setOnClickListener(this);
		btn_dialkey_8.setOnClickListener(this);
		btn_dialkey_9.setOnClickListener(this);
		btn_dialkey_jin.setOnClickListener(this);
		btn_dialkey_mi.setOnClickListener(this);

	}

	public void addRecodes(String phoneNum, String name, String callDuration,
			String typeName) {

		// 插入通话记录
		try {
			ContentValues values = new ContentValues();
			values.put("phone", phoneNum);
			if (!phoneNum.equals(name)) {
				values.put("name", name);
			}
			values.put("call_type", 2);// 1呼入 2呼出 3未接 0挂断
			values.put("call_type_name", typeName);
			Date date = new Date();
			String call_time = sfd.format(date);
			values.put("call_time", call_time);// 时间
			values.put("duration", callDuration);
			values.put("calltime", Integer.parseInt(sfd2.format(date)));// 用来条件插叙的整型事件
			CallhistoryDBTOOls.addhistory(AisinOutCallActivity.this, values);
			// 发送广播通知通话记录变更
			AisinOutCallActivity.this.sendBroadcast(new Intent(
					Constants.BrandName + ".insert.callhistory"));
		} catch (Exception e) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK)
			return true;

		return super.onKeyDown(keyCode, event);
	}

}
