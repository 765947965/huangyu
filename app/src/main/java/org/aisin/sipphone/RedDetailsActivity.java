package org.aisin.sipphone;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.dial.GImagePagerAdapter;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.setts.RedDetailsActivity_Send;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.BitMapcreatPath2smallSize;
import org.aisin.sipphone.tools.CharTools;
import org.aisin.sipphone.tools.CursorTools;
import org.aisin.sipphone.tools.DisplayUtil;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedDetailsActivity extends Activity implements
		OnPageChangeListener, OnClickListener {
	private ViewPager find_ggtp;
	private GImagePagerAdapter gpa;
	private Thread mythread;
	private LinearLayout lineviewGroup;
	private ImageView[] images;
	private int numu;// 记录上一个选中的页
	private boolean ggFlag = true;
	private String fromnickname;
	private String from;
	private String name;
	private String tips;
	private String gift_type;
	private String sender_gift_id;
	private String sub_type;
	private String award_money;
	private String money_type;
	private String fee_rate;
	private byte[] bitmapByte;
	private Bitmap bitmapheaimage;

	private LinearLayout rddtsloutline;
	private TextView reddllclosebt;// 关闭按钮
	private TextView reddetails_namete_typetext;// 红包种类名称 五一红包、每日登陆红包、现金券等
	private CircleImageView reddetails_from_iamge;// 发来红包方的头像
	private TextView reddetails_from_nametext;// 发来红包方的名称
	private AlwaysMarqueeTextView reddetails_from_tips;
	private TextView reddetails_from_money;
	private TextView reddetails_from_money_dj;
	private TextView reddetails_from_money_time;
	private TextView showathorinfo;
	private ImageView lingimage;
	private ImageView qunimage;
	private ImageView money_typeimage;
	private ArrayList<GimageInfo> ginfos = new ArrayList<GimageInfo>();

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
					gpa = new GImagePagerAdapter(RedDetailsActivity.this, ginfos,
							true, false);
					find_ggtp.setAdapter(gpa);
				} else {
					gpa.notifyDataSetChanged();
				}
				images = new ImageView[ginfos.size()];
				ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
						30, 20);
				for (int i = 0; i < ginfos.size(); i++) {
					ImageView mViewPagerImageView = new ImageView(
							RedDetailsActivity.this);
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
									if (ggFlag) {
										mHandler.sendEmptyMessage(1);
									}
								} catch (Exception e) {
								}
							}
						}
					});
					mythread.start();
				}

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reddetailslayout);
		// 获取数据
		from = getIntent().getStringExtra("from");
		fromnickname = getIntent().getStringExtra("fromnickname");
		name = this.getIntent().getStringExtra("name");
		tips = this.getIntent().getStringExtra("tips");
		gift_type = this.getIntent().getStringExtra("gift_type");
		award_money = this.getIntent().getStringExtra("award_money");
		fee_rate = this.getIntent().getStringExtra("fee_rate");
		bitmapByte = this.getIntent().getByteArrayExtra("bitmapByte");
		sender_gift_id = this.getIntent().getStringExtra("sender_gift_id");
		sub_type = this.getIntent().getStringExtra("sub_type");
		money_type = this.getIntent().getStringExtra("money_type");
		if (gift_type == null || award_money == null) {
			finish();
			return;
		}

		rddtsloutline = (LinearLayout) this.findViewById(R.id.rddtsloutline);
		reddllclosebt = (TextView) this.findViewById(R.id.reddllclosebt);
		reddllclosebt.setOnClickListener(this);
		reddetails_namete_typetext = (TextView) this
				.findViewById(R.id.reddetails_namete_typetext);
		reddetails_from_iamge = (CircleImageView) this
				.findViewById(R.id.reddetails_from_iamge);
		reddetails_from_nametext = (TextView) this
				.findViewById(R.id.reddetails_from_nametext);
		qunimage = (ImageView) this.findViewById(R.id.qunimage);
		lingimage = (ImageView) this.findViewById(R.id.lingimage);
		money_typeimage = (ImageView) this.findViewById(R.id.money_typeimage);
		reddetails_from_tips = (AlwaysMarqueeTextView) this
				.findViewById(R.id.reddetails_from_tips);
		reddetails_from_money = (TextView) this
				.findViewById(R.id.reddetails_from_money);
		reddetails_from_money_dj = (TextView) this
				.findViewById(R.id.reddetails_from_money_dj);
		reddetails_from_money_time = (TextView) this
				.findViewById(R.id.reddetails_from_money_time);
		showathorinfo = (TextView) this.findViewById(R.id.showathorinfo);
		showathorinfo.setOnClickListener(this);
		reddetails_from_tips.setOnClickListener(this);
		if (name != null) {
			// 设置红包种类名称
			reddetails_namete_typetext.setText(name);
		}
		if (sender_gift_id != null && !"".equals(sender_gift_id)) {
			showathorinfo.setVisibility(View.VISIBLE);
		}
		if ("personnocommand".equals(sub_type)) {
		} else if ("personwithcommand".equals(sub_type)) {
			lingimage.setVisibility(View.VISIBLE);
		} else if ("groupnocommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
		} else if ("groupwithcommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
			lingimage.setVisibility(View.VISIBLE);
		}
		if ("0".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_0);
			money_typeimage.setVisibility(View.VISIBLE);
		} else if ("1".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_1);
			money_typeimage.setVisibility(View.VISIBLE);
		}
		// 设置发红包方名称
		if (fromnickname != null && !"".equals(fromnickname.trim())) {
			reddetails_from_nametext.setText(fromnickname.trim() + "的" + name);
		} else {
			if (from != null && !"".equals(from.trim())) {
				if ("system".equals(from.trim())) {
					reddetails_from_nametext.setText("环宇的" + name);
				} else {
					// 匹配通讯录或者UID
					boolean tempf = true;
					// 匹配通讯录
					for (Long lg : CursorTools.cttmap.keySet()) {
						Contact ctt = CursorTools.cttmap.get(lg);
						if (ctt == null) {
							continue;
						}
						for (String strp : ctt.getPhonesList()) {
							if (from.trim().equals(strp.trim())) {
								if (ctt.getRemark() != null
										&& !"".equals(ctt.getRemark())) {
									reddetails_from_nametext.setText(ctt
											.getRemark() + "的" + name);
									tempf = false;// 成功匹配到 置为假
									break;
								}
							}
						}
					}
					// 匹配UID
					// 都没有匹配成功直接设置号码
					if (tempf) {
						// 没有成功匹配到
						reddetails_from_nametext.setText(from.trim() + "的"
								+ name);
					}
				}
			}
		}
		// 设置红包详情
		reddetails_from_tips.setText(tips);
		int t_sp = (int) (CharTools.getStringLength(tips) * 18);
		int t_px = DisplayUtil.sp2px(this, t_sp);
		if (t_px >= PhoneInfo.width) {
			reddetails_from_tips.setTextColor(Color.parseColor("#00BFFF"));
		}

		// 设置金额及使用详情
		if ("logindaily".equals(gift_type.trim())
				|| gift_type.trim().endsWith("_money")) {
			// 环宇每日登录红包或者金钱红包
			double money_temp = Double.parseDouble(award_money.trim())
					/ (double) 100;
			Double d = Double.parseDouble(fee_rate.trim());
			int times_temp = (int) (money_temp * 100 / d);
			reddetails_from_money.setText(money_temp + "");
			reddetails_from_money_dj.setText("元");
			reddetails_from_money_time.setText("已存入钱包");
		} else if (gift_type.trim().endsWith("_month")) {
			// 赠送的天红包
			reddetails_from_money.setText(award_money.trim());
			reddetails_from_money_dj.setText("天");
			reddetails_from_money_time.setText("已存入钱包");
		} else if (gift_type.trim().endsWith("_4gdata")) {
			double money_temp = Double.parseDouble(award_money.trim());
			if (money_temp > 1024) {
				reddetails_from_money.setText(Math
						.round((money_temp / (double) 1024) * 10) / 10.0 + "");
				reddetails_from_money_dj.setText("MB");
			} else {
				reddetails_from_money.setText(money_temp + "");
				reddetails_from_money_dj.setText("KB");
			}
			reddetails_from_money_time.setText("已存入钱包");
		} else if (gift_type.trim().endsWith("_right")) {
			double money_temp = (double) Integer.parseInt(award_money.trim())
					/ (double) 100;
			reddetails_from_money.setText(money_temp + "");
			reddetails_from_money_dj.setText("元");
			reddetails_from_money_time.setText("已存入钱包");
		}
		find_ggtp = (ViewPager) this.findViewById(R.id.reddetails_ggtp);
		lineviewGroup = (LinearLayout) this.findViewById(R.id.lineviewGroup);
		find_ggtp.setOnPageChangeListener(this);
		mHandler.sendEmptyMessage(2);
		// 设置发红包方的头像
		if (from != null && !"".equals(from)) {
			File file = this.getFileStreamPath(from + "headimage.jpg");
			if (file.exists()) {
				bitmapheaimage = BitmapFactory.decodeFile(file
						.getAbsolutePath());
				if (bitmapheaimage != null) {
					reddetails_from_iamge.setImageBitmap(bitmapheaimage);
				}
			} else {
				if (bitmapByte != null) {
					bitmapheaimage = BitmapFactory.decodeByteArray(bitmapByte,
							0, bitmapByte.length);
					if (bitmapheaimage != null) {
						reddetails_from_iamge.setImageBitmap(bitmapheaimage);
					}
				}
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		LayoutParams lp = find_ggtp.getLayoutParams();
		try {
			GimageInfo gi = ginfos.get(0);
			lp.height = (int) (((float) gi.getbmpH() / (float) gi.getbmpW()) * find_ggtp
					.getWidth());
			find_ggtp.setLayoutParams(lp);
		} catch (Exception e) {
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.reddllclosebt) {
			finish();
		} else if (v.getId() == R.id.reddetails_from_tips) {
			Intent intent = new Intent(RedDetailsActivity.this,
					org.aisin.sipphone.setts.RedDlTextShow.class);
			intent.putExtra("tips", tips);
			RedDetailsActivity.this.startActivity(intent);
		} else if (v.getId() == R.id.showathorinfo) {
			Intent intents = new Intent(RedDetailsActivity.this,
					org.aisin.sipphone.setts.ShowRedRecevedInfo.class);
			intents.putExtra("gift_id", sender_gift_id);
			RedDetailsActivity.this.startActivity(intents);
		}
	}

	@Override
	protected void onResume() {
		ggFlag = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		ggFlag = false;
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmapheaimage != null) {
			bitmapheaimage.recycle();
			bitmapheaimage = null;
		}
		if (bitmapByte != null) {
			bitmapByte = null;
		}
		RecoveryTools.unbindDrawables(rddtsloutline);// 回收容器
	}

}
