package org.aisin.sipphone.setts;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MyRedEnvelope extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private ImageView myredenvelope_bar_back;
	private ImageView ppred;
	private ImageView ppred_qun;
	private TextView liebiao;
	private TextView setts_red_news;
	private EditText redcodeinput;
	private TextView redcodeinput_bt;
	private TextView redusertext;
	private PopupWindow popupWindow;
	private ProgressDialog prd;
	private String command;
	private MyRedEnvelopeBroadcast broadcast;
	private SimpleDateFormat sdformat = new SimpleDateFormat("MMdd");
	private TextView ppwindow_redall;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (SharedPreferencesTools.getSharedPreferences_4RED_COUT(
						MyRedEnvelope.this).getBoolean(
						SharedPreferencesTools.REDCOUT_key, false)) {
					setts_red_news.setVisibility(View.VISIBLE);
					// 设置有效红包个数
					setts_red_news.setText(SharedPreferencesTools
							.getSharedPreferences_4RED_COUT(MyRedEnvelope.this)
							.getInt(SharedPreferencesTools.REhasnum_key, 0)
							+ "");
					ppwindow_redall.setTextColor(Color.parseColor("#ed2f15"));
				} else {
					ppwindow_redall.setTextColor(Color.parseColor("#000000"));
					setts_red_news.setVisibility(View.GONE);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myredenvelope);
		command = this.getIntent().getStringExtra("command");
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		myredenvelope_bar_back = (ImageView) this
				.findViewById(R.id.myredenvelope_bar_back);
		ppred = (ImageView) this.findViewById(R.id.ppred);
		ppred_qun = (ImageView) this.findViewById(R.id.ppred_qun);
		liebiao = (TextView) this.findViewById(R.id.liebiao);
		setts_red_news = (TextView) this.findViewById(R.id.setts_red_news);
		redcodeinput = (EditText) this.findViewById(R.id.redcodeinput);
		redcodeinput_bt = (TextView) this.findViewById(R.id.redcodeinput_bt);
		redusertext = (TextView) this.findViewById(R.id.redusertext);
		redusertext.setOnClickListener(this);
		redcodeinput_bt.setOnClickListener(this);
		myredenvelope_bar_back.setOnClickListener(this);
		ppred.setOnClickListener(this);
		ppred_qun.setOnClickListener(this);
		liebiao.setOnClickListener(this);

		// 初始化pupwindow
		View popView = LayoutInflater.from(this).inflate(
				R.layout.red_popupwindow, null);
		popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		ppwindow_redall = (TextView) popView.findViewById(R.id.ppwindow_redall);
		View popred_line = popView.findViewById(R.id.popred_line);
		TextView ppwindow_rednot = (TextView) popView
				.findViewById(R.id.ppwindow_rednot);
		TextView sendoutred_bt = (TextView) popView
				.findViewById(R.id.sendoutred_bt);
		popred_line.setVisibility(View.GONE);
		ppwindow_rednot.setVisibility(View.GONE);
		ppwindow_redall.setOnClickListener(this);
		sendoutred_bt.setOnClickListener(this);

		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在处理，请稍候...");

		broadcast = new MyRedEnvelopeBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".MyRedEnvelope.setts_red_news");
		registerReceiver(broadcast, filter);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if (command != null && !"".equals(command)) {
			try {
				redcodeinput.setText(URLDecoder.decode(command, "utf-8"));
			} catch (Exception e) {
			}
		}
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.myredenvelope_bar_back:
			finish();
			break;
		case R.id.ppred:
			Intent intent = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.PersonalRedEnvelope.class);
			MyRedEnvelope.this.startActivity(intent);
			break;
		case R.id.ppred_qun:
			Intent intent0 = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.GroupRedEnvelopeConfig.class);
			MyRedEnvelope.this.startActivity(intent0);
			break;
		case R.id.liebiao:
			showPop(v);
			break;
		case R.id.ppwindow_redall:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			Intent intent1 = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.RedListActivity.class);
			MyRedEnvelope.this.startActivity(intent1);
			break;
		case R.id.sendoutred_bt:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			Intent intent2 = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.RedListActivity.class);
			intent2.putExtra("direct", "sended");
			MyRedEnvelope.this.startActivity(intent2);
			break;
		case R.id.redcodeinput_bt:
			if ("".equals(redcodeinput.getText().toString())) {
			} else {
				// 查询口令
				prd.show();
				new HttpTask_CkeckRedCode().execute("");
			}
			break;
		case R.id.redusertext:
			Intent intentreduser = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.ShowReduser.class);
			MyRedEnvelope.this.startActivity(intentreduser);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (broadcast != null) {
			unregisterReceiver(broadcast);
		}
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		RecoveryTools.unbindDrawables(removelayout);// 回收容
	}

	class HttpTask_CkeckRedCode extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String datenums = SharedPreferencesTools
					.getSharedPreferences_4REDDaily(MyRedEnvelope.this)
					.getString(SharedPreferencesTools.REDDaily_codenums,
							"000000");
			if (datenums.startsWith(sdformat.format(new Date()))
					&& Integer.parseInt(datenums.substring(4)) > 9) {
				return "{'result':'71'}";
			}
			String str_codetem = null;
			try {
				str_codetem = URLEncoder.encode(redcodeinput.getText()
						.toString(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			String url = URLTools.GetHttpURL_4RedCodeTORedInfo(
					MyRedEnvelope.this, str_codetem);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
			JSONObject json = null;
			int doresult = -104;
			String sended_gift_id = null;
			String from = null;
			String fromnickname = null;
			String tips = null;
			String money_type = null;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
					sended_gift_id = json.optString("sended_gift_id");
					from = json.optString("from");
					fromnickname = json.optString("fromnickname");
					tips = json.optString("tips");
					money_type = json.optString("money_type");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Intent intentinfo = new Intent(MyRedEnvelope.this,
					org.aisin.sipphone.setts.CkeckOutRedErroreInfo.class);
			if (sended_gift_id != null && !"".equals(sended_gift_id)) {
				intentinfo.putExtra("gift_id", sended_gift_id);
				intentinfo.putExtra("from", from);
				intentinfo.putExtra("fromnickname", fromnickname);
				intentinfo.putExtra("tips", tips);
				intentinfo.putExtra("money_type", money_type);
			}
			switch (doresult) {
			case 0:
				String name = json.optString("name");
				String command = json.optString("command");
				String from_phone = json.optString("from_phone");
				String sub_type = json.optString("sub_type");
				Intent intent = new Intent(MyRedEnvelope.this,
						org.aisin.sipphone.setts.TOpenRed.class);
				intent.putExtra("from", from);
				intent.putExtra("name", name);
				intent.putExtra("sended_gift_id", sended_gift_id);
				intent.putExtra("command", command);
				intent.putExtra("from_phone", from_phone);
				intent.putExtra("sub_type", sub_type);
				intent.putExtra("fromnickname", fromnickname);
				intent.putExtra("tips", tips);
				intent.putExtra("money_type", money_type);
				MyRedEnvelope.this.startActivity(intent);
				return;
			case 5:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示", "参数不能为空！");
				break;
			case 6:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示", "sign错误!");
				break;
			case 51:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示",
				// "红包不存在,请确认口令!");
				break;
			case 53:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示", "红包已过有效期!");
				break;
			case 56:
				intentinfo.putExtra("erroreinfo", "红包可能已经抢完啦");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示", "红包类型错误!");
				break;
			case 57:
				String datenums = SharedPreferencesTools
						.getSharedPreferences_4REDDaily(MyRedEnvelope.this)
						.getString(SharedPreferencesTools.REDDaily_codenums,
								"000000");
				if (datenums.startsWith(sdformat.format(new Date()))) {
					int tempnums = Integer.parseInt(datenums.substring(4));
					tempnums = tempnums + 1;
					String tempnums_str = null;
					if (tempnums >= 10) {
						tempnums_str = tempnums + "";
					} else {
						tempnums_str = "0" + tempnums;
					}
					SharedPreferencesTools
							.getSharedPreferences_4REDDaily(MyRedEnvelope.this)
							.edit()
							.putString(
									SharedPreferencesTools.REDDaily_codenums,
									sdformat.format(new Date()) + tempnums_str)
							.commit();
					intentinfo.putExtra("erroreinfo_3", "已输错" + tempnums
							+ "次,还剩" + (10 - tempnums) + "次");
				} else {
					SharedPreferencesTools
							.getSharedPreferences_4REDDaily(MyRedEnvelope.this)
							.edit()
							.putString(
									SharedPreferencesTools.REDDaily_codenums,
									sdformat.format(new Date()) + "01")
							.commit();
					intentinfo.putExtra("erroreinfo_3", "已输错1次,还剩9次");
				}
				intentinfo.putExtra("erroreinfo", "没有对应的红包哦");
				intentinfo.putExtra("erroreinfo_2", "是不是口令输错啦?");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示",
				// "红包口令被占用,口令错误!");
				break;
			case 59:
				intentinfo.putExtra("erroreinfo", "不能再领这个红包了哦");
				intentinfo.putExtra("erroreinfo_2", "你已经领过这个红包了");
				break;
			case 45:
				intentinfo.putExtra("erroreinfo", "您的网络可能不给力哦!");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示", "服务器异常!");
				break;
			case 71:
				intentinfo.putExtra("erroreinfo", "口令输错次数已达上限哦!");
				intentinfo.putExtra("erroreinfo_2", "请明天再来尝试");
				break;
			default:
				intentinfo.putExtra("erroreinfo", "您的网络可能不给力哦!");
				// new AisinBuildDialog(MyRedEnvelope.this, "提示",
				// "联网失败，请检查您的网络信号！");
				break;
			}
			MyRedEnvelope.this.startActivity(intentinfo);
		}
	}

	// 接收更新红包个数的广播
	private class MyRedEnvelopeBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					Constants.BrandName + ".MyRedEnvelope.setts_red_news")) {
				mHandler.sendEmptyMessage(1);
			}
		}
	}

	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent) {
		if (popupWindow == null) {
			return;
		}
		// 设置popwindow显示位置
		popupWindow.showAsDropDown(parent);

		// 获取popwindow焦点 popupWindow.setFocusable(true); //
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

	}
}
