package org.aisin.sipphone.mai_list;

import java.io.File;
import java.util.Map;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.sqlitedb.Friend_data_Check;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UPFrends4;
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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AddAisinFriend extends Activity implements OnClickListener,
		TextWatcher, OnKeyListener {
	private Context mContext;
	private LinearLayout removelayout;
	private ImageView addaisinfriend_back;
	private ImageView qrcodecom;
	private EditText searchtext;
	private ImageView cleartext;
	private TextView seartchbt;
	private ProgressDialog prd;
	private ListView listview;
	private addfriendBroadcast broadcast;
	private AddAisinFriend_Adapter adapter;
	private boolean showflagone = false;
	private TreeSet<Contact> contacts = new TreeSet<Contact>();
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (adapter == null) {
					adapter = new AddAisinFriend_Adapter(mContext,
							contacts);
					listview.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}

			} else if (msg.what == 2) {
				new AisinBuildDialog(mContext, "提示", "添加好友成功!");
				mHandler.sendEmptyMessage(1);
				mContext.sendBroadcast(new Intent(
						Constants.BrandName + ".mailist.updata"));
			} else if (msg.what == 3) {
				new AisinBuildDialog(mContext, "提示", "添加好友失败,请稍后再试!");
			} else if (msg.what == 4) {
				if (showflagone) {
					showflagone = false;
					Contact ctt = contacts.first();
					Intent intent = new Intent(
							mContext,
							org.aisin.sipphone.mai_list.Detailed_information.class);
					if (ctt.getRemark() != null && !"".equals(ctt.getRemark())
							&& !"null".equals(ctt.getRemark())) {
						intent.putExtra("Contact.name", ctt.getRemark());
					} else {
						intent.putExtra("Contact.name", ctt.getFriendphone());
					}
					intent.putExtra("Contact.showflag", ctt.isShowflag());
					intent.putExtra("Contact.isIsfreand", ctt.isIsfreand());
					intent.putExtra("Contact.isMyfriend", ctt.isMyfriend());
					intent.putStringArrayListExtra("Contact.phonelist",
							ctt.getPhonesList());
					intent.putExtra("Contact.sex", ctt.getSex());
					intent.putExtra("Contact.bthday", ctt.getBthday());
					intent.putExtra("Contact.friendname", ctt.getFriendname());
					intent.putExtra("Contact.freanduid", ctt.getUid());
					intent.putExtra("Contact.friendphone", ctt.getFriendphone());
					intent.putExtra("Contact.signature", ctt.getSignature());
					intent.putExtra("Contact.address", ctt.getProvince() + "-"
							+ ctt.getCity());
					intent.putExtra("Contact.tx_url", ctt.getUid()
							+ "headimage.jpg");
					intent.putExtra("Contact.avatarid", ctt.getAvatarid());
					mContext.startActivity(intent);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.addaisinfriend);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		addaisinfriend_back = (ImageView) this
				.findViewById(R.id.addaisinfriend_back);
		qrcodecom = (ImageView) this.findViewById(R.id.qrcodecom);
		searchtext = (EditText) this.findViewById(R.id.searchtext);
		cleartext = (ImageView) this.findViewById(R.id.cleartext);
		seartchbt = (TextView) this.findViewById(R.id.seartchbt);
		listview = (ListView) this.findViewById(R.id.listview);
		addaisinfriend_back.setOnClickListener(this);
		qrcodecom.setOnClickListener(this);
		cleartext.setOnClickListener(this);
		seartchbt.setOnClickListener(this);
		searchtext.addTextChangedListener(this);
		searchtext.setOnKeyListener(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("查询中...");

		broadcast = new addfriendBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".addfriend");
		filter.addAction(Constants.BrandName + ".addfriend.noupmails");
		registerReceiver(broadcast, filter);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcast);
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.addaisinfriend_back:
			finish();
			break;
		case R.id.qrcodecom:
			// 扫二维码
			Intent intent = new Intent(mContext,
					com.dtr.zxing.activity.CaptureActivity.class);
			startActivityForResult(intent, 1);
			break;
		case R.id.cleartext:
			searchtext.setText("");
			break;
		case R.id.seartchbt:
			// 搜索环宇好友
			// 判断输入是否正确
			if (Checkinput()) {
				prd.show();
				new HttpTask_checkfriend().execute(searchtext.getText()
						.toString());
			}
			break;
		}
	}

	private boolean Checkinput() {
		String inputtext = searchtext.getText().toString().trim();
		return Checkinput(inputtext);
	}

	private boolean Checkinput(String inputtext) {
		if ("".equals(inputtext)) {
			new AisinBuildDialog(mContext, "提示", "请输入正确的环宇号或者手机号码!");
			return false;
		} else if (!Check_format.Check_num(inputtext)) {
			new AisinBuildDialog(mContext, "提示",
					"搜索的环宇号或者手机号码只能是数字!");
			return false;
		} else if (inputtext.startsWith("1") && inputtext.length() != 11) {
			new AisinBuildDialog(mContext, "提示", "请输入正确的手机号码!");
			return false;
		} else if (inputtext.length() < 6 || inputtext.length() > 20) {
			new AisinBuildDialog(mContext, "提示", "请输入正确的环宇号!");
			return false;
		}
		return true;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 文字改变
		String inputtemp = s.toString();
		if ("".equals(inputtemp)) {
			cleartext.setVisibility(View.INVISIBLE);
		} else {
			cleartext.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	class HttpTask_checkfriend extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// 获取查询是否环宇好友的URL
			String url = URLTools.GetHttpURL_4Friend(mContext,
					params[0].trim());
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
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
				try {
					contacts.clear();
					JSONArray friendslist = json.getJSONArray("friendslist");
					for (int i = 0; i < friendslist.length(); i++) {
						JSONObject jobfriend = friendslist.getJSONObject(i);
						String province = jobfriend.optString("province");
						String picture = jobfriend.optString("picture");
						String picmd5 = jobfriend.optString("picmd5");
						String ver = jobfriend.optString("ver");
						String uid = jobfriend.optString("uid");
						String city = jobfriend.optString("city");
						String company = jobfriend.optString("company");
						String profession = jobfriend.optString("profession");
						String school = jobfriend.optString("school");
						String sex = jobfriend.optString("sex");
						String signature = jobfriend.optString("signature");
						String mobileNumber = jobfriend
								.optString("mobileNumber");
						String name = jobfriend.optString("name");
						Contact contact = new Contact();
						contact.setRemark(name);
						contact.setFriendname(name);
						contact.setMobileNumber(mobileNumber);
						contact.setFriendphone(mobileNumber);
						contact.setProvince(province);
						contact.setPicture(picture);
						contact.setPicmd5(picmd5);
						contact.setVer(ver);
						contact.setUid(uid);
						contact.setCity(city);
						contact.setCompany(company);
						contact.setProfession(profession);
						contact.setSchool(school);
						contact.setSex(sex);
						contact.setSignature(signature);
						contact.setShowflag(true);
						contact.setIsfreand(true);
						Contact cttcheck = Friend_data_Check.Checkdata4Sqlit(
								mContext, uid, false);
						if (cttcheck != null) {
							contact.setMyfriend(true);
						} else {
							contact.setMyfriend(false);
						}
						contacts.add(contact);
					}
					mHandler.sendEmptyMessage(1);
					// 开启线程 下载头像
					new Thread(new Runnable() {
						@Override
						public void run() {
							boolean downflag = false;
							for (Contact cta : contacts) {
								File file = mContext
										.getFileStreamPath(cta.getUid()
												+ "headimage.jpg");
								if (!file.exists()
										&& !"".equals(cta.getPicture())
										&& !"null".equals(cta.getPicture())) {
									if (HttpUtils.downloadImage(
											mContext,
											cta.getPicture(), cta.getUid()
													+ "headimage.jpg")) {
										downflag = true;
									}
								}
							}
							if (downflag) {
								mHandler.sendEmptyMessage(1);
							}
							mHandler.sendEmptyMessage(4);
						}
					}).start();
				} catch (Exception e) {
					showflagone = false;
					new AisinBuildDialog(mContext, "提示",
							"查询失败,请稍后再试!");
				}
				break;
			case 13:
				showflagone = false;
				new AisinBuildDialog(mContext, "搜索结果", "未找到匹配的环宇用户!");
				break;
			default:
				showflagone = false;
				new AisinBuildDialog(mContext, "提示",
						"网络不可用,请检查网络连接!");
				break;
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& keyCode == KeyEvent.KEYCODE_ENTER) {
			if (Checkinput()) {
				prd.show();
				new HttpTask_checkfriend().execute(searchtext.getText()
						.toString());
			}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null) {
			String rawResult = data.getStringExtra("rawResult");
			// 对返回值进行判断
			if (rawResult == null) {
				new AisinBuildDialog(mContext, "提示", "二维码不正确!");
			} else {
				if (rawResult.startsWith("http:")) {
					SharedPreferences shared_SNSshare = SharedPreferencesTools
							.getSharedPreferences_msglist_date_share(mContext);
					String invite_url = shared_SNSshare.getString(
							SharedPreferencesTools.SPF_msglist_date_INVITE_URL,
							"");
					try {
						if (rawResult.startsWith(invite_url.split("\\?")[0])) {
							Map<String, String> map = HttpUtils
									.URLRequest(rawResult);
							String phone = map.get("phone");
							if (phone != null) {
								if (Checkinput(phone)) {
									showflagone = true;
									prd.show();
									new HttpTask_checkfriend().execute(phone);
								}
							} else {
								Intent uppdate = new Intent(
										"android.intent.action.VIEW",
										Uri.parse(rawResult));
								mContext.startActivity(uppdate);
							}
						} else {
							Intent uppdate = new Intent(
									"android.intent.action.VIEW",
									Uri.parse(rawResult));
							mContext.startActivity(uppdate);
						}
					} catch (Exception e) {
						new AisinBuildDialog(mContext, "提示",
								"二维码不正确!");
					}

				} else {
					new AisinBuildDialog(mContext, "提示", "二维码不正确!");
				}
			}
		}
	}

	private class addfriendBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Constants.BrandName + ".addfriend")) {
				// 上传好友 添加好友
				String uid = intent.getStringExtra("uid");
				Contact ctttemp = null;
				for (Contact c : contacts) {
					if (c.getUid().equals(uid)) {
						ctttemp = c;
						break;
					}
				}
				if (ctttemp != null) {
					final Contact ctttemp_f = ctttemp;
					new Thread(new Runnable() {
						@Override
						public void run() {
							boolean upflag = UPFrends4.Commitdata_one(
									mContext, ctttemp_f);
							if (upflag) {
								UPFrends4.addFriend_one(mContext,
										ctttemp_f);
								ctttemp_f.setMyfriend(true);
								mHandler.sendEmptyMessage(2);
							} else {
								mHandler.sendEmptyMessage(3);
							}
						}
					}).start();
				} else {
					mHandler.sendEmptyMessage(3);
				}
			} else if (intent.getAction().equals(
					Constants.BrandName + ".addfriend.noupmails")) {
				// 添加好友
				String uid = intent.getStringExtra("uid");
				Contact ctttemp = null;
				for (Contact c : contacts) {
					if (c.getUid().equals(uid)) {
						ctttemp = c;
						break;
					}
				}
				if (ctttemp != null) {
					UPFrends4.addFriend_one(mContext, ctttemp);
					ctttemp.setMyfriend(true);
					mHandler.sendEmptyMessage(1);
					mContext.sendBroadcast(new Intent(
							Constants.BrandName + ".mailist.updata"));
				}
			}
		}
	}

}
