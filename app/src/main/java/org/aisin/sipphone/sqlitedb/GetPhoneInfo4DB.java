package org.aisin.sipphone.sqlitedb;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class GetPhoneInfo4DB {

	public static synchronized ArrayList<String> getInfo(Context context,
			ArrayList<String> phones) {
		try {
			ArrayList<String> arrylist = new ArrayList<String>();
			SQLiteDatabase sdbase = SQLiteDatabase.openOrCreateDatabase("/data"
					+ Environment.getDataDirectory().getAbsolutePath() + "/"
					+ context.getPackageName() + "/callHomeDB.db", null);
			for (int i = 0; i < phones.size(); i++) {
				String phone = phones.get(i);
				if (phone == null || phone.length() < 3 || phone.length() > 12) {
					arrylist.add("");
					continue;
				}
				if ((phone.startsWith("1") && phone.length() < 7)
						|| (phone.startsWith("0") && phone.length() < 3)) {
					arrylist.add("");
					continue;
				}

				StringBuilder strb = new StringBuilder();
				if (phone.length() >= 7 && phone.startsWith("1")) {
					String checkValue = phone.substring(0, 7);
					Cursor csr = sdbase.query("caller_loc",
							new String[] { "loca_code" }, "number=?",
							new String[] { checkValue }, null, null, null);
					if (csr != null && csr.moveToFirst()) {
						String loca_code = csr.getString(0);
						csr.close();
						csr = null;
						if (loca_code != null && !"".equals(loca_code)) {
							csr = sdbase.query("locat_info",
									new String[] { "location" }, "code=?",
									new String[] { loca_code }, null, null,
									null);
							if (csr != null && csr.moveToFirst()) {
								String location = csr.getString(0);
								strb.append(location);
								csr.close();
								csr = null;
							}
						}
					}
					if (csr != null) {
						csr.close();
						csr = null;
					}
					csr = sdbase.query("carrier_info",
							new String[] { "carrier" }, "prefix=?",
							new String[] { checkValue.substring(0, 3) }, null,
							null, null);
					if (csr != null && csr.moveToFirst()) {
						String carrier = csr.getString(0);
						strb.append(" " + carrier);
						csr.close();
						csr = null;
					}
					if (csr != null) {
						csr.close();
						csr = null;
					}
				} else {
					String checkValue = phone.substring(0, 3);
					Cursor csr = sdbase.query("area_info",
							new String[] { "loca_code" }, "number=?",
							new String[] { checkValue }, null, null, null);
					String loca_code = null;
					if (csr != null) {
						if (!csr.moveToFirst()) {
							csr.close();
							csr = null;
							if (phone.length() >= 4) {
								checkValue = phone.substring(0, 4);
								csr = sdbase.query("area_info",
										new String[] { "loca_code" },
										"number=?",
										new String[] { checkValue }, null,
										null, null);
								if (csr != null) {
									if (csr.moveToFirst()) {
										loca_code = csr.getString(0);
									}
									csr.close();
									csr = null;
								}
							}
						} else {
							loca_code = csr.getString(0);
							csr.close();
							csr = null;
						}
					}
					if (loca_code != null && !"".equals(loca_code)) {
						csr = sdbase.query("locat_info",
								new String[] { "location" }, "code=?",
								new String[] { loca_code }, null, null, null);
						if (csr != null && csr.moveToFirst()) {
							String location = csr.getString(0);
							strb.append(location);
							csr.close();
							csr = null;
						}
						if (csr != null) {
							csr.close();
							csr = null;
						}
					}
				}
				arrylist.add(strb.toString());
			}
			sdbase.close();
			return arrylist;
		} catch (Exception e) {
			return null;
		}
	}

}
