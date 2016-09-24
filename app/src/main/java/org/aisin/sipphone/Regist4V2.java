package org.aisin.sipphone;

import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Regist4V2 extends Activity implements OnListnerShearch,
		OnClickListener {
	private Context mContext;
	private LinearLayout red4v2_linelayout;
	private ImageView regist4v2_back;
	private LineEditText rgv2_phnum;
	private TextView user_agreement;
	private TextView rg4v2_regorlog;
	private ProgressDialog prd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.regist4v2layout);
		red4v2_linelayout = (LinearLayout) this
				.findViewById(R.id.red4v2_linelayout);
		regist4v2_back = (ImageView) this.findViewById(R.id.regist4v2_back);
		rgv2_phnum = (LineEditText) this.findViewById(R.id.rgv2_phnum);
		user_agreement = (TextView) this.findViewById(R.id.user_agreement);
		rg4v2_regorlog = (TextView) this.findViewById(R.id.rg4v2_regorlog);
		regist4v2_back.setOnClickListener(this);
		rgv2_phnum.setShearchListner(this);
		rgv2_phnum.setFocusable(true);
		user_agreement.setOnClickListener(this);
		rg4v2_regorlog.setOnClickListener(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");

	}

	@Override
	public void Search(String text) {
		if (text.trim().length() == 11) {
			rg4v2_regorlog.setEnabled(true);
		} else {
			rg4v2_regorlog.setEnabled(false);
		}

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.regist4v2_back:
			Intent intent = new Intent(mContext,
					org.aisin.sipphone.LodingActivity.class);
			intent.putExtra("start_flag", "NOWAIT");
			// 返回登录注册界面
			startActivity(intent);
			finish();
			break;
		case R.id.user_agreement:
			Intent intent_tx = new Intent(mContext,
					org.aisin.sipphone.setts.ShowTextView.class);
			intent_tx.putExtra("title_name", "用户协议");
			intent_tx.putExtra("show_text", getString(R.string.usertpss));
			// 用户协议
			startActivity(intent_tx);
			break;
		case R.id.rg4v2_regorlog:
			// 注册
			// 验证手机号码的正确性
			if (CheckInput()) {
				AisinBuildDialog mybuild = new AisinBuildDialog(mContext);
				mybuild.setTitle("提示");
				mybuild.setMessage("环宇将验证码发送到+86"
						+ rgv2_phnum.getText().toString());
				mybuild.setOnDialogCancelListener("取消", null);
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								// 发送验证码并跳转
								prd.setMessage("获取验证码...");
								prd.show();
								new HttpTask_getyzcode().execute("reg");

							}
						});
				mybuild.dialogShow();
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键退出到注册登录界面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(mContext,
					org.aisin.sipphone.LodingActivity.class);
			intent.putExtra("start_flag", "NOWAIT");
			// 返回登录注册界面
			startActivity(intent);
			finish();
			return true;
		} else {
			return false;
		}
	}

	private boolean CheckInput() {
		if (!Check_network.isNetworkAvailable(mContext)) {
			Toast.makeText(this, "网络不可用,请检查网络连接!", Toast.LENGTH_SHORT).show();
			return false;
		}
		boolean rg_phone_falg = Check_format.Check_mobilePhone(rgv2_phnum
				.getText().toString().trim());
		if (!rg_phone_falg) {
			Toast.makeText(this, "手机号码格式不正确!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	// 发送注册验证码
	private class HttpTask_getyzcode extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			String reg_url = URLTools.GetHttpURL_4RegistGetCode_V2(
					mContext, rgv2_phnum.getText().toString().trim());
			// Log.i("环宇", reg_url);
			String result = HttpUtils.result_url_get(reg_url,
					"{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
			JSONObject json = null;
			int doresult = -104;
			String authcode = null;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer
							.parseInt(json.optString("result", "-14"));
					authcode = json.optString("authcode", "");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:

				AisinBuildDialog mybuild = new AisinBuildDialog(mContext);
				mybuild.setTitle("提示");
				mybuild.setMessage("验证码已下发至您的手机,请注意查收!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								Intent intent = new Intent(
										mContext,
										org.aisin.sipphone.Regist4V2_CheckCode.class);
								intent.putExtra("reg_phonenum", rgv2_phnum
										.getText().toString().trim());
								mContext.startActivity(intent);
								Regist4V2.this.finish();
							}
						});
				mybuild.dialogShow();
				return;
			case 5:
				new AisinBuildDialog(mContext, "提示", "输入不能为空!");
				break;
			case 6:
				new AisinBuildDialog(mContext, "提示", "sign错误!");
				break;
			case 35:
				new AisinBuildDialog(mContext, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			case 37:
				new AisinBuildDialog(mContext, "提示", "手机号码格式不对");
				break;
			case 38:
				new AisinBuildDialog(mContext, "提示", "暂不支持该地区电话号码注册!");
				break;
			case 45:
				new AisinBuildDialog(mContext, "提示", "服务器异常!");
				break;
			default:
				new AisinBuildDialog(mContext, "提示", "服务器异常!");
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(red4v2_linelayout);// 回收容
	}
}
