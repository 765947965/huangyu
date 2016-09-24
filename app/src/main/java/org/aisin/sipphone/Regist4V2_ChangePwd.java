package org.aisin.sipphone;

import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
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
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Regist4V2_ChangePwd extends Activity implements OnListnerShearch,
		OnClickListener {
	private LinearLayout rg4v2cpd_linelayout;
	private TextView rg4v2_pwdcodeshow;
	private LineEditText rg4v2_npwdcodeinput;
	private TextView rg4v2_surecpwdbt;
	private TextView rg4v2_not_surecpwdbt;
	private ProgressDialog prd;

	private String phnum;// 要修改密码的手机号码
	private String nn_pwd;// 要修改密码的的原始密码

	private String outflag;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == -1) {
				Regist4V2_ChangePwd.this.finish();
			} else if (msg.what == -2) {
				// 启动开屏页
				Intent itent = new Intent(Regist4V2_ChangePwd.this,
						org.aisin.sipphone.StartPager.class);
				Regist4V2_ChangePwd.this.startActivity(itent);
				Regist4V2_ChangePwd.this.overridePendingTransition(
						R.anim.aisinactivityinput, R.anim.startpageroutput);
				Regist4V2_ChangePwd.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.regist4v2_changepwd);

		phnum = getIntent().getStringExtra("phnum");
		nn_pwd = getIntent().getStringExtra("nn_pwd");
		outflag = getIntent().getStringExtra("outflag");
		if (phnum == null || "".equals(phnum) || nn_pwd == null
				|| "".equals(nn_pwd)) {
			if (outflag != null && "noopenA".equals(outflag)) {
				finish();
				return;
			}
			// 跳转到注册界面
			Intent intent = new Intent(this, org.aisin.sipphone.Regist4V2.class);
			this.startActivity(intent);
			this.finish();
			return;
		}

		rg4v2cpd_linelayout = (LinearLayout) this
				.findViewById(R.id.rg4v2cpd_linelayout);
		rg4v2_pwdcodeshow = (TextView) this
				.findViewById(R.id.rg4v2_pwdcodeshow);
		rg4v2_npwdcodeinput = (LineEditText) this
				.findViewById(R.id.rg4v2_npwdcodeinput);
		rg4v2_surecpwdbt = (TextView) this.findViewById(R.id.rg4v2_surecpwdbt);
		rg4v2_not_surecpwdbt = (TextView) this
				.findViewById(R.id.rg4v2_not_surecpwdbt);
		rg4v2_pwdcodeshow.setText("您的密码为: " + nn_pwd);
		rg4v2_npwdcodeinput.setShearchListner(this);
		rg4v2_npwdcodeinput.setFocusable(true);
		rg4v2_surecpwdbt.setOnClickListener(this);
		rg4v2_not_surecpwdbt.setOnClickListener(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("修改密码...");
	}

	@Override
	public void Search(String text) {
		if (text.trim().length() > 0) {
			rg4v2_surecpwdbt.setEnabled(true);
		} else {
			rg4v2_surecpwdbt.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.rg4v2_surecpwdbt:
			// 修改密码
			if (!Check_network.isNetworkAvailable(this)) {
				Toast.makeText(this, "网络不可用,请检查网络连接!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			boolean rg_passowrd_falg = Check_format
					.check_password4regset(rg4v2_npwdcodeinput.getText()
							.toString().trim());
			if (!rg_passowrd_falg) {
				new AisinBuildDialog(this, "提示", "密码只能由数字或字母组成!");
				return;
			}
			// 修改密码请求
			prd.show();
			new HttpTask_changepassword().execute("cpwd");
			break;

		case R.id.rg4v2_not_surecpwdbt:
			if (outflag != null && "noopenA".equals(outflag)) {
				finish();
				return;
			}
			// 不修改密码,跳到主Activity
			if (CheckUpadateTime.CheckResult_4bootpager(this)) {
				Intent intent = new Intent(this,
						org.aisin.sipphone.BootPage.class);
				startActivity(intent);
				overridePendingTransition(R.anim.aisinactivityinput,
						R.anim.startpageroutput);
				finish();
			} else {
				// 检测是否展示动态开屏还是直接启动主界面
				new HttpTask_start_page(Regist4V2_ChangePwd.this, mHandler)
						.execute("");
			}
			break;
		}
	}

	private class HttpTask_changepassword extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到更改密码的URL
			String url = URLTools.GetHttpURL_4ChangePassword_V2(
					Regist4V2_ChangePwd.this, nn_pwd, rg4v2_npwdcodeinput
							.getText().toString().trim(), phnum);
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			// TODO Auto-generated method stub
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
				// 保存用户数据
				if (prd != null) {
					prd.dismiss();
				}
				// 如果是本帐号修改的密码，则保存信息
				UserInfo userinfo = UserInfo_db
						.getUserInfo(Regist4V2_ChangePwd.this);
				if (userinfo != null && userinfo.getPhone().equals(phnum)) {
					UserInfo_db.SaveUserInfo(Regist4V2_ChangePwd.this, userinfo
							.getBding_phone(), userinfo.getPhone().trim(),
							rg4v2_npwdcodeinput.getText().toString().trim(),
							userinfo.getUid().trim());
				}

				AisinBuildDialog mybuild = new AisinBuildDialog(
						Regist4V2_ChangePwd.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("密码修改成功!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								if (outflag != null
										&& "noopenA".equals(outflag)) {
									Regist4V2_ChangePwd.this.finish();
									return;
								}

								if (CheckUpadateTime
										.CheckResult_4bootpager(Regist4V2_ChangePwd.this)) {
									Intent intent = new Intent(
											Regist4V2_ChangePwd.this,
											org.aisin.sipphone.BootPage.class);
									Regist4V2_ChangePwd.this
											.startActivity(intent);
									Regist4V2_ChangePwd.this
											.overridePendingTransition(
													R.anim.aisinactivityinput,
													R.anim.startpageroutput);

									Regist4V2_ChangePwd.this.finish();
								} else {
									// 检测是否展示动态开屏还是直接启动主界面
									new HttpTask_start_page(
											Regist4V2_ChangePwd.this, mHandler)
											.execute("");
								}
							}
						});
				mybuild.dialogShow();
				return;
			case 5:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示", "服务器异常!");
				break;
			case 13:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示", "用户不存在!");
				break;
			case 12:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示", "原密码错误!");
				break;
			default:
				new AisinBuildDialog(Regist4V2_ChangePwd.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(rg4v2cpd_linelayout);// 回收容
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键退出到注册登录界面
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			AisinBuildDialog mybuild = new AisinBuildDialog(
					Regist4V2_ChangePwd.this);
			mybuild.setTitle("提示");
			mybuild.setMessage("确定不修改密码？");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							if (outflag != null && "noopenA".equals(outflag)) {
								Regist4V2_ChangePwd.this.finish();
								return;
							}
							if (CheckUpadateTime
									.CheckResult_4bootpager(Regist4V2_ChangePwd.this)) {
								Intent intent = new Intent(
										Regist4V2_ChangePwd.this,
										org.aisin.sipphone.BootPage.class);
								Regist4V2_ChangePwd.this.startActivity(intent);
								Regist4V2_ChangePwd.this
										.overridePendingTransition(
												R.anim.aisinactivityinput,
												R.anim.startpageroutput);
								Regist4V2_ChangePwd.this.finish();
							} else {
								// 检测是否展示动态开屏还是直接启动主界面
								new HttpTask_start_page(
										Regist4V2_ChangePwd.this, mHandler)
										.execute("");
							}
						}
					});
			mybuild.dialogShow();
			return true;
		} else {
			return false;
		}
	}
}
