package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ProvinceListview extends Activity implements OnClickListener {
	private LinearLayout pcvlist;
	private ImageView setts_selfpd_back;
	private ListView provincelisview;
	private ArrayList<String> arraylist = new ArrayList<String>();
	private pcardBroadcast pbroad;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				ProvinceListview.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.provincelistviewlayout);
		pcvlist = (LinearLayout) this.findViewById(R.id.pcvlist);
		setts_selfpd_back = (ImageView) this
				.findViewById(R.id.setts_selfpd_back);
		provincelisview = (ListView) this.findViewById(R.id.provincelisview);
		setts_selfpd_back.setOnClickListener(this);
		try {
			SQLiteDatabase sdbase = SQLiteDatabase.openOrCreateDatabase(
					"/data" + Environment.getDataDirectory().getAbsolutePath()
							+ "/" + this.getPackageName() + "/db_citys.db", null);
			Cursor cur = sdbase.query("provinces", null, null, null, null, null,
					null);
			if (cur != null) {
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					String name = cur.getString(cur.getColumnIndex("name"));
					int _id = cur.getInt(cur.getColumnIndex("_id"));
					arraylist.add(name + "-" + (_id - 1));
				}
			}
			sdbase.close();
			ProvinceListviewAdapter plap = new ProvinceListviewAdapter(
					ProvinceListview.this, arraylist);
			provincelisview.setAdapter(plap);
			// 注册监听信息改变
			pbroad = new pcardBroadcast();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Constants.BrandName + ".pbroad.cityoled");
			registerReceiver(pbroad, filter);
		} catch (Exception e) {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.setts_selfpd_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pbroad != null) {
			unregisterReceiver(pbroad);
		}
		arraylist.clear();
		arraylist = null;
		RecoveryTools.unbindDrawables(pcvlist);// 回收容
	}

	// 接收更新事物
	private class pcardBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mHandler.sendEmptyMessage(1);
		}
	}
}
