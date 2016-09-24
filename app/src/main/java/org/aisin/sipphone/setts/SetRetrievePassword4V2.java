package org.aisin.sipphone.setts;

import org.aisin.sipphone.LineEditText;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetRetrievePassword4V2 extends Activity implements
		OnListnerShearch, OnClickListener {
	private LinearLayout stpasd4v2_linelayout;
	private ImageView setrpd4v2_back;
	private LineEditText setrpd4v2_phnum;
	private ImageView setrpd4v2_clearpnum;
	private TextView setrpd4v2bt;
	private ProgressDialog prd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setrpassword4v2);
		stpasd4v2_linelayout = (LinearLayout) this
				.findViewById(R.id.stpasd4v2_linelayout);
		setrpd4v2_back = (ImageView) this.findViewById(R.id.setrpd4v2_back);
		setrpd4v2_phnum = (LineEditText) this
				.findViewById(R.id.setrpd4v2_phnum);
		setrpd4v2_clearpnum = (ImageView) this
				.findViewById(R.id.setrpd4v2_clearpnum);
		setrpd4v2bt = (TextView) this.findViewById(R.id.setrpd4v2bt);
		setrpd4v2_back.setOnClickListener(this);
		setrpd4v2_phnum.setShearchListner(this);
		setrpd4v2_phnum.setFocusable(true);
		setrpd4v2_clearpnum.setOnClickListener(this);
		setrpd4v2bt.setOnClickListener(this);
		UserInfo usif = UserInfo_db.getUserInfo(this);
		if (usif != null) {
			setrpd4v2_phnum.setText(usif.getPhone());
			setrpd4v2_phnum.setSelection(usif.getPhone().length());
		}
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(stpasd4v2_linelayout);// 回收容器
	}

	@Override
	public void Search(String text) {
		if (text != null && text.length() == 11) {
			setrpd4v2bt.setEnabled(true);
		} else {
			setrpd4v2bt.setEnabled(false);
		}
		if (text != null && text.length() > 0) {
			setrpd4v2_clearpnum.setVisibility(View.VISIBLE);
		} else {
			setrpd4v2_clearpnum.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setrpd4v2_back:
			finish();
			break;
		case R.id.setrpd4v2_clearpnum:
			setrpd4v2_phnum.setText("");
			break;
		case R.id.setrpd4v2bt:
			// 下发验证码
			AisinBuildDialog mybuild = new AisinBuildDialog(
					SetRetrievePassword4V2.this);
			mybuild.setTitle("提示");
			mybuild.setMessage("环宇将验证码发送到+86"
					+ setrpd4v2_phnum.getText().toString());
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							prd.setMessage("获取验证码...");
							prd.show();
							new HttpTask_rpassword_yzCode().execute("code");
						}
					});
			mybuild.dialogShow();
			break;
		}
	}

	// 获取验证码
	private class HttpTask_rpassword_yzCode extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到获取密码的URL
			String url = URLTools.GetHttpURL_4GetPassword_yzCode_V2(
					SetRetrievePassword4V2.this, setrpd4v2_phnum.getText()
							.toString().trim());
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = json.optInt("result", -104);
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
				AisinBuildDialog mybuild = new AisinBuildDialog(
						SetRetrievePassword4V2.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("验证码已发送至您手机上,请注意查收!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								// 开启输入验证码Activity
								Intent intent = new Intent(
										SetRetrievePassword4V2.this,
										org.aisin.sipphone.setts.SetRetrievePassword4V2_code.class);
								intent.putExtra("gpwdpnum", setrpd4v2_phnum
										.getText().toString().trim());
								SetRetrievePassword4V2.this
										.startActivity(intent);
								SetRetrievePassword4V2.this.finish();

							}
						});
				mybuild.dialogShow();
				return;
			case 5:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"服务器异常!");
				break;
			case 35:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"给同一手机号码发送的短信数量超过当日限制");
				break;
			default:
				new AisinBuildDialog(SetRetrievePassword4V2.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}
}
