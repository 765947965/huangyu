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

public class SPD_changeName extends Activity implements TextWatcher {
	private String name;
	private LinearLayout removelayout;
	private EditText spd_cn_inputname;
	private TextView tjsum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spd_changename);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		spd_cn_inputname = (EditText) this.findViewById(R.id.spd_cn_inputname);
		spd_cn_inputname.addTextChangedListener(this);
		tjsum = (TextView) this.findViewById(R.id.tjsum);
		name = this.getIntent().getStringExtra("name");
		if (name != null && !"".equals(name)) {
			spd_cn_inputname.setText(name);
			spd_cn_inputname.setSelection(name.length());
		}
	}

	public void viewonclick(View view) {
		if (view.getId() == R.id.setts_selfpd_cname_back) {
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
		String temptext = spd_cn_inputname.getText().toString();
		// 保存数据
		Intent intent = new Intent(Constants.BrandName + ".spdrbt.name");
		intent.putExtra("name", temptext);
		SPD_changeName.this.sendBroadcast(intent);
		SPD_changeName.this.finish();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String temp = s.toString();
		tjsum.setText(10 - temp.length() + "");
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}
}
