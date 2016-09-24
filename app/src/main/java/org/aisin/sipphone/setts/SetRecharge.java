package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SetRecharge extends Activity implements OnClickListener {

	private LinearLayout setrecharge_linlayout;
	private ImageView setts_recharge_bar_back;
	private RelativeLayout setts_recharge_yd_relayout;
	private RelativeLayout setts_recharge_lt_relayout;
	private RelativeLayout setts_recharge_dx_relayout;
	private RelativeLayout setts_recharge_as_relayout;
	private RelativeLayout setts_recharge_zfb_relayout;
	private View viewline;
	private RelativeLayout setts_recharge_wxzf_relayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setrecharge);
		setrecharge_linlayout = (LinearLayout) this
				.findViewById(R.id.setrecharge_linlayout);
		setts_recharge_bar_back = (ImageView) this
				.findViewById(R.id.setts_recharge_bar_back);
		setts_recharge_yd_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_yd_relayout);
		setts_recharge_lt_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_lt_relayout);
		setts_recharge_dx_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_dx_relayout);
		setts_recharge_as_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_as_relayout);
		setts_recharge_zfb_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_zfb_relayout);
		viewline = this.findViewById(R.id.viewline);
		setts_recharge_wxzf_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_wxzf_relayout);
		setts_recharge_bar_back.setOnClickListener(this);
		setts_recharge_yd_relayout.setOnClickListener(this);
		setts_recharge_lt_relayout.setOnClickListener(this);
		setts_recharge_dx_relayout.setOnClickListener(this);
		setts_recharge_as_relayout.setOnClickListener(this);
		setts_recharge_zfb_relayout.setOnClickListener(this);
		setts_recharge_wxzf_relayout.setOnClickListener(this);
		if (Constants.WXZF) {
			viewline.setVisibility(View.VISIBLE);
			setts_recharge_wxzf_relayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(SetRecharge.this,
				org.aisin.sipphone.setts.MobileCardRecharge.class);
		switch (id) {
		case R.id.setts_recharge_bar_back:
			finish();
			break;
		case R.id.setts_recharge_yd_relayout:
			intent.putExtra("setting_mcr_back_text", "移动卡充值");
			startActivity(intent);
			break;
		case R.id.setts_recharge_lt_relayout:
			intent.putExtra("setting_mcr_back_text", "联通卡充值");
			startActivity(intent);
			break;
		case R.id.setts_recharge_dx_relayout:
			intent.putExtra("setting_mcr_back_text", "电信卡充值");
			startActivity(intent);
			break;
		case R.id.setts_recharge_as_relayout:
			intent.putExtra("setting_mcr_back_text", "环宇卡充值");
			startActivity(intent);
			break;
		case R.id.setts_recharge_zfb_relayout:
			Intent intent2 = new Intent(SetRecharge.this,
					org.aisin.sipphone.aipay.MyPayActivity.class);
			intent2.putExtra("RechargeFlag", "ZFB");
			startActivity(intent2);
			break;
		case R.id.setts_recharge_wxzf_relayout:
			/*
			 * Intent intent3 = new Intent(SetRecharge.this,
			 * org.aisin.sipphone.aipay.MyPayActivity.class);
			 * intent3.putExtra("RechargeFlag", "WX"); startActivity(intent3);
			 */
			ShowDialog();
			break;
		}
	}

	private void ShowDialog() {
		// TODO Auto-generated method stub
		if (isFinishing()) {
			return;
		}
		AlertDialog builder = new AlertDialog.Builder(this).create();
		View view = LayoutInflater.from(this).inflate(R.layout.imageshowdialog,
				null);
		builder.setView(view, 0, 0, 0, 0);
		builder.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(setrecharge_linlayout);// 回收容器
	}

}
