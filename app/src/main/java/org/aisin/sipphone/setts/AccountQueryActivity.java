package org.aisin.sipphone.setts;

import java.util.TreeMap;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AccountQueryActivity extends Activity implements OnClickListener {
	private LinearLayout accoutqueryactivity_linlayout;
	private ImageView setts_accountquery_back;
	private TextView setts_accountquery_text_sjhm;
	private TextView setts_accountquery_text_kyye;
	private TextView setts_accountquery_text_yxqz;
	private TextView setts_accountquery_text_tcmc;
	private TextView setts_accountquery_text_bydq;
	private TextView switch_accquery_ljcz;
	private ProgressDialog prd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountqueryactivity);
		accoutqueryactivity_linlayout = (LinearLayout) this
				.findViewById(R.id.accoutqueryactivity_linlayout);
		setts_accountquery_back = (ImageView) this
				.findViewById(R.id.setts_accountquery_back);
		setts_accountquery_text_sjhm = (TextView) this
				.findViewById(R.id.setts_accountquery_text_sjhm);
		setts_accountquery_text_kyye = (TextView) this
				.findViewById(R.id.setts_accountquery_text_kyye);
		setts_accountquery_text_yxqz = (TextView) this
				.findViewById(R.id.setts_accountquery_text_yxqz);
		setts_accountquery_text_tcmc = (TextView) this
				.findViewById(R.id.setts_accountquery_text_tcmc);
		setts_accountquery_text_bydq = (TextView) this
				.findViewById(R.id.setts_accountquery_text_bydq);
		switch_accquery_ljcz = (TextView) this
				.findViewById(R.id.switch_accquery_ljcz);
		setts_accountquery_back.setOnClickListener(this);
		switch_accquery_ljcz.setOnClickListener(this);
		prd = new ProgressDialog(AccountQueryActivity.this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在查询，请稍候...");
		prd.show();
		// 开启查询
		new HttpTask_accountquery().execute("cxzh");
	}

	private class HttpTask_accountquery extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 获取查询URL
			String url = URLTools
					.GetHttpURL_4Accountquery_YE(AccountQueryActivity.this);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
			JSONObject json = null;
			int doresult = -14;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer
							.parseInt(json.optString("result", "-14"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:
				try {
					String balance = json.getString("balance") + "元";
					String validate = json.getString("validate");
					setts_accountquery_text_sjhm.setText(UserInfo_db
							.getUserInfo(AccountQueryActivity.this).getPhone());
					setts_accountquery_text_kyye.setText(balance);
					setts_accountquery_text_yxqz.setText(validate);
					if (!"".equals(json.optString("package", ""))) {
						JSONArray jsarray = json.getJSONArray("package");
						JSONObject jsobj_tc = jsarray.getJSONObject(0);
						String product = jsobj_tc.getString("product");
						TreeMap<String, String> map = new TreeMap<String, String>();
						map.put("1", "包90天套餐");
						map.put("2", "包30天套餐");
						map.put("12", "包365天套餐");
						map.put("18", "包180天套餐");
						product = map.get(product.trim()) == null ? "无" : map
								.get(product.trim());
						String exp_time = jsobj_tc.getString("exp_time");
						setts_accountquery_text_tcmc.setText(product);
						setts_accountquery_text_bydq.setText(exp_time);
					} else {
						setts_accountquery_text_tcmc.setText("无");
						setts_accountquery_text_bydq.setText("无");
					}
				} catch (Exception e) {
				}
				break;
			case -1:
				Toast.makeText(AccountQueryActivity.this, "数据查询失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
				break;
			case -2:
				Toast.makeText(AccountQueryActivity.this, "参数错误,请联系客服人员!",
						Toast.LENGTH_SHORT).show();
				break;
			case -6:
				Toast.makeText(AccountQueryActivity.this, "Sign错误,,请联系客服人员!",
						Toast.LENGTH_SHORT).show();
				break;
			case -8:
				Toast.makeText(AccountQueryActivity.this, "账户已过有效期!",
						Toast.LENGTH_SHORT).show();
				break;
			case -9:
				Toast.makeText(AccountQueryActivity.this, "账户余额不足!",
						Toast.LENGTH_SHORT).show();
				break;
			case -10:
				Toast.makeText(AccountQueryActivity.this, "账户已被冻结,请联系客服人员!",
						Toast.LENGTH_SHORT).show();
				break;
			case -11:
				Toast.makeText(AccountQueryActivity.this, "后台程序错误,请联系客服人员!",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(AccountQueryActivity.this, "网络连接失败,请稍候再试!",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setts_accountquery_back:
			finish();
			break;
		case R.id.switch_accquery_ljcz:
			Intent intent = new Intent(AccountQueryActivity.this,
					SetRecharge.class);
			startActivity(intent);
			finish();
			break;

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(accoutqueryactivity_linlayout);// 回收容
	}

}
