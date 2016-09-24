package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SetKeyboardMusic extends Activity implements OnClickListener,
		OnItemClickListener {
	private LinearLayout removelayout;
	private ImageView setts_recharge_bar_back;
	private ListView listview;
	private ArrayList<String> musicnames = new ArrayList<String>();
	private SetKeyboardMusicAdapter adapter;
	private String checkmusicname;
	private Intent intent;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (adapter == null) {
					adapter = new SetKeyboardMusicAdapter(
							SetKeyboardMusic.this, musicnames);
					listview.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		intent = this.getIntent();
		setContentView(R.layout.setkeyboardmusic);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		setts_recharge_bar_back = (ImageView) this
				.findViewById(R.id.setts_recharge_bar_back);
		listview = (ListView) this.findViewById(R.id.listview);
		setts_recharge_bar_back.setOnClickListener(this);
		musicnames.add("无");
		try {
			String[] strs = this.getAssets().list(Constants.musicp);
			for (String name : strs) {
				musicnames.add(name);
			}
			mHandler.sendEmptyMessage(1);
		} catch (Exception e) {
		}
		checkmusicname = SharedPreferencesTools.getSharedPreferences_4KEYMUSIC(
				this).getString(SharedPreferencesTools.KEYMUSIC_KEY, "无");
		listview.setOnItemClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (adapter != null) {
				adapter.setisplayover();
			}
			if (intent != null) {
				intent.putExtra("rawResult", checkmusicname);
				setResult(1, intent);
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.setts_recharge_bar_back) {
			if (adapter != null) {
				adapter.setisplayover();
			}
			if (intent != null) {
				intent.putExtra("rawResult", checkmusicname);
				setResult(1, intent);
				finish();
			}
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String musicname = musicnames.get(position);
		if (musicname.equals(checkmusicname)) {
			return;
		}
		checkmusicname = musicname;
		SharedPreferencesTools
				.getSharedPreferences_4KEYMUSIC(SetKeyboardMusic.this).edit()
				.putString(SharedPreferencesTools.KEYMUSIC_KEY, musicname)
				.commit();
		SetKeyboardMusic.this.sendBroadcast(new Intent(Constants.BrandName
				+ ".keyboardmusic"));
		mHandler.sendEmptyMessage(1);
	}

}
