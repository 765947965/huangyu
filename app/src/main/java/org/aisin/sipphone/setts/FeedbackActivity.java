package org.aisin.sipphone.setts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.tools.Common;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.MD5;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.UserInfo_db;
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
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FeedbackActivity extends Activity implements TextWatcher,
		OnClickListener, OnItemClickListener {
	private LinearLayout removelayout;
	private ImageView setts_selfpd_cqianming_back;
	private TextView titleinput;
	private EditText spd_cn_inputqianming;
	private TextView sendfeedback;
	private ArrayList<String> items;
	private ListView listview;
	private boolean showd = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedbackactivity);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		setts_selfpd_cqianming_back = (ImageView) this
				.findViewById(R.id.setts_selfpd_cqianming_back);
		titleinput = (TextView) this.findViewById(R.id.titleinput);
		spd_cn_inputqianming = (EditText) this
				.findViewById(R.id.spd_cn_inputqianming);
		sendfeedback = (TextView) this.findViewById(R.id.sendfeedback);
		listview = (ListView) this.findViewById(R.id.listview);
		spd_cn_inputqianming.addTextChangedListener(this);
		setts_selfpd_cqianming_back.setOnClickListener(this);
		sendfeedback.setOnClickListener(this);
		titleinput.setOnClickListener(this);
		removelayout.setOnClickListener(this);
		items = new ArrayList<String>();
		items.add("一、App使用异常（如闪退、卡顿、死机等）");
		items.add("二、通话质量问题（如无回拨，无法拨出,听不清对方讲话，拨出延时等）");
		items.add("三、通讯录与通话记录问题（如无法显示，显示异常等）");
		items.add("四、发现界面问题（如无法显示，无法跳转，跳转异常等）");
		items.add("五、充值问题（如无法充值，充值不到账，充值成功后无通知等）");
		items.add("六、其他问题");
		items.add("七、意见建议");
		titleinput.setText(items.get(0));
		List<Map<String, Object>> listdata = new ArrayList<Map<String, Object>>();
		for (String itemtext : items) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("itemtext", itemtext);
			listdata.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, listdata,
				R.layout.spinneritem, new String[] { "itemtext" },
				new int[] { R.id.itemtext });
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
		listview.setVisibility(View.GONE);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if ("".equals(spd_cn_inputqianming.getText().toString())) {
			sendfeedback.setEnabled(false);
		} else {
			sendfeedback.setEnabled(true);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	private void anim(View v, int time, float fromy, float toy) {
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, fromy, Animation.RELATIVE_TO_SELF,
				toy);
		mup0.setDuration(time);
		animup.addAnimation(mup0);
		v.startAnimation(animup);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.setts_selfpd_cqianming_back) {
			finish();
		} else if (id == R.id.sendfeedback) {
			new HttpTask_feed().execute("");
		} else if (id == R.id.titleinput) {
			listview.setVisibility(View.VISIBLE);
			anim(listview, 200, -1f, 0);
			showd = true;
		} else if (id == R.id.removelayout) {
			if (showd) {
				showd = false;
				anim(listview, 200, 0, -1f);
				listview.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容器
	}

	private class HttpTask_feed extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);
			HttpPost post = new HttpPost(HttpUtils.ADVICE);
			try {
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				UserInfo userinfo = UserInfo_db
						.getUserInfo(FeedbackActivity.this);
				list.add(new BasicNameValuePair("uid", userinfo.getUid()));
				list.add(new BasicNameValuePair("phone", userinfo.getPhone()));
				list.add(new BasicNameValuePair("sign", MD5.toMD5(userinfo
						.getUid().trim() + Common.SIGN_KEY)));
				list.add(new BasicNameValuePair("brandname",
						Constants.BrandName));
				list.add(new BasicNameValuePair("agent_id", userinfo
						.getAgent_id()));
				list.add(new BasicNameValuePair("pv", userinfo.getPv()));
				list.add(new BasicNameValuePair("v", userinfo.getV()));
				list.add(new BasicNameValuePair("title", titleinput.getText()
						.toString()));
				list.add(new BasicNameValuePair("advice", spd_cn_inputqianming
						.getText().toString()));
				UrlEncodedFormEntity formEntiry = new UrlEncodedFormEntity(
						list, HTTP.UTF_8);
				post.setEntity(formEntiry);
				HttpResponse response = httpclient.execute(post);
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					return EntityUtils.toString(response.getEntity(), "UTF-8");
				}
				return null;
			} catch (Exception e) {
				return null;
			} finally {
				post.abort();
			}
		}

		@Override
		protected void onPostExecute(String result) {
			// 处理数据
			JSONObject json = null;
			int doresult = -104;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = Integer.parseInt(json
							.optString("result", "-104"));
				}
			} catch (Exception e) {
			}
			switch (doresult) {
			case 0:
				new AisinBuildDialog(FeedbackActivity.this, "提示", "提交成功!");
				break;
			case 5:
				new AisinBuildDialog(FeedbackActivity.this, "提示", "参数不能为空!");
				break;
			case 6:
				new AisinBuildDialog(FeedbackActivity.this, "提示", "sign错误!");
				break;
			case -104:
				new AisinBuildDialog(FeedbackActivity.this, "提示",
						"网络连接不可用!请检查网络");
				break;
			default:
				new AisinBuildDialog(FeedbackActivity.this, "提示", "提交失败!请稍后再试!"
						+ doresult);
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		titleinput.setText(items.get(arg2));
		if (showd) {
			showd = false;
			anim(listview, 200, 0, -1f);
			listview.setVisibility(View.GONE);
		}
	}

}
