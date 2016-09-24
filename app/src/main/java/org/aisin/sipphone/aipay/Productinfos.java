package org.aisin.sipphone.aipay;

public class Productinfos {
	private String subject;
	private int goodstype;
	private String body;
	private int price;
	private String goodsId;
	private boolean checked;

	public Productinfos(String subject, int goodstype, String body,
			int price, String goodsId, boolean checked) {
		super();
		this.subject = subject;
		this.goodstype = goodstype;
		this.body = body;
		this.price = price;
		this.goodsId = goodsId;
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getSubject() {
		return subject;
	}

	public int getGoodstype() {
		return goodstype;
	}

	public String getBody() {
		return body;
	}

	public int getPrice() {
		return price;
	}

	public String getGoodsId() {
		return goodsId;
	}

}
