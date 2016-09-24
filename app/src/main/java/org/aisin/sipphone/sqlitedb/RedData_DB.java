package org.aisin.sipphone.sqlitedb;

import org.aisin.sipphone.tools.UserInfo_db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RedData_DB extends SQLiteOpenHelper {

	private final static int version = 1;

	private RedData_DB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	private RedData_DB(Context context) {
		this(context, UserInfo_db.getUserInfo(context).getUid() + "_reddatadb",
				null, version);
	}

	public static RedData_DB getMDD(Context context) {
		if (UserInfo_db.getUserInfo(context) == null) {
			return null;
		}
		return new RedData_DB(context);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatsql = "CREATE TABLE reddatadb_datatable (gift_id TEXT PRIMARY KEY,splitsnumber TEXT,shake_ratio TEXT,received_money TEXT,status TEXT,returned_money TEXT,sub_type TEXT,command TEXT,uidfrom TEXT,open_time TEXT,money TEXT,has_open INTEGER,direct TEXT,create_time TEXT,from_phone TEXT,fromnickname TEXT,money_type TEXT,tips TEXT,exp_time TEXT,type TEXT,sender_gift_id TEXT,name TEXT,year Text,monthy Text);create index idxmonthy on reddatadb_datatable(monthy);create index idxyear on reddatadb_datatable(year);create index idxdirect on reddatadb_datatable(direct);create index idxcreate_time on reddatadb_datatable(create_time);";
		db.execSQL(creatsql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
