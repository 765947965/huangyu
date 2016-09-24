package org.aisin.sipphone.aipay;

import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.tools.RecoveryTools;

import com.alipay.sdk.pay.demo.PayDemoActivity;
import com.weixin.paydemo.PayActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MyPayActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private LinearLayout removelayout;
	private TextView paytitlename;
	private ImageView call_details_back;
	private TextView maillist_txlbt;
	private TextView maillist_hybt;
	private ListView listview;
	private TextView text_bt;
	private TextView text_ts;
	private ProgressDialog prd;

	private String RechargeFlag;
	private MyPayActivityAdapter adapter;
	private Productinfos ppcfnoew;// 当前选中的充值类型
	private ArrayList<Productinfos> pcsAll;
	private ArrayList<Productinfos> pcsShow = new ArrayList<Productinfos>();

	private int googstype = 2;// 默认2直冲 1包月
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 1) {
				pcsShow.clear();
				for (Productinfos np : pcsAll) {
					if (np.getGoodstype() == googstype) {
						pcsShow.add(np);
						if (np.isChecked()) {
							ppcfnoew = np;
						}
					}
				}
				mHandler.sendEmptyMessage(2);
			} else if (msg.what == 2) {
				if (adapter == null) {
					adapter = new MyPayActivityAdapter(MyPayActivity.this,
							pcsShow);
					listview.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			} else if (msg.what == 3) {
				prd.dismiss();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		RechargeFlag = this.getIntent().getStringExtra("RechargeFlag");
		if (RechargeFlag == null || "".equals(RechargeFlag)) {
			finish();
			return;
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mypayactivity);
		removelayout = (LinearLayout) this.findViewById(R.id.removelayout);
		paytitlename = (TextView) this.findViewById(R.id.paytitlename);
		call_details_back = (ImageView) this
				.findViewById(R.id.call_details_back);
		maillist_txlbt = (TextView) this.findViewById(R.id.maillist_txlbt);
		maillist_hybt = (TextView) this.findViewById(R.id.maillist_hybt);
		listview = (ListView) this.findViewById(R.id.listview);
		text_bt = (TextView) this.findViewById(R.id.text_bt);
		text_ts = (TextView) this.findViewById(R.id.text_ts);
		call_details_back.setOnClickListener(this);
		maillist_txlbt.setOnClickListener(this);
		maillist_hybt.setOnClickListener(this);
		text_bt.setOnClickListener(this);
		listview.setOnItemClickListener(this);
		prd = new ProgressDialog(this);
		prd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		prd.setMessage("正在获取订单信息...");
		if ("ZFB".equals(RechargeFlag)) {
			paytitlename.setText("支付宝充值");
			text_ts.setText("将免费获取手机支付插件，该插件是由支付宝公司提供，请放心使用！");
			pcsAll = NewProduct.getproducts_ZFB();
		} else if ("WX".equals(RechargeFlag)) {
			paytitlename.setText("微信充值");
			text_ts.setText("将免费获取手机支付插件，该插件是由腾讯公司提供，请放心使用！");
			pcsAll = NewProduct.getproducts_WX();
		}
		if (pcsAll != null) {
			mHandler.sendEmptyMessage(1);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.call_details_back) {
			finish();
		} else if (id == R.id.maillist_txlbt) {
			if (googstype == 2) {
				return;
			}
			googstype = 2;
			maillist_txlbt.setTextColor(Color.parseColor("#FFFFFF"));
			maillist_txlbt
					.setBackgroundResource(R.drawable.maillisttitlebanck_txl_pres);
			maillist_hybt.setTextColor(Color.parseColor("#332c2b"));
			maillist_hybt
					.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_dis);
			mHandler.sendEmptyMessage(1);
		} else if (id == R.id.maillist_hybt) {
			if (googstype == 1) {
				return;
			}
			googstype = 1;
			maillist_txlbt.setTextColor(Color.parseColor("#332c2b"));
			maillist_txlbt
					.setBackgroundResource(R.drawable.maillisttitlebanck_txl_dis);
			maillist_hybt.setTextColor(Color.parseColor("#FFFFFF"));
			maillist_hybt
					.setBackgroundResource(R.drawable.maillisttitlebanck_axhy_pres);
			mHandler.sendEmptyMessage(1);
		} else if (id == R.id.text_bt) {// 充值
			// Log.i("环宇", ppcfnoew.getBody());
			if ("ZFB".equals(RechargeFlag)) {
				new PayDemoActivity(MyPayActivity.this, ppcfnoew.getSubject(),
						ppcfnoew.getBody(), ppcfnoew.getPrice() + "").pay();
			} else if ("WX".equals(RechargeFlag)) {
				prd.show();
				new PayActivity(MyPayActivity.this, ppcfnoew, mHandler).Pay();
			}
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		prd.dismiss();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容器
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		for (Productinfos pcf : pcsShow) {
			pcf.setChecked(false);
		}
		pcsShow.get(position).setChecked(true);
		ppcfnoew = pcsShow.get(position);
		mHandler.sendEmptyMessage(2);
	}

}
