package org.aisin.sipphone.mai_list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class SearchText extends EditText {

	private OnListnerShearch shearch;

	public SearchText(Context context) {
		super(context);
	}

	public SearchText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SearchText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
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
