package org.aisin.sipphone.mai_list;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.dial.Dial_SherchListAdapter;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildCancelListener;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.setts.DynamicListView;
import org.aisin.sipphone.setts.DynamicListView.DynamicListViewListener;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.ContactLoadinteface;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.InitIsDoubleTelephone;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MailList_Fragment extends Fragment implements OnListnerShearch,
		ContactLoadinteface, OnTouchListener, OnClickListener,
		DynamicListViewListener {
	private LinearLayout maillistlainearlayout;
	private View mail_list_fragment;
	private ImageView mail_list_add;
	private TextView maillist_txlbt;// 显示通讯录好友
	private TextView maillist_hybt;// 显示环宇好友
	private int listviewshowflag = 0, TXL = 0, HY = 1;// 当前显示的好友类型
	private boolean upfriendflag = true;// 是否更新头像 好友资料
	private boolean actionflag = true;// 通讯录和环宇好友是否可点击
	private boolean v_NOFlag = true;// 是否切换环宇好友和搜索好友
	private TextView mail_listactivity_tishi;
	private SearchText searchtext;
	private ImageView cleartext;
	private DynamicListView mail_listactivity_list;
	private ListView mail_listactivity_list_shearch;
	private Dial_SherchListAdapter d_sherchlist;
	private MailList_Adapter mailadapter;
	private TextView mail_list_abcT;
	private boolean mail_list_abcT_flag = true;
	private MailList_abcList abclist;
	private int temtp = 0;
	private ArrayList<Contact> contacts = new ArrayList<Contact>();// 联系人集合
	private TreeMap<String, Integer> szm_map = new TreeMap<String, Integer>();// 联系人首字母出现位置
	private TreeSet<Contact> contacts_ss = new TreeSet<Contact>();// 搜索结果联系人结果集合
	private String[] jss = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "~" };
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 搜索变更 重载搜索结果
				String searchtext = msg.getData().getString("searchtext");
				if (searchtext == null) {
					searchtext = "";
				}
				SearchMaiList(searchtext);
			} else if (msg.what == 2) {
				// 载入联系人
				if (contacts.size() > 0) {
					// 更新集合
					if (listviewshowflag == HY) {
						// 在这里要先刷新是因为好友的头像在这之前已经被清理了
						if (mailadapter == null) {
							mailadapter = new MailList_Adapter(
									AisinActivity.context, contacts, szm_map);
							mail_listactivity_list.setAdapter(mailadapter);
						} else {
							mailadapter.notifyDataSetChanged();
						}
					}
					// 读取在集合中 第几个位置 联系人首字母改变了(处理这个比较耗时所以要先更新listview 等处理完了再次更新)
					TreeMap<String, Integer> szm_map_temp = new TreeMap<String, Integer>();
					for (int i = 0; i < contacts.size(); i++) {
						if (szm_map_temp.get(contacts.get(i).getF_PY()) == null) {
							szm_map_temp.put(contacts.get(i).getF_PY(), i);
						}
					}
					// 对缺省首字符做定位
					for (int i = 0; i < jss.length; i++) {
						if (i == 0 && szm_map_temp.get(jss[i]) == null) {// #第一个字符如果没有的话要做特殊处理
							szm_map_temp.put(jss[i], 0);
						} else {
							if (szm_map_temp.get(jss[i]) == null) {
								szm_map_temp.put(jss[i],
										szm_map_temp.get(jss[i - 1]));
							}
						}
					}
					szm_map.clear();
					szm_map.putAll(szm_map_temp);
					szm_map_temp.clear();
					szm_map_temp = null;

					// 设置中间提提示加载中的文本框不可见
					mail_listactivity_tishi.setText("");
					mail_listactivity_tishi.setVisibility(View.INVISIBLE);
					// 更新集合
					if (mailadapter == null) {
						mailadapter = new MailList_Adapter(
								AisinActivity.context, contacts, szm_map);
						mail_listactivity_list.setAdapter(mailadapter);
					} else {
						mailadapter.notifyDataSetChanged();
					}
					abclist.setVisibility(View.VISIBLE);
					mail_listactivity_list.setVisibility(View.VISIBLE);
					if (v_NOFlag) {
						v_NOFlag = false;
						mail_listactivity_list_shearch.setVisibility(View.GONE);
						cleartext.setVisibility(View.INVISIBLE);
					}
				} else {
					if (listviewshowflag == TXL) {
						mail_listactivity_tishi
								.setText("联系人为空！或者没有访问联系人的权限！点我查看开启权限方法!");
					} else if (listviewshowflag == HY) {
						mail_listactivity_tishi.setText("暂无环宇好友,点击添加按钮添加环宇好友!");
					}
					mail_listactivity_list.setVisibility(View.INVISIBLE);
					mail_listactivity_tishi.setVisibility(View.VISIBLE);
				}
				String hinttext = "";
				if (listviewshowflag == HY) {
					hinttext = "可搜索" + contacts.size() + "个环宇好友";
				} else if (listviewshowflag == TXL) {
					hinttext = "可搜索" + contacts.size() + "个联系人";
				}
				searchtext.setHint(hinttext);
			} else if (msg.what == 3) {
				// 侧边的ABCD导航栏点击后 中间的提示框0.5秒后隐藏
				mail_list_abcT.setVisibility(View.INVISIBLE);
				mail_list_abcT_flag = true;// 打开开关
			} else if (msg.what == 4) {// 重载通讯录
				// 开启线程重新强制初始化联系人数据
				new Thread(new Runnable() {
					@Override
					public void run() {
						CursorTools.loadContacts_2(AisinActivity.context,
								MailList_Fragment.this, true);
					}
				}).start();
			} else if (msg.what == 5) {
				temtp += 1;
				int tmp = maillistlainearlayout.getScrollY() + temtp;
				if (tmp >= 90) {
					tmp = 90;
					temtp = 0;
				}
				maillistlainearlayout.scrollTo(0, tmp);
			} else if (msg.what == 6) {
				temtp += 1;
				int tmp = maillistlainearlayout.getScrollY() - temtp;
				if (tmp <= 0) {
					tmp = 0;
					temtp = 0;
				}
				maillistlainearlayout.scrollTo(0, tmp);
			} else if (msg.what == 7) {
				actionflag = true;
				mail_listactivity_list.doneRefresh();
			} else if (msg.what == 8) {
				mail_listactivity_list.doneRefresh();
			} else if (msg.what == 9) {
				goCT_checkFriends_Dialog();
			}
		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		mail_list_fragment = inflater.inflate(R.layout.mail_listactivity,
				container, false);
		maillistlainearlayout = (LinearLayout) mail_list_fragment
				.findViewById(R.id.maillistlainearlayout);
		maillist_txlbt = (TextView) mail_list_fragment
				.findViewById(R.id.maillist_txlbt);
		maillist_hybt = (TextView) mail_list_fragment
				.findViewById(R.id.maillist_hybt);
		mail_list_add = (ImageView) mail_list_fragment
				.findViewById(R.id.mail_list_add);
		mail_listactivity_tishi = (TextView) mail_list_fragment
				.findViewById(R.id.mail_listactivity_tishi);
		searchtext = (SearchText) mail_list_fragment
				.findViewById(R.id.searchtext);
		cleartext = (ImageView) mail_list_fragment.findViewById(R.id.cleartext);
		mail_listactivity_list = (DynamicListView) mail_list_fragment
				.findViewById(R.id.mail_listactivity_list);
		mail_listactivity_list_shearch = (ListView) mail_list_fragment
				.findViewById(R.id.mail_listactivity_list_shearch);
		abclist = (MailList_abcList) mail_list_fragment
				.findViewById(R.id.mlist_abclist);
		mail_list_abcT = (TextView) mail_list_fragment
				.findViewById(R.id.mail_list_abcT);
		mail_listactivity_list.setOnRefreshListener(this);
		maillist_txlbt.setOnClickListener(this);
		maillist_hybt.setOnClickListener(this);
		mail_list_add.setOnClickListener(this);
		mail_listactivity_tishi.setOnClickListener(this);
		searchtext.setShearchListner(this);
		cleartext.setOnClickListener(this);
		abclist.setOnTouchListener(this);

		// 开启线程初始化联系人数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				actionflag = false;
				CursorTools.loadContacts_2(AisinActivity.context,
						MailList_Fragment.this, false);
			}
		}).start();
		return mail_list_fragment;
	}

	private synchronized void SearchMaiList(String searchtext) {
		if ("".equals(searchtext)) {
			// 为空 清空搜索结果集合 侧边ABCD导航栏显示 发送显示联系人列表的通知
			contacts_ss.clear();
			if (d_sherchlist == null) {
				d_sherchlist = new Dial_SherchListAdapter(
						AisinActivity.context, contacts_ss,
						Dial_SherchListAdapter.MLIST);
				mail_listactivity_list_shearch.setAdapter(d_sherchlist);
			} else {
				d_sherchlist.notifyDataSetChanged();
			}
			v_NOFlag = true;
			mHandler.sendEmptyMessage(2);
			// 让整体下来
			goDown();
			return;
		}
		// 让整体上去
		goUP();
		// 搜索联系人
		contacts_ss.clear();
		for (Contact ctt : contacts) {
			TreeSet<String> arrlist_ss = ctt.getSearchlist4name();
			for (String str : arrlist_ss) {
				if (str.indexOf(searchtext) >= 0) {// str是否包含搜索数据
					if (Check_format.check_ABC(searchtext)) {// 如果是字母检索 做拼音匹配
						for (char car : ctt.getSpy().toCharArray()) {// 如果不是以任何一个名字的首字母开始的，就跳过
							if (searchtext.startsWith(car + "")) {
								ctt.setMatchstr_mailist(searchtext);
								contacts_ss.add(ctt);
								break;
							}
						}
					} else {
						ctt.setMatchstr_mailist(searchtext);
						contacts_ss.add(ctt);
						break;
					}
				}
			}
		}
		if (d_sherchlist == null) {
			d_sherchlist = new Dial_SherchListAdapter(AisinActivity.context,
					contacts_ss, Dial_SherchListAdapter.MLIST);
			mail_listactivity_list_shearch.setAdapter(d_sherchlist);
		} else {
			d_sherchlist.notifyDataSetChanged();
		}
		mail_listactivity_list_shearch.setVisibility(View.VISIBLE);
		abclist.setVisibility(View.INVISIBLE);
		mail_listactivity_list.setVisibility(View.GONE);
		cleartext.setVisibility(View.VISIBLE);
	}

	// 输入框变更触发的搜索事件
	@Override
	public void Search(String text) {
		if (contacts.isEmpty()) {
			return;
		}
		Bundle bdl = new Bundle();
		bdl.putString("searchtext", text);
		Message ms = new Message();
		ms.setData(bdl);
		ms.what = 1;
		mHandler.sendMessage(ms);
	}

	// 侧边ABC导航栏点击响应事件
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (contacts.isEmpty()) {
			return true;
		}
		if (event.getMetaState() == MotionEvent.ACTION_DOWN) {
			float one_vy = (float) v.getHeight() / 27f;
			float ey = event.getY();
			int key = (int) (ey / one_vy);
			if (key < 0 || key > 26) {
				return true;
			}
			String key_value = jss[key];

			// 开启显示提示框
			if ("~".equals(key_value)) {
				mail_list_abcT.setText("#");
			} else {
				mail_list_abcT.setText(key_value.toUpperCase());
			}
			mail_list_abcT.setVisibility(View.VISIBLE);
			if (mail_list_abcT_flag) {
				mail_list_abcT_flag = false;// 关掉开关，避免重复创建线程调用
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(3);
					}
				}, 500);
			}

			int item_num = szm_map.get(key_value);
			mail_listactivity_list.setSelection(item_num);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.mail_list_add) {
			if (listviewshowflag == TXL) {
				// 添加联系人
				Intent intent = new Intent(Intent.ACTION_INSERT);
				intent.setType("vnd.android.cursor.dir/person");
				intent.setType("vnd.android.cursor.dir/contact");
				intent.setType("vnd.android.cursor.dir/raw_contact");
				startActivity(intent);
			} else {
				// 添加环宇好友
				Intent intent = new Intent(AisinActivity.context,
						org.aisin.sipphone.mai_list.AddAisinFriend.class);
				startActivity(intent);
			}
		} else if (v.getId() == R.id.maillist_txlbt) {
			if (!actionflag || listviewshowflag == TXL) {
				return;
			}
			actionflag = false;
			listviewshowflag = TXL;
			maillist_txlbt.setTextColor(Color.parseColor("#FFFFFF"));
			maillist_txlbt
					.setBackgroundResource(R.drawable.maillisttitlebanck_txl_pres);
			maillist_hybt.setTextColor(Color.parseColor("#1160FD"));
			maillist_hybt
					.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_dis);
			new Thread(new Runnable() {
				@Override
				public void run() {
					CursorTools.loadContacts_2(AisinActivity.context,
							MailList_Fragment.this, false);
				}
			}).start();
		} else if (v.getId() == R.id.maillist_hybt) {
			if (!actionflag || listviewshowflag == HY) {
				return;
			}
			actionflag = false;
			listviewshowflag = HY;
			maillist_txlbt.setTextColor(Color.parseColor("#1160FD"));
			maillist_txlbt
					.setBackgroundResource(R.drawable.maillisttitlebanck_txl_dis);
			maillist_hybt.setTextColor(Color.parseColor("#FFFFFF"));
			maillist_hybt
					.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_pres);
			contacts.clear();
			contacts.addAll(CursorTools.friendslist);
			mHandler.sendEmptyMessage(2);
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 处理头像
					CursorTools.checkupfriends(AisinActivity.context,
							MailList_Fragment.this);
				}
			}).start();
		} else if (v.getId() == R.id.cleartext) {
			searchtext.setText("");
		} else if (v.getId() == R.id.mail_listactivity_tishi) {
			// 开启权限教程
			if (listviewshowflag == TXL) {
				startActivity(new Intent(AisinActivity.context,
						org.aisin.sipphone.setts.TutorialActivity.class));
			}
		}
	}

	@Override
	public void DoadDown_map(TreeMap<Long, Contact> cttmap) {// 非修改UI不用hander调用
		AisinActivity.context.sendBroadcast(new Intent(Constants.BrandName
				+ ".insert.callhistory.now"));
		if (listviewshowflag == TXL) {
			TreeSet<Contact> settemp = new TreeSet<Contact>();// 排序集合
			for (Entry<Long, Contact> et : cttmap.entrySet()) {
				settemp.add(et.getValue());
			}
			contacts.clear();
			contacts.addAll(settemp);
			settemp.clear();
			settemp = null;
			mHandler.sendEmptyMessage(2);
			if (!upfriendflag) {
				actionflag = true;
			}
		}
		// 加载联系人的头像
		if (upfriendflag) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					// 第一次加载或者主动刷新的时候才做匹配 匹配头像
					CursorTools.LoadingPicture(AisinActivity.context,
							MailList_Fragment.this);
				}
			}).start();
		}
	}

	@Override
	public void headimagedown(boolean upflag) {
		if (listviewshowflag == TXL) {
			if (upflag) {
				mHandler.sendEmptyMessage(2);// 头像加载完毕
			}
		}
		// 匹配通讯录环宇好友
		// 第一次加载的时候才做匹配
		if (upfriendflag) {
			upfriendflag = false;
			mHandler.sendEmptyMessage(9);
		}
	}

	private void goCT_checkFriends_Dialog() {
		// 0 没有手动设置过 1 手动开启 2 手动关闭
		final SharedPreferences spfus = SharedPreferencesTools
				.getSharedPreferences_ALLSWITCH(AisinActivity.context);
		int UPC_sNUM = spfus
				.getInt(SharedPreferencesTools.UPContant2Service, 1);
		if (UPC_sNUM == 0) {
			AisinBuildDialog mybuild = new AisinBuildDialog(
					AisinActivity.context);
			mybuild.setTitle("提示");
			mybuild.setMessage("是否同步本地通讯录环宇好友?");
			mybuild.setOnDialogCancelListener("取消",
					new DialogBuildCancelListener() {
						@Override
						public void dialogCancel() {
							spfus.edit()
									.putInt(SharedPreferencesTools.UPContant2Service,
											2).commit();
							goCT_checkFriends(false);
						}
					});
			mybuild.setOnDialogConfirmListener("确定",
					new DialogBuildConfirmListener() {
						@Override
						public void dialogConfirm() {
							spfus.edit()
									.putInt(SharedPreferencesTools.UPContant2Service,
											1).commit();
							goCT_checkFriends(true);
						}
					});
			mybuild.dialogShow();
		} else if (UPC_sNUM == 1) {
			goCT_checkFriends(true);
		} else if (UPC_sNUM == 2) {
			goCT_checkFriends(false);
		}
	}

	private void goCT_checkFriends(final boolean b) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (MailList_Fragment.class) {
					CursorTools.checkFriends(AisinActivity.context,
							MailList_Fragment.this, b);
				}
			}
		}).start();
	}

	@Override
	public void upfrendsdwon(boolean upflag) {
		AisinActivity.context.sendBroadcast(new Intent(Constants.BrandName
				+ ".insert.callhistory.now"));
		// 服务器环宇好友匹配完毕
		if (listviewshowflag == TXL) {
			if (upflag) {
				mHandler.sendEmptyMessage(2);// 服务器好友匹配完毕
			}
		}
		if (listviewshowflag == HY) {
			contacts.clear();
			contacts.addAll(CursorTools.friendslist);
			mHandler.sendEmptyMessage(2);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 处理环宇好友头像
				CursorTools.checkupfriends(AisinActivity.context,
						MailList_Fragment.this);
			}
		}).start();
	}

	// 联系人强制重载
	public void ReSetMailListview() {
		upfriendflag = true;
		actionflag = false;
		mHandler.sendEmptyMessage(4);
	}

	@Override
	public void showfriends(boolean upflag) {
		mHandler.sendEmptyMessage(7);
		if (listviewshowflag == HY) {
			if (upflag) {
				AisinActivity.context.sendBroadcast(new Intent(
						Constants.BrandName + ".insert.callhistory.now"));
				mHandler.sendEmptyMessage(2);
			}
		}
	}

	// 把整体移上去
	private void goUP() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (maillistlainearlayout.getScrollY() < 90) {
					try {
						Thread.sleep(10);
						mHandler.sendEmptyMessage(5);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	private void goDown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (maillistlainearlayout.getScrollY() > 0) {
					try {
						Thread.sleep(10);
						mHandler.sendEmptyMessage(6);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView,
			boolean isRefresh) {
		if (isRefresh) {
			if (!actionflag) {
				mHandler.sendEmptyMessage(8);
				return false;
			}
			// 下拉刷新数据;
			SharedPreferencesTools
					.getSharedPreferences_4upfriends(AisinActivity.context)
					.edit()
					.putString(SharedPreferencesTools.upfrendsuptime, "0000")
					.commit();
			upfriendflag = true;
			actionflag = false;
			mHandler.sendEmptyMessage(4);
		}
		return false;
	}
}
