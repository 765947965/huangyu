/*
AisinService.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

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
package org.aisin.sipphone.directcall;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.PhoneInfo;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public final class AisinService extends Service {

	private final String ACTION_NAME = "send_broadcast";
	private static String TAG = "AisinService";
	private static AisinService instance;
	private SharedPreferences shared = null;
	private boolean iscall;

	public static boolean isReady() {
		return instance != null;
	}

	public static AisinService instance() {
		if (isReady())
			return instance;

		throw new RuntimeException("AisinService not instantiated yet");
	}

	private String message;
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (PhoneInfo.SDKVersion > 11) {
					try {
						if (instance != null) {
							// Toast.makeText(instance, message,
							// Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
					}
				}
			}
		}

	};
	private LinphoneCoreListenerBase mListener;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (intent != null) {
			if ("stopself".equals(intent.getStringExtra("startflag"))) {
				AisinService.this.stopSelf();
				return super.onStartCommand(intent, flags, startId);
			}
		}
		try {
			// 注册
			AisinManager.getInstance().initAccount();
		} catch (LinphoneCoreException e) {
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate() {
		boolean sc = true;
		try {
			super.onCreate();
			LinphoneCoreFactory.instance().setLogCollectionPath(
					getFilesDir().getAbsolutePath());
			LinphoneCoreFactory.instance().enableLogCollection(
					!(getResources().getBoolean(R.bool.disable_every_log)));
			dumpDeviceInformation();
			dumpInstalledLinphoneInformation();
			instance = this;
			if (!AisinManager.isInstanciated()) {
				AisinManager.createAndStart(getApplicationContext());
			}
			// instance is ready once linphone manager has been created
			AisinManager.getLc().addListener(
					mListener = new LinphoneCoreListenerBase() {

						@Override
						public void callState(LinphoneCore lc,
								LinphoneCall call, LinphoneCall.State state,
								String message) {
							try {
								message = state.toString();
								if (mHandler != null) {
									mHandler.sendEmptyMessage(1);
								}
							} catch (Exception e) {
							} catch (Error e) {
							}
							if (instance == null) {
								Log.i("Service not ready, discarding call state change to ",
										state.toString());
								return;
							}

							if (state == State.Error) {
								sendOutcallBroadCast(5);
								return;
							}

							if (AisinManager.getLc().getCallsNb() == 0) {
								sendOutcallBroadCast(6);

								return;
							}

							if (state == State.OutgoingEarlyMedia) {
								sendOutcallBroadCast(2);

							}
							if (state == State.Connected) {
								sendOutcallBroadCast(3);

							}

							if (state == State.StreamsRunning) {
								sendOutcallBroadCast(4);

							}

						}

						@Override
						public void globalState(LinphoneCore lc,
								LinphoneCore.GlobalState state, String message) {
							try {
								message = state.toString();
								if (mHandler != null) {
									mHandler.sendEmptyMessage(1);
								}
							} catch (Exception e) {
							} catch (Error e) {
							}
							if (state == GlobalState.GlobalOn) {
							}
						}

						@Override
						public void registrationState(LinphoneCore lc,
								LinphoneProxyConfig cfg,
								LinphoneCore.RegistrationState state,
								String smessage) {
							try {
								message = state.toString();
								if (mHandler != null) {
									mHandler.sendEmptyMessage(1);
								}
							} catch (Exception e) {
							} catch (Error e) {
							}

							if (state == RegistrationState.RegistrationOk
									&& AisinManager.getLc()
											.getDefaultProxyConfig() != null
									&& AisinManager.getLc()
											.getDefaultProxyConfig()
											.isRegistered()) {
								if (!iscall) {
									// Toast.makeText(getApplicationContext(),
									// "registerOk", 3000).show();
									sendOutcallBroadCast(7);
									iscall = true;
								}

							}

							if ((state == RegistrationState.RegistrationFailed || state == RegistrationState.RegistrationCleared)
									&& (AisinManager.getLc()
											.getDefaultProxyConfig() == null || !AisinManager
											.getLc().getDefaultProxyConfig()
											.isRegistered())) {
								sendOutcallBroadCast(1);
							}

							if (state == RegistrationState.RegistrationNone) {
								sendOutcallBroadCast(1);
							}
						}
					});
			SharedPreferencesTools
					.getSharedPreferences_4ZBZL(AisinService.this).edit()
					.putBoolean(SharedPreferencesTools.zbzlling, true).commit();
		} catch (Error e) {
			sc = false;

		} catch (Exception e) {
			sc = false;
		}
		if (!sc) {
			SharedPreferencesTools
					.getSharedPreferences_4ZBZL(AisinService.this).edit()
					.putBoolean(SharedPreferencesTools.zbzlling, false)
					.commit();
			AisinService.this.stopSelf();
		}
	}

	@SuppressWarnings("deprecation")
	private void dumpDeviceInformation() {
		StringBuilder sb = new StringBuilder();
		sb.append("DEVICE=").append(Build.DEVICE).append("\n");
		sb.append("MODEL=").append(Build.MODEL).append("\n");
		// MANUFACTURER doesn't exist in android 1.5.
		// sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
		sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
		sb.append("EABI=").append(Build.CPU_ABI).append("\n");
		Log.i(sb.toString());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public synchronized void onDestroy() {

		try {
			LinphoneCore lc = AisinManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null) {
				lc.removeListener(mListener);
			}

			instance = null;
			AisinManager.destroy();

			super.onDestroy();
		} catch (Exception e) {
		} catch (Error e) {
		}
	}

	private void dumpInstalledLinphoneInformation() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException nnfe) {
		}

		if (info != null) {

		} else {

		}
	}

	public void tryingNewOutgoingCallButAlreadyInCall() {
	}

	public void tryingNewOutgoingCallButCannotGetCallParameters() {
	}

	public void tryingNewOutgoingCallButWrongDestinationAddress() {
	}

	public void onCallEncryptionChanged(final LinphoneCall call,
			final boolean encrypted, final String authenticationToken) {
	}

	public void sendOutcallBroadCast(int num) {
		Intent mIntent = new Intent(ACTION_NAME);
		mIntent.putExtra("status", num);
		sendBroadcast(mIntent);
	}

	public void setIscall(boolean iscall) {
		this.iscall = iscall;
	}

	public boolean isIscall() {
		return iscall;
	}

}
