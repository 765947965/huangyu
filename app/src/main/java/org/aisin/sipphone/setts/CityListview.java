package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CityListview extends Activity implements OnItemClickListener {
	private String province;// 省份
	private String province_id;// 省份ID

	private LinearLayout removelayout;
	private TextView titlename;
	private ListView listvity;
	private ArrayList<String> arraylist = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		province = this.getIntent().getStringExtra("province");
		province_id = this.getIntent().getStringExtra("province_id");
		if (province == null || "".equals(province) || province_id == null
				|| "".equals(province_id)) {
			finish();
			return;
		}
		setContentView(R.layout.citylistview);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		titlename = (TextView) this.findViewById(R.id.titlename);
		listvity = (ListView) this.findViewById(R.id.listvity);
		titlename.setText(province);
		try {
			SQLiteDatabase sdbase = SQLiteDatabase.openOrCreateDatabase(
					"/data" + Environment.getDataDirectory().getAbsolutePath()
							+ "/" + this.getPackageName() + "/db_citys.db", null);
			Cursor cur = sdbase
					.query("citys", new String[] { "name" }, "province_id=?",
							new String[] { province_id }, null, null, null);
			if (cur != null) {
				for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
					String name = cur.getString(0);
					if (name.indexOf(".") >= 0) {
						continue;
					}
					arraylist.add(name);
				}
			}
			sdbase.close();
			CityListviewAdapter cadapter = new CityListviewAdapter(
					CityListview.this, arraylist);
			listvity.setAdapter(cadapter);
			listvity.setOnItemClickListener(this);
		} catch (Exception e) {
			finish();
		}
	}

	public void OnRelativeLayoutClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.setts_selfpd_back:// 退出
			CityListview.this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
		arraylist.clear();
		arraylist = null;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(Constants.BrandName + ".pbroad.cityoled");
		intent.putExtra("province", province);
		intent.putExtra("city", arraylist.get(position));
		CityListview.this.sendBroadcast(intent);
		CityListview.this.finish();
	}
}
