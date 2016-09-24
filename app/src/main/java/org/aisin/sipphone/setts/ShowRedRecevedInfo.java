package org.aisin.sipphone.setts;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.tools.BitMapcreatPath2smallSize;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShowRedRecevedInfo extends Activity implements OnClickListener {
	private String gift_id;
	private String showflag;
	private RedObject redobject;

	private Bitmap fromheadimage;

	private LinearLayout removelayout;
	private TextView reddetails_from_nametext;// 来自谁的红包
	private ImageView lingimage;// 令
	private ImageView qunimage;// 群
	private TextView tipstext;
	private CircleImageView reddetails_from_iamge;// 头像
	private RelativeLayout zjerelayout; // 金额
	private TextView moneytext;
	private TextView moneytext_dj;
	private TextView statustext;// 红包状态
	private ImageView money_typeimage;// 是否拼手气红包 废弃
	private TextView reddllclosebt;// 关闭按钮
	private ImageView redlistviewr_i;// 刷新按钮
	private TextView infored;

	private ProgressDialog prd;

	private ListView listview;

	private ShowRedRecevedInfoAdapter sria;
	private int tap = 0;// 列表金钱格式化方式 0金钱 1天 2流量
	private int tk = 0;// 0 不显示感谢语 1显示感谢语

	private ArrayList<TreeMap<String, Object>> maps = new ArrayList<TreeMap<String, Object>>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				reddetails_from_nametext.setText("来自"
						+ redobject.getFromnickname() + "的红包");
				if ("0".equals(redobject.getMoney_type())) {
					money_typeimage.setImageResource(R.drawable.moneytype_0);
				} else if ("1".equals(redobject.getMoney_type())) {
					money_typeimage.setImageResource(R.drawable.moneytype_1);
				}
				tipstext.setText(redobject.getTips());
				File file = ShowRedRecevedInfo.this.getFileStreamPath(redobject
						.getFrom() + "headimage.jpg");
				if (file != null) {
					fromheadimage = BitmapFactory.decodeFile(file
							.getAbsolutePath());
					if (fromheadimage != null) {
						reddetails_from_iamge.setImageBitmap(fromheadimage);
					}
				}

				if ("logindaily".equals(redobject.getType().trim())
						|| redobject.getType().trim().endsWith("_money")
						|| redobject.getType().trim().endsWith("_right")) {
					tap = 0;
					// 环宇每日登录红包或者金钱红包
					double money_temp = Double
							.parseDouble(redobject.getMoney()) / (double) 100;
					double received_money_d = Double.parseDouble(redobject
							.getReceived_money()) / (double) 100;
					infored.setText("共领取" + received_money_d + "元话费,"
							+ redobject.getHas_open() + "/"
							+ redobject.getSplitsnumber() + "个");
					if (redobject.getType().trim().endsWith("_right")) {
						infored.setText("共领取" + received_money_d + "元特权红包,"
								+ redobject.getHas_open() + "/"
								+ redobject.getSplitsnumber() + "个");
					}
					moneytext.setText(money_temp + "");
					moneytext_dj.setText("元");
				} else if (redobject.getType().trim().endsWith("_month")) {
					tap = 1;
					infored.setText("共领取" + redobject.getReceived_money()
							+ "天通话时长," + redobject.getHas_open() + "/"
							+ redobject.getSplitsnumber() + "个");
					moneytext.setText(redobject.getMoney());
					moneytext_dj.setText("天");
				} else if (redobject.getType().trim().endsWith("_4gdata")) {
					tap = 2;
					if (Integer.parseInt(redobject.getMoney().trim()) > 1024) {
						infored.setText("共领取"
								+ Math.round((Integer.parseInt(redobject
										.getReceived_money().trim()) / (double) 1024) * 10)
								/ 10.0 + "MB流量," + redobject.getHas_open()
								+ "/" + redobject.getSplitsnumber() + "个");
						moneytext
								.setText(Math.round((Integer.parseInt(redobject
										.getMoney().trim()) / (double) 1024) * 10)
										/ 10.0 + "");
						moneytext_dj.setText("MB");
					} else {
						infored.setText("共领取" + redobject.getReceived_money()
								+ "KB流量," + redobject.getHas_open() + "/"
								+ redobject.getSplitsnumber() + "个");
						moneytext.setText(redobject.getMoney());
						moneytext_dj.setText("KB");
					}
				}
				new HttpTask_CkeckRedInfo().execute("");
			} else if (msg.what == 2) {
				if (sria == null) {
					sria = new ShowRedRecevedInfoAdapter(
							ShowRedRecevedInfo.this, maps, tap, tk);
					listview.setAdapter(sria);
				} else {
					sria.notifyDataSetChanged();
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (TreeMap<String, Object> map : maps) {
							File file = ShowRedRecevedInfo.this
									.getFileStreamPath((String) map.get("uid")
											+ "headimage.jpg");
							boolean ttp = false;
							if (file.exists()) {
								Bitmap bt = BitMapcreatPath2smallSize
										.GetBitmap4Path2samll(file
												.getAbsolutePath());
								if (bt != null) {
									map.put("headimage", bt);
									ttp = true;
								}
							}
							if (!ttp) {
								String picture = (String) (map.get("picture"));
								if (!"".equals(picture)) {
									boolean downallflag = HttpUtils
											.downloadImage(
													ShowRedRecevedInfo.this,
													picture,
													(String) map.get("uid")
															+ "headimage.jpg");
									if (downallflag) {
										File filen = ShowRedRecevedInfo.this
												.getFileStreamPath((String) map
														.get("uid")
														+ "headimage.jpg");
										if (file.exists()) {
											Bitmap btt = BitMapcreatPath2smallSize
													.GetBitmap4Path2samll(filen
															.getAbsolutePath());
											if (btt != null) {
												map.put("headimage", btt);
											}
										}
									}
								}
							}
						}
						mHandler.sendEmptyMessage(3);
					}
				}).start();
			} else if (msg.what == 3) {
				if (sria == null) {
					sria = new ShowRedRecevedInfoAdapter(
							ShowRedRecevedInfo.this, maps, tap, tk);
					listview.setAdapter(sria);
				} else {
					sria.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		gift_id = this.getIntent().getStringExtra("gift_id");
		showflag = this.getIntent().getStringExtra("showflag");
		if (gift_id == null || "".equals(gift_id)) {
			this.finish();
			return;
		}
		setContentView(R.layout.showredrecevedinfo);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		reddetails_from_nametext = (TextView) this
				.findViewById(R.id.reddetails_from_nametext);
		lingimage = (ImageView) this.findViewById(R.id.lingimage);
		qunimage = (ImageView) this.findViewById(R.id.qunimage);
		zjerelayout = (RelativeLayout) this.findViewById(R.id.zjerelayout);
		reddetails_from_iamge = (CircleImageView) this
				.findViewById(R.id.reddetails_from_iamge);
		statustext = (TextView) this.findViewById(R.id.statustext);
		moneytext = (TextView) this.findViewById(R.id.moneytext);
		moneytext_dj = (TextView) this.findViewById(R.id.moneytext_dj);
		money_typeimage = (ImageView) this.findViewById(R.id.money_typeimage);
		reddllclosebt = (TextView) this.findViewById(R.id.reddllclosebt);
		reddllclosebt.setOnClickListener(this);
		tipstext = (TextView) this.findViewById(R.id.tipstext);
		redlistviewr_i = (ImageView) this.findViewById(R.id.redlistviewr_i);
		redlistviewr_i.setOnClickListener(this);
		infored = (TextView) this.findViewById(R.id.infored);
		listview = (ListView) this.findViewById(R.id.listview);
		if ("sended".equals(showflag)) {
			zjerelayout.setVisibility(View.VISIBLE);
		}

		// 查询红包信息
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在加载,请稍候...");
		prd.show();
		new HttpTask_CkeckRedCode().execute("");
	}

	// 查询红包信息
	class HttpTask_CkeckRedInfo extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = URLTools.GetHttpURL_4RedTAInfo(
					ShowRedRecevedInfo.this, gift_id);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
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
					JSONArray jsa = json
							.getJSONArray("received_user_gift_info");
					maps.clear();
					for (int i = 0; i < jsa.length(); i++) {
						JSONObject jffb = jsa.getJSONObject(i);
						String picture = jffb.optString("picture");
						String open_time = jffb.optString("open_time");
						String mobileNumber = jffb.optString("mobileNumber");
						String money = jffb.optString("money", "0");
						String name = jffb.optString("name");
						String thankyou = jffb.optString("thankyou");
						String uid = jffb.optString("uid");
						TreeMap<String, Object> maptemp = new TreeMap<String, Object>();
						maptemp.put("picture", picture);
						maptemp.put("open_time", open_time);
						maptemp.put("mobileNumber", mobileNumber);
						maptemp.put("money", money);
						maptemp.put("name", name);
						maptemp.put("thankyou", thankyou);
						maptemp.put("uid", uid);
						maps.add(maptemp);
					}
					mHandler.sendEmptyMessage(2);
				} catch (JSONException e) {
					new AisinBuildDialog(ShowRedRecevedInfo.this, "提示",
							"联网失败，请检查您的网络信号！");
				}
				break;
			default:
				new AisinBuildDialog(ShowRedRecevedInfo.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}
		}
	}

	// 获取单个红包信息
	class HttpTask_CkeckRedCode extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = URLTools.GetHttpURL_4getoneRed(
					ShowRedRecevedInfo.this, gift_id);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
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
				try {
					JSONObject jsz = json.getJSONArray("gifts")
							.getJSONObject(0);
					RedObject redobjecttemp = new RedObject();
					redobjecttemp
							.setSplitsnumber(jsz.optString("splitsnumber"));
					redobjecttemp.setShake_ratio(jsz.optString("shake_ratio"));
					redobjecttemp.setReceived_money(jsz.optString(
							"received_money", "0"));
					redobjecttemp.setReturned_money(jsz.optString(
							"returned_money", "0"));
					redobjecttemp.setStatus(jsz.optString("status"));
					redobjecttemp.setSub_type(jsz.optString("sub_type"));
					redobjecttemp.setCommand(jsz.optString("command"));
					redobjecttemp.setFrom(jsz.optString("from"));
					redobjecttemp.setOpen_time(jsz.optString("open_time"));
					redobjecttemp.setGift_id(jsz.optString("gift_id"));
					redobjecttemp.setMoney(jsz.optString("money", "0"));
					String has_open_str = jsz.optString("has_open");
					if (has_open_str != null && !"".equals(has_open_str)) {
						redobjecttemp.setHas_open(Integer.parseInt(has_open_str
								.trim()));
					} else {
						redobjecttemp.setHas_open(0);
					}
					redobjecttemp.setDirect(jsz.optString("direct"));
					redobjecttemp.setCreate_time(jsz.optString("create_time"));
					redobjecttemp.setFrom_phone(jsz.optString("from_phone"));
					redobjecttemp
							.setFromnickname(jsz.optString("fromnickname"));
					redobjecttemp.setMoney_type(jsz.optString("money_type"));
					redobjecttemp.setTips(jsz.optString("tips"));
					redobjecttemp.setExp_time(jsz.optString("exp_time"));
					redobjecttemp.setType(jsz.optString("type"));
					redobjecttemp.setSender_gift_id(jsz
							.optString("sender_gift_id"));
					redobjecttemp.setName(jsz.optString("name"));
					redobject = redobjecttemp;
					if (UserInfo_db.getUserInfo(ShowRedRecevedInfo.this)
							.getUid().equals(redobjecttemp.getFrom())) {
						tk = 1;
					}
					mHandler.sendEmptyMessage(1);
				} catch (Exception e) {
					if (prd != null) {
						prd.dismiss();
					}
					new AisinBuildDialog(ShowRedRecevedInfo.this, "提示",
							"联网失败，请检查您的网络信号！");
				}
				break;
			default:
				if (prd != null) {
					prd.dismiss();
				}
				new AisinBuildDialog(ShowRedRecevedInfo.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (fromheadimage != null) {
			fromheadimage.recycle();
			fromheadimage = null;
		}
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.reddllclosebt:
			finish();
			break;

		case R.id.redlistviewr_i:
			prd.setMessage("正在刷新,请稍候...");
			prd.show();
			new HttpTask_CkeckRedCode().execute("");
			break;
		}
	}
}
