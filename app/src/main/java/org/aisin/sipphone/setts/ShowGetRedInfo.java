package org.aisin.sipphone.setts;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.CircleImageView;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowGetRedInfo extends Activity {
	private LinearLayout removelayout;
	private CircleImageView reddetails_from_iamge;
	private TextView redfroemname;
	private TextView tipstext;
	private TextView moneytext;
	private TextView moneytext_dj;
	private EditText mythankstext;
	private ImageView qunimage;
	private ImageView lingimage;
	private ImageView money_typeimage;

	private String from;
	private String fromnickname;
	private String tips;
	private String money;
	private String type;
	private String name;
	private String gift_id;
	private String sended_gift_id;
	private String sub_type;
	private String money_type;
	private String command;

	private Bitmap bitmapheaimage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		from = this.getIntent().getStringExtra("from");
		fromnickname = this.getIntent().getStringExtra("fromnickname");
		tips = this.getIntent().getStringExtra("tips");
		money = this.getIntent().getStringExtra("money");
		type = this.getIntent().getStringExtra("type");
		name = this.getIntent().getStringExtra("name");
		gift_id = this.getIntent().getStringExtra("gift_id");
		sended_gift_id = this.getIntent().getStringExtra("sended_gift_id");
		sub_type = this.getIntent().getStringExtra("sub_type");
		money_type = this.getIntent().getStringExtra("money_type");
		command = this.getIntent().getStringExtra("command");
		setContentView(R.layout.showgetredinfo);
		tipstext = (TextView) this.findViewById(R.id.tipstext);
		redfroemname = (TextView) this.findViewById(R.id.redfroemname);
		reddetails_from_iamge = (CircleImageView) this
				.findViewById(R.id.reddetails_from_iamge);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		moneytext = (TextView) this.findViewById(R.id.moneytext);
		moneytext_dj = (TextView) this.findViewById(R.id.moneytext_dj);
		mythankstext = (EditText) this.findViewById(R.id.mythankstext);
		qunimage = (ImageView) this.findViewById(R.id.qunimage);
		lingimage = (ImageView) this.findViewById(R.id.lingimage);
		money_typeimage = (ImageView) this.findViewById(R.id.money_typeimage);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		redfroemname.setText("来自" + fromnickname + "的" + name);
		if ("personnocommand".equals(sub_type)) {

		} else if ("personwithcommand".equals(sub_type)) {
			lingimage.setVisibility(View.VISIBLE);
		} else if ("groupnocommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
		} else if ("groupwithcommand".equals(sub_type)) {
			qunimage.setVisibility(View.VISIBLE);
			lingimage.setVisibility(View.VISIBLE);
		}
		if ("0".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_0);
			money_typeimage.setVisibility(View.VISIBLE);
		} else if ("1".equals(money_type)) {
			money_typeimage.setImageResource(R.drawable.moneytype_1);
			money_typeimage.setVisibility(View.VISIBLE);
		}
		tipstext.setText(tips);
		try {
			// 设置金额及使用详情
			if ("logindaily".equals(type.trim())
					|| type.trim().endsWith("_money")) {
				// 环宇每日登录红包或者金钱红包
				double money_temp = Double.parseDouble(money.trim())
						/ (double) 100;
				moneytext.setText(money_temp + "");
				moneytext_dj.setText("元");
			} else if (type.trim().endsWith("_month")) {
				// 赠送的天红包
				moneytext.setText(money.trim());
				moneytext_dj.setText("天");
			} else if (type.trim().endsWith("_4gdata")) {
				double money_temp = Double.parseDouble(money.trim());
				if (money_temp > 1024) {
					double b = Math.round((money_temp / (double) 1024) * 10) / 10.0;
					moneytext.setText(b + "");
					moneytext_dj.setText("MB");
				} else {
					moneytext.setText(money_temp + "KB");
					moneytext_dj.setText("KB");
				}
			} else if (type.trim().endsWith("_right")) {
				double money_temp = (double) Integer.parseInt(money.trim())
						/ (double) 100;
				moneytext.setText(money_temp + "");
				moneytext_dj.setText("元");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 设置头像
		File file = this.getFileStreamPath(from + "headimage.jpg");
		if (file.exists()) {
			bitmapheaimage = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmapheaimage != null) {
				reddetails_from_iamge.setImageBitmap(bitmapheaimage);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmapheaimage != null) {
			bitmapheaimage.recycle();
			bitmapheaimage = null;
		}
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}

	public void OnChangeclick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.showredrelayou_bt:// 开启分享页面
			String invite_sns_message = "我正在使用环宇电话,我抢到了好友"
					+ moneytext.getText().toString()
					+ moneytext_dj.getText().toString() + name
					+ "好玩极了!话费、流量不够用就赶紧来玩吧!";
			Intent intent = new Intent(ShowGetRedInfo.this,
					org.aisin.sipphone.setts.ShowGetRedInfoShare.class);
			intent.putExtra("invite_sns_message", invite_sns_message);
			intent.putExtra("command", command);
			ShowGetRedInfo.this.startActivity(intent);

			break;
		case R.id.surethangkstext:
			// 答谢好友
			if ("".equals(mythankstext.getText().toString())) {
				new AisinBuildDialog(ShowGetRedInfo.this, "提示", "请输入感谢语!");
			} else {
				new HttpTask_thanksfrends().execute("");
			}
			break;
		case R.id.reddllclosebt:
			finish();
			break;
		case R.id.showathorinfo:// 看手气
			Intent intents = new Intent(ShowGetRedInfo.this,
					org.aisin.sipphone.setts.ShowRedRecevedInfo.class);
			intents.putExtra("gift_id", sended_gift_id);
			intents.putExtra("from", from);
			intents.putExtra("fromnickname", fromnickname);
			intents.putExtra("tips", tips);
			intents.putExtra("money_type", money_type);
			ShowGetRedInfo.this.startActivity(intents);
			break;
		}
	}

	// 答谢好友
	class HttpTask_thanksfrends extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			String str_codetem = null;
			try {
				str_codetem = URLEncoder.encode(mythankstext.getText()
						.toString(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			String url = URLTools.GetHttpURL_4thanksfriend(ShowGetRedInfo.this,
					gift_id, str_codetem);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:
				new AisinBuildDialog(ShowGetRedInfo.this, "提示", "答谢成功!");
				break;
			}
		}
	}
}
