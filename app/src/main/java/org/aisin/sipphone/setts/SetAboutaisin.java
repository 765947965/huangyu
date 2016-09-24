package org.aisin.sipphone.setts;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.HttpTask_CheckUpdate;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetAboutaisin extends Activity implements OnClickListener {
	private LinearLayout setaboutaisin_lilayout;
	private ImageView setaboutaisin_bar_back;
	private ImageView qrcodeimage;
	private TextView version_value;
	private RelativeLayout setts_zfsm_relayout;
	private RelativeLayout setts_czsm_relayout;
	private RelativeLayout setts_bzzx_relayout;
	private RelativeLayout setts_kfrx_relayout;
 	private RelativeLayout setts_xbbtx_relayout;
	private RelativeLayout setts_jcxbb_relayout;
	private ProgressDialog prd;
	private SharedPreferences shared_msglist_date;
	private TextView upappver;
	private Bitmap bitmap;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				if (CheckUpadateTime.CheckResult_4newAPP(SetAboutaisin.this)) {
					// 显示new
					SharedPreferences sft = SharedPreferencesTools
							.getSharedPreferences_msglist_date_share(SetAboutaisin.this);
					String ver_gt = sft.getString(
							SharedPreferencesTools.upAPPVer, "");
					upappver.setText(ver_gt);
				} else {
					// 不显示
					upappver.setText("");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setaboutaisin);
		setaboutaisin_lilayout = (LinearLayout) this
				.findViewById(R.id.setaboutaisin_lilayout);
		setaboutaisin_bar_back = (ImageView) this
				.findViewById(R.id.setaboutaisin_bar_back);
		qrcodeimage = (ImageView) this.findViewById(R.id.qrcodeimage);
		version_value = (TextView) this.findViewById(R.id.version_value);
		setts_zfsm_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_zfsm_relayout);
		setts_czsm_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_czsm_relayout);
		setts_bzzx_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_bzzx_relayout);
		setts_kfrx_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_kfrx_relayout);
		setts_xbbtx_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_xbbtx_relayout);
		setts_jcxbb_relayout = (RelativeLayout) this
				.findViewById(R.id.setts_jcxbb_relayout);
		upappver = (TextView) this.findViewById(R.id.upappver);
		setaboutaisin_bar_back.setOnClickListener(this);
		setts_zfsm_relayout.setOnClickListener(this);
		setts_czsm_relayout.setOnClickListener(this);
		setts_bzzx_relayout.setOnClickListener(this);
		setts_kfrx_relayout.setOnClickListener(this);
		setts_xbbtx_relayout.setOnClickListener(this);
		setts_jcxbb_relayout.setOnClickListener(this);
		version_value.setText("环宇 "
				+ UserInfo_db.getUserInfo(SetAboutaisin.this).getV());
		shared_msglist_date = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(SetAboutaisin.this);

		// 二维码图片生成
		try {
			SharedPreferences shared_SNSshare = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(SetAboutaisin.this);
			String invite_url = shared_SNSshare.getString(
					SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
			String text = invite_url.replace(
					"phone=%s",
					"phone="
							+ UserInfo_db.getUserInfo(SetAboutaisin.this)
									.getPhone()).replace("channel=%s",
					"channel=" + Constants.BrandName);
			int w = 250;
			int h = 250;
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, w, h);
			int[] rawData = new int[w * h];
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					int color = Color.WHITE;
					if (matrix.get(i, j)) {
						color = Color.BLACK;
					}
					rawData[i + (j * w)] = color;
				}
			}
			bitmap = Bitmap.createBitmap(w, h, Config.RGB_565);
			bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
			if (bitmap != null) {
				qrcodeimage.setImageBitmap(bitmap);
			}
		} catch (Exception e) {
		}

		// 显示更新
		mHandler.sendEmptyMessage(1);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		switch (id) {
		case R.id.setaboutaisin_bar_back:
			finish();
			break;
		case R.id.setts_zfsm_relayout:
//			intent = new Intent(SetAboutaisin.this,
//					org.aisin.sipphone.setts.ShowRedUserTextView.class);
//			intent.putExtra("showkey", "zfsm");
//			startActivity(intent);
			String ur4 = SharedPreferencesTools
			.getSharedPreferences_msglist_date_share(
					AisinActivity.context)
			.getString(
					SharedPreferencesTools.SPF_msglist_date_servicepage_instructions_url,
					"");
			intent = new Intent(SetAboutaisin.this,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent.putExtra("flag_name", "使用说明");
			intent.putExtra("url_view", ur4);
			startActivity(intent);
			break;
		case R.id.setts_czsm_relayout:
			intent = new Intent(SetAboutaisin.this,
					org.aisin.sipphone.setts.ShowRedUserTextView.class);
			intent.putExtra("showkey", "czsm");
			startActivity(intent);
			break;
		case R.id.setts_xbbtx_relayout:
			intent = new Intent(SetAboutaisin.this,
					org.aisin.sipphone.BootPage.class);
			intent.putExtra("flag", "close");
			startActivity(intent);
			SetAboutaisin.this.overridePendingTransition(
					R.anim.aisinactivityinput, R.anim.startpageroutput);
			break;
		case R.id.setts_kfrx_relayout:
			String service_phone = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(SetAboutaisin.this)
					.getString(
							SharedPreferencesTools.SPF_msglist_date_service_phone,
							"4000711126");
			Toast.makeText(this, "环宇客服热线是 :" + service_phone, Toast.LENGTH_LONG)
					.show();

			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse("tel:" + service_phone));
			startActivity(callIntent);
			break;
		case R.id.setts_bzzx_relayout:
			intent = new Intent(SetAboutaisin.this,
					org.aisin.sipphone.setts.HelpNow.class);
			startActivity(intent);
			break;
		case R.id.setts_jcxbb_relayout:
			if (!Check_network.isNetworkAvailable(SetAboutaisin.this)) {
				Toast.makeText(this, "网络不可用,请检查网络连接!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			prd = new ProgressDialog(SetAboutaisin.this);
			prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			prd.setMessage("正在检查更新，请稍候...");
			prd.show();
			new HttpTask_CheckUpdate(SetAboutaisin.this, 1, prd, mHandler)
					.execute("CheckUpdate");
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		RecoveryTools.unbindDrawables(setaboutaisin_lilayout);// 回收容器
	}

}
