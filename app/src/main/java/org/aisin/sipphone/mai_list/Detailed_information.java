package org.aisin.sipphone.mai_list;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.sqlitedb.GetPhoneInfo4DB;
import org.aisin.sipphone.tools.AvatarID;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UPFrends4;
import org.aisin.sipphone.tools.UserInfo_db;
import org.aisin.sipphone_call_records.Call_details;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Detailed_information extends Activity implements OnClickListener {
	private LinearLayout detailed_information_linlayout;
	private ImageView detailed_information_activity_back;
	private TextView dinfo_editor;
	private ImageView details_information_tx;
	private ImageView showimage;
	private TextView details_information_name2phone;
	private TextView r_loding4v2_lodingbt;
	private ListView detailed_information_list;

	private String name;
	private Long contactID;
	private byte[] bitmapByte;
	private String tx_url;
	private Bitmap bitmaptx;
	private ArrayList<String> phonelist;
	private ArrayList<String> phonelist_gsd;
	private DetailedInformationListAdapter dila;
	private boolean isIsfreand;
	private String sex;
	private String bthday;
	private String friendname;
	private String freanduid;
	private String friendphone;
	private String signature;
	private String address;
	private boolean showflag;
	private boolean isMyfriend;
	private String invite_sns_message;
	private String invite_sms_message;
	private String invite_url;
	private UserInfo userinfo;

	private ImageView seximage;
	private TextView agetext;
	private TextView freandnametext;
	private TextView freanduidtext;
	private TextView signaturetext;
	private TextView signaturetitle;
	private TextView bthdaytitle;
	private TextView addresstitle;
	private View signatureline;
	private TextView bthdaytext;
	private View bthdayline;
	private TextView addresstext;
	private View addressine;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				details_information_tx.setImageBitmap(bitmaptx);
			} else if (msg.what == 2) {
				int avatarid = Detailed_information.this.getIntent()
						.getIntExtra("Contact.avatarid", 0);
				if (avatarid != 0) {
					details_information_tx.setImageResource(avatarid);
				} else {
					details_information_tx.setImageResource(AvatarID
							.getAvatarID());
				}
			} else if (msg.what == 3) {
				new AisinBuildDialog(Detailed_information.this, "提示", "添加好友成功!");
				dinfo_editor.setVisibility(View.INVISIBLE);
			} else if (msg.what == 4) {
				new AisinBuildDialog(Detailed_information.this, "提示",
						"添加好友失败,请稍后再试!");
			} else if (dila != null && detailed_information_list != null) {
				dila.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailed_information_activity);
		bitmapByte = getIntent().getByteArrayExtra("Contact.tx");
		name = getIntent().getStringExtra("Contact.name");
		contactID = getIntent().getLongExtra("Contact.contactID", 0);
		phonelist = getIntent().getStringArrayListExtra("Contact.phonelist");
		tx_url = getIntent().getStringExtra("Contact.tx_url");
		if (name == null || phonelist == null) {
			finish();
			return;
		}
		detailed_information_linlayout = (LinearLayout) this
				.findViewById(R.id.detailed_information_linlayout);
		detailed_information_activity_back = (ImageView) this
				.findViewById(R.id.detailed_information_activity_back);
		dinfo_editor = (TextView) this.findViewById(R.id.dinfo_editor);
		dinfo_editor.setOnClickListener(this);
		details_information_tx = (ImageView) this
				.findViewById(R.id.details_information_tx);
		showimage = (ImageView) this.findViewById(R.id.showimage);
		details_information_name2phone = (TextView) this
				.findViewById(R.id.details_information_name2phone);
		r_loding4v2_lodingbt = (TextView) this
				.findViewById(R.id.r_loding4v2_lodingbt);
		detailed_information_list = (ListView) this
				.findViewById(R.id.detailed_information_list);
		seximage = (ImageView) this.findViewById(R.id.seximage);
		agetext = (TextView) this.findViewById(R.id.agetext);
		freandnametext = (TextView) this.findViewById(R.id.freandname);
		freanduidtext = (TextView) this.findViewById(R.id.freanduid);
		signaturetext = (TextView) this.findViewById(R.id.signature);
		signatureline = this.findViewById(R.id.signatureline);
		bthdaytext = (TextView) this.findViewById(R.id.bthday);
		bthdayline = this.findViewById(R.id.bthdayline);
		addresstext = (TextView) this.findViewById(R.id.address);
		addressine = this.findViewById(R.id.addressine);
		signaturetitle = (TextView) this.findViewById(R.id.signaturetitle);
		bthdaytitle = (TextView) this.findViewById(R.id.bthdaytitle);
		addresstitle = (TextView) this.findViewById(R.id.addresstitle);
		showimage.setOnClickListener(this);
		r_loding4v2_lodingbt.setOnClickListener(this);
		detailed_information_activity_back.setOnClickListener(this);
		details_information_name2phone.setText(name);

		isIsfreand = getIntent().getBooleanExtra("Contact.isIsfreand", false);
		isMyfriend = getIntent().getBooleanExtra("Contact.isMyfriend", true);
		showflag = getIntent().getBooleanExtra("Contact.showflag", false);
		if (isIsfreand) {
			sex = getIntent().getStringExtra("Contact.sex");
			bthday = getIntent().getStringExtra("Contact.bthday");
			friendname = getIntent().getStringExtra("Contact.friendname");
			freanduid = getIntent().getStringExtra("Contact.freanduid");
			friendphone = getIntent().getStringExtra("Contact.friendphone");
			signature = getIntent().getStringExtra("Contact.signature");
			address = getIntent().getStringExtra("Contact.address");
			if (sex != null && !"".equals(sex)) {
				if ("男".equals(sex)) {
					seximage.setImageResource(R.drawable.one_profile_male_left_dark);
					seximage.setVisibility(View.VISIBLE);
				} else if ("女".equals(sex)) {
					seximage.setImageResource(R.drawable.one_profile_female_left_dark);
					seximage.setVisibility(View.VISIBLE);
				}
			}
			if (bthday != null && !"".equals(bthday) && bthday.length() > 4
					&& !"null".equals(bthday)) {
				SimpleDateFormat sdformat = new SimpleDateFormat("yyyy");
				int yeartody = Integer.parseInt(sdformat.format(new Date()));
				int bthday_int = Integer
						.parseInt(bthday.trim().substring(0, 4));
				agetext.setText((yeartody - bthday_int) + "岁");
				agetext.setVisibility(View.VISIBLE);
				bthdaytext.setText(bthday);
				bthdaytext.setVisibility(View.VISIBLE);
				bthdayline.setVisibility(View.VISIBLE);
				bthdaytitle.setVisibility(View.VISIBLE);
			}
			if (friendname != null && !"".equals(friendname)
					&& !"null".equals(friendname)) {
				freandnametext.setText("昵称: " + friendname);
				freandnametext.setVisibility(View.VISIBLE);
			}
			if (freanduid != null && !"".equals(freanduid)
					&& !"null".equals(freanduid)) {
				freanduidtext.setText("环宇号: " + freanduid);
				freanduidtext.setVisibility(View.VISIBLE);
			}
			if (signature != null && !"".equals(signature)
					&& !"null".equals(signature)) {
				signaturetext.setText(signature);
				signaturetext.setVisibility(View.VISIBLE);
				signatureline.setVisibility(View.VISIBLE);
				signaturetitle.setVisibility(View.VISIBLE);
			}
			if (address != null && !"".equals(address) && !"-".equals(address)
					&& !"null-null".equals(address)) {
				addresstext.setText(address);
				addresstext.setVisibility(View.VISIBLE);
				addressine.setVisibility(View.VISIBLE);
				addresstitle.setVisibility(View.VISIBLE);
			}
			if (showflag) {
				phonelist.clear();
				phonelist.add(friendphone);
				if (!isMyfriend) {
					dinfo_editor.setText("加为好友");
					dinfo_editor.setVisibility(View.VISIBLE);
				} else {
					dinfo_editor.setVisibility(View.INVISIBLE);
				}
			}
		} else {// 非环宇用户
			Pattern p = Pattern.compile("1[3-578][0-9]{9}");
			for (String str : phonelist) {
				Matcher m = p.matcher(str);
				if (m.find()) {
					r_loding4v2_lodingbt.setVisibility(View.VISIBLE);
					break;
				}
			}
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
		phonelist_gsd = new ArrayList<String>();
		dila = new DetailedInformationListAdapter(Detailed_information.this,
				phonelist, phonelist_gsd, bitmapByte, name, friendphone);
		detailed_information_list.setAdapter(dila);
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (bitmapByte != null) {
					bitmaptx = BitmapFactory.decodeByteArray(bitmapByte, 0,
							bitmapByte.length);
					mHandler.sendEmptyMessage(1);
				} else if (tx_url != null && !"".equals(tx_url)) {
					File file = Detailed_information.this
							.getFileStreamPath(tx_url);
					if (file.exists()) {
						bitmaptx = BitmapFactory.decodeFile(file
								.getAbsolutePath());
					}
					if (bitmaptx != null) {
						mHandler.sendEmptyMessage(1);
					} else {
						mHandler.sendEmptyMessage(2);
					}
				} else {
					mHandler.sendEmptyMessage(2);

				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					phonelist_gsd.addAll(GetPhoneInfo4DB.getInfo(
							Detailed_information.this, phonelist));
					mHandler.sendEmptyMessage(5);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmaptx != null) {
			bitmaptx.recycle();
			bitmaptx = null;
		}
		bitmapByte = null;
		RecoveryTools.unbindDrawables(detailed_information_linlayout);// 回收容
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.detailed_information_activity_back:
			finish();
			break;
		case R.id.showimage:
			// 展示头像
			if (bitmapByte != null) {
				Intent intent_tx = new Intent(Detailed_information.this,
						org.aisin.sipphone.setts.ShowImage.class);
				intent_tx.putExtra("imagepath_byte", bitmapByte);
				Detailed_information.this.startActivity(intent_tx);
				Detailed_information.this.overridePendingTransition(
						R.anim.anim_5, R.anim.anim_3);
			} else if (tx_url != null && !"".equals(tx_url)
					&& !"null".equals(tx_url)) {
				Intent intent_tx = new Intent(Detailed_information.this,
						org.aisin.sipphone.setts.ShowImage.class);
				intent_tx.putExtra("imagepath", Detailed_information.this
						.getFileStreamPath(tx_url).getAbsolutePath());
				Detailed_information.this.startActivity(intent_tx);
				Detailed_information.this.overridePendingTransition(
						R.anim.anim_5, R.anim.anim_3);
			}
			break;
		case R.id.dinfo_editor:// 编辑联系人
			if (showflag && !isMyfriend) {
				// 添加环宇好友
				new Thread(new Runnable() {
					@Override
					public void run() {
						boolean upflag = UPFrends4.Commitdata_one(
								Detailed_information.this, name, friendphone);
						if (upflag) {
							Intent intent = new Intent(Constants.BrandName
									+ ".addfriend.noupmails");
							intent.putExtra("uid", freanduid);
							Detailed_information.this.sendBroadcast(intent);
							mHandler.sendEmptyMessage(3);
						} else {
							mHandler.sendEmptyMessage(4);
						}
					}
				}).start();
			} else {
				Intent intent = new Intent(Intent.ACTION_EDIT,
						Uri.withAppendedPath(
								ContactsContract.Contacts.CONTENT_URI,
								String.valueOf(contactID)));
				startActivity(intent);
				finish();
			}
			break;
		case R.id.r_loding4v2_lodingbt:// 邀请好友
			if (phonelist.size() > 1) {
				AisinBuildDialog mybuild = new AisinBuildDialog(
						Detailed_information.this);
				mybuild.setListViewItem(phonelist, new onMyItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ShareFriends(phonelist.get(position));
					}
				});
				mybuild.setOnDialogCancelListener("取消", null);
				mybuild.dialogShow();
			} else {
				ShareFriends(phonelist.get(0));
			}
			break;
		}
	}

	private void ShareFriends(final String phonetem) {
		AisinBuildDialog mybuild = new AisinBuildDialog(
				Detailed_information.this);
		mybuild.setListViewItem(new String[] { "短信邀请" },
				new onMyItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (position == 0) {
							Uri smsToUri = Uri.parse("smsto:" + phonetem);// 联系人地址
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
							Detailed_information.this.startActivity(mIntent);
						} else if (position == 1) {
							Intent intent = new Intent(
									Detailed_information.this,
									org.aisin.sipphone.setts.SharedActivity.class);
							intent.putExtra("invite_sns_message",
									invite_sns_message);
							intent.putExtra("shareflag", "weixin");
							Detailed_information.this.startActivity(intent);
						}
					}
				});
		mybuild.setOnDialogCancelListener("取消", null);
		mybuild.dialogShow();
	}

}
