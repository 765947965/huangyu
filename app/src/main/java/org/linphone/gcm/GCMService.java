package org.linphone.gcm;
/*
GCMService.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.directcall.AisinManager;
import org.aisin.sipphone.directcall.AisinPreferences;
import org.linphone.mediastream.Log;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * @author Sylvain Berfini
 */
// Warning ! Do not rename the service !
public class GCMService extends GCMBaseIntentService {

	public GCMService() {
		
	}
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.e("Error while registering push notification : " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.d("Push notification received");
		if (AisinManager.isInstanciated()) {
			AisinManager.getLc().setNetworkReachable(false);
			AisinManager.getLc().setNetworkReachable(true);
		}
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Log.d("Registered push notification : " + regId);
		AisinPreferences.instance().setPushNotificationRegistrationID(regId);
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.w("Unregistered push notification : " + regId);
		AisinPreferences.instance().setPushNotificationRegistrationID(null);
	}
	
	protected String[] getSenderIds(Context context) {
	    return new String[] { context.getString(R.string.push_sender_id) };
	}
}
