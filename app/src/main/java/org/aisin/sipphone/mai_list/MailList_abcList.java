package org.aisin.sipphone.mai_list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class MailList_abcList extends View {
	private String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
	private Paint paint = new Paint();

	public MailList_abcList(Context context) {
		super(context);
	}

	public MailList_abcList(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MailList_abcList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int h = getHeight();
		int h2 = h / str.length();
		int width = getWidth();
		for (int i = 0; i < str.length(); i++) {
			paint.setTypeface(Typeface.MONOSPACE);
			paint.setAntiAlias(true);
			paint.setTextSize((float) (h2 * 0.6));
			paint.setColor(Color.BLACK);
			float xPos = width / 2 - paint.measureText(str.substring(i, i + 1))
					/ 2;
			float yPos = h2 * i + h2;
			canvas.drawText(str.substring(i, i + 1), xPos, yPos, paint);
			paint.reset();
		}
	}
}
