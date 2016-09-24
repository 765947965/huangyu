package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.BitmapUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowSetKeyback extends Activity implements OnClickListener {
	private LinearLayout removelayout;
	private TextView banckkey;
	private TextView banckkey1;
	private TextView button;
	private View top;
	private View butoom;
	private boolean zdyflag;

	private String filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		filename = this.getIntent().getStringExtra("filename");
		zdyflag = this.getIntent().getBooleanExtra("zdyflag", false);
		if (filename == null || "".equals(filename)) {
			finish();
			return;
		}
		setContentView(R.layout.showsetkeyback);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		banckkey = (TextView) this.findViewById(R.id.banckkey);
		banckkey1 = (TextView) this.findViewById(R.id.banckkey1);
		button = (TextView) this.findViewById(R.id.button);
		top = this.findViewById(R.id.top);
		butoom = this.findViewById(R.id.butoom);
		button.setOnClickListener(this);
		top.setOnClickListener(this);
		butoom.setOnClickListener(this);
		BitmapUtils bitmapUtils = new BitmapUtils(this);
		if (zdyflag) {
			bitmapUtils.display(banckkey, filename);
		} else {
			bitmapUtils.display(banckkey, "assets/" + Constants.KEYBACK + "/"
					+ filename);
		}
		bitmapUtils.display(banckkey1, "assets/" + Constants.KEYBACK + "/"
				+ Constants.KEYBACK_WRIGHT);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button) {
			// 设置背景
			SharedPreferencesTools
					.getSharedPreferences_4keybackground(ShowSetKeyback.this)
					.edit().putString(SharedPreferencesTools.KBG_KEY, filename)
					.putBoolean(SharedPreferencesTools.KBG_ZDY, zdyflag)
					.commit();
			ShowSetKeyback.this.sendBroadcast(new Intent(Constants.BrandName
					+ ".keybackUP"));
			ShowSetKeyback.this.finish();
		} else if (id == R.id.top || id == R.id.butoom) {
			finish();
		}
	}
}
