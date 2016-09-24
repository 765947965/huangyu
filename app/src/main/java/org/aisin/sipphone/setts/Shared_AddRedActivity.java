package org.aisin.sipphone.setts;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.AlwaysMarqueeTextView;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.AddRedToold;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Shared_AddRedActivity extends Activity implements OnClickListener {

	private ImageView img_back, img_redclose, img_sendfromname;
	private TextView tv_sendfromname, tv_red_error, tv_sendfromname_messages;
	private AlwaysMarqueeTextView tv_red_tips;
	private ImageView img_red_anim;
	private AlertDialog dialog;
	private View view;
	private Context mContext;

	private boolean credflag = false;
	private String aoutup;
	private String from;
	private String fromnickname;
	private String gift_id;
	private String name;
	private String tips;
	private String gift_type;
	private String anim;
	private byte[] bitmapByte;
	private String aboveNum;
	private AnimationDrawable animationDrawable;
	private RedObject redObject;
	private SimpleDateFormat sdformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Animation anim = AnimationUtils.loadAnimation(mContext,
						R.anim.anim_1);
				view.startAnimation(anim);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(3);
					}
				}, 610);
			} else if (msg.what == 2) {
				Animation anim = AnimationUtils.loadAnimation(mContext,
						R.anim.anim_2);
				view.startAnimation(anim);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(3);
					}
				}, 160);
			} else if (msg.what == 3) {
				if (dialog != null) {
					try {
						dialog.cancel();
					} catch (Exception e) {
					}
				}
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shared__add_red);
		mContext = this;

		aboveNum = getIntent().getStringExtra("aboveNum");
		img_back = (ImageView) findViewById(R.id.setaccount_bar_back);
		img_back.setOnClickListener(this);
		if (aboveNum.equals("no")) {
			initData();
			initView();
		} else {
			new AisinBuildDialog(mContext, "红包上限", "此类红包获取已达上限");
		}

	}

	private void initData() {

		redObject = (RedObject) getIntent().getSerializableExtra("redObject");
		from = redObject.getFrom();
		fromnickname = redObject.getFromnickname();
		gift_id = redObject.getGift_id();
		name = redObject.getName();
		tips = redObject.getTips();
		gift_type = redObject.getType();

		bitmapByte = this.getIntent().getByteArrayExtra("bitmapByte");
		if (gift_id == null || "".equals(gift_id)) {
			finish();
			return;
		}

	}

	private void initView() {

		view = (View) LayoutInflater.from(mContext).inflate(
				R.layout.shared_addred, null);
		img_redclose = (ImageView) view.findViewById(R.id.red_closedbt);
		img_sendfromname = (ImageView) view
				.findViewById(R.id.sendfromname_image);
		img_red_anim = (ImageView) view.findViewById(R.id.red_anim_image);
		tv_sendfromname = (TextView) view.findViewById(R.id.sendfromname_text);
		tv_red_error = (TextView) view.findViewById(R.id.red_errortext);
		tv_sendfromname_messages = (TextView) view
				.findViewById(R.id.sendfromname_messages_text);
		tv_red_tips = (AlwaysMarqueeTextView) view
				.findViewById(R.id.red_anim_text);
		img_redclose.setOnClickListener(this);

		img_red_anim.setOnClickListener(this);
		if (bitmapByte != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0,
					bitmapByte.length);
			img_sendfromname.setImageBitmap(bitmap);
		} else {
			img_sendfromname.setImageResource(R.drawable.headimage);
		}
		if (from != null && !"".equals(from.trim())) {
			if ("system".equals(from.trim())) {
				tv_sendfromname.setText("环宇");
			}
		}
		if (!name.isEmpty()) {
			tv_sendfromname_messages.setText("给您发来" + name);
		}
		tv_red_tips.setText(tips);
		img_red_anim.setImageResource(R.drawable.rpopen);
		showDialog(view);

	}

	private void showDialog(View view) {

		AlertDialog.Builder build = new AlertDialog.Builder(mContext);
		dialog = build.create();
		dialog.setCancelable(false);
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(view);
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);

		int screenWidth = dm.widthPixels;
		int screenHeight = dm.heightPixels;

		params.gravity = Gravity.CENTER;
		params.width = screenWidth * 4 / 5;
		params.height = screenHeight * 3 / 5;
		dialog.getWindow().setAttributes(params);

	}

	private class HttpTask_Red_checkout extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
			}
			// 获取拆红包的URl
			String url = URLTools.GetHttpURL_4RedDaily_CheckOUT_Url(mContext,
					gift_id, "open");
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -14;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer
							.parseInt(json.optString("result", "-14"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			switch (doresult) {
			case 0:
				String award_money = json.optString("award_money", "");
				String fee_rate = json.optString("fee_rate", "");
				// 启动拆红包成功展示Activity
				redObject.setHas_open(1);
				redObject.setMoney(award_money);
				redObject.setOpen_time(sdformat.format(new Date()));
				AddRedToold.addred(mContext, redObject, "");
				Intent intent = new Intent(mContext,
						org.aisin.sipphone.RedDetailsActivity.class);
				intent.putExtra("fromnickname", fromnickname);
				intent.putExtra("from", from);
				intent.putExtra("name", name);
				intent.putExtra("tips", tips);
				intent.putExtra("gift_type", gift_type);
				intent.putExtra("award_money", award_money);
				intent.putExtra("fee_rate", fee_rate);
				if (bitmapByte != null) {
					intent.putExtra("bitmapByte", bitmapByte);
				}

				mContext.startActivity(intent);
				credflag = true;
				finish();
				return;
			case 6:
				tv_red_error.setText("sign错误");
				break;
			case 51:
				tv_red_error.setText("红包id不存在，无法拆红包!");
				break;
			case 52:
				tv_red_error.setText("该红包已经拆过了!");
				break;
			case 53:
				tv_red_error.setText("红包已过有效期!");
				break;
			case 45:
				tv_red_error.setText("服务器异常!");
				break;
			default:
				tv_red_error.setText("联网失败,请检查网络连接!");
				break;
			}
			img_red_anim.setImageResource(R.drawable.rpopen);
			img_red_anim.setEnabled(true);
			img_redclose.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setaccount_bar_back:
			this.finish();
			break;
		case R.id.red_closedbt:

			mHandler.sendEmptyMessage(1);
			break;
		case R.id.red_anim_image:
			img_red_anim.setEnabled(false);
			img_redclose.setEnabled(false);
			// 开启动画
			img_red_anim.setImageResource(R.anim.redcheckoutanim);
			animationDrawable = (AnimationDrawable) img_red_anim.getDrawable();
			animationDrawable.start();
			// 拆红包
			new HttpTask_Red_checkout().execute("checkout");

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		if (redObject != null && redObject.getHas_open() != 1)
			AddRedToold.addred(mContext, redObject, "");

		if (aboveNum.equals("no")) {
			mHandler.sendEmptyMessage(1);

		}
		super.onDestroy();
	}

}
