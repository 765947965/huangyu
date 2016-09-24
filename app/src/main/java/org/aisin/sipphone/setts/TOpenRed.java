package org.aisin.sipphone.setts;

import java.io.File;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.tools.AddRedToold;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TOpenRed extends Activity implements OnClickListener {
	private String from;
	private String name;
	private String sended_gift_id;
	private String command;
	private String from_phone;
	private String fromnickname;
	private String tips;
	private String sub_type;
	private String money_type;

	private LinearLayout removelayout;
	private CircleImageView reddetails_from_iamge;
	private TextView redfroemname;
	private TextView tipstext;
	private ImageView animimge1;
	private ImageView animimge2;
	private TextView reddllclosebt;
	private ImageView qunimage;
	private ImageView lingimage;
	private ImageView money_typeimage;

	private ProgressBar progreebar;
	private TextView progreebar_tishi;

	private MediaPlayer mp;

	// 重力感应管理器
	private SensorManager sensorManager;
	// 震动控件
	private Vibrator vibrator;

	private Bitmap bitmapheaimage;

	private boolean openredaflag = true;// 控制当前是否可以调用摇的动画或者方法

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				startAnim();
			} else if (msg.what == 2) {
				// 拆红包
				progreebar.setVisibility(View.VISIBLE);
				progreebar_tishi.setVisibility(View.VISIBLE);
				progreebar_tishi.setText("正在摇红包...");
				new HttpTask_ckekoutRed().execute("");
			} else if (msg.what == 3) {
				new HttpTask_ckekoutRed_headimage().execute("");
			} else if (msg.what == 4) {
				File file = TOpenRed.this.getFileStreamPath(from
						+ "headimage.jpg");
				bitmapheaimage = BitmapFactory.decodeFile(file
						.getAbsolutePath());
				if (bitmapheaimage != null) {
					reddetails_from_iamge.setImageBitmap(bitmapheaimage);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		from = this.getIntent().getStringExtra("from");
		name = this.getIntent().getStringExtra("name");
		sended_gift_id = this.getIntent().getStringExtra("sended_gift_id");
		command = this.getIntent().getStringExtra("command");
		from_phone = this.getIntent().getStringExtra("from_phone");
		fromnickname = this.getIntent().getStringExtra("fromnickname");
		tips = this.getIntent().getStringExtra("tips");
		sub_type = this.getIntent().getStringExtra("sub_type");
		money_type = this.getIntent().getStringExtra("money_type");
		setContentView(R.layout.topenred);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		reddetails_from_iamge = (CircleImageView) this
				.findViewById(R.id.reddetails_from_iamge);
		reddllclosebt = (TextView) this.findViewById(R.id.reddllclosebt);
		reddllclosebt.setOnClickListener(this);
		redfroemname = (TextView) this.findViewById(R.id.redfroemname);
		tipstext = (TextView) this.findViewById(R.id.tipstext);
		animimge1 = (ImageView) this.findViewById(R.id.animimge1);
		animimge2 = (ImageView) this.findViewById(R.id.animimge2);
		qunimage = (ImageView) this.findViewById(R.id.qunimage);
		lingimage = (ImageView) this.findViewById(R.id.lingimage);
		money_typeimage = (ImageView) this.findViewById(R.id.money_typeimage);
		progreebar = (ProgressBar) this.findViewById(R.id.progreebar);
		progreebar_tishi = (TextView) this.findViewById(R.id.progreebar_tishi);
		animimge1.setOnClickListener(this);
		animimge2.setOnClickListener(this);
		redfroemname.setText("来自" + fromnickname + "的" + name);
		if ("personnocommand".equals(sub_type)) {

		} else if ("personwithcommand".equals(sub_type)) {
			lingimage.setVisibility(View.VISIBLE);
		} else if ("groupnocommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
		} else if ("groupwithcommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
			lingimage.setVisibility(View.VISIBLE);
		}
		if ("0".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_0);
			money_typeimage.setVisibility(View.VISIBLE);
		} else if ("1".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_1);
			money_typeimage.setVisibility(View.VISIBLE);
		}
		tipstext.setText(tips);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		mp = MediaPlayer.create(this, R.raw.shake_sound_male);

		// 设置头像

		File file = this.getFileStreamPath(from + "headimage.jpg");
		if (file.exists()) {
			bitmapheaimage = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmapheaimage != null) {
				reddetails_from_iamge.setImageBitmap(bitmapheaimage);
			}
		} else {
			mHandler.sendEmptyMessage(3);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (sensorManager != null) {// 注册监听器
			sensorManager.registerListener(sensorEventListener,
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_NORMAL);
			// 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
		if (bitmapheaimage != null) {
			bitmapheaimage.recycle();
			bitmapheaimage = null;
		}
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (sensorManager != null) {
			sensorManager.unregisterListener(sensorEventListener);
		}
	}

	public void startAnim() { // 定义摇一摇动画动画
		if (openredaflag) {
			openredaflag = false;
		} else {
			return;
		}
		progreebar.setVisibility(View.GONE);
		progreebar_tishi.setVisibility(View.INVISIBLE);
		mp.start();// 音乐
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
		mup0.setDuration(500);
		TranslateAnimation mup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +1f);
		mup1.setDuration(500);
		// 延迟执行1秒
		mup1.setStartOffset(500);
		animup.addAnimation(mup0);
		animup.addAnimation(mup1);
		// 上图片的动画效果的添加
		animimge1.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, +1f);
		mdn0.setDuration(500);
		TranslateAnimation mdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
		mdn1.setDuration(500);
		// 延迟执行1秒
		mdn1.setStartOffset(500);
		animdn.addAnimation(mdn0);
		animdn.addAnimation(mdn1);
		// 下图片动画效果的添加
		animimge2.startAnimation(animdn);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(2);
			}
		}, 1000);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.animimge1:
			startAnim();
			break;

		case R.id.animimge2:
			startAnim();
			break;
		case R.id.reddllclosebt:
			finish();
			break;
		}
	}

	/**
	 * * 重力感应监听
	 */
	private SensorEventListener sensorEventListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			// 传感器信息改变时执行该方法
			float[] values = event.values;
			float x = values[0]; // x轴方向的重力加速度，向右为正
			float y = values[1]; // y轴方向的重力加速度，向前为正
			float z = values[2]; // z轴方向的重力加速度，向上为正
			// Log.i("环宇", "x轴方向的重力加速度" + x + "；y轴方向的重力加速度" + y + "；z轴方向的重力加速度"
			// + z);
			// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
			int medumValue = 28;
			if ((Math.abs(x) + Math.abs(y) > medumValue)
					|| (Math.abs(y) + Math.abs(z) > medumValue)
					|| (Math.abs(x) + Math.abs(z) > medumValue)) {
				vibrator.vibrate(200);// 震动
				mHandler.sendEmptyMessage(1);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	class HttpTask_ckekoutRed extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = URLTools.GetHttpURL_4RedDaily_CheckOUT_Url(
					TOpenRed.this, sended_gift_id, "shake");
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			progreebar.setVisibility(View.GONE);
			progreebar_tishi.setVisibility(View.INVISIBLE);
			openredaflag = true;
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Intent intentinfo = new Intent(TOpenRed.this,
					org.aisin.sipphone.setts.CkeckOutRedErroreInfo.class);
			switch (doresult) {
			case 0:
				try {
					// 展示红包详情
					String fee_rate = json.optString("fee_rate");

					JSONObject jsz = json.getJSONObject("gifts");
					RedObject redobject = new RedObject();
					redobject.setFrom(jsz.optString("from"));
					redobject.setOpen_time(jsz.optString("open_time"));
					redobject.setGift_id(jsz.optString("gift_id"));
					redobject.setMoney(jsz.optString("money", "0"));
					String has_open_str = jsz.optString("has_open");
					if (has_open_str != null && !"".equals(has_open_str)) {
						redobject.setHas_open(Integer.parseInt(has_open_str
								.trim()));
					} else {
						redobject.setHas_open(1);
					}
					redobject.setDirect(jsz.optString("direct"));
					redobject.setCreate_time(jsz.optString("create_time"));
					redobject.setFrom_phone(jsz.optString("from_phone"));
					redobject.setFromnickname(jsz.optString("fromnickname"));
					redobject.setMoney_type(jsz.optString("money_type"));
					redobject.setTips(jsz.optString("tips"));
					redobject.setExp_time(jsz.optString("exp_time"));
					redobject.setType(jsz.optString("type"));
					redobject.setSub_type(jsz.optString("sub_type"));
					redobject
							.setSender_gift_id(jsz.optString("sender_gift_id"));
					redobject.setName(jsz.optString("name"));
					// 红包数据加入本地
					AddRedToold.addred(TOpenRed.this, redobject, "");

					Intent intent = new Intent(TOpenRed.this,
							org.aisin.sipphone.setts.ShowGetRedInfo.class);
					intent.putExtra("fee_rate", fee_rate);
					intent.putExtra("money", redobject.getMoney());
					intent.putExtra("type", redobject.getType());
					intent.putExtra("sub_type", redobject.getSub_type());
					intent.putExtra("name", redobject.getName());
					intent.putExtra("from", redobject.getFrom());
					intent.putExtra("gift_id", redobject.getGift_id());
					intent.putExtra("sended_gift_id", sended_gift_id);
					intent.putExtra("command", command);
					intent.putExtra("fromnickname", redobject.getFromnickname());
					intent.putExtra("tips", redobject.getTips());
					intent.putExtra("money_type", redobject.getMoney_type());
					TOpenRed.this.startActivity(intent);
					TOpenRed.this.finish();
				} catch (Exception e) {
				}
				return;
			case 6:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(TOpenRed.this, "提示", "sign错误!");
				break;
			case 51:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(TOpenRed.this, "提示", "红包id不存在!");
				break;
			case 52:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(TOpenRed.this, "提示", "红包已经拆过了!");
				break;
			case 53:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(TOpenRed.this, "提示", "红包已过有效期!");
				break;
			case 58:
				progreebar_tishi.setVisibility(View.VISIBLE);
				progreebar_tishi.setText("本次未摇中!再摇一次?");
				return;
			case 45:
				progreebar_tishi.setVisibility(View.VISIBLE);
				progreebar_tishi.setText("没摇中,网络不给力!");
				// new AisinBuildDialog(TOpenRed.this, "提示", "服务器异常!");
				return;
			default:
				progreebar_tishi.setVisibility(View.VISIBLE);
				progreebar_tishi.setText("没摇中,网络不给力!");
				// new AisinBuildDialog(TOpenRed.this, "提示", "联网失败，请检查您的网络信号！");
				return;
			}
			TOpenRed.this.startActivity(intentinfo);
			TOpenRed.this.finish();
		}
	}

	// 拉取发红包者头像
	class HttpTask_ckekoutRed_headimage extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = URLTools.GetHttpURL_4Friend(TOpenRed.this, from);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:
				try {
					JSONArray jsarray = json.getJSONArray("friendslist");
					JSONObject js = jsarray.getJSONObject(0);
					final String picture = js.optString("picture");
					if ("".equals(picture)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (HttpUtils.downloadImage(TOpenRed.this,
										picture, from + "headimage.jpg")) {
									mHandler.sendEmptyMessage(4);
								}
							}
						}).start();
					}
				} catch (Exception e) {
				}
				break;
			}
		}
	}
}
