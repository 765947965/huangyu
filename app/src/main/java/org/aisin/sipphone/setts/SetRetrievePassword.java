package org.aisin.sipphone.setts;

import org.aisin.sipphone.LineEditText;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.Misc;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetRetrievePassword extends Activity implements OnClickListener {
	private LinearLayout setretrievepassword_linlayout;
	private ImageView setts_retrievepassword_account_back;
	private LineEditText setts_rpassword_account_phone;
	private TextView setts_rpassword_account_bt;
	private TextView setts_rpassword_account_bt_yzm;
	private LineEditText getpassword_yzm;
	private int num = 60;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				setts_rpassword_account_bt_yzm.setText("(" + num + ")秒");
			} else if (msg.what == 2) {
				setts_rpassword_account_bt_yzm.setText("获取验证码");
				setts_rpassword_account_bt_yzm.setEnabled(true);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setretrievepassword);
		setretrievepassword_linlayout = (LinearLayout) this
				.findViewById(R.id.setretrievepassword_linlayout);
		setts_retrievepassword_account_back = (ImageView) this
				.findViewById(R.id.setts_retrievepassword_account_back);
		setts_retrievepassword_account_back.setOnClickListener(this);
		setts_rpassword_account_phone = (LineEditText) this
				.findViewById(R.id.setts_rpassword_account_phone);
		getpassword_yzm = (LineEditText) this
				.findViewById(R.id.getpassword_yzm);
		setts_rpassword_account_bt = (TextView) this
				.findViewById(R.id.setts_rpassword_account_bt);
		setts_rpassword_account_bt_yzm = (TextView) this
				.findViewById(R.id.setts_rpassword_account_bt_yzm);
		setts_rpassword_account_bt.setOnClickListener(this);
		setts_rpassword_account_bt_yzm.setOnClickListener(this);
		UserInfo userinfo = UserInfo_db.getUserInfo(SetRetrievePassword.this);
		if (userinfo != null) {
			setts_rpassword_account_phone.setText(userinfo.getPhone());
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setts_retrievepassword_account_back:
			finish();
			break;
		case R.id.setts_rpassword_account_bt_yzm:
			// 校验
			if (!CheckInout()) {
				return;
			}
			// 获取验证码
			setts_rpassword_account_bt_yzm.setEnabled(false);
			new HttpTask_rpassword_yzCode().execute("getyzCode");
			break;
		case R.id.setts_rpassword_account_bt:
			if (!CheckInout()) {
				return;
			}
			String yzcode = getpassword_yzm.getText().toString();
			if (yzcode == null || yzcode.length() < 4) {
				Toast.makeText(this, "验证码不能为空!", Toast.LENGTH_SHORT).show();
				return;
			}
			setts_rpassword_account_bt.setEnabled(false);
			new HttpTask_rpassword().execute("rpassword");
			break;
		}
	}

	private boolean CheckInout() {
		if (!Check_network.isNetworkAvailable(SetRetrievePassword.this)) {
			Toast.makeText(this, "网络不可用,请检查网络连接!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (setts_rpassword_account_phone.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "手机号码不能为空!", Toast.LENGTH_SHORT).show();
			return false;

		}
		boolean rg_phone_falg2 = Check_format
				.Check_mobilePhone(setts_rpassword_account_phone.getText()
						.toString().trim());
		if (!rg_phone_falg2) {
			Toast.makeText(this, "手机号码格式不正确!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private class HttpTask_rpassword extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到获取密码的URL
			String url = URLTools.GetHttpURL_4GetPassword_V2(
					SetRetrievePassword.this, setts_rpassword_account_phone
							.getText().toString().trim(), getpassword_yzm
							.getText().toString().trim());
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			JSONObject json = null;
			int doresult = -104;
			String pwd = null;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = json.optInt("result", -104);
					pwd = json.optString("pwd", "");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (doresult) {
			case 0:

				AisinBuildDialog mybuild = new AisinBuildDialog(
						SetRetrievePassword.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("密码找回成功!\n您的密码是: "
						+ Misc.cryptDataByPwd(pwd));
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								SetRetrievePassword.this.finish();
							}
						});
				mybuild.dialogShow();

				return;
			case 5:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "服务器异常!");
				break;
			case 13:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "用户不存在!");
				break;
			case 33:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "验证码过期!");
				break;
			case 32:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "验证码错误!");
				break;
			case 38:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"暂不支持该地区找回密码!");
				break;
			default:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			setts_rpassword_account_bt.setEnabled(true);
		}
	}

	// 获取验证码
	private class HttpTask_rpassword_yzCode extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到获取密码的URL
			String url = URLTools.GetHttpURL_4GetPassword_yzCode_V2(
					SetRetrievePassword.this, setts_rpassword_account_phone
							.getText().toString().trim());
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -104;
			String authcode = null;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = json.optInt("result", -104);
					authcode = json.optString("authcode", "");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			switch (doresult) {
			case 0:
				if (authcode != null && !"".equals(authcode)) {
					getpassword_yzm.setText(authcode);
					new AisinBuildDialog(SetRetrievePassword.this, "提示",
							"验证码为: " + authcode);
				} else {
					new AisinBuildDialog(SetRetrievePassword.this, "提示",
							"验证码已发送至手机:"
									+ setts_rpassword_account_phone.getText()
											.toString().trim() + "请注意查收!");
				}
				// 开启线程计时
				num = 60;
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (num > 0) {
							try {
								mHandler.sendEmptyMessage(1);
								num = num - 1;
								if (num == 0) {
									mHandler.sendEmptyMessage(2);
								}
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
						}
					}
				}).start();
				return;
			case 5:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetRetrievePassword.this, "提示", "服务器异常!");
				break;
			case 35:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			default:
				new AisinBuildDialog(SetRetrievePassword.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			setts_rpassword_account_bt_yzm.setEnabled(true);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(setretrievepassword_linlayout);// 回收容器
	}

}
