package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonRedSendSucess extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private TextView successtitlename;
	private TextView numforsuccess;
	private TextView pdsuccessbt;

	private String redname;
	private int rednum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		redname = this.getIntent().getStringExtra("redname");
		rednum = this.getIntent().getIntExtra("rednum", 1);
		if (redname == null || "".equals(redname)) {
			finish();
			return;
		}
		setContentView(R.layout.personredsendsucess);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		successtitlename = (TextView) this.findViewById(R.id.successtitlename);
		numforsuccess = (TextView) this.findViewById(R.id.numforsuccess);
		pdsuccessbt = (TextView) this.findViewById(R.id.pdsuccessbt);
		successtitlename.setText(redname);
		numforsuccess.setText(rednum + "个红包已发出");
		pdsuccessbt.setOnClickListener(this);
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
		if (v.getId() == R.id.pdsuccessbt) {
			finish();
		}
	}
}
