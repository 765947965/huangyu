package org.aisin.sipphone.aipay;

import java.util.ArrayList;

public class NewProduct {
	public static ArrayList<Productinfos> getproducts_WX() {
		ArrayList<Productinfos> pcs = new ArrayList<Productinfos>();

		pcs.add(new Productinfos("直冲", 2, "充50元到账200元", 50, "90050", true));
		pcs.add(new Productinfos("直冲", 2, "充100元到账500元", 100, "90100", false));
		pcs.add(new Productinfos("直冲", 2, "充200元到账1000元", 200, "90200", false));
		pcs.add(new Productinfos("直冲", 2, "充500元到账2500元", 500, "90500", false));
		pcs.add(new Productinfos("包月", 1, "90元包3个月套餐", 90, "1", true));
		pcs.add(new Productinfos("包月", 1, "180元包6个月套餐", 180, "18", false));
		pcs.add(new Productinfos("包月", 1, "365元包12个月套餐", 365, "12", false));

		return pcs;
	}

	public static ArrayList<Productinfos> getproducts_ZFB() {
		ArrayList<Productinfos> pcs = new ArrayList<Productinfos>();

		pcs.add(new Productinfos("直冲", 2, "充50元到账100元", 50, "", true));
		pcs.add(new Productinfos("直冲", 2, "充100元到账200元", 100, "", false));
		pcs.add(new Productinfos("直冲", 2, "充200元到账400元", 200, "", false));
		//pcs.add(new Productinfos("直冲", 2, "充300元到账1000元", 300, "", false));
		pcs.add(new Productinfos("包月", 1, "90元包3个月套餐", 90, "", true));
		pcs.add(new Productinfos("包月", 1, "180元包6个月套餐", 180, "", false));
		pcs.add(new Productinfos("包月", 1, "365元包12个月套餐", 365, "", false));
		return pcs;
	}
}
