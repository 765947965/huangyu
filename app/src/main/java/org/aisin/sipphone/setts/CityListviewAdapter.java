package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CityListviewAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> arraylist;

	public CityListviewAdapter(Context context, ArrayList<String> arraylist) {
		this.context = context;
		this.arraylist = arraylist;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylist.size();
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
		ViewHolder_cityplad holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.citylistviewadapter,
					parent, false);
			holder = new ViewHolder_cityplad();
			holder.spd_pccitylarl = (RelativeLayout) convertView
					.findViewById(R.id.spd_pccitylarl);
			holder.provincecityname = (TextView) convertView
					.findViewById(R.id.provincecityname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_cityplad) convertView.getTag();
		}
		holder.provincecityname.setText(arraylist.get(position));
		return convertView;
	}

	class ViewHolder_cityplad {
		RelativeLayout spd_pccitylarl;
		TextView provincecityname;
	}
}
