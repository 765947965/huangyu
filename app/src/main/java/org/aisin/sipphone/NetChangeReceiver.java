package org.aisin.sipphone;

import org.aisin.sipphone.tools.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class NetChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			// 手机网络连接成功
			Intent service = new Intent(context,
					org.aisin.sipphone.CkeckUpServer.class);
			context.startService(service);
			context.sendBroadcast(new Intent(Constants.BrandName
					+ ".network.yes"));
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			// 手机没有任何的网络
			context.sendBroadcast(new Intent(Constants.BrandName
					+ ".network.no"));
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			// 无线网络连接成功
			Intent service = new Intent(context,
					org.aisin.sipphone.CkeckUpServer.class);
			context.startService(service);
			context.sendBroadcast(new Intent(Constants.BrandName
					+ ".network.yes"));
		}
	}
}
