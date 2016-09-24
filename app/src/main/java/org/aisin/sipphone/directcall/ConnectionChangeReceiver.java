package org.aisin.sipphone.directcall;

import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.UserInfo_db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		UserInfo userInfo=UserInfo_db.getUserInfo(context);
		ConnectivityManager connManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
		if(activeNetInfo!=null){
		 int netWorkType=Check_network.getNetworkClass(context);
		 switch (netWorkType) {
		
		case 1:
			
			chaneAccounts(userInfo);
			break;
		case 2:
			
			chaneAccounts(userInfo);
			break;
		case 3:
			
			chaneAccounts(userInfo);
			break;
		case 4:
			
			chaneAccounts(userInfo);
			break;
		default:
			break;
			
			
		}
			
			   /*  State stateWifi=connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			     State stateMobile=connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
			     if(stateWifi==State.CONNECTED||stateWifi==State.DISCONNECTED
			    		 ||stateMobile==stateMobile.CONNECTED||stateMobile==stateMobile.DISCONNECTED){
			    	 AisinManager.getInstance().changeCount(userInfo);
			     }*/
		}
		
	}

	private void chaneAccounts(UserInfo userInfo){
		if(AisinManager.isInstanciated())
			AisinManager.getInstance().changeAccount(userInfo);
	}
}
