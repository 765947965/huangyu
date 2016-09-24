package org.aisin.sipphone.setts;

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

public class TutorialActivity extends Activity {
	@ViewInject(R.id.removelayout)
	private LinearLayout removelayout;
	@ViewInject(R.id.setts_changepassword_back)
	private ImageView setts_changepassword_back;
	@ViewInject(R.id.samsung)
	private ImageView samsung;
	@ViewInject(R.id.htc)
	private ImageView htc;
	@ViewInject(R.id.lenovo)
	private ImageView lenovo;
	@ViewInject(R.id.meizu)
	private ImageView meizu;
	@ViewInject(R.id.xiaomi)
	private ImageView xiaomi;
	@ViewInject(R.id.huawei)
	private ImageView huawei;
	@ViewInject(R.id.vivo)
	private ImageView vivo;
	@ViewInject(R.id.tencent)
	private ImageView tencent;
	@ViewInject(R.id.gj360)
	private ImageView gj360;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorialactivity);

		ViewUtils.inject(this);
	}

	@OnClick({ R.id.setts_changepassword_back, R.id.samsung, R.id.htc,
			R.id.lenovo, R.id.meizu, R.id.xiaomi, R.id.huawei, R.id.vivo,
			R.id.tencent, R.id.gj360 })
	public void ImageClick(View view) {
		int id = view.getId();
		Intent intent = null;
		switch (id) {
		case R.id.setts_changepassword_back:
			finish();
			return;
		case R.id.samsung:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "samsung");
			break;
		case R.id.htc:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "htc");
			break;
		case R.id.lenovo:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "lenovo");
			break;
		case R.id.meizu:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "meizu");
			break;
		case R.id.xiaomi:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "xiaomi");
			break;
		case R.id.huawei:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "huawei");
			break;
		case R.id.vivo:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "vivo");
			break;
		case R.id.tencent:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "tencent");
			break;
		case R.id.gj360:
			intent = new Intent(TutorialActivity.this,
					org.aisin.sipphone.setts.TutorialShowInfo.class);
			intent.putExtra("showflag", "gj360");
			break;
		}
		if (intent != null) {
			TutorialActivity.this.startActivity(intent);
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
