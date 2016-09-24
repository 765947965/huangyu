package org.aisin.sipphone;

import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.HttpUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DolowniamgeServer extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent == null) {
			stopSelf();
			return super.onStartCommand(intent, flags, startId);
		}
		final String furl = intent.getStringExtra("furl");
		final String imagename = intent.getStringExtra("imagename");
		if (furl == null || "".equals(furl) || imagename == null
				|| "".equals(imagename)
				|| !Check_network.isNetworkAvailable(DolowniamgeServer.this)) {
			DolowniamgeServer.this.stopSelf();
		} else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					HttpUtils.downloadImage(DolowniamgeServer.this, furl,
							imagename);
					DolowniamgeServer.this.stopSelf();
				}
			}).start();
		}

		return super.onStartCommand(intent, flags, startId);
	}

}
