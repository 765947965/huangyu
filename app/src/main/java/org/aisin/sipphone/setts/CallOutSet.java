package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.SwitchState;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class CallOutSet extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private LinearLayout calloutsetactivity_linlayout;
	private ImageView setts_calloutset_back;
	private ToggleButton calloutset_switch, calloutset_switch_3G_4G,
			calloutset_switch_wifi;
	private SharedPreferences sharepreferens;
	private Editor editor;

	private TextView textzhibo3g;
	private RelativeLayout layoutzhibo3g;
	private TextView textzhibowifi;
	private RelativeLayout layoutzhibowifi;
	private TextView titlezhibo;
	private TextView textzhibo;
	private TextView z96ipsm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calloutsetactivity);
		calloutsetactivity_linlayout = (LinearLayout) this
				.findViewById(R.id.calloutsetactivity_linlayout);
		setts_calloutset_back = (ImageView) this
				.findViewById(R.id.setts_calloutset_back);
		calloutset_switch = (ToggleButton) this
				.findViewById(R.id.calloutset_switch);
		calloutset_switch_3G_4G = (ToggleButton) this
				.findViewById(R.id.calloutset_switch_3G_4G);
		calloutset_switch_wifi = (ToggleButton) this
				.findViewById(R.id.calloutset_switch_wifi);
		textzhibo3g = (TextView) this.findViewById(R.id.textzhibo3g);
		layoutzhibo3g = (RelativeLayout) this.findViewById(R.id.layoutzhibo3g);
		textzhibowifi = (TextView) this.findViewById(R.id.textzhibowifi);
		layoutzhibowifi = (RelativeLayout) this
				.findViewById(R.id.layoutzhibowifi);
		titlezhibo = (TextView) this.findViewById(R.id.titlezhibo);
		textzhibo = (TextView) this.findViewById(R.id.textzhibo);
		z96ipsm = (TextView) this.findViewById(R.id.z96ipsm);
		setts_calloutset_back.setOnClickListener(this);
		z96ipsm.setText(this.getString(R.string.aboutaisin_bzzx7_1) + "\n"
				+ this.getString(R.string.aboutaisin_bzzx7_2) + "\n"
				+ this.getString(R.string.aboutaisin_bzzx7_3) + "\n"
				+ this.getString(R.string.aboutaisin_bzzx7_4) + "\n"
				+ this.getString(R.string.aboutaisin_bzzx7_5));
		sharepreferens = SharedPreferencesTools
				.getSharedPreferences_ALLSWITCH(CallOutSet.this);
		editor = sharepreferens.edit();
		int answallflag = sharepreferens.getInt(
				SharedPreferencesTools.SPF_ALLSWITCH_ANSWALLFLAG,
				SwitchState.ANSWALL_Y);
		boolean g3_4g_flag = sharepreferens.getBoolean(
				SharedPreferencesTools.SPF_NETWORK_3G_4G, false);
		boolean wifi_flag = sharepreferens.getBoolean(
				SharedPreferencesTools.SPF_NETWORK_WIFI, false);
		if (answallflag == SwitchState.ANSWALL_Y) {
			calloutset_switch.setChecked(true);
		} else {
			calloutset_switch.setChecked(false);
		}
		if (g3_4g_flag) {
			calloutset_switch_3G_4G.setChecked(true);
		} else {
			calloutset_switch_3G_4G.setChecked(false);
		}

		if (wifi_flag) {
			calloutset_switch_wifi.setChecked(true);
		} else {
			calloutset_switch_wifi.setChecked(false);
		}

		calloutset_switch.setOnCheckedChangeListener(this);
		calloutset_switch_3G_4G.setOnCheckedChangeListener(this);
		calloutset_switch_wifi.setOnCheckedChangeListener(this);

		if (!SharedPreferencesTools.getSharedPreferences_4ZBZL(this)
				.getBoolean(SharedPreferencesTools.zbzlling, true)) {
			textzhibo3g.setVisibility(View.GONE);
			layoutzhibo3g.setVisibility(View.GONE);
			textzhibowifi.setVisibility(View.GONE);
			layoutzhibowifi.setVisibility(View.GONE);
			titlezhibo.setVisibility(View.GONE);
			textzhibo.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.setts_calloutset_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(calloutsetactivity_linlayout);// 回收容器
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// 开关监听
		int id = buttonView.getId();
		switch (id) {
		case R.id.calloutset_switch:
			if (isChecked) {
				editor.putInt(SharedPreferencesTools.SPF_ALLSWITCH_ANSWALLFLAG,
						SwitchState.ANSWALL_Y).commit();
			} else {
				editor.putInt(SharedPreferencesTools.SPF_ALLSWITCH_ANSWALLFLAG,
						SwitchState.ANSWALL_N).commit();
			}
			break;
		case R.id.calloutset_switch_3G_4G:
			if (isChecked) {
				editor.putBoolean(SharedPreferencesTools.SPF_NETWORK_3G_4G,
						true).commit();
			} else {
				editor.putBoolean(SharedPreferencesTools.SPF_NETWORK_3G_4G,
						false).commit();
			}

			break;
		case R.id.calloutset_switch_wifi:
			if (isChecked) {
				editor.putBoolean(SharedPreferencesTools.SPF_NETWORK_WIFI, true)
						.commit();
			} else {
				editor.putBoolean(SharedPreferencesTools.SPF_NETWORK_WIFI,
						false).commit();

			}
			break;
		}

	}

}
