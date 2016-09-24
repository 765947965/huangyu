package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SetWallet extends Activity implements OnClickListener {
	private LinearLayout setwallet_linlayout;
	private ImageView setwallet_bar_back;
	private RelativeLayout setts_account_balance_relayout;
	private RelativeLayout setts_account_water_relayout;
	private RelativeLayout setts_single_query_relayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setwallet);
		setwallet_linlayout = (LinearLayout) this
				.findViewById(R.id.setwallet_linlayout);
		setwallet_bar_back = (ImageView) this
				.findViewById(R.id.setwallet_bar_back);
		setts_account_balance_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_account_balance_relayout);
		setts_account_water_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_account_water_relayout);
		setts_single_query_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_single_query_relayout);
		setwallet_bar_back.setOnClickListener(this);
		setts_account_balance_relayout.setOnClickListener(this);
		setts_account_water_relayout.setOnClickListener(this);
		setts_single_query_relayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		UserInfo userinfo = UserInfo_db.getUserInfo(SetWallet.this);
		switch (id) {
		case R.id.setwallet_bar_back:
			finish();
			break;
		case R.id.setts_account_balance_relayout:
			Intent intent = new Intent(SetWallet.this,
					org.aisin.sipphone.setts.AccountQueryActivity.class);
			startActivity(intent);
			break;
		case R.id.setts_account_water_relayout:
			Intent intent2 = new Intent(SetWallet.this,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent2.putExtra("flag_name", "账户流水");
			intent2.putExtra("url_view",
					HttpUtils.LS + "?phone=" + userinfo.getPhone() + "&uid="
							+ userinfo.getUid());
			startActivity(intent2);
			break;
		case R.id.setts_single_query_relayout:
			Intent intent3 = new Intent(SetWallet.this,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent3.putExtra("flag_name", "话单查询");
			intent3.putExtra("url_view",
					HttpUtils.HD + "?phone=" + userinfo.getPhone() + "&uid="
							+ userinfo.getUid());
			startActivity(intent3);
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(setwallet_linlayout);// 回收容器
	}

}
