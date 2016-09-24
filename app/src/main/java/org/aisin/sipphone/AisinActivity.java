package org.aisin.sipphone;

import java.util.ArrayList;
import java.util.Random;

import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.dial.Dial_Fragment;
import org.aisin.sipphone.dial.Dial_barImageInterf;
import org.aisin.sipphone.directcall.AisinService;
import org.aisin.sipphone.find.Find_Fragment;
import org.aisin.sipphone.mai_list.MailList_Fragment;
import org.aisin.sipphone.nearbystores.Nearby_stores_Fragment;
import org.aisin.sipphone.setts.Setts_Fragment;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.ContactsManager;
import org.aisin.sipphone.tools.DownloadImage;
import org.aisin.sipphone.tools.Downloadcomplete;
import org.aisin.sipphone.tools.GetAction_reports;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.JumpMap;
import org.aisin.sipphone.tools.KeyBoardSoundPools;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.aisin.sipphone_call_records.CallRecords_Fragment;

import cn.bmob.v3.Bmob;

import com.baidu.mapapi.SDKInitializer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AisinActivity extends FragmentActivity implements OnClickListener,
		Dial_barImageInterf, Downloadcomplete, OnLongClickListener {
	public static Context context;
	private redrecedBroadcast redcedbd;// 红包刷新 清除拨号盘号码 接受广播
	private ImageView red_dot;
	private RelativeLayout aisinactivity_relayout;
	private ImageView dial;
	private ImageView mail_list;
	private ImageView call_records;
	private ImageView find;
	private ImageView nearby_stores;
	private RelativeLayout setbarlayout;
	private ImageView setts;
	private RelativeLayout mysetts_2layout;
	private ImageView red_dot_2;
	private LinearLayout tabbar_lainelayout;
	private ImageView tab_call_bt;
	private ImageView tab_callbanck_bt;
	private FragmentManager fm;
	private final int MF1 = 1, MF2 = 2, MF3 = 3, MF4 = 4, MF5 = 5, MF6 = 6;
	private int FlagMF = 0;
	private Dial_Fragment dial_fragment;
	private MailList_Fragment mail_list_fragment;
	private CallRecords_Fragment call_records_fragment;
	private Find_Fragment find_fragment;
	private Nearby_stores_Fragment near_fragment;
	private Setts_Fragment setts_fragment;
	private int dialimageid = R.drawable.dial_selected1_up;
	private ContentObserver maillistreserver;
	private ContentObserver callrecordstreserver;
	private boolean outMode = true;// 退出方式true 直接退出虚拟机
	// 记录是否要更新通话记录
	private boolean Thjl = false;// 记录是否要更新通话记录
	private boolean Txl = false;// 记录是否要更新通讯录

	private static AisinActivity instance;

	private boolean resulmbflag = true;// 避免onResume重复执行

	private String startpager_to;

	private int numclearnum = 0;// 按清除号码的次数
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				dial.setImageResource(dialimageid);// 更新bar图标
			} else if (msg.what == 2) {
				call_records_fragment.ResetcallRrecords();// 更新通话记录
			} else if (msg.what == 3) {// 更新通讯录
				mail_list_fragment.ReSetMailListview();
			} else if (msg.what == 4) {// 更新广告页
				GetGImageMap.getImageMap(AisinActivity.this, true);// 强制刷新广告图片集合
				dial_fragment.ReSetViewPager();// 通知拨号界面更新广告
				find_fragment.ReSetViewPager();// 通知发现界面更新广告
			} else if (msg.what == 5) {// 伸缩拨号界面的键盘
				dial_fragment.DownORHNP_mhander();
			} else if (msg.what == 6) {// 服务页更新广告
				find_fragment.upServerdata();
			} else if (msg.what == 7) {
				new HttpTask_Red_CHECKOUT(context, mHandler).execute("redlist");
			} else if (msg.what == 8) {

				new HttpTask_Red_CHECKOUT(context, mHandler).execute("");
			} else if (msg.what == 9) {
				// 刷新我的小圆点
				// 检查是否显示小红点
				boolean hongdianflag = false;
				if (SharedPreferencesTools.getSharedPreferences_4RED_COUT(
						context).getBoolean(SharedPreferencesTools.REDCOUT_key,
						false)) {
					hongdianflag = true;
				}
				// 检查是否有新版本
				if (CheckUpadateTime.CheckResult_4newAPP(context)) {
					hongdianflag = true;
				}
				// 检查是否阅读过了96IP电话说明
				if (SharedPreferencesTools.getSharedPreferences_496IP(
						AisinActivity.context).getBoolean(
						SharedPreferencesTools.k96IP, false)) {

				}

				if (hongdianflag) {
					red_dot.setVisibility(View.VISIBLE);
					red_dot_2.setVisibility(View.VISIBLE);
				} else {
					red_dot.setVisibility(View.INVISIBLE);
					red_dot_2.setVisibility(View.INVISIBLE);
				}
				// 通知设置界面更新
				setts_fragment.sendmhandler();
			} else if (msg.what == 10) {
				// 通知拨号盘清除已输入号码
				dial_fragment.Clearinputnum();
			} else if (msg.what == 11) {
				new HttpTask_Red(context, mHandler).execute("red");
			} else if (msg.what == 12) {
				Toast tost = Toast.makeText(AisinActivity.this, "长按清除键可清除全部号码",
						Toast.LENGTH_SHORT);
				tost.setGravity(Gravity.CENTER, 0, 0);
				tost.show();
			} else if (msg.what == 13) {
				dial_fragment.UPKeyBack();// 更新拨号盘背景
			} else if (msg.what == 14) {
				dial_fragment.UPKeyBoardMusic();// 更新拨号盘音乐
			} else if (msg.what == 15) {
				dial_fragment.UPNetWorkFlag(false);// 网络不可用
			} else if (msg.what == 16) {
				dial_fragment.UPNetWorkFlag(true);// 网络可用
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		Constants.showbaidumap = SharedPreferencesTools
				.getSharedPreferences_4YCSZ(this).getBoolean(
						SharedPreferencesTools.ycsz_neary_flag,
						Constants.showbaidumap);
		if (Constants.showbaidumap) {
			try {
				SDKInitializer.initialize(getApplicationContext());// 百度初始化
			} catch (Error e) {
				Constants.showbaidumap = false;
			}
		}
		super.onCreate(savedInstanceState);
		context = this;
		instance = this;
		// 初始化音池
		new Thread(new Runnable() {
			@Override
			public void run() {
				KeyBoardSoundPools.getSoundPools(context);
			}
		}).start();
		startpager_to = this.getIntent().getStringExtra("startpager_to");
		setContentView(R.layout.aisinactivity);
		GetGImageMap.getImageMap(this, false);// 加载图片
		dial_fragment = new Dial_Fragment() {

			@Override
			protected void setUE(String yue, String date) {
				// TODO Auto-generated method stub
				setts_fragment.setYUEValue(yue, date);
			}
		};
		dial_fragment.setDial_barImageInterf(this);
		mail_list_fragment = new MailList_Fragment();
		call_records_fragment = new CallRecords_Fragment();
		find_fragment = new Find_Fragment();
		setts_fragment = new Setts_Fragment();
		if (Constants.showbaidumap) {
			near_fragment = new Nearby_stores_Fragment();
		}
		// 把四个fragment添加到管理器
		fm = getSupportFragmentManager();
		FragmentTransaction fttemp = fm.beginTransaction();
		fttemp.add(R.id.fragments, dial_fragment);
		fttemp.add(R.id.fragments, mail_list_fragment);
		fttemp.add(R.id.fragments, call_records_fragment);
		fttemp.add(R.id.fragments, find_fragment);
		fttemp.add(R.id.fragments, setts_fragment);
		if (Constants.showbaidumap) {
			fttemp.add(R.id.fragments, near_fragment);
		}
		fttemp.commitAllowingStateLoss();

		red_dot = (ImageView) this.findViewById(R.id.red_dot);
		aisinactivity_relayout = (RelativeLayout) this
				.findViewById(R.id.aisinactivity_relayout);
		dial = (ImageView) this.findViewById(R.id.dial);
		mail_list = (ImageView) this.findViewById(R.id.mail_list);
		call_records = (ImageView) this.findViewById(R.id.call_records);
		find = (ImageView) this.findViewById(R.id.find);
		nearby_stores = (ImageView) this.findViewById(R.id.nearby_stores);
		setbarlayout = (RelativeLayout) this.findViewById(R.id.setbarlayout);
		setts = (ImageView) this.findViewById(R.id.setts);
		mysetts_2layout = (RelativeLayout) this
				.findViewById(R.id.mysetts_2layout);
		red_dot_2 = (ImageView) this.findViewById(R.id.red_dot_2);
		tabbar_lainelayout = (LinearLayout) this
				.findViewById(R.id.tabbar_lainelayout);
		tab_call_bt = (ImageView) this.findViewById(R.id.tab_call_bt);
		tab_callbanck_bt = (ImageView) this.findViewById(R.id.tab_callbanck_bt);
		tab_call_bt.setOnClickListener(this);
		tab_callbanck_bt.setOnClickListener(this);
		tab_callbanck_bt.setOnLongClickListener(this);
		dial.setOnClickListener(this);
		mail_list.setOnClickListener(this);
		call_records.setOnClickListener(this);
		find.setOnClickListener(this);
		setts.setOnClickListener(this);
		nearby_stores.setOnClickListener(this);
		mysetts_2layout.setOnClickListener(this);
		if (Constants.showbaidumap) {
			mysetts_2layout.setVisibility(View.VISIBLE);
			nearby_stores.setVisibility(View.VISIBLE);
			setbarlayout.setVisibility(View.GONE);
		} else {
			mysetts_2layout.setVisibility(View.GONE);
			nearby_stores.setVisibility(View.GONE);
			setbarlayout.setVisibility(View.VISIBLE);
		}
		// 检查是否显示小红点
		boolean hongdianflag = false;
		if (SharedPreferencesTools.getSharedPreferences_4RED_COUT(context)
				.getBoolean(SharedPreferencesTools.REDCOUT_key, false)) {
			hongdianflag = true;
		}
		// 检查是否有新版本
		if (CheckUpadateTime.CheckResult_4newAPP(context)) {
			hongdianflag = true;
		}

		if (hongdianflag) {
			red_dot.setVisibility(View.VISIBLE);
			red_dot_2.setVisibility(View.VISIBLE);
		} else {
			red_dot.setVisibility(View.INVISIBLE);
			red_dot_2.setVisibility(View.INVISIBLE);
		}

		// 判断显示第几个页面
		SharedPreferences spf = SharedPreferencesTools
				.getSharedPreferences_4FIRSTY(this);
		if (!Constants.showbaidumap) {
			if (spf.getInt(SharedPreferencesTools.firstyhome, 1) == 6) {
				spf.edit().putInt(SharedPreferencesTools.firstyhome, 1)
						.commit();
			}
		}
		setTabSelection(spf.getInt(SharedPreferencesTools.firstyhome, 1));

		// 创建联系人和通话记录改变的监听器
		maillistreserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {// 联系人发生改变
				super.onChange(selfChange);
				Txl = true;
				SharedPreferencesTools
						.getSharedPreferences_4upfriends(AisinActivity.context)
						.edit()
						.putString(SharedPreferencesTools.upfrendstime, "0000")
						.putString(SharedPreferencesTools.upfrendsuptime,
								"0000").commit();
			}

		};
		callrecordstreserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {// 通话记录发生改变
				super.onChange(selfChange);
				Thjl = true;
			}
		};
		// 注册联系人和通话记录改变的监听器
		this.getContentResolver().registerContentObserver(
				CallLog.Calls.CONTENT_URI, true, callrecordstreserver);
		this.getContentResolver().registerContentObserver(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true,
				maillistreserver);

		// 注册刷新红包、清除号码盘、刷新通话记录 等提醒事物
		redcedbd = new redrecedBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".redcedbd.upreddate");
		filter.addAction(Constants.BrandName
				+ ".redcedbd.upreddate.cannotcheck");
		filter.addAction(Constants.BrandName + ".dial.clearnum");
		filter.addAction(Constants.BrandName + ".find.upServerdata");
		filter.addAction(Constants.BrandName + ".insert.callhistory");
		filter.addAction(Constants.BrandName + ".insert.callhistory.now");
		filter.addAction(Constants.BrandName + ".mailist.updata");
		filter.addAction(Constants.BrandName + ".setTabSelection");
		filter.addAction(Constants.BrandName + ".redlist");
		filter.addAction(Constants.BrandName + ".keybackUP");
		filter.addAction(Constants.BrandName + ".keyboardmusic");
		filter.addAction(Constants.BrandName + ".network.no");
		filter.addAction(Constants.BrandName + ".network.yes");
		registerReceiver(redcedbd, filter);
		Intent intent = new Intent(this,
				org.aisin.sipphone.directcall.AisinService.class);
		intent.putExtra("startflag", "stopself");
		startService(intent);
	}

	private void ActivityJump() {
		// 检查是否需要跳转
		String wwwdatakey_to = SharedPreferencesTools
				.getSharedPreferences_4wwwdata(this).getString(
						SharedPreferencesTools.urlto, "");
		String wwwdatakey_command = SharedPreferencesTools
				.getSharedPreferences_4wwwdata(this).getString(
						SharedPreferencesTools.command, "");
		if (!"".equals(wwwdatakey_to)) {
			startpager_to = wwwdatakey_to;
			SharedPreferencesTools.getSharedPreferences_4wwwdata(this).edit()
					.putString(SharedPreferencesTools.urlto, "")
					.putString(SharedPreferencesTools.command, "").commit();
		}
		if (startpager_to != null && !"".equals(startpager_to)) {
			if (startpager_to.startsWith("http")) {
				// 跳转到URL
				if (startpager_to.endsWith(".mp4")) {
					Intent intent = new Intent(context,
							org.aisin.sipphone.dial.LoadVideoActivity.class);
					intent.putExtra("url_view", startpager_to);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.CheckWebView.class);
					intent.putExtra("url_view", startpager_to);
					context.startActivity(intent);
				}
			} else if (Check_format.Check_num(startpager_to.trim())) {
				// 匹配要跳转到的Activity
				if (startpager_to.trim().length() == 1) {
					int temp = Integer.parseInt(startpager_to.trim());
					if (temp >= 1 && temp <= 5) {
						setTabSelection(temp);
					}
				} else {
					Intent intenttemp = JumpMap.getIntent(context,
							startpager_to.trim());
					if (intenttemp != null) {
						intenttemp.putExtra("command", wwwdatakey_command);
						this.startActivity(intenttemp);
					}
				}
			}
			startpager_to = null;
		}
	}

	// 得到FragmentTransaction管理器
	private FragmentTransaction get_ft(int FlagMFN) {
		FragmentTransaction ft = fm.beginTransaction();

		// if (FlagMFN > FlagMF) {
		// ft.setCustomAnimations(R.animator.fragment_right_enter,
		// R.animator.fragment_right_exit);
		// } else if (FlagMFN < FlagMF) {
		// ft.setCustomAnimations(R.animator.fragment_left_enter,
		// R.animator.fragment_left_exit);
		// }
		FlagMF = FlagMFN;// 更新目前选中的fragment
		return ft;
	}

	// 底部BAR按钮响应事件
	private void setTabSelection(int index) {
		if (FlagMF == index) {
			if (FlagMF == MF1) {// 如果是点击拨号按钮 发送广播让拨号界面自己判断是否伸缩拨号界面
				mHandler.sendEmptyMessage(5);
			}
			return;// 重复点击同一个fragment
		}
		if (Constants.showbaidumap) {
			if (index == MF1) {
				mysetts_2layout.setVisibility(View.VISIBLE);
			} else {
				mysetts_2layout.setVisibility(View.INVISIBLE);
			}
		}
		// 得到一个Fragment事务
		FragmentTransaction transaction = get_ft(index);
		// 先隐藏掉所有的Fragment，
		hideFragments(transaction);
		// 显示对应的Fragment
		switch (index) {
		case MF1:
			transaction.show(dial_fragment);
			break;
		case MF2:
			transaction.show(mail_list_fragment);
			break;
		case MF3:
			transaction.show(call_records_fragment);
			break;
		case MF4:
			transaction.show(find_fragment);
			break;
		case MF5:
			transaction.show(setts_fragment);
			break;
		case MF6:
			transaction.show(near_fragment);
			break;
		}
		UpImageviewSrc(index);
		transaction.commitAllowingStateLoss();
	}

	// 隐藏所有的fragment
	private void hideFragments(FragmentTransaction transaction) {
		transaction.hide(dial_fragment);
		transaction.hide(mail_list_fragment);
		transaction.hide(call_records_fragment);
		transaction.hide(find_fragment);
		transaction.hide(setts_fragment);
		if (Constants.showbaidumap) {
			transaction.hide(near_fragment);
		}
	}

	// 切换底部菜单栏图标
	public void UpImageviewSrc(int flag) {
		switch (flag) {
		case 1:
			dial.setImageResource(dialimageid);
			mail_list.setImageResource(R.drawable.mail_list_default);
			call_records.setImageResource(R.drawable.call_records_default);
			find.setImageResource(R.drawable.find_default);
			setts.setImageResource(R.drawable.setts_default1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_default);
			break;
		case 2:
			dial.setImageResource(R.drawable.dial_default1);
			mail_list.setImageResource(R.drawable.mail_list_selected);
			call_records.setImageResource(R.drawable.call_records_default);
			find.setImageResource(R.drawable.find_default);
			setts.setImageResource(R.drawable.setts_default1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_default);
			break;
		case 3:
			dial.setImageResource(R.drawable.dial_default1);
			mail_list.setImageResource(R.drawable.mail_list_default);
			call_records.setImageResource(R.drawable.call_records_selected);
			find.setImageResource(R.drawable.find_default);
			setts.setImageResource(R.drawable.setts_default1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_default);
			break;
		case 4:
			dial.setImageResource(R.drawable.dial_default1);
			mail_list.setImageResource(R.drawable.mail_list_default);
			call_records.setImageResource(R.drawable.call_records_default);
			find.setImageResource(R.drawable.find_selected);
			setts.setImageResource(R.drawable.setts_default1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_default);
			break;
		case 5:
			dial.setImageResource(R.drawable.dial_default1);
			mail_list.setImageResource(R.drawable.mail_list_default);
			call_records.setImageResource(R.drawable.call_records_default);
			find.setImageResource(R.drawable.find_default);
			setts.setImageResource(R.drawable.setts_selected1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_default);
			break;
		case 6:
			dial.setImageResource(R.drawable.dial_default1);
			mail_list.setImageResource(R.drawable.mail_list_default);
			call_records.setImageResource(R.drawable.call_records_default);
			find.setImageResource(R.drawable.find_default);
			setts.setImageResource(R.drawable.setts_default1);
			nearby_stores.setImageResource(R.drawable.nearby_stores_selected);
			break;
		}
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.dial:
			setTabSelection(MF1);
			break;

		case R.id.mail_list:
			setTabSelection(MF2);
			break;
		case R.id.call_records:
			setTabSelection(MF3);
			break;
		case R.id.find:
			setTabSelection(MF4);
			break;
		case R.id.setts:
			setTabSelection(MF5);
			break;
		case R.id.mysetts_2layout:
			setTabSelection(MF5);
			break;
		case R.id.nearby_stores:
			setTabSelection(MF6);
			break;
		case R.id.tab_call_bt:// 打电话
			dial_fragment.CallPhonenew_AisinActivity();
			break;
		case R.id.tab_callbanck_bt:// 电话号码退格
			numclearnum += 1;
			if (numclearnum % 4 == 0) {
				mHandler.sendEmptyMessage(12);
			}
			dial_fragment.numbanck_AisinActivity(false);
			break;
		}
	}

	// 重新进入Activity时调用的事件
	@Override
	protected void onResume() {
		super.onResume();
		ActivityJump();
		// 检查用户是否登录 没有登录 退出程序
		final UserInfo userinfo = UserInfo_db.getUserInfo(AisinActivity.this);
		if (userinfo == null) {
			// 退到登录注册界面
			Intent intentld = new Intent(this,
					org.aisin.sipphone.LodingActivity.class);
			this.startActivity(intentld);
			outMode = false;
			finish();
			return;
		} else {
			if (!resulmbflag) {
				return;
			}
			resulmbflag = false;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					resulmbflag = true;
				}
			}, 10000);
			// 尝试启动服务
			// Intent intent = new Intent(AisinActivity.this,
			// org.aisin.sipphone.directcall.AisinService.class);
			// stopService(intent);
			// startService(intent);
			// if (!AisinService.isReady()) {
			// Intent intent = new Intent(AisinActivity.this,
			// org.aisin.sipphone.directcall.AisinService.class);
			// startService(intent);
			// }

			// 检测是否需要修改通话记录和通讯录
			dial_fragment.upYue();
			if (Thjl) {
				Thjl = false;
				mHandler.sendEmptyMessage(2);
			}
			if (Txl) {
				Txl = false;
				mHandler.sendEmptyMessage(3);
			}
			// 检测是否需要更新用户数据
			Checkup4Resume();
		}
	}

	// 实现Dial_barImageInterf接口，让dial回调的改变bar图标的接口
	@Override
	public void Changeimage(int Rimageid) {
		// dial 图片变更回调函数
		dialimageid = Rimageid;
		mHandler.sendEmptyMessage(1);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// TODO Auto-generated method stub
		// 取消通话记录和通讯录的监听
		this.getContentResolver().unregisterContentObserver(
				callrecordstreserver);
		this.getContentResolver().unregisterContentObserver(maillistreserver);
		// 注销红包提醒事 清除拨号盘号码物监听器
		if (redcedbd != null) {
			unregisterReceiver(redcedbd);
		}

		instance = null;
		stopAisinService();

		if (outMode) {
			// 退出虚拟机
			System.exit(0);
		} else {
			RecoveryTools.unbindDrawables(aisinactivity_relayout);// 回收容器
		}

	}

	// 实现Downloadcomplete接口，让图片下载完成回调的接口
	@Override
	public void down() {
		// 广告更新完毕回调 调用dial更新广告
		mHandler.sendEmptyMessage(4);
	}

	// 处理在resume中需要处理更新用户资料
	private synchronized void Checkup4Resume() {
		// 检查每日签到红包红包
		if (CheckUpadateTime.CheckResult_4REDDaily(context)) {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(11);
				}
			}, 2000);
		} else {
			// 当天是否联网更新成功过显示未拆有效红包数据
			if (CheckUpadateTime.CheckResult_4Ckeckredhaves(context)) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(8);
					}
				}, 2000);
			}
		}

		// 检查更新(msglist)
		if (CheckUpadateTime.CheckResult_4msglist_UP_time(AisinActivity.this)) {// 判断上次更新时间
			new HttpTask_CheckUpdate(AisinActivity.this, 0, null, null)
					.execute("CheckUpdate");
		}

		// 检测是否需要更新getadlist(广告)
		if (CheckUpadateTime.CheckResult_4getadlist_UP_time(AisinActivity.this)) {
			// 开启线程更新广告
			new Thread(new Runnable() {
				@Override
				public void run() {
					synchronized (AisinActivity.class) {
						// 下载图片的线程唯一
						DownloadImage.downling(AisinActivity.this,
								AisinActivity.this);
					}
				}
			}).start();
		}

		// 上传广告跟踪数据
		if (CheckUpadateTime
				.CheckResult_4AdvertisingEffectUpload(AisinActivity.this)) {
			// 上传广告效果
			new AdvertisingEffectUpload(AisinActivity.this).execute("");
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GetAction_reports.ADDARSNUM(this, null, null, -1, 0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 接收更新红包事物提醒的广播 清除拨号盘广播
	private class redrecedBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新红包提醒事物
			if (intent.getAction().equals(
					Constants.BrandName + ".redcedbd.upreddate.cannotcheck")) {
				mHandler.sendEmptyMessage(9);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".redcedbd.upreddate")) {
				if (!Check_network.isNetworkAvailable(AisinActivity.this)) {
					return;
				}
				mHandler.sendEmptyMessage(8);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".dial.clearnum")) {
				if (FlagMF == MF1) {
					mHandler.sendEmptyMessage(10);
				}
			} else if (intent.getAction().equals(
					Constants.BrandName + ".insert.callhistory")) {
				Thjl = true;
			} else if (intent.getAction().equals(
					Constants.BrandName + ".insert.callhistory.now")) {
				mHandler.sendEmptyMessage(2);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".find.upServerdata")) {
				mHandler.sendEmptyMessage(6);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".mailist.updata")) {
				Txl = true;
			} else if (intent.getAction().equals(
					Constants.BrandName + ".setTabSelection")) {
				int position = intent.getIntExtra("position", 1);

				setTabSelection(position);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".redlist")) {
				mHandler.sendEmptyMessage(7);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".keybackUP")) {
				mHandler.sendEmptyMessage(13);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".keyboardmusic")) {
				mHandler.sendEmptyMessage(14);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".network.no")) {
				mHandler.sendEmptyMessage(15);
			} else if (intent.getAction().equals(
					Constants.BrandName + ".network.yes")) {
				mHandler.sendEmptyMessage(16);
			}
		}
	}

	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final AisinActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("AisinActivity not instantiated yet");
	}

	private void stopAisinService() {
		if (AisinService.isReady()) {
			Intent intent = new Intent(AisinActivity.this,
					org.aisin.sipphone.directcall.AisinService.class);
			stopService(intent);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.tab_callbanck_bt) {
			dial_fragment.numbanck_AisinActivity(true);
		}
		return true;
	}

	@Override
	public void TabBarShow(boolean b) {
		// 显示隐藏拨号按钮
		if (b) {
			tabbar_lainelayout.setVisibility(View.VISIBLE);
		} else {
			tabbar_lainelayout.setVisibility(View.INVISIBLE);
		}
	}
}
