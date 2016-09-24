package org.aisin.sipphone.setts;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.aisin.sipphone.AlwaysMarqueeTextView;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.dial.GImagePagerAdapter;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.tools.CharTools;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.DisplayUtil;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedDetailsActivity_Send extends Activity implements
		OnClickListener, OnPageChangeListener {

	private ViewPager find_ggtp;
	private GImagePagerAdapter gpa;
	private Thread mythread;
	private LinearLayout lineviewGroup;
	private ImageView[] images;
	private int numu;// 记录上一个选中的页
	private SimpleDateFormat sdformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdformat_2 = new SimpleDateFormat(
			"yyyy年MM月dd日-HH:mm");

	private RedObject robct;
	private Bitmap bitmapheaimage;

	private LinearLayout rddtsloutline;
	private TextView reddllclosebt;// 关闭按钮
	private TextView reddetails_namete_typetext;// 红包种类名称 五一红包、每日登陆红包、现金券等
	private CircleImageView reddetails_from_iamge;// 发来红包方的头像
	private TextView reddetails_from_nametext;// 发来红包方的名称
	private TextView reddetails_from_money_time;// 是否继续召唤朋友
	private TextView textinfosendred;
	private AlwaysMarqueeTextView reddetails_from_tips;
	private TextView reddetails_from_money;
	private TextView reddetails_from_money_dj;
	private ArrayList<GimageInfo> ginfos = new ArrayList<GimageInfo>();
	private ImageView money_typeimage;
	private TextView statustext;
	private ImageView redlistviewr_i;// 刷新按钮
	private ImageView qunimage;
	private ImageView lingimage;
	private TextView showathorinfo;

	private ProgressDialog prd;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 调整广告展示viewpager的展示页
				if (find_ggtp != null && gpa != null
						&& GetGImageMap.imagelist.size() > 1) {
					int cid = find_ggtp.getCurrentItem();
					if (cid == GetGImageMap.imagelist.size() - 1) {
						// 如果展示的是最后一个页面的 回滚到第一个页面
						for (int i = GetGImageMap.imagelist.size() - 2; i >= 0; i--) {
							find_ggtp.setCurrentItem(i);
						}
					} else {
						// 回滚到下一个页面
						cid = find_ggtp.getCurrentItem() + 1;
						find_ggtp.setCurrentItem(cid);
					}
				}
			} else if (msg.what == 2) {// 更新广告
				if (GetGImageMap.imagelist == null
						|| GetGImageMap.imagelist.size() == 0) {
					return;
				}
				for (GimageInfo ginfo : GetGImageMap.imagelist) {
					if (ginfo.getBitmap() == null) {
						return;
					}
				}
				ginfos.clear();
				ginfos.addAll(GetGImageMap.imagelist);
				if (gpa == null) {
					gpa = new GImagePagerAdapter(RedDetailsActivity_Send.this,
							ginfos, true, false);
					find_ggtp.setAdapter(gpa);
				} else {
					gpa.notifyDataSetChanged();
				}
				images = new ImageView[ginfos.size()];
				ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
						30, 20);
				for (int i = 0; i < ginfos.size(); i++) {
					ImageView mViewPagerImageView = new ImageView(
							RedDetailsActivity_Send.this);
					mViewPagerImageView
							.setLayoutParams(viewPagerImageViewParams);
					if (i == 0) {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerchanged);
					} else {
						mViewPagerImageView
								.setImageResource(R.drawable.bootpagerunchanged);
					}
					images[i] = mViewPagerImageView;
					lineviewGroup.addView(mViewPagerImageView);
				}
				if (mythread == null) {
					mythread = new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								try {
									Thread.sleep(3000);
									mHandler.sendEmptyMessage(1);
								} catch (Exception e) {
								}
							}
						}
					});
					mythread.start();
				}

			} else if (msg.what == 3) {
				initview();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		robct = (RedObject) this.getIntent().getSerializableExtra("robct");
		if (robct == null) {
			finish();
			return;
		}
		setContentView(R.layout.reddetailsactivity_send);
		rddtsloutline = (LinearLayout) this.findViewById(R.id.rddtsloutline);
		reddllclosebt = (TextView) this.findViewById(R.id.reddllclosebt);
		reddllclosebt.setOnClickListener(this);
		reddetails_namete_typetext = (TextView) this
				.findViewById(R.id.reddetails_namete_typetext);
		reddetails_from_iamge = (CircleImageView) this
				.findViewById(R.id.reddetails_from_iamge);
		reddetails_from_nametext = (TextView) this
				.findViewById(R.id.reddetails_from_nametext);
		textinfosendred = (TextView) this.findViewById(R.id.textinfosendred);
		showathorinfo = (TextView) this.findViewById(R.id.showathorinfo);
		showathorinfo.setOnClickListener(this);
		reddetails_from_tips = (AlwaysMarqueeTextView) this
				.findViewById(R.id.reddetails_from_tips);
		reddetails_from_money = (TextView) this
				.findViewById(R.id.reddetails_from_money);
		reddetails_from_money_dj = (TextView) this
				.findViewById(R.id.reddetails_from_money_dj);
		reddetails_from_money_time = (TextView) this
				.findViewById(R.id.reddetails_from_money_time);
		textinfosendred.setOnClickListener(this);
		reddetails_from_money_time.setOnClickListener(this);
		money_typeimage = (ImageView) this.findViewById(R.id.money_typeimage);
		statustext = (TextView) this.findViewById(R.id.statustext);
		find_ggtp = (ViewPager) this.findViewById(R.id.reddetails_ggtp);
		lineviewGroup = (LinearLayout) this.findViewById(R.id.lineviewGroup);
		redlistviewr_i = (ImageView) this.findViewById(R.id.redlistviewr_i);
		qunimage = (ImageView) this.findViewById(R.id.qunimage);
		lingimage = (ImageView) this.findViewById(R.id.lingimage);
		redlistviewr_i.setOnClickListener(this);
		find_ggtp.setOnPageChangeListener(this);
		reddetails_from_tips.setOnClickListener(this);
		if (robct.getType().startsWith("p2p")) {
			showathorinfo.setVisibility(View.VISIBLE);
		}
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在刷新...");
		mHandler.sendEmptyMessage(2);
		// 初始化文字数据
		initview();

		// 设置发红包方的头像

		File file = this.getFileStreamPath(robct.getFrom() + "headimage.jpg");
		if (file.exists()) {
			bitmapheaimage = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmapheaimage != null) {
				reddetails_from_iamge.setImageBitmap(bitmapheaimage);
			}
		}

	}

	private void initview() {
		if ("personnocommand".equals(robct.getSub_type())) {
			reddetails_from_nametext.setText(robct.getFromnickname() + "的"
					+ robct.getName());
		} else if ("personwithcommand".equals(robct.getSub_type())) {
			reddetails_from_nametext.setText(robct.getFromnickname() + "的"
					+ robct.getName());
			lingimage.setVisibility(View.VISIBLE);
		} else if ("groupnocommand".equals(robct.getSub_type())) {
			reddetails_from_nametext.setText(robct.getFromnickname() + "的"
					+ robct.getName());
			qunimage.setVisibility(View.VISIBLE);
		} else if ("groupwithcommand".equals(robct.getSub_type())) {
			reddetails_from_nametext.setText(robct.getFromnickname() + "的"
					+ robct.getName());
			qunimage.setVisibility(View.VISIBLE);
			lingimage.setVisibility(View.VISIBLE);
		} else {
			reddetails_from_nametext.setText(robct.getFromnickname() + "的"
					+ robct.getName());
		}
		reddetails_namete_typetext.setText(robct.getName());
		reddetails_from_tips.setText(robct.getTips());
		int t_sp = (int) (CharTools.getStringLength(robct.getTips()) * 18);
		int t_px = DisplayUtil.sp2px(this, t_sp);
		if (t_px >= PhoneInfo.width) {
			reddetails_from_tips.setTextColor(Color.parseColor("#00BFFF"));
		}

		if (!"has_ended".equals(robct.getStatus())
				&& ("groupwithcommand".equals(robct.getSub_type()) || "personwithcommand"
						.equals(robct.getSub_type()))) {
			reddetails_from_money_time.setVisibility(View.VISIBLE);
		} else {
			reddetails_from_money_time.setVisibility(View.INVISIBLE);
		}

		try {
			// 设置金额及使用详情
			if ("logindaily".equals(robct.getType().trim())
					|| robct.getType().trim().endsWith("_money")
					|| robct.getType().trim().endsWith("_right")) {
				// 环宇每日登录红包或者金钱红包
				double money_temp = Double.parseDouble(robct.getMoney())
						/ (double) 100;
				reddetails_from_money.setText(money_temp + "");
				reddetails_from_money_dj.setText("元");
				double returned_money_d = Double.parseDouble(robct
						.getReturned_money()) / (double) 100;
				double received_money_d = Double.parseDouble(robct
						.getReceived_money()) / (double) 100;
				if (returned_money_d == 0) {
					textinfosendred.setText(sdformat_2.format(sdformat
							.parse(robct.getCreate_time().trim()))
							+ " 包好 ,已领取"
							+ robct.getHas_open()
							+ "/"
							+ robct.getSplitsnumber()
							+ "个,共"
							+ received_money_d + "元/" + money_temp + "元");
				} else {
					textinfosendred.setText(sdformat_2.format(sdformat
							.parse(robct.getCreate_time().trim()))
							+ " 包好 ,已领取"
							+ robct.getHas_open()
							+ "/"
							+ robct.getSplitsnumber()
							+ "个,共"
							+ received_money_d
							+ "元/"
							+ money_temp
							+ "元,已退回"
							+ returned_money_d + "元");
				}
			} else if (robct.getType().trim().endsWith("_month")) {
				// 赠送的天红包
				reddetails_from_money.setText(robct.getMoney());
				reddetails_from_money_dj.setText("天");
				if ("0".equals(robct.getReturned_money())) {
					textinfosendred.setText(sdformat_2.format(sdformat
							.parse(robct.getCreate_time().trim()))
							+ " 包好 ,已领取"
							+ robct.getHas_open()
							+ "/"
							+ robct.getSplitsnumber()
							+ "个,共"
							+ robct.getReceived_money()
							+ "天/"
							+ robct.getMoney() + "天");
				} else {
					textinfosendred.setText(sdformat_2.format(sdformat
							.parse(robct.getCreate_time().trim()))
							+ " 包好 ,已领取"
							+ robct.getHas_open()
							+ "/"
							+ robct.getSplitsnumber()
							+ "个,共"
							+ robct.getReceived_money()
							+ "天/"
							+ robct.getMoney()
							+ "天,已退回"
							+ robct.getReturned_money() + "天");
				}
			} else if (robct.getType().trim().endsWith("_4gdata")) {
				double money_temp = Double.parseDouble(robct.getMoney());
				if (money_temp > 1024) {
					reddetails_from_money.setText(money_temp / (double) 1024
							+ "");
					reddetails_from_money_dj.setText("MB");
				} else {
					reddetails_from_money.setText(money_temp + "");
					reddetails_from_money_dj.setText("KB");
				}
				if ("0".equals(robct.getReturned_money())) {
					if (Integer.parseInt(robct.getMoney().trim()) > 1024) {
						textinfosendred
								.setText(sdformat_2.format(sdformat.parse(robct
										.getCreate_time().trim()))
										+ " 包好 ,已领取"
										+ robct.getHas_open()
										+ "/"
										+ robct.getSplitsnumber()
										+ "个,共"
										+ Math.round((Integer.parseInt(robct
												.getReceived_money().trim()) / (double) 1024) * 10)
										/ 10.0
										+ "MB/"
										+ Math.round((Integer.parseInt(robct
												.getMoney().trim()) / (double) 1024) * 10)
										/ 10.0 + "MB");
					} else {
						textinfosendred.setText(sdformat_2.format(sdformat
								.parse(robct.getCreate_time().trim()))
								+ " 包好 ,已领取"
								+ robct.getHas_open()
								+ "/"
								+ robct.getSplitsnumber()
								+ "个,共"
								+ robct.getReceived_money()
								+ "KB/"
								+ robct.getMoney() + "KB");
					}
				} else {
					if (Integer.parseInt(robct.getMoney().trim()) > 1024) {
						textinfosendred
								.setText(sdformat_2.format(sdformat.parse(robct
										.getCreate_time().trim()))
										+ " 包好 ,已领取"
										+ robct.getHas_open()
										+ "/"
										+ robct.getSplitsnumber()
										+ "个,共"
										+ Math.round((Integer.parseInt(robct
												.getReceived_money().trim()) / (double) 1024) * 10)
										/ 10.0
										+ "MB/"
										+ Math.round((Integer.parseInt(robct
												.getMoney().trim()) / (double) 1024) * 10)
										/ 10.0
										+ "MB,已退回"
										+ Math.round((Integer.parseInt(robct
												.getReturned_money().trim()) / (double) 1024) * 10)
										/ 10.0 + "MB");
					} else {
						textinfosendred.setText(sdformat_2.format(sdformat
								.parse(robct.getCreate_time().trim()))
								+ " 包好 ,已领取"
								+ robct.getHas_open()
								+ "/"
								+ robct.getSplitsnumber()
								+ "个,共"
								+ robct.getReceived_money()
								+ "KB/"
								+ robct.getMoney()
								+ "KB,已退回"
								+ robct.getReturned_money() + "KB");
					}
				}
			}

			if ("0".equals(robct.getMoney_type())) {
				money_typeimage.setImageResource(R.drawable.moneytype_0);
				money_typeimage.setVisibility(View.VISIBLE);
			} else {
				money_typeimage.setImageResource(R.drawable.moneytype_1);
				money_typeimage.setVisibility(View.VISIBLE);
			}
			if ("has_sended".equals(robct.getStatus())) {
				statustext.setText("领取中...");
			} else if ("has_packed".equals(robct.getStatus())) {
				statustext.setText("已包好");
			} else if ("has_ended".equals(robct.getStatus())) {
				if (robct.getHas_open() == Integer.parseInt(robct
						.getSplitsnumber().trim())) {
					statustext.setText("已领完");
				} else {
					statustext.setText("已过期");
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		try {
			LayoutParams lp = find_ggtp.getLayoutParams();
			GimageInfo gi = ginfos.get(0);
			lp.height = (int) (((float) gi.getbmpH() / (float) gi.getbmpW()) * find_ggtp
					.getWidth());
			find_ggtp.setLayoutParams(lp);
		} catch (Exception e) {
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmapheaimage != null) {
			bitmapheaimage.recycle();
			bitmapheaimage = null;
		}
		if (rddtsloutline != null) {
			RecoveryTools.unbindDrawables(rddtsloutline);// 回收容器
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		int id = v.getId();
		switch (id) {
		case R.id.reddllclosebt:
			finish();
			break;
		case R.id.showathorinfo:
			Intent intents = new Intent(RedDetailsActivity_Send.this,
					org.aisin.sipphone.setts.ShowRedRecevedInfo.class);
			intents.putExtra("gift_id", robct.getGift_id());
			intents.putExtra("showflag", "sended");
			intents.putExtra("from", robct.getFrom());
			intents.putExtra("fromnickname", robct.getFromnickname());
			intents.putExtra("tips", robct.getTips());
			intents.putExtra("money_type", robct.getMoney_type());
			RedDetailsActivity_Send.this.startActivity(intents);
			break;
		case R.id.reddetails_from_money_time:
			if ("has_packed".equals(robct.getStatus())) {
				// 调到设置口令
				if ("groupwithcommand".equals(robct.getSub_type())) {
					Intent intent = new Intent(RedDetailsActivity_Send.this,
							org.aisin.sipphone.setts.SetRedCodeBefore.class);
					intent.putExtra("codetype", "group");
					intent.putExtra("title_text1", "群红包已准备好");
					intent.putExtra("title_text2", "要发群红包请生成红包口令");
					intent.putExtra("sended_gift_id", robct.getGift_id());
					RedDetailsActivity_Send.this.startActivity(intent);
					RedDetailsActivity_Send.this.finish();
				} else if ("personwithcommand".equals(robct.getSub_type())) {
					Intent intent = new Intent(RedDetailsActivity_Send.this,
							org.aisin.sipphone.setts.SetRedCodeBefore.class);
					intent.putExtra("codetype", "personal");
					intent.putExtra("title_text1", "个人红包已准备好");
					intent.putExtra("title_text2", "要发非环宇好友红包请生成红包口令");
					intent.putExtra("sended_gift_id", robct.getGift_id());
					RedDetailsActivity_Send.this.startActivity(intent);
					RedDetailsActivity_Send.this.finish();
				}
			} else if ("has_sended".equals(robct.getStatus())) {
				// 启动设置口令界面
				Intent intent = new Intent(RedDetailsActivity_Send.this,
						org.aisin.sipphone.setts.SetSendRedCode.class);
				intent.putExtra("command", robct.getCommand());
				if (robct.getSub_type().startsWith("person")) {
					intent.putExtra("codetype", "personal");
				}
				intent.putExtra("sended_gift_id", robct.getGift_id());
				RedDetailsActivity_Send.this.startActivity(intent);
				RedDetailsActivity_Send.this.finish();
			}
		case R.id.textinfosendred:// 展开红包领取的详情

			break;
		case R.id.redlistviewr_i:// 刷新详情
			prd.show();
			new HttpTask_CkeckRedCode().execute("");
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		images[numu].setImageResource(R.drawable.bootpagerunchanged);
		images[arg0].setImageResource(R.drawable.bootpagerchanged);
		numu = arg0;
	}

	// 获取单个红包信息
	class HttpTask_CkeckRedCode extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String url = URLTools.GetHttpURL_4getoneRed(
					RedDetailsActivity_Send.this, robct.getGift_id());
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
					JSONObject jsz = json.getJSONArray("gifts")
							.getJSONObject(0);
					RedObject redobject = new RedObject();
					redobject.setSplitsnumber(jsz.optString("splitsnumber"));
					redobject.setShake_ratio(jsz.optString("shake_ratio"));
					redobject.setReceived_money(jsz.optString("received_money",
							"0"));
					redobject.setReturned_money(jsz.optString("returned_money",
							"0"));
					redobject.setStatus(jsz.optString("status"));
					redobject.setSub_type(jsz.optString("sub_type"));
					redobject.setCommand(jsz.optString("command"));
					redobject.setFrom(jsz.optString("from"));
					redobject.setOpen_time(jsz.optString("open_time"));
					redobject.setGift_id(jsz.optString("gift_id"));
					redobject.setMoney(jsz.optString("money", "0"));
					String has_open_str = jsz.optString("has_open");
					if (has_open_str != null && !"".equals(has_open_str)) {
						redobject.setHas_open(Integer.parseInt(has_open_str
								.trim()));
					} else {
						redobject.setHas_open(0);
					}
					redobject.setDirect(jsz.optString("direct"));
					redobject.setCreate_time(jsz.optString("create_time"));
					redobject.setFrom_phone(jsz.optString("from_phone"));
					redobject.setFromnickname(jsz.optString("fromnickname"));
					redobject.setMoney_type(jsz.optString("money_type"));
					redobject.setTips(jsz.optString("tips"));
					redobject.setExp_time(jsz.optString("exp_time"));
					redobject.setType(jsz.optString("type"));
					redobject
							.setSender_gift_id(jsz.optString("sender_gift_id"));
					redobject.setName(jsz.optString("name"));
					robct = redobject;
					RedData_DBHelp.addRedDatas(RedDetailsActivity_Send.this,
							redobject);
					// 刷新列表
					mHandler.sendEmptyMessage(3);
					RedDetailsActivity_Send.this.sendBroadcast(new Intent(
							Constants.BrandName + ".redlisttoup"));
				} catch (Exception e) {
					new AisinBuildDialog(RedDetailsActivity_Send.this, "提示",
							"刷新失败,请稍候重试!");
				}
				break;
			default:
				new AisinBuildDialog(RedDetailsActivity_Send.this, "提示",
						"刷新失败,请稍候重试!");
				break;
			}

		}
	}

}
