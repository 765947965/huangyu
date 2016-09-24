package org.aisin.sipphone;

import java.io.File;
import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.FileManager;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BootPage extends Activity implements OnPageChangeListener {

	private MediaPlayer mp;

	private ViewPager viewpager;
	private LinearLayout lineviewGroup;
	private ArrayList<Integer> ids;
	private ImageView[] images;
	private int numu;// 记录上一个选中的页
	private String flag;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (flag != null && "close".equals(flag)) {
					BootPage.this.finish();
					return;
				}
				// 启动主Activity
				Intent intent = new Intent(BootPage.this,
						org.aisin.sipphone.AisinActivity.class);
				BootPage.this.startActivity(intent);
				BootPage.this.overridePendingTransition(
						R.anim.aisinactivityinput, R.anim.startpageroutput);
				BootPage.this.finish();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bootpagelayout);
		viewpager = (ViewPager) this.findViewById(R.id.viewpager);
		lineviewGroup = (LinearLayout) this.findViewById(R.id.lineviewGroup);
		ids = new ArrayList<Integer>();
		ids.add(R.drawable.bootpage1);
		ids.add(R.drawable.bootpage2);
		ids.add(R.drawable.bootpage3);
		ids.add(R.drawable.bootpage4);
		ids.add(R.drawable.bootpage5);
		flag = this.getIntent().getStringExtra("flag");
		BootPageAdapter adapter = new BootPageAdapter(BootPage.this, ids,
				handler);
		viewpager.setAdapter(adapter);
		viewpager.setOnPageChangeListener(this);
		images = new ImageView[ids.size()];
		ViewGroup.LayoutParams viewPagerImageViewParams = new ViewGroup.LayoutParams(
				30, 20);
		for (int i = 0; i < ids.size(); i++) {
			ImageView mViewPagerImageView = new ImageView(BootPage.this);
			mViewPagerImageView.setLayoutParams(viewPagerImageViewParams);
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
		// 下载开屏页(非新版本特性)
		if (!(flag != null && "close".equals(flag))) {
			new HttpTask_start_page(BootPage.this, null).execute("");
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mp = MediaPlayer
							.create(BootPage.this, R.raw.robertschumann);
					mp.setLooping(true);
					mp.start();
				} catch (Exception e) {
				}
			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (mp != null) {
			mp.stop();
			mp.release();
			mp = null;
		}
		super.onDestroy();
		System.exit(0);
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
}
