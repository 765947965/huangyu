package org.aisin.sipphone.nearbystores;

import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tianyu.R;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayInMap extends Activity {
	private Context context;
	private MapView mMapView;
	private BaiduMap mBaiduMap;

	private LinearLayout displayinmap_linlayout;
	private TextView displayInMap_titlename;
	private ImageView displayInMap_bar_back;
	private TextView displayinmap_tothis;

	// 起点终点坐标
	double st_latitude;
	double st_longitude;
	double latitude = 0;
	double longitude = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.displayinmap);
		displayinmap_tothis = (TextView) this
				.findViewById(R.id.displayinmap_tothis);

		displayinmap_linlayout = (LinearLayout) this
				.findViewById(R.id.displayinmap_linlayout);
		displayInMap_titlename = (TextView) this
				.findViewById(R.id.displayInMap_titlename);
		displayInMap_bar_back = (ImageView) this
				.findViewById(R.id.displayInMap_bar_back);
		displayInMap_bar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mMapView = (MapView) this.findViewById(R.id.displayInMap_mMapView);
		mBaiduMap = mMapView.getMap();
		Intent intent = getIntent();
		st_latitude = intent.getDoubleExtra("RoutePlanDemo.st_latitude", 0);
		st_longitude = intent.getDoubleExtra("RoutePlanDemo.st_longitude", 0);
		latitude = intent.getDoubleExtra("DisplayInMap.latitude", 0);
		longitude = intent.getDoubleExtra("DisplayInMap.longitude", 0);
		LatLng p = new LatLng(
				intent.getDoubleExtra("DisplayInMap.latitude", 0),
				intent.getDoubleExtra("DisplayInMap.longitude", 0));
		String titlename = intent.getStringExtra("DisplayInMap.name");
		displayInMap_titlename.setText(titlename);
		displayinmap_tothis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				GetOtherInfo.doDH(context, st_latitude, st_longitude, latitude, longitude);
				/*Intent intent = new Intent(DisplayInMap.this,
						org.aisin.sipphone.nearbystores.RoutePlanDemo.class);
				intent.putExtra("RoutePlanDemo.st_latitude", st_latitude);
				intent.putExtra("RoutePlanDemo.st_longitude", st_longitude);
				intent.putExtra("RoutePlanDemo.en_latitude", latitude);
				intent.putExtra("RoutePlanDemo.en_longitude", longitude);
				DisplayInMap.this.startActivity(intent);*/
			}
		});
		mBaiduMap = mMapView.getMap();
		// 定义Maker坐标点
		LatLng point = new LatLng(latitude, longitude);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.show_here);
		if (bitmap != null) {
			// 构建MarkerOption，用于在地图上添加Marker
			OverlayOptions option = new MarkerOptions().position(point)
					.icon(bitmap).draggable(true);
			// 在地图上添加Marker，并显示
			mBaiduMap.addOverlay(option);
		}
		// 监听拖搁
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// Log.i("环宇", arg0.getTitle() + arg0.toString());
				return false;
			}
		});
		// 设置缩放级别 target(p) 定义中心点
		mBaiduMap.setMapStatus(MapStatusUpdateFactory
				.newMapStatus(new MapStatus.Builder().zoom(16).target(p)
						.build()));

	}

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
		RecoveryTools.unbindDrawables(displayinmap_linlayout);// 回收容
	}

}
