package org.aisin.sipphone;

import java.util.ArrayList;

import org.aisin.sipphone.commong.OraLodingUser;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class R_Loding4v2 extends Activity implements OnClickListener,
		OnListnerShearch, TextWatcher, OnItemClickListener {

	private FrameLayout r_loding4v2_layout;
	private ImageView r_loding4v2_back;
	private LineEditText r_loding4v2_phnum;
	private ImageView rl4v2_clearpnum;
	private LineEditText r_loding4v2_pwd;
	private TextView r_loding4v2_lodingbt;
	private TextView r_loding4v2_getpassword;
	private ListView lplist;
	private ArrayList<OraLodingUser> oldus = new ArrayList<OraLodingUser>();// 存储登录过的账户
	private ArrayList<OraLodingUser> oldus_showlist = new ArrayList<OraLodingUser>();// 存储需要在lisview中显示的账户
	private R_Loding4v2_SPLIST rl4v2sadapter;
	private ProgressDialog prd;
	private String str_binput;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (rl4v2sadapter == null) {
					rl4v2sadapter = new R_Loding4v2_SPLIST(R_Loding4v2.this,
							oldus_showlist);
					lplist.setAdapter(rl4v2sadapter);
				} else {
					rl4v2sadapter.notifyDataSetChanged();
				}
			} else if (msg.what == 2) {
				// 更新后更新用户资料
				new HttpTask_getselfdata().execute("");
			} else if (msg.what == 3) {
				if (prd != null) {
					prd.dismiss();
				}
				AisinBuildDialog mybuild = new AisinBuildDialog(
						R_Loding4v2.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("登录成功!");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								if (CheckUpadateTime
										.CheckResult_4bootpager(R_Loding4v2.this)) {
									Intent intent = new Intent(
											R_Loding4v2.this,
											org.aisin.sipphone.BootPage.class);
									R_Loding4v2.this.startActivity(intent);
									R_Loding4v2.this.overridePendingTransition(
											R.anim.aisinactivityinput,
											R.anim.startpageroutput);
									R_Loding4v2.this.finish();
								} else {
									// 检测是否展示动态开屏还是直接启动主界面
									new HttpTask_start_page(R_Loding4v2.this,
											mHandler).execute("");
								}

							}
						});
				mybuild.dialogShow();
			} else if (msg.what == -1) {
				R_Loding4v2.this.finish();
			} else if (msg.what == -2) {
				// 启动开屏页
				Intent itent = new Intent(R_Loding4v2.this,
						org.aisin.sipphone.StartPager.class);
				R_Loding4v2.this.startActivity(itent);
				R_Loding4v2.this.overridePendingTransition(
						R.anim.aisinactivityinput, R.anim.startpageroutput);
				R_Loding4v2.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.r_loding4v2);
		r_loding4v2_layout = (FrameLayout) this
				.findViewById(R.id.r_loding4v2_layout);
		r_loding4v2_back = (ImageView) this.findViewById(R.id.r_loding4v2_back);
		r_loding4v2_phnum = (LineEditText) this
				.findViewById(R.id.r_loding4v2_phnum);
		rl4v2_clearpnum = (ImageView) this.findViewById(R.id.rl4v2_clearpnum);
		r_loding4v2_pwd = (LineEditText) this
				.findViewById(R.id.r_loding4v2_pwd);
		r_loding4v2_lodingbt = (TextView) this
				.findViewById(R.id.r_loding4v2_lodingbt);
		r_loding4v2_getpassword = (TextView) this
				.findViewById(R.id.r_loding4v2_getpassword);
		lplist = (ListView) this.findViewById(R.id.lplist);
		lplist.setOnItemClickListener(this);
		r_loding4v2_back.setOnClickListener(this);
		r_loding4v2_phnum.setShearchListner(this);
		r_loding4v2_phnum.addTextChangedListener(this);
		r_loding4v2_phnum.setFocusable(true);
		r_loding4v2_pwd.setShearchListner(this);
		rl4v2_clearpnum.setOnClickListener(this);
		r_loding4v2_lodingbt.setOnClickListener(this);
		r_loding4v2_getpassword.setOnClickListener(this);
		ArrayList<OraLodingUser> oldus_t = OraLodingUserTools.getolus(this);
		if (oldus_t != null) {
			oldus.addAll(oldus_t);
			oldus_t.clear();
			oldus_t = null;
		}
		if (oldus.size() > 0) {
			r_loding4v2_phnum.setText(oldus.get(0).getPhonenum());
			r_loding4v2_phnum.setSelection(oldus.get(0).getPhonenum().length());
		}
		str_binput = getIntent().getStringExtra("str_binput");
		if (str_binput != null && !"".equals(str_binput)) {
			r_loding4v2_phnum.setText(str_binput);
			r_loding4v2_phnum.setSelection(str_binput.length());
		}
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(r_loding4v2_layout);// 回收容
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.r_loding4v2_back:
			Intent intent = new Intent(R_Loding4v2.this,
					org.aisin.sipphone.LodingActivity.class);
			intent.putExtra("start_flag", "NOWAIT");
			// 返回登录注册界面
			startActivity(intent);
			finish();
			break;

		case R.id.rl4v2_clearpnum:
			r_loding4v2_phnum.setText("");
			break;
		case R.id.r_loding4v2_lodingbt:
			// 登录
			prd.setMessage("登录中...");
			prd.show();
			new HttpTask_Loding().execute("loding");
			break;
		case R.id.r_loding4v2_getpassword:
			// 忘记密码
			Intent intent_gp = new Intent(this,
					org.aisin.sipphone.setts.SetRetrievePassword4V2.class);
			startActivity(intent_gp);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键退出到注册登录界面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this,
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

	@Override
	public void Search(String text) {
		if (r_loding4v2_phnum.getText().toString().trim().length() == 11
				&& r_loding4v2_pwd.getText().toString().trim().length() > 0) {
			r_loding4v2_lodingbt.setEnabled(true);
		} else {
			r_loding4v2_lodingbt.setEnabled(false);
		}

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s.toString().isEmpty()) {
			rl4v2_clearpnum.setVisibility(View.INVISIBLE);
			oldus_showlist.clear();
			oldus_showlist.addAll(oldus);
			lplist.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(1);
		} else if (s.toString().length() > 0 && s.toString().length() < 11) {
			rl4v2_clearpnum.setVisibility(View.VISIBLE);
			oldus_showlist.clear();
			for (int i = 0; i < oldus.size(); i++) {
				if (oldus.get(i).getPhonenum().startsWith(s.toString())) {
					oldus_showlist.add(oldus.get(i));
				}
			}
			lplist.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(1);
		} else {
			rl4v2_clearpnum.setVisibility(View.VISIBLE);
			oldus_showlist.clear();
			lplist.setVisibility(View.GONE);
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		lplist.setVisibility(View.GONE);
		r_loding4v2_phnum.setText(oldus_showlist.get(position).getPhonenum());
	}

	// 登录
	private class HttpTask_Loding extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			String reg_url = URLTools.GetHttpURL_4LODING_V2(R_Loding4v2.this,
					r_loding4v2_phnum.getText().toString().trim(),
					r_loding4v2_pwd.getText().toString().trim());
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
				String pwd = json.optString("password", "");
				// 保存用户数据
				UserInfo_db.SaveUserInfo(R_Loding4v2.this, bding_phone,
						r_loding4v2_phnum.getText().toString().trim(),
						Misc.cryptDataByPwd(pwd.trim()), uid.trim());// 密码需解密
				// 保存agent_id
				try {
					String agentid = json.getString("agent_id");
					SharedPreferences spf = SharedPreferencesTools
							.getSharedPreferences_4agent_id(R_Loding4v2.this);
					if (agentid != null && spf != null) {
						spf.edit()
								.putString(SharedPreferencesTools.agent_id,
										agentid.trim()).commit();
					}
				} catch (Exception e) {
				}

				// 记录登录过的用户
				OraLodingUserTools
						.addolus(R_Loding4v2.this, new OraLodingUser(
								r_loding4v2_phnum.getText().toString().trim(),
								System.currentTimeMillis()));
				mHandler.sendEmptyMessage(2);
				return;
			case 5:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "sign错误!");
				break;
			case 37:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "手机号码错误,请重新输入!");
				break;
			case 45:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "服务器异常!");
				break;
			case 13:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "用户不存在!");
				break;
			case 12:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "密码错误!");
				break;
			default:
				new AisinBuildDialog(R_Loding4v2.this, "提示", "联网失败，请检查您的网络信号！");
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
			UserXXInfo uxi = User_data_Ts.getUXXInfo4DB_self(R_Loding4v2.this,
					UserInfo_db.getUserInfo(R_Loding4v2.this).getUid());
			UserInfo usi = UserInfo_db.getUserInfo(R_Loding4v2.this);
			String ver = null;
			if (uxi == null) {
				ver = "1.0";
			} else {
				ver = uxi.getVer();
			}
			String reg_url = URLTools.GetHttpURL_4UserXXinfo_url(
					R_Loding4v2.this, usi.getUid(), ver);
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
					User_data_Ts.add2upUXXI(R_Loding4v2.this, uid + "", cv);
				} catch (Exception e) {
				}
				break;
			}
			mHandler.sendEmptyMessage(3);
		}
	}
}
