package org.aisin.sipphone.nearbystores;

import java.util.ArrayList;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.commong.GimageInfo;
import org.aisin.sipphone.dial.GImagePagerAdapter;
import org.aisin.sipphone.tools.GetGImageMap;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tianyu.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsPageActivity extends Activity implements OnClickListener {
	private LinearLayout detailspageactivity_linlayout;
	private ImageView detailspageal_bank;
	private TextView details_name;
	private TextView detailts_tothis;
	private TextView detailts_address;
	private TextView detailts_telephone;
	private ViewPager detailts_ggtp;
	private ArrayList<GimageInfo> imagelist;// 广告图片集合信息
	// 现在的坐标
	double st_latitude;
	double st_longitude;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 调整广告展示viewpager的展示页
				if (detailts_ggtp != null && imagelist != null) {
					int cid = detailts_ggtp.getCurrentItem();
					if (cid == imagelist.size() - 1) {
						// 如果展示的是最后一个页面的 回滚到第一个页面
						for (int i = imagelist.size() - 2; i >= 0; i--) {
							detailts_ggtp.setCurrentItem(i);
						}
					} else {
						// 回滚到下一个页面
						cid = detailts_ggtp.getCurrentItem() + 1;
						detailts_ggtp.setCurrentItem(cid);
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailspageactivitylayout);
		detailspageactivity_linlayout = (LinearLayout) this
				.findViewById(R.id.detailspageactivity_linlayout);
		detailspageal_bank = (ImageView) this
				.findViewById(R.id.detailspageal_bank);
		detailspageal_bank.setOnClickListener(this);
		details_name = (TextView) this.findViewById(R.id.details_name);
		detailts_tothis = (TextView) this.findViewById(R.id.detailts_tothis);
		detailts_address = (TextView) this.findViewById(R.id.detailts_address);
		detailts_telephone = (TextView) this
				.findViewById(R.id.detailts_telephone);
		detailts_ggtp = (ViewPager) this.findViewById(R.id.detailts_ggtp);
		final String name = this.getIntent().getStringExtra(
				"DetailsPageActivity.name");
		String address = this.getIntent().getStringExtra(
				"DetailsPageActivity.address");
		String telephone = this.getIntent().getStringExtra(
				"DetailsPageActivity.telephone");
		final double latitude = this.getIntent().getDoubleExtra(
				"DetailsPageActivity.latitude", 0);
		final double longitude = this.getIntent().getDoubleExtra(
				"DetailsPageActivity.longitude", 0);
		st_latitude = getIntent()
				.getDoubleExtra("RoutePlanDemo.st_latitude", 0);
		st_longitude = getIntent().getDoubleExtra("RoutePlanDemo.st_longitude",
				0);
		Log.i("环宇", st_latitude + ":" + st_longitude + ":" + latitude + ":"
				+ longitude);
		details_name.setText(name);
		detailts_address.setText("地址: " + address);
		if (telephone == null) {
			telephone = "";
		}
		detailts_telephone.setText("电话: " + telephone);
		if (!"".equals(telephone)) {
			final String telephone_temp = telephone;
			detailts_telephone.setTextColor(Color.BLACK);
			detailts_telephone.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CallPhoneManage.callPhone(DetailsPageActivity.this, null,
							"", telephone_temp);
				}
			});
		}
		if (latitude != 0f && longitude != 0f) {
			detailts_tothis.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 开启地图描点
					Intent intent = new Intent(DetailsPageActivity.this,
							org.aisin.sipphone.nearbystores.DisplayInMap.class);
					intent.putExtra("DisplayInMap.name", name);
					intent.putExtra("DisplayInMap.latitude", latitude);
					intent.putExtra("DisplayInMap.longitude", longitude);
					intent.putExtra("RoutePlanDemo.st_latitude", st_latitude);
					intent.putExtra("RoutePlanDemo.st_longitude", st_longitude);
					DetailsPageActivity.this.startActivity(intent);
				}
			});
		}

		imagelist = GetGImageMap.getImageMap(this, false);
		if (imagelist != null) {
			GImagePagerAdapter gpa = new GImagePagerAdapter(this, imagelist,
					false, false);
			detailts_ggtp.setAdapter(gpa);
			// 开启线程 循环广告图片
			new Thread(new Runnable() {
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
			}).start();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.detailspageal_bank:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(detailspageactivity_linlayout);// 回收容
	}

}
