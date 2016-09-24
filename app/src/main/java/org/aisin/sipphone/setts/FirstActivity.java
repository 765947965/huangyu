package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class FirstActivity extends Activity {
	@ViewInject(R.id.removelayout)
	private LinearLayout removelayout;
	@ViewInject(R.id.setts_recharge_bar_back)
	private ImageView setts_recharge_bar_back;
	@ViewInject(R.id.setts_fr1_relayout)
	private RelativeLayout setts_fr1_relayout;
	@ViewInject(R.id.setts_fr2_relayout)
	private RelativeLayout setts_fr2_relayout;
	@ViewInject(R.id.setts_fr3_relayout)
	private RelativeLayout setts_fr3_relayout;
	@ViewInject(R.id.setts_fr4_relayout)
	private RelativeLayout setts_fr4_relayout;
	@ViewInject(R.id.setts_fr5_relayout)
	private RelativeLayout setts_fr5_relayout;
	@ViewInject(R.id.setts_fr6_relayout)
	private RelativeLayout setts_fr6_relayout;
	@ViewInject(R.id.imageview_fr1)
	private ImageView imageview_fr1;
	@ViewInject(R.id.imageview_fr2)
	private ImageView imageview_fr2;
	@ViewInject(R.id.imageview_fr3)
	private ImageView imageview_fr3;
	@ViewInject(R.id.imageview_fr4)
	private ImageView imageview_fr4;
	@ViewInject(R.id.imageview_fr5)
	private ImageView imageview_fr5;
	@ViewInject(R.id.imageview_fr6)
	private ImageView imageview_fr6;

	private SharedPreferences spf;
	private int FNUM = 1;// 当前选中页
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fisrtylayout);
		intent = this.getIntent();
		ViewUtils.inject(this);

		spf = SharedPreferencesTools.getSharedPreferences_4FIRSTY(this);
		if (!Constants.showbaidumap) {
			if (spf.getInt(SharedPreferencesTools.firstyhome, 1) == 6) {
				spf.edit().putInt(SharedPreferencesTools.firstyhome, 1)
						.commit();
			}
			setts_fr6_relayout.setVisibility(View.GONE);
		}

		setItem(spf.getInt(SharedPreferencesTools.firstyhome, 1));
	}
	
	private void ResultClose(){
		if(intent!=null){
			setResult(3, intent);
		}
		finish();
	}

	@OnClick({ R.id.setts_recharge_bar_back, R.id.setts_fr1_relayout,
			R.id.setts_fr2_relayout, R.id.setts_fr3_relayout,
			R.id.setts_fr4_relayout, R.id.setts_fr5_relayout,
			R.id.setts_fr6_relayout })
	public void LayoutClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.setts_recharge_bar_back:
			ResultClose();
			break;
		case R.id.setts_fr1_relayout:
			setItem(1);
			break;
		case R.id.setts_fr2_relayout:
			setItem(2);
			break;
		case R.id.setts_fr3_relayout:
			setItem(3);
			break;
		case R.id.setts_fr4_relayout:
			setItem(4);
			break;
		case R.id.setts_fr5_relayout:
			setItem(5);
			break;
		case R.id.setts_fr6_relayout:
			setItem(6);
			break;
		}
	}

	private void setItem(int FNUM_T) {
		switch (FNUM) {
		case 1:
			imageview_fr1.setVisibility(View.INVISIBLE);
			break;
		case 2:
			imageview_fr2.setVisibility(View.INVISIBLE);
			break;
		case 3:
			imageview_fr3.setVisibility(View.INVISIBLE);
			break;
		case 4:
			imageview_fr4.setVisibility(View.INVISIBLE);
			break;
		case 5:
			imageview_fr5.setVisibility(View.INVISIBLE);
			break;
		case 6:
			imageview_fr6.setVisibility(View.INVISIBLE);
			break;
		}
		switch (FNUM_T) {
		case 1:
			imageview_fr1.setVisibility(View.VISIBLE);
			break;
		case 2:
			imageview_fr2.setVisibility(View.VISIBLE);
			break;
		case 3:
			imageview_fr3.setVisibility(View.VISIBLE);
			break;
		case 4:
			imageview_fr4.setVisibility(View.VISIBLE);
			break;
		case 5:
			imageview_fr5.setVisibility(View.VISIBLE);
			break;
		case 6:
			imageview_fr6.setVisibility(View.VISIBLE);
			break;
		}
		FNUM = FNUM_T;
		spf.edit().putInt(SharedPreferencesTools.firstyhome, FNUM_T).commit();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}

}
