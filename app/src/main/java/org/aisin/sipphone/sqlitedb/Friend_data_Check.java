package org.aisin.sipphone.sqlitedb;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import org.aisin.sipphone.commong.Contact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Friend_data_Check {

	public static Contact Checkdata4Sqlit(Context context, String uid,
			boolean flag) {// flag为false只获取ver,为TURE获取所有信息
		Frends_Data_DB fdb = Frends_Data_DB.getMDD(context);
		if (fdb == null) {
			return null;
		}
		String[] columns = null;
		if (flag) {
			columns = new String[] { "uid", "ver", "picture", "picmd5",
					"signature", "name", "remark", "mobileNumber", "phone",
					"sex", "birthday", "tx_imagedownflag", "province",
					"company", "profession", "school", "city" };
		} else {
			columns = new String[] { "uid", "ver", "picmd5" };
		}
		SQLiteDatabase rd = fdb.getReadableDatabase();
		Cursor cs = rd.query("friends_datatable", columns, "uid=?",
				new String[] { uid }, null, null, null);
		if (cs.moveToFirst()) {
			if (flag) {
				String uidck = cs.getInt(0) + "";
				String ver = cs.getString(1);
				String picture = cs.getString(2);
				String picmd5 = cs.getString(3);
				String signature = cs.getString(4);
				String name = cs.getString(5);// 昵称
				String remark = cs.getString(6);// 本地通讯录上传的名称
				String mobileNumber = cs.getString(7);
				String phone = cs.getString(8);
				String sex = cs.getString(9);
				String birthday = cs.getString(10);
				int tx_imagedownflag = cs.getInt(11);
				String province = cs.getString(12);
				String company = cs.getString(13);
				String profession = cs.getString(14);
				String school = cs.getString(15);
				String city = cs.getString(16);
				Contact ctt = new Contact();
				ctt.setIsfreand(true);
				ctt.setUid(uidck);
				ctt.setVer(ver);
				ctt.setPicture(picture);
				ctt.setPicmd5(picmd5);
				ctt.setSignature(signature);
				ctt.setFriendname(name);
				ctt.setRemark(remark);
				ctt.setMobileNumber(mobileNumber);
				ctt.setFriendphone(phone);
				ctt.getPhonesList().add(phone);
				ctt.setSex(sex);
				ctt.setBthday(birthday);
				ctt.setTx_imagedownflag(tx_imagedownflag);
				ctt.setProvince(province);
				ctt.setCompany(company);
				ctt.setProfession(profession);
				ctt.setSchool(school);
				ctt.setCity(city);
				cs.close();
				rd.close();
				return ctt;
			} else {
				String uidck = cs.getInt(0) + "";
				String ver = cs.getString(1);
				String picmd5 = cs.getString(2);
				Contact ctt = new Contact();
				ctt.setUid(uidck);
				ctt.setVer(ver);
				ctt.setPicmd5(picmd5);
				cs.close();
				rd.close();
				fdb.close();
				return ctt;
			}
		} else {
			rd.close();
			fdb.close();
			return null;
		}
	}

	public static ArrayList<Contact> GetAllFriends(Context context) {
		Frends_Data_DB fdb = Frends_Data_DB.getMDD(context);
		if (fdb == null) {
			return null;
		}
		String[] columns = new String[] { "uid", "ver", "picture", "picmd5",
				"signature", "name", "remark", "mobileNumber", "phone", "sex",
				"birthday", "tx_imagedownflag", "province", "company",
				"profession", "school", "city" };
		SQLiteDatabase rd = fdb.getReadableDatabase();
		Cursor cs = rd.query("friends_datatable", columns, null, null, null,
				null, null);
		if (cs == null || !cs.moveToFirst()) {
			if (cs != null) {
				cs.close();
			}
			rd.close();
			fdb.close();
			return null;
		}
		ArrayList<Contact> ctttemps = new ArrayList<Contact>();
		do {
			String uidck = cs.getInt(0) + "";
			String ver = cs.getString(1);
			String picture = cs.getString(2);
			String picmd5 = cs.getString(3);
			String signature = cs.getString(4);
			String name = cs.getString(5);// 昵称
			String remark = cs.getString(6);// 本地通讯录上传的名称
			String mobileNumber = cs.getString(7);
			String phone = cs.getString(8);
			String sex = cs.getString(9);
			String birthday = cs.getString(10);
			int tx_imagedownflag = cs.getInt(11);
			String province = cs.getString(12);
			String company = cs.getString(13);
			String profession = cs.getString(14);
			String school = cs.getString(15);
			String city = cs.getString(16);
			Contact ctt = new Contact();
			ctt.setShowflag(true);
			ctt.setIsfreand(true);
			ctt.setUid(uidck);
			ctt.setVer(ver);
			ctt.setPicture(picture);
			ctt.setPicmd5(picmd5);
			ctt.setSignature(signature);
			ctt.setFriendname(name);
			ctt.setRemark(remark);
			ctt.setMobileNumber(mobileNumber);
			ctt.setFriendphone(phone);
			ctt.getPhonesList().add(phone);
			ctt.setSex(sex);
			ctt.setBthday(birthday);
			ctt.setTx_imagedownflag(tx_imagedownflag);
			ctt.setProvince(province);
			ctt.setCompany(company);
			ctt.setProfession(profession);
			ctt.setSchool(school);
			ctt.setCity(city);
			ctttemps.add(ctt);
		} while (cs.moveToNext());
		cs.close();
		rd.close();
		fdb.close();
		return ctttemps;
	}

	public static void add4DataupFriends(Context context, String uid,
			ContentValues cv) {
		Contact ctt = Checkdata4Sqlit(context, uid, false);
		Frends_Data_DB fdb = Frends_Data_DB.getMDD(context);
		if (fdb == null) {
			return;
		}
		SQLiteDatabase sqd = fdb.getWritableDatabase();
		if (ctt == null) {
			sqd.insert("friends_datatable", null, cv);
		} else {
			sqd.update("friends_datatable", cv, "uid=?",
					new String[] { uid.trim() });
		}
		sqd.close();
		fdb.close();
	}

	public static void UPdataMoreFriends(Context context,
			TreeMap<String, ContentValues> cvsmap) {
		if (context != null && cvsmap != null) {
			ArrayList<Contact> cttslist = GetAllFriends(context);
			TreeMap<String, Contact> cttsmap = new TreeMap<String, Contact>();
			if (cttslist != null) {
				for (Contact ct : cttslist) {
					cttsmap.put(ct.getUid(), ct);
				}
			}
			Frends_Data_DB fdb = Frends_Data_DB.getMDD(context);
			SQLiteDatabase sqd = null;
			try {
				sqd = fdb.getWritableDatabase();
				sqd.beginTransaction();

				for (Entry<String, ContentValues> ents : cvsmap.entrySet()) {
					if (cttsmap.get(ents.getKey()) != null) {
						sqd.update("friends_datatable", ents.getValue(),
								"uid=?", new String[] { ents.getKey().trim() });
					} else {
						sqd.insert("friends_datatable", null, ents.getValue());
					}
				}
				sqd.setTransactionSuccessful();
				cttsmap.clear();
				cttsmap = null;
			} catch (Exception e) {
			} finally {
				if (sqd != null) {
					sqd.endTransaction();
					sqd.close();
				}
				if (fdb != null) {
					fdb.close();
				}
				cvsmap.clear();
				cvsmap = null;
			}
		}
	}
}
