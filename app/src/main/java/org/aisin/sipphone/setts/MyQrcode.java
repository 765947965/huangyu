package org.aisin.sipphone.setts;

import java.io.File;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyQrcode extends Activity implements OnClickListener {
	private RelativeLayout removelayout;
	private ImageView myqrcode_back;
	private ImageView myqrcode_tx;
	private TextView name_text;
	private ImageView seximage;
	private TextView address;
	private ImageView myqrcodeimage;

	private Bitmap bitmap_qr;
	private Bitmap bitmap_tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myqrcode);
		removelayout = (RelativeLayout) this.findViewById(R.id.removelayout);
		myqrcode_back = (ImageView) this.findViewById(R.id.myqrcode_back);
		myqrcode_tx = (ImageView) this.findViewById(R.id.myqrcode_tx);
		name_text = (TextView) this.findViewById(R.id.name);
		seximage = (ImageView) this.findViewById(R.id.seximage);
		address = (TextView) this.findViewById(R.id.address);
		myqrcodeimage = (ImageView) this.findViewById(R.id.myqrcodeimage);
		myqrcode_back.setOnClickListener(this);

		UserXXInfo uxxi = User_data_Ts.getUXXInfo4DB_self(this, UserInfo_db
				.getUserInfo(this).getUid());
		if (uxxi != null) {
			File file = this.getFileStreamPath(uxxi.getUid() + "headimage.jpg");
			if (file.exists()) {
				bitmap_tx = BitmapFactory.decodeFile(file.getAbsolutePath());
				if (bitmap_tx != null) {
					myqrcode_tx.setImageBitmap(bitmap_tx);
				}
			}
			String name = uxxi.getName();
			if (name != null && !"".equals(name) && !"null".equals(name)) {
				name_text.setText(name);
			} else {
				name_text.setText(uxxi.getMobileNumber());
			}
			String sex = uxxi.getSex();
			if ("男".equals(sex)) {
				seximage.setImageResource(R.drawable.one_profile_male_left_dark);

			} else if ("女".equals(sex)) {
				seximage.setImageResource(R.drawable.one_profile_female_left_dark);

			}
			String province = uxxi.getProvince();
			String city = uxxi.getCity();
			if (province != null && !"".equals(province)
					&& !"null".equals(province) && city != null
					&& !"".equals(city) && !"null".equals(city)) {
				address.setText(province + "  " + city);
				address.setVisibility(View.VISIBLE);
			}
		}

		// 二维码图片生成
		try {
			SharedPreferences shared_SNSshare = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(MyQrcode.this);
			String invite_url = shared_SNSshare.getString(
					SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
			String text = invite_url
					.replace(
							"phone=%s",
							"phone="
									+ UserInfo_db.getUserInfo(MyQrcode.this)
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
			bitmap_qr = Bitmap.createBitmap(w, h, Config.RGB_565);
			bitmap_qr.setPixels(rawData, 0, w, 0, 0, w, h);
			if (bitmap_qr != null) {
				myqrcodeimage.setImageBitmap(bitmap_qr);
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.myqrcode_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap_qr != null) {
			bitmap_qr.recycle();
			bitmap_qr = null;
		}
		if (bitmap_tx != null) {
			bitmap_tx.recycle();
			bitmap_tx = null;
		}
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

}
