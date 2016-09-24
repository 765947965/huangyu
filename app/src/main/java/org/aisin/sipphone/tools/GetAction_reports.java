package org.aisin.sipphone.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.aisin.sipphone.commong.Action_report;
import org.aisin.sipphone.commong.UserInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

public class GetAction_reports {

	private static TreeMap<String, Action_report> areport;

	private static TreeMap<String, Action_report> GetARS(Context context) {
		if (areport != null) {
			return areport;
		} else {
			areport = new TreeMap<String, Action_report>();
			try {
				FileInputStream in = context.openFileInput("GAREPORTJSON");
				StringBuilder strb = new StringBuilder();
				byte[] bys = new byte[1024];
				int num = 0;
				while ((num = in.read(bys)) != -1) {
					strb.append(new String(bys, 0, num, "UTF-8"));
				}
				in.close();
				JSONObject json = new JSONObject(strb.toString());
				JSONArray arrays = json.getJSONArray("adreport");
				for (int i = 0; i < arrays.length(); i++) {
					JSONObject jsona = arrays.getJSONObject(i);
					Action_report art = new Action_report();
					art.setAgent_id(jsona.optString("agent_id"));
					art.setAdid(jsona.optString("adid"));
					art.setPicname(jsona.optString("picname"));
					art.setShownum(jsona.optInt("shownum"));
					art.setClicknum(jsona.optInt("clicknum"));
					areport.put(jsona.optString("picname"), art);
				}
			} catch (Exception e) {
			}
			return areport;
		}
	}

	public static synchronized String ADDARSNUM(Context context, String adid,
			String picname, int shownum, int clicknum) {
		GetARS(context);
		if (adid == null && picname == null) {// 用于记录数据 或者其它操作
			try {
				JSONArray jsay = new JSONArray();
				for (Entry<String, Action_report> ent : areport.entrySet()) {
					Action_report artz = ent.getValue();
					JSONObject js = new JSONObject();
					js.put("agent_id", artz.getAgent_id());
					js.put("adid", artz.getAdid());
					js.put("picname", artz.getPicname());
					js.put("shownum", artz.getShownum());
					js.put("clicknum", artz.getClicknum());
					jsay.put(js);
				}
				if (shownum == -1) {
					JSONObject jo = new JSONObject();
					jo.put("adreport", jsay);
					FileOutputStream out = context.openFileOutput(
							"GAREPORTJSON", Context.MODE_PRIVATE);
					out.write(jo.toString().getBytes("UTF-8"));
					out.flush();
					out.close();
				}
				return jsay.toString();
			} catch (Exception e) {
			}
		}
		if (adid == null) {// 用于初始化
			return null;
		}
		if (picname == null) {// 用于清除数据
			areport.clear();
			return null;
		}
		if (areport.get(picname) == null) {
			UserInfo usi = UserInfo_db.getUserInfo(context);
			Action_report art = new Action_report();
			art.setAgent_id(usi.getAgent_id());
			art.setAdid(adid);
			art.setPicname(picname);
			art.setShownum(shownum);
			art.setClicknum(clicknum);
			areport.put(picname, art);
		} else {
			Action_report art = areport.get(picname);
			art.setShownum(art.getShownum() + shownum);
			art.setClicknum(art.getClicknum() + clicknum);
		}
		return null;
	}
}
