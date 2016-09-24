package org.aisin.sipphone.aipay;

/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import com.alipay.sdk.pay.demo.PayDemoActivity;
import com.weixin.paydemo.PayActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class AlixDemo extends Activity implements OnClickListener {
	static String TAG = "AppDemo4";
	// 模拟商户商品列表
	private ScrollView alipay_layout_linlayout;
	ArrayList<Products.ProductDetail> mproductlist;
	private ImageView setts_alipay_title_back_image;
	private TextView btnSupp;
	private CheckBox check1, check2, check3, check4, check5, check6, check7;
	private View lay1, lay2, lay3, lay4, lay5, lay6, lay7;
	private RadioGroup radioGroup;
	private RadioButton radioButton;
	int tempIndex = -1;
	private View viewZhi, viewBao;
	private CompoundButton tempView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_layout);
		initProductList();
		layInit();
	}

	void initProductList() {
		Products products = new Products();
		this.mproductlist = products.retrieveProductInfo();

		checkBoxInit();
		alipay_layout_linlayout = (ScrollView) this
				.findViewById(R.id.alipay_layout_linlayout);
		setts_alipay_title_back_image = (ImageView) this
				.findViewById(R.id.setts_alipay_title_back_image);
		setts_alipay_title_back_image.setOnClickListener(this);
		viewZhi = this.findViewById(R.id.alipay_zhichong);
		viewBao = this.findViewById(R.id.alipay_baoyue);

		radioGroup = (RadioGroup) this.findViewById(R.id.alipay_group);
		radioGroup
				.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.alipay_radiozhi:
							viewBao.setVisibility(View.GONE);
							viewZhi.setVisibility(View.VISIBLE);
							check1.setChecked(true);
							break;
						case R.id.alipay_radiobao:
							viewZhi.setVisibility(View.GONE);
							viewBao.setVisibility(View.VISIBLE);
							check5.setChecked(true);
							break;
						}
					}
				});

		radioButton = (RadioButton) this.findViewById(R.id.alipay_radiobao);
		radioButton.setChecked(true);

		btnSupp = (TextView) this.findViewById(R.id.alipay_supp);
		btnSupp.setOnClickListener(this);
	}

	private void checkBoxInit() {

		check1 = (CheckBox) this.findViewById(R.id.alipay_check1);
		check2 = (CheckBox) this.findViewById(R.id.alipay_check2);
		check3 = (CheckBox) this.findViewById(R.id.alipay_check3);
		check4 = (CheckBox) this.findViewById(R.id.alipay_check4);
		check5 = (CheckBox) this.findViewById(R.id.alipay_check5);
		check6 = (CheckBox) this.findViewById(R.id.alipay_check6);
		check7 = (CheckBox) this.findViewById(R.id.alipay_check7);

		CheckBoxListener checkListener = new CheckBoxListener();
		check1.setOnCheckedChangeListener(checkListener);
		check2.setOnCheckedChangeListener(checkListener);
		check3.setOnCheckedChangeListener(checkListener);
		check4.setOnCheckedChangeListener(checkListener);
		check5.setOnCheckedChangeListener(checkListener);
		check6.setOnCheckedChangeListener(checkListener);
		check7.setOnCheckedChangeListener(checkListener);
	}

	private void layInit() {
		LayLinstener layListener = new LayLinstener();
		lay1 = this.findViewById(R.id.alipay_lay1);
		lay1.setOnClickListener(layListener);
		lay2 = this.findViewById(R.id.alipay_lay2);
		lay2.setOnClickListener(layListener);
		lay3 = this.findViewById(R.id.alipay_lay3);
		lay3.setOnClickListener(layListener);
		lay4 = this.findViewById(R.id.alipay_lay4);
		lay4.setOnClickListener(layListener);
		lay5 = this.findViewById(R.id.alipay_lay5);
		lay5.setOnClickListener(layListener);
		lay6 = this.findViewById(R.id.alipay_lay6);
		lay6.setOnClickListener(layListener);
		lay7 = this.findViewById(R.id.alipay_lay7);
		lay7.setOnClickListener(layListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alipay_supp:
			final int index = tempIndex;
			onItemClickDo(index);
			break;
		case R.id.setts_alipay_title_back_image:
			finish();
			break;
		}
	}

	public void onItemClickDo(int index) {
		String subject = mproductlist.get(index).subject;
		String body = mproductlist.get(index).body;
		String total_fee = mproductlist.get(index).price;
		// 支付
		new PayDemoActivity(AlixDemo.this, subject, body, total_fee).pay();
		// new PayActivity(this).Pay();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(alipay_layout_linlayout);// 回收容
	}

	private class CheckBoxListener implements
			android.widget.CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			int checkBoxId = buttonView.getId();
			if (isChecked && buttonView != tempView) {
				if (tempView != null) {
					tempView.setOnCheckedChangeListener(null);
					tempView.setChecked(false);
					tempView.setOnCheckedChangeListener(this);
				}
				tempView = buttonView;
				switch (checkBoxId) {
				case R.id.alipay_check1:
					tempIndex = 0;
					break;
				case R.id.alipay_check2:
					tempIndex = 1;
					break;
				case R.id.alipay_check3:
					tempIndex = 2;
					break;
				case R.id.alipay_check4:
					tempIndex = 3;
					break;
				case R.id.alipay_check5:
					tempIndex = 4;
					break;
				case R.id.alipay_check6:
					tempIndex = 5;
					break;
				case R.id.alipay_check7:
					tempIndex = 6;
					break;
				}
			} else {
				buttonView.setChecked(true);
			}
		}
	}

	private class LayLinstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.alipay_lay1:
				check1.setChecked(true);
				break;
			case R.id.alipay_lay2:
				check2.setChecked(true);
				break;
			case R.id.alipay_lay3:
				check3.setChecked(true);
				break;
			case R.id.alipay_lay4:
				check4.setChecked(true);
				break;
			case R.id.alipay_lay5:
				check5.setChecked(true);
				break;
			case R.id.alipay_lay6:
				check6.setChecked(true);
				break;
			case R.id.alipay_lay7:
				check7.setChecked(true);
				break;
			}
		}

	}
}