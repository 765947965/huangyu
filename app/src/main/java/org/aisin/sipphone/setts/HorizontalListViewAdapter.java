package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HorizontalListViewAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Contact> addreds_list;

	public HorizontalListViewAdapter(Context context,
			ArrayList<Contact> addreds_list) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.addreds_list = addreds_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return addreds_list.size();
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
		// TODO Auto-generated method stub
		ViewHolder_hrtlva holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.horizontallistviewadapter,
					parent, false);
			holder = new ViewHolder_hrtlva();
			holder.itemtext = (TextView) convertView
					.findViewById(R.id.itemtext);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_hrtlva) convertView.getTag();
		}
		Contact cttmp = addreds_list.get(position);
		if (cttmp.isShowflag()) {
			if (cttmp.getRemark() == null || "".equals(cttmp.getRemark())
					|| "null".equals(cttmp.getRemark())) {
				holder.itemtext.setText(cttmp.getFriendphone());
			} else {
				holder.itemtext.setText(cttmp.getRemark());
			}
		} else {
			if (cttmp.getRemark() == null || "".equals(cttmp.getRemark())
					|| "null".equals(cttmp.getRemark())) {
				holder.itemtext.setText(cttmp.getPhonesList().get(0));
			} else {
				holder.itemtext.setText(cttmp.getRemark());
			}
		}
		return convertView;
	}

}

class ViewHolder_hrtlva {
	TextView itemtext;
}
