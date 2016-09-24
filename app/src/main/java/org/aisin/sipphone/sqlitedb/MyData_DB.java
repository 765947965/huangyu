package org.aisin.sipphone.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyData_DB extends SQLiteOpenHelper {
	private final static String name = "datadb";
	private final static int version = 1;
	private static MyData_DB mdd;

	private MyData_DB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	private MyData_DB(Context context) {
		this(context, name, null, version);
	}

	public static MyData_DB getMDD(Context context) {
		if (mdd == null) {
			mdd = new MyData_DB(context);
		}
		return mdd;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatsql = "CREATE TABLE personal_datatable (uid INT PRIMARY KEY NOT NULL,province TEXT,picture TEXT,picmd5 TEXT,ver TEXT,city TEXT,company TEXT,profession TEXT,school TEXT,sex TEXT,birthday TEXT,picurl_prefix TEXT,location TEXT,signature TEXT,from_self TEXT,mobileNumber TEXT,email TEXT,name TEXT);";
		db.execSQL(creatsql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
