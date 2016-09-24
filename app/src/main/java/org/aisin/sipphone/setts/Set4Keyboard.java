package org.aisin.sipphone.setts;

import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.GetMusicName4PY;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;

import com.lidroid.xutils.BitmapUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class Set4Keyboard extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	private LinearLayout removelayout;
	private LinearLayout yinchanglayout;
	private ImageView setts_recharge_bar_back;
	private ImageView keybcksmall;
	private RelativeLayout setts_recharge_yd_relayout;
	private RelativeLayout setts_recharge_lt_relayout;
	private RelativeLayout setts_sxx_relayout;
	private TextView musictext;
	private TextView sxxtext;
	private ToggleButton upcontants_switch;
	private ToggleButton huibomusic_switch;
	private ToggleButton outv2orv2_switch;
	private ToggleButton nerby_switch;
	private EditText nerby_edittext;
	private String musicoldname;
	private TreeMap<String, String> musicnames4py;
	private BitmapUtils bitmapUtils;
	private String morenbk;
	private int clicknum = 0;
	private SharedPreferences sharepreferens;
	private SharedPreferences ycsz;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				SharedPreferences sp = SharedPreferencesTools
						.getSharedPreferences_4keybackground(Set4Keyboard.this);
				String morenbk_t = sp.getString(SharedPreferencesTools.KBG_KEY,
						Constants.KEYBACK_MR);
				if (morenbk_t.equals(morenbk)) {
					return;
				}
				morenbk = morenbk_t;
				boolean zdyflag = sp.getBoolean(SharedPreferencesTools.KBG_ZDY,
						false);
				if (!Constants.KEYBACK_WRIGHT.equals(morenbk)) {
					if (zdyflag) {
						bitmapUtils.display(keybcksmall, morenbk);
					} else {
						bitmapUtils.display(keybcksmall, "assets/"
								+ Constants.KEYBACK + "/" + morenbk);
					}
					keybcksmall.setVisibility(View.VISIBLE);
				} else {
					keybcksmall.setVisibility(View.INVISIBLE);
				}
			} else if (msg.what == 2) {
				int FNUM = SharedPreferencesTools.getSharedPreferences_4FIRSTY(
						Set4Keyboard.this).getInt(
						SharedPreferencesTools.firstyhome, 1);
				switch (FNUM) {
				case 1:
					sxxtext.setText("拨号");
					break;
				case 2:
					sxxtext.setText("通讯录");
					break;
				case 3:
					sxxtext.setText("通话记录");
					break;
				case 4:
					sxxtext.setText("发现");
					break;
				case 5:
					sxxtext.setText("我");
					break;
				case 6:
					sxxtext.setText("附近门店");
					break;
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set4keyboard);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		yinchanglayout = (LinearLayout) this.findViewById(R.id.yinchanglayout);
		setts_recharge_bar_back = (ImageView) this
				.findViewById(R.id.setts_recharge_bar_back);
		setts_recharge_yd_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_yd_relayout);
		setts_recharge_lt_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_recharge_lt_relayout);
		setts_sxx_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_sxx_relayout);
		musictext = (TextView) this.findViewById(R.id.musictext);
		sxxtext = (TextView) this.findViewById(R.id.sxxtext);
		keybcksmall = (ImageView) this.findViewById(R.id.keybcksmall);
		upcontants_switch = (ToggleButton) this
				.findViewById(R.id.upcontants_switch);
		huibomusic_switch = (ToggleButton) this
				.findViewById(R.id.huibomusic_switch);
		outv2orv2_switch = (ToggleButton) this
				.findViewById(R.id.outv2orv2_switch);
		nerby_switch = (ToggleButton) this.findViewById(R.id.nerby_switch);
		nerby_edittext = (EditText) this.findViewById(R.id.nerby_edittext);
		removelayout.setOnClickListener(this);
		setts_recharge_bar_back.setOnClickListener(this);
		setts_recharge_yd_relayout.setOnClickListener(this);
		setts_recharge_lt_relayout.setOnClickListener(this);
		setts_sxx_relayout.setOnClickListener(this);
		upcontants_switch.setOnCheckedChangeListener(this);
		huibomusic_switch.setOnCheckedChangeListener(this);
		bitmapUtils = new BitmapUtils(this);
		musicnames4py = GetMusicName4PY.getMusicNames();
		musicoldname = SharedPreferencesTools.getSharedPreferences_4KEYMUSIC(
				this).getString(SharedPreferencesTools.KEYMUSIC_KEY, "无");
		String msname4cn = musicnames4py.get(musicoldname);
		if (msname4cn != null) {
			musictext.setText(msname4cn);
		} else {
			musictext.setText(musicoldname);
		}
		sharepreferens = SharedPreferencesTools
				.getSharedPreferences_ALLSWITCH(this);
		boolean OpenMUSIC = sharepreferens.getBoolean(
				SharedPreferencesTools.OpenMUSIC, true);
		huibomusic_switch.setChecked(OpenMUSIC);
		int UPC_sNUM = sharepreferens.getInt(
				SharedPreferencesTools.UPContant2Service, 1);
		if (UPC_sNUM == 1) {
			upcontants_switch.setChecked(true);
		} else if (UPC_sNUM == 2) {
			upcontants_switch.setChecked(false);
		}
		ycsz = SharedPreferencesTools.getSharedPreferences_4YCSZ(this);
		String num_vout = ycsz.getString(SharedPreferencesTools.ycsz_outv,
				Constants.calloutactivity);
		if (num_vout.equals("1")) {
			outv2orv2_switch.setChecked(true);
		} else if (num_vout.equals("2")) {
			outv2orv2_switch.setChecked(false);
		}
		nerby_edittext.setText(ycsz.getString(
				SharedPreferencesTools.ycsz_neary_name, ""));
		nerby_switch.setChecked(ycsz.getBoolean(
				SharedPreferencesTools.ycsz_neary_flag, false));
		outv2orv2_switch.setOnCheckedChangeListener(this);
		nerby_switch.setOnCheckedChangeListener(this);
		mHandler.sendEmptyMessage(1);
		mHandler.sendEmptyMessage(2);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.setts_recharge_bar_back) {
			finish();
		} else if (id == R.id.removelayout) {
			clicknum += 1;
			if (clicknum == 10) {
				yinchanglayout.setVisibility(View.VISIBLE);
			}
		} else if (id == R.id.setts_recharge_yd_relayout) {
			// 按键音设置
			Intent intent = new Intent(Set4Keyboard.this,
					org.aisin.sipphone.setts.SetKeyboardMusic.class);
			Set4Keyboard.this.startActivityForResult(intent, 1);
		} else if (id == R.id.setts_recharge_lt_relayout) {
			// 设置背景
			Intent intent = new Intent(Set4Keyboard.this,
					org.aisin.sipphone.setts.SetKeyback.class);
			Set4Keyboard.this.startActivityForResult(intent, 2);
		} else if (id == R.id.setts_sxx_relayout) {
			// 设置首页
			Intent intent = new Intent(Set4Keyboard.this,
					org.aisin.sipphone.setts.FirstActivity.class);
			Set4Keyboard.this.startActivityForResult(intent, 3);
		}
	}

	@Override
	protected void onDestroy() {
		ycsz.edit()
				.putString(SharedPreferencesTools.ycsz_neary_name,
						nerby_edittext.getText().toString()).commit();
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null) {
			String rawResult = data.getStringExtra("rawResult");
			if (!musicoldname.equals(rawResult)) {
				musicoldname = rawResult;
				String msname4cn = musicnames4py.get(rawResult);
				if (msname4cn != null) {
					musictext.setText(msname4cn);
				} else {
					musictext.setText(rawResult);
				}
			}
		} else if (requestCode == 2) {
			mHandler.sendEmptyMessage(1);
		} else if (requestCode == 3) {
			mHandler.sendEmptyMessage(2);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		switch (id) {
		case R.id.huibomusic_switch:
			sharepreferens.edit()
					.putBoolean(SharedPreferencesTools.OpenMUSIC, isChecked)
					.commit();
			break;
		case R.id.upcontants_switch:
			if (isChecked) {
				sharepreferens.edit()
						.putInt(SharedPreferencesTools.UPContant2Service, 1)
						.commit();
			} else {
				sharepreferens.edit()
						.putInt(SharedPreferencesTools.UPContant2Service, 2)
						.commit();
			}
			break;
		case R.id.outv2orv2_switch:
			if (isChecked) {
				ycsz.edit().putString(SharedPreferencesTools.ycsz_outv, "1")
						.commit();
			} else {
				ycsz.edit().putString(SharedPreferencesTools.ycsz_outv, "2")
						.commit();
			}
			break;
		case R.id.nerby_switch:
			ycsz.edit()
					.putBoolean(SharedPreferencesTools.ycsz_neary_flag,
							isChecked).commit();
			break;
		}
	}

}
