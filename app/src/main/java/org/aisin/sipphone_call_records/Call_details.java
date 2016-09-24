package org.aisin.sipphone_call_records;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.setts.SetInviteriends;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract.Intents;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Call_details extends Activity implements OnClickListener {
	private LinearLayout call_details_activity_linlayout;
	private ImageView call_details_back;
	private ImageView call_details_imageadd;
	private ImageView call_details_activity_tx;
	private TextView call_details_name;
	private TextView call_details_phone;
	private TextView call_details_time_1;
	private TextView call_details_type_1;
	private TextView call_details_time_2;
	private TextView call_details_type_2;
	private TextView call_details_time_3;
	private TextView call_details_type_3;
	private TextView call_details_more;
	private RelativeLayout call_details_tel_item;
	private TextView call_details_tel_phone;
	private TextView call_details_info;
	private TextView r_loding4v2_lodingbt;
	private boolean addc = false;// 是否可添加联系人标志
	private String phone;
	private TreeSet<CallhistoryInfo> set;
	private int txid;// 头像ID
	private byte[] bitmapByte;
	private Bitmap bitmaptx;
	private String invite_sns_message;
	private String invite_sms_message;
	private String invite_url;
	private UserInfo userinfo;
	private String phoneinfo;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (r_loding4v2_lodingbt != null) {
					r_loding4v2_lodingbt.setVisibility(View.VISIBLE);
				}
			} else if (msg.what == 2) {
				if (call_details_info != null && phoneinfo != null
						&& !"".equals(phoneinfo)) {
					call_details_info.setVisibility(View.VISIBLE);
					call_details_info.setText(phoneinfo);
				}
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_details_activity);
		phone = getIntent().getStringExtra("phone");
		set = (TreeSet<CallhistoryInfo>) getIntent()
				.getSerializableExtra("set");
		txid = (Integer) getIntent().getIntExtra("TXID", 0);
		bitmapByte = (byte[]) getIntent().getSerializableExtra("history.tx");
		if (phone == null || set == null) {
			finish();
			return;
		}
		call_details_activity_linlayout = (LinearLayout) this
				.findViewById(R.id.call_details_activity_linlayout);
		call_details_back = (ImageView) this
				.findViewById(R.id.call_details_back);
		call_details_imageadd = (ImageView) this
				.findViewById(R.id.call_details_imageadd);
		call_details_activity_tx = (ImageView) this
				.findViewById(R.id.call_details_activity_tx);
		call_details_name = (TextView) this
				.findViewById(R.id.call_details_name);
		call_details_phone = (TextView) this
				.findViewById(R.id.call_details_phone);
		call_details_time_1 = (TextView) this
				.findViewById(R.id.call_details_time_1);
		call_details_type_1 = (TextView) this
				.findViewById(R.id.call_details_type_1);
		call_details_time_2 = (TextView) this
				.findViewById(R.id.call_details_time_2);
		call_details_type_2 = (TextView) this
				.findViewById(R.id.call_details_type_2);
		call_details_time_3 = (TextView) this
				.findViewById(R.id.call_details_time_3);
		call_details_type_3 = (TextView) this
				.findViewById(R.id.call_details_type_3);
		call_details_more = (TextView) this
				.findViewById(R.id.call_details_more);
		call_details_tel_item = (RelativeLayout) this
				.findViewById(R.id.call_details_tel_item);
		call_details_tel_phone = (TextView) this
				.findViewById(R.id.call_details_tel_phone);
		call_details_info = (TextView) this
				.findViewById(R.id.call_details_info);
		r_loding4v2_lodingbt = (TextView) this
				.findViewById(R.id.r_loding4v2_lodingbt);
		call_details_back.setOnClickListener(this);
		call_details_imageadd.setOnClickListener(this);
		call_details_more.setOnClickListener(this);
		call_details_tel_item.setOnClickListener(this);
		r_loding4v2_lodingbt.setOnClickListener(this);
		// 初始化
		Initialization();
	}

	private void Initialization() {
		// 设置头像
		if (bitmapByte != null) {
			bitmaptx = BitmapFactory.decodeByteArray(bitmapByte, 0,
					bitmapByte.length);
			call_details_activity_tx.setImageBitmap(bitmaptx);
		} else {
			call_details_activity_tx.setImageResource(txid);
		}

		call_details_phone.setText(phone);
		call_details_tel_phone.setText(phone);
		int num = 1;
		for (CallhistoryInfo clhi : set) {
			if (num == 4) {
				break;
			}
			if (num == 1) {
				if (clhi.getName() == null || "".equals(clhi.getName())) {
					call_details_imageadd
							.setImageResource(R.drawable.addmail_select);
					addc = true;
				}
				call_details_name.setText(clhi.getName());
				call_details_time_1.setText(clhi.getChainal_call_time());
				call_details_type_1.setText(clhi.getCall_type_name());
			}
			if (num == 2) {
				call_details_time_2.setText(clhi.getChainal_call_time());
				call_details_type_2.setText(clhi.getCall_type_name());
			}
			if (num == 3) {
				call_details_time_3.setText(clhi.getChainal_call_time());
				call_details_type_3.setText(clhi.getCall_type_name());
			}
			num += 1;
		}
		if (set.size() > 3) {
			call_details_more.setText("更多...");
		}

		SharedPreferences shared_SNSshare = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(this);
		invite_url = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
		invite_sns_message = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_SNS_MESSAGE, "");
		invite_sms_message = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_SMS_MESSAGE, "");
		userinfo = UserInfo_db.getUserInfo(this);
		// 开启线程查找是否环宇好友
		try {
			String ipcall_prefix = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(this)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_ipcall_prefix,
							"");
			if (!"".equals(ipcall_prefix) && phone.startsWith(ipcall_prefix)) {
				phone = phone.substring(ipcall_prefix.length());
			}
			Pattern p = Pattern.compile("1[3-578][0-9]{9}");
			final Matcher m = p.matcher(phone);
			if (m.find()) {// 如果是手机号码 格式化手机号码 再做环宇好友匹配
				phone = m.group(0);
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean isFriend = false;
						for (Contact ctt : CursorTools.friendslist) {
							if (phone.indexOf(ctt.getFriendphone()) != -1) {
								isFriend = true;
								break;
							}
						}
						if (!isFriend) {
							mHandler.sendEmptyMessage(1);
						}
					}
				}).start();
			}
			// 开启线程匹配归属地
			new Thread(new Runnable() {
				@Override
				public void run() {
					ArrayList<String> arraylist = new ArrayList<String>();
					arraylist.add(phone);
					try {
						phoneinfo = GetPhoneInfo4DB.getInfo(Call_details.this,
								arraylist).get(0);
						mHandler.sendEmptyMessage(2);
					} catch (Exception e) {
					}
				}
			}).start();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.call_details_back:
			finish();
			break;
		case R.id.call_details_imageadd:
			if (addc) {// 添加联系人
				Intent intent = new Intent(Intent.ACTION_INSERT,
						Uri.withAppendedPath(
								Uri.parse("content://com.android.contacts"),
								"contacts"));
				intent.putExtra(Intents.Insert.PHONE, phone);
				startActivity(intent);
			}
			break;
		case R.id.call_details_more:
			if (set.size() > 3) {
				// 显示更多通话记录
				Intent intent = new Intent(Call_details.this,
						org.aisin.sipphone_call_records.MoreCall_records.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("morecall_records", set);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		case R.id.call_details_tel_item:
			// 打电话
			CallhistoryInfo callhistoryinfo = set.first();
			CallPhoneManage.callPhone(Call_details.this, bitmapByte,
					callhistoryinfo.getName(), callhistoryinfo.getPhone());
			break;
		case R.id.r_loding4v2_lodingbt:// 邀请好友
			AisinBuildDialog mybuild = new AisinBuildDialog(Call_details.this);
			mybuild.setListViewItem(new String[] { "短信" },
					new onMyItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							if (position == 0) {
								Uri smsToUri = Uri.parse("smsto:" + phone);// 联系人地址
								Intent mIntent = new Intent(
										android.content.Intent.ACTION_SENDTO,
										smsToUri);
								mIntent.putExtra(
										"sms_body",
										invite_sms_message
												+ invite_url
														.replace(
																"phone=%s",
																"phone="
																		+ userinfo
																				.getPhone())
														.replace("channel=%s",
																"channel=sms"));
								Call_details.this.startActivity(mIntent);
							} else if (position == 1) {
								Intent intent = new Intent(
										Call_details.this,
										org.aisin.sipphone.setts.SharedActivity.class);
								intent.putExtra("invite_sns_message",
										invite_sns_message);
								intent.putExtra("shareflag", "weixin");
								Call_details.this.startActivity(intent);
							}
						}
					});
			mybuild.setOnDialogCancelListener("取消", null);
			mybuild.dialogShow();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmaptx != null) {
			bitmaptx.recycle();
			bitmaptx = null;
		}
		bitmapByte = null;
		RecoveryTools.unbindDrawables(call_details_activity_linlayout);// 回收容
	}
}
