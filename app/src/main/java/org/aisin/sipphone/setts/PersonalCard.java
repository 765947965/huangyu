package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonalCard extends Activity implements OnFocusChangeListener,
		TextWatcher, OnClickListener {

	private String province;// 省份
	private String city;// 城市
	private String company;// 公司
	private String profession;// 职位
	private String school;// 学校

	private LinearLayout removelayout;
	private ImageView setts_selfpd_back;
	private TextView spd_addresstext;
	private EditText sdp_companytext;
	private TextView tjsum_comp;
	private EditText sdp_professiontext;
	private TextView tjsum_profession;
	private EditText sdp_schooltext;
	private TextView tjsum_school;

	private pcardBroadcast pbroad;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				spd_addresstext.setText(province + "-" + city);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personalcard);
		province = this.getIntent().getStringExtra("province");
		city = this.getIntent().getStringExtra("city");
		company = this.getIntent().getStringExtra("company");
		profession = this.getIntent().getStringExtra("profession");
		school = this.getIntent().getStringExtra("school");

		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		setts_selfpd_back = (ImageView) this
				.findViewById(R.id.setts_selfpd_back);
		setts_selfpd_back.setOnClickListener(this);
		spd_addresstext = (TextView) this.findViewById(R.id.spd_addresstext);
		sdp_companytext = (EditText) this.findViewById(R.id.sdp_companytext);
		tjsum_comp = (TextView) this.findViewById(R.id.tjsum_comp);
		sdp_professiontext = (EditText) this
				.findViewById(R.id.sdp_professiontext);
		tjsum_profession = (TextView) this.findViewById(R.id.tjsum_profession);
		sdp_schooltext = (EditText) this.findViewById(R.id.sdp_schooltext);
		tjsum_school = (TextView) this.findViewById(R.id.tjsum_school);
		sdp_companytext.setOnFocusChangeListener(this);
		sdp_professiontext.setOnFocusChangeListener(this);
		sdp_schooltext.setOnFocusChangeListener(this);
		sdp_companytext.addTextChangedListener(this);
		sdp_professiontext.addTextChangedListener(this);
		sdp_schooltext.addTextChangedListener(this);

		if (province != null && !"".equals(province)
				&& !"null".equals(province) && city != null && !"".equals(city)
				&& !"null".equals(city)) {
			spd_addresstext.setText(province + "-" + city);
		}
		if (company != null && !"".equals(company) && !"null".equals(company)) {
			sdp_companytext.setText(company);
		}
		if (profession != null && !"".equals(profession)
				&& !"null".equals(profession)) {
			sdp_professiontext.setText(profession);
		}
		if (school != null && !"".equals(school) && !"null".equals(school)) {
			sdp_schooltext.setText(school);
		}

		// 注册监听信息改变
		pbroad = new pcardBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.BrandName + ".pbroad.cityoled");
		registerReceiver(pbroad, filter);
	}

	public void OnRelativeLayoutClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.spd_addressrl:// 地区选择
			Intent intent = new Intent(PersonalCard.this,
					org.aisin.sipphone.setts.ProvinceListview.class);
			PersonalCard.this.startActivity(intent);
			break;

		default:
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		int id = v.getId();
		switch (id) {
		case R.id.sdp_companytext:
			if (hasFocus) {
				tjsum_comp.setVisibility(View.VISIBLE);
			} else {
				tjsum_comp.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.sdp_professiontext:
			if (hasFocus) {
				tjsum_profession.setVisibility(View.VISIBLE);
			} else {
				tjsum_profession.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.sdp_schooltext:
			if (hasFocus) {
				tjsum_school.setVisibility(View.VISIBLE);
			} else {
				tjsum_school.setVisibility(View.INVISIBLE);
			}
			break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String sdp_companytext_temp = sdp_companytext.getText().toString();
		String sdp_professiontext_temp = sdp_professiontext.getText()
				.toString();
		String sdp_schooltext_temp = sdp_schooltext.getText().toString();
		tjsum_comp.setText(20 - sdp_companytext_temp.length() + "");
		tjsum_profession.setText(20 - sdp_professiontext_temp.length() + "");
		tjsum_school.setText(20 - sdp_schooltext_temp.length() + "");
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pbroad != null) {
			unregisterReceiver(pbroad);
		}
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}

	// 接收更新事物
	private class pcardBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			province = intent.getStringExtra("province");
			city = intent.getStringExtra("city");
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setts_selfpd_back) {
			savedata();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			savedata();
			return true;
		} else {
			return false;
		}
	}

	private void savedata() {
		// 保存数据
		Intent intent = new Intent(Constants.BrandName + ".spdrbt.personalcard");
		if (province != null) {
			intent.putExtra("province", province);
		}
		if (city != null) {
			intent.putExtra("city", city);
		}
		String temp_company = sdp_companytext.getText().toString();
		intent.putExtra("company", temp_company);
		String temp_profession = sdp_professiontext.getText().toString();
		intent.putExtra("profession", temp_profession);
		String temp_school = sdp_schooltext.getText().toString();
		intent.putExtra("school", temp_school);
		PersonalCard.this.sendBroadcast(intent);
		PersonalCard.this.finish();
	}
}
