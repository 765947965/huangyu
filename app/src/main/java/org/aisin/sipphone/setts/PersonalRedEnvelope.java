package org.aisin.sipphone.setts;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.mai_list.MailList_abcList;
import org.aisin.sipphone.mai_list.SearchText;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.HorizontalListView;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PersonalRedEnvelope extends Activity implements
		OnItemClickListener, TextWatcher, OnTouchListener, OnClickListener {
	private LinearLayout removelayout;
	private ListView aixinfriendlistview;
	private ImageView psll_bar_back;
	private SearchText searchtext;
	private MailList_abcList abclist;
	private TextView mail_list_abcT;
	private TextView aixinfriendtext;
	private TextView aixin_not_friendtext;
	private ImageView cleartext;
	private HorizontalListView hlistview;
	private HorizontalListViewAdapter hadapter;
	private TextView nextbt;
	private boolean mail_list_abcT_flag = true;
	private int listviewshowflag = 0, aixinfriend = 0, notaixinfriend = 1;// 当前显示的好友类型
	private PersonalRedEnvelopeAdapter adapter;
	private String[] searchs = new String[1];
	private TreeMap<String, Contact> addreds = new TreeMap<String, Contact>();// 当前收集的要发红包的人
	private ArrayList<Contact> addreds_list = new ArrayList<Contact>();// 当前收集的要发红包的人
																		// 缓存数组集合

	private ArrayList<Contact> contacts = new ArrayList<Contact>();// 展示列表集合
	private TreeSet<Contact> contacts_not = new TreeSet<Contact>();// 非环宇好友集合
	private TreeMap<String, Integer> szm_map = new TreeMap<String, Integer>();// 联系人首字母出现位置
	private TreeSet<Contact> contacts_ss = new TreeSet<Contact>();// 搜索结果联系人结果集合
	private String[] jss = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", "~" };

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				contacts.clear();
				try {
					contacts.addAll(CursorTools.friendslist);
				} catch (Exception e) {
				}
				mHandler.sendEmptyMessage(4);
			} else if (msg.what == 2) {
				contacts.clear();
				contacts.addAll(contacts_ss);
				mHandler.sendEmptyMessage(4);
			} else if (msg.what == 3) {
				// 处理非环宇好友
				contacts_not.clear();// 非环宇好友
				TreeMap<Long, Contact> ctat = new TreeMap<Long, Contact>();
				if (CursorTools.cttmap != null) {
					try {
						ctat.putAll(CursorTools.cttmap);
					} catch (Exception e) {
					}
				}
				ArrayList<Contact> friendslist = new ArrayList<Contact>();
				friendslist.addAll(CursorTools.friendslist);
				Set<Long> lgs = ctat.keySet();
				for (Long lg : lgs) {
					Contact ctt = ctat.get(lg);
					if (ctt == null) {
						continue;
					}
					for (int i = 0; i < ctt.getPhonesList().size(); i++) {
						boolean isfriend = false;
						for (Contact cts : friendslist) {
							if (ctt.getPhonesList().get(i)
									.equals(cts.getFriendphone())) {
								isfriend = true;// 如果有找到相同的则是环宇好友
							}
						}
						if (!isfriend) {
							Contact temp = new Contact();
							temp.setRemark(ctt.getRemark());
							temp.setF_PY(ctt.getF_PY());
							temp.setT_PY(ctt.getT_PY());
							temp.setSpy(ctt.getSpy());
							temp.setMatchstr(ctt.getMatchstr());
							temp.setMatchstr_mailist(ctt.getMatchstr_mailist());
							temp.setTx(ctt.getTx());
							temp.getSearchlist4name().addAll(
									ctt.getSearchlist4name());
							temp.getPhonesList()
									.add(ctt.getPhonesList().get(i));
							temp.setIsfreand(false);
							contacts_not.add(temp);
						}
					}
				}
				ctat.clear();
				ctat = null;
				friendslist.clear();
				friendslist = null;
				contacts.clear();
				contacts.addAll(contacts_not);
				mHandler.sendEmptyMessage(4);
			} else if (msg.what == 4) {
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

				if (adapter == null) {
					adapter = new PersonalRedEnvelopeAdapter(
							PersonalRedEnvelope.this, contacts, szm_map,
							searchs, addreds);
					aixinfriendlistview.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			} else if (msg.what == 5) {
				// 侧边的ABCD导航栏点击后 中间的提示框0.5秒后隐藏
				mail_list_abcT.setVisibility(View.INVISIBLE);
				mail_list_abcT_flag = true;// 打开开关
			} else if (msg.what == 6) {
				if (hadapter == null) {
					hadapter = new HorizontalListViewAdapter(
							PersonalRedEnvelope.this, addreds_list);
					hlistview.setAdapter(hadapter);
				} else {
					hadapter.notifyDataSetChanged();
				}
				nextbt.setText("下一步(" + addreds_list.size() + ")");
				if (addreds_list.size() > 0) {
					nextbt.setEnabled(true);
				} else {
					nextbt.setText("下一步");
					nextbt.setEnabled(false);
				}
			} else if (msg.what == 7) {
				adapter.notifyDataSetChanged();
			} else if (msg.what == 8) {
				addreds.clear();
				addreds_list.clear();
				listviewshowflag = aixinfriend;
				mHandler.sendEmptyMessage(6);
				aixinfriendtext.setTextColor(Color.parseColor("#FFFFFF"));
				aixinfriendtext
						.setBackgroundResource(R.drawable.maillisttitlebanck_txl_pres);
				aixin_not_friendtext.setTextColor(Color.parseColor("#1160FD"));
				aixin_not_friendtext
						.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_dis);
				searchtext.setText("");
			} else if (msg.what == 9) {
				addreds.clear();
				addreds_list.clear();
				listviewshowflag = notaixinfriend;
				mHandler.sendEmptyMessage(6);
				aixinfriendtext.setTextColor(Color.parseColor("#1160FD"));
				aixinfriendtext
						.setBackgroundResource(R.drawable.maillisttitlebanck_txl_dis);
				aixin_not_friendtext.setTextColor(Color.parseColor("#FFFFFF"));
				aixin_not_friendtext
						.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_pres);
				searchtext.setText("");
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalredenvelope);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		aixinfriendlistview = (ListView) this
				.findViewById(R.id.aixinfriendlistview);
		psll_bar_back = (ImageView) this.findViewById(R.id.psll_bar_back);
		searchtext = (SearchText) this.findViewById(R.id.searchtext);
		abclist = (MailList_abcList) this.findViewById(R.id.ppre_abclist);
		mail_list_abcT = (TextView) this.findViewById(R.id.mail_list_abcT);
		aixinfriendtext = (TextView) this.findViewById(R.id.aixinfriendtext);
		aixin_not_friendtext = (TextView) this
				.findViewById(R.id.aixin_not_friendtext);
		cleartext = (ImageView) this.findViewById(R.id.cleartext);
		hlistview = (HorizontalListView) this.findViewById(R.id.hlistview);
		nextbt = (TextView) this.findViewById(R.id.nextbt);
		cleartext.setOnClickListener(this);
		aixinfriendtext.setOnClickListener(this);
		aixin_not_friendtext.setOnClickListener(this);
		searchtext.addTextChangedListener(this);
		aixinfriendlistview.setOnItemClickListener(this);
		hlistview.setOnItemClickListener(this);
		abclist.setOnTouchListener(this);
		nextbt.setOnClickListener(this);
		psll_bar_back.setOnClickListener(this);
		mHandler.sendEmptyMessage(1);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent == aixinfriendlistview) {

			ImageView item_radiobutton = (ImageView) view
					.findViewById(R.id.item_radiobutton);
			Contact ctmp = contacts.get(position);
			if (listviewshowflag == aixinfriend) {
				if (addreds.get(ctmp.getUid()) != null) {
					addreds_list.remove(ctmp);
					addreds.remove(ctmp.getUid());
					item_radiobutton.setImageResource(R.drawable.aos);
				} else {
					addreds_list.add(ctmp);
					addreds.put(ctmp.getUid(), ctmp);
					item_radiobutton.setImageResource(R.drawable.aor);
				}
			} else {
				if (addreds.get(ctmp.getPhonesList().get(0)) != null) {
					addreds_list.remove(ctmp);
					addreds.remove(ctmp.getPhonesList().get(0));
					item_radiobutton.setImageResource(R.drawable.aos);
				} else {
					addreds_list.add(ctmp);
					addreds.put(ctmp.getPhonesList().get(0), ctmp);
					item_radiobutton.setImageResource(R.drawable.aor);
				}
			}

		} else if (parent == hlistview) {
			Contact cts = addreds_list.get(position);
			if (listviewshowflag == aixinfriend) {
				addreds.remove(cts.getUid());
			} else {
				addreds.remove(cts.getPhonesList().get(0));
			}
			addreds_list.remove(position);
			mHandler.sendEmptyMessage(7);
		}
		mHandler.sendEmptyMessage(6);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String searchtext = s.toString();
		searchs[0] = searchtext;
		if ("".equals(searchtext)) {
			cleartext.setVisibility(View.INVISIBLE);
			if (listviewshowflag == aixinfriend) {
				mHandler.sendEmptyMessage(1);
			} else {
				mHandler.sendEmptyMessage(3);
			}
		} else {
			cleartext.setVisibility(View.VISIBLE);
			contacts_ss.clear();
			ArrayList<Contact> ctttemp = new ArrayList<Contact>();
			if (listviewshowflag == aixinfriend) {
				ctttemp.addAll(CursorTools.friendslist);
			} else {
				ctttemp.addAll(contacts_not);
			}
			for (Contact ctt : ctttemp) {
				TreeSet<String> arrlist_ss = ctt.getSearchlist4name();
				for (String str : arrlist_ss) {
					if (str.indexOf(searchtext) >= 0) {// str是否包含搜索数据
						if (Check_format.check_ABC(searchtext)) {// 如果是字母检索
																	// 做拼音匹配
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
			ctttemp.clear();
			ctttemp = null;
			mHandler.sendEmptyMessage(2);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
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
						mHandler.sendEmptyMessage(5);
					}
				}, 500);
			}

			int item_num = szm_map.get(key_value);
			aixinfriendlistview.setSelection(item_num);
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.aixinfriendtext:
			if (listviewshowflag == aixinfriend) {
				return;
			}
			if (addreds_list.size() > 0) {
				AisinBuildDialog builder_af = new AisinBuildDialog(
						PersonalRedEnvelope.this);
				builder_af.setTitle("提示");
				builder_af.setMessage("切换好友类型,将清空已选好友!是否继续?");
				builder_af.setOnDialogCancelListener("取消", null);
				builder_af.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								mHandler.sendEmptyMessage(8);
							}
						});
				builder_af.dialogShow();
			} else {
				mHandler.sendEmptyMessage(8);
			}
			break;
		case R.id.aixin_not_friendtext:
			if (listviewshowflag == notaixinfriend) {
				return;
			}
			if (addreds_list.size() > 0) {
				AisinBuildDialog builder_af_not = new AisinBuildDialog(
						PersonalRedEnvelope.this);
				builder_af_not.setTitle("提示");
				builder_af_not.setMessage("切换好友类型,将清空已选好友!是否继续?");
				builder_af_not.setOnDialogCancelListener("取消", null);
				builder_af_not.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								mHandler.sendEmptyMessage(9);
							}
						});
				builder_af_not.dialogShow();
			} else {
				mHandler.sendEmptyMessage(9);
			}
			break;
		case R.id.cleartext:
			searchtext.setText("");
			break;
		case R.id.nextbt:
			Intent intent = new Intent(PersonalRedEnvelope.this,
					org.aisin.sipphone.setts.PersonalRedEnvelopeConfig.class);
			ArrayList<String> uidorphones = new ArrayList<String>();
			uidorphones.addAll(addreds.keySet());
			if (listviewshowflag == aixinfriend) {
				intent.putExtra("addred_flag", "uid");
			} else {
				intent.putExtra("addred_flag", "phone");
			}
			if (uidorphones.size() == 1) {
				intent.putExtra("remark", addreds_list.get(0).getRemark());
			}

			intent.putStringArrayListExtra("uidorphones", uidorphones);
			PersonalRedEnvelope.this.startActivityForResult(intent, 1);
			break;
		case R.id.psll_bar_back:
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			// 是否结束自己
			if (data != null) {
				String outflag = data.getStringExtra("outflag");
				if ("over".equals(outflag)) {
					PersonalRedEnvelope.this.finish();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容
	}

}
