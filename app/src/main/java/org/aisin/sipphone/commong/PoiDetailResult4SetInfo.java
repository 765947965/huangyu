package org.aisin.sipphone.commong;

import org.aisin.sipphone.tools.LatLngtoDistance;

import com.baidu.mapapi.search.poi.PoiDetailResult;

public class PoiDetailResult4SetInfo implements
		Comparable<PoiDetailResult4SetInfo> {
	private String distance_str;
	private PoiDetailResult podr;
	private double distance_db;

	public PoiDetailResult4SetInfo(double latitude, double longitude,
			PoiDetailResult podr) {
		super();
		this.podr = podr;
		distance_db = LatLngtoDistance.Distance(latitude, longitude,
				podr.getLocation().latitude, podr.getLocation().longitude);
		distance_str = Math.round(distance_db / 100d) / 10d + "公里";
	}

	public String getDistance_str() {
		return distance_str;
	}

	public PoiDetailResult getPodr() {
		return podr;
	}

	@Override
	public int compareTo(PoiDetailResult4SetInfo another) {
		// TODO Auto-generated method stub
		return (int) (this.distance_db - another.distance_db);
	}

}
