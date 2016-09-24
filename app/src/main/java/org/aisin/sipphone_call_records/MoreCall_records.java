package org.aisin.sipphone_call_records;

import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MoreCall_records extends Activity {
	private LinearLayout morecall_records_activity_linlayout;
	private ImageView morecall_records_back;
	private TreeSet<CallhistoryInfo> set;
	private ListView list;
	private MoreCall_records_Adapter mra;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.morecall_records_activity);
		set = (TreeSet<CallhistoryInfo>) getIntent().getSerializableExtra(
				"morecall_records");
		if (set == null) {
			finish();
		}
		mra = new MoreCall_records_Adapter(MoreCall_records.this, set);
		morecall_records_activity_linlayout = (LinearLayout) this
				.findViewById(R.id.morecall_records_activity_linlayout);
		morecall_records_back = (ImageView) this
				.findViewById(R.id.morecall_records_back);
		morecall_records_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		list = (ListView) this.findViewById(R.id.morecall_record_listview);
		list.setAdapter(mra);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(morecall_records_activity_linlayout);// 回收容
	}

}
