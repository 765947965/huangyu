package org.aisin.sipphone.setts;

import java.io.File;

import org.aisin.sipphone.AisinActivity;
import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.commong.UserXXInfo;
import org.aisin.sipphone.sqlitedb.User_data_Ts;
import org.aisin.sipphone.tools.BitMapcreatPath2smallSize;
import org.aisin.sipphone.tools.CheckUpadateTime;
import org.aisin.sipphone.tools.Check_network;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;
import org.linphone.mediastream.Log;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Setts_Fragment extends Fragment implements OnClickListener {
	private View setts_fragment;
	private ViewGroup setts_selfinfo_layout;// 个人信息
	private ImageView setts_selfinfo_image;
	private ImageView shouwimageaction;
	private TextView setts_selfinfo_username;
	private TextView setts_logon_userphone, zhanhao, yue, dqdate;

	private File headiamge;

	private ViewGroup setts_account_relayout, zongdaili, quanxianbanzhu;
	private ViewGroup setts_calloutset_relayout;
	private ViewGroup setts_Keyback_relayout;
	private ViewGroup setts_recharge_relayout;
	private ViewGroup setts_wallet_relayout;
	private ViewGroup setts_Feedback_relayout;
	private ViewGroup setts_inviteriends_relayout;
	private ViewGroup setts_aboutaisin_relayout;
	private ViewGroup setts_red_relayout;
	private TextView setts_red_news;
	private ImageView setts_aboutaisin_news;
	private Bitmap bitmap_headimage;
	private String bitmap_headimage_path;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				// 判断红包是否显示new
				if (SharedPreferencesTools.getSharedPreferences_4RED_COUT(
						AisinActivity.context).getBoolean(
						SharedPreferencesTools.REDCOUT_key, false)) {
					setts_red_news.setVisibility(View.VISIBLE);
				} else {
					setts_red_news.setVisibility(View.INVISIBLE);
				}
				// 设置有效红包个数
				setts_red_news.setText(SharedPreferencesTools
						.getSharedPreferences_4RED_COUT(AisinActivity.context)
						.getInt(SharedPreferencesTools.REhasnum_key, 0)
						+ "");
				// 判断更新是否显示new
				if (CheckUpadateTime.CheckResult_4newAPP(AisinActivity.context)) {
					// 显示new
					setts_aboutaisin_news.setVisibility(View.VISIBLE);
				} else {
					// 不显示
					setts_aboutaisin_news.setVisibility(View.INVISIBLE);
				}
				// 发送广播通知 如果红包界面存在更新未拆红包数
				AisinActivity.context.sendBroadcast(new Intent(
						Constants.BrandName + ".MyRedEnvelope.setts_red_news"));
			} else if (msg.what == 2) {
				// 设置头像
				if (bitmap_headimage != null) {
					bitmap_headimage.recycle();
					bitmap_headimage = null;
				}
				bitmap_headimage = BitmapFactory
						.decodeFile(bitmap_headimage_path);
				setts_selfinfo_image.setImageBitmap(bitmap_headimage);
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			String FRAGMENTS_TAG = "android:support:fragments";
			// remove掉保存的Fragment
			savedInstanceState.remove(FRAGMENTS_TAG);
		}
		setts_fragment = inflater.inflate(R.layout.settsactivity2, container,
				false);
		quanxianbanzhu = (ViewGroup) setts_fragment
				.findViewById(R.id.quanxianbanzhu);
		zongdaili = (ViewGroup) setts_fragment.findViewById(R.id.zongdaili);
		setts_selfinfo_layout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_selfinfo_layout);
		setts_selfinfo_image = (ImageView) setts_fragment
				.findViewById(R.id.setts_selfinfo_image);
		shouwimageaction = (ImageView) setts_fragment
				.findViewById(R.id.shouwimageaction);
		setts_selfinfo_username = (TextView) setts_fragment
				.findViewById(R.id.setts_selfinfo_username);
		setts_logon_userphone = (TextView) setts_fragment
				.findViewById(R.id.setts_logon_userphone);
		setts_account_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_account_relayout);
		setts_calloutset_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_calloutset_relayout);
		setts_Keyback_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_Keyback_relayout);
		setts_recharge_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_recharge_relayout);
		setts_wallet_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_wallet_relayout);
		setts_Feedback_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_Feedback_relayout);
		setts_inviteriends_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_inviteriends_relayout);
		setts_aboutaisin_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_aboutaisin_relayout);
		setts_red_relayout = (ViewGroup) setts_fragment
				.findViewById(R.id.setts_red_relayout);
		setts_red_news = (TextView) setts_fragment
				.findViewById(R.id.setts_red_news);
		zhanhao = (TextView) setts_fragment.findViewById(R.id.zhanhao);
		setts_aboutaisin_news = (ImageView) setts_fragment
				.findViewById(R.id.setts_aboutaisin_news);
		yue = (TextView) setts_fragment.findViewById(R.id.yue);
		dqdate = (TextView) setts_fragment.findViewById(R.id.dqdate);
		// 检查是否显示有新红包
		mHandler.sendEmptyMessage(1);
		quanxianbanzhu.setOnClickListener(this);
		zongdaili.setOnClickListener(this);
		shouwimageaction.setOnClickListener(this);
		setts_selfinfo_layout.setOnClickListener(this);
		setts_account_relayout.setOnClickListener(this);
		setts_calloutset_relayout.setOnClickListener(this);
		setts_Keyback_relayout.setOnClickListener(this);
		setts_recharge_relayout.setOnClickListener(this);
		setts_wallet_relayout.setOnClickListener(this);
		setts_Feedback_relayout.setOnClickListener(this);
		setts_inviteriends_relayout.setOnClickListener(this);
		setts_aboutaisin_relayout.setOnClickListener(this);
		setts_red_relayout.setOnClickListener(this);
		return setts_fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		final UserInfo userinfo = UserInfo_db
				.getUserInfo(AisinActivity.context);
		if (userinfo != null) {
			// 更新登录手机号码
			UserXXInfo uxi = User_data_Ts.getUXXInfo4DB_self(
					AisinActivity.context, userinfo.getUid());
			if (uxi == null) {
				uxi = new UserXXInfo();
			}
			setts_logon_userphone.setText(userinfo.getPhone());
			zhanhao.setText("帐号:" + userinfo.getPhone());
			String name = uxi.getName();
			if (name != null && !"".equals(name)) {
				setts_selfinfo_username.setText(name);
			} else {
				setts_selfinfo_username.setText("用户");
			}
			// 加载头像
			final String picture = uxi.getPicture();
			String picmd5 = uxi.getPicmd5();
			final String picurl_prefix = uxi.getPicurl_prefix();
			headiamge = AisinActivity.context.getFileStreamPath(userinfo
					.getUid() + "headimage.jpg");
			bitmap_headimage_path = headiamge.getAbsolutePath();
			if (picture != null && !"".equals(picture) && picmd5 != null
					&& !"".equals(picmd5) && picurl_prefix != null
					&& !"".equals(picurl_prefix)) {
				// 如果头像文件不存在通知下载
				if (!headiamge.exists()) {
					if (Check_network.isNetworkAvailable(AisinActivity.context)) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								// 下载头像图片
								HttpUtils.downloadImage(AisinActivity.context,
										picurl_prefix + picture,
										userinfo.getUid() + "headimage.jpg");
								mHandler.sendEmptyMessage(2);
							}
						}).start();
					}
				} else {
					if (bitmap_headimage == null) {
						bitmap_headimage = BitMapcreatPath2smallSize
								.GetBitmap4Path2samll(bitmap_headimage_path);
						if (bitmap_headimage != null) {
							setts_selfinfo_image
									.setImageBitmap(bitmap_headimage);
						} else {
							setts_selfinfo_image
									.setImageResource(R.drawable.defaultuserimage);
						}
					} else {
						bitmap_headimage.recycle();
						bitmap_headimage = null;
						bitmap_headimage = BitMapcreatPath2smallSize
								.GetBitmap4Path2samll(bitmap_headimage_path);
						if (bitmap_headimage != null) {
							setts_selfinfo_image
									.setImageBitmap(bitmap_headimage);
						} else {
							setts_selfinfo_image
									.setImageResource(R.drawable.defaultuserimage);
						}
					}
				}
			} else {
				if (bitmap_headimage != null) {
					bitmap_headimage.recycle();
					bitmap_headimage = null;
				}
				setts_selfinfo_image
						.setImageResource(R.drawable.defaultuserimage);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent intent = null;
		switch (id) {
		case R.id.quanxianbanzhu:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.TutorialActivity.class);
			startActivity(intent);
			break;
		case R.id.zongdaili:
			String url = SharedPreferencesTools
					.getSharedPreferences_msglist_date_share(AisinActivity.context).getString(SharedPreferencesTools.SPF_msglist_date_general_agents_url, "");
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.CheckWebView.class);
			intent.putExtra("flag_name", "总代理入口");
			intent.putExtra("url_view", url);
			startActivity(intent);
			break;
		case R.id.setts_selfinfo_layout:
			// 打开个人资料
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SelfPersonalData.class);
			break;
		case R.id.shouwimageaction:
			// 显示头像
			if (headiamge.exists()) {
				Intent intent_tx = new Intent(AisinActivity.context,
						org.aisin.sipphone.setts.ShowImage.class);
				intent_tx.putExtra("imagepath", headiamge.getAbsolutePath());
				AisinActivity.context.startActivity(intent_tx);
				((Activity) AisinActivity.context).overridePendingTransition(
						R.anim.anim_5, R.anim.anim_3);
			}
			return;
		case R.id.setts_account_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetAccount.class);
			break;
		case R.id.setts_calloutset_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.CallOutSet.class);
			break;
		case R.id.setts_Keyback_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.Set4Keyboard.class);
			break;
		case R.id.setts_recharge_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetRecharge.class);
			break;
		case R.id.setts_wallet_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetWallet.class);
			break;
		case R.id.setts_Feedback_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.FeedbackActivity.class);
			break;
		case R.id.setts_inviteriends_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetInviteriends.class);
			break;
		case R.id.setts_aboutaisin_relayout:
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.SetAboutaisin.class);
			break;
		case R.id.setts_red_relayout:// 我的红包
			intent = new Intent(AisinActivity.context,
					org.aisin.sipphone.setts.MyRedEnvelope.class);
			break;
		}
		startActivity(intent);
	}

	// 检测是否显示新红包
	public void sendmhandler() {
		mHandler.sendEmptyMessage(1);
	}

	public void setYUEValue(String yue, String date) {
		// TODO Auto-generated method stub
		this.yue.setText(Html.fromHtml("<font color=#000000>" + "余额:"
				+ "</font><font color=#f47920>" + yue + "元" + "</font>"));
		dqdate.setText("到期日期:" + date);
	}
}