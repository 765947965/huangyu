package org.aisin.sipphone.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aisin.sipphone.commong.Contact;
import org.aisin.sipphone.sqlitedb.Friend_data_Check;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author Administrator 更新服务器上的好友列表
 */
public class UPFrends4 {
	public static synchronized void Commitdata(Context context) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpPost post = new HttpPost(
					URLTools.GetHttpURL_4Commitfriend(context));

			if (CursorTools.cttmap == null) {
				return;
			}

			JSONArray arrays_alls = new JSONArray();
			for (Long lg : CursorTools.cttmap.keySet()) {
				Contact ctt = CursorTools.cttmap.get(lg);
				if (ctt == null) {
					continue;
				}
				JSONObject js = new JSONObject();
				js.put("id", lg);
				js.put("name", ctt.getRemark());
				JSONArray phs = new JSONArray();
				for (String str : ctt.getPhonesList()) {
					phs.put(str);
				}
				js.put("mobile", phs);
				arrays_alls.put(js);
			}
			JSONObject json = new JSONObject();
			json.put("contactlist", arrays_alls);
			JSONArray ja = new JSONArray();
			ja.put(((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
			json.put("mac", ja);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("aixinContact", json.toString()));
			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(list,
					HTTP.UTF_8);
			post.setEntity(formEntiry);
			HttpResponse response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				post.abort();
				JSONObject jnb = new JSONObject(result);
				int doresult = Integer
						.parseInt(jnb.optString("result", "-104"));
				if (doresult == 0) {
					// 更新今天上传日期
					SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
					String date = sdf.format(new Date());
					SharedPreferencesTools
							.getSharedPreferences_4upfriends(context)
							.edit()
							.putString(SharedPreferencesTools.upfrendstime,
									date).commit();
				}
			} else {
				post.abort();
			}
		} catch (Exception e) {
		}
	}

	// 上传一个号码
	public static synchronized boolean Commitdata_one(Context context,
			Contact ctt) {
		return Commitdata_one(context, ctt.getRemark(), ctt.getFriendphone());
	}

	// 上传一个号码
	public static synchronized boolean Commitdata_one(Context context,
			String remark, String phone) {

		try {
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpPost post = new HttpPost(
					URLTools.GetHttpURL_4Commitfriend(context));

			JSONArray arrays_alls = new JSONArray();

			JSONObject js = new JSONObject();
			js.put("id", System.currentTimeMillis());
			js.put("name", remark);
			JSONArray phs = new JSONArray();
			phs.put(phone);
			js.put("mobile", phs);
			arrays_alls.put(js);

			JSONObject json = new JSONObject();
			json.put("contactlist", arrays_alls);
			JSONArray ja = new JSONArray();
			ja.put(((TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
			json.put("mac", ja);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("aixinContact", json.toString()));
			UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(list,
					HTTP.UTF_8);
			post.setEntity(formEntiry);
			HttpResponse response = httpclient.execute(post);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				post.abort();
				JSONObject jnb = new JSONObject(result);
				int doresult = Integer
						.parseInt(jnb.optString("result", "-104"));
				if (doresult == 0) {
					return true;
				} else {
					return false;
				}
			} else {
				post.abort();
				return false;
			}
		} catch (Exception e) {
			return false;
		}

	}

	@SuppressLint("SimpleDateFormat")
	public static synchronized void Updata(Context context) {
		try {
			// 拉取好友列表
			String url = URLTools.GetHttpURL_4UPfriend(context);
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			JSONObject json = new JSONObject(result);
			int doresult = Integer.parseInt(json.optString("result", "-104"));
			if (doresult == 0 || doresult == 64) {
				TreeMap<String, Contact> ctts = new TreeMap<String, Contact>();
				if (doresult == 0) {
					JSONArray jas = json.getJSONArray("friends");
					// 收取好友信息
					for (int i = 0; i < jas.length(); i++) {
						JSONObject jtp = jas.getJSONObject(i);
						String phone = jtp.getString("phone");
						String remark = jtp.getString("remark");// 通讯录名称
						String uid = jtp.getString("uid");
						// 查询数据库中是否有该条记录
						Contact ctt = Friend_data_Check.Checkdata4Sqlit(
								context, uid, false);
						if (ctt == null || "".equals(ctt.getVer())) {
							ctt = new Contact();
							ctt.setVer("1.0");
							ctt.setUid(uid);
							ctt.setPicmd5("");
						}
						ctt.setFriendphone(phone);
						ctt.setRemark(remark);
						ctts.put(uid, ctt);
					}
				} else if (doresult == 64) {
					ArrayList<Contact> ctttemp = Friend_data_Check
							.GetAllFriends(context);
					if (ctttemp != null) {
						for (Contact cst : ctttemp) {
							ctts.put(cst.getUid(), cst);
						}
						ctttemp.clear();
						ctttemp = null;
					}
				}
				HttpClient httpclient = new DefaultHttpClient();
				// 请求超时
				httpclient.getParams().setParameter(
						CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
				// 读取超时
				httpclient.getParams().setParameter(
						CoreConnectionPNames.SO_TIMEOUT, 5000);
				HttpPost post = new HttpPost(
						URLTools.GetHttpURL_4UPfriend2One(context));
				JSONArray jsas = new JSONArray();
				for (Entry<String, Contact> ents : ctts.entrySet()) {
					Contact ctttemp = ents.getValue();
					JSONObject jobt = new JSONObject();
					jobt.put("uid", ctttemp.getUid());
					jobt.put("ver", ctttemp.getVer());
					jsas.put(jobt);
				}
				JSONObject jsons = new JSONObject();
				jsons.put("friendslist", jsas);
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				list.add(new BasicNameValuePair("friends", jsons.toString()));
				UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
						list, HTTP.UTF_8);
				post.setEntity(formEntiry);
				HttpResponse response = httpclient.execute(post);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					String result_z = EntityUtils.toString(
							response.getEntity(), "UTF-8");
					post.abort();
					JSONObject jsoct_z = new JSONObject(result_z);
					int doresult_z = jsoct_z.optInt("result", -104);
					if (doresult_z == 0) {
						try {
							JSONArray jsonas = jsoct_z
									.getJSONArray("friendslist");
							if (jsonas != null && jsonas.length() > 0) {
								TreeMap<String, ContentValues> cvsmap = new TreeMap<String, ContentValues>();
								for (int i = 0; i < jsonas.length(); i++) {
									JSONObject jsfriend_z = jsonas
											.getJSONObject(i);
									String province = jsfriend_z
											.optString("province");// 省份
									String picture = jsfriend_z
											.optString("picture");// 头像下载地址
									String picmd5 = jsfriend_z
											.optString("picmd5");// 头像md5
									String ver = jsfriend_z.optString("ver");// 好友资料版本
									String uid = jsfriend_z.optString("uid");// 好友uid
									String mobileNumber = jsfriend_z
											.optString("mobileNumber");// 好友绑定的号码
									String company = jsfriend_z
											.optString("company");// 好友公司
									String profession = jsfriend_z
											.optString("profession");// 好友置业
									String school = jsfriend_z
											.optString("school");// 好友学校
									String sex = jsfriend_z.optString("sex");// 好友性别
									String birthday = jsfriend_z
											.optString("birthday");// 好友生日
									String signature = jsfriend_z
											.optString("signature");// 好友签名
									String city = jsfriend_z.optString("city");// 好友城市
									String name = jsfriend_z.optString("name");// 好友自己设定的昵称
									Contact czztt = ctts.get(uid);
									if (czztt != null) {
										ContentValues cv = new ContentValues();
										cv.put("uid", Integer.parseInt(czztt
												.getUid().trim()));
										cv.put("ver", ver);
										if (!picmd5.equals(czztt.getPicmd5())) {
											cv.put("picmd5", picmd5);
											cv.put("tx_imagedownflag", 1);
											// 0不需要更新头像，1需要更新头像
										} else {
											cv.put("tx_imagedownflag", 0);
										}
										if ("".equals(picture)) {
											cv.put("tx_imagedownflag", 0);
										}
										cv.put("phone", czztt.getFriendphone());// 好友的电话号码
										cv.put("remark", czztt.getRemark());
										cv.put("province", province);
										cv.put("picture", picture);
										cv.put("mobileNumber", mobileNumber);
										cv.put("company", company);
										cv.put("profession", profession);
										cv.put("school", school);
										cv.put("sex", sex);
										cv.put("birthday", birthday);
										cv.put("signature", signature);
										cv.put("city", city);
										cv.put("name", name);
										cvsmap.put(uid, cv);
									}
								}
								Friend_data_Check.UPdataMoreFriends(context, cvsmap);
							}
						} catch (Exception e) {
						}
						if (doresult == 0) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
							String date = sdf.format(new Date());
							SharedPreferencesTools
									.getSharedPreferences_4upfriends(context)
									.edit()
									.putString(
											SharedPreferencesTools.upfrendsuptime,
											date)
									.putString(
											SharedPreferencesTools.upfrendver,
											json.optString("ver", "1.0"))
									.commit();
						} else if (doresult == 64) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
							String date = sdf.format(new Date());
							SharedPreferencesTools
									.getSharedPreferences_4upfriends(context)
									.edit()
									.putString(
											SharedPreferencesTools.upfrendsuptime,
											date).commit();
						}
						ctts.clear();
						ctts = null;
					}
				} else {
					post.abort();
				}
			}
		} catch (Exception e) {
		}
	}

	public static void addFriend_one(Context context, Contact contact) {
		ContentValues cv = new ContentValues();
		cv.put("uid", contact.getUid());
		cv.put("ver", contact.getVer());
		String picture = contact.getPicture();
		if (picture != null && !"".equals(picture) && !"null".equals(picture)) {
			cv.put("picture", picture);
			cv.put("picmd5", contact.getPicmd5());
			File file = context.getFileStreamPath(contact.getUid()
					+ "headimage.jpg");
			if (file.exists()) {
				cv.put("tx_imagedownflag", 0);
			} else {
				cv.put("tx_imagedownflag", 1);
			}
		} else {
			cv.put("picture", "");
			cv.put("picmd5", "");
			cv.put("tx_imagedownflag", 1);
		}
		cv.put("phone", contact.getFriendphone());
		cv.put("remark", contact.getRemark());
		cv.put("province", contact.getProvince());
		cv.put("mobileNumber", contact.getMobileNumber());
		cv.put("company", contact.getCompany());
		cv.put("profession", contact.getProfession());
		cv.put("school", contact.getSchool());
		cv.put("sex", contact.getSex());
		cv.put("birthday", contact.getBthday());
		cv.put("signature", contact.getSignature());
		cv.put("city", contact.getCity());
		cv.put("name", contact.getFriendname());
		Friend_data_Check.add4DataupFriends(context, contact.getUid(), cv);
	}
}
