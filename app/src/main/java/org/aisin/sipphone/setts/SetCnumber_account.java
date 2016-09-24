package org.aisin.sipphone.setts;

import org.aisin.sipphone.LineEditText;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.OraLodingUser;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.OraLodingUserTools;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.aisin.sipphone.tools.VerificationCode;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetCnumber_account extends Activity implements OnClickListener,
		OnListnerShearch {
	private LinearLayout cnumber_account_linlayout;
	private ImageView setts_cnumber_account_back; // 返回键
	private TextView cnumber_account_oldphone_num; // 原手机号码
	private LineEditText cnumber_account_newphone_num; // 新手机号码
	private LineEditText cnumber_account_newphone_num_agen; // 确认新手机号码
	private LineEditText old_pp_account_c_passowrd; // 原设密码
	private TextView cnumber_account_changebut; // 确定按钮
	private String code = VerificationCode.getCode();
	private EditText codeinput;
	private ProgressDialog prd;
	private View view;
	private AlertDialog progressBuilder;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				progressBuilder.dismiss();
				String codeinput_t = codeinput.getText().toString();
				if (codeinput_t != null && codeinput_t.equals(code)) {
					// 发送改绑请求
					prd.setMessage("更改绑定中...");
					prd.show();
					new HttpTask_cacc().execute("cacc");
				} else {
					progressBuilder.setTitle("验证码错误,请重新输入!");
					progressBuilder.show();
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cnumber_account);
		cnumber_account_linlayout = (LinearLayout) this
				.findViewById(R.id.cnumber_account_linlayout);
		setts_cnumber_account_back = (ImageView) this
				.findViewById(R.id.setts_cnumber_account_back);
		cnumber_account_oldphone_num = (TextView) this
				.findViewById(R.id.cnumber_account_oldphone_num);
		cnumber_account_newphone_num = (LineEditText) this
				.findViewById(R.id.cnumber_account_newphone_num);
		cnumber_account_newphone_num_agen = (LineEditText) this
				.findViewById(R.id.cnumber_account_newphone_num_agen);
		old_pp_account_c_passowrd = (LineEditText) this
				.findViewById(R.id.old_pp_account_c_passowrd);
		cnumber_account_changebut = (TextView) this
				.findViewById(R.id.cnumber_account_changebut);
		setts_cnumber_account_back.setOnClickListener(this);
		cnumber_account_changebut.setOnClickListener(this);
		cnumber_account_newphone_num.setShearchListner(this);
		cnumber_account_newphone_num_agen.setShearchListner(this);
		old_pp_account_c_passowrd.setShearchListner(this);
		cnumber_account_oldphone_num.setText(UserInfo_db.getUserInfo(
				SetCnumber_account.this).getPhone());
		cnumber_account_newphone_num.setFocusable(true);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");
		view = LayoutInflater.from(this).inflate(R.layout.cnbacodetext, null);
		codeinput = (EditText) view.findViewById(R.id.cnbacdtt);

		progressBuilder = new AlertDialog.Builder(this).create();
		progressBuilder.setView(view);
		progressBuilder.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mHandler.sendEmptyMessage(1);
					}
				});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setts_cnumber_account_back:
			finish();
			break;

		case R.id.cnumber_account_changebut:
			if (!Checkinput()) {
				return;
			}
			// 发送验证码请求
			prd.setMessage("获取验证码...");
			prd.show();
			new HttpTask_cnumber_acc_yzm().execute("yzm");
			break;
		}
	}

	private boolean Checkinput() {

		if (!Check_network.isNetworkAvailable(SetCnumber_account.this)) {
			new AisinBuildDialog(SetCnumber_account.this, "提示",
					"网络不可用,请检查网络连接!");
			return false;
		}
		if (cnumber_account_newphone_num.getText().toString().trim().length() == 0) {
			new AisinBuildDialog(SetCnumber_account.this, "提示", "新手机号码不能为空!");
			return false;
		}
		if (cnumber_account_newphone_num_agen.getText().toString().trim()
				.length() == 0) {
			new AisinBuildDialog(SetCnumber_account.this, "提示", "重复新手机号码不能为空!");
			return false;
		}
		if (old_pp_account_c_passowrd.getText().toString().trim().length() == 0) {
			new AisinBuildDialog(SetCnumber_account.this, "提示", "原设密码不能为空!");
			return false;
		}
		if (!cnumber_account_newphone_num_agen
				.getText()
				.toString()
				.trim()
				.equals(cnumber_account_newphone_num.getText().toString()
						.trim())) {
			new AisinBuildDialog(SetCnumber_account.this, "提示", "两次输入新手机号码不一致!");
			return false;
		}
		if (cnumber_account_oldphone_num
				.getText()
				.toString()
				.trim()
				.equals(cnumber_account_newphone_num.getText().toString()
						.trim())) {
			new AisinBuildDialog(SetCnumber_account.this, "提示",
					"新手机号不能与当前手机号相同!");
			return false;

		}
		boolean rg_phone_falg = Check_format
				.Check_mobilePhone(cnumber_account_newphone_num.getText()
						.toString().trim());
		if (!rg_phone_falg) {
			new AisinBuildDialog(SetCnumber_account.this, "提示", "手机号码格式不正确!");
			return false;
		}
		boolean password_falg = Check_format
				.check_password4regset(old_pp_account_c_passowrd.getText()
						.toString().trim());
		if (!password_falg) {
			new AisinBuildDialog(SetCnumber_account.this, "提示",
					"密码只能由数字或者字母组成!");
			return false;
		}
		return true;
	}

	private class HttpTask_cnumber_acc_yzm extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			String url = URLTools
					.GetHttpURL_4YZM(SetCnumber_account.this, code);
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
			try {

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			switch (doresult) {
			case 0:
				// 校验验证码
				if (prd != null) {
					prd.dismiss();
				}
				progressBuilder.setTitle("请输入发送到手机上的验证码");
				progressBuilder.show();
				return;
			case 35:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			case 37:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 38:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"暂不支持该地区电话号码注册!");
				break;
			case 45:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "服务器异常!");
				break;
			default:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"获取验证码失败,请稍候再试!");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}

	private class HttpTask_cacc extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到请求改绑的URL
			String url = URLTools.GetHttpURL_4CAccount_V2(
					SetCnumber_account.this, old_pp_account_c_passowrd
							.getText().toString().trim(),
					cnumber_account_newphone_num.getText().toString().trim());
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
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
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
				if (prd != null) {
					prd.dismiss();
				}
				String uid = json.optString("uid", "0");
				// 保存用户数据
				UserInfo_db.SaveUserInfo(SetCnumber_account.this,
						cnumber_account_newphone_num.getText().toString()
								.trim(), cnumber_account_newphone_num.getText()
								.toString().trim(), old_pp_account_c_passowrd
								.getText().toString().trim(), uid.trim());
				// 记录登录过的用户
				OraLodingUserTools.addolus(
						SetCnumber_account.this,
						new OraLodingUser(cnumber_account_newphone_num
								.getText().toString().trim(), System
								.currentTimeMillis()));
				// 重置需要更新的用户信息
				CheckUpadateTime.ReSetValue(SetCnumber_account.this);

				AisinBuildDialog mybuild = new AisinBuildDialog(
						SetCnumber_account.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("恭喜您,改绑成功!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								SetCnumber_account.this
										.sendBroadcast(new Intent(
												Constants.BrandName
														+ ".find.upServerdata"));
								SetCnumber_account.this.finish();
							}
						});
				mybuild.dialogShow();
				return;
			case 5:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "服务器异常!");
				break;
			case 35:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			case 13:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "帐号不存在!");
				break;
			case 12:
				new AisinBuildDialog(SetCnumber_account.this, "提示", "密码错误!");
				break;
			case 31:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
						"该帐号已被其他用户绑定!");
				break;
			default:
				new AisinBuildDialog(SetCnumber_account.this, "提示",
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
		RecoveryTools.unbindDrawables(cnumber_account_linlayout);// 回收容器
	}

	@Override
	public void Search(String text) {
		if (cnumber_account_newphone_num.getText().toString().length() == 11
				&& cnumber_account_newphone_num_agen.getText().toString()
						.length() == 11
				&& old_pp_account_c_passowrd.getText().toString().length() > 0) {
			cnumber_account_changebut.setEnabled(true);
		} else {
			cnumber_account_changebut.setEnabled(false);
		}
	}
}
