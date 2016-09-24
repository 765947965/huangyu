package org.aisin.sipphone.directcall;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.dial.Dial_SherchListAdapter;
import org.aisin.sipphone.mai_list.MailList_Adapter;
import org.aisin.sipphone.mai_list.MailList_abcList;
import org.aisin.sipphone.mai_list.OnListnerShearch;
import org.aisin.sipphone.mai_list.SearchText;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.ContactLoadinteface;
import org.aisin.sipphone.tools.CursorTools;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreListenerBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity implements OnListnerShearch,
		ContactLoadinteface, OnTouchListener, OnClickListener {

	private final String ACTION_NAME = "send_broadcast";
	private ImageView mail_list_add;
	private TextView mail_listactivity_tishi;
	private SearchText searchtext;
	private ListView mail_listactivity_list;
	private TextView mail_list_abcT;
	private boolean mail_list_abcT_flag = true;
	private MailList_abcList abclist;
	private boolean contacts_tx = true;
	private int listviewshowflag = 0, TXL = 0, HY = 1;// 当前显示的好友类型
	private TextView maillist_txlbt;// 显示通讯录好友
	private TextView maillist_hybt;// 显示环宇好友
	private ImageView img_back;
	private boolean show_back;
	private AisinOutCallActivity outcallActivity;
	private LinphoneCoreListenerBase mListener;
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
				String searchtext = msg.getData().getString("searchtext");
				if (searchtext == null) {
					searchtext = "";
				}
				SearchMaiList(searchtext);
			}
			if (msg.what == 2) {
				if (contacts.size() > 0) {
					// 读取在集合中 第几个位置 联系人首字母改变了
					szm_map.clear();
					for (int i = 0; i < contacts.size(); i++) {
						if (szm_map.get(contacts.get(i).getF_PY()) == null) {
							szm_map.put(contacts.get(i).getF_PY(), i);
						}
					}
					// 对缺省首字符做定位
					for (int i = 0; i < jss.length; i++) {
						if (i == 0 && szm_map.get(jss[i]) == null) {// #第一个字符如果没有的话要做特殊处理
							szm_map.put(jss[i], 0);
						} else {
							if (szm_map.get(jss[i]) == null) {
								szm_map.put(jss[i], szm_map.get(jss[i - 1]));
							}
						}
					}

					mail_listactivity_tishi.setText("");
					mail_listactivity_tishi.setVisibility(View.INVISIBLE);

					// 创建adapter
					MailList_Adapter mailadpter = new MailList_Adapter(
							ContactsActivity.this, contacts, szm_map);
					mail_listactivity_list.setAdapter(mailadpter);
				} else {
					mail_listactivity_tishi
							.setText("读取联系人失败！请检查是否允许本软件访问联系人的权限！");
					if (listviewshowflag == HY) {
						mail_listactivity_tishi.setText("暂无环宇好友,点击添加按钮添加环宇好友!");
						mail_listactivity_list.setVisibility(View.INVISIBLE);
						mail_listactivity_tishi.setVisibility(View.VISIBLE);
					}
				}
			}
			if (msg.what == 3) {
				mail_list_abcT.setVisibility(View.INVISIBLE);
				mail_list_abcT_flag = true;// 打开开关
			}
		}

	};

	private synchronized void SearchMaiList(String searchtext) {
		if ("".equals(searchtext)) {
			contacts_ss.clear();
			abclist.setVisibility(View.VISIBLE);
			mHandler.sendEmptyMessage(2);
			return;
		}
		abclist.setVisibility(View.INVISIBLE);
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
		// 创建adapter
		mail_listactivity_list.setAdapter(new Dial_SherchListAdapter(this,
				contacts_ss, Dial_SherchListAdapter.MLIST));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_listactivity);
		mail_list_add = (ImageView) findViewById(R.id.mail_list_add);
		mail_listactivity_tishi = (TextView) findViewById(R.id.mail_listactivity_tishi);
		searchtext = (SearchText) findViewById(R.id.searchtext);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
		mail_listactivity_list = (ListView) findViewById(R.id.mail_listactivity_list);
		abclist = (MailList_abcList) findViewById(R.id.mlist_abclist);
		mail_list_abcT = (TextView) findViewById(R.id.mail_list_abcT);
		img_back = (ImageView) findViewById(R.id.mail_bar_back);
		maillist_txlbt = (TextView) findViewById(R.id.maillist_txlbt);
		maillist_hybt = (TextView) findViewById(R.id.maillist_hybt);
		maillist_txlbt.setOnClickListener(this);
		maillist_hybt.setOnClickListener(this);
		mail_list_add.setOnClickListener(this);
		img_back.setOnClickListener(this);
		searchtext.setShearchListner(this);
		abclist.setOnTouchListener(this);
		show_back = getIntent().getBooleanExtra("show_back", false);
		if (show_back)
			img_back.setVisibility(View.VISIBLE);
		else
			img_back.setVisibility(View.GONE);

		// 初始化联系人数据
		CursorTools.loadContacts_2(this, this, false);
		mListener = new LinphoneCoreListenerBase() {
			@Override
			public void callState(LinphoneCore lc, LinphoneCall call,
					State state, String message) {

				if (AisinManager.getLc().getCallsNb() == 0) {
					finish();
					return;
				}

			}
		};
		LinphoneCore lc = AisinManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.addListener(mListener);
		}

	}

	public void sendOutcallBroadCast(int num) {
		Intent mIntent = new Intent(ACTION_NAME);
		mIntent.putExtra("status", num);
		sendBroadcast(mIntent);
	}

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
				Intent intent = new Intent(ContactsActivity.this,
						org.aisin.sipphone.mai_list.AddAisinFriend.class);
				startActivity(intent);
			}
		}

		if (v.getId() == R.id.mail_bar_back) {
			this.finish();
		} else if (v.getId() == R.id.maillist_txlbt) {
			if (listviewshowflag == TXL) {
				return;
			}
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
					CursorTools.loadContacts_2(ContactsActivity.this,
							ContactsActivity.this, false);
				}
			}).start();
		} else if (v.getId() == R.id.maillist_hybt) {
			if (listviewshowflag == HY) {
				return;
			}
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
					CursorTools.checkupfriends(ContactsActivity.this,
							ContactsActivity.this);
				}
			}).start();
		}
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected void onPause() {
		LinphoneCore lc = AisinManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null) {
			lc.removeListener(mListener);
		}
		super.onPause();
	}

	@Override
	public void DoadDown_map(TreeMap<Long, Contact> cttmap) {
		if (listviewshowflag == TXL) {
			contacts.clear();
			contacts = null;
			contacts = new ArrayList<Contact>();
			TreeSet<Contact> settemp = new TreeSet<Contact>();
			for (Entry<Long, Contact> et : cttmap.entrySet()) {
				settemp.add(et.getValue());
			}
			contacts.addAll(settemp);
			settemp.clear();
			settemp = null;
			mHandler.sendEmptyMessage(2);
		}
	}

	@Override
	public void headimagedown(boolean upflag) {
	}

	@Override
	public void upfrendsdwon(boolean upflag) {
	}

	@Override
	public void showfriends(boolean upflag) {
		if (listviewshowflag == HY) {
			mHandler.sendEmptyMessage(2);
		}
	}

}
