package org.aisin.sipphone.tools;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.CallhistoryInfo;
import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.commong.OBCallhistoryInfo;
import org.aisin.sipphone.sqlitedb.CallhistoryDBTOOls;
import org.aisin.sipphone.sqlitedb.Friend_data_Check;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class CursorTools {

	private static final Lock lock = new ReentrantLock();// 通讯录修改加锁

	public static TreeSet<Contact> friendslist = new TreeSet<Contact>();// 好友记录
	private static TreeSet<OBCallhistoryInfo> ob_set = null;// 通话记录
	public static TreeMap<Long, Contact> cttmap = null;// 通讯录
	private static HanyuPinyinOutputFormat format_py = new HanyuPinyinOutputFormat(); // 华语拼音
	private static SimpleDateFormat format = new java.text.SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static Calendar current = Calendar.getInstance();
	private static Calendar today = Calendar.getInstance(); // 今天
	private static Calendar yesterday = Calendar.getInstance(); // 昨天
	private static Calendar day_tmonth = Calendar.getInstance(); // 两个月前
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-");
	private static String date_2 = sdf.format(new Date());
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
	public static String date_3 = sdf3.format(new Date());
	static {
		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		day_tmonth.set(Calendar.YEAR, current.get(Calendar.YEAR));
		day_tmonth.set(Calendar.MONTH, current.get(Calendar.MONTH));
		day_tmonth.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 60);
		day_tmonth.set(Calendar.HOUR_OF_DAY, 0);
		day_tmonth.set(Calendar.MINUTE, 0);
		day_tmonth.set(Calendar.SECOND, 0);
	}

	// 获取通话记录 按最后一次通话时间排序
	public static synchronized void getCallRecords(Context context,
			CursorLoadinterface csli, boolean Compulsory) {// Compulsory 强制重载
		if (Compulsory) {// 强制清空数据
			if (ob_set != null) {
				ob_set.clear();
				ob_set = null;
			}
		}
		if (ob_set != null) {
			if (csli != null) {
				csli.DoadDown(ob_set);
			}
		} else {
			try {
				String[] fields = { CallLog.Calls.NUMBER, CallLog.Calls.TYPE,
						CallLog.Calls.DATE, CallLog.Calls.CACHED_NAME,
						CallLog.Calls.DURATION, "_id" };
				Cursor cursor = context.getContentResolver()
						.query(CallLog.Calls.CONTENT_URI,
								fields,
								CallLog.Calls.DATE + ">"
										+ day_tmonth.getTimeInMillis(), null,
								CallLog.Calls.DATE + " DESC");
				if (cursor != null && cursor.moveToFirst()) {
					ob_set = new TreeSet<OBCallhistoryInfo>();
					TreeMap<String, OBCallhistoryInfo> map = new TreeMap<String, OBCallhistoryInfo>();
					do {
						// 号码
						String number = cursor.getString(0);
						if (number == null) {
							continue;
						}
						OBCallhistoryInfo obhtemp = map.get(number);
						if (obhtemp != null && obhtemp.getSet().size() > 6) {// 同一个号码的记录超过10条
																				// 跳出
							continue;
						}
						// 呼叫类型
						int type;
						String call_type_name;
						int call_type_imageid;
						switch (Integer.parseInt(cursor.getString(1))) {
						case Calls.INCOMING_TYPE:
							type = 1;
							call_type_name = "呼入电话";
							call_type_imageid = R.drawable.call_status_incoming;
							break;
						case Calls.OUTGOING_TYPE:
							type = 2;
							call_type_name = "呼出电话";
							call_type_imageid = R.drawable.call_status_outgoing;
							break;
						case Calls.MISSED_TYPE:
							type = 3;
							call_type_name = "未接电话";
							call_type_imageid = R.drawable.call_status_missed;
							break;
						default:
							type = 0;// 应该是挂断.根据我手机类型判断出的
							call_type_name = "";
							call_type_imageid = R.drawable.call_status_reject;
							break;
						}
						SimpleDateFormat sfd = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date date = new Date(
								Long.parseLong(cursor.getString(2)));
						// 呼叫时间
						String time = sfd.format(date);
						String chainal_call_time = formatDateTime(time.trim()
								.substring(0, 16));
						// 联系人
						String name = cursor.getString(3);
						if (name == null || "".equals(name.trim())) {
							name = "";
						}
						if (number == null
								|| "".equals(number.trim())
								|| (number.startsWith("-") && number.length() < 5)) {
							name = "未知号码";
						}
						// 通话时间,单位:s
						String cursor_times = cursor.getString(4);
						String duration = "00:00";
						if (cursor_times != null && !"".equals(cursor_times)) {
							try {
								duration = cal_time(Integer
										.parseInt(cursor_times.trim()));
							} catch (Exception e) {
							}
						}

						if (map.containsKey(number.trim())) {
							OBCallhistoryInfo obch = map.get(number.trim());
							obch.getSet()
									.add(new CallhistoryInfo(cursor
											.getString(5), name, number, type,
											call_type_name, call_type_imageid,
											time, chainal_call_time, duration,
											false));
						} else {
							OBCallhistoryInfo obch = new OBCallhistoryInfo();
							obch.setPhone(number.trim());
							obch.getSet()
									.add(new CallhistoryInfo(cursor
											.getString(5), name, number, type,
											call_type_name, call_type_imageid,
											time, chainal_call_time, duration,
											false));
							map.put(number.trim(), obch);
						}
					} while (cursor.moveToNext());
					// 加入本地数据库通话记录
					for (CallhistoryInfo oti : CallhistoryDBTOOls
							.getchinfo(context)) {
						if (map.containsKey(oti.getPhone())) {
							OBCallhistoryInfo obch = map.get(oti.getPhone());
							if (obch.getSet().size() < 11) {
								oti.setChainal_call_time(formatDateTime(oti
										.getCall_time().trim().substring(0, 16)));
								obch.getSet().add(oti);
							}
						} else {
							oti.setChainal_call_time(formatDateTime(oti
									.getCall_time().trim().substring(0, 16)));
							OBCallhistoryInfo obch = new OBCallhistoryInfo();
							obch.setPhone(oti.getPhone());
							obch.getSet().add(oti);
							map.put(oti.getPhone(), obch);
						}
					}
					for (Entry<String, OBCallhistoryInfo> entry : map
							.entrySet()) {
						ob_set.add(entry.getValue());
					}
					map.clear();
					map = null;
					if (csli != null) {
						csli.DoadDown(ob_set);
					}
				} else {
					if (csli != null) {
						csli.DoadDown(null);
					}
				}
				if (cursor != null) {
					cursor.close();// 关闭
				}
			} catch (Exception e) {
			}

		}

	}

	// 获取联系人数据2
	public static void loadContacts_2(final Context context,
			final ContactLoadinteface contactldi, boolean Compulsory) {// 强制重载
		lock.lock();
		try {
			if (Compulsory) {// 强制清空数据
				if (cttmap != null) {
					cttmap.clear();
					cttmap = null;
				}
			}
			if (cttmap != null) {
				if (contactldi != null) {
					contactldi.DoadDown_map(cttmap);
				}
			} else {
				try {
					cttmap = new TreeMap<Long, Contact>();
					String[] mContactsProjection = new String[] {
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
							// ContactsContract.CommonDataKinds.Phone.TYPE,
							ContactsContract.CommonDataKinds.Phone.NUMBER,
							ContactsContract.Contacts.DISPLAY_NAME,
					/* ContactsContract.Contacts.PHOTO_ID */};
					ContentResolver resolver = context.getContentResolver();
					Cursor phoneCursor = resolver.query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							mContactsProjection, null, null, null);
					while (phoneCursor != null && phoneCursor.moveToNext()) {
						Long CONTACT_ID = phoneCursor.getLong(0);
						String NUMBER = phoneCursor.getString(1);
						String DISPLAY_NAME = phoneCursor.getString(2);
						if (NUMBER == null || "".equals(NUMBER)) {
							continue;
						}
						Contact contact = cttmap.get(CONTACT_ID);
						if (contact == null) {
							contact = new Contact();
							if (DISPLAY_NAME != null
									&& !"".equals(DISPLAY_NAME)) {
								contact.setRemark(DISPLAY_NAME);
							}
							contact.setContractID(CONTACT_ID);
							contact.getPhonesList().add(
									NUMBER.replaceAll(" ", "").replaceAll("-",
											""));
							cttmap.put(CONTACT_ID, contact);
						} else {
							contact.getPhonesList().add(NUMBER);
						}
					}
					// 对数据进行拼音检索处理
					for (Long lg : cttmap.keySet()) {
						Contact ctct = cttmap.get(lg);
						if (ctct != null) {
							PinyinRetrieval(ctct);
						}
					}
					if (contactldi != null) {
						contactldi.DoadDown_map(cttmap);
					}
					if (phoneCursor != null) {
						phoneCursor.close();
					}
				} catch (Exception e) {
				}
			}
		} finally {
			lock.unlock();
		}
	}

	// 匹配拼音
	private static void PinyinRetrieval(Contact contact) {
		String name = contact.getRemark();
		TreeSet<String> arrlist = contact.getSearchlist4name();
		if (arrlist == null || name == null) {
			return;
		}
		// 名字号码加入检索集合
		arrlist.add(name);
		if (contact.isShowflag()) {
			arrlist.add(contact.getFriendphone());
		} else {
			for (String phonenum : contact.getPhonesList()) {
				arrlist.add(phonenum);
			}
		}

		// 转化拼音
		char[] arrs = name.trim().toCharArray();
		if (arrs == null || arrs.length <= 0) {
			return;
		}
		// 如果首字母为非汉字 设置首字母
		if (!Character.toString(arrs[0]).matches("[\\u4E00-\\u9FA5]+")) {
			if (Character.toString(arrs[0]).matches("[a-zA-Z]")) {// 首字母为字母 则取字母
				contact.setF_PY((arrs[0] + "").toLowerCase());// 取首字母
			} else {// 取首字母为"~" 默认为~
			}
		}
		// 检索汉字拼音
		StringBuilder namepingying = new StringBuilder();
		StringBuilder namepingying_szm = new StringBuilder();
		for (int i = 0; i < arrs.length; i++) {
			if (Character.toString(arrs[i]).matches("[\\u4E00-\\u9FA5]+")) {// 遇到汉字则检测
																			// 非汉字跳过
				String[] temp = null;
				try {
					temp = PinyinHelper.toHanyuPinyinStringArray(arrs[i],
							format_py);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					// TODO Auto-generated catch
					// block
					e.printStackTrace();
				}
				if (temp == null || temp.length == 0) {
					continue;
				}
				String pingying = temp[0].substring(0, temp[0].length() - 1);
				namepingying.append(pingying);
				String szmtemp = pingying.substring(0, 1);
				namepingying_szm.append(szmtemp);
				if (i == 0) {
					contact.setF_PY(szmtemp);// 取第一个字符的首字母
				}
				if (i == 2) {
					contact.setT_PY(szmtemp);// 取第二个字符的首字母
				}
			}
		}
		contact.setSpy(namepingying_szm.toString());
		arrlist.add(namepingying.toString());
		arrlist.add(namepingying_szm.toString());
	}

	// 加载头像
	public static void LoadingPicture(final Context context,
			final ContactLoadinteface contactldi) {
		lock.lock();
		try {
			if (cttmap == null) {
				if (contactldi != null) {
					contactldi.headimagedown(false);
				}
				return;
			} else {// 保证cttmap单线程操作
				new Thread(new Runnable() {
					@Override
					public void run() {

						// 如果有 获取头像
						ContentResolver resolver = context.getContentResolver();
						if (resolver == null || cttmap == null) {
							if (contactldi != null) {
								contactldi.headimagedown(false);
							}
							return;
						}
						try {
							for (Entry<Long, Contact> ent : cttmap.entrySet()) {
								Contact contact = ent.getValue();
								if (contact == null) {
									continue;
								}
								Uri uri_tx = ContentUris.withAppendedId(
										ContactsContract.Contacts.CONTENT_URI,
										contact.getContractID());
								InputStream input = ContactsContract.Contacts
										.openContactPhotoInputStream(resolver,
												uri_tx);
								if (input != null) {
									contact.setTx(BitmapFactory
											.decodeStream(input));
								}
							}
						} catch (Exception e) {
						}
						if (contactldi != null) {
							contactldi.headimagedown(true);
						}
					}
				}).start();
			}
		} finally {
			lock.unlock();
		}
	}

	// 匹配服务器好友
	public static void checkFriends(Context context,
			ContactLoadinteface contactldi, boolean checkset) {
		lock.lock();
		try {
			if (cttmap == null) {
				if (contactldi != null) {
					contactldi.upfrendsdwon(false);
				}
				return;
			}
			if (Check_network.isNetworkAvailable(context)) {
				// 检查今天是否匹配过
				if (checkset
						&& CheckUpadateTime
								.CheckResult_4Ckeckupfriendstime(context)) {
					// 上传本地通讯录
					UPFrends4.Commitdata(context);
				}
				// 检查今天是否同步过服务器数据
				if (CheckUpadateTime
						.CheckResult_4Ckeckupfriends_uptime(context)) {
					// 更新本地环宇好友
					UPFrends4.Updata(context);
				}
			}
			// 读取本地记录的环宇好友
			ArrayList<Contact> altemp = Friend_data_Check
					.GetAllFriends(context);
			if (altemp == null) {
				friendslist.clear();
				if (contactldi != null) {
					contactldi.upfrendsdwon(false);
				}
				return;
			}
			// 给环宇好友匹配拼音检索
			for (Contact ctt : altemp) {
				PinyinRetrieval(ctt);
			}
			TreeSet<Contact> friendslist_temp = new TreeSet<Contact>();
			friendslist_temp.addAll(friendslist);
			friendslist.clear();
			friendslist.addAll(altemp);
			altemp.clear();
			altemp = null;
			// 对本地通讯录做匹配
			for (Long lg : cttmap.keySet()) {
				Contact yold = cttmap.get(lg);
				if (yold == null) {
					continue;
				}
				for (String str_phone : yold.getPhonesList()) {
					Iterator<Contact> ictt = friendslist.iterator();
					while (ictt.hasNext()) {
						Contact ctt = ictt.next();
						if (str_phone.equals(ctt.getFriendphone())) {
							yold.setIsfreand(true);
							yold.setSignature(ctt.getSignature());
							yold.setFriendname(ctt.getFriendname());
							yold.setFriendphone(ctt.getFriendphone());
							yold.setProfession(ctt.getProfession());
							yold.setCompany(ctt.getCompany());
							yold.setProvince(ctt.getProvince());
							yold.setCity(ctt.getCity());
							yold.setSchool(ctt.getSchool());
							yold.setUid(ctt.getUid());
							yold.setSex(ctt.getSex());
							yold.setBthday(ctt.getBthday());
						}
					}
				}
			}
			// Iterator<Contact> ictts = friendslist_temp.iterator();
			// while (ictts.hasNext()) {
			// ictts.next().receveheadimage();
			// }
			friendslist_temp.clear();
			friendslist_temp = null;
			if (contactldi != null) {
				contactldi.upfrendsdwon(true);
			}

		} finally {
			lock.unlock();
		}
	}

	public static void checkupfriends(Context context,
			ContactLoadinteface contactldi) {
		lock.lock();
		try {
			// 处理头像
			boolean upflag_tx = false;
			Iterator<Contact> ictt = friendslist.iterator();
			TreeMap<String, ContentValues> cvsmap = new TreeMap<String, ContentValues>();
			while (ictt.hasNext()) {
				Contact ctt = ictt.next();
				if (ctt.getTx_imagedownflag() == 1) {
					// 下载头像
					boolean downallflag = false;
					if (ctt.getPicture() != null
							&& !"".equals(ctt.getPicture())
							&& !"null".equals(ctt.getPicture())) {
						downallflag = HttpUtils.downloadImage(context,
								ctt.getPicture(), ctt.getUid()
										+ "headimage.jpg");
					}
					if (downallflag) {
						Bitmap bitmap = BitMapcreatPath2smallSize
								.GetBitmap4Path2samll(context
										.getFileStreamPath(
												ctt.getUid() + "headimage.jpg")
										.getAbsolutePath());
						if (bitmap != null) {
							upflag_tx = true;
							ctt.setTx_fread(bitmap);
							ContentValues cv = new ContentValues();
							cv.put("uid", ctt.getUid());
							cv.put("tx_imagedownflag", 0);
							cvsmap.put(ctt.getUid(), cv);
							ctt.setTx_imagedownflag(0);
						}
					}
				} else {
					if (ctt.getTx_fread() != null) {
						continue;
					} else {
					}
					File fl = context.getFileStreamPath(ctt.getUid()
							+ "headimage.jpg");
					if (fl.exists()) {
						Bitmap bitmap = BitMapcreatPath2smallSize
								.GetBitmap4Path2samll(fl.getAbsolutePath());
						if (bitmap != null) {
							upflag_tx = true;
							ctt.setTx_fread(bitmap);
						}
					}
				}
			}
			Friend_data_Check.UPdataMoreFriends(context, cvsmap);
			if (contactldi != null) {
				contactldi.showfriends(upflag_tx);
			}
		} finally {
			lock.unlock();
		}

	}

	public static String formatDateTime(String time) {
		if (time == null || "".equals(time)) {
			return "";
		}
		Date date = null;
		try {
			date = format.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		current.setTime(date);
		if (current.after(today)) {
			return "今天 " + time.split(" ")[1];
		} else if (current.before(today) && current.after(yesterday)) {

			return "昨天 " + time.split(" ")[1];
		} else {
			// int index = time.indexOf("-") + 1;
			// return time.substring(index, time.length());

			return time.replaceAll(date_2, "");
		}
	}

	private static String cal_time(int second) {
		int h = 0;
		int d = 0;
		int s = 0;
		int temp = second % 3600;
		if (second > 3600) {
			h = second / 3600;
			if (temp != 0) {
				if (temp > 60) {
					d = temp / 60;
					if (temp % 60 != 0) {
						s = temp % 60;
					}
				} else {
					s = temp;
				}
			}
		} else {
			d = second / 60;
			if (second % 60 != 0) {
				s = second % 60;
			}
		}
		String hh = h + "";
		String dd = d + "";
		String ss = s + "";
		if (hh.length() == 1)
			hh = "0" + hh;
		if (dd.length() == 1)
			dd = "0" + dd;
		if (ss.length() == 1)
			ss = "0" + ss;
		// return h + "时" + d + "分" + s + "秒";
		return hh + ":" + dd + ":" + ss;
	}

	public static boolean DeletedataBYW(Context context, String wheres,
			String[] values) {
		try {
			ContentResolver resolver = context.getContentResolver();
			int num = resolver
					.delete(CallLog.Calls.CONTENT_URI, wheres, values);
			if (num > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
