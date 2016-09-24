package org.aisin.sipphone.sqlitedb;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TreeSet;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.AisinApp;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CallhistoryDBTOOls {
	private static TreeSet<CallhistoryInfo> set = new TreeSet<CallhistoryInfo>();
	private static Calendar current = Calendar.getInstance();
	private static Calendar day_tmonth = Calendar.getInstance(); // 一个月前
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat sdformat_5 = new SimpleDateFormat(
			"yyyyMMdd");
	private static int today = 0;
	static {
		day_tmonth.set(Calendar.YEAR, current.get(Calendar.YEAR));
		day_tmonth.set(Calendar.MONTH, current.get(Calendar.MONTH));
		day_tmonth.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 30);
		day_tmonth.set(Calendar.HOUR_OF_DAY, 0);
		day_tmonth.set(Calendar.MINUTE, 0);
		day_tmonth.set(Calendar.SECOND, 0);
		today = Integer.parseInt(sdformat_5.format(day_tmonth.getTime()));
	}

	public static TreeSet<CallhistoryInfo> getchinfo(Context context) {
		synchronized (CallhistoryDBTOOls.class) {
			if (context == null) {
				context = AisinApp.getInstance().getApplicationContext();
			}
			SQLiteDatabase rd = MycallhistoryDB.getMDD(context)
					.getReadableDatabase();
			Cursor csr = rd.query("callhd_datatable", new String[] { "name",
					"phone", "call_type", "call_type_name", "call_time",
					"duration", "ID" }, "calltime>?",
					new String[] { today + "" }, null, null, "call_time");
			if (csr != null && csr.moveToFirst()) {
				set.clear();
				do {
					String name = csr.getString(0);
					String phone = csr.getString(1);
					int call_type = csr.getInt(2);
					String call_type_name = csr.getString(3);
					int call_type_imageid = R.drawable.call_status_outgoing;
					if (call_type == 1) {
						call_type_imageid = R.drawable.call_status_incoming;
					} else if (call_type == 3) {
						call_type_imageid = R.drawable.call_status_missed;
					} else if (call_type == 0) {
						call_type_imageid = R.drawable.call_status_reject;
					}
					String call_time = csr.getString(4);
					String duration = csr.getString(5);
					set.add(new CallhistoryInfo(csr.getString(6), name, phone,
							call_type, call_type_name, call_type_imageid,
							call_time, "", duration, true));
				} while (csr.moveToNext());
				csr.close();
			}
			rd.close();
			return set;
		}
	}

	public static void addhistory(Context context, ContentValues cv) {
		synchronized (CallhistoryDBTOOls.class) {
			SQLiteDatabase rd = MycallhistoryDB.getMDD(context)
					.getWritableDatabase();
			rd.insert("callhd_datatable", null, cv);
			rd.close();
			deletedata(context);// 删除一个月之前的数据
		}
	}

	private static void deletedata(Context context) {
		SQLiteDatabase rd = MycallhistoryDB.getMDD(context)
				.getWritableDatabase();
		rd.delete("callhd_datatable", "calltime<?", new String[] { today + "" });
		rd.close();
	}

	public static boolean DeletedataBYW(Context context, String wheres,
			String[] values) {
		try {
			SQLiteDatabase rd = MycallhistoryDB.getMDD(context)
					.getWritableDatabase();
			rd.delete("callhd_datatable", wheres, values);
			rd.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
