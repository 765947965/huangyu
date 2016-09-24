package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.DialogBuildConfirmListener;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_format;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MobileCardRecharge extends Activity implements OnClickListener,
		onMyItemClickListener {
	private LinearLayout mobile_card_recharge_linlayout;
	private ImageView setting_mcr_back;
	private ImageView qrcodecom;
	private LinearLayout mianellayout;
	private TextView setting_mcr_back_text;
	private TextView prepaid_card_type;
	private EditText prepaid_card_num;
	private EditText prepaid_card_password;
	private TextView prepaid_card_bt;
	private TextView chongzhitishi;
	private String[] Recharge_values = { "充50元到账200元", "充100元到账500元",
			"充200元到账1000元", "充500元到账2500元" };
	private String cardValue = "50";
	private String paytype = "";// 充值卡类型
	private AisinBuildDialog mybuild;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mobile_card_recharge);
		mobile_card_recharge_linlayout = (LinearLayout) this
				.findViewById(R.id.mobile_card_recharge_linlayout);
		setting_mcr_back = (ImageView) this.findViewById(R.id.setting_mcr_back);
		qrcodecom = (ImageView) this.findViewById(R.id.qrcodecom);
		mianellayout = (LinearLayout) this.findViewById(R.id.mianellayout);
		setting_mcr_back_text = (TextView) this
				.findViewById(R.id.setting_mcr_back_text);
		prepaid_card_type = (TextView) this
				.findViewById(R.id.prepaid_card_type);
		prepaid_card_num = (EditText) this.findViewById(R.id.prepaid_card_num);
		prepaid_card_password = (EditText) this
				.findViewById(R.id.prepaid_card_password);
		prepaid_card_bt = (TextView) this.findViewById(R.id.prepaid_card_bt);
		chongzhitishi = (TextView) this.findViewById(R.id.chongzhitishi);
		setting_mcr_back.setOnClickListener(this);
		prepaid_card_bt.setOnClickListener(this);
		qrcodecom.setOnClickListener(this);
		prepaid_card_type.setOnClickListener(this);

		String title_name = getIntent().getStringExtra("setting_mcr_back_text");
		if (title_name == null | title_name.length() == 0) {
			title_name = "手机卡充值";
		}
		if (title_name.equals("移动卡充值")) {
			paytype = "29";
		}
		if (title_name.equals("联通卡充值")) {
			paytype = "36";
		}
		if (title_name.equals("电信卡充值")) {
			paytype = "38";
		}
		if (title_name.equals("环宇卡充值")) {
			paytype = "98";
			cardValue = "10";
			mianellayout.setVisibility(View.GONE);
			// qrcodecom.setVisibility(View.VISIBLE);
			chongzhitishi.setText("环宇卡是指官方网站发行的充值卡并且充值无赠送！");
		}
		prepaid_card_type.setText(Recharge_values[0]);
		setting_mcr_back_text.setText(title_name);
		mybuild = new AisinBuildDialog(this);
		mybuild.setTitle("请选择充值卡面额!");
		mybuild.setListViewItem(Recharge_values, this);
		mybuild.setOnDialogCancelListener("取消", null);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.setting_mcr_back:
			finish();
			break;
		case R.id.prepaid_card_type:
			mybuild.dialogShow();
			break;
		case R.id.prepaid_card_bt:
			if (!Check_network.isNetworkAvailable(MobileCardRecharge.this)) {
				Toast.makeText(this, "网络不可用,请检查网络连接!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (TextUtils.isEmpty(prepaid_card_num.getText().toString())) {
				Toast.makeText(this, "请输入正确的充值卡序列号!", Toast.LENGTH_SHORT)
						.show();
				return;
			}
			if (TextUtils.isEmpty(prepaid_card_password.getText().toString())) {
				Toast.makeText(this, "请输入正确的充值卡密码!", Toast.LENGTH_SHORT).show();
				return;
			}
			prepaid_card_bt.setEnabled(false);
			prepaid_card_bt.setText("正在充值...");
			// 请求服务器
			new HttpTask_cardrecharge().execute("cardrecharge");
			break;
		case R.id.qrcodecom:
			// 扫二维码
			Intent intent = new Intent(MobileCardRecharge.this,
					com.dtr.zxing.activity.CaptureActivity.class);
			startActivityForResult(intent, 1);
			break;
		}
	}

	private class HttpTask_cardrecharge extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			String url = URLTools.GetHttpURL_4Chongzhika(
					MobileCardRecharge.this, paytype, "2", cardValue,
					prepaid_card_num.getText().toString().trim(),
					prepaid_card_password.getText().toString().trim());
			String result = HttpUtils.result_url_get(url, "{'result':'-100'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			JSONObject json = null;
			int doresult = -100;
			try {
				if (result != null) {
					json = new JSONObject(result);
					doresult = json.optInt("result", -100);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			switch (doresult) {
			case 0:
				// 需要刷新msglist和广告，重置msglist检查更新的时间，让主Activity再次检查并存储更新信息
				CheckUpadateTime.ReSetValue(MobileCardRecharge.this);
				AisinBuildDialog mybuild = new AisinBuildDialog(
						MobileCardRecharge.this);
				mybuild.setTitle("提示");
				mybuild.setMessage("提交成功正在处理中，请在2分钟以后查询余额！");
				mybuild.setOnDialogConfirmListener("确定",
						new DialogBuildConfirmListener() {
							@Override
							public void dialogConfirm() {
								MobileCardRecharge.this
										.sendBroadcast(new Intent(
												Constants.BrandName
														+ ".find.upServerdata"));
								CheckUpadateTime
										.ReSetValue(MobileCardRecharge.this);
								MobileCardRecharge.this.finish();
							}
						});
				mybuild.dialogShow();
				return;
			case -3:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"充值卡号或者密码错误！");
				break;
			case -5:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"充值卡号或者密码错误！");
				break;
			case -6:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"充值卡号或者密码错误！");
				break;
			case -7:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"充值卡号或者密码错误！");
				break;
			case -9:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"充值卡号或者密码错误！");
				break;
			case -104:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"联网失败，数据解析错误！");
				break;
			case -100:// -100
				new AisinBuildDialog(MobileCardRecharge.this, "提示", "无法连接网络！");
				break;
			default:
				new AisinBuildDialog(MobileCardRecharge.this, "提示",
						"提交请求失败，请稍后再试！");
				break;
			}
			prepaid_card_bt.setEnabled(true);
			prepaid_card_bt.setText("确定充值");
			super.onPostExecute(result);

		}
	}

	private void setcardValue(int position) {
		switch (position) {
		case 0:
			cardValue = "50";
			prepaid_card_type.setText(Recharge_values[position]);
			break;

		case 1:
			cardValue = "100";
			prepaid_card_type.setText(Recharge_values[position]);
			break;
		case 2:
			cardValue = "200";
			prepaid_card_type.setText(Recharge_values[position]);
			break;
		case 3:
			cardValue = "500";
			prepaid_card_type.setText(Recharge_values[position]);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && data != null) {
			String rawResult = data.getStringExtra("rawResult");
			// 对返回值进行判断
			if (rawResult == null) {
				new AisinBuildDialog(MobileCardRecharge.this, "提示", "请扫描充值二维码!");
			} else {
				try {
					String[] strs = rawResult.split("&");
					String[] branndnametype = strs[0].split("=");
					String[] cardnotype = strs[1].split("=");
					String[] pwdtype = strs[2].split("=");
					if (!"recharge".equals(branndnametype[0])
							|| !"cardno".equals(cardnotype[0])
							|| !"pwd".equals(pwdtype[0])) {
						throw new RuntimeException("非法");
					}
					String branndname = branndnametype[1];
					String cardno = cardnotype[1];
					String pwd = pwdtype[1];
					if (!Constants.BrandName.equals(branndname)) {
						new AisinBuildDialog(MobileCardRecharge.this, "提示",
								"请使用对应品牌的充值卡!");
						return;
					}
					prepaid_card_num.setText(cardno);
					prepaid_card_password.setText(pwd);
				} catch (Exception e) {
					new AisinBuildDialog(MobileCardRecharge.this, "提示",
							"请扫描充值二维码!");
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(mobile_card_recharge_linlayout);// 回收容器
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		setcardValue(position);
	}
}
