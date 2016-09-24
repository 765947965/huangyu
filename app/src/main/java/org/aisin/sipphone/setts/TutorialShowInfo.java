package org.aisin.sipphone.setts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.GetTutorialShowMaps;
import org.aisin.sipphone.tools.RecoveryTools;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TutorialShowInfo extends Activity {
	@ViewInject(R.id.removelayout)
	private LinearLayout removelayout;
	@ViewInject(R.id.setts_changepassword_back)
	private ImageView setts_changepassword_back;
	@ViewInject(R.id.showtexttitle)
	private TextView showtexttitle;
	@ViewInject(R.id.listview)
	private ListView listview;

	private String showflag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		showflag = this.getIntent().getStringExtra("showflag");
		if (showflag == null || "".equals(showflag)) {
			finish();
			return;
		}
		setContentView(R.layout.tutorialshowinfo);

		ViewUtils.inject(this);

		if ("samsung".equals(showflag)) {
			showtexttitle.setText("三星教程");
		} else if ("htc".equals(showflag)) {
			showtexttitle.setText("HTC教程");
		} else if ("lenovo".equals(showflag)) {
			showtexttitle.setText("联想教程");
		} else if ("meizu".equals(showflag)) {
			showtexttitle.setText("魅族教程");
		} else if ("xiaomi".equals(showflag)) {
			showtexttitle.setText("小米教程");
		} else if ("huawei".equals(showflag)) {
			showtexttitle.setText("华为教程");
		} else if ("vivo".equals(showflag)) {
			showtexttitle.setText("VIVO教程");
		} else if ("tencent".equals(showflag)) {
			showtexttitle.setText("腾讯手机管家教程");
		} else if ("gj360".equals(showflag)) {
			showtexttitle.setText("360手机管家教程");
		}

		String[] titletexts = GetTutorialShowMaps.TutorialTitles(showflag);
		Integer[] imageids = GetTutorialShowMaps.TutorialImagesID(showflag);
		if (titletexts != null && imageids != null
				&& titletexts.length == imageids.length) {
			List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < titletexts.length; i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("itemtext", titletexts[i]);
				map.put("itemimage", imageids[i]);
				listdata.add(map);
			}
			SimpleAdapter adapter = new SimpleAdapter(TutorialShowInfo.this,
					listdata, R.layout.tutorialshowinfoadapter, new String[] {
							"itemtext", "itemimage" }, new int[] {
							R.id.texttitle, R.id.imageitem });
			listview.setAdapter(adapter);
		}

	}

	@OnClick(R.id.setts_changepassword_back)
	public void MyClick(View view) {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}
}
