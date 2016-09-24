package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class RedDlTextShow extends Activity {
	private TextView reddltexts_t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reddltextshow);
		reddltexts_t = (TextView) this.findViewById(R.id.reddltexts_t);
		String tips = getIntent().getStringExtra("tips");
		if (tips == null || "".equals(tips)) {
			finish();
			return;
		}
		reddltexts_t.setText(tips);
		reddltexts_t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RedDlTextShow.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(reddltexts_t);// 回收容
	}
}
