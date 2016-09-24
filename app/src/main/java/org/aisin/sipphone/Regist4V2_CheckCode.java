package org.aisin.sipphone;

import org.aisin.sipphone.commong.OraLodingUser;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.Misc;
import org.aisin.sipphone.tools.OraLodingUserTools;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Regist4V2_CheckCode extends Activity implements OnListnerShearch,
		OnClickListener {

	private static Context content;
	private String reg_phonenum;
	private LinearLayout rg4v2_cc_linelayout;
	private ImageView regist4v2_code_back;
	private TextView yzcode_phnum;
	private LineEditText rgv2_phnum;
	private TextView cannotsevedcode;
	private TextView timejs;
	private TextView r42_ccode_ts;
	private TextView rg4v2_code_regorlog;
	private ProgressDialog prd;
	private int num = Constants.YcodeTime;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 获取个人资料
				new HttpTask_getselfdata().execute("getselfdata");
			} else if (msg.what == 2) {
				if (prd != null) {
					prd.dismiss();
				}
				AisinBuildDialog mybuild = new AisinBuildDialog(
						Regist4V2_CheckCode.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("恭喜您，注册成功!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								// 跳到修改密码界面
								if (content != null) {
									UserInfo ui = UserInfo_db
											.getUserInfo(content);
									Intent intent = new Intent(
											content,
											org.aisin.sipphone.Regist4V2_ChangePwd.class);
									intent.putExtra("phnum", ui.getPhone());
									intent.putExtra("nn_pwd", ui.getPwd());
									startActivity(intent);
									Regist4V2_CheckCode.this.finish();
								}
							}
						});
				mybuild.dialogShow();
			}else if (msg.what == 3) {
				timejs.setText(num + "秒");
			} else if (msg.what == 4) {
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
		content = this;
		reg_phonenum = getIntent().getStringExtra("reg_phonenum");
		if (reg_phonenum == null || reg_phonenum.trim().length() != 11) {
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
		yzcode_phnum.setText("验证码已发送至 +86" + reg_phonenum);
		cannotsevedcode.setOnClickListener(this);
		rgv2_phnum.setShearchListner(this);
		rgv2_phnum.setFocusable(true);
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
						mHandler.sendEmptyMessage(3);
						num -= 1;
						if (num < 0) {
							mHandler.sendEmptyMessage(4);
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
	public void Search(String text) {
		if (text.trim().length() > 0) {
			rg4v2_code_regorlog.setEnabled(true);
		} else {
			rg4v2_code_regorlog.setEnabled(false);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.regist4v2_code_back:

			AisinBuildDialog mybuild_2 = new AisinBuildDialog(
					Regist4V2_CheckCode.this);
			mybuild_2.setTitle("提示");
			mybuild_2.setMessage("验证码已下发，确定返回？");
			mybuild_2.setOnDialogCancelListener("取消", null);
			mybuild_2.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							// 跳转到注册界面
							Intent intent = new Intent(
									Regist4V2_CheckCode.this,
									org.aisin.sipphone.Regist4V2.class);
							Regist4V2_CheckCode.this.startActivity(intent);
							Regist4V2_CheckCode.this.finish();

						}
					});
			mybuild_2.dialogShow();
			break;
		case R.id.cannotsevedcode:
			AisinBuildDialog mybuild = new AisinBuildDialog(
					Regist4V2_CheckCode.this);
			mybuild.setTitle("提示");
			mybuild.setMessage("是否再次发送验证码？");
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							// 下发验证码
							prd.setMessage("获取验证码...");
							prd.show();
							new HttpTask_getyzcode().execute("agen");

						}
					});
			mybuild.dialogShow();
			break;
		case R.id.rg4v2_code_regorlog:
			// 注册
			prd.setMessage("注册中...");
			prd.show();
			new HttpTask_Regist().execute("reg");
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键退出到注册登录界面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AisinBuildDialog mybuild_2 = new AisinBuildDialog(
					Regist4V2_CheckCode.this);
			mybuild_2.setTitle("提示");
			mybuild_2.setMessage("验证码已下发，确定返回？");
			mybuild_2.setOnDialogCancelListener("取消", null);
			mybuild_2.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							// 跳转到注册界面
							Intent intent = new Intent(
									Regist4V2_CheckCode.this,
									org.aisin.sipphone.Regist4V2.class);
							Regist4V2_CheckCode.this.startActivity(intent);
							Regist4V2_CheckCode.this.finish();

						}
					});
			mybuild_2.dialogShow();
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(rg4v2_cc_linelayout);// 回收容
	}

	// 发送注册验证码
	private class HttpTask_getyzcode extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			String reg_url = URLTools.GetHttpURL_4RegistGetCode_V2(
					Regist4V2_CheckCode.this, reg_phonenum.trim());
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
			switch (doresult) {
			case 0:
				if (authcode != null && !"".equals(authcode)) {
					r42_ccode_ts.setText("请输入该验证码: " + authcode);
				} else {
					r42_ccode_ts.setText("");
				}
				cannotsevedcode.setEnabled(false);
				cannotsevedcode.setTextColor(Color.parseColor("#646464"));
				return;
			case 5:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "输入不能为空!");
				break;
			case 6:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "sign错误!");
				break;
			case 35:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"发送给同一手机号码短信的数量超过当日限制");
				break;
			case 37:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "手机号码格式不对");
				break;
			case 38:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"暂不支持该地区电话号码注册!");
				break;
			case 45:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "服务器异常!");
				break;
			default:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "服务器异常!");
				break;
			}
		}
	}

	// 注册
	private class HttpTask_Regist extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {

			String reg_url = URLTools.GetHttpURL_4REG_V2(
					Regist4V2_CheckCode.this, reg_phonenum.trim(), rgv2_phnum
							.getText().toString().trim());
			String result = HttpUtils.result_url_get(reg_url,
					"{'result':'-104'}");
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

			switch (doresult) {
			case 0:
				String uid = json.optString("uid", "0");
				String bding_phone = json.optString("phone", "");
				String pwd = json.optString("pwd", "");
				// 保存用户数据
				UserInfo_db.SaveUserInfo(Regist4V2_CheckCode.this, bding_phone,
						reg_phonenum.trim(), Misc.cryptDataByPwd(pwd.trim()),
						uid.trim());// 密码需解密

				// 保存agent_id
				try {
					String agentid = json.getString("agent_id");
					SharedPreferences spf = SharedPreferencesTools
							.getSharedPreferences_4agent_id(Regist4V2_CheckCode.this);
					if (agentid != null && spf != null) {
						spf.edit()
								.putString(SharedPreferencesTools.agent_id,
										agentid.trim()).commit();
					}
				} catch (Exception e) {
				}

				// 记录登录过的用户
				OraLodingUserTools.addolus(
						Regist4V2_CheckCode.this,
						new OraLodingUser(reg_phonenum.trim(), System
								.currentTimeMillis()));
				mHandler.sendEmptyMessage(1);
				return;
			case 5:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "输入不能为空!");
				break;
			case 6:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "服务器异常!");
				break;
			case 33:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"验证码过期,请重新获取!");
				break;
			case 32:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示", "验证码错误!");
				break;
			case 31:
				if (prd != null) {
					prd.dismiss();
				}

				AisinBuildDialog mybuild = new AisinBuildDialog(
						Regist4V2_CheckCode.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("该账号已注册!是否直接登录?");
				mybuild.setOnDialogCancelListener("取消", null);
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								// 启动登录Activity
								Intent intent = new Intent(
										Regist4V2_CheckCode.this,
										org.aisin.sipphone.R_Loding4v2.class);
								intent.putExtra("str_binput",
										reg_phonenum.trim());
								Regist4V2_CheckCode.this.startActivity(intent);
								Regist4V2_CheckCode.this.finish();

							}
						});
				mybuild.dialogShow();
				return;
			case 38:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"暂不支持该地区电话号码注册!");
				break;
			default:
				new AisinBuildDialog(Regist4V2_CheckCode.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
			super.onPostExecute(result);
		}
	}

	// 获取个人资料
	private class HttpTask_getselfdata extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			UserXXInfo uxi = User_data_Ts.getUXXInfo4DB_self(
					Regist4V2_CheckCode.this,
					UserInfo_db.getUserInfo(Regist4V2_CheckCode.this).getUid());
			UserInfo usi = UserInfo_db.getUserInfo(Regist4V2_CheckCode.this);
			String ver = null;
			if (uxi == null) {
				ver = "1.0";
			} else {
				ver = uxi.getVer();
			}
			String reg_url = URLTools.GetHttpURL_4UserXXinfo_url(
					Regist4V2_CheckCode.this, usi.getUid(), ver);
			String result = HttpUtils.result_url_get(reg_url,
					"{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			// 处理数据
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (JSONException e) {
			}
			switch (doresult) {
			case 0:
				// 存储数据
				try {
					int uid = json.getInt("uid");
					String province = json.optString("province");
					String picture = json.optString("picture");
					String picmd5 = json.optString("picmd5");
					String ver = json.optString("ver");
					String picurl_prefix = json.optString("picurl_prefix");
					String city = json.optString("city");
					String company = json.optString("company");
					String profession = json.optString("profession");
					String school = json.optString("school");
					String sex = json.optString("sex");
					String birthday = json.optString("birthday");
					String location = json.optString("location");
					String signature = json.optString("signature");
					String from = json.optString("from_self");
					String mobileNumber = json.optString("mobileNumber");
					String email = json.optString("email");
					String name = json.optString("name");
					ContentValues cv = new ContentValues();
					cv.put("uid", uid);
					if (province != null && !"".equals(province)
							&& !"null".equals(province)) {
						cv.put("province", province);
					}
					if (picture != null && !"".equals(picture)
							&& !"null".equals(picture)) {
						cv.put("picture", picture);
					}
					if (ver != null && !"".equals(ver) && !"null".equals(ver)) {
						cv.put("ver", ver);
					}
					if (picmd5 != null && !"".equals(picmd5)
							&& !"null".equals(picmd5)) {
						cv.put("picmd5", picmd5);
					}
					if (picurl_prefix != null && !"".equals(picurl_prefix)
							&& !"null".equals(picurl_prefix)) {
						cv.put("picurl_prefix", picurl_prefix);
					}
					if (city != null && !"".equals(city)
							&& !"null".equals(city)) {
						cv.put("city", city);
					}
					if (company != null && !"".equals(company)
							&& !"null".equals(company)) {
						cv.put("company", company);
					}
					if (profession != null && !"".equals(profession)
							&& !"null".equals(profession)) {
						cv.put("profession", profession);
					}
					if (school != null && !"".equals(school)
							&& !"null".equals(school)) {
						cv.put("school", school);
					}
					if (sex != null && !"".equals(sex) && !"null".equals(sex)) {
						cv.put("sex", sex);
					}
					if (location != null && !"".equals(location)
							&& !"null".equals(location)) {
						cv.put("location", location);
					}
					if (birthday != null && !"".equals(birthday)
							&& !"null".equals(birthday)) {
						cv.put("birthday", birthday);
					}
					if (signature != null && !"".equals(signature)
							&& !"null".equals(signature)) {
						cv.put("signature", signature);
					}
					if (from != null && !"".equals(from)
							&& !"null".equals(from)) {
						cv.put("from_self", from);
					}
					if (mobileNumber != null && !"".equals(mobileNumber)
							&& !"null".equals(mobileNumber)) {
						cv.put("mobileNumber", mobileNumber);
					}
					if (email != null && !"".equals(email)
							&& !"null".equals(email)) {
						cv.put("email", email);
					}
					if (name != null && !"".equals(name)
							&& !"null".equals(name)) {
						cv.put("name", name);
					}
					User_data_Ts.add2upUXXI(Regist4V2_CheckCode.this, uid + "",
							cv);
				} catch (Exception e) {
				}
				break;
			}

			mHandler.sendEmptyMessage(2);
		}
	}

}
