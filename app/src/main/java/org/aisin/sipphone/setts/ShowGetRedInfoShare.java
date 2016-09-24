package org.aisin.sipphone.setts;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class ShowGetRedInfoShare extends Activity {
	private RelativeLayout removelayout;

	private String invite_sns_message;
	private String command;
	private UserInfo userinfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showgetredinfoshare);
		removelayout = (RelativeLayout) this.findViewById(R.id.removelayout);
		invite_sns_message = this.getIntent().getStringExtra(
				"invite_sns_message");
		command = this.getIntent().getStringExtra("command");
		userinfo = UserInfo_db.getUserInfo(this);
	}

	public void OnChangeclick(View v) {
		int id = v.getId();
		Intent intent = new Intent(ShowGetRedInfoShare.this,
				org.aisin.sipphone.setts.SharedActivity.class);
		intent.putExtra("invite_sns_message", invite_sns_message);
		intent.putExtra("invite_app", "环宇-红包");
		intent.putExtra("bitmapid", R.drawable.redsharedimage);
		intent.putExtra("iamgeurl", HttpUtils.redsharedImageurl);
		intent.putExtra(
				"invite_url",
				SharedPreferencesTools
						.getSharedPreferences_msglist_date_share(
								ShowGetRedInfoShare.this)
						.getString(
								SharedPreferencesTools.SPF_msglist_date_giftshare_url,
								"")
						.replace("gift_command=%s", "gift_command=" + command)
						.replace("uid=%s", "uid=" + userinfo.getUid()));
		switch (id) {
		case R.id.fx_weixin:
			intent.putExtra("shareflag", "weixin");
			ShowGetRedInfoShare.this.startActivity(intent);
			break;
		case R.id.fx_weibo:
			intent.putExtra("shareflag", "weibo");
			ShowGetRedInfoShare.this.startActivity(intent);
			break;
		case R.id.fx_qq:
			intent.putExtra("shareflag", "QQ");
			ShowGetRedInfoShare.this.startActivity(intent);
			break;
		case R.id.fx_weixinfriends:
			intent.putExtra("shareflag", "penyouquan");
			ShowGetRedInfoShare.this.startActivity(intent);
			break;
		case R.id.fx_qzone:
			intent.putExtra("shareflag", "Qzone");
			ShowGetRedInfoShare.this.startActivity(intent);
			break;
		case R.id.fx_message:
			Uri smsToUri = Uri.parse("smsto:");// 联系人地址
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra(
					"sms_body",
					invite_sns_message
							+ SharedPreferencesTools
									.getSharedPreferences_msglist_date_share(
											ShowGetRedInfoShare.this)
									.getString(
											SharedPreferencesTools.SPF_msglist_date_giftshare_url,
											"").replace("%s", command));
			this.startActivity(mIntent);
			break;
		case R.id.canseltext:
			finish();
			break;
		case R.id.removelayout:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (removelayout != null) {
			RecoveryTools.unbindDrawables(removelayout);// 回收容
		}
	}
}
