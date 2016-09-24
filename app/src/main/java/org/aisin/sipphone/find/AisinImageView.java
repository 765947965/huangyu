package org.aisin.sipphone.find;

import org.aisin.sipphone.tianyu.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AisinImageView extends LinearLayout {

	private TextView tv_name;
	private ImageView img_pic;
	View view;
	private LinearLayout linearLayout;

	public AisinImageView(Context context) {
		super(context);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	public AisinImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		// TODO Auto-generated constructor stub
	}

	@SuppressLint("NewApi")
	public AisinImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	private void initView(Context context) {
		view = LayoutInflater.from(context).inflate(R.layout.linear_layout,
				this, true);
		linearLayout = (LinearLayout) view
				.findViewById(R.id.lin_aisinimageview);
		tv_name = (TextView) view.findViewById(R.id.tvaisin01);
		img_pic = (ImageView) view.findViewById(R.id.imgimgview02);
	}

	public void setText(String name) {
		tv_name.setText(name);
	}

	public void setImageViewColor(int color) {
		img_pic.setBackgroundColor(color);

	}

	public void setImageResource(Bitmap bitMap) {
		img_pic.setImageBitmap(bitMap);
	}

	public void setTextColor(int color) {
		tv_name.setTextColor(color);
	}

	@Override
	public void setBackgroundResource(int resid) {

		linearLayout.setBackgroundResource(resid);
	}

	public ImageView getImg_pic() {
		return img_pic;
	}

	public void setImg_pic(ImageView img_pic) {
		this.img_pic = img_pic;
	}

	public String getText() {
		return tv_name.getText().toString();
	}

}
