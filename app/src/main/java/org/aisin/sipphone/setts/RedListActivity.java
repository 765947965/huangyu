package org.aisin.sipphone.setts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.setts.DynamicListView.DynamicListViewListener;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class RedListActivity extends Activity implements OnClickListener,
		DynamicListViewListener, OnItemClickListener {
	private View hearderViewLayout;// 红包列表头

	private LinearLayout redlistlayout_linelayout;
	private ImageView redlistviewlose_i;
	private ImageView redlistviewr_i;

	// 列表头控件
	private LinearLayout redlisttop_relayout;
	private DynamicListView myred_listview;// 红包列表
	private TextView redslat_yearchange;// 年份切换
	private LinearLayout redslat_yearchange_layout;
	private TextView sendorrecevedtext;// 发出或者收到文字
	private TextView myred_allmoney;// 红包总金额
	private TextView myred_allmoneynum;// 红包个数
	private TextView myred_changeredtype;// 切换红包类型
	private ProgressDialog prd;
	private int year;
	private String checkyear;// 当前选中的年份
	private String[] cities;// 存储年份
	private String direct;

	private SimpleDateFormat sdformat_3 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdformat_5 = new SimpleDateFormat("yyyyMMdd");
	private int datestr_today;

	private int money = 0;// 单位分
	private TreeMap<String, RedObject> redobs = new TreeMap<String, RedObject>();
	private ArrayList<RedObject> redobs_showlist = new ArrayList<RedObject>();
	private ArrayList<String> gitfids = new ArrayList<String>();// 用于存储发生了改变的红包的giftid
	private Red_listviewAdapter adapter;
	private final int ALL = 0, NOTCHECKOUT = 1, SENDTOUTRED = 2;// 收集红包列表种类
	private int Flag = 0;// 当前要收集的种类

	private myuplistviewbroad mlbd;// 刷新广播接收者

	private PopupWindow popupWindow;

	private Animation anim;

	private boolean toastflag = true;// listview拉到最后是否显示没有更多记录

	private boolean outupdata = false;// 记录退出的时候是否要更新本地红包数据

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 给redobs_showlist赋值
				redobs_showlist.clear();
				TreeSet<RedObject> tempset = new TreeSet<RedObject>();
				if (Flag == ALL) {
					for (Entry<String, RedObject> ent : redobs.entrySet()) {
						if ("received".equals(ent.getValue().getDirect())) {
							tempset.add(ent.getValue());
						}
					}
				} else if (Flag == NOTCHECKOUT) {
					for (Entry<String, RedObject> ent : redobs.entrySet()) {
						if ("received".equals(ent.getValue().getDirect())
								&& ent.getValue().getHas_open() == 0) {
							tempset.add(ent.getValue());
						}
					}
				} else if (Flag == SENDTOUTRED) {
					for (Entry<String, RedObject> ent : redobs.entrySet()) {
						if ("sended".equals(ent.getValue().getDirect())) {
							tempset.add(ent.getValue());
						}
					}
				}
				redobs_showlist.addAll(tempset);
				tempset.clear();
				tempset = null;
				// 加载listview
				if (adapter == null) {
					adapter = new Red_listviewAdapter(RedListActivity.this,
							redobs_showlist);
					myred_listview.setAdapter(adapter);
				} else {
					// 网络更新后的刷新 必须重置adapter中listnums的值
					if (redobs_showlist.size() > 10) {
						adapter.setListnums(10);
					} else {
						adapter.setListnums(redobs_showlist.size());
					}
					adapter.notifyDataSetChanged();
				}
				// 计算红包总数和总金额
				myred_allmoneynum.setText(redobs_showlist.size() + "个");
				money = 0;
				if (Flag != NOTCHECKOUT) {
					for (RedObject rdb : redobs_showlist) {
						String type = rdb.getType() != null ? rdb.getType()
								: "";
						if (rdb.getMoney() != null
								&& ("logindaily".equals(type) || type
										.endsWith("_money"))) {
							// 只添加已经拆了的
							if (Flag == ALL && rdb.getHas_open() == 1) {
								try {
									int tenp = Integer.parseInt(rdb.getMoney()
											.trim());
									money += tenp;
								} catch (Exception e) {
								}
							} else if (Flag == SENDTOUTRED) {
								try {
									int tenp = Integer.parseInt(rdb.getMoney()
											.trim());
									money += tenp;
								} catch (Exception e) {
								}
							}
						}
					}
					myred_allmoney.setText((double) money / (double) 100 + "元");
				} else {
					myred_allmoney.setText("未知");
				}
				if (prd != null) {
					prd.dismiss();
				}
				toastflag = true;
				// redlistviewr_i.clearAnimation();
			} else if (msg.what == 2) {
				prd.setMessage("正在刷新，请稍候...");
				prd.show();
				new HttpTask_Red_getdata().execute("lodingreddata");
			} else if (msg.what == 3) {
				if (adapter == null || redobs_showlist == null) {
					if (myred_listview != null) {
						myred_listview.doneMore();
					}
					return;
				}
				if (adapter.getListnums() == redobs_showlist.size()) {
					if (toastflag) {
						toastflag = false;
						Toast.makeText(RedListActivity.this, "没有更多记录!",
								Toast.LENGTH_SHORT).show();
					}
					myred_listview.doneMore();
					return;
				}
				if (adapter.getListnums() + 10 > redobs_showlist.size()) {
					adapter.setListnums(redobs_showlist.size());
				} else {
					adapter.setListnums(adapter.getListnums() + 10);
				}
				adapter.notifyDataSetChanged();
				myred_listview.doneMore();
			} else if (msg.what == 4) {
				new AisinBuildDialog(RedListActivity.this, "提示", "暂无红包数据!");
			} else if (msg.what == 5) {// 单纯更新本地数据
				prd.setMessage("正在加载，请稍候...");
				prd.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							ArrayList<RedObject> redobjects = RedData_DBHelp
									.getAllReds(RedListActivity.this, year + "");
							if (redobjects != null) {
								redobs.clear();
								Iterator<RedObject> iteratorreds = redobjects
										.iterator();
								while (iteratorreds.hasNext()) {
									RedObject rb = iteratorreds.next();
									redobs.put(rb.getGift_id(), rb);
								}
								redobjects.clear();
								redobjects = null;
								// 通知更新红包列表
								mHandler.sendEmptyMessage(1);
							} else {
								mHandler.sendEmptyMessage(6);
							}
						} catch (Exception e) {
						}
					}
				}).start();
			} else if (msg.what == 6) {
				new HttpTask_Red_getdata().execute("lodingreddata");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.redlistlayout);
		redlistlayout_linelayout = (LinearLayout) this
				.findViewById(R.id.redlistlayout_linelayout);
		redlistviewlose_i = (ImageView) this
				.findViewById(R.id.redlistviewlose_i);
		redlistviewlose_i.setOnClickListener(this);
		redlistviewr_i = (ImageView) this.findViewById(R.id.redlistviewr_i);
		redlistviewr_i.setOnClickListener(this);
		anim = AnimationUtils.loadAnimation(this, R.anim.upanim);
		myred_listview = (DynamicListView) this
				.findViewById(R.id.myred_listview);
		myred_listview.setDoMoreWhenBottom(true); // 滚动到低端的时候自动加载更多
		// myred_listview.setOnRefreshListener(this); // 刷新监听
		myred_listview.setOnMoreListener(this);// 下拉更多监听

		// 初始化红包列表头
		hearderViewLayout = LayoutInflater.from(RedListActivity.this).inflate(
				R.layout.redlistlayoutadapteritem_header, null);
		redlisttop_relayout = (LinearLayout) hearderViewLayout
				.findViewById(R.id.redlisttop_relayout);
		if (PhoneInfo.height != 0) {
			LayoutParams lptemp = redlisttop_relayout.getLayoutParams();
			lptemp.height = (int) (PhoneInfo.height * 0.35);
			redlisttop_relayout.setLayoutParams(lptemp);
		}
		redslat_yearchange_layout = (LinearLayout) hearderViewLayout
				.findViewById(R.id.redslat_yearchange_layout);
		redslat_yearchange = (TextView) hearderViewLayout
				.findViewById(R.id.redslat_yearchange);
		sendorrecevedtext = (TextView) hearderViewLayout
				.findViewById(R.id.sendorrecevedtext);
		myred_allmoney = (TextView) hearderViewLayout
				.findViewById(R.id.myred_allmoney);
		myred_allmoneynum = (TextView) hearderViewLayout
				.findViewById(R.id.myred_allmoneynum);
		myred_changeredtype = (TextView) hearderViewLayout
				.findViewById(R.id.myred_changeredtype);
		redslat_yearchange_layout.setOnClickListener(this);
		myred_changeredtype.setOnClickListener(this);

		// listview加载列表头
		if (PhoneInfo.SDKVersion > 9) {
			// 去掉拉到顶的障碍效果
			myred_listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		myred_listview.addHeaderView(hearderViewLayout);
		myred_listview.setOnItemClickListener(this);

		// 初始化pupwindow
		View popView = LayoutInflater.from(RedListActivity.this).inflate(
				R.layout.red_popupwindow, null);
		popupWindow = new PopupWindow(popView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ColorDrawable dw = new ColorDrawable(-00000);
		popupWindow.setBackgroundDrawable(dw);
		TextView ppwindow_redall = (TextView) popView
				.findViewById(R.id.ppwindow_redall);
		TextView ppwindow_rednot = (TextView) popView
				.findViewById(R.id.ppwindow_rednot);
		TextView sendoutred_bt = (TextView) popView
				.findViewById(R.id.sendoutred_bt);
		ppwindow_redall.setOnClickListener(this);
		ppwindow_rednot.setOnClickListener(this);
		sendoutred_bt.setOnClickListener(this);

		// 加载红包数据
		prd = new ProgressDialog(RedListActivity.this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 加载红包数据
		datestr_today = Integer.parseInt(sdformat_5.format(new Date()));
		SimpleDateFormat sfd = new SimpleDateFormat("yyyy");
		year = Integer.parseInt(sfd.format(new Date()));
		if (year < 2015) {
			Toast.makeText(this, "本机时间不正确,请先调整本机时间!", Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		checkyear = year + "";
		direct = "received";
		myred_changeredtype.setText("收到的红包");
		redslat_yearchange.setText(checkyear);
		if ("sended".equals(this.getIntent().getStringExtra("direct"))) {
			myred_changeredtype.setText("发出的红包");
			sendorrecevedtext.setText("已发出");
			Flag = SENDTOUTRED;
			direct = "sended";
		}

		// 年份dialog数组
		cities = new String[year - 2015 + 1];
		for (int yr = year; yr >= 2015; yr--) {
			cities[year - yr] = yr + "";
		}

		// 注册刷新广播监听
		mlbd = new myuplistviewbroad();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".redlisttoup");
		registerReceiver(mlbd, filter);

		mHandler.sendEmptyMessage(5);
	}

	private void savereddata() {
		// 更新本地数据
		for (String gitfid : gitfids) {
			RedObject red = redobs.get(gitfid);
			if (red != null) {
				RedData_DBHelp.addRedDatas(RedListActivity.this, red);
			}
		}
	}

	private class HttpTask_Red_getdata extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 获取得到红包数据URL
			String url = URLTools.GetHttpURL_4Red_GETDATA_Url(
					RedListActivity.this, checkyear, direct);
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
			switch (doresult) {
			case 0:
				// 保存刷新后的数据
				SharedPreferencesTools
						.getSharedPreferences_4RED_COUT(RedListActivity.this)
						.edit()
						.putString(
								checkyear + SharedPreferencesTools.REDDATA_key,
								result).commit();
				final JSONObject jsonf = json;
				// 处理JSON数据是一个耗时的操作
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							JSONArray gifts = jsonf.getJSONArray("gifts");
							redobs.clear();
							ArrayList<RedObject> arrays = new ArrayList<RedObject>();
							for (int i = 0; i < gifts.length(); i++) {
								JSONObject jsz = gifts.getJSONObject(i);
								RedObject redobject = new RedObject();
								redobject.setSplitsnumber(jsz
										.optString("splitsnumber"));
								redobject.setShake_ratio(jsz
										.optString("shake_ratio"));
								redobject.setReceived_money(jsz.optString(
										"received_money", "0"));
								redobject.setReturned_money(jsz.optString(
										"returned_money", "0"));
								redobject.setStatus(jsz.optString("status"));
								redobject.setSub_type(jsz.optString("sub_type"));
								redobject.setCommand(jsz.optString("command"));
								redobject.setFrom(jsz.optString("from"));
								redobject.setOpen_time(jsz
										.optString("open_time"));
								redobject.setGift_id(jsz.optString("gift_id"));
								redobject.setMoney(jsz.optString("money", "0"));
								String has_open_str = jsz.optString("has_open");
								if (has_open_str != null
										&& !"".equals(has_open_str)) {
									redobject.setHas_open(Integer
											.parseInt(has_open_str.trim()));
								} else {
									redobject.setHas_open(0);
								}
								redobject.setDirect(jsz.optString("direct"));
								redobject.setCreate_time(jsz
										.optString("create_time"));
								redobject.setFrom_phone(jsz
										.optString("from_phone"));
								redobject.setFromnickname(jsz
										.optString("fromnickname"));
								redobject.setMoney_type(jsz
										.optString("money_type"));
								redobject.setTips(jsz.optString("tips"));
								redobject.setExp_time(jsz.optString("exp_time"));
								redobject.setType(jsz.optString("type"));
								redobject.setSender_gift_id(jsz
										.optString("sender_gift_id"));
								redobject.setName(jsz.optString("name"));
								arrays.add(redobject);
								redobs.put(jsz.optString("gift_id"), redobject);
							}
							// 通知更新红包列表
							mHandler.sendEmptyMessage(1);
							// 加入本地数据库
							RedData_DBHelp.addMoreRedDatas(
									RedListActivity.this, arrays);

						} catch (Exception e) {
							if (prd != null) {
								prd.dismiss();
							}
							mHandler.sendEmptyMessage(4);
						}
					}
				}).start();
				return;
			case 6:
				new AisinBuildDialog(RedListActivity.this, "提示", "sign错误!");
				break;
			default:
				new AisinBuildDialog(RedListActivity.this, "提示",
						"联网失败,请检查网络连接!");
				break;
			}
			if (prd != null) {
				prd.dismiss();
			}
		}
	}

	class myuplistviewbroad extends BroadcastReceiver {// 用户接收广播 刷新红包数据列表

		@Override
		public void onReceive(Context context, Intent intent) {
			String gift_id = intent.getStringExtra("gift_id");
			String money = intent.getStringExtra("money");
			String open_time = intent.getStringExtra("open_time");
			if (gift_id != null && !"".equals(gift_id) && money != null
					&& !"".equals(money) && open_time != null
					&& !"".equals(open_time)) {
				RedObject robt = redobs.get(gift_id);
				if (robt != null) {
					robt.setHas_open(1);
					robt.setMoney(money);
					robt.setOpen_time(open_time);
					outupdata = true;
					gitfids.add(gift_id);
					mHandler.sendEmptyMessage(1);
				}
			} else {
				// 单纯的刷新(用于接收本地红包数据改变后的刷新)
				mHandler.sendEmptyMessage(5);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			savedata();
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		RedListActivity.this.sendBroadcast(new Intent(Constants.BrandName
				+ ".redlist"));

		// 注销注册的广播
		if (mlbd != null) {
			unregisterReceiver(mlbd);
		}
		if (redobs != null) {
			redobs.clear();
			redobs = null;
		}
		if (redobs_showlist != null) {
			redobs_showlist.clear();
			redobs_showlist = null;
		}
		if (redlistlayout_linelayout != null) {
			RecoveryTools.unbindDrawables(redlistlayout_linelayout);// 回收容器
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.redlistviewlose_i:
			savedata();
			break;
		case R.id.redlistviewr_i:
			// 播放动画
			// redlistviewr_i.startAnimation(anim);
			mHandler.sendEmptyMessage(2);
			break;
		case R.id.myred_changeredtype:
			showPop(v);
			break;
		case R.id.ppwindow_redall:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			if (Flag == ALL) {
				break;
			}
			if (outupdata) {
				outupdata = false;
				savereddata();
			}
			myred_changeredtype.setText("收到的红包");
			sendorrecevedtext.setText("已收到");
			Flag = ALL;
			direct = "received";
			mHandler.sendEmptyMessage(5);
			break;

		case R.id.ppwindow_rednot:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			if (Flag == NOTCHECKOUT) {
				break;
			}
			if (outupdata) {
				outupdata = false;
				savereddata();
			}
			myred_changeredtype.setText("未拆红包");
			sendorrecevedtext.setText("已收到");
			Flag = NOTCHECKOUT;
			direct = "received";
			mHandler.sendEmptyMessage(5);
			break;
		case R.id.sendoutred_bt:
			if (popupWindow != null) {
				popupWindow.dismiss();
			}
			if (Flag == SENDTOUTRED) {
				break;
			}
			if (outupdata) {
				outupdata = false;
				savereddata();
			}
			myred_changeredtype.setText("发出的红包");
			sendorrecevedtext.setText("已发出");
			Flag = SENDTOUTRED;
			direct = "sended";
			mHandler.sendEmptyMessage(5);
			break;
		case R.id.redslat_yearchange_layout:
			// 弹出年份切换框
			showyearchange();
			break;
		}
	}

	private void showyearchange() {
		if (cities == null || cities.length == 0) {
			return;
		}
		AisinBuildDialog mybuild = new AisinBuildDialog(RedListActivity.this);
		mybuild.setTitle("请选择年份");
		mybuild.setListViewItem(cities, new onMyItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (!checkyear.equals(cities[position])) {
					// 先保存数据(如果需要)
					if (outupdata) {
						outupdata = false;
						savereddata();
					}

					checkyear = cities[position];
					redslat_yearchange.setText(cities[position]);
					mHandler.sendEmptyMessage(5);
				}
			}
		});
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();
	}

	private void savedata() {
		if (outupdata) {
			outupdata = false;
			savereddata();
		}
		RedListActivity.this.finish();
	}

	/**
	 * 显示popWindow
	 * */
	public void showPop(View parent) {
		if (popupWindow == null) {
			return;
		}
		// 设置popwindow显示位置
		popupWindow.showAsDropDown(parent);

		// 获取popwindow焦点 popupWindow.setFocusable(true); //
		// 设置popwindow如果点击外面区域，便关闭。
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();

	}

	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView,
			boolean isRefresh) {
		if (isRefresh) {
			// 下拉刷新数据;
		} else {
			// 底部下拉加载更多列表项
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(500);// 制造延迟效果
						mHandler.sendEmptyMessage(3);
					} catch (InterruptedException e) {
					}
				}
			}).start();
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position < 2) {
			return;
		}
		RedObject robct = redobs_showlist.get(position - 2);
		if ("received".equals(robct.getDirect())) {
			if (robct.getHas_open() == 1) {
				// 进详情
				// 启动拆红包成功展示Activity
				Intent intent = new Intent(RedListActivity.this,
						org.aisin.sipphone.RedDetailsActivity.class);
				intent.putExtra("fromnickname", robct.getFromnickname());
				intent.putExtra("from", robct.getFrom());
				intent.putExtra("name", robct.getName());
				intent.putExtra("tips", robct.getTips());
				intent.putExtra("gift_type", robct.getType());
				intent.putExtra("award_money", robct.getMoney());
				String fee_rate = SharedPreferencesTools
						.getSharedPreferences_msglist_date_share(
								RedListActivity.this)
						.getString(
								SharedPreferencesTools.SPF_msglist_date_FEERATE,
								"0.39");
				Double d = Double.parseDouble(fee_rate.trim());
				fee_rate = (int) (d * 100) + "";
				intent.putExtra("fee_rate", fee_rate);
				if (robct.getType().startsWith("p2p")) {
					intent.putExtra("sub_type", robct.getSub_type());
					intent.putExtra("money_type", robct.getMoney_type());
					intent.putExtra("sender_gift_id", robct.getSender_gift_id());
				}
				RedListActivity.this.startActivity(intent);
			} else if (robct.getHas_open() == 0) {
				// 拆红包
				Intent intent = new Intent(RedListActivity.this,
						org.aisin.sipphone.RedDialog.class);
				intent.putExtra("fromnickname", robct.getFromnickname());
				intent.putExtra("from", robct.getFrom());
				intent.putExtra("gift_id", robct.getGift_id());
				intent.putExtra("name", robct.getName());
				intent.putExtra("tips", robct.getTips());
				intent.putExtra("gift_type", robct.getType());
				RedListActivity.this.startActivity(intent);
				// 开启动画
				RedListActivity.this.overridePendingTransition(R.anim.anim_4,
						R.anim.anim_3);
			}

		} else {
			Intent intent = new Intent(RedListActivity.this,
					org.aisin.sipphone.setts.RedDetailsActivity_Send.class);
			intent.putExtra("robct", robct);
			RedListActivity.this.startActivity(intent);
		}
	}

}
