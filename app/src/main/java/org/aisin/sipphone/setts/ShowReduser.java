package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ShowReduser extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private ImageView myqrcode_back;
	private RelativeLayout showreduser_kouling;
	private RelativeLayout showreduser_geren;
	private RelativeLayout showreduser_qun;
	private RelativeLayout showreduser_zensong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showreduser);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		myqrcode_back = (ImageView) this.findViewById(R.id.myqrcode_back);
		showreduser_kouling = (RelativeLayout) this
				.findViewById(R.id.showreduser_kouling);
		showreduser_geren = (RelativeLayout) this
				.findViewById(R.id.showreduser_geren);
		showreduser_qun = (RelativeLayout) this
				.findViewById(R.id.showreduser_qun);
		showreduser_zensong = (RelativeLayout) this
				.findViewById(R.id.showreduser_zensong);
		myqrcode_back.setOnClickListener(this);
		showreduser_kouling.setOnClickListener(this);
		showreduser_geren.setOnClickListener(this);
		showreduser_qun.setOnClickListener(this);
		showreduser_zensong.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(ShowReduser.this,
				org.aisin.sipphone.setts.ShowRedUserTextView.class);
		switch (id) {
		case R.id.myqrcode_back:
			finish();
			return;
		case R.id.showreduser_kouling:
			intent.putExtra("showkey", "kouling");
			break;
		case R.id.showreduser_geren:
			intent.putExtra("showkey", "geren");
			break;
		case R.id.showreduser_qun:
			intent.putExtra("showkey", "qun");
			break;
		case R.id.showreduser_zensong:
			intent.putExtra("showkey", "zensong");
			break;
		}
		ShowReduser.this.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容
	}
}
