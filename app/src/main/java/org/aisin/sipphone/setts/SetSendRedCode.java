package org.aisin.sipphone.setts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetSendRedCode extends Activity {
	private String sended_gift_id;
	private RedObject robct;
	private LinearLayout removelayout;
	private EditText codeedittext;
	private TextView codeedit_bt;
	private boolean setmycode = false;
	private ProgressDialog prd;
	private String command;
	private boolean editeable = true;// 输入框是否可编辑

	private String codetype;
	private ArrayList<String> phones;

	private ImageView imagebt_wx;
	private ImageView imagebt_wb;
	private ImageView imagebt_qq;
	private ImageView imagebt_pyq;
	private ImageView imagebt_qqkj;
	private UserInfo userinfo;

	private String title_text;
	private String message_sub;

	private String invite_sns_message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sended_gift_id = this.getIntent().getStringExtra("sended_gift_id");
		command = this.getIntent().getStringExtra("command");
		codetype = this.getIntent().getStringExtra("codetype");
		phones = this.getIntent().getStringArrayListExtra("phones");
		if (sended_gift_id == null || "".equals(sended_gift_id)) {
			finish();
			return;
		}
		robct = RedData_DBHelp.GetRedData(this, sended_gift_id);
		if (robct == null) {
			finish();
			return;
		}
		setContentView(R.layout.setsendredcode);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		codeedittext = (EditText) this.findViewById(R.id.codeedittext);
		codeedit_bt = (TextView) this.findViewById(R.id.codeedit_bt);
		imagebt_wx = (ImageView) this.findViewById(R.id.imagebt_wx);
		imagebt_wb = (ImageView) this.findViewById(R.id.imagebt_wb);
		imagebt_qq = (ImageView) this.findViewById(R.id.imagebt_qq);
		imagebt_pyq = (ImageView) this.findViewById(R.id.imagebt_pyq);
		imagebt_qqkj = (ImageView) this.findViewById(R.id.imagebt_qqkj);
		if ("personal".equals(codetype)) {
			imagebt_wx.setVisibility(View.GONE);
			imagebt_wb.setVisibility(View.GONE);
			imagebt_qq.setVisibility(View.GONE);
			imagebt_pyq.setVisibility(View.GONE);
			imagebt_qqkj.setVisibility(View.GONE);
		}

		// 设置Edittext不可编辑状态
		codeedittext.setEnabled(true);
		codeedittext.setFocusable(false);
		codeedittext.setFocusableInTouchMode(false);

		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在处理，请稍候...");
		if (command != null && !"".equals(command)) {
			codeedittext.setText(command);

			// 设置金额及使用详情
			message_sub = "";
			if ("logindaily".equals(robct.getType().trim())
					|| robct.getType().trim().endsWith("_money")) {
				// 环宇每日登录红包或者金钱红包
				double money_temp = Double.parseDouble(robct.getMoney())
						/ (double) 100;
				message_sub = money_temp + "元话费";
				title_text = "环宇-话费";
			} else if (robct.getType().trim().endsWith("_month")) {
				// 赠送的天红包
				message_sub = robct.getMoney() + "天免费通话";
				title_text = "环宇-话费";
			} else if (robct.getType().trim().endsWith("_4gdata")) {
				double money_temp = Double.parseDouble(robct.getMoney());
				if (money_temp > 1024) {
					message_sub = money_temp / (double) 1024 + "MB流量";
				} else {
					message_sub = money_temp + "KB流量";
				}
				title_text = "环宇-流量";
			} else if (robct.getType().trim().endsWith("_right")) {
				double money_temp = (double) Integer.parseInt(robct.getMoney())
						/ (double) 100;
				message_sub = money_temp + "元特权";
				title_text = "环宇-特权";
			}
			invite_sns_message = "\"" + command + "\"红包口令,"
					+ robct.getFromnickname() + "发了 " + message_sub
					+ "红包,打开 \"环宇-我-红包\",输入口令,快来抢!";
		} else {
			// 开启生成口令
			new HttpTask_SetsendRedCode().execute("");
		}
		userinfo = UserInfo_db.getUserInfo(this);
	}

	public void Onchangeclick(View v) {
		int id = v.getId();

		Intent intent = new Intent(SetSendRedCode.this,
				org.aisin.sipphone.setts.SharedActivity.class);
		intent.putExtra("invite_sns_message", invite_sns_message);
		intent.putExtra("invite_app", "环宇-红包");
		intent.putExtra("bitmapid", R.drawable.redsharedimage);
		intent.putExtra("iamgeurl", HttpUtils.redsharedImageurl);
		intent.putExtra(
				"invite_url",
				SharedPreferencesTools
						.getSharedPreferences_msglist_date_share(
								SetSendRedCode.this)
						.getString(
								SharedPreferencesTools.SPF_msglist_date_giftshare_url,
								"")
						.replace("gift_command=%s", "gift_command=" + command)
						.replace("uid=%s", "uid=" + userinfo.getUid()));
		switch (id) {
		case R.id.setredcode_banck:

			finish();
			break;
		case R.id.codeedit_bt:
			if (codeedittext.getText().toString().length() < 2) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "口令不能少于两个字符!");
			} else {
				// 开启生成口令
				prd.show();
				new HttpTask_SetsendRedCode().execute("");
			}
			break;
		case R.id.removelayout:
			if (editeable) {
				editeable = false;
				setmycode = true;
				codeedittext.setFocusableInTouchMode(true);
				codeedittext.setFocusable(true);
				codeedittext.requestFocus();
				codeedit_bt.setVisibility(View.VISIBLE);
				codeedittext.setSelection(codeedittext.getText().toString()
						.length());
			} else {
				editeable = true;
				codeedittext.setText(command);
				codeedittext.setFocusable(false);
				codeedittext.setFocusableInTouchMode(false);
				codeedit_bt.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.codeedittext:
			editeable = false;
			setmycode = true;
			codeedittext.setFocusableInTouchMode(true);
			codeedittext.setFocusable(true);
			codeedittext.requestFocus();
			codeedit_bt.setVisibility(View.VISIBLE);
			break;
		case R.id.imagebt_wx:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			intent.putExtra("shareflag", "weixin");
			SetSendRedCode.this.startActivity(intent);
			break;
		case R.id.imagebt_wb:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			intent.putExtra("shareflag", "weibo");
			SetSendRedCode.this.startActivity(intent);
			break;
		case R.id.imagebt_qq:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			intent.putExtra("shareflag", "QQ");
			SetSendRedCode.this.startActivity(intent);
			break;
		case R.id.imagebt_pyq:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			intent.putExtra("shareflag", "penyouquan");
			intent.putExtra("invite_app", title_text);
			intent.putExtra("invite_sns_message", "\"" + command + "\"环宇码,"
					+ robct.getFromnickname() + "发了 " + message_sub
					+ ",打开 \"环宇-我\",输入环宇码,快来抢!");
			SetSendRedCode.this.startActivity(intent);
			break;
		case R.id.imagebt_qqkj:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			intent.putExtra("shareflag", "Qzone");
			SetSendRedCode.this.startActivity(intent);
			break;
		case R.id.imagebt_dx:
			if (command == null || "".equals(command)) {
				new AisinBuildDialog(SetSendRedCode.this, "提示", "未设置口令!");
				break;
			}
			StringBuilder strb = new StringBuilder();
			if (phones != null) {
				for (String strp : phones) {
					strb.append(strp + ";");
				}
			}
			Uri smsToUri = Uri.parse("smsto:" + strb.toString());// 联系人地址
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra(
					"sms_body",
					invite_sns_message
							+ SharedPreferencesTools
									.getSharedPreferences_msglist_date_share(
											SetSendRedCode.this)
									.getString(
											SharedPreferencesTools.SPF_msglist_date_giftshare_url,
											"").replace("%s", command));
			this.startActivity(mIntent);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}

	class HttpTask_SetsendRedCode extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// 获取设置红包口令的URL
			String url = null;
			if (!setmycode) {
				url = URLTools.GetHttpURL_4SetSendRedCodeURL(
						SetSendRedCode.this, sended_gift_id, "get", null);
			} else {
				String str_codetem = null;
				try {
					str_codetem = URLEncoder.encode(codeedittext.getText()
							.toString(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					return null;
				}
				url = URLTools
						.GetHttpURL_4SetSendRedCodeURL(SetSendRedCode.this,
								sended_gift_id, "set", str_codetem);
			}
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
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
				String command_js = json.optString("command");
				command = command_js;
				codeedittext.setText(command_js);
				codeedittext.setFocusable(false);
				codeedittext.setFocusableInTouchMode(false);
				codeedit_bt.setVisibility(View.INVISIBLE);
				// 更改本地存储的数据
				ContentValues cv = new ContentValues();
				cv.put("status", "has_sended");
				cv.put("command", codeedittext.getText().toString());
				RedData_DBHelp.upRedDate(SetSendRedCode.this, sended_gift_id,
						cv);
				SetSendRedCode.this.sendBroadcast(new Intent(// 如果红包列表存在 通知更新
						Constants.BrandName + ".redlisttoup"));
				// 设置金额及使用详情
				message_sub = "";
				if ("logindaily".equals(robct.getType().trim())
						|| robct.getType().trim().endsWith("_money")) {
					// 环宇每日登录红包或者金钱红包
					double money_temp = Double.parseDouble(robct.getMoney())
							/ (double) 100;
					message_sub = money_temp + "元";
				} else if (robct.getType().trim().endsWith("_month")) {
					// 赠送的天红包
					message_sub = robct.getMoney() + "天";
				} else if (robct.getType().trim().endsWith("_4gdata")) {
					double money_temp = Double.parseDouble(robct.getMoney());
					if (money_temp > 1024) {
						message_sub = money_temp / (double) 1024 + "MB";
					} else {
						message_sub = money_temp + "KB";
					}
				} else if (robct.getType().trim().endsWith("_right")) {
					double money_temp = (double) Integer.parseInt(robct
							.getMoney()) / (double) 100;
					message_sub = money_temp + "天";
				}
				invite_sns_message = "\"" + command + "\"红包口令,"
						+ robct.getFromnickname() + "发了 " + message_sub
						+ "红包,打开 \"环宇-我-红包\",输入口令,快来抢!";
				if (setmycode) {
					setmycode = false;
					new AisinBuildDialog(SetSendRedCode.this, "提示", "口令更改成功!");
				}
				break;
			case 5:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "sign错误!");
				break;
			case 10:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "参数错误!");
				break;
			case 51:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "红包不存在!");
				break;
			case 56:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "红包类型错误!");
				break;
			case 57:
				new AisinBuildDialog(SetSendRedCode.this, "提示",
						"红包口令已被占用,请修改口令!");
				break;
			case 45:
				new AisinBuildDialog(SetSendRedCode.this, "提示", "服务器异常!");
				break;
			default:
				new AisinBuildDialog(SetSendRedCode.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
		}
	}

}
