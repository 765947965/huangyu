package org.aisin.sipphone_call_records;

import java.util.ArrayList;
import java.util.TreeSet;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MoreCall_records_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<CallhistoryInfo> list = new ArrayList<CallhistoryInfo>();

	MoreCall_records_Adapter(Context context, TreeSet<CallhistoryInfo> set) {
		super();
		this.inflater = LayoutInflater.from(context);
		for (CallhistoryInfo chi : set) {
			list.add(chi);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder2 holder;
		if (convertView == null) {

			convertView = inflater.inflate(
					R.layout.morecall_records_adapter_item, parent, false);
			holder = new ViewHolder2();
			holder.morecall_records_adpter_item_calltime = (TextView) convertView
					.findViewById(R.id.morecall_records_adpter_item_calltime);
			holder.morecall_records_adpter_item_duration = (TextView) convertView
					.findViewById(R.id.morecall_records_adpter_item_duration);
			holder.morecall_records_adpter_item_calltype = (TextView) convertView
					.findViewById(R.id.morecall_records_adpter_item_calltype);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder2) convertView.getTag();
		}
		CallhistoryInfo chi = list.get(position);
		holder.morecall_records_adpter_item_calltime.setText(chi
				.getChainal_call_time());
		holder.morecall_records_adpter_item_duration.setText(chi.getDuration());
		holder.morecall_records_adpter_item_calltype.setText(chi
				.getCall_type_name());
		return convertView;
	}

}

class ViewHolder2 {
	TextView morecall_records_adpter_item_calltime;
	TextView morecall_records_adpter_item_duration;
	TextView morecall_records_adpter_item_calltype;
}
