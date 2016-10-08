package org.aisin.sipphone.setts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowRedRecevedInfoAdapter extends BaseAdapter {
	private Context context;
	private int maxint = 0;
	private int tap = 0;
	private int tk = 0;
	private LayoutInflater inflater;
	private ArrayList<TreeMap<String, Object>> maps;
	private SimpleDateFormat sdformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat format2 = new java.text.SimpleDateFormat(
			"MM月dd日 HH:mm");

	public ShowRedRecevedInfoAdapter(Context context,
			ArrayList<TreeMap<String, Object>> maps, int tap, int tk) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.maps = maps;
		this.tap = tap;
		this.tk = tk;
		int temp = 0;
		try {
			for (int i = 0; i < maps.size(); i++) {
				int tempt = Integer.parseInt((String) maps.get(i).get("money"));
				if (tempt > temp) {
					maxint = i;
					temp = tempt;
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void notifyDataSetChanged() {
		int temp = 0;
		try {
			for (int i = 0; i < maps.size(); i++) {
				int tempt = Integer.parseInt((String) maps.get(i).get("money"));
				if (tempt > temp) {
					maxint = i;
					temp = tempt;
				}
			}
		} catch (Exception e) {
		}
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return maps.size();
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

			convertView = inflater.inflate(R.layout.showredrecevedinfoadapter,
					parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.reddetails_from_iamge = (ImageView) convertView
					.findViewById(R.id.reddetails_from_iamge);
			holder.gxy = (TextView) convertView.findViewById(R.id.gxy);
			holder.money = (TextView) convertView.findViewById(R.id.money);
			holder.sq = (TextView) convertView.findViewById(R.id.sq);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			if (maxint == position) {
				holder.sq.setVisibility(View.VISIBLE);
			} else {
				holder.sq.setVisibility(View.INVISIBLE);
			}
			TreeMap<String, Object> map = maps.get(position);
			if (map.get("name") != null) {
				String name = (String) map.get("name");
				if (!"".equals(name) && !"null".equals(name)) {
					holder.name.setText(name);
				} else {
					holder.name.setText("用户");
				}
			}
			if (map.get("headimage") != null) {
				Bitmap bitmap = (Bitmap) map.get("headimage");
				if (bitmap != null) {
					holder.reddetails_from_iamge.setImageBitmap(bitmap);
					holder.reddetails_from_iamge.setVisibility(View.VISIBLE);
				} else {
					holder.reddetails_from_iamge
							.setImageResource(R.drawable.defaultuserimage);
					holder.reddetails_from_iamge.setVisibility(View.INVISIBLE);
				}
			}
			if (map.get("open_time") != null) {
				String open_time = (String) map.get("open_time");
				try {
					open_time = format2
							.format(sdformat.parse(open_time.trim()));
					holder.time.setText(open_time);
				} catch (ParseException e) {
				}

			}
			if (map.get("thankyou") != null && !"".equals(map.get("thankyou"))) {
				String thankyou = (String) map.get("thankyou");
				holder.gxy.setText(thankyou);
				holder.gxy.setVisibility(View.VISIBLE);
			} else {
				holder.gxy.setVisibility(View.GONE);
			}
			if (map.get("money") != null && !"".equals(map.get("money"))) {
				String money = (String) map.get("money");
				switch (tap) {
				case 0:
					double money_temp = Double.parseDouble(money)
							/ (double) 100.0;
					holder.money.setText(money_temp + "元");
					break;

				case 1:
					holder.money.setText(money + "天");
					break;
				case 2:
					if (Integer.parseInt(money) > 1024) {
						holder.money.setText(Math.round((Integer.parseInt(money
								.trim()) / (double) 1024) * 10) / 10.0 + "MB");
					} else {
						holder.money.setText(money + "KB");
					}
					break;
				default:
					holder.money.setText("");
					break;
				}

			}
		} catch (Exception e) {
		}
		if (tk == 0) {
			holder.gxy.setVisibility(View.GONE);
		}
		return convertView;
	}
	class ViewHolder {
		TextView name;
		TextView time;
		ImageView reddetails_from_iamge;
		TextView gxy;
		TextView money;
		TextView sq;
	}
}


