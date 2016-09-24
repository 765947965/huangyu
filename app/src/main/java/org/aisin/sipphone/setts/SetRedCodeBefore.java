package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetRedCodeBefore extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private TextView title_cansal;
	private TextView title1;
	private TextView title2;
	private TextView pdsuccessbt;

	private String title_text1;
	private String title_text2;
	private String sended_gift_id;
	private ArrayList<String> phones;

	private String codetype;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		title_text1 = this.getIntent().getStringExtra("title_text1");
		title_text2 = this.getIntent().getStringExtra("title_text2");
		sended_gift_id = this.getIntent().getStringExtra("sended_gift_id");
		codetype = this.getIntent().getStringExtra("codetype");
		phones = this.getIntent().getStringArrayListExtra("phones");
		if (title_text1 == null || "".equals(title_text1)
				|| title_text2 == null || "".equals(title_text2)
				|| sended_gift_id == null || "".equals(sended_gift_id)) {
			finish();
			return;
		}
		setContentView(R.layout.setredcodebefore);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		title_cansal = (TextView) this.findViewById(R.id.title_cansal);
		title1 = (TextView) this.findViewById(R.id.title1);
		title2 = (TextView) this.findViewById(R.id.title2);
		pdsuccessbt = (TextView) this.findViewById(R.id.pdsuccessbt);
		title_cansal.setOnClickListener(this);
		pdsuccessbt.setOnClickListener(this);
		title1.setText(title_text1);
		title2.setText(title_text2);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.title_cansal:
			finish();
			break;
		case R.id.pdsuccessbt:
			// 启动设置口令界面
			Intent intent = new Intent(SetRedCodeBefore.this,
					org.aisin.sipphone.setts.SetSendRedCode.class);
			if (phones != null) {
				intent.putStringArrayListExtra("phones", phones);
			}
			intent.putExtra("codetype", codetype);
			intent.putExtra("sended_gift_id", sended_gift_id);
			SetRedCodeBefore.this.startActivity(intent);
			SetRedCodeBefore.this.finish();
			break;
		}
	}
}
