package org.aisin.sipphone.sqlitedb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MycallhistoryDB extends SQLiteOpenHelper {
	private final static String name = "callhistorydatadb";
	private final static int version = 1;
	private static MycallhistoryDB mdd;

	private MycallhistoryDB(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	private MycallhistoryDB(Context context) {
		this(context, name, null, version);
	}

	public static MycallhistoryDB getMDD(Context context) {
		if (mdd == null) {
			mdd = new MycallhistoryDB(context);
		}
		return mdd;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatsql = "CREATE TABLE callhd_datatable (ID INTEGER PRIMARY KEY AUTOINCREMENT,phone TEXT,name TEXT,call_type INT,call_type_name TEXT,call_time TEXT,chainal_call_time TEXT,duration TEXT,calltime INT);";
		db.execSQL(creatsql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
