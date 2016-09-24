package org.aisin.sipphone.sqlitedb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import org.aisin.sipphone.commong.RedObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RedData_DBHelp {

	public static ArrayList<RedObject> getAllReds(Context context, String year) {
		synchronized (RedData_DBHelp.class) {

			RedData_DB reddb = RedData_DB.getMDD(context);
			SQLiteDatabase rd = null;
			Cursor cs = null;
			if (reddb == null) {
				return null;
			}
			try {
				rd = reddb.getReadableDatabase();
				String[] columns = new String[] { "splitsnumber",
						"shake_ratio", "received_money", "status",
						"returned_money", "sub_type", "command", "uidfrom",
						"open_time", "gift_id", "money", "has_open", "direct",
						"create_time", "from_phone", "fromnickname",
						"money_type", "tips", "exp_time", "type",
						"sender_gift_id", "name" };
				cs = null;
				if (year != null) {
					cs = rd.query("reddatadb_datatable", columns, "year=?",
							new String[] { year }, null, null, null);
				} else {
					cs = rd.query("reddatadb_datatable", columns, null, null,
							null, null, null);
				}
				ArrayList<RedObject> redobjects = new ArrayList<RedObject>();
				cs.moveToFirst();
				do {
					RedObject redobject = new RedObject();
					redobject.setSplitsnumber(cs.getString(0));
					redobject.setShake_ratio(cs.getString(1));
					redobject.setReceived_money(cs.getString(2));
					redobject.setStatus(cs.getString(3));
					redobject.setReturned_money(cs.getString(4));
					redobject.setSub_type(cs.getString(5));
					redobject.setCommand(cs.getString(6));
					redobject.setFrom(cs.getString(7));
					redobject.setOpen_time(cs.getString(8));
					redobject.setGift_id(cs.getString(9));
					redobject.setMoney(cs.getString(10));
					redobject.setHas_open(cs.getInt(11));
					redobject.setDirect(cs.getString(12));
					redobject.setCreate_time(cs.getString(13));
					redobject.setFrom_phone(cs.getString(14));
					redobject.setFromnickname(cs.getString(15));
					redobject.setMoney_type(cs.getString(16));
					redobject.setTips(cs.getString(17));
					redobject.setExp_time(cs.getString(18));
					redobject.setType(cs.getString(19));
					redobject.setSender_gift_id(cs.getString(20));
					redobject.setName(cs.getString(21));
					redobjects.add(redobject);
				} while (cs.moveToNext());
				return redobjects;
			} catch (Exception e) {
				return null;
			} finally {
				if (cs != null) {
					cs.close();
				}
				if (rd != null) {
					rd.close();
				}
				if (reddb != null) {
					reddb.close();
				}
			}

		}
	}

	// 查询单个
	public static RedObject GetRedData(Context context, String gift_id) {
		synchronized (RedData_DBHelp.class) {

			if (gift_id == null || "".equals(gift_id)) {
				return null;
			}
			RedData_DB reddb = RedData_DB.getMDD(context);
			SQLiteDatabase rd = null;
			Cursor cs = null;
			if (reddb == null) {
				return null;
			}
			try {
				rd = reddb.getReadableDatabase();
				String[] columns = new String[] { "splitsnumber",
						"shake_ratio", "received_money", "status",
						"returned_money", "sub_type", "command", "uidfrom",
						"open_time", "gift_id", "money", "has_open", "direct",
						"create_time", "from_phone", "fromnickname",
						"money_type", "tips", "exp_time", "type",
						"sender_gift_id", "name" };
				cs = rd.query("reddatadb_datatable", columns, "gift_id=?",
						new String[] { gift_id }, null, null, null);
				cs.moveToFirst();
				RedObject redobject = new RedObject();
				redobject.setSplitsnumber(cs.getString(0));
				redobject.setShake_ratio(cs.getString(1));
				redobject.setReceived_money(cs.getString(2));
				redobject.setStatus(cs.getString(3));
				redobject.setReturned_money(cs.getString(4));
				redobject.setSub_type(cs.getString(5));
				redobject.setCommand(cs.getString(6));
				redobject.setFrom(cs.getString(7));
				redobject.setOpen_time(cs.getString(8));
				redobject.setGift_id(cs.getString(9));
				redobject.setMoney(cs.getString(10));
				redobject.setHas_open(cs.getInt(11));
				redobject.setDirect(cs.getString(12));
				redobject.setCreate_time(cs.getString(13));
				redobject.setFrom_phone(cs.getString(14));
				redobject.setFromnickname(cs.getString(15));
				redobject.setMoney_type(cs.getString(16));
				redobject.setTips(cs.getString(17));
				redobject.setExp_time(cs.getString(18));
				redobject.setType(cs.getString(19));
				redobject.setSender_gift_id(cs.getString(20));
				redobject.setName(cs.getString(21));
				return redobject;
			} catch (Exception e) {
				return null;
			} finally {
				if (cs != null) {
					cs.close();
				}
				if (rd != null) {
					rd.close();
				}
				if (reddb != null) {
					reddb.close();
				}
			}

		}
	}

	// 修改一条数据的部分数据
	public static void upRedDate(Context context, String gift_id,
			ContentValues cv) {
		synchronized (RedData_DBHelp.class) {
			RedData_DB reddb = RedData_DB.getMDD(context);
			SQLiteDatabase rd = null;
			try {
				rd = reddb.getWritableDatabase();
				rd.update("reddatadb_datatable", cv, "gift_id=?",
						new String[] { gift_id });
			} catch (Exception e) {
			} finally {

				if (rd != null) {
					rd.close();
				}
				if (reddb != null) {
					reddb.close();
				}
			}
		}
	}

	// 增加或者修改红包数据(一条数据)
	public static void addRedDatas(Context context, RedObject redobject) {
		synchronized (RedData_DBHelp.class) {
			if (redobject == null) {
				return;
			}
			RedData_DB reddb = RedData_DB.getMDD(context);
			SQLiteDatabase rd = null;
			Cursor csred = null;
			try {
				rd = reddb.getWritableDatabase();
				// 先读取是否有记录
				csred = rd.query("reddatadb_datatable", null, "gift_id=?",
						new String[] { redobject.getGift_id() }, null, null,
						null);

				ContentValues cv = new ContentValues();
				cv.put("splitsnumber", redobject.getSplitsnumber());
				cv.put("shake_ratio", redobject.getShake_ratio());
				cv.put("received_money", redobject.getReceived_money());
				cv.put("status", redobject.getStatus());
				cv.put("returned_money", redobject.getReturned_money());
				cv.put("sub_type", redobject.getSub_type());
				cv.put("command", redobject.getCommand());
				cv.put("uidfrom", redobject.getFrom());
				cv.put("open_time", redobject.getOpen_time());
				cv.put("money", redobject.getMoney());
				cv.put("has_open", redobject.getHas_open());
				cv.put("direct", redobject.getDirect());
				cv.put("create_time", redobject.getCreate_time());
				cv.put("from_phone", redobject.getFrom_phone());
				cv.put("fromnickname", redobject.getFromnickname());
				cv.put("money_type", redobject.getMoney_type());
				cv.put("tips", redobject.getTips());
				cv.put("exp_time", redobject.getExp_time());
				cv.put("type", redobject.getType());
				cv.put("sender_gift_id", redobject.getSender_gift_id());
				cv.put("name", redobject.getName());

				if (csred == null || !csred.moveToFirst()) {
					cv.put("gift_id", redobject.getGift_id());
					cv.put("year", redobject.getCreate_time().substring(0, 4));
					cv.put("monthy", redobject.getCreate_time().substring(5, 7));
					rd.insert("reddatadb_datatable", null, cv);
				} else {
					rd.update("reddatadb_datatable", cv, "gift_id=?",
							new String[] { redobject.getGift_id() });
				}
			} catch (Exception e) {
				return;
			} finally {
				if (csred != null) {
					csred.close();
				}

				if (rd != null) {
					rd.close();
				}
				if (reddb != null) {
					reddb.close();
				}
			}

		}
	}

	// 增加或者修改多条数据
	public static void addMoreRedDatas(Context context,
			ArrayList<RedObject> redobjects) {
		if (redobjects != null) {
			addMoreRedDatas2(context, redobjects);
		}
	}

	public static void addMoreRedDatas2(Context context,
			ArrayList<RedObject> redobjects) {
		if (context != null && redobjects != null) {
			ArrayList<RedObject> redarraylist = getAllReds(context, null);
			TreeMap<String, RedObject> redsmap = new TreeMap<String, RedObject>();
			if (redarraylist != null) {
				for (RedObject obeject : redarraylist) {
					redsmap.put(obeject.getGift_id(), obeject);
				}
				redarraylist.clear();
				redarraylist = null;
			}
			synchronized (RedData_DBHelp.class) {
				RedData_DB reddb = RedData_DB.getMDD(context);
				SQLiteDatabase rd = null;

				try {
					rd = reddb.getWritableDatabase();
					rd.beginTransaction();
					Iterator<RedObject> redobjects_i = redobjects.iterator();
					while (redobjects_i.hasNext()) {
						RedObject redobject = redobjects_i.next();
						ContentValues cv = new ContentValues();
						cv.put("splitsnumber", redobject.getSplitsnumber());
						cv.put("shake_ratio", redobject.getShake_ratio());
						cv.put("received_money", redobject.getReceived_money());
						cv.put("status", redobject.getStatus());
						cv.put("returned_money", redobject.getReturned_money());
						cv.put("sub_type", redobject.getSub_type());
						cv.put("command", redobject.getCommand());
						cv.put("uidfrom", redobject.getFrom());
						cv.put("open_time", redobject.getOpen_time());
						cv.put("money", redobject.getMoney());
						cv.put("has_open", redobject.getHas_open());
						cv.put("direct", redobject.getDirect());
						cv.put("create_time", redobject.getCreate_time());
						cv.put("from_phone", redobject.getFrom_phone());
						cv.put("fromnickname", redobject.getFromnickname());
						cv.put("money_type", redobject.getMoney_type());
						cv.put("tips", redobject.getTips());
						cv.put("exp_time", redobject.getExp_time());
						cv.put("type", redobject.getType());
						cv.put("sender_gift_id", redobject.getSender_gift_id());
						cv.put("name", redobject.getName());
						if (redsmap.get(redobject.getGift_id()) == null) {
							cv.put("gift_id", redobject.getGift_id());
							cv.put("year", redobject.getCreate_time()
									.substring(0, 4));
							cv.put("monthy", redobject.getCreate_time()
									.substring(5, 7));
							rd.insert("reddatadb_datatable", null, cv);
						} else {
							rd.update("reddatadb_datatable", cv, "gift_id=?",
									new String[] { redobject.getGift_id() });
						}
					}
					rd.setTransactionSuccessful();
					redsmap.clear();
					redsmap = null;
				} catch (Exception e) {
					return;
				} finally {
					if (rd != null) {
						rd.endTransaction();
						rd.close();
					}
					if (reddb != null) {
						reddb.close();
					}
				}
			}
		}
	}
}
