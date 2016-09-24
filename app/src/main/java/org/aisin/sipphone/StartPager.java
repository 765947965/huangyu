package org.aisin.sipphone;

import java.io.File;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartPager extends Activity {
	private RelativeLayout removelinlayout;
	private ImageView image;
	private TextView startptg;
	private Bitmap bitmap;
	private boolean startflag = true;
	private String to;
	private int showtime;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				startptg.setVisibility(View.GONE);
				startAisinActivity();
			} else if (msg.what == 2) {
				if (showtime < 0) {
					if (startflag) {
						mHandler.sendEmptyMessage(1);
					}
				} else {
					startptg.setText(" " + showtime + " ");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startpager);
		removelinlayout = (RelativeLayout) this
				.findViewById(R.id.removelinlayout);
		image = (ImageView) this.findViewById(R.id.image);
		startptg = (TextView) this.findViewById(R.id.startptg);
		String strtemp = SharedPreferencesTools
				.getSharedPreferences_4startpager(this).getString(
						SharedPreferencesTools.startpager_data, "");
		try {
			JSONObject json = new JSONObject(strtemp);
			int doresult = json.getInt("result");
			if (doresult == 0) {
				File file = StartPager.this
						.getFileStreamPath("start_pager.jpg");
				if (!file.exists()) {
					// 如果图片不存在
					mHandler.sendEmptyMessage(1);
					return;
				}
				bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
				if (bitmap == null) {
					// 图片文件存在 而图片创建失败，删除图片文件，以备重新下载
					file.delete();
					// 如果图片不存在
					mHandler.sendEmptyMessage(1);
					return;
				}
				image.setImageBitmap(bitmap);
				// 为image添加点击事件
				to = json.optString("to");
				if (to != null && !"".equals(to.trim())) {
					image.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							startflag = false;
							mHandler.sendEmptyMessage(1);
						}
					});
				}
				// 为跳过按钮添加点击事件
				startptg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mHandler.sendEmptyMessage(1);
					}
				});
				// 开启倒计时进入主ACTIVITY
				showtime = json.optInt("show_duration", 3);
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (showtime >= -1) {
							mHandler.sendEmptyMessage(2);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}
							showtime -= 1;
						}
					}
				}).start();
			} else {
				mHandler.sendEmptyMessage(1);
			}
		} catch (Exception e) {
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		RecoveryTools.unbindDrawables(removelinlayout);// 回收容器
	}

	private synchronized void startAisinActivity() {
		// 启动主Activity
		Intent intent = new Intent(StartPager.this,
				org.aisin.sipphone.AisinActivity.class);
		if (!startflag) {
			intent.putExtra("startpager_to", to);
		}
		StartPager.this.startActivity(intent);
		StartPager.this.overridePendingTransition(R.anim.aisinactivityinput,
				R.anim.startpageroutput);
		StartPager.this.finish();
	}
}
