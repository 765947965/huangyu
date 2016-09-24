package org.aisin.sipphone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class HttpTask_Red_CHECKOUT extends AsyncTask<String, String, String> {
	private Context context;
	private Handler mHandler;
	private SimpleDateFormat sdformat_3 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat sdformat_5 = new SimpleDateFormat("yyyyMMdd");
	private int datestr_today;
	private String year;
	private int nochecknum = 0;// 未拆有效红包数量

	private RedObject robject;// 展示的拆红包的

	public HttpTask_Red_CHECKOUT(Context context, Handler mHandler) {
		this.context = context;
		this.mHandler = mHandler;
		datestr_today = Integer.parseInt(sdformat_5.format(new Date()));
	}

	@Override
	protected String doInBackground(String... paramArrayOfParams) {
		// 获取得到红包数据URL
		if (paramArrayOfParams[0] != null
				&& "redlist".equals(paramArrayOfParams[0])) {
			return null;
		} else {
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy");
			year = sfd.format(new Date());
			String url = URLTools.GetHttpURL_4Red_GETDATA_Url(context, year
					+ "", "received");
			String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
			return result;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		final String resultf = result;
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (HttpTask_Red_CHECKOUT.class) {

					JSONObject json = null;
					int doresult = -104;
					try {
						if (resultf != null) {
							json = new JSONObject(resultf);
							doresult = Integer.parseInt(json.optString(
									"result", "-104"));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					boolean tempflag = false;// 标识是否真的联网查询后改变的 是的才通知红包刘表改变
					TreeSet<RedObject> redobjects = new TreeSet<RedObject>();
					switch (doresult) {
					case 0:
						tempflag = true;
						try {
							JSONArray gifts = json.getJSONArray("gifts");

							try {
								// 更新发出去的红包 加入数组
								String urlsend = URLTools
										.GetHttpURL_4Red_GETDATA_Url(context,
												year + "", "sended");
								String resultsend = HttpUtils.result_url_get(
										urlsend, "{'result':'-104'}");
								JSONObject jssend = new JSONObject(resultsend);
								if (jssend.optInt("result", -104) == 0) {
									JSONArray giftssend = jssend
											.getJSONArray("gifts");
									for (int i = 0; i < giftssend.length(); i++) {
										gifts.put(giftssend.getJSONObject(i));
									}
								}
							} catch (Exception e) {
							}

							ArrayList<RedObject> arrays = new ArrayList<RedObject>();
							for (int i = 0; i < gifts.length(); i++) {
								JSONObject jsz = gifts.getJSONObject(i);
								RedObject redobject = new RedObject();
								redobject.setSplitsnumber(jsz
										.optString("splitsnumber"));
								redobject.setShake_ratio(jsz
										.optString("shake_ratio"));
								redobject.setReceived_money(jsz
										.optString("received_money","0"));
								redobject.setReturned_money(jsz
										.optString("returned_money","0"));
								redobject.setStatus(jsz.optString("status"));
								redobject.setSub_type(jsz.optString("sub_type"));
								redobject.setCommand(jsz.optString("command"));
								redobject.setFrom(jsz.optString("from"));
								redobject.setOpen_time(jsz
										.optString("open_time"));
								redobject.setGift_id(jsz.optString("gift_id"));
								redobject.setMoney(jsz.optString("money","0"));
								String has_open_str = jsz.optString("has_open");
								if (has_open_str != null
										&& !"".equals(has_open_str)) {
									redobject.setHas_open(Integer
											.parseInt(has_open_str.trim()));
								} else {
									redobject.setHas_open(0);
								}
								redobject.setDirect(jsz.optString("direct"));
								redobject.setCreate_time(jsz
										.optString("create_time"));
								redobject.setFrom_phone(jsz
										.optString("from_phone"));
								redobject.setFromnickname(jsz
										.optString("fromnickname"));
								redobject.setMoney_type(jsz
										.optString("money_type"));
								redobject.setTips(jsz.optString("tips"));
								redobject.setExp_time(jsz.optString("exp_time"));
								redobject.setType(jsz.optString("type"));
								redobject.setSender_gift_id(jsz
										.optString("sender_gift_id"));
								redobject.setName(jsz.optString("name"));
								arrays.add(redobject);
							}

							// 加入本地数据库
							RedData_DBHelp.addMoreRedDatas(context, arrays);
						} catch (Exception e) {
						}
						break;
					}
					ArrayList<RedObject> redobjectarray = RedData_DBHelp
							.getAllReds(context, null);
					if (redobjectarray != null) {
						redobjects.addAll(redobjectarray);
						redobjectarray.clear();
						redobjectarray = null;
					}
					try {
						for (RedObject redobj : redobjects) {
							String direct = redobj.getDirect();
							if ("sended".equals(direct)) {
								continue;// 发出的红包跳过
							}
							String has_open_str = redobj.getHas_open() + "";
							int hasopen = 1;
							if (has_open_str != null
									&& !"".equals(has_open_str)) {
								hasopen = Integer.parseInt(has_open_str.trim());
							}
							if (hasopen == 1) {
								// 拆了直接跳出
								continue;
							}
							String exp_time = redobj.getExp_time();
							Date date = sdformat_3.parse(exp_time.trim());
							// 过期日期
							int datestr = Integer.parseInt(sdformat_5
									.format(date));
							if (datestr_today <= datestr) {
								// 有没过期的未拆的红包
								// 置有红包提醒事物为真
								nochecknum += 1;
								robject = redobj;
							}
						}
						redobjects.clear();
						redobjects = null;
						if (nochecknum > 0) {
							SharedPreferencesTools
									.getSharedPreferences_4RED_COUT(context)
									.edit()
									.putBoolean(
											SharedPreferencesTools.REDCOUT_key,
											true)
									.putInt(SharedPreferencesTools.REhasnum_key,
											nochecknum).commit();
						} else {
							SharedPreferencesTools
									.getSharedPreferences_4RED_COUT(context)
									.edit()
									.putBoolean(
											SharedPreferencesTools.REDCOUT_key,
											false)
									.putInt(SharedPreferencesTools.REhasnum_key,
											nochecknum).commit();
						}
						// 本地数据发生了改变 如果红包列表ACTIVITY存在 则更新列表
						if (tempflag) {
							SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
							String date = sdf.format(new Date());
							SharedPreferencesTools
									.getSharedPreferences_4RED_COUT(context)
									.edit()
									.putString(
											SharedPreferencesTools.REDDATA_dian,
											date).commit();
							context.sendBroadcast(new Intent(
									Constants.BrandName + ".redlisttoup"));

							// 开启拆红包还是有几个红包拆
							if (nochecknum == 1) {
								Intent intent = new Intent(context,
										org.aisin.sipphone.RedDialog.class);
								intent.putExtra("aoutup", "aoutup");// 主动联网检测剩余未拆红包
								intent.putExtra("fromnickname",
										robject.getFromnickname());
								intent.putExtra("from", robject.getFrom());
								intent.putExtra("gift_id", robject.getGift_id());
								intent.putExtra("name", robject.getName());
								intent.putExtra("tips", robject.getTips());
								intent.putExtra("anim", "diaoluo");// 掉落动画
								intent.putExtra("gift_type", "logindaily");
								context.startActivity(intent);
								// 开启动画
								((Activity) context).overridePendingTransition(
										R.anim.anim_4, R.anim.anim_3);
							} else if (nochecknum > 1) {
								Intent intent = new Intent(
										context,
										org.aisin.sipphone.setts.RedDialogNums.class);
								intent.putExtra("nums", nochecknum + "");
								context.startActivity(intent);
								((Activity) context).overridePendingTransition(
										R.anim.anim_4, R.anim.anim_3);
							}
						}
						mHandler.sendEmptyMessage(9);
					} catch (Exception e) {
					}

				}
			}
		}).start();
	}
}
