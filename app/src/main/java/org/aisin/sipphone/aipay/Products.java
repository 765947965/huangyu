package org.aisin.sipphone.aipay;

/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

import java.util.ArrayList;

/**
 * demo展示商品列表的商品信息
 * 
 */
public final class Products {

	public class ProductDetail {
		String subject;
		String body;
		String price;
		int resId;
	}

	ArrayList<Products.ProductDetail> mproductlist = new ArrayList<Products.ProductDetail>();

	public ArrayList<Products.ProductDetail> retrieveProductInfo() {
		ProductDetail productDetail = null;

		productDetail = new ProductDetail();
		productDetail.subject = "直冲";
		productDetail.body = "50元直冲";
		productDetail.price = "50";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		productDetail = new ProductDetail();
		productDetail.subject = "直冲";
		productDetail.body = "100元直冲";
		productDetail.price = "100";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		productDetail = new ProductDetail();
		productDetail.subject = "直冲";
		productDetail.body = "200元直冲";
		productDetail.price = "200";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		productDetail = new ProductDetail();
		productDetail.subject = "直冲";
		productDetail.body = "500元直冲";
		productDetail.price = "500";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		//
		productDetail = new ProductDetail();
		productDetail.subject = "包月";
		productDetail.body = "90元包月";
		productDetail.price = "90";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		productDetail = new ProductDetail();
		productDetail.subject = "包月";
		productDetail.body = "180元包月";
		productDetail.price = "180";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		productDetail = new ProductDetail();
		productDetail.subject = "包月";
		productDetail.body = "365元包月";
		productDetail.price = "365";
		productDetail.resId = 30;
		mproductlist.add(productDetail);

		return mproductlist;
	}
}
