package org.aisin.sipphone.sqlitedb;

import org.aisin.sipphone.commong.UserXXInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class User_data_Ts {

	public static UserXXInfo getUXXInfo4DB_self(Context context, String checkuid) {
		try {
			SQLiteDatabase rd = MyData_DB.getMDD(context).getReadableDatabase();
			Cursor cs = rd.query("personal_datatable", null, "uid=?",
					new String[] { checkuid.trim() }, null, null, null);
			if (cs.moveToFirst()) {
				int uid = cs.getInt(cs.getColumnIndex("uid"));
				String province = cs.getString(cs.getColumnIndex("province"));
				String picture = cs.getString(cs.getColumnIndex("picture"));
				String picmd5 = cs.getString(cs.getColumnIndex("picmd5"));
				String ver = cs.getString(cs.getColumnIndex("ver"));
				String picurl_prefix = cs.getString(cs
						.getColumnIndex("picurl_prefix"));
				String city = cs.getString(cs.getColumnIndex("city"));
				String company = cs.getString(cs.getColumnIndex("company"));
				String profession = cs.getString(cs.getColumnIndex("profession"));
				String school = cs.getString(cs.getColumnIndex("school"));
				String sex = cs.getString(cs.getColumnIndex("sex"));
				String birthday = cs.getString(cs.getColumnIndex("birthday"));
				String location = cs.getString(cs.getColumnIndex("location"));
				String signature = cs.getString(cs.getColumnIndex("signature"));
				String from = cs.getString(cs.getColumnIndex("from_self"));
				String mobileNumber = cs.getString(cs
						.getColumnIndex("mobileNumber"));
				String email = cs.getString(cs.getColumnIndex("email"));
				String name = cs.getString(cs.getColumnIndex("name"));
				UserXXInfo uxi = new UserXXInfo();
				uxi.setUid(uid);
				uxi.setProvince(province);
				uxi.setPicture(picture);
				uxi.setPicmd5(picmd5);
				uxi.setVer(ver);
				uxi.setPicurl_prefix(picurl_prefix);
				uxi.setCity(city);
				uxi.setCompany(company);
				uxi.setProfession(profession);
				uxi.setSchool(school);
				uxi.setSex(sex);
				uxi.setBirthday(birthday);
				uxi.setLocation(location);
				uxi.setSignature(signature);
				uxi.setFrom(from);
				uxi.setMobileNumber(mobileNumber);
				uxi.setEmail(email);
				uxi.setName(name);
				cs.close();
				rd.close();
				return uxi;
			} else {
				cs.close();
				rd.close();
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static void add2upUXXI(Context context, String uid, ContentValues cv) {
		UserXXInfo uxxi = getUXXInfo4DB_self(context, uid);
		SQLiteDatabase rd = MyData_DB.getMDD(context).getWritableDatabase();
		if (uxxi != null) {
			// 修改
			rd.update("personal_datatable", cv, "uid=?",
					new String[] { uid.trim() });
		} else {
			rd.insert("personal_datatable", null, cv);
		}
		uxxi = null;
		rd.close();
	}
}
