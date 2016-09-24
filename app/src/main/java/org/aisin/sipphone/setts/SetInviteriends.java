package org.aisin.sipphone.setts;

import org.aisin.sipphone.HttpTask_SharedAdd_Red;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.RecoveryTools;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SetInviteriends extends Activity implements OnClickListener {

	private LinearLayout setinviteriends_linlayout;
	private ImageView setinviteriends_bar_back;
	private ImageView share_weixinfriends_image;// 朋友圈
	private ImageView share_weixin_image;// 微信好友
	private ImageView share_qq_image;// QQ
	private ImageView share_weibo_image;// 微博
	private ImageView share_qzone_image;// QQ空间
	private ImageView share_message_image;// 短信
	private UserInfo userinfo;
	private SharedPreferences shared_SNSshare;
	private String invite_url;
	private String invite_sns_message;
	private String invite_sms_message;
	private TextView tv_share_times, tv_yaoqing_times;
	private TextView tv_shared_minites, tv_yaoqing_minites;

	private Context mContext;
	private SharedPreferences shared;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setinviteriends);
		mContext = this;
		shared = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(mContext);

		setinviteriends_linlayout = (LinearLayout) this
				.findViewById(R.id.setinviteriends_linlayout);
		userinfo = UserInfo_db.getUserInfo(SetInviteriends.this);
		setinviteriends_bar_back = (ImageView) this
				.findViewById(R.id.setinviteriends_bar_back);
		share_weixinfriends_image = (ImageView) this
				.findViewById(R.id.share_weixinfriends_image);
		share_weixin_image = (ImageView) this
				.findViewById(R.id.share_weixin_image);
		share_qq_image = (ImageView) this.findViewById(R.id.share_qq_image);
		share_weibo_image = (ImageView) this
				.findViewById(R.id.share_weibo_image);
		share_qzone_image = (ImageView) this
				.findViewById(R.id.share_qzone_image);
		share_message_image = (ImageView) this
				.findViewById(R.id.share_message_image);
		String share_times_monthly = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_SHARE_TIMES_MINITES,
				"10");
		String invite_times_monthly = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_TIME_MONTHLY,
				"20");
		String share_gift_minites = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_SHARE_GIFT_MINITES,
				"10");
		String invite_gift_minites = shared.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_GIFE_MINITES,
				"40");

		tv_share_times = (TextView) findViewById(R.id.setts_shared_times);
		tv_yaoqing_times = (TextView) findViewById(R.id.setts_yaoqing_times);
		tv_shared_minites = (TextView) findViewById(R.id.setts_shared_minites);
		tv_yaoqing_minites = (TextView) findViewById(R.id.setts_yaoqing_minites);
		try {
			/*tv_yaoqing_times.setText("邀请好友(每月最多可获得"
					+ Integer.parseInt(invite_times_monthly.trim())
					* Integer.parseInt(invite_gift_minites.trim()) + "分钟话费)");
			tv_share_times.setText("分享环宇(每月最多可获得"
					+ Integer.parseInt(share_times_monthly.trim())
					* Integer.parseInt(share_gift_minites.trim()) + "分钟话费)");*/
		} catch (Exception e) {
		}
		tv_yaoqing_minites.setText(invite_gift_minites + "分钟");
		tv_shared_minites.setText(share_gift_minites + "分钟");
		setinviteriends_bar_back.setOnClickListener(this);
		share_weixinfriends_image.setOnClickListener(this);
		share_weixin_image.setOnClickListener(this);
		share_qq_image.setOnClickListener(this);
		share_weibo_image.setOnClickListener(this);
		share_qzone_image.setOnClickListener(this);
		share_message_image.setOnClickListener(this);
		shared_SNSshare = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(SetInviteriends.this);
		invite_url = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
		invite_sns_message = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_SNS_MESSAGE, "");
		invite_sms_message = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_SMS_MESSAGE, "");

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = new Intent(SetInviteriends.this,
				org.aisin.sipphone.setts.SharedActivity.class);
		intent.putExtra("invite_sns_message", invite_sns_message);
		switch (id) {
		case R.id.setinviteriends_bar_back:
			finish();
			break;
		case R.id.share_weixinfriends_image:
			shared.edit().putInt("weixin", 1).commit();
			intent.putExtra("shareflag", "penyouquan");
			SetInviteriends.this.startActivityForResult(intent, 33);
			break;
		case R.id.share_weixin_image:
			shared.edit().putInt("weixin", 0).commit();
			intent.putExtra("shareflag", "weixin");
			SetInviteriends.this.startActivityForResult(intent, 33);
			break;
		case R.id.share_qq_image:
			intent.putExtra("shareflag", "QQ");
			SetInviteriends.this.startActivityForResult(intent, 33);
			break;
		case R.id.share_weibo_image:
			intent.putExtra("shareflag", "weibo");
			SetInviteriends.this.startActivityForResult(intent, 33);
			break;
		case R.id.share_qzone_image:
			intent.putExtra("shareflag", "Qzone");
			SetInviteriends.this.startActivityForResult(intent, 33);
			break;
		case R.id.share_message_image:
			Uri smsToUri = Uri.parse("smsto:");// 联系人地址
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra(
					"sms_body",
					invite_sms_message
							+ invite_url.replace("phone=%s",
									"phone=" + userinfo.getPhone()).replace(
									"channel=%s", "channel=sms"));
			this.startActivity(mIntent);
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			int result = data.getIntExtra("result", 1);
			String shareflag = data.getStringExtra("shareflag");

			if (result == 0
					&& ("penyouquan".equals(shareflag)
							|| "weibo".equals(shareflag) || "Qzone"
								.equals(shareflag))) {
				// 分享成功
				/*new HttpTask_SharedAdd_Red(SetInviteriends.this)
						.execute("shared_add");*/
			} else if (result == 1) {
				// 分享失败
			} else if (result == 2) {
				// 分享取消
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		RecoveryTools.unbindDrawables(setinviteriends_linlayout);// 回收容器
	}

}
