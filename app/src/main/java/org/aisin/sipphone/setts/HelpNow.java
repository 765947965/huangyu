package org.aisin.sipphone.setts;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class HelpNow extends Activity {

	@ViewInject(R.id.removelayout)
	private LinearLayout removelayout;
	@ViewInject(R.id.setaboutaisin_bar_back)
	private ImageView setaboutaisin_bar_back;
	@ViewInject(R.id.setts_bzzx_relayout)
	private RelativeLayout setts_bzzx_relayout;
	@ViewInject(R.id.setts_qxbz_relayout)
	private RelativeLayout setts_qxbz_relayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.helpnow);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.setaboutaisin_bar_back, R.id.setts_bzzx_relayout,
			R.id.setts_qxbz_relayout })
	public void ImageClick(View view) {
		int id = view.getId();
		Intent intent = null;
		switch (id) {
		case R.id.setaboutaisin_bar_back:
			finish();
			return;
		case R.id.setts_bzzx_relayout:// 无额外费用 账户统一 去电显示
			/*
			 * intent = new Intent(HelpNow.this,
			 * org.aisin.sipphone.setts.ShowRedUserTextView.class);
			 * intent.putExtra("showkey", "bzzx"); startActivity(intent);
			 */
			Intent intent2 = new Intent(HelpNow.this,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent2.putExtra("flag_name", "使用说明");
			intent2.putExtra("url_view", "http://m.zjtytx.com:8060/about.html");
			startActivity(intent2);
			break;
		case R.id.setts_qxbz_relayout:
			intent = new Intent(HelpNow.this,
					org.aisin.sipphone.setts.TutorialActivity.class);
			startActivity(intent);
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}
}
