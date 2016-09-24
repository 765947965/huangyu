package org.aisin.sipphone;

import org.aisin.sipphone.mai_list.OnListnerShearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class LineEditText extends EditText {

	private OnListnerShearch shearch;

	private Paint mPaint;

	/**
	 * @param context
	 * @param attrs
	 */
	public LineEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mPaint = new Paint();

		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.GRAY);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 画底线
		// canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
		// this.getHeight() - 1, mPaint);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		if (shearch != null) {
			shearch.Search(text.toString().trim());
		}
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

	public void setShearchListner(OnListnerShearch shearch) {
		this.shearch = shearch;
	}
}