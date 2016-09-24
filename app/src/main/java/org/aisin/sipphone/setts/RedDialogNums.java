package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedDialogNums extends Activity implements OnClickListener {

	private String nums;

	private LinearLayout reddialog_linlayout;
	private TextView numstext;
	private TextView sendfromname_messages_text;
	private ImageView red_anim_image;
	private ImageView red_closedbt;

	private View reddiloag_top;
	private View reddiloag_botton;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				Animation anim = AnimationUtils.loadAnimation(
						RedDialogNums.this, R.anim.anim_2);
				reddialog_linlayout.startAnimation(anim);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mHandler.sendEmptyMessage(2);
					}
				}, 160);
			} else if (msg.what == 2) {
				RedDialogNums.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		nums = this.getIntent().getStringExtra("nums");
		setContentView(R.layout.reddialognums);
		numstext = (TextView) this.findViewById(R.id.numstext);
		sendfromname_messages_text = (TextView) this
				.findViewById(R.id.sendfromname_messages_text);
		red_anim_image = (ImageView) this.findViewById(R.id.red_anim_image);
		red_closedbt = (ImageView) this.findViewById(R.id.red_closedbt);
		reddiloag_top = this.findViewById(R.id.reddiloag_top);
		reddiloag_botton = this.findViewById(R.id.reddiloag_botton);
		reddialog_linlayout = (LinearLayout) this
				.findViewById(R.id.reddialog_linlayout);
		reddiloag_top.setOnClickListener(this);
		reddiloag_botton.setOnClickListener(this);
		red_closedbt.setOnClickListener(this);
		numstext.setText(nums);
		sendfromname_messages_text.setText("你有" + nums + "个未拆红包");
		red_anim_image.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.red_anim_image) {
			// 启动红包列表
			Intent intent = new Intent(RedDialogNums.this,
					org.aisin.sipphone.setts.RedListActivity.class);
			RedDialogNums.this.startActivity(intent);
			RedDialogNums.this.finish();
		} else if (v.getId() == R.id.reddiloag_top
				|| v.getId() == R.id.reddiloag_botton
				|| v.getId() == R.id.red_closedbt) {
			mHandler.sendEmptyMessage(1);
		}
	}

}
