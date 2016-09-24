package org.aisin.sipphone;


import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class HttpTask_SharedAdd_Red extends AsyncTask<String, String, String> {

	private Context context;
	
	public HttpTask_SharedAdd_Red(Context context) {
		this.context = context;
		
	}
	@Override
	protected String doInBackground(String... params) {
		
		String url = URLTools.GetHttpURL_Add_Shared_RedPackage(context);
		String result = HttpUtils.result_url_get(url, "{'result':'-104'}");
		return result;
		
	}
	
	@Override
	protected void onPostExecute(String result) {
		
		JSONObject json = null;
		int doresult = -104;
		try {
			if (result != null) {
				json = new JSONObject(result);
				doresult = Integer.parseInt(json.optString("result", "-104"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		switch (doresult) {
		
		case 0:
			
			try {
				JSONObject jsz = new JSONObject(result);
				RedObject redobject = new RedObject();
				redobject.setFrom(jsz.optString("from"));
				redobject.setOpen_time(jsz.optString("open_time"));
				redobject.setGift_id(jsz.optString("gift_id"));
				redobject.setMoney(jsz.optString("money","0"));
				String has_open_str = jsz.optString("has_open");
				if (has_open_str != null && !"".equals(has_open_str)) {
					redobject
							.setHas_open(Integer.parseInt(has_open_str.trim()));
				} else {
					redobject.setHas_open(1);
				}
				redobject.setDirect(jsz.optString("direct"));
				redobject.setCreate_time(jsz.optString("create_time"));
				redobject.setFrom_phone(jsz.optString("from_phone"));
				redobject.setFromnickname(jsz.optString("fromnickname"));
				redobject.setMoney_type(jsz.optString("money_type"));
				redobject.setTips(jsz.optString("tips"));
				redobject.setExp_time(jsz.optString("exp_time"));
				redobject.setType(jsz.optString("type"));
				redobject.setSender_gift_id(jsz.optString("sender_gift_id"));
				redobject.setName(jsz.optString("name"));
				
				Intent intent = new Intent(context,
						org.aisin.sipphone.setts.Shared_AddRedActivity.class);
				intent.putExtra("aboveNum", "no");
				intent.putExtra("redObject",redobject);
				
				context.startActivity(intent);
				// 开启动画
				((Activity) context).overridePendingTransition(R.anim.anim_4,
						R.anim.anim_3);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
			
		case 54:
			
			//new AisinBuildDialog(context, "result", ""+doresult);
			Intent intent1 = new Intent(context,
					org.aisin.sipphone.setts.Shared_AddRedActivity.class);
			intent1.putExtra("aboveNum", "yes");
			context.startActivity(intent1);
			// 开启动画
			((Activity) context).overridePendingTransition(R.anim.anim_4,
					R.anim.anim_3);
			break;
		default:
			
			break;
		}
		 
		  
	}
	

}
