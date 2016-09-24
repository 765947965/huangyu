package org.aisin.sipphone;

import java.util.ArrayList;
import java.util.TreeMap;

import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.sqlitedb.Friend_data_Check;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.ContactLoadinteface;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.DBManager;
import org.aisin.sipphone.tools.GetAction_reports;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LodingActivity extends Activity implements OnClickListener,
		ContactLoadinteface {
	private final int WAIT_FOR = 1;
	public static final int RETT = 0, LD = 1;
	private FrameLayout loding_linlayout;
	private TextView registration;
	private TextView loding;
	private String start_flag;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == WAIT_FOR) {
				// 如果用户没有注册跳入注册页面
				if (UserInfo_db.getUserInfo(LodingActivity.this) == null) {
					registration.setVisibility(View.VISIBLE);
					loding.setVisibility(View.VISIBLE);
				} else {
					if (CheckUpadateTime
							.CheckResult_4bootpager(LodingActivity.this)) {
						Intent intent = new Intent(LodingActivity.this,
								org.aisin.sipphone.BootPage.class);
						LodingActivity.this.startActivity(intent);
						LodingActivity.this.overridePendingTransition(
								R.anim.aisinactivityinput,
								R.anim.startpageroutput);
						LodingActivity.this.finish();
					} else {
						// 检测是否展示动态开屏还是直接启动主界面
						new HttpTask_start_page(LodingActivity.this, mHandler)
								.execute("");
					}
				}

			} else if (msg.what == -1) {
				LodingActivity.this.finish();
			} else if (msg.what == -2) {
				// 启动开屏页
				Intent itent = new Intent(LodingActivity.this,
						org.aisin.sipphone.StartPager.class);
				LodingActivity.this.startActivity(itent);
				LodingActivity.this.overridePendingTransition(
						R.anim.aisinactivityinput, R.anim.startpageroutput);
				LodingActivity.this.finish();
			} else if (msg.what == 2) {
				if (start_flag != null && "NOWAIT".equals(start_flag)) {
					// 从注册界面返回来的 无需等待效果 直接显示注册或登录按钮
					mHandler.sendEmptyMessage(WAIT_FOR);
				} else {
					/*
					 * mHandler.postDelayed(new Runnable() {
					 * 
					 * @Override public void run() { // 制造等待效果进入注册界面或者主界面
					 * mHandler.sendEmptyMessage(WAIT_FOR); } }, 500);
					 */
					mHandler.sendEmptyMessage(WAIT_FOR);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Intent intent = getIntent();
			if (intent != null) {
				String data = intent.getDataString();
				int wh_num = data.indexOf("?");
				if (wh_num > 0) {
					data = data.substring(wh_num + 1);
					String[] kvs = data.split("&");
					Editor edit = SharedPreferencesTools
							.getSharedPreferences_4wwwdata(this).edit();
					if (kvs != null && kvs.length > 0) {
						for (String str : kvs) {
							String[] kv = str.split("=");
							if (kv != null && kv.length == 2) {
								edit.putString(kv[0].trim(), kv[1].trim());
							}
						}
					}
					edit.commit();
				}
			}
		} catch (Exception e) {
		}

		setContentView(R.layout.loding);
		loding_linlayout = (FrameLayout) this
				.findViewById(R.id.loding_linlayout);
		registration = (TextView) this.findViewById(R.id.registration);
		registration.setOnClickListener(this);
		loding = (TextView) this.findViewById(R.id.loding);
		loding.setOnClickListener(this);
		new PhoneInfo(this);// 初始化手机信息
		start_flag = getIntent().getStringExtra("start_flag");

		// 开启线程初始化联系人数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				new DBManager(LodingActivity.this, R.raw.db_citys,
						"db_citys.db").openDatabase();// 初始化城市数据库
				new DBManager(LodingActivity.this, R.raw.callhomedb,
						"callHomeDB.db").openDatabaseFromZIP();// 初始化手机归属地
				// 载入广告效果数据
				GetAction_reports
						.ADDARSNUM(LodingActivity.this, null, "", 0, 0);
				mHandler.sendEmptyMessage(2);
				// CursorTools.loadContacts_2(LodingActivity.this,
				// LodingActivity.this, false);
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
		case R.id.registration:
			Intent intent_rg = new Intent(LodingActivity.this,
					org.aisin.sipphone.Regist4V2.class);
			startActivity(intent_rg);
			break;
		case R.id.loding:
			Intent intent_ld = new Intent(LodingActivity.this,
					org.aisin.sipphone.R_Loding4v2.class);
			startActivity(intent_ld);
			break;
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(loding_linlayout);// 回收容
	}

	@Override
	public void DoadDown_map(TreeMap<Long, Contact> cttmap) {
		ArrayList<Contact> altemp = Friend_data_Check
				.GetAllFriends(LodingActivity.this);
		if (altemp != null) {
			CursorTools.friendslist.addAll(altemp);
			altemp.clear();
			altemp = null;
		}
		mHandler.sendEmptyMessage(2);
	}

	@Override
	public void headimagedown(boolean upflag) {
		// TODO Auto-generated method stub
	}

	@Override
	public void upfrendsdwon(boolean upflag) {
		// TODO Auto-generated method stub
	}

	@Override
	public void showfriends(boolean upflag) {
		// TODO Auto-generated method stub

	}

}
