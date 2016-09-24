package org.aisin.sipphone.setts;

import org.aisin.sipphone.LineEditText;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChangePassword extends Activity implements OnClickListener,
		OnListnerShearch {
	private LinearLayout changepassword_linlayout;
	private ImageView setts_changepassword_back;
	private LineEditText changepassword_old;
	private LineEditText changepassword_new;
	private TextView changepassword_bt;
	private ProgressDialog prd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepassword);
		changepassword_linlayout = (LinearLayout) this
				.findViewById(R.id.changepassword_linlayout);
		setts_changepassword_back = (ImageView) this
				.findViewById(R.id.setts_changepassword_back);
		changepassword_old = (LineEditText) this
				.findViewById(R.id.changepassword_old);
		changepassword_new = (LineEditText) this
				.findViewById(R.id.changepassword_new);
		changepassword_bt = (TextView) this
				.findViewById(R.id.changepassword_bt);
		setts_changepassword_back.setOnClickListener(this);
		changepassword_bt.setOnClickListener(this);
		changepassword_old.setFocusable(true);
		changepassword_old.setShearchListner(this);
		changepassword_new.setShearchListner(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setts_changepassword_back:
			finish();
			break;

		case R.id.changepassword_bt:
			if (!Checkinput()) {
				return;
			}
			// 修改密码
			prd.setMessage("修改密码...");
			prd.show();
			new HttpTask_changepassword().execute("changepwd");
			break;
		}
	}

	private boolean Checkinput() {

		if (!Check_network.isNetworkAvailable(ChangePassword.this)) {
			new AisinBuildDialog(ChangePassword.this, "提示", "网络不可用,请检查网络连接!");
			return false;
		}
		if (changepassword_old.getText().toString().trim().length() == 0) {
			new AisinBuildDialog(ChangePassword.this, "提示", "旧密码不能为空!");
			return false;
		}
		if (changepassword_new.getText().toString().trim().length() == 0) {
			new AisinBuildDialog(ChangePassword.this, "提示", "新密码不能为空!");
			return false;
		}
		boolean rg_passowrd_falg = Check_format
				.check_password4regset(changepassword_old.getText().toString()
						.trim());
		if (!rg_passowrd_falg) {
			new AisinBuildDialog(ChangePassword.this, "提示", "旧密码由数字或者字母组成!");
			return false;
		}
		boolean rg_passowrd_falg2 = Check_format
				.check_password4regset(changepassword_new.getText().toString()
						.trim());
		if (!rg_passowrd_falg2) {
			new AisinBuildDialog(ChangePassword.this, "提示", "新密码只能由数字或者字母组成!");
			return false;
		}
		if (changepassword_old.getText().toString().trim()
				.equals(changepassword_new.getText().toString().trim())) {
			new AisinBuildDialog(ChangePassword.this, "提示", "新密码不能和旧密码相同!");
			return false;
		}
		return true;
	}

	private class HttpTask_changepassword extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 得到更改密码的URL
			String url = URLTools.GetHttpURL_4ChangePassword_V2(
					ChangePassword.this, changepassword_old.getText()
							.toString().trim(), changepassword_new.getText()
							.toString().trim(),
					UserInfo_db.getUserInfo(ChangePassword.this).getPhone());
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
				if (prd != null) {
					prd.dismiss();
				}
				// 保存用户数据
				UserInfo userinfo = UserInfo_db
						.getUserInfo(ChangePassword.this);
				UserInfo_db.SaveUserInfo(ChangePassword.this,
						userinfo.getBding_phone(), userinfo.getPhone().trim(),
						changepassword_new.getText().toString().trim(),
						userinfo.getUid().trim());

				AisinBuildDialog mybuild = new AisinBuildDialog(
						ChangePassword.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("密码修改成功!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								ChangePassword.this.finish();
							}
						});
				mybuild.dialogShow();
				return;
			case 5:
				new AisinBuildDialog(ChangePassword.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(ChangePassword.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(ChangePassword.this, "提示", "手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(ChangePassword.this, "提示", "服务器异常!");
				break;
			case 13:
				new AisinBuildDialog(ChangePassword.this, "提示", "用户不存在!");
				break;
			case 12:
				new AisinBuildDialog(ChangePassword.this, "提示", "原密码错误!");
				break;
			default:
				new AisinBuildDialog(ChangePassword.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
			changepassword_bt.setText("确定");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(changepassword_linlayout);// 回收容器
	}

	@Override
	public void Search(String text) {
		if (changepassword_old.getText().toString().length() > 0
				&& changepassword_new.getText().toString().length() > 0) {
			changepassword_bt.setEnabled(true);
		} else {
			changepassword_bt.setEnabled(false);
		}
	}
}
