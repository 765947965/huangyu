package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CkeckOutRedErroreInfo extends Activity implements OnClickListener {

	private String erroreinfo;
	private String erroreinfo_2;
	private String erroreinfo_3;
	private String gift_id;
	private String from;
	private String fromnickname;
	private String tips;
	private String money_type;

	private LinearLayout removelayout;
	private TextView erroeinfotext;
	private TextView erroeinfotext_2;
	private TextView erroeinfotext_3;
	private TextView showredrelayou_bt;
	private TextView reddllclosebt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		erroreinfo = this.getIntent().getStringExtra("erroreinfo");
		erroreinfo_2 = this.getIntent().getStringExtra("erroreinfo_2");
		erroreinfo_3 = this.getIntent().getStringExtra("erroreinfo_3");
		gift_id = this.getIntent().getStringExtra("gift_id");
		from = this.getIntent().getStringExtra("from");
		fromnickname = this.getIntent().getStringExtra("fromnickname");
		tips = this.getIntent().getStringExtra("tips");
		money_type = this.getIntent().getStringExtra("money_type");
		setContentView(R.layout.ckeckoutrederroreinfo);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		erroeinfotext = (TextView) this.findViewById(R.id.erroeinfotext);
		erroeinfotext_2 = (TextView) this.findViewById(R.id.erroeinfotext_2);
		erroeinfotext_3 = (TextView) this.findViewById(R.id.erroeinfotext_3);
		reddllclosebt = (TextView) this.findViewById(R.id.reddllclosebt);
		reddllclosebt.setOnClickListener(this);
		showredrelayou_bt = (TextView) this
				.findViewById(R.id.showredrelayou_bt);
		erroeinfotext.setText(erroreinfo);
		if (erroreinfo_2 != null && !"".equals(erroreinfo_2)) {
			erroeinfotext_2.setText(erroreinfo_2);
		}
		if (erroreinfo_3 != null && !"".equals(erroreinfo_3)) {
			erroeinfotext_3.setText(erroreinfo_3);
		}

		showredrelayou_bt.setOnClickListener(this);

		if (gift_id != null && !"".equals(gift_id)) {
			erroeinfotext_2.setText("看看TA们的手气");
			erroeinfotext_2.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.showredrelayou_bt
				|| v.getId() == R.id.reddllclosebt) {
			finish();
		} else if (v.getId() == R.id.erroeinfotext_2) {
			// 展示红包领取详情
			Intent intent = new Intent(CkeckOutRedErroreInfo.this,
					org.aisin.sipphone.setts.ShowRedRecevedInfo.class);
			intent.putExtra("gift_id", gift_id);
			intent.putExtra("from", from);
			intent.putExtra("fromnickname", fromnickname);
			intent.putExtra("tips", tips);
			intent.putExtra("money_type", money_type);
			CkeckOutRedErroreInfo.this.startActivity(intent);
			CkeckOutRedErroreInfo.this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}
}
