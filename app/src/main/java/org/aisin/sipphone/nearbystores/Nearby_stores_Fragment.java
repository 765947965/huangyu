package org.aisin.sipphone.nearbystores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Nearby_stores_Fragment extends Fragment implements
		OnGetPoiSearchResultListener, OnClickListener,
		DynamicListView.DynamicListViewListener {
	private LinearLayout nearby_litslayout;// 地图层详情框
	// private TextView callout_nearby;
	private TextView tohear_nearby;// 点击进详情
	private ImageView md_update;// 刷新按钮 暂时废弃
	private PoiSearch mPoiSearch;
	private TextView mdinfo;// 显示店铺详细信息
	private FrameLayout nearby_baidumap;// 地图层
	private RelativeLayout nearby_listview_relayout;// listview层
	private DynamicListView nearby_listview;// listview
	private TextView nearby_jzz;// 列表显示加载中
	private ImageView result_change;// 切换地图和列表
	private boolean showview = true;// 为truelistview展示
	private String keywordstr;

	MapView mMapView;
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	boolean isFirstLoc = true;// 是否首次定位
	double latitude, longitude;// 当前所在位置
	int index_inum = 0;// 下次要加载的页码数
	Nearby_stores_listadapter nsla;// adapter
	boolean nearbylistflag = true;// true//为listview刷新,false为listview加载更多
	boolean isupdatelist = true;// 搜索结果是否在刷新地址数据的时候刷新
	BitmapDescriptor bitmap;

	// 当前地图提示框显示的相关文本
	private String ts_show_name = null;
	private String ts_show_address = null;
	private String ts_show_telephone = null;
	private double ts_show_latitude = 0, ts_show_longitude = 0;// 当前所在位置

	int setsize = 0;// 记录当前详细信息检索到哪一页
	List<String> list = new ArrayList<String>();
	TreeMap<String, PoiDetailResult> map = new TreeMap<String, PoiDetailResult>();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				mPoiSearch.searchNearby((new PoiNearbySearchOption())
						.keyword(keywordstr)
						.location(new LatLng(latitude, longitude))
						.radius(10000).pageNum(index_inum));
				index_inum += 1;
			} else if (msg.what == 2) {// 地址信息加载更多或者刷新完毕
				nearby_jzz.setVisibility(View.INVISIBLE);
				if (nsla == null) {
					nsla = new Nearby_stores_listadapter(AisinActivity.context,
							map, latitude, longitude);
					nearby_listview.setAdapter(nsla);
				} else {
					nsla.notifyDataSetChanged();
					nearby_listview.doneRefresh();
				}
				// 重新在地图上绘制点
				mBaiduMap.clear();
				for (Entry<String, PoiDetailResult> ent : map.entrySet()) {
					// 定义Maker坐标点
					LatLng point = ent.getValue().getLocation();
					// 构建MarkerOption，用于在地图上添加Marker
					OverlayOptions option = new MarkerOptions().position(point)
							.icon(bitmap).draggable(true);
					// 在地图上添加Marker，并显示
					mBaiduMap.addOverlay(option);
				}

			} else if (msg.what == 3) {
				mLocClient.requestLocation();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		View view = inflater.inflate(R.layout.nearby_stores_fragment_layout,
				container, false);
		nearby_listview_relayout = (RelativeLayout) view
				.findViewById(R.id.nearby_listview_relayout);
		result_change = (ImageView) view.findViewById(R.id.result_change);
		result_change.setOnClickListener(this);
		nearby_jzz = (TextView) view.findViewById(R.id.nearby_jzz);
		nearby_baidumap = (FrameLayout) view.findViewById(R.id.nearby_baidumap);
		nearby_listview = (DynamicListView) view
				.findViewById(R.id.nearby_listview);
		nearby_listview.setDoMoreWhenBottom(true); // 滚动到低端的时候不自己加载更多
		nearby_listview.setOnRefreshListener(this); // 刷新监听
		nearby_listview.setOnMoreListener(this);// 下拉更多监听
		keywordstr = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(AisinActivity.context)
				.getString(SharedPreferencesTools.SPF_msglist_date_nearby_shop,
						Constants.nearbyname);
		keywordstr = SharedPreferencesTools.getSharedPreferences_4YCSZ(
				AisinActivity.context).getString(
				SharedPreferencesTools.ycsz_neary_name, keywordstr);

		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.show_here);// 地图图标

		nearby_litslayout = (LinearLayout) view
				.findViewById(R.id.nearby_litslayout);
		// 地图层下面提示框点击响应详情
		nearby_litslayout.setOnClickListener(this);
		tohear_nearby = (TextView) view.findViewById(R.id.tohear_nearby);
		tohear_nearby.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		mdinfo = (TextView) view.findViewById(R.id.mdinfo);
		md_update = (ImageView) view.findViewById(R.id.md_update);
		md_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(1);
			}
		});
		mMapView = (MapView) view.findViewById(R.id.mMapView);
		mBaiduMap = mMapView.getMap();

		// 初始化搜索模块，注册搜索事件监听
		// 创建POI检索监听者；
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);

		// 自定义覆盖点点击监听
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				double marker_latitude = arg0.getPosition().latitude;
				double marker_longitude = arg0.getPosition().longitude;
				for (Entry<String, PoiDetailResult> ent : map.entrySet()) {
					PoiDetailResult result = ent.getValue();
					LatLng llg = ent.getValue().getLocation();
					double ent_latitude = llg.latitude;
					double ent_longitude = llg.longitude;
					if (marker_latitude == ent_latitude
							&& marker_longitude == ent_longitude) {
						responseCovering(result);
						break;
					}
				}
				return false;
			}
		});

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(AisinActivity.context);
		mLocClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(30000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		nearby_baidumap.setVisibility(View.INVISIBLE);
		nearby_listview_relayout.setVisibility(View.VISIBLE);
		result_change.setBackgroundResource(R.drawable.result_change_map);
		return view;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mPoiSearch.destroy();
		mLocClient.stop();
		mMapView.onDestroy();
		mMapView = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mMapView.onResume();
	}

	// 详情检索回调
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

		if (setsize >= list.size()) {
			mHandler.sendEmptyMessage(2);// 通知刷新listview
			if (index_inum == 1) {// 首次第一页数据加载成功
				nearby_listview.doneRefresh();// 可能是刷新引起
				nearby_litslayout.setVisibility(View.INVISIBLE);
			} else if (index_inum > 1) {// 后续加载更多刷新
				nearby_listview.doneMore();// 通知listview加载更多完成
			}
			return;
		}
		//
		// 获取Place详情页检索结果
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			// mdinfo.setText("抱歉，未找到结果");
		} else {
			// 循环检索
			map.put(result.getUid(), result);
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(list.get(setsize)));
			setsize += 1;
		}
	}

	// 检索结果回调
	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			Toast.makeText(AisinActivity.context, "未找到更多结果", Toast.LENGTH_LONG)
					.show();
			nearby_jzz.setText("暂无数据");
			if (nearbylistflag) {
				nearby_listview.doneRefresh();// 通知listview刷新完成
			} else {
				nearby_listview.doneMore();// 通知listview加载更多完成
			}
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			// 检索第一个POI的详情
			for (PoiInfo poi : result.getAllPoi()) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
						.poiUid(poi.uid));
				list.add(poi.uid);
			}
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(list.get(setsize)));
			setsize += 1;
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(AisinActivity.context, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	// 响应地图覆盖物点击事件方法
	private void responseCovering(PoiDetailResult result) {
		nearby_litslayout.setVisibility(View.VISIBLE);
		if (result != null) {
			mdinfo.setText(result.getName() + "\n电话:" + result.getTelephone()
					+ "\n地址:" + result.getAddress());
			ts_show_name = result.getName();
			ts_show_address = result.getAddress();
			String telephone = result.getTelephone();
			if (telephone != null && !"".equals(telephone)) {
				telephone = telephone.replaceAll(" ", "").replaceAll("-", "")
						.replaceAll("\\(", "").replaceAll("\\)", "");
			} else {
				telephone = "";
			}
			ts_show_telephone = telephone;
			ts_show_latitude = result.getLocation().latitude;
			ts_show_longitude = result.getLocation().longitude;
		} else {
			mdinfo.setText("暂无数据");
			ts_show_name = null;
			ts_show_address = null;
			ts_show_telephone = null;
			ts_show_latitude = 0;
			ts_show_longitude = 0;
		}
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			// 地图缩放级别1~20级
			mBaiduMap.setMapStatus(MapStatusUpdateFactory
					.newMapStatus(new MapStatus.Builder().zoom(16).build()));

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);

			}
			if (isupdatelist) {
				isupdatelist = false;
				// 只在请求刷新数据的时候改变原始位置坐标
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				mPoiSearch.searchNearby((new PoiNearbySearchOption())
						.keyword(keywordstr)
						.location(new LatLng(latitude, longitude))
						.radius(10000).pageNum(index_inum));
				index_inum += 1;
			}

		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.result_change) {
			if (showview) {
				showview = false;
				result_change
						.setBackgroundResource(R.drawable.result_change_list);
				nearby_baidumap.setVisibility(View.VISIBLE);
				nearby_listview_relayout.setVisibility(View.INVISIBLE);
			} else {
				showview = true;
				result_change
						.setBackgroundResource(R.drawable.result_change_map);
				nearby_baidumap.setVisibility(View.INVISIBLE);
				nearby_listview_relayout.setVisibility(View.VISIBLE);
			}
		} else if (v.getId() == R.id.nearby_litslayout) {
			// 到详情页
			if (ts_show_name == null || ts_show_address == null
					|| ts_show_telephone == null) {
				return;
			}
			Intent intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.nearbystores.DetailsPageActivity.class);
			intent.putExtra("DetailsPageActivity.name", ts_show_name);
			intent.putExtra("DetailsPageActivity.address", ts_show_address);
			intent.putExtra("DetailsPageActivity.telephone", ts_show_telephone);
			intent.putExtra("DetailsPageActivity.latitude", ts_show_latitude);
			intent.putExtra("DetailsPageActivity.longitude", ts_show_longitude);
			intent.putExtra("RoutePlanDemo.st_latitude", latitude);
			intent.putExtra("RoutePlanDemo.st_longitude", longitude);
			AisinActivity.context.startActivity(intent);
		}
	}

	// 刷新或者加载更多回调函数
	@Override
	public boolean onRefreshOrMore(DynamicListView dynamicListView,
			boolean isRefresh) {
		if (isRefresh) {
			// 刷新数据;
			nearbylistflag = true;
			setsize = 0;
			index_inum = 0;
			list.clear();
			map.clear();
			isupdatelist = true;
			mHandler.sendEmptyMessage(3);// 重新发起定位并搜索
		} else {
			// 加载更多
			nearbylistflag = false;
			mHandler.sendEmptyMessage(1);
		}
		return false;
	}
}
