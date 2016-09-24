package org.aisin.sipphone_call_records;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.OBCallhistoryInfo;
import org.aisin.sipphone.tools.CursorLoadinterface;
import org.aisin.sipphone.tools.CursorTools;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class CallRecords_Fragment extends Fragment implements
		CursorLoadinterface, OnClickListener {
	private View call_records_fragment;
	private TextView call_recordsactivity_tishi;
	private ListView call_tecords_list;
	private TreeSet<OBCallhistoryInfo> set = new TreeSet<OBCallhistoryInfo>();
	private Call_records_Adapter cradpter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (set.size() > 0) {
					// 加载通话记录列表 隐藏加载中的文本提示框
					call_recordsactivity_tishi.setText("");
					call_recordsactivity_tishi.setVisibility(View.INVISIBLE);
					LoadListView();
				} else {
					call_recordsactivity_tishi
							.setText("通话记录为空，或者没有访问通话记录的权限！点我查看开启权限方法!");
				}
			} else if (msg.what == 2) { // 开启线程强制重新加载通话记录
				new Thread(new Runnable() {
					@Override
					public void run() {
						CursorTools.getCallRecords(AisinActivity.context,
								CallRecords_Fragment.this, true);
					}
				}).start();

			} else if (msg.what == 3) {// 更新头像
				if (cradpter == null) {
					cradpter = new Call_records_Adapter(AisinActivity.context,
							set, handler);
					call_tecords_list.setAdapter(cradpter);
				} else {
					cradpter.notifyDataSetChanged();
				}
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
		call_records_fragment = inflater.inflate(R.layout.call_recordsactivity,
				container, false);
		call_recordsactivity_tishi = (TextView) call_records_fragment
				.findViewById(R.id.call_recordsactivity_tishi);
		call_tecords_list = (ListView) call_records_fragment
				.findViewById(R.id.call_tecords_list);
		call_recordsactivity_tishi.setOnClickListener(this);
		// 加载通话记录,耗时操作，采用子线程回调
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CursorTools.getCallRecords(AisinActivity.context,
						CallRecords_Fragment.this, false);
			}
		}).start();

		return call_records_fragment;
	}

	// 加载通话记录列表
	private void LoadListView() {
		if (cradpter == null) {
			cradpter = new Call_records_Adapter(AisinActivity.context, set,
					handler);
			call_tecords_list.setAdapter(cradpter);
		} else {
			cradpter.notifyDataSetChanged();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (CallRecords_Fragment.class) {
					try {
						// 匹配头像
						for (OBCallhistoryInfo ohi : set) {
							// 优先匹配本地通讯录
							for (Entry<Long, Contact> ent : CursorTools.cttmap
									.entrySet()) {
								Contact cts = ent.getValue();
								if (cts == null) {
									continue;
								}
								for (String pnum : cts.getPhonesList()) {
									if (ohi.getPhone().equals(pnum)
											&& cts.getTx() != null) {
										ohi.setHeadbitmap(cts.getTx());
									}
								}
							}
							// 如果匹配不到通讯录 则匹配环宇好友
							if (ohi.getHeadbitmap() == null) {
								Iterator<Contact> ctf = CursorTools.friendslist
										.iterator();
								while (ctf.hasNext()) {
									Contact ctact = ctf.next();
									if (ctact.getFriendphone().equals(
											ohi.getPhone())
											&& ctact.getTx_fread() != null) {
										ohi.setHeadbitmap(ctact.getTx_fread());
									}
								}
							}
						}

					} catch (Exception e) {
					} catch (Error e) {
					}
					handler.sendEmptyMessage(3);
				}
			}
		}).start();
	}

	@Override
	public void DoadDown(TreeSet<OBCallhistoryInfo> ob_set) {
		// 通话记录加载完毕回调
		synchronized (CallRecords_Fragment.class) {
			this.set.clear();
			if (ob_set != null) {
				this.set.addAll(ob_set);
			}
			handler.sendEmptyMessage(1);
		}
	}

	// 强制重新加载通话记录
	public void ResetcallRrecords() {
		handler.sendEmptyMessage(2);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.call_recordsactivity_tishi) {
			// 开启权限教程
			startActivity(new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.TutorialActivity.class));
		}
	}
}
