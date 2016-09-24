package org.aisin.sipphone.nearbystores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.CallPhoneManage;
import org.aisin.sipphone.commong.PoiDetailResult4SetInfo;
import org.aisin.sipphone.tianyu.R;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiDetailResult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Nearby_stores_listadapter extends BaseAdapter {
	private Context context;
	private List<PoiDetailResult4SetInfo> list = new ArrayList<PoiDetailResult4SetInfo>();
	TreeMap<String, PoiDetailResult> map;
	double latitude;
	double longitude;

	public Nearby_stores_listadapter(Context context,
			TreeMap<String, PoiDetailResult> map, double latitude,
			double longitude) {
		this.context = context;
		this.map = map;
		this.latitude = latitude;
		this.longitude = longitude;
		TreeSet<PoiDetailResult4SetInfo> settemp = new TreeSet<PoiDetailResult4SetInfo>();
		for (Entry<String, PoiDetailResult> ent : map.entrySet()) {
			settemp.add(new PoiDetailResult4SetInfo(latitude, longitude, ent
					.getValue()));
		}
		list.clear();
		list.addAll(settemp);
		settemp.clear();
		settemp = null;
	}

	@Override
	public void notifyDataSetChanged() {
		TreeSet<PoiDetailResult4SetInfo> settemp = new TreeSet<PoiDetailResult4SetInfo>();
		for (Entry<String, PoiDetailResult> ent : map.entrySet()) {
			settemp.add(new PoiDetailResult4SetInfo(latitude, longitude, ent
					.getValue()));
		}
		list.clear();
		list.addAll(settemp);
		super.notifyDataSetChanged();
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
		// TODO Auto-generated method stub
		ViewHolder_n holder;
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.nearby_stores_listadapter_activity, parent, false);
			holder = new ViewHolder_n();
			holder.nearby_layout_adpter = (LinearLayout) convertView
					.findViewById(R.id.nearby_layout_adpter);
			holder.nearby_layout_detailslisen = (LinearLayout) convertView
					.findViewById(R.id.nearby_layout_detailslisen);
			holder.nearby_name = (TextView) convertView
					.findViewById(R.id.nearby_name);
			holder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			holder.adpter_address = (TextView) convertView
					.findViewById(R.id.adpter_address);
			holder.nearby_adapter_calloutbt = (TextView) convertView
					.findViewById(R.id.nearby_adapter_calloutbt);
			holder.nearby_stores_tonearby = (TextView) convertView
					.findViewById(R.id.nearby_stores_tonearby);
			holder.nearby_stores_tonearby_relayout = (RelativeLayout) convertView
					.findViewById(R.id.nearby_stores_tonearby_relayout);
			holder.nearby_stores_calloutbt_relayout = (RelativeLayout) convertView
					.findViewById(R.id.nearby_stores_calloutbt_relayout);
			convertView.setTag(holder);
		} else {

			holder = (ViewHolder_n) convertView.getTag();

		}
		PoiDetailResult4SetInfo pdrinfo = list.get(position);
		final PoiDetailResult pdr = pdrinfo.getPodr();
		holder.nearby_name.setText(pdr.getName());
		holder.distance.setText(pdrinfo.getDistance_str());
		holder.adpter_address.setText(pdr.getAddress());
		String telephone = pdr.getTelephone();
		if (telephone != null && !"".equals(telephone)) {
			telephone = telephone.replaceAll(" ", "").replaceAll("-", "")
					.replaceAll("\\(", "").replaceAll("\\)", "");
			final String telephone_temp = telephone;
			holder.nearby_adapter_calloutbt.setTextColor(Color.BLACK);
			holder.nearby_stores_calloutbt_relayout.setEnabled(true);
			holder.nearby_stores_calloutbt_relayout
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							CallPhoneManage.callPhone(context, null, "",
									telephone_temp);
						}
					});
		} else {
			telephone = "";
			holder.nearby_adapter_calloutbt.setTextColor(Color
					.parseColor("#C1C1C1"));
			holder.nearby_stores_calloutbt_relayout.setEnabled(false);
		}
		final String telephone_temp = telephone;
		holder.nearby_layout_detailslisen
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 到详情页
						Intent intent = new Intent(
								context,
								org.aisin.sipphone.nearbystores.DetailsPageActivity.class);
						intent.putExtra("DetailsPageActivity.name",
								pdr.getName());
						intent.putExtra("DetailsPageActivity.address",
								pdr.getAddress());
						intent.putExtra("DetailsPageActivity.telephone",
								telephone_temp);
						intent.putExtra("DetailsPageActivity.latitude",
								pdr.getLocation().latitude);
						intent.putExtra("DetailsPageActivity.longitude",
								pdr.getLocation().longitude);
						intent.putExtra("RoutePlanDemo.st_latitude", latitude);
						intent.putExtra("RoutePlanDemo.st_longitude", longitude);
						context.startActivity(intent);
					}
				});
		holder.nearby_stores_tonearby_relayout
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						LatLng enlatling = pdr.getLocation();
						// 导航
						GetOtherInfo.doDH(context, latitude, longitude,
								enlatling.latitude, enlatling.longitude);
					}
				});
		return convertView;

	}

	class ViewHolder_n {
		TextView nearby_name;
		TextView nearby_stores_tonearby;
		TextView distance;
		TextView adpter_address;
		TextView nearby_adapter_calloutbt;
		LinearLayout nearby_layout_adpter;
		LinearLayout nearby_layout_detailslisen;
		RelativeLayout nearby_stores_tonearby_relayout;
		RelativeLayout nearby_stores_calloutbt_relayout;
	}
}
