package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ShowImage extends Activity {

	private RelativeLayout removelayout;
	private ImageView showiamge_imange;
	private byte[] bitmapByte;
	private String path;
	private Bitmap bitmap;
	private Bitmap bitmap_1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bitmapByte = this.getIntent().getByteArrayExtra("imagepath_byte");
		path = this.getIntent().getStringExtra("imagepath");
		if ((path == null || "".equals(path) || "null".equals(path))
				&& (bitmapByte == null || bitmapByte.length == 0)) {
			finish();
			return;
		}
		setContentView(R.layout.showimage);
		removelayout = (RelativeLayout) this.findViewById(R.id.removelayout);
		showiamge_imange = (ImageView) this.findViewById(R.id.showiamge_imange);
		if (bitmapByte != null && bitmapByte.length > 0) {
			bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0,
					bitmapByte.length);
		} else if (path != null && !"".equals(path) && !"null".equals(path)) {
			bitmap = BitmapFactory.decodeFile(path);
		}
		if (bitmap != null) {
			DisplayMetrics dm = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			int screenHeight = dm.heightPixels;
			float sflv_w = (float) screenWidth / (float) bitmap.getWidth();
			float sflv_h = (float) screenHeight / (float) bitmap.getHeight();
			float sflv = sflv_w < sflv_h ? sflv_w : sflv_h;
			if (sflv == 1.0f) {
				sflv = 0.8f;
			}
			Matrix matrix = new Matrix();
			matrix.setScale(sflv, sflv);
			bitmap_1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			bitmap = null;
			showiamge_imange.setImageBitmap(bitmap_1);
		} else {
			finish();
			return;
		}
		removelayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowImage.this.finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (bitmap_1 != null) {
			bitmap_1.recycle();
			bitmap_1 = null;
		}
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}

}
