package org.aisin.sipphone.aipay;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPayActivityAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Productinfos> pcsShow;

	public MyPayActivityAdapter(Context context, ArrayList<Productinfos> pcsShow) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.pcsShow = pcsShow;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pcsShow.size();
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mypayactivityadapter,
					parent, false);
			holder = new ViewHolder();
			holder.bodytext = (TextView) convertView
					.findViewById(R.id.bodytext);
			holder.checkimage = (ImageView) convertView
					.findViewById(R.id.checkimage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Productinfos nps = pcsShow.get(position);
		holder.bodytext.setText(nps.getBody());
		if (nps.isChecked()) {
			holder.checkimage.setImageResource(R.drawable.check_box_checked);
		} else {
			holder.checkimage.setImageResource(R.drawable.check_box_uncheck);
		}
		return convertView;
	}

	class ViewHolder {
		TextView bodytext;
		ImageView checkimage;
	}
}
