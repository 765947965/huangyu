package org.aisin.sipphone.setts;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProvinceListviewAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private ArrayList<String> arraylist;

	public ProvinceListviewAdapter(Context context, ArrayList<String> arraylist) {
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
		ViewHolder_plad holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.provincelistviewadapter,
					parent, false);
			holder = new ViewHolder_plad();
			holder.spd_pclarl = (RelativeLayout) convertView
					.findViewById(R.id.spd_pclarl);
			holder.provincename = (TextView) convertView
					.findViewById(R.id.provincename);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder_plad) convertView.getTag();
		}
		String province = arraylist.get(position);
		final String[] strs = province.split("-");
		if (strs != null && strs.length == 2) {
			holder.provincename.setText(strs[0]);
			holder.spd_pclarl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 启动省份城市 城市列表
					Intent intent = new Intent(context,
							org.aisin.sipphone.setts.CityListview.class);
					intent.putExtra("province", strs[0]);
					intent.putExtra("province_id", strs[1]);
					context.startActivity(intent);
				}
			});
		} else {
			holder.provincename.setText("");
			holder.spd_pclarl.setOnClickListener(null);
		}
		return convertView;
	}

	class ViewHolder_plad {
		RelativeLayout spd_pclarl;
		TextView provincename;
	}

}
