package org.aisin.sipphone.sqlitedb;

import org.aisin.sipphone.tools.UserInfo_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Frends_Data_DB extends SQLiteOpenHelper {
	private final static int version = 1;

	private Frends_Data_DB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	private Frends_Data_DB(Context context) {
		this(context, UserInfo_db.getUserInfo(context).getUid() + "_frends",
				null, version);
	}

	public static Frends_Data_DB getMDD(Context context) {
		if (UserInfo_db.getUserInfo(context) == null) {
			return null;
		}
		return new Frends_Data_DB(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatsql = "CREATE TABLE friends_datatable (uid INT PRIMARY KEY NOT NULL,phone TEXT,province TEXT,picture TEXT,picmd5 TEXT,ver TEXT,city TEXT,company TEXT,profession TEXT,school TEXT,sex TEXT,birthday TEXT,picurl_prefix TEXT,location TEXT,signature TEXT,from_self TEXT,mobileNumber TEXT,email TEXT,name TEXT,remark TEXT,tx_image BLOB,tx_imagedownflag INTEGER);";
		db.execSQL(creatsql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
