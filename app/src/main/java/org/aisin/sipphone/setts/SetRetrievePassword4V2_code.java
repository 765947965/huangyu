package org.aisin.sipphone.setts;

import org.aisin.sipphone.LineEditText;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.Misc;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetRetrievePassword4V2_code extends Activity implements
		OnClickListener, OnListnerShearch {
	private LinearLayout rg4v2_cc_linelayout;
	private ImageView regist4v2_code_back;
	private TextView yzcode_phnum;
	private LineEditText rgv2_phnum;
	private TextView cannotsevedcode;
	private TextView timejs;
	private TextView r42_ccode_ts;
	private TextView rg4v2_code_regorlog;
	private ProgressDialog prd;

	private String gpwdpnum;

	private int num = Constants.YcodeTime;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				timejs.setText(num + "秒");
			} else if (msg.what == 2) {
				timejs.setVisibility(View.GONE);
				cannotsevedcode.setEnabled(true);
				cannotsevedcode.setTextColor(Color.parseColor("#1160FD"));
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist4v2_checkcode);

		gpwdpnum = getIntent().getStringExtra("gpwdpnum");
		if (gpwdpnum == null || gpwdpnum.length() != 11) {
			finish();
			return;
		}

		rg4v2_cc_linelayout = (LinearLayout) this
				.findViewById(R.id.rg4v2_cc_linelayout);
		regist4v2_code_back = (ImageView) this
				.findViewById(R.id.regist4v2_code_back);
		yzcode_phnum = (TextView) this.findViewById(R.id.yzcode_phnum);
		rgv2_phnum = (LineEditText) this.findViewById(R.id.rgv2_phnum);
		cannotsevedcode = (TextView) this.findViewById(R.id.cannotsevedcode);
		timejs = (TextView) this.findViewById(R.id.timejs);
		r42_ccode_ts = (TextView) this.findViewById(R.id.r42_ccode_ts);
		rg4v2_code_regorlog = (TextView) this
				.findViewById(R.id.rg4v2_code_regorlog);
		regist4v2_code_back.setOnClickListener(this);
		yzcode_phnum.setText("验证码已发送至 +86" + gpwdpnum);
		rgv2_phnum.setShearchListner(this);
		rgv2_phnum.setFocusable(true);
		cannotsevedcode.setOnClickListener(this);
		rg4v2_code_regorlog.setOnClickListener(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");
		cannotsevedcode.setEnabled(false);
		cannotsevedcode.setTextColor(Color.parseColor("#646464"));
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						mHandler.sendEmptyMessage(1);
						num -= 1;
						if (num < 0) {
							mHandler.sendEmptyMessage(2);
							return;
						}
						Thread.sleep(1000);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(rg4v2_cc_linelayout);// 回收容器
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.regist4v2_code_back:
			AisinBuildDialog mybuild_bc = new AisinBuildDialog(
					SetRetrievePassword4V2_code.this);
			mybuild_bc.setTitle("提示");
			mybuild_bc.setMessage("验证码已下发，确定返回？");
			mybuild_bc.setOnDialogCancelListener("取消", null);
			mybuild_bc.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							SetRetrievePassword4V2_code.this.finish();
						}
					});
			mybuild_bc.dialogShow();
			break;
		case R.id.cannotsevedcode:
			AisinBuildDialog mybuild = new AisinBuildDialog(
					SetRetrievePassword4V2_code.this);
			mybuild.setTitle("提示");
			mybuild.setMessage("是否再次发送验证码?");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							// 下发验证码
							prd.setMessage("获取验证码...");
							prd.show();
							new HttpTask_rpassword_yzCode().execute("agen");
						}
					});
			mybuild.dialogShow();
			break;
		case R.id.rg4v2_code_regorlog:
			// 获取密码
			prd.setMessage("获取密码...");
			prd.show();
			new HttpTask_rpassword().execute("getpwd");
			break;
		}
	}

	@Override
	public void Search(String text) {
		if (!text.isEmpty()) {
			rg4v2_code_regorlog.setEnabled(true);
		} else {
			rg4v2_code_regorlog.setEnabled(false);
		}
	}

	// 获取验证码
	private class HttpTask_rpassword_yzCode extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到获取密码的URL
			String url = URLTools.GetHttpURL_4GetPassword_yzCode_V2(
					SetRetrievePassword4V2_code.this, gpwdpnum.trim());
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
				if (prd != null) {
					prd.dismiss();
				}
				if (authcode != null && !"".equals(authcode)) {
					r42_ccode_ts.setText("请输入该验证码: " + authcode);
				} else {
					r42_ccode_ts.setText("");
				}
				cannotsevedcode.setEnabled(false);
				cannotsevedcode.setTextColor(Color.parseColor("#646464"));
				return;
			case 5:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"服务器异常!");
				break;
			case 35:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			default:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}

	private class HttpTask_rpassword extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到获取密码的URL
			String url = URLTools.GetHttpURL_4GetPassword_V2(
					SetRetrievePassword4V2_code.this, gpwdpnum.trim(),
					rgv2_phnum.getText().toString().trim());
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
				if (prd != null) {
					prd.dismiss();
				}
				// 跳转到修改密码
				Intent intent = new Intent(SetRetrievePassword4V2_code.this,
						org.aisin.sipphone.Regist4V2_ChangePwd.class);
				intent.putExtra("phnum", gpwdpnum);
				intent.putExtra("nn_pwd", Misc.cryptDataByPwd(pwd.trim()));
				intent.putExtra("outflag", "noopenA");
				SetRetrievePassword4V2_code.this.startActivity(intent);
				SetRetrievePassword4V2_code.this.finish();
				return;
			case 5:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"服务器异常!");
				break;
			case 13:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"用户不存在!");
				break;
			case 33:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"验证码过期!");
				break;
			case 32:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"验证码错误!");
				break;
			case 38:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"暂不支持该地区找回密码!");
				break;
			default:
				new AisinBuildDialog(SetRetrievePassword4V2_code.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键退出到注册登录界面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AisinBuildDialog mybuild_bc = new AisinBuildDialog(
					SetRetrievePassword4V2_code.this);
			mybuild_bc.setTitle("提示");
			mybuild_bc.setMessage("验证码已下发，确定返回？");
			mybuild_bc.setOnDialogCancelListener("取消", null);
			mybuild_bc.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							SetRetrievePassword4V2_code.this.finish();
						}
					});
			mybuild_bc.dialogShow();
			return true;
		} else {
			return false;
		}
	}
}
