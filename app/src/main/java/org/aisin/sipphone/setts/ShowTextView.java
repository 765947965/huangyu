package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowTextView extends Activity implements OnClickListener {
	private LinearLayout showtextview_linlayout;
	private ImageView setting_showtextview_back;
	private TextView setting_showtextview_text;
	private TextView showtextview_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showtextview);
		showtextview_linlayout = (LinearLayout) this
				.findViewById(R.id.showtextview_linlayout);
		setting_showtextview_back = (ImageView) this
				.findViewById(R.id.setting_showtextview_back);
		setting_showtextview_text = (TextView) this
				.findViewById(R.id.setting_showtextview_text);
		showtextview_text = (TextView) this
				.findViewById(R.id.showtextview_text);
		setting_showtextview_back.setOnClickListener(this);
		String title_name = getIntent().getStringExtra("title_name");
		String show_text = getIntent().getStringExtra("show_text");
		if (title_name != null && show_text != null) {
			setting_showtextview_text.setText(title_name);
			showtextview_text.setText(show_text);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setting_showtextview_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(showtextview_linlayout);// 回收容
	}

}
