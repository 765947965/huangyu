package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SPD_qianming extends Activity implements TextWatcher {
	private LinearLayout removelayout;
	private EditText spd_cn_inputqianming;
	private String signature;
	private TextView tjsum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spd_changqianming);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		spd_cn_inputqianming = (EditText) this
				.findViewById(R.id.spd_cn_inputqianming);
		spd_cn_inputqianming.addTextChangedListener(this);
		tjsum = (TextView) this.findViewById(R.id.tjsum);
		signature = this.getIntent().getStringExtra("signature");
		if (signature != null && !"".equals(signature)
				&& !"null".equals(signature)) {
			spd_cn_inputqianming.setText(signature);
			spd_cn_inputqianming.setSelection(signature.length());
		}
	}

	public void viewonclick(View view) {
		if (view.getId() == R.id.setts_selfpd_cqianming_back) {
			saveddata();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 针对按返回键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			saveddata();
			return true;
		} else {
			return false;
		}
	}

	private void saveddata() {
		String temp = spd_cn_inputqianming.getText().toString();
		Intent intent = new Intent(Constants.BrandName + ".spdrbt.qianming");
		intent.putExtra("signature", temp);
		SPD_qianming.this.sendBroadcast(intent);
		SPD_qianming.this.finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		String temp = s.toString();
		tjsum.setText(30 - temp.length() + "");
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}
}
