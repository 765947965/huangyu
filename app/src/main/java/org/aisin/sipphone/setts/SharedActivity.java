package org.aisin.sipphone.setts;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.aisin.sipphone.tianyu.R;
import org.aisin.sipphone.commong.UserInfo;
import org.aisin.sipphone.tools.Constants;
import org.aisin.sipphone.tools.HttpUtils;
import org.aisin.sipphone.tools.SharedPreferencesTools;
import org.aisin.sipphone.tools.UserInfo_db;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.weixin.paydemo.WXContacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.util.Log;

public class SharedActivity extends Activity implements IWeiboHandler.Response {

	private SharedPreferences shared_SNSshare;
	private String invite_url;
	private String invite_app;
	private String iamgeurl = HttpUtils.aixinImageurl;
	private int bitmapid;
	private UserInfo userinfo;

	private String shareflag;
	private String invite_sns_message;

	private Intent intent;

	// 微信
	private String appid = WXContacts.APP_ID;
	private IWXAPI api;
	// QQ
	public Tencent mTencent;
	// 微博
	/** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
	private SsoHandler mSsoHandler;
	private AuthInfo mAuthInfo;
	private Oauth2AccessToken mAccessToken;
	/** 微博分享的接口实例 */
	private IWeiboShareAPI mWeiboShareAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		shareflag = this.getIntent().getStringExtra("shareflag");
		invite_sns_message = this.getIntent().getStringExtra(
				"invite_sns_message");
		if (shareflag == null || "".equals(shareflag)) {
			finish();
			return;
		}
		intent = this.getIntent();
		setContentView(R.layout.sharedactivity);
		userinfo = UserInfo_db.getUserInfo(this);
		shared_SNSshare = SharedPreferencesTools
				.getSharedPreferences_msglist_date_share(SharedActivity.this);
		invite_url = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_URL, "");
		invite_app = shared_SNSshare.getString(
				SharedPreferencesTools.SPF_msglist_date_INVITE_APP, "");
		bitmapid = R.drawable.ic_launcher;
		String invite_app_temp = this.getIntent().getStringExtra("invite_app");
		if (invite_app_temp != null && !"".equals(invite_app_temp)) {
			invite_app = invite_app_temp;
		}
		int bitmapid_temp = this.getIntent().getIntExtra("bitmapid", 0);
		if (bitmapid_temp != 0) {
			bitmapid = bitmapid_temp;
		}
		String iamgeurl_temp = this.getIntent().getStringExtra("iamgeurl");
		if (iamgeurl_temp != null && !"".equals(iamgeurl_temp)) {
			iamgeurl = iamgeurl_temp;
		}
		String invite_url_temp = this.getIntent().getStringExtra("invite_url");
		if (invite_url_temp != null && !"".equals(invite_url_temp)) {
			invite_url = invite_url_temp;
		}

		if ("penyouquan".equals(shareflag)) {
			Weixin(SendMessageToWX.Req.WXSceneTimeline);
		} else if ("weixin".equals(shareflag)) {
			Weixin(SendMessageToWX.Req.WXSceneSession);
		} else if ("QQ".equals(shareflag)) {
			FX_QQ();
		} else if ("Qzone".equals(shareflag)) {
			FX_Qzone();
		} else if ("weibo".equals(shareflag)) {
			try {
				mAuthInfo = new AuthInfo(this, Constants.APP_KEY,
						Constants.REDIRECT_URL, Constants.SCOPE);
				mSsoHandler = new SsoHandler(this, mAuthInfo);
				mSsoHandler.authorize(new AuthListener());
				if (savedInstanceState != null && mWeiboShareAPI != null) {
					mWeiboShareAPI.handleWeiboResponse(intent, this);
				}
			} catch (Error e) {
			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		if (intent != null)
			mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			// 微博成功
			intent.putExtra("shareflag", shareflag);
			intent.putExtra("result", 0);// 0成功 1失败 2取消
			setResult(33, intent);
			SharedActivity.this.finish();
			break;
		case WBConstants.ErrorCode.ERR_FAIL:

			// 微博失败
			intent.putExtra("shareflag", shareflag);
			intent.putExtra("result", 1);
			setResult(33, intent);
			SharedActivity.this.finish();
			break;

		case WBConstants.ErrorCode.ERR_CANCEL:
			// 微博取消
			intent.putExtra("shareflag", shareflag);
			intent.putExtra("result", 2);
			setResult(33, intent);
			SharedActivity.this.finish();
			break;

		default:
			break;
		}

	}

	// 分享到微信
	private void Weixin(int scene) {
		if (api == null) {
			api = WXAPIFactory.createWXAPI(SharedActivity.this, appid, true);
			api.registerApp(appid);
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = invite_url.replace("phone=%s",
				"phone=" + userinfo.getPhone()).replace("channel=%s",
				"channel=weixinfriend ");
		WXMediaMessage msg = new WXMediaMessage(webpage);
		if (SendMessageToWX.Req.WXSceneTimeline == scene) {
			msg.title = invite_app + " " + invite_sns_message;
		} else {
			msg.title = invite_app;
		}
		msg.description = invite_sns_message;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), bitmapid);
		msg.thumbData = bmpToByteArray(thumb, true);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = "webpage";
		req.message = msg;
		req.scene = scene;
		// 调用api接口发送数据到微信
		api.sendReq(req);
		SharedActivity.this.finish();
	}

	// 分享到QQ
	private void FX_QQ() {
		if (mTencent == null)
			mTencent = Tencent
					.createInstance("1105327103", SharedActivity.this);
		Bundle bundle = new Bundle();
		bundle.putString(QQShare.SHARE_TO_QQ_TITLE, invite_app);
		bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL,
				invite_url.replace("phone=%s", "phone=" + userinfo.getPhone())
						.replace("channel=%s", "channel=qq"));
		bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, iamgeurl);
		bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, invite_sns_message);
		bundle.putString(QQShare.SHARE_TO_QQ_APP_NAME, invite_app);
		mTencent.shareToQQ(SharedActivity.this, bundle, new IUiListener() {
			@Override
			public void onError(UiError arg0) {
				if (arg0.errorCode == -6) {
					// 浏览器打开失败
					new AlertDialog.Builder(SharedActivity.this)
							.setMessage("没有安装手机QQ客户端，请安装后再分享！")
							.setPositiveButton("确定", new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									intent.putExtra("shareflag", shareflag);
									intent.putExtra("result", 1);
									setResult(33, intent);
									SharedActivity.this.finish();
								}
							}).show();
				}
			}

			@Override
			public void onComplete(Object arg0) {
				intent.putExtra("shareflag", shareflag);
				intent.putExtra("result", 0);
				setResult(33, intent);
				SharedActivity.this.finish();
			}

			@Override
			public void onCancel() {
				intent.putExtra("shareflag", shareflag);
				intent.putExtra("result", 2);
				setResult(33, intent);
				SharedActivity.this.finish();
			}
		});
	}

	// 分享到QQ空间
	private void FX_Qzone() {
		if (mTencent == null) {
			mTencent = Tencent
					.createInstance("1105327103", SharedActivity.this);
		}
		Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
				QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, invite_app);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, invite_sns_message);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,
				invite_url.replace("phone=%s", "phone=" + userinfo.getPhone())
						.replace("channel=%s", "channel=qzone"));
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, iamgeurl);
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, invite_app);
		ArrayList<String> imageUrls = new ArrayList<String>();
		imageUrls.add(iamgeurl);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
		mTencent.shareToQzone(SharedActivity.this, params, new IUiListener() {
			@Override
			public void onError(UiError arg0) {
				if (arg0.errorCode == -6) {
					// 浏览器打开失败
					new AlertDialog.Builder(SharedActivity.this)
							.setMessage("没有安装手机QQ客户端，请安装后再分享！")
							.setPositiveButton("确定", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									intent.putExtra("shareflag", shareflag);
									intent.putExtra("result", 1);
									setResult(33, intent);
									SharedActivity.this.finish();
								}
							}).show();
				} else {
					SharedActivity.this.finish();
				}
			}

			@Override
			public void onComplete(Object arg0) {
				intent.putExtra("shareflag", shareflag);
				intent.putExtra("result", 0);
				setResult(33, intent);
				SharedActivity.this.finish();
			}

			@Override
			public void onCancel() {
				intent.putExtra("shareflag", shareflag);
				intent.putExtra("result", 2);
				setResult(33, intent);
				SharedActivity.this.finish();
			}
		});
	}

	// 把图片转换成字节流
	public static byte[] bmpToByteArray(final Bitmap bmp,
			final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
	 * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
	 * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
	 * SharedPreferences 中。
	 */
	class AuthListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			// 从 Bundle 中解析 Token
			mAccessToken = Oauth2AccessToken.parseAccessToken(values);
			if (mAccessToken.isSessionValid()) {

				// 创建微博 SDK 接口实例
				mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(
						SharedActivity.this, Constants.APP_KEY);
				mWeiboShareAPI.registerApp();

				// 1. 初始化微博的分享消息
				WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

				// 分享微博消息文本内容
				TextObject textObject = new TextObject();
				textObject.text = invite_app + " - " + invite_sns_message;
				weiboMessage.textObject = textObject;

				ImageObject imageObject = new ImageObject();
				imageObject.setImageObject(BitmapFactory.decodeResource(
						getResources(), bitmapid));
				weiboMessage.imageObject = imageObject;

				WebpageObject mediaObject = new WebpageObject();
				mediaObject.identify = Utility.generateGUID();
				mediaObject.title = invite_app;
				mediaObject.description = invite_sns_message;

				// 设置 Bitmap 类型的图片到视频对象里
				mediaObject.setThumbImage(BitmapFactory.decodeResource(
						getResources(), bitmapid));

				mediaObject.actionUrl = invite_url.replace("phone=%s",
						"phone=" + userinfo.getPhone()).replace("channel=%s",
						"channel=weibo");
				mediaObject.defaultText = "Webpage 默认文案";
				weiboMessage.mediaObject = mediaObject;

				// 2. 初始化从第三方到微博的消息请求
				SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
				// 用transaction唯一标识一个请求
				request.transaction = String
						.valueOf(System.currentTimeMillis());
				request.multiMessage = weiboMessage;
				AuthInfo authInfo = new AuthInfo(SharedActivity.this,
						Constants.APP_KEY, Constants.REDIRECT_URL,
						Constants.SCOPE);
				String token = "";
				if (mAccessToken != null) {
					token = mAccessToken.getToken();
				}
				mWeiboShareAPI.sendRequest(SharedActivity.this, request,
						authInfo, token, new WeiboAuthListener() {

							@Override
							public void onWeiboException(WeiboException arg0) {
							}

							@Override
							public void onComplete(Bundle bundle) {
								Oauth2AccessToken newToken = Oauth2AccessToken
										.parseAccessToken(bundle);
							}

							@Override
							public void onCancel() {
							}
						});
			} else {
				// 以下几种情况，您会收到 Code：
				// 1. 当您未在平台上注册的应用程序的包名与签名时；
				// 2. 当您注册的应用程序包名与签名不正确时；
				// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
				String code = values.getString("code");
				Log.i("环宇", "code:" + code);
				SharedActivity.this.finish();
			}
		}

		@Override
		public void onCancel() {
			SharedActivity.this.finish();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			SharedActivity.this.finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (mTencent != null) {
			mTencent.onActivityResult(requestCode, resultCode, data);
			if ("QQ".equals(shareflag)) {
				SharedActivity.this.finish();
			}
		}
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
}
