package org.aisin.sipphone.setts;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.RedObject;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.myview.AisinBuildDialog;
import org.aisin.sipphone.myview.AisinBuildDialog.onMyItemClickListener;
import org.aisin.sipphone.sqlitedb.RedData_DBHelp;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.URLTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonalRedEnvelopeConfig extends Activity implements
		OnClickListener {
	private LinearLayout removelayout;
	private ImageView palpc_back;

	private TextView towhere;

	private ImageView redconfigheadimage;

	private EditText whoimisedit;

	private TextView red_lx;
	private String red_lxtext;

	private TextView allmoney;
	private EditText money_input;
	private TextView danwei;

	private EditText palpc_inputzfy;

	private TextView bttallmoney;

	private TextView sendpalpcredbt;

	private ArrayList<String> uidorphones;
	private String addred_flag;
	private String remark;

	private UserXXInfo uxi;
	private UserInfo usinfo;

	private Intent startintent;// 启动该界面的意图
	private ProgressDialog prd;

	private int balance = 100000;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				red_lx.setText(red_lxtext);
				if ("话费-普通红包".equals(red_lxtext)
						|| "话费-拼手气红包".equals(red_lxtext)) {
					allmoney.setText("总金额");
					danwei.setText("元");
					Spanned spnd = Html
							.fromHtml("<font color=#AAA292>总金额  </font><font color=#DB3917>"
									+ money_input.getText().toString()
									+ ".00</font><font color=#AAA292>元  </font>");
					bttallmoney.setText(spnd);
				} else if ("流量-普通红包".equals(red_lxtext)
						|| "流量-拼手气红包".equals(red_lxtext)) {
					allmoney.setText("总流量");
					danwei.setText("KB");
					Spanned spnd = Html
							.fromHtml("<font color=#AAA292>总流量  </font><font color=#DB3917>"
									+ money_input.getText().toString()
									+ ".00</font><font color=#AAA292>KB  </font>");
					bttallmoney.setText(spnd);
				}
			} else if (msg.what == 2) {

			} else if (msg.what == 3) {
				String temp = red_lx.getText().toString();
				String money = money_input.getText().toString();
				if ("".equals(money)) {
					money = "0";
				}
				if ("话费-普通红包".equals(temp) || "话费-拼手气红包".equals(temp)) {
					Spanned spnd = Html
							.fromHtml("<font color=#AAA292>总金额  </font><font color=#DB3917>"
									+ money
									+ ".00</font><font color=#AAA292>元  </font>");
					bttallmoney.setText(spnd);
				} else if ("流量-普通红包".equals(temp) || "流量-拼手气红包".equals(temp)) {
					Spanned spnd = Html
							.fromHtml("<font color=#AAA292>总流量  </font><font color=#DB3917>"
									+ money
									+ ".00</font><font color=#AAA292>KB  </font>");
					bttallmoney.setText(spnd);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uidorphones = this.getIntent().getStringArrayListExtra("uidorphones");
		addred_flag = this.getIntent().getStringExtra("addred_flag");
		remark = this.getIntent().getStringExtra("remark");
		if (uidorphones == null || addred_flag == null
				|| "".equals(addred_flag)) {
			finish();
			return;
		}
		startintent = this.getIntent();
		setContentView(R.layout.personalredenvelopeconfig);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		towhere = (TextView) this.findViewById(R.id.towhere);
		redconfigheadimage = (ImageView) this
				.findViewById(R.id.redconfigheadimage);
		palpc_back = (ImageView) this.findViewById(R.id.palpc_back);
		whoimisedit = (EditText) this.findViewById(R.id.whoimisedit);
		red_lx = (TextView) this.findViewById(R.id.red_lx);
		allmoney = (TextView) this.findViewById(R.id.allmoney);
		money_input = (EditText) this.findViewById(R.id.money_input);
		danwei = (TextView) this.findViewById(R.id.danwei);
		palpc_inputzfy = (EditText) this.findViewById(R.id.palpc_inputzfy);
		bttallmoney = (TextView) this.findViewById(R.id.bttallmoney);
		sendpalpcredbt = (TextView) this.findViewById(R.id.sendpalpcredbt);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在处理，请稍候...");
		if ("phone".equals(addred_flag)) {
			sendpalpcredbt.setText("塞进红包");
		}
		palpc_back.setOnClickListener(this);
		if (uidorphones.size() == 1) {
			Spanned spnd = Html
					.fromHtml("<font color=#ED2E14>准备发给  </font><font color=#FF6506>"
							+ remark + " </font>");
			towhere.setText(spnd);
		} else {
			Spanned spnd = Html
					.fromHtml("<font color=#ED2E14>准备给  </font><font color=#FF6506>"
							+ uidorphones.size()
							+ " </font><font color=#ED2E14>  人发</font>");
			towhere.setText(spnd);
		}
		usinfo = UserInfo_db.getUserInfo(this);
		try {
			uxi = User_data_Ts.getUXXInfo4DB_self(this, UserInfo_db
					.getUserInfo(this).getUid());
		} catch (Exception e) {
		}
		if (uxi == null || uxi.getName() == null || "".equals(uxi.getName())
				|| "null".equals(uxi.getName())) {
			whoimisedit.setHint("请输入姓名");
		} else {
			whoimisedit.setHint(uxi.getName());
		}
		red_lx.setOnClickListener(this);
		Spanned spnd = Html
				.fromHtml("<font color=#AAA292>总金额  </font><font color=#FF6600>0.00</font><font color=#AAA292>元  </font>");
		bttallmoney.setText(spnd);
		money_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mHandler.sendEmptyMessage(3);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		sendpalpcredbt.setOnClickListener(this);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// 获取金额
		new HttpTask_accountquery().execute("");
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		LayoutParams lp = redconfigheadimage.getLayoutParams();
		lp.height = (int) (0.306 * redconfigheadimage.getWidth());
		redconfigheadimage.setLayoutParams(lp);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(removelayout);// 回收容
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.palpc_back:
			finish();
			break;
		case R.id.red_lx:
			AisinBuildDialog mybuild = new AisinBuildDialog(
					PersonalRedEnvelopeConfig.this);
			final String[] items = { "话费-普通红包", "话费-拼手气红包" };
			mybuild.setListViewItem(items, new onMyItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					red_lxtext = items[position];
					mHandler.sendEmptyMessage(1);
				}
			});
			mybuild.dialogShow();
			break;
		case R.id.sendpalpcredbt:
			// 发红包
			if (("".equals(whoimisedit.getHint().toString()) || "请输入姓名"
					.equals(whoimisedit.getHint().toString()))
					&& "".equals(whoimisedit.getText().toString())) {
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"请输入姓名!");
			} else if (money_input.getText().toString().equals("")
					|| money_input.getText().toString().equals("0")) {
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"请输入金额!");
			} else {
				try {
					if (Integer.parseInt(money_input.getText().toString()
							.trim()) > balance) {
						new AisinBuildDialog(PersonalRedEnvelopeConfig.this,
								"提示", "输入的金额过大!");
					} else {
						prd.show();
						new HttpTask_sendRed().execute("");
					}
				} catch (Exception e) {
				}
			}
			break;
		}
	}

	class HttpTask_sendRed extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// 获取发送个人红包的URL
			String gift_type = "p2p_money";
			String money_type = "0";
			String temp = red_lx.getText().toString();
			if ("话费-普通红包".equals(temp)) {
				gift_type = "p2p_money";
				money_type = "1";
			} else if ("话费-拼手气红包".equals(temp)) {
				gift_type = "p2p_money";
				money_type = "0";
			} else if ("流量-普通红包".equals(temp)) {
				gift_type = "p2p_4gdata";
				money_type = "1";
			} else if ("流量-拼手气红包".equals(temp)) {
				gift_type = "p2p_4gdata";
				money_type = "0";
			}
			String zfy = palpc_inputzfy.getText().toString();
			if (zfy == null || "".equals(zfy)) {
				zfy = palpc_inputzfy.getHint().toString();
			}
			try {
				zfy = URLEncoder.encode(zfy, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
			String fromname = whoimisedit.getText().toString();
			if (fromname == null || "".equals(fromname)) {
				fromname = whoimisedit.getHint().toString();
			}
			try {
				fromname = URLEncoder.encode(fromname, "utf-8");
			} catch (UnsupportedEncodingException e) {
			}
			String url = URLTools.GetHttpURL_4SendPersonRed(
					PersonalRedEnvelopeConfig.this, uidorphones, addred_flag,
					usinfo.getUid() + "", gift_type, money_input.getText()
							.toString().trim()
							+ "00", money_type, temp, zfy, fromname);
			String result = HttpUtils.result_url_get(url, "{'result':'-14'}");
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			if (prd != null) {
				prd.dismiss();
			}
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
				try {
					JSONObject jsz = new JSONObject(result);
					RedObject redobject = new RedObject();
					redobject.setSplitsnumber(jsz.optString("splitsnumber"));
					redobject.setShake_ratio(jsz.optString("shake_ratio"));
					redobject.setReceived_money(jsz.optString("received_money",
							"0"));
					redobject.setReturned_money(jsz.optString("returned_money",
							"0"));
					redobject.setStatus(jsz.optString("status"));
					redobject.setSub_type(jsz.optString("sub_type"));
					redobject.setCommand(jsz.optString("command"));
					redobject.setFrom(jsz.optString("from"));
					redobject.setOpen_time(jsz.optString("open_time"));
					redobject.setGift_id(jsz.optString("gift_id"));
					redobject.setMoney(jsz.optString("money", "0"));
					String has_open_str = jsz.optString("has_open");
					if (has_open_str != null && !"".equals(has_open_str)) {
						redobject.setHas_open(Integer.parseInt(has_open_str
								.trim()));
					} else {
						redobject.setHas_open(0);
					}
					redobject.setDirect(jsz.optString("direct"));
					redobject.setCreate_time(jsz.optString("create_time"));
					redobject.setFrom_phone(jsz.optString("from_phone"));
					redobject.setFromnickname(jsz.optString("fromnickname"));
					redobject.setMoney_type(jsz.optString("money_type"));
					redobject.setTips(jsz.optString("tips"));
					redobject.setExp_time(jsz.optString("exp_time"));
					redobject.setType(jsz.optString("type"));
					redobject
							.setSender_gift_id(jsz.optString("sender_gift_id"));
					redobject.setName(jsz.optString("name"));
					// 添加红包记录
					RedData_DBHelp.addRedDatas(PersonalRedEnvelopeConfig.this,
							redobject);
					if (startintent != null) {
						// 让选择列表的Activity结束自己
						startintent.putExtra("outflag", "over");
						PersonalRedEnvelopeConfig.this
								.setResult(1, startintent);
					}
					// 启动红包发成功的界面(是否要生成口令)
					if ("uid".equals(addred_flag)) {
						// 启动红包完成,结束自己
						Intent intent = new Intent(
								PersonalRedEnvelopeConfig.this,
								org.aisin.sipphone.setts.PersonRedSendSucess.class);
						intent.putExtra("redname", red_lx.getText().toString()
								.substring(0, 2));
						intent.putExtra("rednum", uidorphones.size());
						PersonalRedEnvelopeConfig.this.startActivity(intent);
						PersonalRedEnvelopeConfig.this.finish();
					} else {
						// 启动设置口令界面
						Intent intent = new Intent(
								PersonalRedEnvelopeConfig.this,
								org.aisin.sipphone.setts.SetRedCodeBefore.class);
						intent.putStringArrayListExtra("phones", uidorphones);
						intent.putExtra("codetype", "personal");
						intent.putExtra("title_text1", "个人红包已准备好");
						intent.putExtra("title_text2", "要发非环宇好友红包请生成红包口令");
						intent.putExtra("sended_gift_id",
								redobject.getGift_id());
						PersonalRedEnvelopeConfig.this.startActivity(intent);
						PersonalRedEnvelopeConfig.this.finish();
					}

				} catch (Exception e) {
				}
				break;
			case 6:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"sign错误!");
				break;
			case 50:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"红包添加失败(id已存在),请稍后再试");
				break;
			case 54:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"当天添加红包个数已达上限");
				break;
			case 55:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"不支持该类型红包");
				break;
			case 45:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"服务器异常!");
				break;
			case 19:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"余额不足!");
				break;
			default:
				new AisinBuildDialog(PersonalRedEnvelopeConfig.this, "提示",
						"联网失败，请检查您的网络信号！");
				break;
			}

		}
	}

	// 查询余额
	private class HttpTask_accountquery extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... paramArrayOfParams) {
			// 获取查询URL
			String url = URLTools
					.GetHttpURL_4Accountquery_YE(PersonalRedEnvelopeConfig.this);
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
					balance = (int) Double.parseDouble(json
							.getString("balance").trim());
					money_input.setHint("可用:" + balance);
				} catch (Exception e) {
				}
				break;
			}
		}
	}
}
