package org.aisin.sipphone.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitMapcreatPath2smallSize {

	public static Bitmap GetBitmap4Path2samll(String path) {
		try {
			Bitmap temp = BitmapFactory.decodeFile(path);
			int height = temp.getHeight();
			int width = temp.getWidth();
			int temph = height > width ? height : width;
			float sflv = 100f / (float) temph;
			Matrix matrix = new Matrix();
			matrix.setScale(sflv, sflv);
			Bitmap bitmap_1 = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(),
					temp.getHeight(), matrix, true);
			temp.recycle();
			temp = null;
			return bitmap_1;
		} catch (Exception e) {
			return null;
		}catch (Error e) {
			return null;
		}
	}
}
