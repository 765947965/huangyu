package org.aisin.sipphone;

import java.util.ArrayList;

import org.aisin.sipphone.commong.OraLodingUser;
import org.aisin.sipphone.tianyu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class R_Loding4v2_SPLIST extends BaseAdapter {

	private Context context;
	private ArrayList<OraLodingUser> oldus_showlist;
	private LayoutInflater inflater;

	public R_Loding4v2_SPLIST(Context context,
			ArrayList<OraLodingUser> oldus_showlist) {
		super();
		this.context = context;
		this.oldus_showlist = oldus_showlist;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return oldus_showlist.size();
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
		ViewHolder9 holder;
		if (convertView == null) {

			convertView = inflater.inflate(R.layout.r_loding_listview, parent,
					false);
			holder = new ViewHolder9();
			holder.rllvtextpn = (TextView) convertView
					.findViewById(R.id.rllvtextpn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder9) convertView.getTag();
		}
		holder.rllvtextpn.setText(oldus_showlist.get(position).getPhonenum());
		return convertView;
	}

	class ViewHolder9 {
		TextView rllvtextpn;
	}
}
